package com.parzivail.aurek.debug;

import com.parzivail.pswg.api.IDebugBackend;
import com.parzivail.util.data.NamedBufferUtil;
import imgui.ImGui;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import org.joml.Vector3f;

import java.util.HashSet;

public class DebugBackend implements IDebugBackend
{
	private final HashSet<String> KEYS_IN_USE = new HashSet<>();

	public void startFrame()
	{
		KEYS_IN_USE.clear();
	}

	@Override
	public int getInt(String key, int init)
	{
		var b = NamedBufferUtil.getI(key, init);

		if (!KEYS_IN_USE.contains(key))
		{
			var i = new ImInt(b[0]);
			ImGui.inputInt(key, i);
			b[0] = i.get();
		}

		KEYS_IN_USE.add(key);

		return b[0];
	}

	@Override
	public float getFloat(String key, float init)
	{
		var b = NamedBufferUtil.getF(key, init);

		if (!KEYS_IN_USE.contains(key))
		{
			var i = new ImFloat(b[0]);
			ImGui.inputFloat(key, i);
			b[0] = i.get();
		}

		KEYS_IN_USE.add(key);

		return b[0];
	}

	@Override
	public Vector3f getVector(String key, Vector3f init)
	{
		var b = NamedBufferUtil.getF(key, init.x, init.y, init.z);

		if (!KEYS_IN_USE.contains(key))
			ImGui.inputFloat3(key, b);

		KEYS_IN_USE.add(key);

		return new Vector3f(b[0], b[1], b[2]);
	}
}
