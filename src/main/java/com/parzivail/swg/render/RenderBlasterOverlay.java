package com.parzivail.swg.render;

import com.parzivail.swg.Resources;
import com.parzivail.swg.item.ItemBlaster;
import com.parzivail.swg.item.data.BlasterData;
import com.parzivail.swg.proxy.Client;
import com.parzivail.util.ui.FxMC;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class RenderBlasterOverlay
{
	protected static final ResourceLocation WIDGETS_TEX_PATH = Resources.location("textures/gui/widgets.png");

	public static void render(RenderGameOverlayEvent event)
	{
		if (Client.mc == null || Client.mc.player == null)
			return;

		EntityPlayer player = Client.mc.player;
		ItemStack heldStack = player.getHeldItemMainhand();
		Item heldItem = heldStack.getItem();

		if (!(heldItem instanceof ItemBlaster))
			return;

		ItemBlaster blaster = (ItemBlaster)heldItem;
		BlasterData d = new BlasterData(heldStack);

		GL.Color(0xFFFFFFFF);
		Client.mc.getTextureManager().bindTexture(WIDGETS_TEX_PATH);

		ScaledResolution sr = event.getResolution();

		if (d.shotTimer > 0)
		{
			int i = sr.getScaledWidth() / 2;
			int i2 = sr.getScaledHeight() - 17;
			int j2 = i + 91 + 6 + 22;

			EnumHandSide enumhandside = player.getPrimaryHand().opposite();
			if (enumhandside == EnumHandSide.RIGHT)
			{
				j2 = i - 91 - 22 - 40;
			}

			int split = (int)(((float)d.shotTimer / blaster.descriptor.autofireTimeTicks) * 34);
			FxMC.drawTexturedModalRect(j2, i2, 0, 0, 0, 36, 12);
			FxMC.drawTexturedModalRect(j2 + split, i2, 0, split, 12, 35 - split, 12);
		}

		if (d.isCoolingDown())
		{
			int split = (int)(((float)d.cooldownTimer / blaster.descriptor.cooldownTimeTicks) * 62);

			FxMC.drawTexturedModalRect(sr.getScaledWidth() / 2 - 32, sr.getScaledHeight() / 2 + 18, 0, 0, 24, 64, 5);
			FxMC.drawTexturedModalRect(sr.getScaledWidth() / 2 - 31, sr.getScaledHeight() / 2 + 18, 0, 1, 29, split, 5);

			FxMC.drawTexturedModalRect(sr.getScaledWidth() / 2 - 32 + split, sr.getScaledHeight() / 2 + 17, 0, 0, 34, 3, 7);
		}
		else if (d.heat > 0)
		{
			int split = (int)(((float)d.heat / (blaster.descriptor.roundsBeforeOverheat * d.getHeatPerShot())) * 62);
			FxMC.drawTexturedModalRect(sr.getScaledWidth() / 2 - 32, sr.getScaledHeight() / 2 + 18, 0, 0, 24, 64, 5);
			FxMC.drawTexturedModalRect(sr.getScaledWidth() / 2 - 31, sr.getScaledHeight() / 2 + 18, 0, 1, 29, split, 5);
		}
	}
}
