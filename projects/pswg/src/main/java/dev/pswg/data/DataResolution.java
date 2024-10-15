package dev.pswg.data;

import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

/**
 * Represents data that has been loaded with optional dependencies
 *
 * @param <T> The type of data to load
 */
public final class DataResolution<T>
{
	private enum DataResolutionResult
	{
		SUCCESS,
		MISSING_DEPENDENCY
	}

	/**
	 * Indicates the data has been loaded and is ready to consume
	 *
	 * @param data The data that has been loaded
	 * @param <T>  The type of data to load
	 *
	 * @return A successful resolution result
	 */
	public static <T> DataResolution<T> success(T data)
	{
		return new DataResolution<>(DataResolutionResult.SUCCESS, data, null);
	}

	/**
	 * Indicates the data has been loaded and is ready to consume
	 *
	 * @param dependencies The identifiers of the data that this data depends on
	 * @param <T>          The type of data to load
	 *
	 * @return An unsuccessful resolution result with failed dependencies
	 */
	public static <T> DataResolution<T> missingDependency(List<Identifier> dependencies)
	{
		return new DataResolution<>(DataResolutionResult.MISSING_DEPENDENCY, null, dependencies);
	}

	private final DataResolutionResult result;
	private final T data;
	private final List<Identifier> dependencies;

	private DataResolution(DataResolutionResult result, T data, List<Identifier> dependencies)
	{
		this.result = result;
		this.data = data;
		this.dependencies = dependencies;
	}

	/**
	 * Determines if this data has successfully resolved
	 *
	 * @return True if this data has no unresolved dependencies
	 */
	public boolean isResolved()
	{
		return result == DataResolutionResult.SUCCESS;
	}

	/**
	 * Gets the data represented by this instance
	 *
	 * @return The value of the data
	 */
	public T getValue()
	{
		return data;
	}

	/**
	 * Gets a list of dependencies that were unresolved while loading this data
	 *
	 * @return The list of unresolved dependencies, or empty if there are none
	 */
	public Optional<List<Identifier>> getDependencies()
	{
		return Optional.ofNullable(dependencies);
	}
}
