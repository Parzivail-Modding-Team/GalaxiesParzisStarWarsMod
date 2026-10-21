package dev.pswg.gametest;

import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * The base class of PSWG server GameTest suites.
 *
 * <p>A suite is an ordinary Fabric GameTest class registered under the {@code fabric-gametest} entrypoint of a
 * test mod. Extending this class replaces the native {@code GameTestHelper} parameter of its
 * {@link GameTest}-annotated methods with a typed context that carries the PSWG fixtures:
 *
 * <pre>{@code
 * public final class ExampleGameTests extends PswgGameTestSuite<PswgGameTestContext>
 * {
 *     @Override
 *     protected PswgGameTestContext createContext(GameTestHelper helper, String testName)
 *     {
 *         return new PswgGameTestContext(helper, testName);
 *     }
 *
 *     @GameTest(maxTicks = 40)
 *     public void somethingHappens(PswgGameTestContext test)
 *     {
 *         test.sequence().afterTicks(10).then(() -> test.assertTrue(...)).succeed();
 *     }
 * }
 * }</pre>
 *
 * <p>The native runner still owns discovery, structure placement, ticking, timeouts, retries and reporting.
 * A suite only decides which object its test methods receive, and records a failure snapshot when a method
 * throws before the test has even started its sequence.
 *
 * @param <C> the context type that test methods receive
 */
public abstract class GalaxiesGameTestSuite<C extends GalaxiesGameTestContext> implements CustomTestMethodInvoker
{
	/**
	 * Creates the context of one test.
	 *
	 * <p>It is called on the server thread right before the test method runs.
	 *
	 * @param helper   the native helper of the running test
	 * @param testName the human-readable test name
	 *
	 * @return the context passed to the test method
	 */
	protected abstract C createContext(GameTestHelper helper, String testName);

	@Override
	public final void invokeTestMethod(GameTestHelper helper, Method method) throws ReflectiveOperationException
	{
		var testName = getClass().getSimpleName() + "." + method.getName();
		var context = createContext(helper, testName);

		requireContextParameter(method, context);

		try
		{
			method.invoke(this, context);
		}
		catch (InvocationTargetException exception)
		{
			context.reportFailure(exception.getCause());
			throw exception;
		}
	}

	/**
	 * Verifies that a test method accepts exactly the context this suite creates.
	 *
	 * <p>Fabric skips its own signature validation for suites that customize invocation, so a wrongly
	 * declared method would otherwise fail with an obscure reflection error.
	 *
	 * @param method  the test method
	 * @param context the context created for it
	 */
	private static void requireContextParameter(Method method, GalaxiesGameTestContext context)
	{
		var parameterTypes = method.getParameterTypes();

		if (parameterTypes.length != 1 || !parameterTypes[0].isInstance(context))
		{
			throw new IllegalStateException(
					method.getDeclaringClass().getName() + "#" + method.getName()
					+ " must declare exactly one parameter of type " + context.getClass().getName()
			);
		}
	}
}
