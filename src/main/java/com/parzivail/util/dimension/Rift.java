package com.parzivail.util.dimension;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.util.common.Lumberjack;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by colby on 9/10/2017.
 */
public class Rift
{
	public static boolean travelEntity(World world, Entity entity, int dimension)
	{
		ChunkCoordinates spawnPos;// = null;
		float spawnYaw = 0;

		if (world.isRemote)
		{
			return false;
		}
		else
		{
			float yaw = spawnYaw;

			MinecraftServer mcServer = StarWarsGalaxy.proxy.getMCServer();
			if (mcServer == null)
			{
				return false;
			}
			else
			{
				WorldServer newworld = mcServer.worldServerForDimension(dimension);
				spawnPos = newworld.getSpawnPoint();
				if (newworld == null)
				{
					System.err.println("Cannot Link Entity to Dimension: Could not get World for Dimension " + dimension);
					return false;
				}
				else
				{
					teleportEntity(newworld, entity, dimension, spawnPos, yaw);
					return true;
				}
			}
		}
	}

	private static Entity teleportEntity(World newworld, Entity entity, int dimension, ChunkCoordinates spawn, float yaw)
	{
		World origin = entity.worldObj;
		Entity mount = entity.ridingEntity;
		if (entity.ridingEntity != null)
		{
			entity.mountEntity(null);
			mount = teleportEntity(newworld, mount, dimension, spawn, yaw);
		}

		boolean changingworlds = origin != newworld;
		origin.updateEntityWithOptionalForce(entity, false);
		EntityPlayerMP player;
		if (entity instanceof EntityPlayerMP)
		{
			player = (EntityPlayerMP)entity;
			player.closeScreen();
			if (changingworlds)
			{
				player.dimension = dimension;
				player.playerNetServerHandler.sendPacket(new S07PacketRespawn(player.dimension, player.worldObj.difficultySetting, newworld.getWorldInfo().getTerrainType(), player.theItemInWorldManager.getGameType()));
				((WorldServer)origin).getPlayerManager().removePlayer(player);
			}
		}

		if (changingworlds)
		{
			removeEntityFromWorld(origin, entity);
		}

		entity.setLocationAndAngles((double)spawn.posX + 0.5D, (double)spawn.posY, (double)spawn.posZ + 0.5D, yaw, entity.rotationPitch);
		((WorldServer)newworld).theChunkProviderServer.loadChunk(spawn.posX >> 4, spawn.posZ >> 4);

		while (getCollidingWorldGeometry(newworld, entity.boundingBox, entity).size() != 0)
		{
			++spawn.posY;
			entity.setPosition((double)spawn.posX + 0.5D, (double)spawn.posY, (double)spawn.posZ + 0.5D);
		}

		if (changingworlds)
		{
			if (!(entity instanceof EntityPlayer))
			{
				NBTTagCompound entityNBT = new NBTTagCompound();
				entity.isDead = false;
				String entitystr = EntityList.getEntityString(entity);
				if (entitystr == null)
				{
					Lumberjack.warn("Failed to save entity when linking");
					return null;
				}

				entityNBT.setString("id", entitystr);
				entity.writeToNBT(entityNBT);
				entity.isDead = true;
				entity = EntityList.createEntityFromNBT(entityNBT, newworld);
				if (entity == null)
				{
					Lumberjack.warn("Failed to reconstruct entity when linking");
					return null;
				}

				entity.dimension = newworld.provider.dimensionId;
			}

			newworld.spawnEntityInWorld(entity);
			entity.setWorld(newworld);
		}

		entity.setLocationAndAngles((double)spawn.posX + 0.5D, (double)spawn.posY, (double)spawn.posZ + 0.5D, yaw, entity.rotationPitch);
		newworld.updateEntityWithOptionalForce(entity, false);
		entity.setLocationAndAngles((double)spawn.posX + 0.5D, (double)spawn.posY, (double)spawn.posZ + 0.5D, yaw, entity.rotationPitch);
		if (entity instanceof EntityPlayerMP)
		{
			player = (EntityPlayerMP)entity;
			if (changingworlds)
			{
				player.mcServer.getConfigurationManager().func_72375_a(player, (WorldServer)newworld);
			}

			player.playerNetServerHandler.setPlayerLocation((double)spawn.posX + 0.5D, (double)spawn.posY, (double)spawn.posZ + 0.5D, player.rotationYaw, player.rotationPitch);
		}

		newworld.updateEntityWithOptionalForce(entity, false);
		if (entity instanceof EntityPlayerMP && changingworlds)
		{
			player = (EntityPlayerMP)entity;
			player.theItemInWorldManager.setWorld((WorldServer)newworld);
			player.mcServer.getConfigurationManager().updateTimeAndWeatherForPlayer(player, (WorldServer)newworld);
			player.mcServer.getConfigurationManager().syncPlayerInventory(player);
			Iterator iter = player.getActivePotionEffects().iterator();

			while (iter.hasNext())
			{
				PotionEffect effect = (PotionEffect)iter.next();
				player.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(player.getEntityId(), effect));
			}

			player.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(player.experience, player.experienceTotal, player.experienceLevel));
		}

		entity.setLocationAndAngles((double)spawn.posX + 0.5D, (double)spawn.posY, (double)spawn.posZ + 0.5D, yaw, entity.rotationPitch);
		if (mount != null)
		{
			if (entity instanceof EntityPlayerMP)
			{
				newworld.updateEntityWithOptionalForce(entity, true);
			}

			entity.mountEntity(mount);
		}

		return entity;
	}

	private static void removeEntityFromWorld(World world, Entity entity)
	{
		if (entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)entity;
			player.closeScreen();
			world.playerEntities.remove(player);
			world.updateAllPlayersSleepingFlag();
			int i = entity.chunkCoordX;
			int j = entity.chunkCoordZ;
			if (entity.addedToChunk && world.getChunkProvider().chunkExists(i, j))
			{
				world.getChunkFromChunkCoords(i, j).removeEntity(entity);
				world.getChunkFromChunkCoords(i, j).isModified = true;
			}

			world.loadedEntityList.remove(entity);
			world.onEntityRemoved(entity);
		}

	}

	private static List getCollidingWorldGeometry(World world, AxisAlignedBB axisalignedbb, Entity entity)
	{
		ArrayList collidingBoundingBoxes = new ArrayList();
		int i = MathHelper.floor_double(axisalignedbb.minX);
		int j = MathHelper.floor_double(axisalignedbb.maxX + 1.0D);
		int k = MathHelper.floor_double(axisalignedbb.minY);
		int l = MathHelper.floor_double(axisalignedbb.maxY + 1.0D);
		int i1 = MathHelper.floor_double(axisalignedbb.minZ);
		int j1 = MathHelper.floor_double(axisalignedbb.maxZ + 1.0D);

		for (int k1 = i; k1 < j; ++k1)
		{
			for (int l1 = i1; l1 < j1; ++l1)
			{
				if (world.blockExists(k1, 64, l1))
				{
					for (int i2 = k - 1; i2 < l; ++i2)
					{
						Block block = world.getBlock(k1, i2, l1);
						if (block != null)
						{
							block.addCollisionBoxesToList(world, k1, i2, l1, axisalignedbb, collidingBoundingBoxes, entity);
						}
					}
				}
			}
		}

		return collidingBoundingBoxes;
	}
}
