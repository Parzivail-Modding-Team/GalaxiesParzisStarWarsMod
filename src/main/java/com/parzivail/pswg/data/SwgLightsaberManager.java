package com.parzivail.pswg.data;

import com.google.gson.JsonElement;
import com.parzivail.pswg.Client;
import com.parzivail.pswg.Galaxies;
import com.parzivail.pswg.item.lightsaber.data.LightsaberDescriptor;
import com.parzivail.util.data.TypedDataLoader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SwgLightsaberManager extends TypedDataLoader<LightsaberDescriptor>
{
	public SwgLightsaberManager()
	{
		super("items/lightsabers");
	}

	public static SwgLightsaberManager get(MinecraftServer server)
	{
		return Galaxies.ResourceManagers.get(server).getLightsaberManager();
	}

	public static SwgLightsaberManager get(World world)
	{
		if (world.isClient)
			return Client.ResourceManagers.getLightsaberManager();

		return SwgLightsaberManager.get(world.getServer());
	}

	protected void writeDataEntry(PacketByteBuf buf, LightsaberDescriptor value)
	{
		buf.writeString(value.owner);
		buf.writeString(value.hilt);
		buf.writeFloat(value.bladeHue);
	}

	protected LightsaberDescriptor readDataEntry(Identifier key, PacketByteBuf buf)
	{
		var owner = buf.readString();
		var hilt = buf.readString();
		var bladeHue = buf.readFloat();

		return new LightsaberDescriptor(key, owner, hilt, bladeHue);
	}

	protected LightsaberDescriptor deserializeDataEntry(Identifier identifier, JsonElement jsonObject)
	{
		var version = jsonObject.getAsJsonObject().get("version").getAsInt();

		if (version != 1)
			throw new IllegalArgumentException("Can only parse version 1 blaster descriptors!");

		return new LightsaberDescriptor(identifier, GSON.fromJson(jsonObject, LightsaberDescriptor.class));
	}
}
