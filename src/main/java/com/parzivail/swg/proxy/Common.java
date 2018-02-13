package com.parzivail.swg.proxy;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.network.MessageSpawnParticle;
import com.parzivail.swg.ship.BasicFlightModel;
import com.parzivail.util.common.Lumberjack;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

/**
 * Created by colby on 9/10/2017.
 */
public class Common
{
	private MinecraftServer MCServer;

	public void init()
	{
		Lumberjack.log("Server proxy loaded!");
	}

	public void registerRendering()
	{
	}

	public boolean isThePlayer(EntityPlayer entity)
	{
		return false;
	}

	public boolean isServer()
	{
		return true;
	}

	public void spawnParticle(World world, String name, double x, double y, double z, double vx, double vy, double vz)
	{
		StarWarsGalaxy.network.sendToDimension(new MessageSpawnParticle(world.provider.dimensionId, name, x, y, z, vx, vy, vz), world.provider.dimensionId);
	}

	public Entity getEntityById(int dim, int id)
	{
		return MinecraftServer.getServer().worldServerForDimension(dim).getEntityByID(id);
	}

	public MinecraftServer getMCServer() {
		return FMLCommonHandler.instance().getMinecraftServerInstance();
	}

	public void setupShipRender(BasicFlightModel ship)
	{
	}

	public void postInit()
	{
	}
}
