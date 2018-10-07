package com.parzivail.swg.render.binoculars;

import com.parzivail.swg.Resources;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class RenderMacrobinoculars implements IItemRenderer
{
	private static final ResourceLocation texture = new ResourceLocation(Resources.MODID, "textures/binocular/macro.png");

	private final ModelBinocularsNew model;

	public RenderMacrobinoculars()
	{
		model = new ModelBinocularsNew();
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		GL11.glPushMatrix();
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		switch (type)
		{
			case INVENTORY:
				GL11.glPushMatrix();
				GL11.glDisable(GL11.GL_CULL_FACE);
				GL11.glScalef(0.05F, -0.05F, 0.05F);
				GL11.glTranslatef(-10, 1.0f, 1f);
				GL11.glRotatef(0, 0, 0, 1);
				GL11.glRotatef(260, 0, 1, 0);
				GL11.glTranslatef(-10, 0, 0);
				GL11.glScalef(1, 1, -1);
				model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.625F);
				GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glPopMatrix();
				break;
			case EQUIPPED:
				GL11.glPushMatrix();
				GL11.glDisable(GL11.GL_CULL_FACE);
				GL11.glScalef(0.03F, -0.03F, 0.03F);
				GL11.glRotatef(-40, 0, 1, 0);
				GL11.glRotatef(24, 0, 0, 1);
				GL11.glTranslatef(19, 7, 0);
				GL11.glRotatef(90, 1, 0, 0);
				GL11.glRotatef(90, 0, 0, -1);
				GL11.glRotatef(90, 1, 0, 0);
				GL11.glRotatef(88, -1, 0, 0);
				GL11.glTranslatef(-2.75f, -2, 35);
				GL11.glScalef(-1, -1, 1);
				GL11.glRotatef(180, 0, 0, 1);
				GL11.glRotatef(90, 0, 1, 0);
				GL11.glRotatef(20, 0, 0, 1);
				GL11.glRotatef(4, 0, 0, 1);
				GL.Scale(2);
				model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.625F);
				GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glPopMatrix();
				break;
			case EQUIPPED_FIRST_PERSON:
				GL11.glPushMatrix();
				GL11.glDisable(GL11.GL_CULL_FACE);
				GL11.glScalef(0.027F, -0.027F, 0.027F);
				GL11.glTranslatef(-20, -55, 22);
				GL11.glRotatef(40, 0, 1, 0);
				GL11.glRotatef(10, 0, 0, 1);
				model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.625F);
				GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glPopMatrix();
				break;
			default:
				GL11.glPushMatrix();
				GL11.glDisable(GL11.GL_CULL_FACE);
				GL11.glScalef(0.04F, -0.04F, 0.04F);
				GL11.glTranslatef(0, 5, 0);
				GL11.glTranslatef(0, -20, 0);
				model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.625F);
				GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glPopMatrix();
				break;
		}
		GL11.glPopMatrix();
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return true;
	}
}
