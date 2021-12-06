package com.parzivail.util.scarif;

import com.google.common.io.LittleEndianDataInputStream;
import com.parzivail.util.Lumberjack;
import com.parzivail.util.data.DataReader;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;
import java.util.HashMap;

public class ScarifChunk
{
	public final HashMap<BlockPos, NbtCompound> tiles = new HashMap<>();
	private final ScarifSection[] sections;

	public ScarifChunk(LittleEndianDataInputStream stream) throws IOException
	{
		var numTiles = DataReader.read7BitEncodedInt(stream);

		for (var tileIdx = 0; tileIdx < numTiles; tileIdx++)
		{
			var x = DataReader.read7BitEncodedInt(stream);
			var y = DataReader.read7BitEncodedInt(stream);
			var z = DataReader.read7BitEncodedInt(stream);

			var nbtLen = stream.readInt();
			var nbt = DataReader.readUncompressedNbt(stream, nbtLen);

			tiles.put(new BlockPos(x, y, z), nbt);
		}

		var numSections = DataReader.read7BitEncodedInt(stream);
		sections = new ScarifSection[numSections];

		for (var sectionIdx = 0; sectionIdx < numSections; sectionIdx++)
		{
			try
			{
				int y = stream.readByte();

				var paletteSize = DataReader.read7BitEncodedInt(stream);
				var palette = new BlockState[paletteSize];

				for (var paletteIdx = 0; paletteIdx < paletteSize; paletteIdx++)
					palette[paletteIdx] = DataReader.readBlockState(stream);

				var blockStates = new int[4096];
				for (var blockStateIdx = 0; blockStateIdx < blockStates.length; blockStateIdx++)
				{
					blockStates[blockStateIdx] = DataReader.read7BitEncodedInt(stream);
				}

				sections[sectionIdx] = new ScarifSection(y, palette, blockStates);
			}
			catch (IOException e)
			{
				Lumberjack.error("SCARIF chunk failed to load section " + sectionIdx);
				e.printStackTrace();
			}
		}
	}

	public ScarifSection[] getSections()
	{
		return sections;
	}
}
