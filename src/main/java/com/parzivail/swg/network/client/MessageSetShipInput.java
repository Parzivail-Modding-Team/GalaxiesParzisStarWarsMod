package com.parzivail.swg.network.client;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.entity.EntityShip;
import com.parzivail.swg.network.PMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovementInput;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSetShipInput extends PMessage<MessageSetShipInput>
{
	public int dimension;
	public int id;
	public float mouseDx;
	public float mouseDy;
	public float pitch;
	public float yaw;
	public float roll;
	public boolean forwardInputDown;
	public boolean backInputDown;
	public boolean leftInputDown;
	public boolean rightInputDown;

	public MessageSetShipInput()
	{
	}

	public MessageSetShipInput(EntityShip ship, EntityPlayer pilot, float pitch, float yaw, float roll)
	{
		this.dimension = ship.dimension;
		this.id = ship.getEntityId();
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
		MovementInput movementInput = StarWarsGalaxy.proxy.getMovementInput(pilot);
		this.forwardInputDown = movementInput.forwardKeyDown;
		this.backInputDown = movementInput.backKeyDown;
		this.leftInputDown = movementInput.leftKeyDown;
		this.rightInputDown = movementInput.rightKeyDown;
	}

	@Override
	public IMessage handleMessage(MessageContext context)
	{
		WorldServer world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(this.dimension);
		Entity entity = world.getEntityByID(this.id);

		if (!(entity instanceof EntityShip))
			return null;

		((EntityShip)entity).setInputs(this);

		return null;
	}
}
