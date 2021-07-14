package com.parzivail.pswg.entity.rigs;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.access.util.Matrix4fAccessUtil;
import com.parzivail.pswg.entity.ship.T65BXwing;
import com.parzivail.pswg.rig.IModelRig;
import com.parzivail.pswg.rig.pr3r.PR3Object;
import com.parzivail.pswg.rig.pr3r.PR3RFile;
import com.parzivail.pswg.util.QuatUtil;
import com.parzivail.util.math.Transform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public class RigT65B implements IModelRig<T65BXwing, RigT65B.Part>
{
	public enum Part
	{
		Nose("Nose"),
		Fuselage("Fuselage"),
		Cockpit("Cockpit"),
		WingTopLeft("WingTopLeft"),
		WingBottomLeft("WingBottomLeft"),
		WingTopRight("WingTopRight"),
		WingBottomRight("WingBottomRight");

		private final String partName;

		Part(String partName)
		{
			this.partName = partName;
		}

		public String getPartName()
		{
			return partName;
		}

		public boolean is(PR3Object object)
		{
			return object.name.equals(partName);
		}
	}

	public enum Socket
	{
		CannonTopLeft(Part.WingTopLeft, new Vec3d(-4.2f, 0.27f, -5.73f)),
		CannonBottomLeft(Part.WingBottomLeft, new Vec3d(-4.2f, -0.27f, -5.73f)),
		CannonTopRight(Part.WingTopRight, new Vec3d(4.2f, 0.27f, -5.73f)),
		CannonBottomRight(Part.WingBottomRight, new Vec3d(4.2f, -0.27f, -5.73f)),
		EngineTopLeft(Part.WingTopLeft, new Vec3d(-1.17f, 0.56f, 1.66f)),
		EngineBottomLeft(Part.WingBottomLeft, new Vec3d(-1.17f, -0.56f, 1.66f)),
		EngineTopRight(Part.WingTopRight, new Vec3d(1.17f, 0.56f, 1.66f)),
		EngineBottomRight(Part.WingBottomRight, new Vec3d(1.17f, -0.56f, 1.66f));

		private final Part parent;
		private final Vec3d localPosition;

		Socket(Part parent, Vec3d localPosition)
		{
			this.parent = parent;
			this.localPosition = localPosition;
		}

		public Vec3d getWorldPosition(Transform stack, T65BXwing entity)
		{
			return INSTANCE.getWorldPosition(stack, entity, parent, localPosition);
		}
	}

	public static final RigT65B INSTANCE = new RigT65B();

	private static final PR3RFile RIG = PR3RFile.tryLoad(Resources.id("rigs/ship/xwing_t65b.pr3r"));

	private RigT65B()
	{
	}

	@Override
	public void transform(Transform stack, T65BXwing target, RigT65B.Part part)
	{
		var entry = stack.value();
		var modelMat = entry.getModel();

		var objectRotation = getRotation(target, part);

		modelMat.multiply(objectRotation);
	}

	private Quaternion getRotation(T65BXwing entity, RigT65B.Part part)
	{
		var wingAnim = entity.getWingAnim();
		var cockpitAnim = entity.getCockpitAnim();

		return getRotation(part, wingAnim.isPositiveDirection(), wingAnim.getTimer(), cockpitAnim.isPositiveDirection(), cockpitAnim.getTimer());
	}

	@Override
	public Vec3d getWorldPosition(Transform stack, T65BXwing target, RigT65B.Part part, Vec3d localPosition)
	{
		stack.save();

		stack.multiply(target.getRotation());

		var entry = stack.value();
		var parent = entry.getModel();
		var rig = RIG.objects().get(part.getPartName());
		parent.multiply(rig);

		transform(stack, target, part);

		var vec = Matrix4fAccessUtil.transform(localPosition, parent);
		stack.restore();

		return vec;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void transform(Transform stack, T65BXwing target, RigT65B.Part part, float tickDelta)
	{
		var wingAnim = target.getWingAnim();
		var wingTimer = wingAnim.getTimer();

		if (wingTimer > 0)
			wingTimer -= tickDelta;

		var cockpitAnim = target.getCockpitAnim();
		var cockpitTimer = cockpitAnim.getTimer();

		if (cockpitTimer > 0)
			cockpitTimer -= tickDelta;

		stack.multiply(getRotation(part, wingAnim.isPositiveDirection(), wingTimer, cockpitAnim.isPositiveDirection(), cockpitTimer));
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Vec3d getWorldPosition(Transform stack, T65BXwing target, RigT65B.Part part, Vec3d localPosition, float tickDelta)
	{
		stack.save();
		var entry = stack.value();
		var parent = entry.getModel();
		var rig = RIG.objects().get(part.getPartName());
		parent.multiply(rig);

		transform(stack, target, part, tickDelta);

		parent.multiply(target.getRotation());

		var vec = Matrix4fAccessUtil.transform(localPosition, parent);
		stack.restore();

		return vec;
	}

	private Quaternion getRotation(Part part, boolean wingsOpening, float wingTimer, boolean cockpitOpening, float cockpitTimer)
	{
		if (part.getPartName().startsWith("Wing"))
		{
			var timer = Math.abs(wingTimer);

			float wingAngle;

			if (wingsOpening)
				wingAngle = 13 * (20 - timer) / 20;
			else
				wingAngle = 13 * timer / 20;

			return switch (part)
					{
						case WingTopLeft, WingBottomRight -> QuatUtil.of(0, 0, -wingAngle, true);
						case WingBottomLeft, WingTopRight -> QuatUtil.of(0, 0, wingAngle, true);
						default -> throw new IndexOutOfBoundsException();
					};
		}
		else if (part == RigT65B.Part.Cockpit)
		{
			var timer = Math.abs(cockpitTimer);

			float cockpitAngle;

			if (cockpitOpening)
				cockpitAngle = 50 * (20 - timer) / 20;
			else
				cockpitAngle = 50 * timer / 20;

			return QuatUtil.of(cockpitAngle, 0, 0, true);
		}

		return new Quaternion(Quaternion.IDENTITY);
	}
}
