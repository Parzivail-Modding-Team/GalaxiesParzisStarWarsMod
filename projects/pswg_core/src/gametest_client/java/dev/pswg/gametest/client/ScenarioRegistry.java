package dev.pswg.gametest.client;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects the scenarios of one client GameTest class, in the order they are declared.
 */
public final class ScenarioRegistry
{
	/**
	 * One declared scenario.
	 *
	 * @param name the scenario name, unique within its class
	 * @param body the scenario body
	 */
	record Entry(String name, ClientScenario body)
	{
	}

	/**
	 * The declared scenarios.
	 */
	private final List<Entry> _entries;

	/**
	 * Creates an empty registry.
	 */
	ScenarioRegistry()
	{
		_entries = new ArrayList<>();
	}

	/**
	 * Declares a scenario.
	 *
	 * @param name the scenario name, unique within its class
	 * @param body the scenario body
	 *
	 * @return this registry
	 *
	 * @throws IllegalArgumentException if the name is already declared
	 */
	public ScenarioRegistry add(String name, ClientScenario body)
	{
		if (_entries.stream().anyMatch(entry -> entry.name().equals(name)))
		{
			throw new IllegalArgumentException("Duplicate client scenario: " + name);
		}

		_entries.add(new Entry(name, body));
		return this;
	}

	/**
	 * Gets the declared scenarios.
	 *
	 * @return the scenarios in declaration order
	 */
	List<Entry> entries()
	{
		return List.copyOf(_entries);
	}
}
