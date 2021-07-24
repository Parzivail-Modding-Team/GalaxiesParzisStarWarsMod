package com.parzivail.util.scarif;

import com.google.common.io.LittleEndianDataInputStream;
import com.parzivail.util.Lumberjack;
import com.parzivail.util.binary.DataReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.ChunkPos;

import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.HashMap;

public record ScarifStructure(FileChannel file, LittleEndianDataInputStream stream, HashMap<ChunkPos, Long> entries)
{
	private static final String MAGIC = "SCRF";

	public static ScarifStructure read(Identifier filename)
	{
		try
		{
			var raf = DataReader.getFile("data", filename);
			if (raf == null)
				throw new IOException("Could not load file");

			// TODO: unpack these files to a neighbor directory of the remote asset dir, alongside
			//  a sha256 of the file, and check both at runtime to see if they need to be re-unpacked.
			//  Using the unpacked file will allow native seeking (it's a file, not a resource) and will
			//  prevent those temp files from being introduced
			var fs = Channels.newInputStream(raf);
			var s = new LittleEndianDataInputStream(fs);

			var identBytes = new byte[MAGIC.length()];
			var read = s.read(identBytes);
			var ident = new String(identBytes);
			if (!ident.equals(MAGIC) || read != identBytes.length)
				throw new IOException("Input file not SCARIF structure");

			var version = s.readInt();
			if (version != 2)
				throw new IOException("Input file not SCARIF v2 structure");

			var numChunks = s.readInt();

			var chunkHeaderEntries = new HashMap<ChunkPos, Long>();

			for (var i = 0; i < numChunks; i++)
			{
				var x = s.readInt();
				var z = s.readInt();
				var offset = s.readLong();

				chunkHeaderEntries.put(new ChunkPos(x, z), offset);
			}

			return new ScarifStructure(raf, s, chunkHeaderEntries);
		}
		catch (IOException e)
		{
			var crashReport = CrashReport.create(e, String.format("Could not load structure: %s", filename));
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
