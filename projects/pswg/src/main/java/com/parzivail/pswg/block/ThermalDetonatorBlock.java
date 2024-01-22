package com.parzivail.pswg.block;

import com.parzivail.pswg.container.SwgEntities;
import com.parzivail.pswg.entity.BlasterBoltEntity;
import com.parzivail.pswg.entity.ThermalDetonatorEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class ThermalDetonatorBlock extends Block
{

	public ThermalDetonatorBlock(Settings settings)
	{
		super(settings);
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
	{
		if (entity instanceof BlasterBoltEntity)
		{
			var tde = new ThermalDetonatorEntity(SwgEntities.Misc.ThermalDetonator, world);
			tde.setPos(pos.getX(), pos.getY(), pos.getZ());
			tde.setPrimed(true);
			tde.setLife(0);
			world.spawnEntity(tde);
		}
		if (entity instanceof PlayerEntity player)
		{
			player.sendMessage(Text.of("IHATEYOU"), true);
		}
		super.onEntityCollision(state, world, pos, entity);
	}

	@Override
	public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile)
	{
		if (projectile instanceof BlasterBoltEntity bbe)
		{
			explode(world, hit.getBlockPos());
		}
		super.onProjectileHit(world, state, hit, projectile);
	}

	@Override
	public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion)
	{
		explode(world, pos);
		super.onDestroyedByExplosion(world, pos, explosion);
	}

	public void explode(World world, BlockPos blockPos)
	{
		world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
		var tde = new ThermalDetonatorEntity(SwgEntities.Misc.ThermalDetonator, world);
		tde.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
		tde.setPrimed(true);
		tde.setLife(0);

		world.spawnEntity(tde);
	}
}
