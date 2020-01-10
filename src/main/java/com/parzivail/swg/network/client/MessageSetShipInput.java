package com.parzivail.swg.network.client;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.entity.EntityShip;
import com.parzivail.swg.network.PMessage;
import com.parzivail.util.math.lwjgl.Matrix4f;
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
	public Matrix4f rotation;
	public boolean forwardInputDown;
	public boolean backInputDown;
	public boolean leftInputDown;
	public boolean rightInputDown;

	public MessageSetShipInput()
	{
	}

	public MessageSetShipInput(EntityShip ship, EntityPlayer pilot, Matrix4f rotation)
	{
		this.dimension = ship.dimension;
		this.id = ship.getEntityId();
		this.rotation = rotation;
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
