package com.parzivail.pswg.client.pr3r;

import com.google.common.io.LittleEndianDataInputStream;
import com.parzivail.pswg.util.PIO;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.Matrix4f;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class PR3RFile
{
	private static final String MAGIC = "PR3R";
	private static final int ACCEPTED_VERSION = 0x01;

	public final HashMap<String, Matrix4f> objects;

	public PR3RFile(HashMap<String, Matrix4f> objects)
	{
		this.objects = objects;
	}

	public static PR3RFile tryLoad(Identifier modelFile)
	{
		try
		{
			return load(modelFile);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			CrashReport crashReport = CrashReport.create(ex, String.format("Loading PR3R file: %s", modelFile));
			throw new CrashException(crashReport);
		}
	}

	private static PR3RFile load(Identifier modelFile) throws IOException
	{
		InputStream reader = PIO.getStream("data", modelFile);
		LittleEndianDataInputStream objStream = new LittleEndianDataInputStream(reader);

		byte[] identBytes = new byte[MAGIC.length()];
		int read = objStream.read(identBytes);
		String ident = new String(identBytes);
		if (!ident.equals(MAGIC) || read != identBytes.length)
			throw new IOException("Input file not PR3R rig");

		int version = objStream.readInt();

		if (version != ACCEPTED_VERSION)
			throw new IOException(String.format("Input file version is 0x%s, expected 0x%s", Integer.toHexString(version), Integer.toHexString(ACCEPTED_VERSION)));

		int numObjects = objStream.readInt();

		HashMap<String, Matrix4f> objects = new HashMap<>();
		for (int i = 0; i < numObjects; i++)
		{
			String name = PIO.readNullTerminatedString(objStream);
			Matrix4f transformation = PIO.readMatrix4f(objStream);

			objects.put(name, transformation);
		}

		return new PR3RFile(objects);
	}
}
