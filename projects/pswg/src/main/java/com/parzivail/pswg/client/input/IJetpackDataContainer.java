package com.parzivail.pswg.client.input;

import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public interface IJetpackDataContainer
{
	void pswg_setJetpackControls(EnumSet<JetpackControls> controls);

	EnumSet<JetpackControls> pswg_getJetpackControls();

	void pswg_setJetpackForce(Vec3d force);

	Vec3d pswg_getJetpackForce();

	void pswg_tryCancelFallFlying();
}
