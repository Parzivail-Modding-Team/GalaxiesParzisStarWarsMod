package com.parzivail.pswg.item;

import com.parzivail.pswg.container.SwgBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.PillarBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DebugItem extends Item
{
	public DebugItem(Settings settings)
	{
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context)
	{
		if (context.getPlayer() == null || !context.getPlayer().isSneaking())
			return ActionResult.PASS;

		World world = context.getWorld();
		BlockPos start = context.getBlockPos().up();

		if (!world.isAir(start))
			return ActionResult.FAIL;

		int height = world.random.nextInt(30) + 30;
		int baseHeight = height - height / 4 - world.random.nextInt(height / 2);

		BlockState log = SwgBlocks.Log.Sequoia.getDefaultState();
		BlockState leaf = SwgBlocks.Leaves.Sequoia.getDefaultState().with(LeavesBlock.DISTANCE, 1);

		for (int i = height - 1; i >= 0; i--)
		{
			BlockPos center = start.add(0, i, 0);

			float radius = (float)((height / 5f) / Math.sqrt(i + 10));
			circle(world, center, radius, log);

			if (i > baseHeight)
			{
				branch(world, center, log, leaf);
				branch(world, center, log, leaf);
			}
		}

		return ActionResult.SUCCESS;
	}

	private void circle(World world, BlockPos center, float radius, BlockState block)
	{
		for (int x = center.getX() - (int)Math.ceil(radius); x < center.getX() + (int)Math.ceil(radius); x++)
			for (int z = center.getZ() - (int)Math.ceil(radius); z < center.getZ() + (int)Math.ceil(radius); z++)
			{
				float dx = x - center.getX();
				float dz = z - center.getZ();
				if (Math.sqrt(dx * dx + dz * dz) > radius - 0.5f)
					continue;

				world.setBlockState(new BlockPos(x, center.getY(), z), block);
			}
	}

	private void branch(World world, BlockPos start, BlockState log, BlockState leaf)
	{
		Vec3d pos = new Vec3d(start.getX(), start.getY(), start.getZ());
		Vec3d dir = new Vec3d(world.random.nextDouble() * 2 - 1, world.random.nextDouble(), world.random.nextDouble() * 2 - 1).normalize();

		for (int i = 0; i < 4; i++)
		{
			pos = pos.add(dir);
			start = new BlockPos(pos);
			world.setBlockState(start, log.with(PillarBlock.AXIS, Direction.fromRotation(MathHelper.atan2(dir.z, dir.x) / Math.PI * 180).getAxis()));

			if (world.isAir(start.north()))
				world.setBlockState(start.north(), leaf);

			if (world.isAir(start.south()))
				world.setBlockState(start.south(), leaf);

			if (world.isAir(start.east()))
				world.setBlockState(start.east(), leaf);

			if (world.isAir(start.west()))
				world.setBlockState(start.west(), leaf);
		}
	}

	private void donut(World world, BlockPos center, float innerRadius, float outerRadius, BlockState block)
	{
		for (int x = center.getX() - (int)Math.ceil(outerRadius); x < center.getX() + (int)Math.ceil(outerRadius); x++)
			for (int z = center.getZ() - (int)Math.ceil(outerRadius); z < center.getZ() + (int)Math.ceil(outerRadius); z++)
			{
				float dx = x - center.getX();
				float dz = z - center.getZ();
				double r = Math.sqrt(dx * dx + dz * dz);

				if (r > outerRadius - 0.5f || r < innerRadius - 0.5f)
					continue;

				BlockPos pos = new BlockPos(x, center.getY(), z);

				pos = pos.add(world.random.nextGaussian(), world.random.nextGaussian(), world.random.nextGaussian());

				dx = center.getX() - pos.getX();
				dz = center.getZ() - pos.getZ();
				r = Math.sqrt(dx * dx + dz * dz);

				if (r > 5)
					continue;

				world.setBlockState(pos, block);
			}
	}
}
