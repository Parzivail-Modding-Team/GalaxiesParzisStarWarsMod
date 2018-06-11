package com.parzivail.swg.handler;

import cpw.mods.fml.common.registry.GameData;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileHandler
{
	public static void saveNbtMappings(File file)
	{
		NBTTagCompound compound = new NBTTagCompound();
		NBTTagList blockMap = new NBTTagList();

		GameData.GameDataSnapshot gameDataSnapshot = GameData.buildItemDataList();

		for (String key : gameDataSnapshot.idMap.keySet())
		{
			NBTTagCompound c = new NBTTagCompound();
			c.setString("k", key.substring(1)); // substring because GameDataSnapshot adds a discriminator or something dumb
			c.setInteger("v", gameDataSnapshot.idMap.get(key));
			blockMap.appendTag(c);
		}
		compound.setTag("map", blockMap);

		try
		{
			OutputStream outputStream = new FileOutputStream(file);
			CompressedStreamTools.writeCompressed(compound, outputStream);
			outputStream.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
