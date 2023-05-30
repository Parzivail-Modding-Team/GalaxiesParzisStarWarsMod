package com.parzivail.pswg.features.blasters.data;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

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

	/**
	 * Adds an attachment option without a visual component.
	 *
	 * @param layer    The attachment layer this attachment will belong to.
	 * @param id       The ID this attachment will have.
	 * @param function The function that this attachment will have.
	 * @param category The category (and by extension, icon) the attachment falls under.
	 *
	 * @return this
	 */
	public BlasterAttachmentBuilder attachment(int layer, String id, BlasterAttachmentFunction function, BlasterAttachmentCategory category)
	{
		return attachment(layer, id, function, category, null, null);
	}

	/**
	 * Adds an attachment option with a visual component.
	 *
	 * @param layer           The attachment layer this attachment will belong to.
	 * @param id              The ID this attachment will have.
	 * @param function        The function that this attachment will have.
	 * @param category        The category (and by extension, icon) the attachment falls under.
	 * @param visualComponent The part name within the model that should be hidden or shown with regard to this attachment's presence.
	 * @param texture         The texture identifier this attachment's model part should render with, or null if it should use the blaster's main texture.
	 *
	 * @return this
	 */
	public BlasterAttachmentBuilder attachment(int layer, String id, BlasterAttachmentFunction function, BlasterAttachmentCategory category, String visualComponent, Identifier texture)
	{
		if (!entries.containsKey(layer))
			entries.put(layer, new ArrayList<>());

		entries.get(layer).add(new Entry(id, function, category, visualComponent, texture));
		return this;
	}

	/**
	 * Sets a specific attachment to be the "default" attachment of that attachment layer.
	 *
	 * @param layer    The attachment layer to select a preset for.
	 * @param presetId The ID of the attachment that will be enabled by default on that layer.
	 *
	 * @return this
	 */
	public BlasterAttachmentBuilder preset(int layer, String presetId)
	{
		layerDefaults.put(layer, presetId);
		return this;
	}

	/**
	 * Sets a specific layer to require an attachment to be present on that layer, and sets the given
	 * attachment to be the "default" attachment of that layer.
	 *
	 * @param layer    The attachment layer to require an attachment on and select a preset for.
	 * @param presetId The ID of the attachment that will be enabled by default on that layer.
	 *
	 * @return this
	 */
	public BlasterAttachmentBuilder requireLayer(int layer, String presetId)
	{
		preset(layer, presetId);
		requiredLayers.add(layer);
		return this;
	}

	/**
	 * Finalizes this builder and generates an object describing the properties built using the builder
	 * methods. This should not be called by addons, it will be called automatically during mod initialization.
	 *
	 * @return A new built object describing the possible attachments and their layers as a set of bitmaps.
	 */
	@ApiStatus.Internal
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
