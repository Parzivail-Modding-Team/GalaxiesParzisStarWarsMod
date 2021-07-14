package com.parzivail.util.scarif;

import com.google.common.io.LittleEndianDataInputStream;
import com.parzivail.util.Lumberjack;
import com.parzivail.util.binary.DataReader;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.util.HashMap;

public class ScarifChunk
{
	public final HashMap<BlockPos, NbtCompound> tiles = new HashMap<>();
	private final LittleEndianDataInputStream stream;
	public int numSections;
	private boolean initialized = false;

	public ScarifChunk(LittleEndianDataInputStream stream)
	{
		this.stream = stream;
	}

	public void init()
	{
		if (initialized)
		{
			Lumberjack.error("SCARIF chunk cannot initialize twice");
			return;
		}

		try
		{
			var numTiles = DataReader.read7BitEncodedInt(stream);

			for (var i = 0; i < numTiles; i++)
			{
				var x = DataReader.read7BitEncodedInt(stream);
				var y = DataReader.read7BitEncodedInt(stream);
				var z = DataReader.read7BitEncodedInt(stream);

				var nbtLen = stream.readInt();
				var nbt = DataReader.readUncompressedNbt(stream, nbtLen);

				tiles.put(new BlockPos(x, y, z), nbt);
			}

			numSections = DataReader.read7BitEncodedInt(stream);

			initialized = true;
		}
		catch (IOException e)
		{
			Lumberjack.error("SCARIF chunk failed to load tiles");
			e.printStackTrace();
		}
	}

	public ScarifSection readSection()
	{
		try
		{
			int y = stream.readByte();

			var paletteSize = DataReader.read7BitEncodedInt(stream);
			var palette = new BlockState[paletteSize];

			for (var i = 0; i < paletteSize; i++)
				palette[i] = readBlockState();

			var blockStates = new int[4096];
			for (var i = 0; i < blockStates.length; i++)
			{
				blockStates[i] = DataReader.read7BitEncodedInt(stream);
			}

			return new ScarifSection(y, palette, blockStates);
		}
		catch (IOException e)
		{
			Lumberjack.error("SCARIF chunk failed to load section");
			e.printStackTrace();
		}

		return null;
	}

	public BlockState readBlockState() throws IOException
	{
		var name = DataReader.readNullTerminatedString(stream);
		var hasProperties = stream.readByte() == 1;

		var blockRegistry = Registry.BLOCK;
		var block = blockRegistry.get(new Identifier(name));
		var blockState = block.getDefaultState();

		if (hasProperties)
		{
			var stateManager = block.getStateManager();

			var tagLen = stream.readInt();
			var props = DataReader.readUncompressedNbt(stream, tagLen);

			for (var key : props.getKeys())
			{
				var property = stateManager.getProperty(key);
				if (property != null)
					blockState = withProperty(blockState, property, key, props, blockState.toString());
			}
		}

		return blockState;
	}

	private static <T extends Comparable<T>> BlockState withProperty(BlockState state, net.minecraft.state.property.Property<T> property, String key, NbtCompound propertiesTag, String context)
	{
		var optional = property.parse(propertiesTag.getString(key));
		if (optional.isPresent())
			return state.with(property, optional.get());
		else
		{
			Lumberjack.warn("Unable to read property: %s with value: %s for blockstate: %s", key, propertiesTag.getString(key), context);
			return state;
		}
	}
}
