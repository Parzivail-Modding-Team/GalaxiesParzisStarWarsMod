package dev.pswg.util.worldgen.mc;

import dev.pswg.util.worldgen.world.WorldGenView;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;

public class MinecraftWorldView implements WorldGenView
{

	private final StructureWorldAccess world;

	public MinecraftWorldView(StructureWorldAccess world)
	{

		this.world = world;
	}

	@Override
	public BlockState getBlockState(BlockPos pos)
	{
		return world.getBlockState(pos);
	}

	@Override
	public int getTopY(Heightmap.Type type, int x, int z)
	{
		return world.getTopY(type, x, z);
	}

	@Override
	public void setBlockState(BlockPos pos, BlockState state)
	{
		world.setBlockState(pos, state, 3);
	}

	@Override
	public void addEntity(Entity entity)
	{
		world.spawnEntity(entity);
	}
}