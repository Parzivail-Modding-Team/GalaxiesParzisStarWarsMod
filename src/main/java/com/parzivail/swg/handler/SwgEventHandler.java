package com.parzivail.swg.handler;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.entity.EntityCamera;
import com.parzivail.swg.entity.EntityShip;
import com.parzivail.swg.network.client.MessageSetShipInputMode;
import com.parzivail.swg.proxy.ShipInputMode;
import com.parzivail.swg.proxy.SwgClientProxy;
import com.parzivail.swg.register.KeybindRegister;
import com.parzivail.util.math.lwjgl.Vector3f;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.EntityViewRenderEvent;
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
		if (e.phase != TickEvent.Phase.START)
			return;

		if (SwgClientProxy.mc.player != null)
		{
			if (SwgClientProxy.mc.player.getRidingEntity() instanceof EntityShip)
			{
				EntityShip ship = (EntityShip)SwgClientProxy.mc.player.getRidingEntity();
				SwgClientProxy.mc.setRenderViewEntity(SwgClientProxy.entityCamera);

				// this is also the fastest time to poll input
				if (ship.getControllingPassenger() instanceof EntityPlayer && ship.world.isRemote)
				{
					EntityPlayer pilot = (EntityPlayer)ship.getControllingPassenger();
					StarWarsGalaxy.proxy.captureShipInput(pilot, ship);
				}
			}
			else if (SwgClientProxy.mc.getRenderViewEntity() instanceof EntityCamera)
				SwgClientProxy.mc.setRenderViewEntity(SwgClientProxy.mc.player);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(InputEvent.KeyInputEvent e)
	{
		if (KeybindRegister.keyShipChangeInputMode.isPressed())
		{
			if (SwgClientProxy.mc.player != null)
			{
				if (SwgClientProxy.mc.player.getRidingEntity() instanceof EntityShip)
				{
					EntityShip ship = (EntityShip)SwgClientProxy.mc.player.getRidingEntity();
					int shipInputMode = ship.getInputMode();
					shipInputMode++;
					shipInputMode %= SwgClientProxy.shipInputModes.length;
					ShipInputMode mode = SwgClientProxy.shipInputModes[shipInputMode];
					TextComponentTranslation textMode = new TextComponentTranslation(mode.langEntry);
					textMode.getStyle().setColor(TextFormatting.GREEN);
					StarWarsGalaxy.proxy.notifyPlayer(new TextComponentTranslation("pswg.gui.shipInputModeAlert", textMode), true);
					StarWarsGalaxy.NETWORK.sendToServer(new MessageSetShipInputMode(ship, shipInputMode));
				}
			}
		}
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
}
