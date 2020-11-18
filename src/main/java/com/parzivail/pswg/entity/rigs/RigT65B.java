package com.parzivail.pswg.entity.rigs;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.pr3r.PR3RFile;
import com.parzivail.pswg.entity.IEntityRig;
import com.parzivail.pswg.entity.ship.T65BXwing;
import com.parzivail.pswg.util.MathUtil;
import com.parzivail.util.math.QuatUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public class RigT65B implements IEntityRig<T65BXwing, RigT65B.Part>
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

		public Vec3d getWorldPosition(T65BXwing entity)
		{
			return INSTANCE.getWorldPosition(entity, parent, localPosition);
		}
	}

	public static final RigT65B INSTANCE = new RigT65B();

	private static final PR3RFile RIG = PR3RFile.tryLoad(Resources.identifier("rigs/ship/xwing_t65b.pr3r"));

	private RigT65B()
	{
	}

	@Override
	public Quaternion getRotation(T65BXwing entity, RigT65B.Part part)
	{
		short wingTimer = entity.getWingTimer();

		boolean wingsOpening = entity.getWingDirection();
		float timer = Math.abs(wingTimer);

		return getRotation(part, wingsOpening, timer);
	}

	@Override
	public Vec3d getWorldPosition(T65BXwing entity, RigT65B.Part part, Vec3d localPosition)
	{
		Matrix4f rig = RIG.objects.get(part.getPartName());

		localPosition = MathUtil.transform(localPosition, rig);
		localPosition = QuatUtil.rotate(localPosition, getRotation(entity, part));

		return QuatUtil.rotate(localPosition, entity.getRotation());
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Quaternion getRotation(T65BXwing entity, RigT65B.Part part, float tickDelta)
	{
		short wingTimer = entity.getWingTimerClient();

		boolean wingsOpening = entity.getWingDirectionClient();
		float timer = Math.abs(wingTimer);

		if (timer > 0)
			timer -= tickDelta;

		return getRotation(part, wingsOpening, timer);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Vec3d getWorldPosition(T65BXwing entity, RigT65B.Part part, Vec3d localPosition, float tickDelta)
	{
		Matrix4f rig = RIG.objects.get(part.getPartName());

		localPosition = MathUtil.transform(localPosition, rig);
		localPosition = QuatUtil.rotate(localPosition, getRotation(entity, part, tickDelta));

		return QuatUtil.rotate(localPosition, entity.getViewRotation(tickDelta));
	}

	private Quaternion getRotation(RigT65B.Part part, boolean wingsOpening, float timer)
	{
		float angle;

		if (wingsOpening)
			angle = 13 * (20 - timer) / 20;
		else
			angle = 13 * timer / 20;

		switch (part)
		{
			case WingTopLeft:
			case WingBottomRight:
				return QuatUtil.of(0, 0, -angle, true);
			case WingBottomLeft:
			case WingTopRight:
				return QuatUtil.of(0, 0, angle, true);
		}

		return new Quaternion(Quaternion.IDENTITY);
	}
}
