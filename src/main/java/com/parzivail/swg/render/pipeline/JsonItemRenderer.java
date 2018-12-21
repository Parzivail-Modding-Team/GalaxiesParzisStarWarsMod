package com.parzivail.swg.render.pipeline;

import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class JsonItemRenderer extends JsonModelRenderer implements IItemRenderer
{
	public JsonItemRenderer(ResourceLocation modelLocation)
	{
		super(modelLocation);
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return type == ItemRenderType.ENTITY;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		GL.PushAttrib(AttribMask.EnableBit);
		RenderHelper.disableStandardItemLighting();
		if (!compiled)
		{
			displayList = GLAllocation.generateDisplayLists(1);
			GL11.glNewList(displayList, GL11.GL_COMPILE);

			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			drawItem(ModelRotation.X0_Y270, MAX_BRIGHTNESS);
			tessellator.draw();

			GL11.glEndList();

			compiled = true;
		}
		else
			GL11.glCallList(displayList);
		GL.PopAttrib();
	}
}
