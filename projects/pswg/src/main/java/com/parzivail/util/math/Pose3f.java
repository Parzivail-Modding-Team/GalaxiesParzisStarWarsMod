package com.parzivail.util.math;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public record Pose3f(Vector3f pos, Quaternionf rotation)
{
	public Pose3f set(Pose3f other)
	{
		this.pos.set(other.pos().x, other.pos().y, other.pos().z);
		this.rotation.set(other.rotation());

		return this;
	}
}
