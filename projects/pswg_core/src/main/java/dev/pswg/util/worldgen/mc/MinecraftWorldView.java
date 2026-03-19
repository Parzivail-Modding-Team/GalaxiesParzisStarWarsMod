package dev.pswg.util.worldgen.mc;

import dev.pswg.util.worldgen.world.WorldGenView;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public class MinecraftWorldView implements WorldGenView
{

	private final WorldGenLevel world;

	public MinecraftWorldView(WorldGenLevel world)
	{

		this.world = world;
	}

	@Override
	public BlockState getBlockState(BlockPos pos)
	{
		return world.getBlockState(pos);
	}

	@Override
	public int getTopY(Heightmap.Types type, int x, int z)
	{
		return world.getHeight(type, x, z);
	}

	@Override
	public void setBlockState(BlockPos pos, BlockState state)
	{
		world.setBlock(pos, state, 3);
	}

	@Override
	public void addEntity(Entity entity)
	{
		world.addFreshEntity(entity);
	}
}