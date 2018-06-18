package com.parzivail.swg.proxy;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.network.MessageCreateDecal;
import com.parzivail.swg.network.MessageSpawnParticle;
import com.parzivail.util.common.Lumberjack;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
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

	public void createDecal(World world, int type, float x, float y, float z, float size, EnumFacing direction)
	{
		StarWarsGalaxy.network.sendToAllAround(new MessageCreateDecal(world.provider.dimensionId, type, x, y, z, size, direction), new NetworkRegistry.TargetPoint(world.provider.dimensionId, x, y, z, 100));
	}

	public Entity getEntityById(int dim, int id)
	{
		return MinecraftServer.getServer().worldServerForDimension(dim).getEntityByID(id);
	}

	public MinecraftServer getMCServer() {
		return FMLCommonHandler.instance().getMinecraftServerInstance();
	}

	public void postInit()
	{
	}

	public void spawnSmokeParticle(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ)
	{
	}
}
