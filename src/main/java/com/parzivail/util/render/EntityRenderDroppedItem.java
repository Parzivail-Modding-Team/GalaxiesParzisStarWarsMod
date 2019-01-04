package com.parzivail.util.render;

import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

public class EntityRenderDroppedItem extends Render
{
	private final IItemRenderer itemRenderer;
	private final ItemStack representedItem;

	public EntityRenderDroppedItem(IItemRenderer itemRenderer, ItemStack representedItem)
	{
		this.itemRenderer = itemRenderer;
		this.representedItem = representedItem;
	}

	@Override
	public void doRender(Entity e, double x, double y, double z, float p_76986_8_, float p_76986_9_)
	{
		GL.PushMatrix();
		GL.Translate(x, y, z);
		GL.Scale(0.5f);
		itemRenderer.renderItem(IItemRenderer.ItemRenderType.ENTITY, representedItem);
		GL.PopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_)
	{
		return null;
	}
}
