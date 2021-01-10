package com.parzivail.pswg.block.rigs;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.blockentity.TatooineHomeDoorBlockEntity;
import com.parzivail.pswg.client.pr3r.PR3RFile;
import com.parzivail.pswg.rig.IModelRig;
import com.parzivail.pswg.util.MathUtil;
import com.parzivail.util.math.Ease;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
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

	private static final PR3RFile RIG = PR3RFile.tryLoad(Resources.identifier("rigs/block/door/tatooine_home.pr3r"));

	private RigTatooineHomeDoor()
	{
	}

	@Override
	public void transform(MatrixStack stack, TatooineHomeDoorBlockEntity target, Part part)
	{
		if (part != Part.Door)
			return;

		stack.translate(0.5 + MathHelper.sin((float)(((System.currentTimeMillis() % 5000) / 5000f) * Math.PI * 2)), 1, 0.5);
	}

	@Override
	public Vec3d getWorldPosition(MatrixStack stack, TatooineHomeDoorBlockEntity target, Part part, Vec3d localPosition)
	{
		stack.push();
		MatrixStack.Entry entry = stack.peek();
		Matrix4f parent = entry.getModel();
		Matrix4f rig = RIG.objects.get(part.getPartName());
		parent.multiply(rig);

		transform(stack, target, part);

		// TODO: rotate based on block

		Vec3d vec = MathUtil.transform(localPosition, parent);
		stack.pop();

		return vec;
	}

	@Override
	public void transform(MatrixStack stack, TatooineHomeDoorBlockEntity target, Part part, float tickDelta)
	{
		stack.translate(0.5, 0, 0.5);
		stack.multiply(new Quaternion(-90, 0, 0, true));

		if (part != Part.Door)
			return;

		float timer = target.getAnimationTime(tickDelta);

		if (target.isOpening())
			stack.translate(0.845 * Ease.outCubic(1 - timer), 0, 0);
		else
			stack.translate(0.845 * Ease.inCubic(timer), 0, 0);
	}

	@Override
	public Vec3d getWorldPosition(MatrixStack stack, TatooineHomeDoorBlockEntity target, Part part, Vec3d localPosition, float tickDelta)
	{
		stack.push();
		MatrixStack.Entry entry = stack.peek();
		Matrix4f parent = entry.getModel();
		Matrix4f rig = RIG.objects.get(part.getPartName());
		parent.multiply(rig);

		transform(stack, target, part, tickDelta);

		// TODO: rotate based on block

		Vec3d vec = MathUtil.transform(localPosition, parent);
		stack.pop();

		return vec;
	}
}
