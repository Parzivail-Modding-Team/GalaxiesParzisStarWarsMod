package com.parzivail.pswg.character;

import com.parzivail.util.data.PacketByteBufHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class SpeciesVariableDescriptor
{
	public Identifier definingSpecies;
	public String defaultValue;
	public ArrayList<String> possibleValues;

	public SpeciesVariableDescriptor(Identifier definingSpecies, String defaultValues, ArrayList<String> possibleValues)
	{
		this.definingSpecies = definingSpecies;
		this.defaultValue = defaultValues;
		this.possibleValues = possibleValues;
	}

	public static SpeciesVariableDescriptor read(PacketByteBuf buf)
	{
		var textureSrc = PacketByteBufHelper.readNullable(buf, PacketByteBufHelper::readIdentifier);
		var def = buf.readString();
		var values = PacketByteBufHelper.readMany(buf, PacketByteBuf::readString);

		return new SpeciesVariableDescriptor(textureSrc, def, values);
	}

	public void write(PacketByteBuf buf)
	{
		PacketByteBufHelper.writeNullable(buf, definingSpecies, PacketByteBufHelper::writeIdentifier);
		buf.writeString(defaultValue);
		PacketByteBufHelper.writeMany(buf, possibleValues, PacketByteBuf::writeString);
	}
}
