package com.parzivail.swg.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;

/**
 * Created by colby on 9/13/2017.
 */
public class EventHandler
{
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onTick(TickEvent.ClientTickEvent event)
	{
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRender(RenderLivingEvent.Pre event)
	{
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRenderGui(RenderGameOverlayEvent.Post event)
	{
		Minecraft mc = Minecraft.getMinecraft();

		//		GL.PushMatrix();
		//		GL.Disable(EnableCap.Texture2D);
		//		GL.Scale(0.25f);
		//		GL11.glColor3f(1, 1, 1);
		//		Fx.D2.DrawSolidRectangle(mc.displayWidth / 4f, mc.displayHeight / 4f, mc.displayWidth / 2f, mc.displayHeight / 2f);
		//		GL.Enable(EnableCap.Texture2D);
		//		GL.PopMatrix();
	}
}
