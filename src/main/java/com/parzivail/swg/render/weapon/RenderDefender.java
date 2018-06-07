package com.parzivail.swg.render.weapon;

import com.parzivail.swg.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class RenderDefender implements IItemRenderer
{
	private static ResourceLocation texture = new ResourceLocation(Resources.MODID, "textures/weapon/rifle.defender.png");

	private ModelDefender model;

	public RenderDefender()
	{
		this.model = new ModelDefender();
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
				GL11.glScalef(0.04F, -0.04F, 0.04F);
				GL11.glTranslatef(-4, -4f, 1f);
				GL11.glRotatef(25, 0, 0, 1);
				GL11.glTranslatef(0, -5, 2);
				this.model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.625F);
				GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glPopMatrix();
				break;
			case EQUIPPED:
				GL11.glPushMatrix();
				GL11.glDisable(GL11.GL_CULL_FACE);
				GL11.glScalef(0.045F, -0.045F, 0.045F);
				GL11.glRotatef(-40, 0, 1, 0);
				GL11.glRotatef(22, 0, 0, 1);
				GL11.glTranslatef(11, -2, 0);
				GL11.glRotatef(90, 0, 0, -1);
				GL11.glTranslatef(-4, 3, 0);
				this.model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.625F);
				GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glPopMatrix();
				break;
			case EQUIPPED_FIRST_PERSON:
				GL11.glPushMatrix();
				GL11.glDisable(GL11.GL_CULL_FACE);
				GL11.glScalef(0.04F, -0.04F, 0.04F);
				GL11.glTranslatef(8, -23, 9);
				GL11.glRotatef(90, 0, 0, 1);
				GL11.glRotatef(20, 1, 0, 0);
				GL11.glTranslatef(-11, 0, -10);
				GL11.glRotatef(90, 0, 0, -1);
				GL11.glRotatef(30, 0, 1, 0);
				GL11.glRotatef(180, 0, 0, 1);
				if (data[1] instanceof EntityPlayer && ((EntityPlayer)data[1]).isBlocking())
				{
					GL11.glRotatef(-30, 0, 0, 1);
					GL11.glRotatef(90, 1, 0, 0);
					GL11.glTranslatef(2, 0, -12);
				}
				else
				{
					GL11.glRotatef(180, 1, 0, 0);
					GL11.glTranslatef(0, 0, -6);
				}
				this.model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.625F);
				GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glPopMatrix();
				break;
			default:
				GL11.glPushMatrix();
				GL11.glDisable(GL11.GL_CULL_FACE);
				GL11.glScalef(0.05F, -0.05F, 0.05F);
				GL11.glTranslatef(0, -8, 0);
				GL11.glScalef(1, 1, -1);
				this.model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.625F);
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