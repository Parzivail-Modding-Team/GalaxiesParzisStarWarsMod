package com.parzivail.swg.render.pipeline;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

@SideOnly(Side.CLIENT)
public class BakedQuad
{
	/**
	 * Joined 4 vertex records, each stores packed data according to the VertexFormat of the quad. Vanilla minecraft
	 * uses DefaultVertexFormats.BLOCK, Forge uses (usually) ITEM, use BakedQuad.getFormat() to get the correct format.
	 */
	protected final int[] vertexData;
	protected final int tintIndex;
	protected final EnumFacing face;
	protected final TextureAtlasSprite sprite;

	/**
	 * @deprecated Use constructor with the format argument.
	 */
	@Deprecated
	public BakedQuad(int[] vertexDataIn, int tintIndexIn, EnumFacing faceIn, TextureAtlasSprite spriteIn)
	{
		this(vertexDataIn, tintIndexIn, faceIn, spriteIn, true);
	}

	public BakedQuad(int[] vertexDataIn, int tintIndexIn, EnumFacing faceIn, TextureAtlasSprite spriteIn, boolean applyDiffuseLighting)
	{
		this.applyDiffuseLighting = applyDiffuseLighting;
		vertexData = vertexDataIn;
		tintIndex = tintIndexIn;
		face = faceIn;
		sprite = spriteIn;
	}

	public TextureAtlasSprite getSprite()
	{
		return sprite;
	}

	public int[] getVertexData()
	{
		return vertexData;
	}

	public boolean hasTintIndex()
	{
		return tintIndex != -1;
	}

	public int getTintIndex()
	{
		return tintIndex;
	}

	public EnumFacing getFace()
	{
		return face;
	}

	protected final boolean applyDiffuseLighting;

	public boolean shouldApplyDiffuseLighting()
	{
		return applyDiffuseLighting;
	}
}
