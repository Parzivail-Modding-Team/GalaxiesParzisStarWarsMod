package com.parzivail.pswg.structure;

import com.google.common.io.LittleEndianDataInputStream;
import com.parzivail.pswg.util.Lumberjack;
import com.parzivail.pswg.util.PIO;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

public class ScarifStructure
{
	private static final String MAGIC = "SCRF";
	private final LittleEndianDataInputStream stream;
	private final RandomAccessFile file;
	private final HashMap<ChunkPos, Long> entries;

	public ScarifStructure(RandomAccessFile file, LittleEndianDataInputStream stream, HashMap<ChunkPos, Long> entries)
	{
		this.file = file;
		this.stream = stream;
		this.entries = entries;
	}

	public static ScarifStructure read(Identifier filename)
	{
		try
		{
			return readUnsafe(filename);
		}
		catch (IOException e)
		{
			Lumberjack.error("Could not load structure: %s", filename.toString());
			e.printStackTrace();
		}

		return null;
	}

	public static ScarifStructure readUnsafe(Identifier filename) throws IOException
	{
		RandomAccessFile raf = PIO.getFile("data", filename);
		if (raf == null)
			throw new IOException("Could not load file");

		InputStream fs = Channels.newInputStream(raf.getChannel());
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

	public ScarifChunk openChunk(ChunkPos pos)
	{
		try
		{
			file.seek(entries.get(pos));
			return new ScarifChunk(new GZIPInputStream(stream));
		}
		catch (IOException e)
		{
			Lumberjack.error("SCARIF chunk open failed");
			e.printStackTrace();
		}

		return null;
	}
}
