package com.parzivail.pswg.data;

import com.google.gson.JsonElement;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.item.lightsaber.data.LightsaberDescriptor;
import com.parzivail.util.data.TypedDataLoader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class SwgLightsaberManager extends TypedDataLoader<LightsaberDescriptor>
{
	public static final Identifier ID = Resources.id("lightsaber_manager");
	public static final SwgLightsaberManager INSTANCE = new SwgLightsaberManager();

	private SwgLightsaberManager()
	{
		super("items/lightsabers");
	}

	@Override
	public Identifier getFabricId()
	{
		return ID;
	}

	@Override
	protected void writeDataEntry(PacketByteBuf buf, LightsaberDescriptor value)
	{
		buf.writeString(value.owner);
		buf.writeString(value.hilt);
		buf.writeFloat(value.bladeHue);
		buf.writeFloat(value.bladeSaturation);
		buf.writeFloat(value.bladeValue);
	}

	@Override
	protected LightsaberDescriptor readDataEntry(Identifier key, PacketByteBuf buf)
	{
		var owner = buf.readString();
		var hilt = buf.readString();
		var bladeHue = buf.readFloat();
		var bladeSaturation = buf.readFloat();
		var bladeValue = buf.readFloat();

		return new LightsaberDescriptor(key, owner, hilt, bladeHue, bladeSaturation, bladeValue);
	}

	@Override
	protected LightsaberDescriptor deserializeDataEntry(Identifier identifier, JsonElement jsonObject)
	{
		var version = jsonObject.getAsJsonObject().get("version").getAsInt();

		if (version != 1)
			throw new IllegalArgumentException("Can only parse version 1 blaster descriptors!");

		var value = GSON.fromJson(jsonObject, LightsaberDescriptor.class);
		value.id = identifier;
		return value;
	}
}
