package com.parzivail.pswg.character;

import com.parzivail.util.data.PacketByteBufHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class SpeciesDescriptor
{
	public Identifier id;
	public HashMap<String, SpeciesVariableDescriptor> variables;

	public SpeciesDescriptor(Identifier id, HashMap<String, SpeciesVariableDescriptor> variables)
	{
		this.id = id;
		this.variables = variables;
	}

	public static SpeciesDescriptor read(PacketByteBuf buf, Identifier key)
	{
		var variables = PacketByteBufHelper.readHashMap(buf, PacketByteBuf::readString, SpeciesVariableDescriptor::read);

		return new SpeciesDescriptor(key, variables);
	}

	public void write(PacketByteBuf buf)
	{
		PacketByteBufHelper.writeHashMap(buf, variables, PacketByteBuf::writeString, (buf1, value) -> value.write(buf1));
	}
}

