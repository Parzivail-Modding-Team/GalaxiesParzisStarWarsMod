package com.parzivail.pswg.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public interface IDebugBackend
{
	int getInt(String key, int init);

	float getFloat(String key, float init);

	Vector3f getVector(String key, Vector3f init);
}
