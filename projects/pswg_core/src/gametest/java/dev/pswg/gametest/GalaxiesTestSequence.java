package dev.pswg.gametest;

import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestSequence;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A readable list of timed test steps, compiled into a native {@link GameTestSequence}.
 *
 * <p>Each method appends one native step, so the steps run on the server thread, in order, exactly when the
 * server's ticks reach them. Consecutive steps without {@link #afterTicks} run in the same tick, and the
 * test's {@code maxTicks} still bounds the whole sequence.
 *
 * <p>Every step is guarded. An assertion failure inside a step fails the test with its own message. Any
 * other exception is converted into a failed test as well, because vanilla would otherwise let it escape
 * through the server tick and crash the whole run. After the first failure the remaining steps of the same
 * test are skipped so that a broken precondition cannot cascade into misleading secondary failures.
 */
public final class GalaxiesTestSequence
{
	/**
	 * The test this sequence belongs to.
	 */
	private final GalaxiesGameTestContext _context;

	/**
	 * The native sequence the steps are appended to.
	 */
	private final GameTestSequence _sequence;

	/**
	 * Creates a sequence of one test.
	 *
	 * @param context  the test the sequence belongs to
	 * @param sequence the native sequence to append to
	 */
	GalaxiesTestSequence(GalaxiesGameTestContext context, GameTestSequence sequence)
	{
		_context = context;
		_sequence = sequence;
	}

	/**
	 * Appends an action that runs once, in the tick the previous step finished.
	 *
	 * @param description what the step does, recorded in the failure snapshot
	 * @param action      the action; assertions inside it fail the test immediately
	 *
	 * @return this sequence
	 */
	public GalaxiesTestSequence then(String description, Runnable action)
	{
		_sequence.thenExecute(() -> runGuarded(description, action));
		return this;
	}

	/**
	 * Appends an action that runs once, in the tick the previous step finished.
	 *
	 * @param action the action; assertions inside it fail the test immediately
	 *
	 * @return this sequence
	 */
	public GalaxiesTestSequence then(Runnable action)
	{
		return then("step", action);
	}

	/**
	 * Appends a pause: the next step runs once the given number of ticks have passed since the previous one.
	 *
	 * @param ticks the number of ticks to wait
	 *
	 * @return this sequence
	 */
	public GalaxiesTestSequence afterTicks(int ticks)
	{
		_sequence.thenIdle(ticks);
		return this;
	}

	/**
	 * Appends a condition that is re-checked every tick until it holds.
	 *
	 * <p>The assertion may throw an assertion failure any number of times. The step completes in the first
	 * tick it returns normally, and the sequence then continues in that same tick. If it never holds, the test
	 * times out with the assertion's last failure message. Unexpected exceptions are also reported as that
	 * message, together with the first failure snapshot, instead of ending the test early.
	 *
	 * @param description what is being waited for, recorded in the failure snapshot
	 * @param assertion   the condition, expressed as an assertion that throws while it does not hold
	 *
	 * @return this sequence
	 */
	public GalaxiesTestSequence until(String description, Runnable assertion)
	{
		var announced = new AtomicBoolean();
		_sequence.thenWaitUntil(() -> pollGuarded(description, assertion, announced));
		return this;
	}

	/**
	 * Appends a condition that is re-checked every tick until it holds.
	 *
	 * @param assertion the condition, expressed as an assertion that throws while it does not hold
	 *
	 * @return this sequence
	 * @see #until(String, Runnable)
	 */
	public GalaxiesTestSequence until(Runnable assertion)
	{
		return until("condition", assertion);
	}

	/**
	 * Ends the sequence: releases the test's resources and marks the test as passed.
	 *
	 * <p>A test that already failed stays failed.
	 */
	public void succeed()
	{
		then("finish", _context::finish);
		_sequence.thenSucceed();
	}

	/**
	 * Runs one action as a guarded step.
	 *
	 * @param description what the step does
	 * @param action      the action
	 */
	private void runGuarded(String description, Runnable action)
	{
		if (_context.hasFailed())
		{
			return;
		}

		_context.log(description);

		try
		{
			action.run();
		}
		catch (LinkageError | AssertionError | RuntimeException failure)
		{
			throw failed(failure);
		}
	}

	/**
	 * Runs one polled condition as a guarded step.
	 *
	 * @param description what is being waited for
	 * @param assertion   the condition
	 * @param announced   whether the wait was already logged, so a long wait logs once instead of every tick
	 */
	private void pollGuarded(String description, Runnable assertion, AtomicBoolean announced)
	{
		if (_context.hasFailed())
		{
			return;
		}

		if (announced.compareAndSet(false, true))
		{
			_context.log("waiting for " + description);
		}

		try
		{
			assertion.run();
			_context.log("satisfied: " + description);
		}
		catch (GameTestAssertException failure)
		{
			_context.notePollFailure(description, failure);
			throw failure;
		}
		catch (LinkageError | AssertionError | RuntimeException failure)
		{
			throw failed(failure);
		}
	}

	/**
	 * Reports a failed step and returns the exception that fails it natively.
	 *
	 * @param failure what escaped the step
	 *
	 * @return the native failure to throw
	 */
	private GameTestAssertException failed(Throwable failure)
	{
		var translated = _context.translate(failure);
		_context.reportFailure(translated);
		return translated;
	}
}
