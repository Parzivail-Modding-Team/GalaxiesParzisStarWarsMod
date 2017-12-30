package com.parzivail.util.network;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.util.math.RotatedAxes;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.awt.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class PMessage<REQ extends PMessage> implements Serializable, IMessage, IMessageHandler<REQ, IMessage>
{
	private static final HashMap<Class, Pair<Reader, Writer>> handlers = new HashMap<>();
	private static final HashMap<Class, Field[]> fieldCache = new HashMap<>();

	static
	{
		map(byte.class, PMessage::readByte, PMessage::writeByte);
		map(short.class, PMessage::readShort, PMessage::writeShort);
		map(int.class, PMessage::readInt, PMessage::writeInt);
		map(long.class, PMessage::readLong, PMessage::writeLong);
		map(float.class, PMessage::readFloat, PMessage::writeFloat);
		map(double.class, PMessage::readDouble, PMessage::writeDouble);
		map(boolean.class, PMessage::readBoolean, PMessage::writeBoolean);
		map(char.class, PMessage::readChar, PMessage::writeChar);
		map(String.class, PMessage::readString, PMessage::writeString);
		map(NBTTagCompound.class, PMessage::readNBT, PMessage::writeNBT);
		map(ItemStack.class, PMessage::readItemStack, PMessage::writeItemStack);

		map(EntityPlayer.class, PMessage::readPlayer, PMessage::writePlayer);
		map(Entity.class, PMessage::readEntity, PMessage::writeEntity);
		map(Vec3.class, PMessage::readVec3, PMessage::writeVec3);
		map(Vector3f.class, PMessage::readVec3f, PMessage::writeVec3f);
		//map(EntityCooldownEntry.class, PMessage::readEntityCooldownEntry, PMessage::writeEntityCooldownEntry);
		map(Color.class, PMessage::readColor, PMessage::writeColor);
		map(World.class, PMessage::readWorld, PMessage::writeWorld);
		map(ItemStack[].class, PMessage::readItemStacks, PMessage::writeItemStacks);
		//map(EntitySeat[].class, PMessage::readSeats, PMessage::writeSeats);
		//map(ShipData.class, PMessage::readShipData, PMessage::writeShipData);
		map(RotatedAxes.class, PMessage::readRAxes, PMessage::writeRAxes);
	}

	private static boolean acceptField(Field f, Class<?> type)
	{
		int mods = f.getModifiers();
		return !(Modifier.isFinal(mods) || Modifier.isStatic(mods) || Modifier.isTransient(mods)) && handlers.containsKey(type);
	}

	private static Field[] getClassFields(Class<?> clazz)
	{
		if (fieldCache.containsValue(clazz))
			return fieldCache.get(clazz);
		else
		{
			Field[] fields = clazz.getFields();
			Arrays.sort(fields, Comparator.comparing(Field::getName));
			fieldCache.put(clazz, fields);
			return fields;
		}
	}

	private static Pair<Reader, Writer> getHandler(Class<?> clazz)
	{
		Pair<Reader, Writer> pair = handlers.get(clazz);
		if (pair == null)
			throw new RuntimeException("No R/W handler for  " + clazz);
		return pair;
	}

	private static <T extends Object> void map(Class<T> type, Reader<T> reader, Writer<T> writer)
	{
		handlers.put(type, Pair.of(reader, writer));
	}

	private static boolean readBoolean(ByteBuf buf)
	{
		return buf.readBoolean();
	}

	private static byte readByte(ByteBuf buf)
	{
		return buf.readByte();
	}

	private static char readChar(ByteBuf buf)
	{
		return buf.readChar();
	}

	private static Color readColor(ByteBuf buf)
	{
		return new Color(buf.readInt());
	}

	private static double readDouble(ByteBuf buf)
	{
		return buf.readDouble();
	}

	//	private static ShipData readShipData(ByteBuf buf)
	//	{
	//		ShipData data = new ShipData();
	//		data.numPassengers = readInt(buf);
	//		//Brute keep data.seatInfo
	//
	//		data.cameraDistance = readFloat(buf);
	//		data.cameraDistanceMax = readFloat(buf);
	//		data.cameraFloatDampening = readFloat(buf);
	//
	//		data.angularDragCoefficient = readFloat(buf);
	//		data.maxThrottle = readFloat(buf);
	//		data.throttleStep = readFloat(buf);
	//
	//		data.energyWeaponData = readEnergyWeaponData(buf);
	//		data.physicalWeaponData = readPhysicalWeaponData(buf);
	//
	//		data.cloakingActive = readBoolean(buf);
	//
	//		data.shipHealth = readFloat(buf);
	//		data.shieldHealth = readFloat(buf);
	//		data.shipHealthMax = readFloat(buf);
	//		data.shieldHealthMax = readFloat(buf);
	//
	//		data.energyTotalPercentage = readFloat(buf);
	//		data.energyPercentDrainPerMinute = readFloat(buf);
	//		return data;
	//	}

	//	private static PhysicalWeaponData[] readPhysicalWeaponData(ByteBuf buf)
	//	{
	//		int amount = readInt(buf);
	//		PhysicalWeaponData[] data = new PhysicalWeaponData[amount];
	//		for (int i = 0; i < amount; i++)
	//		{
	//			data[i] = new PhysicalWeaponData();
	//			data[i].amountRemaining = readInt(buf);
	//		}
	//		return data;
	//	}

	//	private static EnergyWeaponData[] readEnergyWeaponData(ByteBuf buf)
	//	{
	//		int amount = readInt(buf);
	//		EnergyWeaponData[] data = new EnergyWeaponData[amount];
	//		for (int i = 0; i < amount; i++)
	//		{
	//			data[i] = new EnergyWeaponData();
	//			data[i].enabled = readBoolean(buf);
	//			data[i].overheatTimer = readFloat(buf);
	//		}
	//		return data;
	//	}

	//	public static void writeShipData(ShipData data, ByteBuf buf)
	//	{
	//		writeInt(data.numPassengers, buf);
	//		//Brute keep data.seatInfo
	//
	//		writeFloat(data.cameraDistance, buf);
	//		writeFloat(data.cameraDistanceMax, buf);
	//		writeFloat(data.cameraFloatDampening, buf);
	//
	//		writeFloat(data.angularDragCoefficient, buf);
	//		writeFloat(data.maxThrottle, buf);
	//		writeFloat(data.throttleStep, buf);
	//
	//		writeEnergyWeaponData(data.energyWeaponData, buf);
	//		writePhysicalWeaponData(data.physicalWeaponData, buf);
	//
	//		writeBoolean(data.cloakingActive, buf);
	//
	//		writeFloat(data.shipHealth, buf);
	//		writeFloat(data.shieldHealth, buf);
	//		writeFloat(data.shipHealthMax, buf);
	//		writeFloat(data.shieldHealthMax, buf);
	//
	//		writeFloat(data.energyTotalPercentage, buf);
	//		writeFloat(data.energyPercentDrainPerMinute, buf);
	//	}

	//	private static void writePhysicalWeaponData(PhysicalWeaponData[] data, ByteBuf buf)
	//	{
	//		writeInt(data.length, buf);
	//		for (PhysicalWeaponData aData : data)
	//		{
	//			writeInt(aData.amountRemaining, buf);
	//		}
	//	}

	//	private static void writeEnergyWeaponData(EnergyWeaponData[] data, ByteBuf buf)
	//	{
	//		writeInt(data.length, buf);
	//		for (EnergyWeaponData aData : data)
	//		{
	//			writeBoolean(aData.enabled, buf);
	//			writeFloat(aData.overheatTimer, buf);
	//		}
	//	}

	private static Entity readEntity(ByteBuf buf)
	{
		int dim = buf.readInt();
		int id = buf.readInt();
		if (!StarWarsGalaxy.proxy.isServer())
			return Minecraft.getMinecraft().theWorld.getEntityByID(id);
		return MinecraftServer.getServer().worldServerForDimension(dim).getEntityByID(id);
	}

	//	private static EntitySeat readEntitySeat(ByteBuf buf)
	//	{
	//		int dim = buf.readInt();
	//		int id = buf.readInt();
	//		EntitySeat seat;
	//		if (MinecraftServer.getServer() == null)
	//		{
	//			seat = (EntitySeat)Minecraft.getMinecraft().theWorld.getEntityByID(id);
	//		}
	//		else
	//		{
	//			seat = (EntitySeat)MinecraftServer.getServer().worldServerForDimension(dim).getEntityByID(id);
	//		}
	//		//seat.parentId = buf.readInt();
	//		//seat.seatID = buf.readInt();
	//		seat.parent = (Pilotable)readEntity(buf);
	//		//seat.driver = buf.readBoolean();
	//		seat.playerPosX = (int)buf.readDouble();
	//		seat.playerPosY = (int)buf.readDouble();
	//		seat.playerPosZ = (int)buf.readDouble();
	//		//seat.riderId = buf.readInt();
	//		return seat;
	//	}

	//	private static EntityCooldownEntry readEntityCooldownEntry(ByteBuf buf)
	//	{
	//		Entity e = readEntity(buf);
	//		String name = ByteBufUtils.readUTF8String(buf);
	//		int cooldownLeft = buf.readInt();
	//		return new EntityCooldownEntry(e, name, cooldownLeft);
	//	}

	private static float readFloat(ByteBuf buf)
	{
		return buf.readFloat();
	}

	private static RotatedAxes readRAxes(ByteBuf buf)
	{
		Matrix4f mat = new Matrix4f();

		mat.m00 = buf.readFloat();
		mat.m01 = buf.readFloat();
		mat.m02 = buf.readFloat();
		mat.m03 = buf.readFloat();
		mat.m10 = buf.readFloat();
		mat.m11 = buf.readFloat();
		mat.m12 = buf.readFloat();
		mat.m13 = buf.readFloat();
		mat.m20 = buf.readFloat();
		mat.m21 = buf.readFloat();
		mat.m22 = buf.readFloat();
		mat.m23 = buf.readFloat();
		mat.m30 = buf.readFloat();
		mat.m31 = buf.readFloat();
		mat.m32 = buf.readFloat();
		mat.m33 = buf.readFloat();

		return new RotatedAxes(mat);
	}

	private static int readInt(ByteBuf buf)
	{
		return buf.readInt();
	}

	private static ItemStack readItemStack(ByteBuf buf)
	{
		return ByteBufUtils.readItemStack(buf);
	}

	private static ItemStack[] readItemStacks(ByteBuf buf)
	{
		ArrayList<ItemStack> stacks = new ArrayList<>();
		int count = readInt(buf);
		for (int i = 0; i < count; i++)
			stacks.add(readItemStack(buf));
		return stacks.toArray(new ItemStack[count]);
	}

	//	private static EntitySeat[] readSeats(ByteBuf buf)
	//	{
	//		ArrayList<EntitySeat> stacks = new ArrayList<>();
	//		int count = readInt(buf);
	//		for (int i = 0; i < count; i++)
	//			stacks.add(readEntitySeat(buf));
	//		return stacks.toArray(new EntitySeat[count]);
	//	}

	private static long readLong(ByteBuf buf)
	{
		return buf.readLong();
	}

	private static NBTTagCompound readNBT(ByteBuf buf)
	{
		return ByteBufUtils.readTag(buf);
	}

	private static EntityPlayer readPlayer(ByteBuf buf)
	{
		// int dim = buf.readInt();
		// String uname = ByteBufUtils.readUTF8String(buf);
		// return
		// MinecraftServer.getServer().worldServerForDimension(dim).getPlayerEntityByName(uname);
		return (EntityPlayer)readEntity(buf);
	}

	private static short readShort(ByteBuf buf)
	{
		return buf.readShort();
	}

	private static String readString(ByteBuf buf)
	{
		return ByteBufUtils.readUTF8String(buf);
	}

	private static Vec3 readVec3(ByteBuf buf)
	{
		double x = buf.readDouble();
		double y = buf.readDouble();
		double z = buf.readDouble();
		return Vec3.createVectorHelper(x, y, z);
	}

	private static Vector3f readVec3f(ByteBuf buf)
	{
		float x = buf.readFloat();
		float y = buf.readFloat();
		float z = buf.readFloat();
		return new Vector3f(x, y, z);
	}

	private static World readWorld(ByteBuf buf)
	{
		int dim = buf.readInt();
		return MinecraftServer.getServer().worldServerForDimension(dim);
	}

	private static void writeWorld(World w, ByteBuf buf)
	{
		buf.writeInt(w.provider.dimensionId);
	}

	private static void writeBoolean(boolean b, ByteBuf buf)
	{
		buf.writeBoolean(b);
	}

	private static void writeByte(byte b, ByteBuf buf)
	{
		buf.writeByte(b);
	}

	private static void writeChar(char c, ByteBuf buf)
	{
		buf.writeChar(c);
	}

	private static void writeColor(Color c, ByteBuf buf)
	{
		buf.writeInt(c.getRGB());
	}

	private static void writeDouble(double d, ByteBuf buf)
	{
		buf.writeDouble(d);
	}

	private static void writeRAxes(RotatedAxes axes, ByteBuf buf)
	{
		Matrix4f mat = axes.getMatrix();
		buf.writeFloat(mat.m00);
		buf.writeFloat(mat.m01);
		buf.writeFloat(mat.m02);
		buf.writeFloat(mat.m03);
		buf.writeFloat(mat.m10);
		buf.writeFloat(mat.m11);
		buf.writeFloat(mat.m12);
		buf.writeFloat(mat.m13);
		buf.writeFloat(mat.m20);
		buf.writeFloat(mat.m21);
		buf.writeFloat(mat.m22);
		buf.writeFloat(mat.m23);
		buf.writeFloat(mat.m30);
		buf.writeFloat(mat.m31);
		buf.writeFloat(mat.m32);
		buf.writeFloat(mat.m33);
	}

	private static void writeEntity(Entity entity, ByteBuf buf)
	{
		if (entity == null)
		{
			buf.writeInt(0);
			buf.writeInt(0);
			return;
		}
		buf.writeInt(entity.dimension);
		buf.writeInt(entity.getEntityId());
	}

	//	private static void writeEntitySeat(EntitySeat entity, ByteBuf buf)
	//	{
	//		buf.writeInt(entity.dimension);
	//		buf.writeInt(entity.getEntityId());
	//		//buf.writeInt(entity.parentId);
	//		//buf.writeInt(entity.seatID);
	//		writeEntity(entity.parent, buf);
	//		//buf.writeBoolean(entity.driver);
	//		buf.writeDouble(entity.playerPosX);
	//		buf.writeDouble(entity.playerPosY);
	//		buf.writeDouble(entity.playerPosZ);
	//		//buf.writeInt(entity.riderId);
	//	}

	//	private static void writeEntityCooldownEntry(EntityCooldownEntry entry, ByteBuf buf)
	//	{
	//		writeEntity(entry.entity, buf);
	//		ByteBufUtils.writeUTF8String(buf, entry.effect);
	//		buf.writeInt(entry.cooldownLeft);
	//	}

	private static void writeFloat(float f, ByteBuf buf)
	{
		buf.writeFloat(f);
	}

	private static void writeInt(int i, ByteBuf buf)
	{
		buf.writeInt(i);
	}

	private static void writeItemStack(ItemStack stack, ByteBuf buf)
	{
		ByteBufUtils.writeItemStack(buf, stack);
	}

	private static void writeItemStacks(ItemStack[] stack, ByteBuf buf)
	{
		writeInt(stack.length, buf);
		for (ItemStack stack1 : stack)
			ByteBufUtils.writeItemStack(buf, stack1);
	}

	//	private static void writeSeats(EntitySeat[] stack, ByteBuf buf)
	//	{
	//		writeInt(stack.length, buf);
	//		for (EntitySeat stack1 : stack)
	//			writeEntitySeat(stack1, buf);
	//	}

	private static void writeLong(long l, ByteBuf buf)
	{
		buf.writeLong(l);
	}

	private static void writeNBT(NBTTagCompound cmp, ByteBuf buf)
	{
		ByteBufUtils.writeTag(buf, cmp);
	}

	private static void writePlayer(EntityPlayer player, ByteBuf buf)
	{
		// buf.writeInt(ship.dimension);
		// ByteBufUtils.writeUTF8String(buf, ship.getCommandSenderName());
		writeEntity(player, buf);
	}

	private static void writeShort(short s, ByteBuf buf)
	{
		buf.writeShort(s);
	}

	private static void writeString(String s, ByteBuf buf)
	{
		ByteBufUtils.writeUTF8String(buf, s);
	}

	private static void writeVec3(Vec3 vec, ByteBuf buf)
	{
		buf.writeDouble(vec.xCoord);
		buf.writeDouble(vec.yCoord);
		buf.writeDouble(vec.zCoord);
	}

	private static void writeVec3f(Vector3f vec, ByteBuf buf)
	{
		buf.writeFloat(vec.x);
		buf.writeFloat(vec.y);
		buf.writeFloat(vec.z);
	}

	@Override
	public final void fromBytes(ByteBuf buf)
	{
		try
		{
			Class<?> clazz = this.getClass();
			Field[] clFields = getClassFields(clazz);
			for (Field f : clFields)
			{
				Class<?> type = f.getType();
				if (acceptField(f, type))
					this.readField(f, type, buf);
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException("Error at reading packet " + this, e);
		}
	}

	// The thing you override!
	public IMessage handleMessage(MessageContext context)
	{
		return null;
	}

	@Override
	public final IMessage onMessage(REQ message, MessageContext context)
	{
		return message.handleMessage(context);
	}

	private final void readField(Field f, Class clazz, ByteBuf buf) throws IllegalArgumentException, IllegalAccessException
	{
		Pair<Reader, Writer> handler = getHandler(clazz);
		f.set(this, handler.getLeft().read(buf));
	}

	@Override
	public final void toBytes(ByteBuf buf)
	{
		try
		{
			Class<?> clazz = this.getClass();
			Field[] clFields = getClassFields(clazz);
			for (Field f : clFields)
			{
				Class<?> type = f.getType();
				if (acceptField(f, type))
					this.writeField(f, type, buf);
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException("Error at writing packet " + this, e);
		}
	}

	private final void writeField(Field f, Class clazz, ByteBuf buf) throws IllegalArgumentException, IllegalAccessException
	{
		Pair<Reader, Writer> handler = getHandler(clazz);
		handler.getRight().write(f.get(this), buf);
	}

	public interface Reader<T extends Object>
	{
		T read(ByteBuf buf);
	}

	public interface Writer<T extends Object>
	{
		void write(T t, ByteBuf buf);
	}

}