package dev.pswg.models;

import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.util.math.Vec3d;

public class TripwireMineRenderState extends EntityRenderState
{
	public float pitch;
	public float yaw;
	public boolean primed;
	public float tripwireDistance;
	public Vec3d rotationVec;
}
