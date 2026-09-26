package dev.pswg.utility.math;

import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadTransform;
import net.minecraft.core.Direction;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class QuadTransformUtil
{
	public static QuadTransform moveTowardCenter(Direction face, float amount)
	{
		return quad -> {
			for (int i = 0; i < 4; i++)
			{
				Vector3f pos = quad.copyPos(i, null);

				float x = pos.x;
				float y = pos.y;
				float z = pos.z;

				switch (face)
				{
					case NORTH -> z += amount;
					case SOUTH -> z -= amount;
					case EAST -> x -= amount;
					case WEST -> x += amount;
					case UP -> y -= amount;
					case DOWN -> y += amount;
				}

				quad.pos(i, x, y, z);
			}
			return true;
		};
	}

	public static QuadTransform flipFace()
	{
		return quad ->
		{
			Vector2f uv0 = quad.copyUv(0, null);
			Vector2f uv1 = quad.copyUv(1, null);
			Vector2f uv2 = quad.copyUv(2, null);
			Vector2f uv3 = quad.copyUv(3, null);

			quad.uv(0, uv1);
			quad.uv(1, uv0);
			quad.uv(2, uv3);
			quad.uv(3, uv2);

			return true;
		};
	}

	public static QuadTransform rotateFaceUv90()
	{
		return quad ->
		{
			Vector2f uv0 = quad.copyUv(0, null);
			Vector2f uv1 = quad.copyUv(1, null);
			Vector2f uv2 = quad.copyUv(2, null);
			Vector2f uv3 = quad.copyUv(3, null);

			quad.uv(0, uv1);
			quad.uv(1, uv2);
			quad.uv(2, uv3);
			quad.uv(3, uv0);

			return true;
		};
	}
	public static QuadTransform rotateFaceUv180()
	{
		return quad ->
		{
			Vector2f uv0 = quad.copyUv(0, null);
			Vector2f uv1 = quad.copyUv(1, null);
			Vector2f uv2 = quad.copyUv(2, null);
			Vector2f uv3 = quad.copyUv(3, null);

			quad.uv(0, uv2);
			quad.uv(1, uv3);
			quad.uv(2, uv0);
			quad.uv(3, uv1);

			return true;
		};
	}
	public static QuadTransform rotateFaceUv270()
	{
		return quad ->
		{
			Vector2f uv0 = quad.copyUv(0, null);
			Vector2f uv1 = quad.copyUv(1, null);
			Vector2f uv2 = quad.copyUv(2, null);
			Vector2f uv3 = quad.copyUv(3, null);

			quad.uv(0, uv3);
			quad.uv(1, uv0);
			quad.uv(2, uv1);
			quad.uv(3, uv2);

			return true;
		};
	}
}
