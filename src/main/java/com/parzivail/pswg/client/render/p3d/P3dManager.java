package com.parzivail.pswg.client.render.p3d;

import com.parzivail.pswg.Resources;
import com.parzivail.util.data.KeyedReloadableLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class P3dManager extends KeyedReloadableLoader<P3dModel>
{
	public static final Identifier ID = Resources.id("pm3d_manager");
	public static final P3dManager INSTANCE = new P3dManager();

	private final Map<Identifier, P3dModel> modelData;

	private P3dManager()
	{
		super("models", "p3d");
		modelData = new HashMap<>();
	}

	@Override
	public P3dModel readResource(ResourceManager resourceManager, Profiler profiler, InputStream stream) throws IOException
	{
		// All client-read models should have vertex data
		return P3dModel.read(stream, true);
	}

	public P3dModel get(Identifier identifier)
	{
		return modelData.get(identifier);
	}

	@Override
	protected void apply(Map<Identifier, P3dModel> prepared, ResourceManager manager, Profiler profiler)
	{
		modelData.clear();
		modelData.putAll(prepared);
	}

	@Override
	public Identifier getFabricId()
	{
		return ID;
	}
}
