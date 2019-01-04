package com.parzivail.swg.command;

import com.parzivail.swg.Resources;
import com.parzivail.util.command.CommandParser;
import com.parzivail.util.command.Parameter;
import com.parzivail.util.dimension.Rift;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;

/**
 * Created by colby on 9/10/2017.
 */
public class CommandChangeDimensions extends CommandBase
{
	public String getCommandName()
	{
		return "cdim";
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
		return String.format("commands.%s.cdim.usage", Resources.MODID);
	}

	public void processCommand(ICommandSender sender, String[] parameters)
	{
		Args args = CommandParser.parse(new Args(), parameters);
		if (args == null)
			throw new WrongUsageException(getCommandUsage(sender));
		else
		{
			Entity player = CommandBase.getCommandSenderAsPlayer(sender);
			Rift.travelEntity(player.worldObj, player, args.dimension);
		}
	}

	public class Args
	{
		@Parameter(index = 0)
		public int dimension;
	}
}
