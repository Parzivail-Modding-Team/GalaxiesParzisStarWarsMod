package com.parzivail.pswg.rig.pr3r;

import com.google.common.io.LittleEndianDataInputStream;
import com.parzivail.pswg.util.PIO;
import com.parzivail.util.data.DataReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import org.joml.Matrix4f;

import java.io.IOException;
import java.util.HashMap;

public record PR3RFile(HashMap<String, Matrix4f> objects)
{
	private static final String MAGIC = "PR3R";
	private static final int ACCEPTED_VERSION = 0x01;

	public static PR3RFile tryLoad(Identifier modelFile)
	{
		try
		{
			return load(modelFile);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			var crashReport = CrashReport.create(ex, String.format("Loading PR3R file: %s", modelFile));
			throw new CrashException(crashReport);
		}
	}

	private static PR3RFile load(Identifier modelFile) throws IOException
	{
		var reader = PIO.getStream("data", modelFile);
		var objStream = new LittleEndianDataInputStream(reader);

		var identBytes = new byte[MAGIC.length()];
		var read = objStream.read(identBytes);
		var ident = new String(identBytes);
		if (!ident.equals(MAGIC) || read != identBytes.length)
			throw new IOException("Input file not PR3R rig");

		var version = objStream.readInt();

		if (version != ACCEPTED_VERSION)
			throw new IOException(String.format("Input file version is 0x%s, expected 0x%s", Integer.toHexString(version), Integer.toHexString(ACCEPTED_VERSION)));

		var numObjects = objStream.readInt();

		var objects = new HashMap<String, Matrix4f>();
		for (var i = 0; i < numObjects; i++)
		{
			var name = DataReader.readNullTerminatedString(objStream);
			var transformation = DataReader.readMatrix4f(objStream);

			objects.put(name, transformation);
		}

		return new PR3RFile(objects);
	}
}
