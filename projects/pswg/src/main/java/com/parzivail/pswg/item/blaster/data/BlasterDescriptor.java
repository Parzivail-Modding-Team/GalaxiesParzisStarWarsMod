package com.parzivail.pswg.item.blaster.data;

import com.parzivail.util.data.PacketByteBufHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;

public class BlasterDescriptor
{
	public Identifier id;
	public Identifier sound;
	public BlasterArchetype type;
	public ArrayList<BlasterFiringMode> firingModes;
	public float damage;
	public float range;
	public float weight;
	public float boltColor;
	public int magazineSize;

	public short automaticRepeatTime;
	public short burstRepeatTime;

	// TODO: datapack/archetype? (requires re-calculating first person ADS positions)
	public float adsZoom = 5;

	public short burstSize;

	public BlasterAxialInfo recoil;
	public BlasterAxialInfo spread;
	public BlasterHeatInfo heat;
	public BlasterCoolingBypassProfile cooling;

	public int attachmentDefault;
	public int attachmentMinimum;
	public HashMap<Integer, BlasterAttachmentDescriptor> attachmentMap;

	public BlasterDescriptor(Identifier id, Identifier sound, BlasterArchetype type, ArrayList<BlasterFiringMode> firingModes, float damage, float range, float weight, float boltColor, int magazineSize, short automaticRepeatTime, short burstRepeatTime, short burstSize, BlasterAxialInfo recoil, BlasterAxialInfo spread, BlasterHeatInfo heat, BlasterCoolingBypassProfile cooling, int attachmentDefault, int attachmentMinimum, HashMap<Integer, BlasterAttachmentDescriptor> attachmentMap)
	{
		this.id = id;
		this.sound = sound;
		this.type = type;
		this.firingModes = firingModes;
		this.damage = damage;
		this.range = range;
		this.weight = weight;
		this.boltColor = boltColor;
		this.magazineSize = magazineSize;
		this.automaticRepeatTime = automaticRepeatTime;
		this.burstRepeatTime = burstRepeatTime;
		this.burstSize = burstSize;
		this.recoil = recoil;
		this.spread = spread;
		this.heat = heat;
		this.cooling = cooling;
		this.attachmentDefault = attachmentDefault;
		this.attachmentMinimum = attachmentMinimum;
		this.attachmentMap = attachmentMap;
	}

	public static BlasterDescriptor read(PacketByteBuf buf, Identifier key)
	{
		var sound = PacketByteBufHelper.readIdentifier(buf);

		var archetype = BlasterArchetype.ID_LOOKUP.get(buf.readByte());
		var firingModes = BlasterFiringMode.unpack(buf.readShort());

		var damage = buf.readFloat();
		var range = buf.readFloat();
		var weight = buf.readFloat();
		var boltColor = buf.readFloat();
		var magazineSize = buf.readInt();
		var automaticRepeatTime = buf.readShort();
		var burstRepeatTime = buf.readShort();

		var burstSize = buf.readShort();

		var recoil = BlasterAxialInfo.read(buf);
		var spread = BlasterAxialInfo.read(buf);

		var heat = BlasterHeatInfo.read(buf);

		var cooling = BlasterCoolingBypassProfile.read(buf);

		var attachmentDefault = buf.readInt();
		var attachmentMinimum = buf.readInt();

		var attachmentMap = PacketByteBufHelper.readHashMap(buf, PacketByteBuf::readInt, BlasterAttachmentDescriptor::read);

		return new BlasterDescriptor(key, sound, archetype, firingModes, damage, range, weight, boltColor, magazineSize, automaticRepeatTime, burstRepeatTime, burstSize, recoil, spread, heat, cooling, attachmentDefault, attachmentMinimum, attachmentMap);
	}

	public void write(PacketByteBuf buf)
	{
		PacketByteBufHelper.writeIdentifier(buf, sound);

		buf.writeByte(type.getId());
		buf.writeShort(BlasterFiringMode.pack(firingModes));

		buf.writeFloat(damage);
		buf.writeFloat(range);
		buf.writeFloat(weight);
		buf.writeFloat(boltColor);
		buf.writeInt(magazineSize);
		buf.writeShort(automaticRepeatTime);
		buf.writeShort(burstRepeatTime);

		buf.writeShort(burstSize);

		recoil.write(buf);
		spread.write(buf);

		heat.write(buf);

		cooling.write(buf);

		buf.writeInt(attachmentDefault);
		buf.writeInt(attachmentMinimum);

		if (attachmentMap == null)
			attachmentMap = new HashMap<>();

		PacketByteBufHelper.writeHashMap(buf, attachmentMap, PacketByteBuf::writeInt, (b, v) -> v.write(b));
	}
}
