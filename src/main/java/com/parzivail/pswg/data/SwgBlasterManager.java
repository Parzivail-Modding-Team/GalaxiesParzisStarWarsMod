package com.parzivail.pswg.data;

import com.google.gson.JsonElement;
import com.parzivail.pswg.Client;
import com.parzivail.pswg.Galaxies;
import com.parzivail.pswg.item.blaster.data.BlasterCoolingBypassProfile;
import com.parzivail.pswg.item.blaster.data.BlasterDescriptor;
import com.parzivail.pswg.item.blaster.data.BlasterHeatInfo;
import com.parzivail.pswg.item.blaster.data.BlasterSpreadInfo;
import com.parzivail.util.data.TypedDataLoader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SwgBlasterManager extends TypedDataLoader<BlasterDescriptor>
{
	public SwgBlasterManager()
	{
		super("items/blasters");
	}

	public static SwgBlasterManager get(MinecraftServer server)
	{
		return Galaxies.ResourceManagers.get(server).getBlasterManager();
	}

	public static SwgBlasterManager get(World world)
	{
		if (world.isClient)
			return Client.ResourceManagers.getBlasterManager();

		return SwgBlasterManager.get(world.getServer());
	}

	protected void writeDataEntry(PacketByteBuf buf, BlasterDescriptor value)
	{
		buf.writeBoolean(value.oneHanded);
		buf.writeFloat(value.damage);
		buf.writeFloat(value.range);
		buf.writeFloat(value.weight);
		buf.writeInt(value.boltColor);
		buf.writeInt(value.magazineSize);
		buf.writeInt(value.automaticRepeatTime);

		buf.writeFloat(value.spread.horizontal);
		buf.writeFloat(value.spread.vertical);

		buf.writeInt(value.heat.capacity);
		buf.writeInt(value.heat.perRound);
		buf.writeInt(value.heat.drainSpeed);
		buf.writeInt(value.heat.overheatPenalty);
		buf.writeInt(value.heat.overheatDrainSpeed);
		buf.writeInt(value.heat.passiveCooldownDelay);

		buf.writeFloat(value.cooling.primaryBypassTime);
		buf.writeFloat(value.cooling.primaryBypassTolerance);
		buf.writeFloat(value.cooling.secondaryBypassTime);
		buf.writeFloat(value.cooling.secondaryBypassTolerance);
	}

	protected BlasterDescriptor readDataEntry(Identifier key, PacketByteBuf buf)
	{
		var oneHanded = buf.readBoolean();
		var damage = buf.readFloat();
		var range = buf.readFloat();
		var weight = buf.readFloat();
		var boltColor = buf.readInt();
		var magazineSize = buf.readInt();
		var automaticRepeatTime = buf.readInt();

		var spread_horizontal = buf.readFloat();
		var spread_vertical = buf.readFloat();

		var heat_capacity = buf.readInt();
		var heat_perRound = buf.readInt();
		var heat_drainSpeed = buf.readInt();
		var heat_cooldownDelay = buf.readInt();
		var heat_overheatDrainSpeed = buf.readInt();
		var heat_passiveCooldownDelay = buf.readInt();

		var cooling_primaryBypassTime = buf.readFloat();
		var cooling_primaryBypassTolerance = buf.readFloat();
		var cooling_secondaryBypassTime = buf.readFloat();
		var cooling_secondaryBypassTolerance = buf.readFloat();

		return new BlasterDescriptor(
				key,
				oneHanded,
				damage,
				range,
				weight,
				boltColor,
				magazineSize,
				automaticRepeatTime,
				new BlasterSpreadInfo(spread_horizontal, spread_vertical),
				new BlasterHeatInfo(heat_capacity, heat_perRound, heat_drainSpeed, heat_cooldownDelay, heat_overheatDrainSpeed, heat_passiveCooldownDelay),
				new BlasterCoolingBypassProfile(cooling_primaryBypassTime, cooling_primaryBypassTolerance, cooling_secondaryBypassTime, cooling_secondaryBypassTolerance)
		);
	}

	protected BlasterDescriptor deserializeDataEntry(Identifier identifier, JsonElement jsonObject)
	{
		var version = jsonObject.getAsJsonObject().get("version").getAsInt();

		if (version != 1)
			throw new IllegalArgumentException("Can only parse version 1 blaster descriptors!");

		return new BlasterDescriptor(identifier, GSON.fromJson(jsonObject, BlasterDescriptor.class));
	}
}
