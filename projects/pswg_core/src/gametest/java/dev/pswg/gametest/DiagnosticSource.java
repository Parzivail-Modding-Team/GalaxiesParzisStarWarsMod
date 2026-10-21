package dev.pswg.gametest;

import com.google.gson.JsonElement;

/**
 * Captures one piece of domain state for a failure snapshot.
 *
 * <p>Sources are evaluated lazily, only when a test fails, on the server thread. A source should describe
 * state with the same codecs and components production code uses, so the snapshot reads like a save file
 * and never invents a parallel test-only representation.
 */
@FunctionalInterface
public interface DiagnosticSource
{
	/**
	 * Captures the current state.
	 *
	 * @return the captured state as JSON
	 */
	JsonElement capture();
}
