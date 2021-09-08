package com.parzivail.pswg.block.rigs;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.blockentity.TatooineHomeDoorBlockEntity;
import com.parzivail.pswg.rig.IModelRig;
import com.parzivail.pswg.rig.pr3r.PR3RFile;
import com.parzivail.util.block.rotating.RotatingBlock;
import com.parzivail.util.math.ClientMathUtil;
import com.parzivail.util.math.Ease;
import com.parzivail.util.math.Matrix4fUtil;
import com.parzivail.util.math.Transform;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public class RigTatooineHomeDoor implements IModelRig<TatooineHomeDoorBlockEntity, RigTatooineHomeDoor.Part>
{
	public enum Part
	{
		Frame("Frame"),
		Door("Door"),
		StencilMask("StencilMask");

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

	public static final RigTatooineHomeDoor INSTANCE = new RigTatooineHomeDoor();

	private static final PR3RFile RIG = PR3RFile.tryLoad(Resources.id("rigs/block/door/tatooine_home.pr3r"));

	private RigTatooineHomeDoor()
	{
	}

	@Override
	public void transform(Transform stack, TatooineHomeDoorBlockEntity target, Part part)
	{
		transform(stack, target, part, 0);
	}

	@Override
	public Vec3d getWorldPosition(Transform stack, TatooineHomeDoorBlockEntity target, Part part, Vec3d localPosition)
	{
		return getWorldPosition(stack, target, part, localPosition, 0);
	}

	@Override
	public void transform(Transform stack, TatooineHomeDoorBlockEntity target, Part part, float tickDelta)
	{
		stack.translate(0.5, 0, 0.5);

		var world = target.getWorld();
		var pos = target.getPos();
		var block = world.getBlockState(pos);
		stack.multiply(ClientMathUtil.getRotation(block.get(RotatingBlock.FACING)));

		if (part == Part.Door)
		{
			var timer = target.getAnimationTime(tickDelta);

			if (target.isOpening())
				stack.translate(0, 0, 0.845 * Ease.outCubic(1 - timer));
			else
				stack.translate(0, 0, 0.845 * Ease.inCubic(timer));
		}

		stack.multiply(new Quaternion(-90, 0, 0, true));
	}

	@Override
	public Vec3d getWorldPosition(Transform stack, TatooineHomeDoorBlockEntity target, Part part, Vec3d localPosition, float tickDelta)
	{
		stack.save();
		var entry = stack.value();
		var parent = entry.getModel();
		var rig = RIG.objects().get(part.getPartName());
		parent.multiply(rig);

		transform(stack, target, part, tickDelta);

		var vec = Matrix4fUtil.transform(localPosition, parent);
		stack.restore();

		return vec;
	}
}
