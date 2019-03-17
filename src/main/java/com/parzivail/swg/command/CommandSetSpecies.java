package com.parzivail.swg.command;

import com.parzivail.swg.Resources;
import com.parzivail.swg.player.PswgExtProp;
import com.parzivail.swg.player.species.SpeciesType;
import com.parzivail.util.command.CommandParser;
import com.parzivail.util.command.Parameter;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;

/**
 * Created by colby on 9/10/2017.
 */
public class CommandSetSpecies extends CommandBase
{
	public String getCommandName()
	{
		return "species";
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
		return String.format("commands.%s.species.usage", Resources.MODID);
	}

	public void processCommand(ICommandSender sender, String[] parameters)
	{
		Args args = CommandParser.parse(new Args(), parameters);
		if (args == null)
			throw new WrongUsageException(getCommandUsage(sender));
		else
		{
			Entity player = CommandBase.getCommandSenderAsPlayer(sender);
			try
			{
				SpeciesType speciesType = SpeciesType.getNamedSpecies(args.species);

				PswgExtProp prop = PswgExtProp.get(player);
				if (prop == null)
					return;
				prop.setSpecies(speciesType);
			}
			catch (IllegalArgumentException ignored)
			{
				throw new WrongUsageException("Unsupported species");
			}
		}
	}

	private void spawn(Entity e, Entity player)
	{
		e.setLocationAndAngles(player.posX, player.posY, player.posZ, 0, 0);
		player.worldObj.spawnEntityInWorld(e);
	}

	public class Args
	{
		@Parameter(index = 0)
		public String species;
	}
}
