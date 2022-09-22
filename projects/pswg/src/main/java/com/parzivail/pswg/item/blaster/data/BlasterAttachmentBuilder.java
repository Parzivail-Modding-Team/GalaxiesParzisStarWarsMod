package com.parzivail.pswg.item.blaster.data;

import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class BlasterAttachmentBuilder
{
	private record Entry(String id, BlasterAttachmentFunction function, BlasterAttachmentCategory category, String visualComponent, Identifier texture)
	{
	}

	private final HashMap<Integer, List<Entry>> entries = new HashMap<>();
	private final HashMap<Integer, String> layerDefaults = new HashMap<>();
	private final HashSet<Integer> requiredLayers = new HashSet<>();

	public BlasterAttachmentBuilder attachment(int layer, String id, BlasterAttachmentFunction function, BlasterAttachmentCategory category)
	{
		return attachment(layer, id, function, category, null, null);
	}

	public BlasterAttachmentBuilder attachment(int layer, String id, BlasterAttachmentFunction function, BlasterAttachmentCategory category, String visualComponent, Identifier texture)
	{
		if (!entries.containsKey(layer))
			entries.put(layer, new ArrayList<>());

		entries.get(layer).add(new Entry(id, function, category, visualComponent, texture));
		return this;
	}

	public BlasterAttachmentBuilder preset(int layer, String id)
	{
		layerDefaults.put(layer, id);
		return this;
	}

	public BlasterAttachmentBuilder required(int layer)
	{
		requiredLayers.add(layer);
		return this;
	}

	public BlasterAttachmentMap build()
	{
		int minimumBitmap = 0;
		int defaultBitmap = 0;
		var attachments = new HashMap<Integer, BlasterAttachmentDescriptor>();

		int bit = 0;

		for (var entry : entries.entrySet())
		{
			var layer = entry.getKey();
			var layerAttachments = entry.getValue();
			var layerSize = layerAttachments.size();
			var layerBitmask = ((1 << layerSize) - 1) << bit;
			var layerDefault = layerDefaults.get(layer);

			if (requiredLayers.contains(layer))
				// TODO: allow choosing which attachment is added back
				minimumBitmap |= (1 << bit);

			for (var attachment : layerAttachments)
			{
				var attachmentBitmask = 1 << bit;

				attachments.put(attachmentBitmask, new BlasterAttachmentDescriptor(
						attachmentBitmask, layerBitmask, attachment.category,
						attachment.id, attachment.function, attachment.visualComponent,
						attachment.texture
				));

				if (layerDefault != null && layerDefault.equals(attachment.id))
					defaultBitmap |= (1 << bit);

				bit++;
			}
		}

		return new BlasterAttachmentMap(minimumBitmap, defaultBitmap, attachments);
	}
}
