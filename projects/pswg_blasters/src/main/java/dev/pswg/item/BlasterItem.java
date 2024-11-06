package dev.pswg.item;

import com.mojang.serialization.Codec;
import dev.pswg.Blasters;
import dev.pswg.attributes.AttributeUtil;
import dev.pswg.attributes.GalaxiesEntityAttributes;
import dev.pswg.codec.GalaxiesCodecs;
import dev.pswg.codecgenerator.GenerateCodec;
import dev.pswg.codecgenerator.SelfCodec;
import dev.pswg.entity.BlasterBoltEntity;
import dev.pswg.generated.codecs.IHeatCodec;
import dev.pswg.generated.codecs.IStateComponentCodec;
import dev.pswg.generated.codecs.IStatsComponentCodec;
import dev.pswg.generated.recordbuilders.IStateComponentBuilder;
import dev.pswg.mutablerecord.MutableRecord;
import dev.pswg.networking.GalaxiesPacketCodecs;
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
	/**
	 * The reason, if any, for a blaster to be cooling.
	 * Different cooling modes allow different interactions
	 * to interrupt their progress.
	 */
	public enum CoolingMode
	{
		/**
		 * The blaster is not currently cooling
		 */
		NONE,
		/**
		 * The blaster is cooling due to an overheating event.
		 * This mode displays the bypass minigame.
		 */
		OVERHEAT,
		/**
		 * The blaster is cooling due to a request to manually
		 * vent the accumulated heat. This mode does not display
		 * the bypass minigame.
		 */
		REQUESTED_BYPASS,
		/**
		 * The blaster is cooling after a failed attempt at
		 * the bypass minigame. This mode does not display
		 * the bypass minigame.
		 */
		FAILED_OVERCHARGE;

		public static final Codec<CoolingMode> CODEC = GalaxiesCodecs.forEnum(CoolingMode.class);
		public static final PacketCodec<RegistryByteBuf, CoolingMode> PACKET_CODEC = GalaxiesPacketCodecs.forEnum(CoolingMode.class);
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
	@GenerateCodec
	public record Heat(
			int capacity,
			int perRound,
			int drainSpeed,
			int overheatPenalty,
			int overheatDrainSpeed,
			int passiveCooldownDelay,
			int overchargeBonus
	) implements IHeatCodec
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
	}

	/**
	 * Contains the immutable, intrinsic stats of this particular
	 * variant of blaster
	 *
	 * @param damage               The damage, in hit points (half hearts) a single shot inflicts.
	 * @param range                The maximum distance, in blocks, a blaster can fire a bolt.
	 * @param automaticRepeatDelay The minimum time, in ticks, between two bolts firing during automatic fire.
	 * @param heat                 The heating and cooling stats.
	 */
	@GenerateCodec
	public record StatsComponent(
			float damage,
			int range,
			int automaticRepeatDelay,
			@SelfCodec Heat heat
	) implements IStatsComponentCodec
	{
		public static final StatsComponent DEFAULT = new StatsComponent(8, 48, 4, Heat.DEFAULT);
	}

	/**
	 * The container for the mutable gameplay state of the blaster
	 *
	 * @param isAiming             Determines if the blaster is currently aiming-down-sights
	 * @param lastFired            The timestamp when the blaster was last fired. It is derived
	 *                             from the global timestamp {@link World#getTime()}.
	 * @param fireCooldown         Determines the next world tick when the blaster is able to be
	 *                             fired again. It is derived from the global timestamp {@link World#getTime()}
	 * @param passiveCooldownStart The timestamp when the blaster will begin passively cooling down
	 * @param lastHeated           The timestamp when the blaster last accumulated heat
	 * @param lastTotalHeat        The amount of heat the blaster contained the last time
	 *                             heat was added. To get the current amount of heat, taking
	 *                             into account cooling and other parameters, see {@link #getHeat(World, ItemStack, float)}.
	 * @param lastVentingHeat      The amount of heat at the time of cooling start. Can be different
	 *                             from {@code lastTotalHeat} if e.g. a heat penalty was applied
	 * @param coolingMode          The cooling mode of the blaster, if any
	 * @param burstBoltsRemaining  The amount of bolts remaining in this burst
	 */
	@MutableRecord
	@GenerateCodec
	public record StateComponent(
			boolean isAiming,
			long lastFired,
			long fireCooldown,
			long passiveCooldownStart,
			long lastHeated,
			float lastTotalHeat,
			float lastVentingHeat,
			@SelfCodec CoolingMode coolingMode,
			int burstBoltsRemaining
	) implements IStateComponentBuilder, IStateComponentCodec
	{
		public static final StateComponent DEFAULT = new StateComponent(
				false,
				0,
				0,
				0,
				0,
				0,
				0,
				CoolingMode.NONE,
				0
		);

		/**
		 * Sets the amount of heat the blaster contained the last time
		 * heat was added.
		 *
		 * @param timestamp The timestamp when the heat was generated
		 * @param heat      The total amount of heat in the blaster
		 */
		public StateComponent withHeat(long timestamp, float heat)
		{
			return this.withLastTotalHeat(heat)
			           .withLastHeated(timestamp);
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
	 * Gets the state of the given blaster
	 *
	 * @param stack The stack to query
	 *
	 * @return The blaster's mutable state
	 */
	private static StateComponent getState(ItemStack stack)
	{
		return stack.getOrDefault(STATE, StateComponent.DEFAULT);
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
		var state = getState(stack);

		var lastFired = state.lastFired();
		var cooldown = state.fireCooldown();
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
		var state = getState(stack);

		var isCoolingDown = state.fireCooldown() > world.getTime();
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
		var stats = getStats(stack);
		var state = getState(stack);

		var time = world.getTime() + tickDelta;

		var lastCommittedHeat = state.lastTotalHeat();
		var dissipationPerTick = stats.heat().drainSpeed();

		var dissipation = dissipationPerTick * (time - state.passiveCooldownStart());
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
		var state = getState(stack);

		if (state.isAiming())
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
		var state = getState(stack);

		if (!world.isClient() && user.getItemUseTime() > TOGGLE_AIMING_USE_TIME_TICKS && state.isAiming())
			setAiming(stack, false);

		return super.onStoppedUsing(stack, world, user, remainingUseTicks);
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand)
	{
		var stack = user.getStackInHand(hand);
		var state = getState(stack);

		if (!world.isClient())
		{
			setAiming(stack, !state.isAiming());

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

		var state = getState(itemStack);
		var stats = getStats(itemStack);

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

		var timestamp = world.getTime();
		state = state.withLastFired(timestamp)
		             .withPassiveCooldownStart(timestamp + stats.heat().passiveCooldownDelay())
		             .withFireCooldown(world.getTime() + stats.automaticRepeatDelay());

		var totalHeat = getHeat(world, itemStack, 0);
		if (overchargeTimeRemaining(world, itemStack, 0) == 0)
			totalHeat += stats.heat().perRound();

		if (world instanceof ServerWorld serverWorld)
			fireBolt(user, serverWorld);

		if (totalHeat > stats.heat().capacity())
		{
			// overheat sound

			state = state.withLastVentingHeat(totalHeat + stats.heat().overheatPenalty())
			             .withCoolingMode(CoolingMode.OVERHEAT)
			             .withBurstBoltsRemaining(0);

			totalHeat = 0;
		}

		state = state.withHeat(timestamp, totalHeat);

		itemStack.set(STATE, state);

		return ActionResult.SUCCESS;
	}

	private static void fireBolt(LivingEntity user, ServerWorld serverWorld)
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
}
