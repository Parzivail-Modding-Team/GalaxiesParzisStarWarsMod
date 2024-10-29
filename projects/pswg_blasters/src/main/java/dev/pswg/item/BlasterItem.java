package dev.pswg.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.pswg.Blasters;
import dev.pswg.attributes.AttributeUtil;
import dev.pswg.attributes.GalaxiesEntityAttributes;
import dev.pswg.entity.BlasterBoltEntity;
import dev.pswg.world.TickConstants;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Optional;

public class BlasterItem extends Item implements ILeftClickUsable
{
	public record StateComponent(
			boolean isAiming,
			long lastFired,
			long fireCooldown
	)
	{
		public static final Codec<BlasterItem.StateComponent> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
						                    Codec.BOOL.fieldOf("isAiming").forGetter(BlasterItem.StateComponent::isAiming),
						                    Codec.LONG.fieldOf("lastFired").forGetter(BlasterItem.StateComponent::lastFired),
						                    Codec.LONG.fieldOf("fireCooldown").forGetter(BlasterItem.StateComponent::fireCooldown)
				                    )
				                    .apply(instance, BlasterItem.StateComponent::new)
		);

		public static final PacketCodec<RegistryByteBuf, StateComponent> PACKET_CODEC = PacketCodec.tuple(
				PacketCodecs.BOOL,
				StateComponent::isAiming,
				PacketCodecs.VAR_LONG,
				StateComponent::lastFired,
				PacketCodecs.VAR_LONG,
				StateComponent::fireCooldown,
				StateComponent::new
		);

		public static final StateComponent DEFAULT = new StateComponent(false, 0L, 0L);
	}

	/**
	 * If a blaster us "used" for longer than this time, in ticks, then
	 * the "use" interaction will be considered a "hold to aim" instead of
	 * a "toggle aim", and aiming will cease when the "using" stops.
	 */
	protected static final int TOGGLE_AIMING_USE_TIME_TICKS = 3;

	/**
	 * The attribute modifier that is applied to the {@link EntityAttributes#MOVEMENT_SPEED}
	 * attribute in players when they are aiming-down-sights.
	 */
	protected static final EntityAttributeModifier ATTR_MODIFIER_AIMING_SPEED_PENALTY_ENABLED = new EntityAttributeModifier(
			Blasters.id("aiming_speed_penalty"),
			-0.5F,
			EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
	);

	/**
	 * The attribute modifier that is applied to the {@link GalaxiesEntityAttributes#FIELD_OF_VIEW_ZOOM}
	 * attribute in players when they are aiming-down-sights.
	 */
	protected static final EntityAttributeModifier ATTR_MODIFIER_AIMING_FOV_ENABLED = new EntityAttributeModifier(
			Blasters.id("aiming_zoom"),
			2,
			EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
	);

	/**
	 * The component that determines if the blaster is currently aiming-
	 * down-sights
	 */
	public static final ComponentType<Boolean> IS_AIMING = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Blasters.id("is_aiming"),
			ComponentType.<Boolean>builder().codec(Codec.BOOL).build()
	);

	/**
	 * The component that defines when the blaster was last fired
	 */
	public static final ComponentType<Long> LAST_FIRED = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Blasters.id("last_fired"),
			ComponentType.<Long>builder().codec(Codec.LONG).build()
	);

	/**
	 * The component that defines when the blaster is cooling down until
	 */
	public static final ComponentType<Long> FIRE_COOLDOWN = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Blasters.id("fire_cooldown"),
			ComponentType.<Long>builder().codec(Codec.LONG).build()
	);

	/**
	 * The component that contains the mutable gameplay state of the blaster
	 */
	public static final ComponentType<StateComponent> STATE = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Blasters.id("state"),
			ComponentType.<StateComponent>builder().codec(StateComponent.CODEC).packetCodec(StateComponent.PACKET_CODEC).build()
	);

	/**
	 * @return A new instance of the item settings for this item
	 */
	public static Settings createSettings()
	{
		return new Settings()
				.component(IS_AIMING, false)
				.component(LAST_FIRED, 0L)
				.component(FIRE_COOLDOWN, 0L);
	}

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

	/**
	 * Sets the aiming-down-sights status of the given blaster
	 *
	 * @param stack  The stack to modify
	 * @param aiming True if the blaster should be aiming-down-sights, false otherwise
	 */
	public static void setAiming(ItemStack stack, boolean aiming)
	{
		stack.set(IS_AIMING, aiming);
		var attrs = stack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);

		if (aiming)
		{
			attrs = attrs.with(EntityAttributes.MOVEMENT_SPEED, ATTR_MODIFIER_AIMING_SPEED_PENALTY_ENABLED, AttributeModifierSlot.HAND);
			attrs = attrs.with(GalaxiesEntityAttributes.FIELD_OF_VIEW_ZOOM, ATTR_MODIFIER_AIMING_FOV_ENABLED, AttributeModifierSlot.HAND);
		}
		else
		{
			attrs = AttributeUtil.without(attrs, EntityAttributes.MOVEMENT_SPEED, ATTR_MODIFIER_AIMING_SPEED_PENALTY_ENABLED);
			attrs = AttributeUtil.without(attrs, GalaxiesEntityAttributes.FIELD_OF_VIEW_ZOOM, ATTR_MODIFIER_AIMING_FOV_ENABLED);
		}

		stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, attrs);
	}

	/**
	 * Determines the timestamp when the blaster was last fired. It
	 * is derived from the global timestamp {@link World#getTime()}.
	 *
	 * @param stack The stack to query
	 *
	 * @return A world tick that can be compared against {@link World#getTime()}
	 */
	public static long getLastFired(ItemStack stack)
	{
		return stack.getOrDefault(LAST_FIRED, 0L);
	}

	/**
	 * Sets the timestamp when the blaster was last fired
	 *
	 * @param stack     The stack to modify
	 * @param lastFired The world tick
	 *
	 * @see #getLastFired
	 */
	public static void setLastFired(ItemStack stack, long lastFired)
	{
		stack.set(LAST_FIRED, lastFired);
	}

	/**
	 * Determines the next world tick when the blaster is able to be
	 * fired again. It is derived from the global timestamp {@link World#getTime()}.
	 *
	 * @param stack The stack to query
	 *
	 * @return A world tick that can be compared against {@link World#getTime()}
	 */
	public static long getFireCooldown(ItemStack stack)
	{
		return stack.getOrDefault(FIRE_COOLDOWN, 0L);
	}

	/**
	 * Sets the timestamp when the blaster cooldown should end
	 *
	 * @param stack       The stack to modify
	 * @param cooldownEnd The world tick when the cooldown should end
	 *
	 * @see #getFireCooldown
	 */
	public static void setFireCooldown(ItemStack stack, long cooldownEnd)
	{
		stack.set(FIRE_COOLDOWN, cooldownEnd);
	}

	/**
	 * If currently waiting to be able to fire again, gets the current
	 * progress [0,1) of the cooldown process
	 *
	 * @param world The world to the stack's timestamps are referenced
	 * @param stack The stack to query
	 *
	 * @return A float [0,1) if currently waiting to be able to fire, empty otherwise
	 */
	public static Optional<Float> getFireCooldownProgress(World world, ItemStack stack)
	{
		var lastFired = getLastFired(stack);
		var cooldown = getFireCooldown(stack);
		var time = world.getTime();

		if (cooldown <= lastFired || cooldown < time)
			return Optional.empty();

		var cooldownLength = cooldown - lastFired;
		var cooldownProgress = (float)(time - lastFired) / cooldownLength;
		return Optional.of(cooldownProgress);
	}

	/**
	 * Determines if the blaster is currently able to be fired based on
	 * the blaster's own properties (e.g. ignoring player eligibility)
	 *
	 * @param world The world to the stack's timestamps are referenced
	 * @param user  The entity that is requesting to fire the blaster
	 * @param stack The stack to query
	 *
	 * @return True if the blaster can be fired, false otherwise
	 */
	public static boolean canFire(World world, LivingEntity user, ItemStack stack)
	{
		var isCoolingDown = getFireCooldown(stack) > world.getTime();
		if (isCoolingDown)
			return false;

		// TODO: other checks

		return true;
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
			return TickConstants.ONE_HOUR;

		return super.getMaxUseTime(stack, user);
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user)
	{
		onStoppedUsing(stack, world, user, 0);

		return stack;
	}

	@Override
	public UseAction getUseAction(ItemStack stack)
	{
		return UseAction.NONE;
	}

	@Override
	public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
	{
		if (!world.isClient() && user.getItemUseTime() > TOGGLE_AIMING_USE_TIME_TICKS && isAiming(stack))
			setAiming(stack, false);

		return super.onStoppedUsing(stack, world, user, remainingUseTicks);
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand)
	{
		var stack = user.getStackInHand(hand);

		if (!world.isClient())
		{
			setAiming(stack, !isAiming(stack));

			// this is required to "start using" the item instead of
			// immediately consuming it.
			user.setCurrentHand(hand);

			return ActionResult.CONSUME;
		}

		return ActionResult.FAIL;
	}

	@Override
	public ActionResult useLeft(World world, LivingEntity user, Hand hand)
	{
		ItemStack itemStack = user.getStackInHand(hand);

		if (!canFire(world, user, itemStack))
			return ActionResult.PASS;

		world.playSound(
				null,
				user.getX(),
				user.getY(),
				user.getZ(),
				SoundEvents.ENTITY_SNOWBALL_THROW,
				SoundCategory.NEUTRAL,
				0.5F,
				0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
		);

		setLastFired(itemStack, world.getTime());
		// TODO: pull this value from a default component
		setFireCooldown(itemStack, world.getTime() + TickConstants.ONE_SECOND);

		if (world instanceof ServerWorld serverWorld)
		{
			var projectile = new BlasterBoltEntity(Blasters.BLASTER_BOLT_ENTITY, serverWorld);

			// TODO: abstract into bolt-creating factory
			projectile.setPosition(user.getX(), user.getEyeY() - 0.2f, user.getZ());

			var pitch = user.getPitch();
			var yaw = user.getHeadYaw();
			var roll = 0;

			float f = -MathHelper.sin(yaw * MathHelper.RADIANS_PER_DEGREE) * MathHelper.cos(pitch * MathHelper.RADIANS_PER_DEGREE);
			float g = -MathHelper.sin((pitch + roll) * MathHelper.RADIANS_PER_DEGREE);
			float h = MathHelper.cos(yaw * MathHelper.RADIANS_PER_DEGREE) * MathHelper.cos(pitch * MathHelper.RADIANS_PER_DEGREE);
			projectile.setVelocity(new Vec3d(f, g, h).multiply(5));
			projectile.setAngles(yaw, pitch);

			//			Vec3d vec3d = user.getMovement();
			//			projectile.setVelocity(projectile.getVelocity().add(vec3d));

			serverWorld.spawnEntity(projectile);
		}

		return ActionResult.SUCCESS;
	}
}
