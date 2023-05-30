package com.parzivail.pswg.client.species;

import com.parzivail.util.client.screen.blit.BlittableDynamicAsset;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class SwgSpeciesIcons
{
	private static record Entry(Identifier atlas, int index)
	{
	}

	private static final HashMap<Identifier, Entry> SPECIES_TO_ATLAS = new HashMap<>();

	private static final BlittableDynamicAsset SMALL_BG_CIRCLE = new BlittableDynamicAsset(0, 30, 15, 15, 512, 512);
	private static final BlittableDynamicAsset LARGE_BG_CIRCLE = new BlittableDynamicAsset(0, 280, 20, 20, 512, 512);

	public static void register(Identifier id, Identifier atlas, int index)
	{
		SPECIES_TO_ATLAS.put(id, new Entry(atlas, index));
	}

	public static void renderSmall(DrawContext context, int x, int y, Identifier species, boolean selected)
	{
		var entry = SPECIES_TO_ATLAS.get(species);
		context.drawTexture(entry.atlas, x, y, 15 + 15 * entry.index, selected ? 15 : 0, 15, 15, 512, 512);
	}

	public static void renderSmallCircle(DrawContext context, int x, int y, Identifier species, boolean selected)
	{
		var entry = SPECIES_TO_ATLAS.get(species);
		if (selected)
		{
			SMALL_BG_CIRCLE.setSource(entry.atlas);
			SMALL_BG_CIRCLE.blit(context, x, y);
		}
		context.drawTexture(entry.atlas, x, y, 15 + 15 * entry.index, 30, 15, 15, 512, 512);
	}

	public static void renderLarge(DrawContext context, int x, int y, Identifier species, boolean selected)
	{
		var entry = SPECIES_TO_ATLAS.get(species);
		context.drawTexture(entry.atlas, x, y, 20 * entry.index, selected ? 260 : 240, 20, 20, 512, 512);
	}

	public static void renderLargeCircle(DrawContext context, int x, int y, Identifier species, boolean selected)
	{
		var entry = SPECIES_TO_ATLAS.get(species);
		if (selected)
		{
			LARGE_BG_CIRCLE.setSource(entry.atlas);
			LARGE_BG_CIRCLE.blit(context, x, y);
		}
		context.drawTexture(entry.atlas, x, y, 20 * entry.index, 280, 20, 20, 512, 512);
	}
}
