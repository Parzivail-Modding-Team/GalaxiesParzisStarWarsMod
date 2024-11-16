package dev.pswg.item;

import dev.pswg.Gadgets;
import dev.pswg.entity.ThermalDetonatorEntity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ThermalDetonatorItem extends GrenadeItem
{
	public ThermalDetonatorItem(Settings settings)
	{
		super(settings, Blocks.IRON_BLOCK, Gadgets.THERMAL_DETONATOR_ITEM, 150);
	}
	@Override
	public void throwEntity(World world, ItemStack stack, PlayerEntity player)
	{
		ThermalDetonatorEntity td = new ThermalDetonatorEntity(Gadgets.THERMAL_DETONATOR_ENTITY, world);
		if(stack.contains(Gadgets.PRIMING_TIME))
		{
			td.setLife((int)(stack.get(Gadgets.PRIMING_TIME) + baseTicksToExplosion - world.getTime()));
			td.setPrimed(true);
		}else{
			td.setLife(150);
			td.setPrimed(false);
		}
		td.setVisible(true);
		td.onSpawnPacket(new EntitySpawnS2CPacket(td.getId(), td.getUuid(), player.getX(), player.getY() + 1.5, player.getZ(), -player.getPitch(), -player.getYaw(), td.getType(), 0, Vec3d.ZERO, player.getHeadYaw()));
		td.setOwner(player);
		td.setVelocity(player, player.getPitch(), player.getYaw(), (float)player.getRotationVector().z *10, 1.0F, 0F);

		world.spawnEntity(td);
	}

	@Override
	public void spawnEntity(World world, int power, ItemStack stack , Entity player)
	{
		ThermalDetonatorEntity td = new ThermalDetonatorEntity(Gadgets.THERMAL_DETONATOR_ENTITY, world);
		//world.getTime() - baseTicksToExplosion
		td.setLife(stack.contains(Gadgets.PRIMING_TIME) ? (int)(stack.get(Gadgets.PRIMING_TIME) + baseTicksToExplosion - world.getTime() ) : 1);
		td.setPrimed(stack.contains(Gadgets.PRIMING_TIME) ? true : false );
		td.setExplosionPower(power);
		td.onSpawnPacket(new EntitySpawnS2CPacket(td.getId(), td.getUuid(), player.getX(), player.getY() + 1, player.getZ(), -player.getPitch(), -player.getYaw(), td.getType(), 0, Vec3d.ZERO, player.getHeadYaw()));
		world.spawnEntity(td);
	}
}
