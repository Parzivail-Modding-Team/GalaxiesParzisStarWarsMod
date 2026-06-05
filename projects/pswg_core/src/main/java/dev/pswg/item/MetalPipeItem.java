package dev.pswg.item;

import dev.pswg.container.GalaxiesSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MetalPipeItem extends Item
{
	public MetalPipeItem(Properties properties)
	{
		super(properties);
	}

	@Override
	public void hurtEnemy(ItemStack itemStack, LivingEntity mob, LivingEntity attacker)
	{
		Level level = attacker.level();
		RandomSource random = level.getRandom();
		level.playSound(null, mob.blockPosition(), GalaxiesSounds.METAL_PIPE, SoundSource.PLAYERS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F);
		super.hurtEnemy(itemStack, mob, attacker);
	}
}
