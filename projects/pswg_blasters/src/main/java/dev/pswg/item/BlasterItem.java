package dev.pswg.item;

import com.mojang.serialization.Codec;
import dev.pswg.Blasters;
import dev.pswg.world.TickSpan;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlasterItem extends Item
{
	/**
	 * If a blaster us "used" for longer than this time, in seconds, then
	 * the "use" interaction will be considered a "hold to aim" instead of
	 * a "toggle aim", and aiming will cease when the "using" stops.
	 */
	public static final float TOGGLE_AIMING_USE_TIME_SECONDS = 0.15f;

	/**
	 * The component that determines if the blaster is currently aiming-
	 * down-sights
	 */
	public static final ComponentType<Boolean> IS_AIMING = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Blasters.id("is_aiming"),
			ComponentType.<Boolean>builder().codec(Codec.BOOL).build()
	);

	public BlasterItem(Settings settings)
	{
		super(settings);
	}

	/**
	 * Determines if the blaster is currently aiming-down-sights
	 *
	 * @param stack The stack to query
	 *
	 * @return True if the blaster is currently aiming-down-sights, false otherwise
	 */
	public static boolean isAiming(ItemStack stack)
	{
		return stack.getOrDefault(IS_AIMING, false);
	}

	@Override
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner)
	{
		return false;
	}

	@Override
	public int getMaxUseTime(ItemStack stack, LivingEntity user)
	{
		if (isAiming(stack))
			return TickSpan.fromSeconds(user.getWorld(), 3600);

		return super.getMaxUseTime(stack, user);
	}

	@Override
	public UseAction getUseAction(ItemStack stack)
	{
		return UseAction.NONE;
	}

	@Override
	public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
	{
		var toggleAimingUseTimeTicks = TickSpan.fromSeconds(world, TOGGLE_AIMING_USE_TIME_SECONDS);
		if (!world.isClient && user.getItemUseTime() > toggleAimingUseTimeTicks && isAiming(stack))
			stack.set(IS_AIMING, false);

		return super.onStoppedUsing(stack, world, user, remainingUseTicks);
	}
}
