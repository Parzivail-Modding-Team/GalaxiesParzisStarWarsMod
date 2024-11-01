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
import java.util.function.UnaryOperator;

public class BlasterItem extends Item implements ILeftClickUsable
{
	public enum CoolingMode
	{
		NONE,
		OVERHEAT,
		SUCCESSFUL_BYPASS,
		FAILED_BYPASS
	}

	/**
	 * Contains stats related to blaster heating and cooling
	 *
	 * @param capacity             The maximum amount of heat units that may be accumulated before overheating.
	 * @param perRound             The amount of heat units accumulated per shot fired.
	 * @param drainSpeed           The amount of time, in ticks, after the most recent shot was fired before the
	 *                             blaster will begin to passively cool without venting.
	 * @param overheatPenalty      The amount of heat units removed from the blaster per tick while passively cooling
	 *                             or venting.
	 * @param overheatDrainSpeed   The amount of heat units removed from the blaster per tick while venting due to
	 *                             an overheat.
	 * @param passiveCooldownDelay The amount of "extra" heat units accumulated when the blaster overheats, effectively
	 *                             delaying the blaster from beginning to cool by {@code overheatPenalty / (overheatDrainSpeed * 20)} seconds.
	 * @param overchargeBonus      The amount of time, in ticks, the blaster stays in overcharge when the secondary
	 *                             bypass is triggered.
	 */
	public record Heat(
			int capacity,
			int perRound,
			int drainSpeed,
			int overheatPenalty,
			int overheatDrainSpeed,
			int passiveCooldownDelay,
			int overchargeBonus
	)
	{
		public static final Heat DEFAULT = new Heat(
				100,
				20,
				5,
				60,
				1,
				20,
				40
		);

		public static final Codec<Heat> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
						                    Codec.INT.fieldOf("capacity").forGetter(Heat::capacity),
						                    Codec.INT.fieldOf("perRound").forGetter(Heat::perRound),
						                    Codec.INT.fieldOf("drainSpeed").forGetter(Heat::drainSpeed),
						                    Codec.INT.fieldOf("overheatPenalty").forGetter(Heat::overheatPenalty),
						                    Codec.INT.fieldOf("overheatDrainSpeed").forGetter(Heat::overheatDrainSpeed),
						                    Codec.INT.fieldOf("passiveCooldownDelay").forGetter(Heat::passiveCooldownDelay),
						                    Codec.INT.fieldOf("overchargeBonus").forGetter(Heat::overchargeBonus)
				                    )
				                    .apply(instance, Heat::new)
		);

		public static final PacketCodec<RegistryByteBuf, Heat> PACKET_CODEC = PacketCodec.tuple(
				PacketCodecs.VAR_INT, Heat::capacity,
				PacketCodecs.VAR_INT, Heat::perRound,
				PacketCodecs.VAR_INT, Heat::drainSpeed,
				PacketCodecs.VAR_INT, Heat::overheatPenalty,
				PacketCodecs.VAR_INT, Heat::overheatDrainSpeed,
				PacketCodecs.VAR_INT, Heat::passiveCooldownDelay,
				PacketCodecs.VAR_INT, Heat::overchargeBonus,
				Heat::new
		);
	}

	/**
	 * Contains the immutable, intrinsic stats of this particular
	 * variant of blaster
	 *
	 * @param damage The damage, in hit points (half hearts) a single shot inflicts.
	 * @param range  The maximum distance, in blocks, a blaster can fire a bolt.
	 * @param heat   The heating and cooling stats.
	 */
	public record StatsComponent(
			float damage,
			int range,
			Heat heat
	)
	{
		public static final StatsComponent DEFAULT = new StatsComponent(8, 48, Heat.DEFAULT);

		public static final Codec<StatsComponent> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
						Codec.FLOAT.fieldOf("damage").forGetter(StatsComponent::damage),
						Codec.INT.fieldOf("range").forGetter(StatsComponent::range),
						Heat.CODEC.fieldOf("heat").forGetter(StatsComponent::heat)
				).apply(instance, StatsComponent::new)
		);

		public static final PacketCodec<RegistryByteBuf, StatsComponent> PACKET_CODEC = PacketCodec.tuple(
				PacketCodecs.FLOAT, StatsComponent::damage,
				PacketCodecs.VAR_INT, StatsComponent::range,
				Heat.PACKET_CODEC, StatsComponent::heat,
				StatsComponent::new
		);
	}

	/**
	 * The container for the mutable gameplay state of the blaster
	 *
	 * @param isAiming     Determines if the blaster is currently aiming-down-sights
	 * @param lastFired    Defines when the blaster was last fired
	 * @param fireCooldown Defines when the blaster is cooling down until
	 */
	public record StateComponent(
			boolean isAiming,
			long lastFired,
			long fireCooldown,
			long lastHeated,
			float lastTotalHeat
	)
	{
		public static final Codec<BlasterItem.StateComponent> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
						                    Codec.BOOL.fieldOf("isAiming").forGetter(BlasterItem.StateComponent::isAiming),
						                    Codec.LONG.fieldOf("lastFired").forGetter(BlasterItem.StateComponent::lastFired),
						                    Codec.LONG.fieldOf("fireCooldown").forGetter(BlasterItem.StateComponent::fireCooldown),
						                    Codec.LONG.fieldOf("lastHeated").forGetter(BlasterItem.StateComponent::fireCooldown),
						                    Codec.FLOAT.fieldOf("lastTotalHeat").forGetter(BlasterItem.StateComponent::lastTotalHeat)
				                    )
				                    .apply(instance, BlasterItem.StateComponent::new)
		);

		public static final PacketCodec<RegistryByteBuf, StateComponent> PACKET_CODEC = PacketCodec.tuple(
				PacketCodecs.BOOL, StateComponent::isAiming,
				PacketCodecs.VAR_LONG, StateComponent::lastFired,
				PacketCodecs.VAR_LONG, StateComponent::fireCooldown,
				PacketCodecs.VAR_LONG, StateComponent::lastHeated,
				PacketCodecs.FLOAT, StateComponent::lastTotalHeat,
				StateComponent::new
		);

		public static final StateComponent DEFAULT = new StateComponent(false, 0, 0, 0, 0);

		public StateComponent withIsAiming(boolean isAiming)
		{
			return new StateComponent(isAiming, lastFired, fireCooldown, lastHeated, lastTotalHeat);
		}

		public StateComponent withLastFired(long lastFired)
		{
			return new StateComponent(isAiming, lastFired, fireCooldown, lastHeated, lastTotalHeat);
		}

		public StateComponent withFireCooldown(long fireCooldown)
		{
			return new StateComponent(isAiming, lastFired, fireCooldown, lastHeated, lastTotalHeat);
		}

		public StateComponent withLastHeated(long lastHeated)
		{
			return new StateComponent(isAiming, lastFired, fireCooldown, lastHeated, lastTotalHeat);
		}

		public StateComponent withLastTotalHeat(float lastTotalHeat)
		{
			return new StateComponent(isAiming, lastFired, fireCooldown, lastHeated, lastTotalHeat);
		}
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
	 * The component that contains the mutable gameplay state of the blaster
	 */
	private static final ComponentType<StateComponent> STATE = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Blasters.id("state"),
			ComponentType.<StateComponent>builder().codec(StateComponent.CODEC).packetCodec(StateComponent.PACKET_CODEC).build()
	);

	/**
	 * The component that contains the immutable base statistics of the blaster
	 */
	private static final ComponentType<StatsComponent> STATS = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Blasters.id("stats"),
			ComponentType.<StatsComponent>builder().codec(StatsComponent.CODEC).packetCodec(StatsComponent.PACKET_CODEC).build()
	);

	/**
	 * @return A new instance of the item settings for this item
	 */
	public static Settings createSettings()
	{
		return new Settings()
				.component(STATS, StatsComponent.DEFAULT)
				.component(STATE, StateComponent.DEFAULT);
	}

	public BlasterItem(Settings settings)
	{
		super(settings);
	}

	/**
	 * Gets the stats of the given blaster
	 *
	 * @param stack The stack to query
	 *
	 * @return The blaster's stats
	 */
	public static StatsComponent getStats(ItemStack stack)
	{
		return stack.getOrDefault(STATS, StatsComponent.DEFAULT);
	}

	/**
	 * Applies a state modification to the given stack.
	 *
	 * @param stack         The ItemStack to be modified.
	 * @param stateOperator A UnaryOperator function that modifies and returns a new StateComponent.
	 */
	public static void applyState(ItemStack stack, UnaryOperator<StateComponent> stateOperator)
	{
		stack.apply(STATE, StateComponent.DEFAULT, stateOperator);
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
		return stack.getOrDefault(STATE, StateComponent.DEFAULT).isAiming();
	}

	/**
	 * Sets the aiming-down-sights status of the given blaster
	 *
	 * @param stack  The stack to modify
	 * @param aiming True if the blaster should be aiming-down-sights, false otherwise
	 */
	public static void setAiming(ItemStack stack, boolean aiming)
	{
		applyState(stack, state -> state.withIsAiming(aiming));

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
		return stack.getOrDefault(STATE, StateComponent.DEFAULT).lastFired();
	}

	/**
	 * Sets the timestamp when the blaster was last fired
	 *
	 * @param stack     The stack to modify
	 * @param lastFired The world tick
	 */
	public static void setLastFired(ItemStack stack, long lastFired)
	{
		applyState(stack, state -> state.withLastFired(lastFired));
	}

	/**
	 * Gets the timestamp when heat was last added, and therefore the
	 * time from which all heat calculations (e.g. cooldowns) are made.
	 *
	 * @param stack The stack to query
	 *
	 * @return The world tick when heat was last applied
	 */
	public static long getLastHeated(ItemStack stack)
	{
		return stack.getOrDefault(STATE, StateComponent.DEFAULT).lastHeated();
	}

	/**
	 * Gets the amount of heat the blaster contained the last time
	 * heat was added. To get the current amount of heat, taking
	 * into account cooling and other parameters, see {@link #getHeat(World, ItemStack, float)}.
	 *
	 * @param stack The stack to query
	 *
	 * @return The total amount of heat at the last time heat was added
	 *
	 * @see #getLastHeated
	 */
	public static float getLastTotalHeat(ItemStack stack)
	{
		return stack.getOrDefault(STATE, StateComponent.DEFAULT).lastTotalHeat();
	}

	/**
	 * Sets the amount of heat the blaster contained the last time
	 * heat was added.
	 *
	 * @param stack The stack to modify
	 * @param heat  The total amount of heat in the blaster
	 */
	public static void setLastTotalHeat(ItemStack stack, long timestamp, float heat)
	{
		applyState(stack, state -> state
				.withLastTotalHeat(heat)
				.withLastHeated(timestamp)
		);
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
		return stack.getOrDefault(STATE, StateComponent.DEFAULT).fireCooldown();
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
		applyState(stack, state -> state.withFireCooldown(cooldownEnd));
	}

	/**
	 * If currently waiting to be able to fire again, gets the current
	 * progress [0,1) of the cooldown process
	 *
	 * @param world     The world to the stack's timestamps are referenced
	 * @param stack     The stack to query
	 * @param tickDelta The partial tick to evaluate at
	 *
	 * @return A float [0,1) if currently waiting to be able to fire, empty otherwise
	 */
	public static Optional<Float> getFireCooldownProgress(World world, ItemStack stack, float tickDelta)
	{
		var lastFired = getLastFired(stack);
		var cooldown = getFireCooldown(stack);
		var time = world.getTime() + tickDelta;

		if (cooldown <= lastFired || cooldown < time)
			return Optional.empty();

		var cooldownLength = cooldown - lastFired;
		var cooldownProgress = (time - lastFired) / cooldownLength;
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

	/**
	 * Calculates the current heat of the blaster based on the dissipation rate and the time passed since the last shot.
	 *
	 * @param world     The world to the stack's timestamps are referenced
	 * @param stack     The stack to query
	 * @param tickDelta The partial tick to evaluate at
	 *
	 * @return The current heat of the blaster
	 */
	public static float getHeat(World world, ItemStack stack, float tickDelta)
	{
		var time = world.getTime() + tickDelta;

		var lastCommittedHeat = getLastTotalHeat(stack);
		var lastCommittedHeatTime = getLastHeated(stack);

		var stats = getStats(stack);

		// TODO: other kinds of delays, overheats, etc.
		var dissipationDelayTicks = stats.heat().passiveCooldownDelay();
		var dissipationPerTick = stats.heat().drainSpeed();

		var dissipation = dissipationPerTick * (time - lastCommittedHeatTime - dissipationDelayTicks);
		return MathHelper.clamp(lastCommittedHeat - dissipation, 0, lastCommittedHeat);
	}

	public static float overchargeTimeRemaining(World world, ItemStack itemStack, float tickDelta)
	{
		return 0;
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

		var stats = getStats(itemStack);

		var timestamp = world.getTime();
		setLastFired(itemStack, timestamp);

		// TODO: set in stack
		var passiveCooldownStartTimestamp = timestamp + stats.heat().passiveCooldownDelay();

		var totalHeat = getHeat(world, itemStack, 0);
		if (overchargeTimeRemaining(world, itemStack, 0) == 0)
			totalHeat += stats.heat().perRound();

		// TODO: pull this value from a default component
		setFireCooldown(itemStack, world.getTime() + 4);

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

		if (totalHeat > stats.heat().capacity())
		{
			// overheat sound

			// TODO: set all in stack
			var ventingHeat = totalHeat + stats.heat().overheatPenalty();
			var coolingMode = CoolingMode.OVERHEAT;
			var canBypassCooling = true;
			var burstCounter = 0;

			totalHeat = 0;
		}

		setLastTotalHeat(itemStack, timestamp, totalHeat);

		return ActionResult.SUCCESS;
	}
}
