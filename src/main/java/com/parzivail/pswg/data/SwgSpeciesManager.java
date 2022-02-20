package com.parzivail.pswg.data;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.character.SpeciesDescriptor;
import com.parzivail.util.data.TypedDataLoader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;

import java.util.stream.Collectors;

public class SwgSpeciesManager extends TypedDataLoader<SpeciesDescriptor>
{
	public static final Identifier ID = Resources.id("species_manager");
	public static final SwgSpeciesManager INSTANCE = new SwgSpeciesManager();

	private SwgSpeciesManager()
	{
		super(
				new GsonBuilder().create(),
				"species"
		);
	}

	@Override
	public Identifier getFabricId()
	{
		return ID;
	}

	public SpeciesDescriptor getDataAndAssert(Identifier key)
	{
		var data = super.getData(key);

		if (data != null)
			return data;

		var j = CrashReport.create(new NullPointerException("Cannot get species descriptor for unknown key " + key.toString()), "Getting species descriptor");

		var k = j.addElement("Species Manager Data");
		k.add("Defined keys", this::getDataString);

		throw new CrashException(j);
	}

	@Override
	protected void onServerDataLoaded()
	{
	}

	@Override
	protected void onClientDataLoaded()
	{
	}

	private String getDataString()
	{
		var data = getData();
		if (data == null)
			return "null";
		return data.keySet().stream().map(Identifier::toString).collect(Collectors.joining(", "));
	}

	protected void writeDataEntry(PacketByteBuf buf, SpeciesDescriptor value)
	{
		value.write(buf);
	}

	protected SpeciesDescriptor readDataEntry(Identifier key, PacketByteBuf buf)
	{
		return SpeciesDescriptor.read(buf, key);
	}

	protected SpeciesDescriptor deserializeDataEntry(Identifier identifier, JsonElement jsonObject)
	{
		var version = jsonObject.getAsJsonObject().get("version").getAsInt();

		if (version != 1)
			throw new IllegalArgumentException("Can only parse version 1 species descriptors!");

		var value = GSON.fromJson(jsonObject, SpeciesDescriptor.class);
		value.id = identifier;
		return value;
	}
}
