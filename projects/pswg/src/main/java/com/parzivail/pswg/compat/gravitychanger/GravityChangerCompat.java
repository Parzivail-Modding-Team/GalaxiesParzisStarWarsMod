package com.parzivail.pswg.compat.gravitychanger;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class GravityChangerCompat {
	public static Vec3d vecPlayerToWorld(Entity entity, Vec3d vec) {
		return IGravityChangerCompat.INSTANCE.vecPlayerToWorld(entity, vec);
	}
}

@SuppressWarnings("Convert2Lambda")
interface IGravityChangerCompat
{
	IGravityChangerCompat INSTANCE = getInstance();
	Vec3d vecPlayerToWorld(Entity entity, Vec3d vec);
	private static IGravityChangerCompat getInstance() {
		IGravityChangerCompat instance;
		try {
			Class.forName("me.andrew.gravitychanger.util.RotationUtil", false, IGravityChangerCompat.class.getClassLoader());
			instance = new IGravityChangerCompat()
			{
				@Override
				public Vec3d vecPlayerToWorld(Entity entity, Vec3d vec)
				{
					return RotationUtil.vecPlayerToWorld(vec, GravityChangerAPI.getGravityDirection(entity));
				}
			};
		} catch (LinkageError | ClassNotFoundException e) {
			instance = new IGravityChangerCompat()
			{
				@Override
				public Vec3d vecPlayerToWorld(Entity entity, Vec3d vec)
				{
					return vec;
				}
			};
		}
		return instance;
	}
}
