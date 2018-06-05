package com.parzivail.swg.render.weapon;

import com.parzivail.swg.Resources;
import com.parzivail.swg.render.model.weapon.ModelSpear;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class RenderSpear implements IItemRenderer
{
	private static ResourceLocation texture = new ResourceLocation(Resources.MODID, "textures/weapon/melee.spear.png");

	private ModelSpear model;

	public RenderSpear()
	{
		this.model = new ModelSpear();
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
				GL11.glTranslatef(-18.5f, -7f, -4f);
				GL11.glRotatef(25, 0, 0, 1);
				GL11.glRotatef(180, 0, 1, 0);
				GL11.glTranslatef(-10, 0, 0);
				GL11.glScalef(-1, 1, -1);
				this.model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.625F);
				GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glPopMatrix();
				break;
			case EQUIPPED:
				//				if (data[1] instanceof MobEwok)
				//				{
				//					GL11.glPushMatrix();
				//					GL11.glDisable(GL11.GL_CULL_FACE);
				//					GL11.glScalef(0.05F, -0.05F, 0.05F);
				//					GL11.glRotatef(48, 0, 1, 0);
				//					GL11.glRotatef(90, 0, 0, 1);
				//					GL11.glRotatef(90, 1, 0, 0);
				//					GL11.glTranslatef(-20, 13.5f, -6);
				//					this.model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.625F);
				//					GL11.glEnable(GL11.GL_CULL_FACE);
				//					GL11.glPopMatrix();
				//				}
				//				else
			{
				GL11.glPushMatrix();
				GL11.glDisable(GL11.GL_CULL_FACE);
				GL11.glScalef(0.065F, -0.065F, 0.065F);
				GL11.glRotatef(-40, 0, 1, 0);
				GL11.glRotatef(22, 0, 0, 1);
				if (data[1] instanceof EntityPlayer && ((EntityPlayer)data[1]).isBlocking())
				{
					GL11.glRotatef(30, 0, 1, 0);
					GL11.glTranslatef(-4, 0, 8);
				}
				GL11.glTranslatef(8, 6, 2);
				GL11.glRotatef(90, 0, 0, -1);
				GL11.glTranslatef(22, -3, 0);
				GL11.glRotatef(90, 0, 0, 1);
				GL11.glRotatef(-90, 1, 0, 0);
				this.model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.625F);
				GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glPopMatrix();
			}
			break;
			case EQUIPPED_FIRST_PERSON:
				GL11.glPushMatrix();
				GL11.glDisable(GL11.GL_CULL_FACE);
				GL11.glScalef(0.055F, -0.055F, 0.055F);
				GL11.glTranslatef(8, -23, 9);
				GL11.glRotatef(90, 0, 0, 1);
				GL11.glTranslatef(-10, 10, 4f);
				GL11.glRotatef(130, 1, 0, 0);
				if (data[1] instanceof EntityPlayer && ((EntityPlayer)data[1]).isBlocking())
				{
					GL11.glRotatef(-10, 0, 1, 0);
					GL11.glTranslatef(8, 0, -15);
					GL11.glRotatef(-40, 0, 1, 0);
					GL11.glTranslatef(8, 0, 0);
					GL11.glRotatef(-90, 1, 0, 0);
				}
				this.model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.625F);
				GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glPopMatrix();
				break;
			default:
				GL11.glPushMatrix();
				GL11.glDisable(GL11.GL_CULL_FACE);
				GL11.glScalef(0.04F, -0.04F, 0.04F);
				GL11.glRotatef(90, 0, 0, 1);
				GL11.glTranslatef(-25, -2, -2);
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