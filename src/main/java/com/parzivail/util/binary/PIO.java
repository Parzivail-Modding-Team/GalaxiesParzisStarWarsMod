package com.parzivail.util.binary;

import com.google.common.io.LittleEndianDataInputStream;

/**
 * Created by colby on 12/25/2017.
 */
public class PIO
{
	public static String readNullTerminatedString(LittleEndianDataInputStream s)
	{
		StringBuilder str = new StringBuilder();
		try
		{
			while (true)
			{
				byte b = s.readByte();
				if (b == 0)
					return str.toString();
				str.append((char)b);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
