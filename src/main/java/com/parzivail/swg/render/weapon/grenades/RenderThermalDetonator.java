package com.parzivail.swg.render.weapon.grenades;

import com.parzivail.swg.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class RenderThermalDetonator implements IItemRenderer
{
	private static final ResourceLocation texture = new ResourceLocation(Resources.MODID, "textures/grenade/thermal.png");

	private final ModelThermalDetonator model;

	public RenderThermalDetonator()
	{
		model = new ModelThermalDetonator();
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
				GL11.glScalef(0.4F, -0.4F, 0.4F);
				GL11.glTranslatef(6f, 3.3f, -4f);
				GL11.glRotatef(0, 0, 0, 1);
				GL11.glTranslatef(-10, 0, 0);
				GL11.glScalef(-1, 1, -1);
				model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.625F);
				GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glPopMatrix();
				break;
			case EQUIPPED:
				GL11.glPushMatrix();
				GL11.glDisable(GL11.GL_CULL_FACE);
				GL11.glScalef(0.25f, -0.25f, 0.25f);
				GL11.glRotatef(70, 1, 0, 0);
				GL11.glRotatef(-45, 0, 0, 1);
				GL11.glRotatef(15, 0, 1, 0);
				GL11.glRotatef(-50, 1, 0, 0);
				GL11.glTranslatef(-0.2f, 4, 3);
				GL11.glTranslatef(0, -4, 1.5f);
				model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.625F);
				GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glPopMatrix();
				break;
			case EQUIPPED_FIRST_PERSON:
				GL11.glPushMatrix();
				GL11.glDisable(GL11.GL_CULL_FACE);
				GL11.glScalef(0.15F, -0.15F, 0.15F);
				GL11.glTranslatef(-1, -7, 4);
				model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.625F);
				GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glPopMatrix();
				break;
			default:
				GL11.glPushMatrix();
				GL11.glDisable(GL11.GL_CULL_FACE);
				GL11.glScalef(0.2F, -0.2F, 0.2F);
				GL11.glTranslatef(0, -2, 0);
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
