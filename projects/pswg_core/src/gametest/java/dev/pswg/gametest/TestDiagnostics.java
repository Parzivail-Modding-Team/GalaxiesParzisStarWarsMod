package dev.pswg.gametest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Collects the evidence of one GameTest and writes it as a single JSON snapshot when the test fails.
 *
 * <p>A snapshot holds the failure, the shared world state supplied by the test context, a bounded log of
 * the steps the test took, and every registered {@link DiagnosticSource}. It is written at most once per
 * test, next to the other run artifacts in {@link TestResults#diagnosticsDirectory()}, so a failed CI run
 * can be inspected without reproducing it.
 */
public final class TestDiagnostics
{
	/**
	 * The logger shared by all GameTest harness output.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger("pswg_gametest");

	/**
	 * The serializer of snapshots; pretty printing keeps them readable in a CI artifact viewer.
	 */
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	/**
	 * The most step events one snapshot keeps; older events are dropped first.
	 */
	private static final int MAX_EVENTS = 200;

	/**
	 * The most stack frames of a failure that a snapshot keeps.
	 */
	private static final int MAX_STACK_FRAMES = 20;

	/**
	 * The name of the test the snapshot belongs to.
	 */
	private final String _testName;

	/**
	 * The registered domain state sources, in registration order.
	 */
	private final Map<String, DiagnosticSource> _sources;

	/**
	 * The bounded step log.
	 */
	private final Deque<String> _events;

	/**
	 * Whether the snapshot has already been written.
	 */
	private boolean _written;

	/**
	 * Creates the collector of one test.
	 *
	 * @param testName the test name, used for the snapshot file name
	 */
	public TestDiagnostics(String testName)
	{
		_testName = testName;
		_sources = new LinkedHashMap<>();
		_events = new ArrayDeque<>();
	}

	/**
	 * Gets the name of the test.
	 *
	 * @return the test name
	 */
	public String testName()
	{
		return _testName;
	}

	/**
	 * Registers a source of domain state that is captured into a failure snapshot.
	 *
	 * @param name   the key of the source in the snapshot
	 * @param source the lazily evaluated source
	 */
	public void addSource(String name, DiagnosticSource source)
	{
		_sources.put(name, source);
	}

	/**
	 * Records one step of the test for the failure snapshot and the harness log.
	 *
	 * @param tick  the test tick the event happened on
	 * @param event the event description
	 */
	public void record(long tick, String event)
	{
		append("tick " + tick, event);
	}

	/**
	 * Records one step of a test that has no game tick of its own, such as a client scenario.
	 *
	 * <p>The event is numbered by its position in the test instead of by tick.
	 *
	 * @param event the event description
	 */
	public void record(String event)
	{
		append("step " + (_events.size() + 1), event);
	}

	/**
	 * Appends one labelled event to the bounded log.
	 *
	 * @param label the position label of the event
	 * @param event the event description
	 */
	private void append(String label, String event)
	{
		var line = label + ": " + event;
		LOGGER.debug("[{}] {}", _testName, line);

		if (_events.size() == MAX_EVENTS)
		{
			_events.removeFirst();
		}

		_events.addLast(line);
	}

	/**
	 * Writes the failure snapshot unless one was already written.
	 *
	 * @param worldState the shared world state captured by the test context
	 * @param failure    the failure that ended the test
	 *
	 * @return the written file, or empty when a snapshot already exists or could not be written
	 */
	public Optional<Path> writeSnapshot(JsonObject worldState, Throwable failure)
	{
		if (_written)
		{
			return Optional.empty();
		}

		_written = true;

		var root = new JsonObject();
		root.addProperty("test", _testName);
		root.add("failure", describe(failure));
		root.add("state", worldState);
		root.add("events", eventsJson());
		root.add("sources", sourcesJson());

		var file = TestResults.diagnosticsDirectory().resolve(TestResults.fileStem(_testName) + ".json");

		try
		{
			Files.createDirectories(file.getParent());
			Files.writeString(file, GSON.toJson(root), StandardCharsets.UTF_8);
			LOGGER.error("[{}] failed; diagnostics written to {}", _testName, file);
			return Optional.of(file);
		}
		catch (IOException exception)
		{
			LOGGER.error("[{}] failed and its diagnostics could not be written to {}", _testName, file, exception);
			return Optional.empty();
		}
	}

	/**
	 * Describes a failure with its type, message and a bounded stack.
	 *
	 * @param failure the failure
	 *
	 * @return the JSON description
	 */
	private static JsonObject describe(Throwable failure)
	{
		var description = new JsonObject();
		description.addProperty("type", failure.getClass().getName());
		description.addProperty("message", String.valueOf(failure.getMessage()));

		var stack = new JsonArray();
		var frames = failure.getStackTrace();

		for (var index = 0; index < Math.min(frames.length, MAX_STACK_FRAMES); index++)
		{
			stack.add(frames[index].toString());
		}

		description.add("stack", stack);

		if (failure.getCause() != null)
		{
			description.add("cause", describe(failure.getCause()));
		}

		return description;
	}

	/**
	 * Serializes the step log.
	 *
	 * @return the events as a JSON array
	 */
	private JsonArray eventsJson()
	{
		var events = new JsonArray();

		for (var event : _events)
		{
			events.add(event);
		}

		return events;
	}

	/**
	 * Captures every registered source, isolating a failing source from the rest of the snapshot.
	 *
	 * @return the captured sources keyed by name
	 */
	private JsonObject sourcesJson()
	{
		var sources = new JsonObject();

		for (var entry : _sources.entrySet())
		{
			try
			{
				JsonElement captured = entry.getValue().capture();
				sources.add(entry.getKey(), captured);
			}
			catch (RuntimeException exception)
			{
				sources.addProperty(entry.getKey(), "capture failed: " + exception);
			}
		}

		return sources;
	}
}
