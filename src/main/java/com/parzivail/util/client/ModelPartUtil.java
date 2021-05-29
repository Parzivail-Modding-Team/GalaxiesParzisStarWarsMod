package com.parzivail.util.client;

import net.minecraft.client.model.ModelPart;

public class ModelPartUtil
{
	public static void setRotateAngle(ModelPart part, float pitch, float yaw, float roll)
	{
		part.pitch = pitch;
		part.yaw = yaw;
		part.roll = roll;
	}
}
