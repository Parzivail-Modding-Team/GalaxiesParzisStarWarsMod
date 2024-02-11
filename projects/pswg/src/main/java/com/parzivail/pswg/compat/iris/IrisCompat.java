package com.parzivail.pswg.compat.iris;

import net.irisshaders.iris.api.v0.IrisApi;

public class IrisCompat
{
	public static boolean isShaderPackInUse()
	{
		return IIrisCompat.INSTANCE.isShaderPackInUse();
	}
}

@SuppressWarnings("Convert2Lambda")
interface IIrisCompat
{
	IIrisCompat INSTANCE = getInstance();

	boolean isShaderPackInUse();

	private static IIrisCompat getInstance()
	{
		IIrisCompat instance;
		try
		{
			Class.forName("net.irisshaders.iris.api.v0.IrisApi", false, IIrisCompat.class.getClassLoader());
			instance = new IIrisCompat()
			{
				@Override
				public boolean isShaderPackInUse()
				{
					return IrisApi.getInstance().isShaderPackInUse();
				}
			};
		}
		catch (LinkageError | ClassNotFoundException e)
		{
			instance = new IIrisCompat()
			{
				@Override
				public boolean isShaderPackInUse()
				{
					return false;
				}
			};
		}
		return instance;
	}
}
