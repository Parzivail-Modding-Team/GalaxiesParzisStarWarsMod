package com.parzivail.pswg.features.lightsabers.addon;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class ByteBufResourceReloader<T>
{
	private static final Logger LOGGER = LogUtils.getLogger();
	private final String directory;
	private final String fileExtension;
	private Map<Identifier, T> data;

	public ByteBufResourceReloader(String directory, String fileExtension)
	{
		this.directory = directory;
		this.fileExtension = fileExtension;
	}

	public Map<Identifier, T> getData()
	{
		return data;
	}

	public abstract T deserialize(Identifier entryId, Identifier resourceId, PacketByteBuf buf);

	public void load(ResourceManager resourceManager)
	{
		this.data = loadData(resourceManager);
	}

	protected Map<Identifier, T> loadData(ResourceManager resourceManager)
	{
		var map = new HashMap<Identifier, T>();
		ResourceFinder resourceFinder = new ResourceFinder(directory, fileExtension);

		for (Map.Entry<Identifier, Resource> entry : resourceFinder.findResources(resourceManager).entrySet())
		{
			Identifier entryId = entry.getKey();
			Identifier resourceId = resourceFinder.toResourceId(entryId);

			var buf = new PacketByteBuf(Unpooled.buffer());
			try (InputStream reader = entry.getValue().getInputStream(); ByteBufOutputStream out = new ByteBufOutputStream(buf))
			{
				reader.transferTo(out);
				map.put(resourceId, deserialize(entryId, resourceId, buf));
			}
			catch (IllegalArgumentException | IOException e)
			{
				LOGGER.error("Couldn't parse data file {} from {}", resourceId, entryId, e);
			}
		}

		return map;
	}
}
