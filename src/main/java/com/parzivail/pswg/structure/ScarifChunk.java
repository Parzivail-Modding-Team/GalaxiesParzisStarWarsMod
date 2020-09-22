package com.parzivail.pswg.structure;

import com.google.common.io.LittleEndianDataInputStream;
import com.parzivail.pswg.util.Lumberjack;
import com.parzivail.pswg.util.PIO;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

public class ScarifChunk
{
	public final HashMap<BlockPos, CompoundTag> tiles = new HashMap<>();
	private final LittleEndianDataInputStream stream;
	public int numSections;
	private boolean initialized = false;

	public ScarifChunk(GZIPInputStream stream)
	{
		this.stream = new LittleEndianDataInputStream(stream);
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
			int numTiles = PIO.read7BitEncodedInt(stream);

			for (int i = 0; i < numTiles; i++)
			{
				int x = PIO.read7BitEncodedInt(stream);
				int y = PIO.read7BitEncodedInt(stream);
				int z = PIO.read7BitEncodedInt(stream);

				int nbtLen = stream.readInt();
				CompoundTag nbt = PIO.readUncompressedNbt(stream, nbtLen);

				tiles.put(new BlockPos(x, y, z), nbt);
			}

			numSections = PIO.read7BitEncodedInt(stream);

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
			return readSectionUnsafe();
		}
		catch (IOException e)
		{
			Lumberjack.error("SCARIF chunk failed to load section");
			e.printStackTrace();
		}

		return null;
	}

	private ScarifSection readSectionUnsafe() throws IOException
	{
		int y = stream.readByte();

		int paletteSize = PIO.read7BitEncodedInt(stream);
		BlockState[] palette = new BlockState[paletteSize];

		for (int i = 0; i < paletteSize; i++)
			palette[i] = readBlockState();

		int[] blockStates = new int[4096];
		for (int i = 0; i < blockStates.length; i++)
		{
			blockStates[i] = PIO.read7BitEncodedInt(stream);
		}

		return new ScarifSection(palette, blockStates);
	}

	public BlockState readBlockState() throws IOException
	{
		String name = PIO.readNullTerminatedString(stream);
		boolean hasProperties = stream.readByte() == 1;

		DefaultedRegistry<Block> blockRegistry = Registry.BLOCK;
		Block block = blockRegistry.get(new Identifier(name));
		BlockState blockState = block.getDefaultState();

		if (hasProperties)
		{
			StateManager<Block, BlockState> stateManager = block.getStateManager();

			int tagLen = stream.readInt();
			CompoundTag props = PIO.readUncompressedNbt(stream, tagLen);

			for (String key : props.getKeys())
			{
				Property<?> property = stateManager.getProperty(key);
				if (property != null)
					blockState = State.tryRead(blockState, property, key, props.toString(), props.getString(key));
			}
		}

		return blockState;
	}
}
