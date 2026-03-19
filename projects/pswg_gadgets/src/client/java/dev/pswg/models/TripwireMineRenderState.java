package dev.pswg.models;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.phys.Vec3;

public class TripwireMineRenderState extends EntityRenderState
{
	public float pitch;
	public float yaw;
	public boolean primed;
	public float tripwireDistance;
	public Vec3 rotationVec;
}
