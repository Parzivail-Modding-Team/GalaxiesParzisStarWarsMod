package com.parzivail.pswg.entity.rigs;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.entity.ship.SpeederEntity;
import com.parzivail.pswg.rig.IModelRig;
import com.parzivail.pswg.rig.pr3r.PR3Object;
import com.parzivail.pswg.rig.pr3r.PR3RFile;
import com.parzivail.util.math.Matrix4fUtil;
import com.parzivail.util.math.QuatUtil;
import com.parzivail.util.math.Transform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;

public class RigX34 implements IModelRig<SpeederEntity, RigX34.Part>
{
	public enum Part
	{
		Fuselage("Fuselage");

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
		Origin(Part.Fuselage, new Vec3d(0, 0, 0));

		private final Part parent;
		private final Vec3d localPosition;

		Socket(Part parent, Vec3d localPosition)
		{
			this.parent = parent;
			this.localPosition = localPosition;
		}

		public Vec3d getWorldPosition(Transform stack, SpeederEntity entity)
		{
			return INSTANCE.getWorldPosition(stack, entity, parent, localPosition);
		}
	}

	public static final RigX34 INSTANCE = new RigX34();

	private static final PR3RFile RIG = PR3RFile.tryLoad(Resources.id("rigs/ship/landspeeder_x34.pr3r"));

	private RigX34()
	{
	}

	@Override
	public void transform(Transform stack, SpeederEntity target, RigX34.Part part)
	{
		var entry = stack.value();
		var modelMat = entry.getModel();

		var objectRotation = getRotation(target, part);

		modelMat.rotate(objectRotation);
	}

	private Quaternionf getRotation(SpeederEntity entity, RigX34.Part part)
	{
		return getRotation(part);
	}

	@Override
	public Vec3d getWorldPosition(Transform stack, SpeederEntity target, RigX34.Part part, Vec3d localPosition)
	{
		stack.save();

		stack.multiply(target.getRotation());

		var entry = stack.value();
		var parent = entry.getModel();
		var rig = RIG.objects().get(part.getPartName());
		parent.mul(rig);

		transform(stack, target, part);

		var vec = Matrix4fUtil.transform(localPosition, parent);
		stack.restore();

		return vec;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void transform(Transform stack, SpeederEntity target, RigX34.Part part, float tickDelta)
	{
		stack.multiply(getRotation(part));
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Vec3d getWorldPosition(Transform stack, SpeederEntity target, RigX34.Part part, Vec3d localPosition, float tickDelta)
	{
		stack.save();
		var entry = stack.value();
		var parent = entry.getModel();
		var rig = RIG.objects().get(part.getPartName());
		parent.mul(rig);

		transform(stack, target, part, tickDelta);

		parent.rotate(target.getRotation());

		var vec = Matrix4fUtil.transform(localPosition, parent);
		stack.restore();

		return vec;
	}

	private Quaternionf getRotation(Part part)
	{
		return new Quaternionf(QuatUtil.IDENTITY);
	}
}
