package com.parzivail.util.data;

import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public abstract class KeyedReloadableLoader<T> implements IdentifiableResourceReloadListener
{
	private static final Logger LOGGER = LogManager.getLogger();
	private final String fileSuffix;
	private final int fileSuffixLength;
	private final String startingPath;

	public KeyedReloadableLoader(String startingPath, String fileExtension)
	{
		this.startingPath = startingPath;
		fileSuffix = "." + fileExtension;
		fileSuffixLength = fileSuffix.length();
	}

	public abstract DataResolution<T> readResource(ResourceManager resourceManager, Profiler profiler, Map<Identifier, T> loadedResources, Identifier id, InputStream stream) throws IOException;

	@Override
	public final CompletableFuture<Void> reload(ResourceReloader.Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor)
	{
		return CompletableFuture.supplyAsync(() -> this.prepare(manager, prepareProfiler), prepareExecutor)
		                        .thenCompose(synchronizer::whenPrepared)
		                        .thenAcceptAsync(object -> this.apply(object, manager, applyProfiler), applyExecutor);
	}

	protected Map<Identifier, T> prepare(ResourceManager resourceManager, Profiler profiler)
	{
		Map<Identifier, T> map = Maps.newHashMap();
		var startingPathSubstring = this.startingPath.length() + 1;
		var resources = resourceManager.findResources(this.startingPath, (s) -> s.getPath().endsWith(fileSuffix));

		Queue<Map.Entry<Identifier, Resource>> entryQueue = new LinkedList<>(resources.entrySet());

		while (!entryQueue.isEmpty())
		{
			var resourceEntry = entryQueue.poll();

			var resourceId = resourceEntry.getKey();
			var resourceIdWithoutExt = getIdWithoutExt(resourceId, startingPathSubstring);

			var resource = resourceEntry.getValue();

			LOGGER.info("Loaded resource {}", resourceIdWithoutExt);

			try (var inputStream = resource.getInputStream())
			{
				var result = readResource(resourceManager, profiler, map, resourceEntry.getKey(), inputStream);

				if (result.isResolved())
				{
					var element = result.getData();
					if (element != null)
					{
						var popped = map.put(resourceIdWithoutExt, element);
						if (popped != null)
							throw new IllegalStateException("Duplicate data file ignored with ID " + resourceIdWithoutExt);
					}
					else
						LOGGER.error("Couldn't load data file {} from {} as it's null or empty", resourceIdWithoutExt, resourceId);
				}
				else
				{
					var deps = result.getDependencies();

					var resolvedDeps = true;
					for (var dep : deps)
						if (entryQueue.stream().noneMatch(entry -> getIdWithoutExt(entry.getKey(), startingPathSubstring).equals(dep)))
						{
							LOGGER.error("Couldn't load data file {} from {} as it depends on a missing model: {}", resourceIdWithoutExt, resourceId, dep);
							resolvedDeps = false;
						}

					if (resolvedDeps)
					{
						entryQueue.add(resourceEntry);
						LOGGER.info("Moving resource {} to the end of the resolution queue to satisfy dependencies on {}", resourceIdWithoutExt, deps.stream().map(Identifier::toString).collect(Collectors.joining(", ")));
					}
				}
			}
			catch (Exception ex)
			{
				LOGGER.error("Couldn't parse data file {} from {}", resourceIdWithoutExt, resourceId, ex);
			}
		}

		return map;
	}

	@NotNull
	private Identifier getIdWithoutExt(Identifier resourceId, int pathStart)
	{
		var resourcePath = resourceId.getPath();
		return new Identifier(resourceId.getNamespace(), resourcePath.substring(pathStart, resourcePath.length() - fileSuffixLength));
	}

	protected abstract void apply(Map<Identifier, T> prepared, ResourceManager manager, Profiler profiler);
}

