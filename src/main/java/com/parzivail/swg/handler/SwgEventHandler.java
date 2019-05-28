package com.parzivail.swg.handler;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.entity.EntityCamera;
import com.parzivail.swg.entity.EntityShip;
import com.parzivail.swg.gui.GuiHyperspaceLoading;
import com.parzivail.swg.network.client.MessageSetShipInputMode;
import com.parzivail.swg.proxy.ShipInputMode;
import com.parzivail.swg.proxy.SwgClientProxy;
import com.parzivail.swg.register.KeybindRegister;
import com.parzivail.util.item.ILeftClickInterceptor;
import com.parzivail.util.math.lwjgl.Vector3f;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SwgEventHandler
{
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(TickEvent.RenderTickEvent e)
	{
		SwgClientProxy.animHyperspaceTest.tick();

		if (e.phase != TickEvent.Phase.START)
			return;

		if (SwgClientProxy.mc.player != null)
		{
			ItemStack heldItem = SwgClientProxy.mc.player.getHeldItemMainhand();
			KeybindRegister.keyAttack.setIntercepting(heldItem.getItem() instanceof ILeftClickInterceptor);

			if (heldItem.getItem() instanceof ILeftClickInterceptor)
				SwgClientProxy.checkLeftClickPressed(true);

			Entity rve = SwgClientProxy.mc.getRenderViewEntity();
			if (SwgClientProxy.mc.player.getRidingEntity() instanceof EntityShip)
			{
				EntityShip ship = (EntityShip)SwgClientProxy.mc.player.getRidingEntity();

				if (SwgClientProxy.mc.gameSettings.thirdPersonView == 0 || SwgClientProxy.mc.gameSettings.thirdPersonView == 2)
					SwgClientProxy.mc.setRenderViewEntity(ship);
				else
					SwgClientProxy.mc.setRenderViewEntity(ship.chaseCam);

				// this is also the fastest time to poll input
				if (ship.getControllingPassenger() instanceof EntityPlayer && ship.world.isRemote)
				{
					EntityPlayer pilot = (EntityPlayer)ship.getControllingPassenger();
					StarWarsGalaxy.proxy.captureShipInput(pilot, ship);
				}
			}
			else if (rve instanceof EntityCamera || rve instanceof EntityShip)
				SwgClientProxy.mc.setRenderViewEntity(SwgClientProxy.mc.player);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(TickEvent.ClientTickEvent event)
	{
		if (event.phase != TickEvent.Phase.START)
			return;

		if (SwgClientProxy.mc.player != null)
		{
			//			SoundHandler.tick(event);
			//			WorldDecals.tick(SwgClientProxy.mc.player.dimension);

			if (SwgClientProxy.leftClickDelayTimer > 0)
				SwgClientProxy.leftClickDelayTimer--;
			else
				SwgClientProxy.leftClickDelayTimer = 0;
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(InputEvent.MouseInputEvent event)
	{
		SwgClientProxy.checkLeftClickPressed(false);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(InputEvent.KeyInputEvent e)
	{
		SwgClientProxy.checkLeftClickPressed(false);

		if (SwgClientProxy.mc.player != null)
		{
			if (SwgClientProxy.mc.player.getRidingEntity() instanceof EntityShip)
			{
				if (KeybindRegister.keyShipChangeInputMode.isPressed())
				{
					EntityShip ship = (EntityShip)SwgClientProxy.mc.player.getRidingEntity();
					int shipInputMode = ship.getInputMode();
					shipInputMode++;
					shipInputMode %= 2; // only want Yaw and Roll for mode switch button
					setInputMode(ship, shipInputMode);
				}

				if (KeybindRegister.keyShipLandingMode.isPressed())
				{
					EntityShip ship = (EntityShip)SwgClientProxy.mc.player.getRidingEntity();
					int shipInputMode = 2;
					setInputMode(ship, shipInputMode);
				}
			}

			if (KeybindRegister.keyDebug != null && KeybindRegister.keyDebug.isPressed())
			{
				if (SwgClientProxy.animHyperspaceTest.playing)
				{
					SwgClientProxy.animHyperspaceTest.stop();
					SwgClientProxy.animHyperspaceTest.reset();
				}
				else
					SwgClientProxy.animHyperspaceTest.play();
			}
		}
	}

	private void setInputMode(EntityShip ship, int shipInputMode)
	{
		ShipInputMode mode = EntityShip.shipInputModes[shipInputMode];
		TextComponentTranslation textMode = new TextComponentTranslation(mode.langEntry);
		textMode.getStyle().setColor(TextFormatting.GREEN);
		StarWarsGalaxy.proxy.notifyPlayer(new TextComponentTranslation("pswg.gui.shipInputModeAlert", textMode), true);
		StarWarsGalaxy.NETWORK.sendToServer(new MessageSetShipInputMode(ship, shipInputMode));
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(EntityViewRenderEvent.CameraSetup e)
	{
		if (SwgClientProxy.mc.player != null)
		{
			if (SwgClientProxy.mc.player.getRidingEntity() instanceof EntityShip)
			{
				EntityShip ship = (EntityShip)SwgClientProxy.mc.player.getRidingEntity();
				Vector3f angles = ship.getEulerAngles();

				e.setPitch(angles.x);
				e.setYaw(180 - angles.y);
				e.setRoll(angles.z);
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(GuiOpenEvent event)
	{
		if (event.getGui() instanceof GuiDownloadTerrain && SwgClientProxy.mc.player != null)
		{
			int dim = 2; // TODO: check against any SW dim
			if (SwgClientProxy.mc.player.dimension == dim)
				event.setGui(new GuiHyperspaceLoading());
		}
	}
}
