package com.parzivail.util.data;

import net.minecraft.util.Identifier;

import java.util.List;

public class DataResolution<T>
{
	public static <T> DataResolution<T> success(T data)
	{
		return new DataResolution<>(DataResolutionResult.SUCCESS, data, List.of());
	}

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

	public boolean isResolved()
	{
		return result == DataResolutionResult.SUCCESS;
	}

	public T getData()
	{
		return data;
	}

	public List<Identifier> getDependencies()
	{
		return dependencies;
	}
}
