package dev.pswg.item;

import com.mojang.serialization.Codec;
import dev.pswg.Blasters;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class BlasterItem extends Item
{
	public static final ComponentType<Long> NEXT_TIMESTAMP_COMPONENT = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Blasters.id("next_timestamp"),
			ComponentType.<Long>builder().codec(Codec.LONG).build()
	);

	public BlasterItem(Settings settings)
	{
		super(settings);
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand)
	{
		var stack = user.getStackInHand(hand);

		stack.set(NEXT_TIMESTAMP_COMPONENT, world.getTime() + 10);

		return ActionResult.SUCCESS;
	}
}
