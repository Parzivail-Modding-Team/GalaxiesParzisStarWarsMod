package com.parzivail.swg.command;

import com.parzivail.swg.Resources;
import com.parzivail.swg.ship.BasicFlightModel;
import com.parzivail.swg.ship.VehicleT65;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;

/**
 * Created by colby on 9/10/2017.
 */
public class CommandSpawnShip extends CommandBase
{
	public String getCommandName()
	{
		return "ship";
	}

	/**
	 * Return the required permission level for this command.
	 */
	public int getRequiredPermissionLevel()
	{
		return 2;
	}

	public String getCommandUsage(ICommandSender sender)
	{
		return String.format("commands.%s.ship.usage", Resources.MODID);
	}

	public void processCommand(ICommandSender sender, String[] parameters)
	{
		Args args = CommandParser.parse(new Args(), parameters);
		if (args == null)
			throw new WrongUsageException(getCommandUsage(sender));
		else
		{
			Entity player = getCommandSenderAsPlayer(sender);
			switch (args.ship)
			{
				case "xwing":
					spawn(new VehicleT65(player.worldObj), player);
			}
		}
	}

	private void spawn(BasicFlightModel e, Entity player)
	{
		e.setLocationAndAngles(player.posX, player.posY, player.posZ, 0, 0);
		player.worldObj.spawnEntityInWorld(e);
		e.spawnSeats();
	}

	public class Args
	{
		@Parameter(index = 0)
		public String ship;
	}
}
