package com.parzivail.util.render;

import com.parzivail.util.ui.gltk.GL;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;

public class RenderBasicTileItem implements IItemRenderer
{
	private final float scale;
	private final TileEntity tile;
	private final TileEntitySpecialRenderer tesr;

	public RenderBasicTileItem(Block block, float scale)
	{
		tile = block.createTileEntity(null, 0);
		this.scale = scale;
		tesr = (TileEntitySpecialRenderer)TileEntityRendererDispatcher.instance.mapSpecialRenderers.get(tile.getClass());
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		if (tesr == null)
			return;

		GL.PushMatrix();
		if (type == ItemRenderType.INVENTORY)
		{
			GL.Translate(0, 0.8 * (scale - 1), 0);
			GL.Scale(scale);
		}
		switch (type)
		{
			case EQUIPPED_FIRST_PERSON:
				GL.Rotate(90, 0, 1, 0);
				break;
			case EQUIPPED:
			case INVENTORY:
			case FIRST_PERSON_MAP:
			case ENTITY:
				GL.Rotate(-90, 0, 1, 0);
				break;
		}
		tesr.renderTileEntityAt(tile, -0.5f, -0.5f, -0.5f, 0);
		GL.PopMatrix();
	}
}
