package com.parzivail.swg.render.weapon;

import com.parzivail.swg.Resources;
import com.parzivail.swg.proxy.Client;
import com.parzivail.swg.render.model.weapon.ModelTuskenRifle;
import com.parzivail.swg.weapon.ItemBlasterRifle;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class RenderTuskenRifle implements IItemRenderer
{
	private static ResourceLocation texture = new ResourceLocation(Resources.MODID, "textures/weapon/tuskenRifle.png");

	private ModelTuskenRifle model;

	public RenderTuskenRifle()
	{
		this.model = new ModelTuskenRifle();
	}

	@Override
	public boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type)
	{
		return true;
	}

	@Override
	public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data)
	{
		GL11.glPushMatrix();
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		switch (type)
		{
			case INVENTORY:
				GL11.glPushMatrix();
				GL.Scale(0.1f);
				GL.Translate(3, 0, 1);
				GL.Rotate(-20, 0, 0, 1);
				GL.Rotate(180, 1, 0, 0);
				this.model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.625F);
				GL11.glPopMatrix();
				break;
			case EQUIPPED:
				GL11.glPushMatrix();
				GL.Scale(0.3f);
				GL.Rotate(-45, 0, 1, 0);
				GL.Rotate(-290, 0, 0, 1);
				GL.Rotate(180, 1, 0, 0);
				GL.Translate(2, 1.5f, -1);
				this.model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.625F);
				GL11.glPopMatrix();
				break;
			case EQUIPPED_FIRST_PERSON:
				if (Client.mc.gameSettings.viewBobbing)
					Client.revertViewBobbing(Client.renderPartialTicks);
				EntityPlayer player = (EntityPlayer)data[1];
				GL11.glPushMatrix();
				GL.Scale(0.3f);
				GL.Rotate(180, 1, 0, 0);
				GL.Rotate(140, 0, 1, 0);
				ItemBlasterRifle rifle = (ItemBlasterRifle)item.getItem();
				float ads = rifle.getAdsLerp(item, player.worldObj, player);
				float notAds = 1 - ads;
				GL.Rotate(-40 * notAds + 0 * ads, 0, 0, 1);
				GL.Translate(2.7 * ads, -2 * notAds + -5.84 * ads, 2 * notAds + -1.6 * ads);
				this.model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.625F);
				GL11.glPopMatrix();
				break;
			default:
				GL11.glPushMatrix();
				GL.Scale(0.2f);
				GL.Rotate(180, 1, 0, 0);
				GL.Translate(0, -2, 0);
				this.model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.625F);
				GL11.glPopMatrix();
				break;
		}
		GL11.glPopMatrix();
	}

	@Override
	public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper)
	{
		return true;
	}
}