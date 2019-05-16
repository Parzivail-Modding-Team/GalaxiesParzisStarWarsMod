package com.parzivail.swg.handler;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.entity.EntityShip;
import com.parzivail.swg.proxy.SwgClientProxy;
import com.parzivail.util.math.lwjgl.Matrix4f;
import com.parzivail.util.math.lwjgl.Vector4f;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
				SwgClientProxy.mc.setRenderViewEntity(ship);

				// this is also the fastest time to poll input
				if (ship.getControllingPassenger() instanceof EntityPlayer && ship.world.isRemote)
				{
					EntityPlayer pilot = (EntityPlayer)ship.getControllingPassenger();
					StarWarsGalaxy.proxy.captureShipInput(pilot, ship);
				}
			}
			else if (SwgClientProxy.mc.getRenderViewEntity() instanceof EntityShip)
				SwgClientProxy.mc.setRenderViewEntity(SwgClientProxy.mc.player);
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
				Matrix4f rotatedAxes = ship.getRotation();
				Vector4f forward = Matrix4f.transform(rotatedAxes, new Vector4f(0, 0, 1, 0), null);
				Vector4f roll = Matrix4f.transform(rotatedAxes, new Vector4f(0, 1, 0, 0), null);
				forward = forward.normalise(null);

				float pitch = (float)(Math.asin(-forward.y) / Math.PI * 180);
				float yaw = (float)(Math.atan2(forward.x, forward.z) / Math.PI * 180);

				e.setPitch(pitch);
				e.setYaw(180 - yaw);
				e.setRoll(-ship.roll + ((roll.y < 0) ? 180 : 0));
			}
		}
	}
}
