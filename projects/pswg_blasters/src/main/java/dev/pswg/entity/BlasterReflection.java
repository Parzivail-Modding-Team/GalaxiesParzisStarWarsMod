package dev.pswg.entity;

import com.mojang.serialization.Codec;
import dev.pswg.Blasters;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * Active-use reflection capability for shields, lightsabers and addon items.
 */
public final class BlasterReflection
{
	/**
	 * Requires an active reflector facing the incoming bolt.
	 */
	public static boolean canReflect(LivingEntity target, Vec3 incoming)
	{
		return target.isUsingItem()
		       && target.getUseItem().getOrDefault(COMPONENT, false)
		       && target.getLookAngle().dot(incoming.normalize()) < -0.1;
	}

	public static void register()
	{
	}

	/**
	 * Enables reflection while the item is actively used.
	 */
	public static final DataComponentType<Boolean> COMPONENT = Registry.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			Blasters.id("reflects_blasters"),
			DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build()
	);

	private BlasterReflection()
	{
	}
}
