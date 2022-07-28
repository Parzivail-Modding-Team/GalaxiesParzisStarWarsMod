package com.parzivail.pswg.client.species;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.container.SwgSpeciesRegistry;
import com.parzivail.util.client.screen.blit.BlittableAsset;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class SwgSpeciesIcons
{
	public static final Identifier ATLAS = Resources.id("textures/gui/character/icons.png");

	private static final HashMap<Identifier, Integer> SPECIES_TO_ATLAS = new HashMap<>();

	private static final BlittableAsset SMALL_BG_CIRCLE = new BlittableAsset(0, 30, 15, 15,512, 512);
	private static final BlittableAsset LARGE_BG_CIRCLE = new BlittableAsset(0, 280, 20, 20,512, 512);

	static
	{
		SPECIES_TO_ATLAS.put(SwgSpeciesRegistry.SPECIES_JAWA, 1);
		SPECIES_TO_ATLAS.put(SwgSpeciesRegistry.SPECIES_HUMAN, 2);
		SPECIES_TO_ATLAS.put(SwgSpeciesRegistry.SPECIES_CHISS, 3);
		SPECIES_TO_ATLAS.put(SwgSpeciesRegistry.SPECIES_PANTORAN, 4);
		SPECIES_TO_ATLAS.put(SwgSpeciesRegistry.SPECIES_DEVARONIAN, 5);
		SPECIES_TO_ATLAS.put(SwgSpeciesRegistry.SPECIES_TWILEK, 6);
		SPECIES_TO_ATLAS.put(SwgSpeciesRegistry.SPECIES_DUROS, 7);
		SPECIES_TO_ATLAS.put(SwgSpeciesRegistry.SPECIES_AQUALISH, 8);
		SPECIES_TO_ATLAS.put(SwgSpeciesRegistry.SPECIES_CHAGRIAN, 9);
		SPECIES_TO_ATLAS.put(SwgSpeciesRegistry.SPECIES_TOGRUTA, 10);
		SPECIES_TO_ATLAS.put(SwgSpeciesRegistry.SPECIES_RODIAN, 11);
		SPECIES_TO_ATLAS.put(SwgSpeciesRegistry.SPECIES_BITH, 12);
		SPECIES_TO_ATLAS.put(SwgSpeciesRegistry.SPECIES_WOOKIEE, 13);
		SPECIES_TO_ATLAS.put(SwgSpeciesRegistry.SPECIES_TRANDOSHAN, 14);
		SPECIES_TO_ATLAS.put(SwgSpeciesRegistry.SPECIES_KAMINOAN, 15);
		SPECIES_TO_ATLAS.put(SwgSpeciesRegistry.SPECIES_MON_CALAMARI, 16);
		SPECIES_TO_ATLAS.put(SwgSpeciesRegistry.SPECIES_IKTOTCHI, 17);
	}

	public static void renderSmall(MatrixStack matrices, int x, int y, Identifier species, boolean selected)
	{
		RenderSystem.setShaderTexture(0, ATLAS);
		DrawableHelper.drawTexture(matrices, x, y, 15 + 15 * SPECIES_TO_ATLAS.get(species), selected ? 15 : 0, 15, 15, 512, 512);
	}

	public static void renderSmallCircle(MatrixStack matrices, int x, int y, Identifier species, boolean selected)
	{
		RenderSystem.setShaderTexture(0, ATLAS);
		if (selected)
			SMALL_BG_CIRCLE.blit(matrices, x, y);
		DrawableHelper.drawTexture(matrices, x, y, 15 + 15 * SPECIES_TO_ATLAS.get(species), 30, 15, 15, 512, 512);
	}

	public static void renderLarge(MatrixStack matrices, int x, int y, Identifier species, boolean selected)
	{
		RenderSystem.setShaderTexture(0, ATLAS);
		DrawableHelper.drawTexture(matrices, x, y, 20 * SPECIES_TO_ATLAS.get(species), selected ? 260 : 240, 20, 20, 512, 512);
	}

	public static void renderLargeCircle(MatrixStack matrices, int x, int y, Identifier species, boolean selected)
	{
		RenderSystem.setShaderTexture(0, ATLAS);
		if (selected)
			LARGE_BG_CIRCLE.blit(matrices, x, y);
		DrawableHelper.drawTexture(matrices, x, y, 20 * SPECIES_TO_ATLAS.get(species), 280, 20, 20, 512, 512);
	}
}
