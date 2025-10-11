package dev.pswg.item;

import com.mojang.serialization.Codec;
import dev.pswg.Blasters;
import dev.pswg.attributes.AttributeUtil;
import dev.pswg.attributes.GalaxiesEntityAttributes;
import dev.pswg.codec.GalaxiesCodecs;
import dev.pswg.codecgenerator.GenerateCodec;
import dev.pswg.codecgenerator.SelfCodec;
import dev.pswg.data.BlasterDatapackDefinition;
import dev.pswg.entity.BlasterBoltEntity;
import dev.pswg.generated.codecs.*;
import dev.pswg.generated.recordbuilders.IStateComponentBuilder;
import dev.pswg.mutablerecord.MutableRecord;
import dev.pswg.networking.GalaxiesPacketCodecs;
import dev.pswg.world.TickConstants;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
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
		 * The blaster is, or will next be, cooling passively. Passive cooling
		 * always decreases accumulated heat (see {@link #getAccumulatedHeat})
		 * and not the venting heat (see {@link #getVentingHeat}).
		 */
		PASSIVE(false, false),
		/**
		 * The blaster is cooling due to an overheating event.
		 * This mode displays the bypass minigame.
		 */
		OVERHEAT(true, true),
		/**
		 * The blaster is cooling due to a request to manually
		 * vent the accumulated heat. This mode does not display
		 * the bypass minigame.
		 */
		REQUESTED_BYPASS(true, false),
		/**
		 * The blaster is cooling after a failed attempt at
		 * the bypass minigame. This mode does not display
		 * the bypass minigame.
		 */
		FAILED_OVERCHARGE(true, false);

		private final boolean isCooling;
		private final boolean canBypass;

		CoolingMode(boolean isCooling, boolean canBypass)
		{
			this.isCooling = isCooling;
			this.canBypass = canBypass;
		}

		public boolean isCooling()
		{
			return isCooling;
		}

		public boolean canBypass()
		{
			return canBypass;
		}

		public static final Codec<CoolingMode> CODEC = GalaxiesCodecs.forEnum(CoolingMode.class);
		public static final PacketCodec<RegistryByteBuf, CoolingMode> PACKET_CODEC = GalaxiesPacketCodecs.forEnum(CoolingMode.class);
	}

	/**
	 * Represents the cooling status of a blaster
	 *
	 * @param coolingMode The current cooling mode of the blaster.
	 * @param totalHeat   The total amount of heat accumulated in the blaster, passively or otherwise.
	 */
	public record CoolingStatus(CoolingMode coolingMode, float totalHeat)
	{
	}

	/**
	 * The available cooling bypass categories
	 */
	public enum CoolingBypass
	{
		/**
		 * The primary bypass, encountered first while cooling
		 * down. Easier to achieve, but offers less reward.
		 */
		PRIMARY,

		/**
		 * The secondary bypass, encountered second while cooling
		 * down. Typically the hardest to achieve, but offers the
		 * greatest reward.
		 */
		SECONDARY
	}

	/**
	 * Contains the infrequently-modified attachment data for the blaster
	 *
	 * @param hud The ID of the HUD renderer this blaster should display
	 */
	@GenerateCodec
	public record AttachmentsComponent(Identifier hud) implements IAttachmentsComponentCodec
	{
		public static final AttachmentsComponent DEFAULT = new AttachmentsComponent(
				Blasters.DEFAULT_HUD
		);
	}

	/**
	 * Contains stats related to blaster heating and cooling
	 *
	 * @param capacity             The maximum amount of heat units that may be accumulated before overheating.
	 * @param perRound             The amount of heat units accumulated per shot fired.
	 * @param drainSpeed           The amount of heat units removed from the blaster per tick while passively cooling
	 *                             or venting.
	 * @param overheatPenalty      The amount of "extra" heat units accumulated when the blaster overheats, effectively
	 *                             delaying the blaster from beginning to cool by {@code overheatPenalty / (overheatDrainSpeed * 20)} seconds.
	 * @param overheatDrainSpeed   The amount of heat units removed from the blaster per tick while venting due to
	 *                             an overheat.
	 * @param passiveCooldownDelay The amount of time, in ticks, after the most recent shot was fired before the
	 *                             blaster will begin to passively cool without venting.
	 * @param overchargeBonus      The amount of time, in ticks, the blaster stays in overcharge when the secondary
	 *                             bypass is triggered.
	 */
	@GenerateCodec
	public record Heat(
			int capacity,
			int perRound,
			float drainSpeed,
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

	@GenerateCodec
	public record Cooling(
			float primaryBypassTime,
			float primaryBypassTolerance,
			float secondaryBypassTime,
			float secondaryBypassTolerance
	) implements ICoolingCodec
	{
		public static final Cooling DEFAULT = new Cooling(
				0.7f,
				0.1f,
				0.25f,
				0.05f
		);
	}

	/**
	 * Contains the immutable, intrinsic stats of this particular
	 * variant of blaster
	 *
	 * @param damage               The damage, in hit points (half hearts) a single shot inflicts.
	 * @param range                The maximum distance, in blocks, a blaster can fire a bolt.
	 * @param automaticRepeatDelay The minimum time, in ticks, between two bolts firing during automatic fire.
	 * @param heat                 The heating and heat dissipation stats.
	 * @param cooling              The cooling bypass stats.
	 */
	@GenerateCodec
	public record StatsComponent(
			float damage,
			int range,
			int automaticRepeatDelay,
			@SelfCodec Heat heat,
			@SelfCodec Cooling cooling
	) implements IStatsComponentCodec
	{
		public static final StatsComponent DEFAULT = new StatsComponent(8, 48, 4, Heat.DEFAULT, Cooling.DEFAULT);
	}

	/**
	 * The container for the mutable gameplay state of the blaster
	 *
	 * @param isAiming            Determines if the blaster is currently aiming-down-sights
	 * @param lastFired           The timestamp when the blaster was last fired. It is derived
	 *                            from the global timestamp {@link World#getTime()}.
	 * @param fireCooldown        Determines the next world tick when the blaster is able to be
	 *                            fired again. It is derived from the global timestamp {@link World#getTime()}
	 * @param cooldownStart       The timestamp when the blaster will begin, or has begun, cooling down. The type of cooldown is/will be determined by {@link StateComponent#coolingMode()}
	 * @param lastTotalHeat       The amount of heat the blaster contained the last time
	 *                            heat was added. To get the current amount of heat, taking
	 *                            into account cooling and other parameters, see {@link #getAccumulatedHeat(World, ItemStack, float)}.
	 * @param lastVentingHeat     The amount of heat at the time of cooling start. Can be different
	 *                            from {@link StateComponent#lastTotalHeat()} if e.g. a heat penalty was applied
	 * @param coolingMode         The cooling mode of the blaster, if any
	 * @param burstBoltsRemaining The amount of bolts remaining in this burst
	 */
	@MutableRecord
	@GenerateCodec
	public record StateComponent(
			boolean isAiming,
			long lastFired,
			long fireCooldown,
			long cooldownStart,
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
				CoolingMode.PASSIVE,
				0
		);

		/**
		 * Creates a new StateComponent with the specified CoolingMode and timestamp for the cooldown start.
		 *
		 * @param mode      The new cooling mode of the blaster
		 * @param timestamp The timestamp when the cooling starts
		 *
		 * @return a new StateComponent with the updated cooling mode and cooldown start timestamp
		 */
		public StateComponent withCooling(CoolingMode mode, long timestamp)
		{
			return this.withCoolingMode(mode)
			           .withCooldownStart(timestamp);
		}
	}

	public static final Identifier MISSING_ID = Blasters.id("missingno");

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
	 * The component that contains the datapack registrar ID of the blaster
	 */
	public static final ComponentType<Identifier> ID = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Blasters.id("id"),
			ComponentType.<Identifier>builder().codec(Identifier.CODEC).packetCodec(Identifier.PACKET_CODEC).build()
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
	 * The component that contains the mutable attachments of the blaster
	 */
	private static final ComponentType<AttachmentsComponent> ATTACHMENTS = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Blasters.id("attachments"),
			ComponentType.<AttachmentsComponent>builder().codec(AttachmentsComponent.CODEC).packetCodec(AttachmentsComponent.PACKET_CODEC).build()
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
				.component(ID, MISSING_ID)
				.component(STATS, StatsComponent.DEFAULT)
				.component(ATTACHMENTS, AttachmentsComponent.DEFAULT)
				.component(STATE, StateComponent.DEFAULT);
	}

	/**
	 * Creates a new ItemStack that represents the given {@link BlasterDatapackDefinition}.
	 *
	 * @param id         The id of the blaster item.
	 * @param definition The definition that this stack should represent.
	 *
	 * @return A new stack configured with the given definition.
	 */
	public static ItemStack createStack(Identifier id, BlasterDatapackDefinition definition)
	{
		var stack = new ItemStack(Blasters.BLASTER_ITEM);

		// Set the item name to the model name by default
		stack.set(DataComponentTypes.ITEM_NAME, Text.translatable(id.toTranslationKey()));

		stack.set(ID, id);
		stack.set(STATS, definition.stats());
		stack.set(ATTACHMENTS, definition.attachments());

		return stack;
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
	 * Gets the attachments of the given blaster
	 *
	 * @param stack The stack to query
	 *
	 * @return The blaster's attachments
	 */
	public static AttachmentsComponent getAttachments(ItemStack stack)
	{
		return stack.getOrDefault(ATTACHMENTS, AttachmentsComponent.DEFAULT);
	}

	/**
	 * Gets the state of the given blaster
	 *
	 * @param stack The stack to query
	 *
	 * @return The blaster's mutable state
	 */
	public static StateComponent getState(ItemStack stack)
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

	public static Optional<CoolingBypass> getCoolingBypass(World world, ItemStack stack, float tickDelta)
	{
		var state = getState(stack);
		if (!state.coolingMode.canBypass())
			return Optional.empty();

		var stats = getStats(stack);
		var potentialVentingHeat = getVentingHeat(world, stack, tickDelta);
		if (potentialVentingHeat.isEmpty())
			return Optional.empty();

		var ventingHeat = potentialVentingHeat.get() / state.lastVentingHeat();

		var attachments = getAttachments(stack);

		var primaryBypassTime = stats.cooling().primaryBypassTime();
		var primaryBypassTolerance = getScaledPrimaryBypassTolerance(stats, attachments);
		if (Math.abs(ventingHeat - primaryBypassTime) <= primaryBypassTolerance)
			return Optional.of(CoolingBypass.PRIMARY);

		var secondaryBypassTime = stats.cooling().secondaryBypassTime();
		var secondaryBypassTolerance = getScaledSecondaryBypassTolerance(stats, attachments);
		if (Math.abs(ventingHeat - secondaryBypassTime) <= secondaryBypassTolerance)
			return Optional.of(CoolingBypass.SECONDARY);

		return Optional.empty();
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

		var isWaitingToFire = state.fireCooldown() > world.getTime();
		if (isWaitingToFire)
			return false;

		// TODO: other checks (e.g. quickdraw delay)

		return true;
	}

	/**
	 * Calculates the current accumulated heat of the blaster based on the dissipation rate and the time passed since the last shot.
	 *
	 * @param world     The world to the stack's timestamps are referenced
	 * @param stack     The stack to query
	 * @param tickDelta The partial tick to evaluate at
	 *
	 * @return The current heat of the blaster
	 */
	public static Optional<Float> getAccumulatedHeat(World world, ItemStack stack, float tickDelta)
	{
		var state = getState(stack);
		if (state.coolingMode() != CoolingMode.PASSIVE)
			return Optional.empty();

		var stats = getStats(stack);
		var attachments = getAttachments(stack);

		var time = world.getTime() + tickDelta;

		var lastCommittedHeat = state.lastTotalHeat();
		var dissipationPerTick = getScaledHeatDrainSpeed(stats, attachments);

		var dissipation = dissipationPerTick * (time - state.cooldownStart());
		if (dissipation > lastCommittedHeat)
			return Optional.empty();

		return Optional.of(Math.min(lastCommittedHeat - dissipation, lastCommittedHeat));
	}

	/**
	 * Calculates the current venting heat of the blaster based on the dissipation rate and the time passed since venting started.
	 *
	 * @param world     The world to the stack's timestamps are referenced
	 * @param stack     The stack to query
	 * @param tickDelta The partial tick to evaluate at
	 *
	 * @return The current heat of the blaster
	 */
	public static Optional<Float> getVentingHeat(World world, ItemStack stack, float tickDelta)
	{
		var state = getState(stack);
		if (state.coolingMode() == CoolingMode.PASSIVE)
			return Optional.empty();

		var stats = getStats(stack);
		var attachments = getAttachments(stack);

		var time = world.getTime() + tickDelta;

		var lastVentingHeat = state.lastVentingHeat();
		var dissipationPerTick = getScaledOverheatDrainSpeed(stats, attachments);

		var dissipation = dissipationPerTick * (time - state.cooldownStart());
		if (dissipation > lastVentingHeat)
			return Optional.empty();

		return Optional.of(Math.min(lastVentingHeat - dissipation, lastVentingHeat));
	}

	/**
	 * Determines the cooling status of a blaster, whether passively or actively cooling
	 *
	 * @param world     The world to the stack's timestamps are referenced
	 * @param stack     The stack to query
	 * @param tickDelta The partial tick to evaluate at
	 *
	 * @return The cooling status of the blaster, including its current cooling mode and total accumulated or venting heat
	 */
	public static CoolingStatus getCoolingStatus(World world, ItemStack stack, float tickDelta)
	{
		var state = getState(stack);
		return getVentingHeat(world, stack, tickDelta)
				.map(ventingHeat -> new CoolingStatus(state.coolingMode(), ventingHeat))
				.orElseGet(() -> new CoolingStatus(CoolingMode.PASSIVE, getAccumulatedHeat(world, stack, tickDelta).orElse(0f)));
	}

	/**
	 * Determines the auto-repeat delay of a blaster considering both it's base stats and its
	 * attachment modifiers.
	 *
	 * @param stats       The base blaster stats to consider
	 * @param attachments The blaster attachments to consider
	 *
	 * @return The minimum repeat interval, in ticks
	 */
	private static int getScaledAutoRepeatDelay(StatsComponent stats, AttachmentsComponent attachments)
	{
		// TODO: attachment mutations
		return stats.automaticRepeatDelay();
	}

	private static float getScaledPrimaryBypassTolerance(StatsComponent stats, AttachmentsComponent attachments)
	{
		// TODO: attachment mutations
		return stats.cooling().primaryBypassTolerance();
	}

	private static float getScaledSecondaryBypassTolerance(StatsComponent stats, AttachmentsComponent attachments)
	{
		// TODO: attachment mutations
		return stats.cooling().secondaryBypassTolerance();
	}

	/**
	 * Determines the passive cooldown delay of a blaster considering both it's base stats and its
	 * attachment modifiers.
	 *
	 * @param stats       The base blaster stats to consider
	 * @param attachments The blaster attachments to consider
	 *
	 * @return The delay before passive cooldown begins, in ticks
	 */
	private static int getScaledPassiveCooldownDelay(StatsComponent stats, AttachmentsComponent attachments)
	{
		// TODO: attachment mutations
		return stats.heat().passiveCooldownDelay();
	}

	/**
	 * Determines the passive heat drain speed of a blaster considering both it's base stats and its
	 * attachment modifiers.
	 *
	 * @param stats       The base blaster stats to consider
	 * @param attachments The blaster attachments to consider
	 *
	 * @return The passive drain speed, in units per tick
	 */
	private static float getScaledHeatDrainSpeed(StatsComponent stats, AttachmentsComponent attachments)
	{
		// TODO: attachment mutations
		return stats.heat().drainSpeed();
	}

	/**
	 * Determines the overheated heat drain speed of a blaster considering both it's base stats and its
	 * attachment modifiers.
	 *
	 * @param stats       The base blaster stats to consider
	 * @param attachments The blaster attachments to consider
	 *
	 * @return The overheated drain speed, in units per tick
	 */
	private static float getScaledOverheatDrainSpeed(StatsComponent stats, AttachmentsComponent attachments)
	{
		// TODO: attachment mutations
		return stats.heat().overheatDrainSpeed();
	}

	public static float overchargeTimeRemaining(World world, ItemStack itemStack, float tickDelta)
	{
		// TODO
		return 0;
	}

	@Override
	public boolean canMine(ItemStack stack, BlockState state, World world, BlockPos pos, LivingEntity user)
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
		var attachments = getAttachments(itemStack);
		var stats = getStats(itemStack);

		var timestamp = world.getTime();

		var coolingStatus = getCoolingStatus(world, itemStack, 0);

		if (coolingStatus.coolingMode() == CoolingMode.PASSIVE && state.coolingMode() != CoolingMode.PASSIVE)
			state = state.withCooling(CoolingMode.PASSIVE, timestamp);

		if (state.coolingMode().isCooling())
		{
			var isRepeatEvent = false; // TODO: value ultimately comes from #usageTickLeft
			if (world.isClient() || !state.coolingMode().canBypass() || isRepeatEvent)
			{
				itemStack.set(STATE, state);
				return ActionResult.FAIL;
			}

			var bypass = getCoolingBypass(world, itemStack, 0);
			if (bypass.isEmpty())
			{
				state = state.withCoolingMode(CoolingMode.FAILED_OVERCHARGE);

				// TODO: play sound - failed bypass

				itemStack.set(STATE, state);
				return ActionResult.SUCCESS;
			}
			else if (bypass.get() == CoolingBypass.PRIMARY)
			{
				state = state.withLastTotalHeat(0)
				             .withCooling(CoolingMode.PASSIVE, timestamp);

				// TODO: play sound - primary bypass

				itemStack.set(STATE, state);
				return ActionResult.SUCCESS;
			}
			else if (bypass.get() == CoolingBypass.SECONDARY)
			{
				state = state.withLastTotalHeat(0)
				             .withCooling(CoolingMode.PASSIVE, timestamp);

				// TODO: overcharge bonus

				// TODO: play sound - secondary bypass

				itemStack.set(STATE, state);
				return ActionResult.SUCCESS;
			}
		}

		state = state.withLastFired(timestamp)
		             .withCooling(CoolingMode.PASSIVE, timestamp + getScaledPassiveCooldownDelay(stats, attachments))
		             .withFireCooldown(timestamp + getScaledAutoRepeatDelay(stats, attachments));

		var totalHeat = coolingStatus.totalHeat();

		if (overchargeTimeRemaining(world, itemStack, 0) == 0)
			totalHeat += stats.heat().perRound();

		if (world instanceof ServerWorld serverWorld)
			fireBolt(user, serverWorld);

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

		if (totalHeat > stats.heat().capacity())
		{
			// TODO: play sound - overheat

			state = state.withLastVentingHeat(totalHeat + stats.heat().overheatPenalty())
			             .withCooling(CoolingMode.OVERHEAT, timestamp)
			             .withBurstBoltsRemaining(0);

			totalHeat = 0;
		}

		state = state.withLastTotalHeat(totalHeat);

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
