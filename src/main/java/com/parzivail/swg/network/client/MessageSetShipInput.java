package com.parzivail.swg.network.client;

import com.parzivail.swg.entity.EntityShip;
import com.parzivail.swg.network.PMessage;
import net.minecraft.entity.Entity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSetShipInput extends PMessage<MessageSetShipInput>
{
	public int dimension;
	public int id;
	public boolean forwardInputDown;
	public boolean backInputDown;
	public boolean leftInputDown;
	public boolean rightInputDown;

	public MessageSetShipInput()
	{
	}

	public MessageSetShipInput(EntityShip ship, boolean forwardInputDown, boolean backInputDown, boolean leftInputDown, boolean rightInputDown)
	{
		this.dimension = ship.dimension;
		this.id = ship.getEntityId();
		this.forwardInputDown = forwardInputDown;
		this.backInputDown = backInputDown;
		this.leftInputDown = leftInputDown;
		this.rightInputDown = rightInputDown;
	}

	@Override
	public IMessage handleMessage(MessageContext context)
	{
		WorldServer world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(this.dimension);
		Entity entity = world.getEntityByID(this.id);

		if (!(entity instanceof EntityShip))
			return null;

		((EntityShip)entity).setInputs(forwardInputDown, backInputDown, leftInputDown, rightInputDown);

		return null;
	}
}
