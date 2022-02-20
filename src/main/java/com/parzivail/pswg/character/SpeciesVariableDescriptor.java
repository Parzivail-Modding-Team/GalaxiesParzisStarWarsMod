package com.parzivail.pswg.character;

import com.parzivail.util.data.PacketByteBufHelper;
import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;

public class SpeciesVariableDescriptor
{
	public String defaultValue;
	public ArrayList<String> possibleValues;

	public SpeciesVariableDescriptor(String defaultValues, ArrayList<String> possibleValues)
	{
		this.defaultValue = defaultValues;
		this.possibleValues = possibleValues;
	}

	public static SpeciesVariableDescriptor read(PacketByteBuf buf)
	{
		var def = buf.readString();
		var values = PacketByteBufHelper.readMany(buf, PacketByteBuf::readString);

		return new SpeciesVariableDescriptor(def, values);
	}

	public void write(PacketByteBuf buf)
	{
		buf.writeString(defaultValue);
		PacketByteBufHelper.writeMany(buf, possibleValues, PacketByteBuf::writeString);
	}
}
