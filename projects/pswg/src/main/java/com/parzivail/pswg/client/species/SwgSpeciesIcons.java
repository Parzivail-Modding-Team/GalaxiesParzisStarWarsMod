package com.parzivail.pswg.client.species;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.util.client.screen.blit.BlittableAsset;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class SwgSpeciesIcons
{
	private static record Entry(Identifier atlas, int index)
	{
	}

	private static final HashMap<Identifier, Entry> SPECIES_TO_ATLAS = new HashMap<>();

	private static final BlittableAsset SMALL_BG_CIRCLE = new BlittableAsset(0, 30, 15, 15, 512, 512);
	private static final BlittableAsset LARGE_BG_CIRCLE = new BlittableAsset(0, 280, 20, 20, 512, 512);

	public static void register(Identifier id, Identifier atlas, int index)
	{
		SPECIES_TO_ATLAS.put(id, new Entry(atlas, index));
	}

	public static void renderSmall(MatrixStack matrices, int x, int y, Identifier species, boolean selected)
	{
		var entry = SPECIES_TO_ATLAS.get(species);
		RenderSystem.setShaderTexture(0, entry.atlas);
		DrawableHelper.drawTexture(matrices, x, y, 15 + 15 * entry.index, selected ? 15 : 0, 15, 15, 512, 512);
	}

	public static void renderSmallCircle(MatrixStack matrices, int x, int y, Identifier species, boolean selected)
	{
		var entry = SPECIES_TO_ATLAS.get(species);
		RenderSystem.setShaderTexture(0, entry.atlas);
		if (selected)
			SMALL_BG_CIRCLE.blit(matrices, x, y);
		DrawableHelper.drawTexture(matrices, x, y, 15 + 15 * entry.index, 30, 15, 15, 512, 512);
	}

	public static void renderLarge(MatrixStack matrices, int x, int y, Identifier species, boolean selected)
	{
		var entry = SPECIES_TO_ATLAS.get(species);
		RenderSystem.setShaderTexture(0, entry.atlas);
		DrawableHelper.drawTexture(matrices, x, y, 20 * entry.index, selected ? 260 : 240, 20, 20, 512, 512);
	}

	public static void renderLargeCircle(MatrixStack matrices, int x, int y, Identifier species, boolean selected)
	{
		var entry = SPECIES_TO_ATLAS.get(species);
		RenderSystem.setShaderTexture(0, entry.atlas);
		if (selected)
			LARGE_BG_CIRCLE.blit(matrices, x, y);
		DrawableHelper.drawTexture(matrices, x, y, 20 * entry.index, 280, 20, 20, 512, 512);
	}
}
