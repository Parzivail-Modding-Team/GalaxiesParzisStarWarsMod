/*
 * Based on net.fabricmc.fabric.impl.resource.loader.ModNioResourcePack
 *
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.parzivail.util.data.pack;

import net.fabricmc.loader.api.ModContainer;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.PathUtil;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.regex.Pattern;

public class ModNioResourcePack implements ResourcePack
{
	private static final String resPrefix = ResourceType.CLIENT_RESOURCES.getDirectory() + "/";
	private static final String dataPrefix = ResourceType.SERVER_DATA.getDirectory() + "/";

	private static final Logger LOGGER = LoggerFactory.getLogger(ModNioResourcePack.class);
	private static final Pattern RESOURCE_PACK_PATH = Pattern.compile("[a-z0-9-_.]+");
	private static final FileSystem DEFAULT_FS = FileSystems.getDefault();

	private final Identifier id;
	private final List<Path> basePaths;
	private final Map<ResourceType, Set<String>> namespaces;

	public ModNioResourcePack(ModContainer mod)
	{
		var modId = mod.getMetadata().getId();
		this.id = new Identifier("modid", modId);
		this.basePaths = mod.getRootPaths();
		this.namespaces = readNamespaces(this.basePaths, modId);
	}

	static Map<ResourceType, Set<String>> readNamespaces(List<Path> paths, String modId)
	{
		Map<ResourceType, Set<String>> ret = new EnumMap<>(ResourceType.class);

		for (ResourceType type : ResourceType.values())
		{
			Set<String> namespaces = null;

			for (Path path : paths)
			{
				Path dir = path.resolve(type.getDirectory());
				if (!Files.isDirectory(dir))
					continue;

				String separator = path.getFileSystem().getSeparator();

				try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir))
				{
					for (Path p : ds)
					{
						if (!Files.isDirectory(p))
							continue;

						String s = p.getFileName().toString();
						// s may contain trailing slashes, remove them
						s = s.replace(separator, "");

						if (!RESOURCE_PACK_PATH.matcher(s).matches())
						{
							LOGGER.warn("PSWG NioResourcePack: ignored invalid namespace: {} in mod ID {}", s, modId);
							continue;
						}

						if (namespaces == null)
							namespaces = new HashSet<>();

						namespaces.add(s);
					}
				}
				catch (IOException e)
				{
					LOGGER.warn("getNamespaces in mod " + modId + " failed!", e);
				}
			}

			ret.put(type, namespaces != null ? namespaces : Collections.emptySet());
		}

		return ret;
	}

	private Path getPath(String filename)
	{
		if (hasAbsentNs(filename))
			return null;

		for (Path basePath : basePaths)
		{
			Path childPath = basePath.resolve(filename.replace("/", basePath.getFileSystem().getSeparator())).toAbsolutePath().normalize();

			if (childPath.startsWith(basePath) && exists(childPath))
				return childPath;
		}

		return null;
	}

	private boolean hasAbsentNs(String filename)
	{
		int prefixLen;
		ResourceType type;

		if (filename.startsWith(resPrefix))
		{
			prefixLen = resPrefix.length();
			type = ResourceType.CLIENT_RESOURCES;
		}
		else if (filename.startsWith(dataPrefix))
		{
			prefixLen = dataPrefix.length();
			type = ResourceType.SERVER_DATA;
		}
		else
		{
			return false;
		}

		int nsEnd = filename.indexOf('/', prefixLen);
		if (nsEnd < 0)
			return false;

		return !namespaces.get(type).contains(filename.substring(prefixLen, nsEnd));
	}

	@Nullable
	@Override
	public InputSupplier<InputStream> openRoot(String... pathSegments)
	{
		PathUtil.validatePath(pathSegments);

		Path path = getPath(String.join("/", pathSegments));

		if (path != null && Files.isRegularFile(path))
			return () -> Files.newInputStream(path);

		return null;
	}

	@Override
	@Nullable
	public InputSupplier<InputStream> open(ResourceType type, Identifier id)
	{
		final var filename = type.getDirectory() + "/" + id.getNamespace() + "/" + id.getPath();
		final var path = getPath(filename);
		return path == null ? null : InputSupplier.create(path);
	}

	@Override
	public void findResources(ResourceType type, String namespace, String path, ResultConsumer visitor)
	{
		if (!namespaces.getOrDefault(type, Collections.emptySet()).contains(namespace))
			return;

		for (Path basePath : basePaths)
		{
			String separator = basePath.getFileSystem().getSeparator();
			Path nsPath = basePath.resolve(type.getDirectory()).resolve(namespace);
			Path searchPath = nsPath.resolve(path.replace("/", separator)).normalize();
			if (!exists(searchPath))
				continue;

			try
			{
				Files.walkFileTree(searchPath, new SimpleFileVisitor<>()
				{
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
					{
						String filename = nsPath.relativize(file).toString().replace(separator, "/");
						Identifier identifier = Identifier.of(namespace, filename);

						if (identifier == null)
							LOGGER.error("Invalid path in mod resource-pack {}: {}:{}, ignoring", id, namespace, filename);
						else
							visitor.accept(identifier, InputSupplier.create(file));

						return FileVisitResult.CONTINUE;
					}
				});
			}
			catch (IOException e)
			{
				LOGGER.warn("findResources at " + path + " in namespace " + namespace + ", mod " + id.getPath() + " failed!", e);
			}
		}
	}

	@Override
	public Set<String> getNamespaces(ResourceType type)
	{
		return namespaces.getOrDefault(type, Collections.emptySet());
	}

	@Override
	public <T> T parseMetadata(ResourceMetadataReader<T> metaReader)
	{
		return null;
	}

	@Override
	public void close()
	{
	}

	@Override
	public String getName()
	{
		return this.id.toString();
	}

	private static boolean exists(Path path)
	{
		// NIO Files.exists is notoriously slow when checking the file system
		return path.getFileSystem() == DEFAULT_FS ? path.toFile().exists() : Files.exists(path);
	}
}
