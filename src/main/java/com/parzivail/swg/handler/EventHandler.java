package com.parzivail.swg.handler;

import com.parzivail.swg.ship.BasicFlightModel;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;

/**
 * Created by colby on 9/13/2017.
 */
public class EventHandler
{
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRender(RenderLivingEvent.Pre event)
	{
		if (event.entity instanceof EntityPlayer && ((EntityPlayer)event.entity).ridingEntity instanceof BasicFlightModel && event.isCancelable())
			event.setCanceled(true);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onKeyInput(InputEvent.KeyInputEvent event)
	{
		KeyHandler.onKeyInput(event);
	}

	//	@SubscribeEvent
	//	@SideOnly(Side.CLIENT)
	//	public void onRenderGui(RenderGameOverlayEvent.Post event)
	//	{
	//		Minecraft mc = Minecraft.getMinecraft();
	//	}
}
