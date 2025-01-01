package dev.pswg.item;

import dev.pswg.Gadgets;
import dev.pswg.entity.FragmentationGrenadeEntity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FragmentationGrenadeItem extends GrenadeItem
{
	public FragmentationGrenadeItem(Item.Settings settings)
	{
		super(settings, Blocks.IRON_BLOCK, Gadgets.FRAGMENTATION_GRENADE_ITEM, 150);
	}

	@Override
	public void throwEntity(World world, ItemStack stack, PlayerEntity player)
	{
		FragmentationGrenadeEntity fg = new FragmentationGrenadeEntity(Gadgets.FRAGMENTATION_GRENADE_ENTITY, world);
		if (stack.contains(Gadgets.PRIMING_TIME))
		{
			fg.setLife((int)(stack.get(Gadgets.PRIMING_TIME) + baseTicksToExplosion - world.getTime()));
			fg.setPrimed(true);
		}
		else
		{
			fg.setLife(150);
			fg.setPrimed(false);
		}
		fg.setVisible(true);
		fg.onSpawnPacket(new EntitySpawnS2CPacket(fg.getId(), fg.getUuid(), player.getX(), player.getY() + 1.5, player.getZ(), -player.getPitch(), -player.getYaw(), fg.getType(), 0, Vec3d.ZERO, player.getHeadYaw()));
		fg.setOwner(player);
		fg.setVelocity(player, player.getPitch(), player.getYaw(), (float)player.getRotationVector().z * 10, 1.0F, 0F);

		world.spawnEntity(fg);
	}

	@Override
	public void spawnEntity(World world, int power, ItemStack stack, Entity player)
	{
		FragmentationGrenadeEntity fg = new FragmentationGrenadeEntity(Gadgets.FRAGMENTATION_GRENADE_ENTITY, world);
		//world.getTime() - baseTicksToExplosion
		fg.setLife(stack.contains(Gadgets.PRIMING_TIME) ? (int)(stack.get(Gadgets.PRIMING_TIME) + baseTicksToExplosion - world.getTime()) : 1);
		fg.setPrimed(stack.contains(Gadgets.PRIMING_TIME));
		fg.setExplosionPower(power);
		fg.onSpawnPacket(new EntitySpawnS2CPacket(fg.getId(), fg.getUuid(), player.getX(), player.getY() + 1, player.getZ(), -player.getPitch(), -player.getYaw(), fg.getType(), 0, Vec3d.ZERO, player.getHeadYaw()));
		world.spawnEntity(fg);
	}

	// Used for dispensers
	@Override
	public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction)
	{
		FragmentationGrenadeEntity fg = new FragmentationGrenadeEntity(Gadgets.FRAGMENTATION_GRENADE_ENTITY, world);
		initializeProjectile(fg, pos.getX(), pos.getY(), pos.getZ(), 1f, 0);
		return fg;
	}
}
