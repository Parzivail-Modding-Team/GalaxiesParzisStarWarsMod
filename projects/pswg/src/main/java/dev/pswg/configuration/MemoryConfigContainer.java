package dev.pswg.configuration;

/**
 * Represents a configuration file that only exists in memory
 *
 * @param <TConfig> The type of config to be held
 */
public class MemoryConfigContainer<TConfig> implements IConfigContainer<TConfig>
{
	/**
	 * The configuration file that is held by this instance
	 */
	private final TConfig config;

	public MemoryConfigContainer(TConfig config)
	{
		this.config = config;
	}

	@Override
	public TConfig get()
	{
		return config;
	}
}
