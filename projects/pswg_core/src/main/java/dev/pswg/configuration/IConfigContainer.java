package dev.pswg.configuration;

/**
 * Represents a configuration that can be synchronized with a backing file
 *
 * @param <TConfig> The type of config to be synchronized
 */
public interface IConfigContainer<TConfig>
{
	/**
	 * Gets the current value of the configuration
	 *
	 * @return The configuration
	 */
	TConfig get();
}
