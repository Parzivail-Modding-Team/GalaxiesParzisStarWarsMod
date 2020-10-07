package com.parzivail.pswg.structure;

import com.google.common.io.LittleEndianDataInputStream;
import com.parzivail.pswg.util.Lumberjack;
import com.parzivail.pswg.util.PIO;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.ChunkPos;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.HashMap;

public class ScarifStructure
{
	private static final String MAGIC = "SCRF";
	private final LittleEndianDataInputStream stream;
	private final FileChannel file;
	private final HashMap<ChunkPos, Long> entries;

	public ScarifStructure(FileChannel file, LittleEndianDataInputStream stream, HashMap<ChunkPos, Long> entries)
	{
		this.file = file;
		this.stream = stream;
		this.entries = entries;
	}

	public static ScarifStructure read(Identifier filename)
	{
		try
		{
			FileChannel raf = PIO.getFile("data", filename);
			if (raf == null)
				throw new IOException("Could not load file");

			InputStream fs = Channels.newInputStream(raf);
			LittleEndianDataInputStream s = new LittleEndianDataInputStream(fs);

			byte[] identBytes = new byte[MAGIC.length()];
			int read = s.read(identBytes);
			String ident = new String(identBytes);
			if (!ident.equals(MAGIC) || read != identBytes.length)
				throw new IOException("Input file not SCARIF structure");

			int version = s.readInt();
			if (version != 2)
				throw new IOException("Input file not SCARIF v2 structure");

			int numChunks = s.readInt();

			HashMap<ChunkPos, Long> chunkHeaderEntries = new HashMap<>();

			for (int i = 0; i < numChunks; i++)
			{
				int x = s.readInt();
				int z = s.readInt();
				long offset = s.readLong();

				chunkHeaderEntries.put(new ChunkPos(x, z), offset);
			}

			return new ScarifStructure(raf, s, chunkHeaderEntries);
		}
		catch (IOException e)
		{
			CrashReport crashReport = CrashReport.create(e, String.format("Could not load structure: %s", filename.toString()));
			throw new CrashException(crashReport);
		}
	}

	public ScarifChunk openChunk(ChunkPos pos)
	{
		if (!entries.containsKey(pos))
			return null;

		try
		{
			file.position(entries.get(pos));
			return new ScarifChunk(stream);
		}
		catch (IOException e)
		{
			Lumberjack.error("SCARIF chunk open failed");
			e.printStackTrace();
		}

		return null;
	}
}
