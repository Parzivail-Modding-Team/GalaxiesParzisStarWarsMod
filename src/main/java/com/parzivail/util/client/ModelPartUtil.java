package com.parzivail.util.client;

import net.minecraft.client.model.ModelPart;

public class ModelPartUtil
{
	public static void setRotateAngle(ModelPart part, float pitch, float roll, float yaw)
	{
		part.pitch = pitch;
		part.yaw = roll;
		part.roll = yaw;
	}
}
