package dev.pswg.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.pswg.Blasters;
import dev.pswg.attributes.AttributeUtil;
import dev.pswg.attributes.GalaxiesEntityAttributes;
import dev.pswg.codec.GalaxiesCodecs;
import dev.pswg.codecgenerator.*;
import dev.pswg.data.*;
import dev.pswg.entity.BlasterBoltEntity;
import dev.pswg.generated.codecs.IAttachmentsComponentCodec;
import dev.pswg.generated.codecs.ICoolingCodec;
import dev.pswg.generated.codecs.IStateComponentCodec;
import dev.pswg.generated.recordbuilders.IStateComponentBuilder;
import dev.pswg.interaction.IRecoilEntity;
import dev.pswg.math.GMath;
import dev.pswg.math.ModifierOperation;
import dev.pswg.math.RandomHelper;
import dev.pswg.mutablerecord.MutableRecord;
import dev.pswg.networking.GalaxiesPacketCodecs;
import dev.pswg.sound.BlasterSounds;
import dev.pswg.world.TickConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class BlasterItem extends Item implements ILeftClickUsable, IPrimaryActionHandler, IHandAnimationAware
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
		public static final StreamCodec<RegistryFriendlyByteBuf, CoolingMode> PACKET_CODEC = GalaxiesPacketCodecs.forEnum(CoolingMode.class);
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
	 * @param hud     The ID of the HUD renderer this blaster should display
	 * @param applied The attachments currently applied to the blaster
	 */
	@GenerateCodec
	public record AttachmentsComponent(
			Identifier hud,
			@UseCodec(
					customCodec = @CodecSource(source = GalaxiesCodecs.class, member = "IDENTIFIER_MAP"),
					customPacket = @CodecSource(source = GalaxiesPacketCodecs.class, member = "IDENTIFIER_MAP")
			)
			Map<Identifier, Identifier> applied
	) implements IAttachmentsComponentCodec
	{
		public static final AttachmentsComponent DEFAULT = new AttachmentsComponent(
				Blasters.DEFAULT_HUD,
				Map.of()
		);
	}

	/**
	 * Fractional bypass windows; absence is represented by the containing stats Optional.
	 */
	@GenerateCodec(strict = true)
	public record Cooling(
			@CodecRange(min = 0, max = 1) float primaryBypassTime,
			@CodecRange(min = 0, max = 1) float primaryBypassTolerance,
			@CodecRange(min = 0, max = 1) float secondaryBypassTime,
			@CodecRange(min = 0, max = 1) float secondaryBypassTolerance
	) implements ICoolingCodec
	{
		/**
		 * Keeps both complete windows within their normalized domain after scalar codec validation.
		 */
		public static final Codec<Cooling> CODEC = ICoolingCodec.CODEC.validate(
				value ->
						value.primaryBypassTime() - value.primaryBypassTolerance() >= 0
						&& value.primaryBypassTime() + value.primaryBypassTolerance() <= 1
						&& value.secondaryBypassTime() - value.secondaryBypassTolerance() >= 0
						&& value.secondaryBypassTime() + value.secondaryBypassTolerance() <= 1
						? DataResult.success(value)
						: DataResult.error(() -> "Cooling bypass windows must fit within 0..1")
		);

		/**
		 * Zero-valued cooling used when the cooling object is absent.
		 */
		public static final Cooling ZERO = new BlasterItem.Cooling(
				0,
				0,
				0,
				0
		);

		public static final Cooling DEFAULT = new Cooling(
				0.7f,
				0.1f,
				0.25f,
				0.05f
		);
	}

	/**
	 * The container for the mutable gameplay state of the blaster
	 *
	 * @param isAiming            Determines if the blaster is currently aiming-down-sights
	 * @param lastFired           The timestamp when the blaster was last fired. It is derived
	 *                            from the global timestamp {@link Level#getGameTime()}.
	 * @param fireCooldown        Determines the next world tick when the blaster is able to be
	 *                            fired again. It is derived from the global timestamp {@link Level#getGameTime()}
	 * @param cooldownStart       The timestamp when the blaster will begin, or has begun, cooling down. The type of cooldown is/will be determined by {@link StateComponent#coolingMode()}
	 * @param lastTotalHeat       The amount of heat the blaster contained the last time
	 *                            heat was added. To get the current amount of heat, taking
	 *                            into account cooling and other parameters, see {@link #getAccumulatedHeat(Level, ItemStack, float)}.
	 * @param lastVentingHeat     The amount of heat at the time of cooling start. Can be different
	 *                            from {@link StateComponent#lastTotalHeat()} if e.g. a heat penalty was applied
	 * @param coolingMode         The cooling mode of the blaster, if any
	 * @param burstBoltsRemaining The number of bolts remaining in this burst
	 * @param overchargeStart     The timestamp when the blaster began its overcharge perk
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
			int burstBoltsRemaining,
			long overchargeStart
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
				0,
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
	 * The attribute combinator that is applied to the {@link Attributes#MOVEMENT_SPEED}
	 * attribute in players when they are aiming-down-sights.
	 */
	protected static final AttributeModifier ATTR_MODIFIER_AIMING_SPEED_PENALTY_ENABLED = new AttributeModifier(
			Blasters.id("aiming_speed_penalty"),
			-0.5F,
			AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
	);

	/**
	 * The attribute combinator that is applied to the {@link GalaxiesEntityAttributes#FIELD_OF_VIEW_ZOOM}
	 * attribute in players when they are aiming-down-sights.
	 */
	protected static final AttributeModifier ATTR_MODIFIER_AIMING_FOV_ENABLED = new AttributeModifier(
			Blasters.id("aiming_zoom"),
			2,
			AttributeModifier.Operation.ADD_MULTIPLIED_BASE
	);

	/**
	 * The component that contains the datapack registrar ID of the blaster
	 */
	public static final DataComponentType<Identifier> ID = Registry.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			Blasters.id("id"),
			DataComponentType.<Identifier>builder().persistent(Identifier.CODEC).networkSynchronized(Identifier.STREAM_CODEC).build()
	);

	/**
	 * The component that contains the serial number of the blaster
	 */
	public static final DataComponentType<Long> SERIAL = Registry.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			Blasters.id("serial"),
			DataComponentType.<Long>builder().persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.LONG).build()
	);

	/**
	 * The component that contains the mutable gameplay state of the blaster
	 */
	private static final DataComponentType<StateComponent> STATE = Registry.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			Blasters.id("state"),
			DataComponentType.<StateComponent>builder().persistent(StateComponent.CODEC).networkSynchronized(StateComponent.PACKET_CODEC).build()
	);

	/**
	 * The component that contains the mutable attachments of the blaster
	 */
	private static final DataComponentType<AttachmentsComponent> ATTACHMENTS = Registry.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			Blasters.id("attachments"),
			DataComponentType.<AttachmentsComponent>builder().persistent(AttachmentsComponent.CODEC).networkSynchronized(AttachmentsComponent.PACKET_CODEC).build()
	);

	/**
	 * @return A new instance of the item settings for this item
	 */
	public static Properties createSettings()
	{
		return new Properties()
				.component(ID, MISSING_ID)
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
		stack.set(DataComponents.ITEM_NAME, Component.translatable(id.toLanguageKey()));
		stack.set(DataComponents.ITEM_MODEL, definition.stats().configuration().itemModel());

		stack.set(ID, id);
		var attachments = definition.attachments();
		stack.set(ATTACHMENTS, new AttachmentsComponent(attachments.hud(), attachments.defaults()));

		return stack;
	}

	public BlasterItem(Properties settings)
	{
		super(settings);
	}

	/**
	 * Gets the modern stats definition of the given blaster.
	 *
	 * @param world The world whose current blaster baked is queried
	 * @param stack The stack to query
	 *
	 * @return The blaster definition, if its ID is present in the current baked
	 */
	public static Optional<BlasterDatapackDefinition> getDefinition(Level world, ItemStack stack)
	{
		var id = stack.get(ID);
		if (id == null)
			return Optional.empty();

		return Optional.ofNullable(BlasterData.get(world).blasters().get(id));
	}

	/**
	 * Reads the modern stats for the given blaster.
	 *
	 * @param world The world whose current blaster baked is queried
	 * @param stack The stack to query
	 *
	 * @return The modern stats, if the stack's blaster ID exists in the baked
	 */
	public static Optional<BlasterStats> getStats(Level world, ItemStack stack)
	{
		return getDefinition(world, stack).map(BlasterDatapackDefinition::stats);
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
	 * Gets the currently applied attachment definitions that still resolve within this stack's current blaster baked.
	 * The result is keyed by equipped slot; stale IDs and definitions that no longer fit their slot are omitted.
	 *
	 * @param world The world whose current blaster baked is queried
	 * @param stack The stack to query
	 *
	 * @return Resolved active attachments, or empty if this blaster is absent from the baked
	 */
	public static Optional<Map<Identifier, BlasterAttachmentDefinition>> getActiveAttachments(Level world, ItemStack stack)
	{
		var id = stack.get(ID);
		if (id == null)
			return Optional.empty();

		var resolvedAttachments = BlasterData.get(world).resolvedAttachments(id);
		if (resolvedAttachments.isEmpty())
			return Optional.empty();

		var activeAttachments = new HashMap<Identifier, BlasterAttachmentDefinition>();
		for (var entry : getAttachments(stack).applied().entrySet())
		{
			var definition = resolvedAttachments.orElseThrow().get(entry.getValue());
			if (definition != null && definition.slots().contains(entry.getKey()))
				activeAttachments.put(entry.getKey(), definition);
		}

		return Optional.of(Map.copyOf(activeAttachments));
	}

	/**
	 * Temp scale until the stat modifier evaluation is implemented.
	 */
	private static float getTempRecoilScale(Level world, ItemStack stack)
	{
		var scale = 1.0F;
		for (var attachment : getActiveAttachments(world, stack).orElse(Map.of()).values())
		{
			for (var modifier : attachment.modifiers())
			{
				if (
						modifier.function() == BlasterStatFunction.RECOIL_MULTIPLIER
						&& modifier.operation() == ModifierOperation.MULTIPLY_TOTAL
						&& modifier.modifierCondition().equals(BlasterAttachmentDefinition.ModifierCondition.UNCONDITIONAL)
				)
					scale *= modifier.value();
			}
		}
		return scale;
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
		stack.update(STATE, StateComponent.DEFAULT, stateOperator);
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

		var attrs = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);

		if (aiming)
		{
			attrs = attrs.withModifierAdded(Attributes.MOVEMENT_SPEED, ATTR_MODIFIER_AIMING_SPEED_PENALTY_ENABLED, EquipmentSlotGroup.HAND);
			attrs = attrs.withModifierAdded(GalaxiesEntityAttributes.FIELD_OF_VIEW_ZOOM, ATTR_MODIFIER_AIMING_FOV_ENABLED, EquipmentSlotGroup.HAND);
		}
		else
		{
			attrs = AttributeUtil.without(attrs, Attributes.MOVEMENT_SPEED, ATTR_MODIFIER_AIMING_SPEED_PENALTY_ENABLED);
			attrs = AttributeUtil.without(attrs, GalaxiesEntityAttributes.FIELD_OF_VIEW_ZOOM, ATTR_MODIFIER_AIMING_FOV_ENABLED);
		}

		stack.set(DataComponents.ATTRIBUTE_MODIFIERS, attrs);
	}

	/**
	 * If currently waiting to be able to fire again, gets the current
	 * progress [0,1) of the cooldown process
	 *
	 * @param world     The world to which the stack's timestamps are referenced
	 * @param stack     The stack to query
	 * @param tickDelta The partial tick to evaluate at
	 *
	 * @return A float [0,1) if currently waiting to be able to fire, empty otherwise
	 */
	public static Optional<Float> getFireCooldownProgress(Level world, ItemStack stack, float tickDelta)
	{
		var state = getState(stack);

		var lastFired = state.lastFired();
		var cooldown = state.fireCooldown();
		var time = world.getGameTime() + tickDelta;

		if (cooldown <= lastFired || cooldown < time)
			return Optional.empty();

		var cooldownLength = cooldown - lastFired;
		var cooldownProgress = (time - lastFired) / cooldownLength;
		return Optional.of(cooldownProgress);
	}

	/**
	 * If currently venting heat, gets the current bypass segment that the
	 * cooldown cursor is intersecting.
	 *
	 * @param world     The world to which the stack's timestamps are referenced
	 * @param stack     The stack to query
	 * @param tickDelta The partial tick to evaluate at
	 *
	 * @return A CoolingBypass if currently intersecting one, empty otherwise
	 */
	public static Optional<CoolingBypass> getCoolingBypass(Level world, ItemStack stack, float tickDelta)
	{
		var state = getState(stack);
		if (!state.coolingMode.canBypass())
			return Optional.empty();

		var optionalStats = getStats(world, stack);
		if (optionalStats.isEmpty())
			return Optional.empty();

		var stats = optionalStats.get();
		if (stats.cooling().isEmpty())
			return Optional.empty();

		var potentialVentingHeat = getVentingHeat(world, stack, tickDelta);
		if (potentialVentingHeat.isEmpty())
			return Optional.empty();

		var lastVentingHeat = state.lastVentingHeat();
		if (lastVentingHeat <= 0)
			return Optional.empty();

		var ventingHeat = potentialVentingHeat.get() / lastVentingHeat;

		var attachments = getAttachments(stack);

		var primaryBypassTime = stats.cooling().orElseThrow().primaryBypassTime();
		var primaryBypassTolerance = getScaledPrimaryBypassTolerance(stats, attachments);
		if (Math.abs(ventingHeat - primaryBypassTime) <= primaryBypassTolerance)
			return Optional.of(CoolingBypass.PRIMARY);

		var secondaryBypassTime = stats.cooling().orElseThrow().secondaryBypassTime();
		var secondaryBypassTolerance = getScaledSecondaryBypassTolerance(stats, attachments);
		if (Math.abs(ventingHeat - secondaryBypassTime) <= secondaryBypassTolerance)
			return Optional.of(CoolingBypass.SECONDARY);

		return Optional.empty();
	}

	/**
	 * Determines if the blaster is currently able to be fired based on
	 * the blaster's own properties (e.g., ignoring player eligibility)
	 *
	 * @param world The world to which the stack's timestamps are referenced
	 * @param user  The entity that is requesting to fire the blaster
	 * @param stack The stack to query
	 *
	 * @return True if the blaster can be fired, false otherwise
	 */
	public static boolean canFire(Level world, LivingEntity user, ItemStack stack)
	{
		if (getDefinition(world, stack).isEmpty())
			return false;

		var state = getState(stack);

		var isWaitingToFire = state.fireCooldown() >= world.getGameTime();
		if (isWaitingToFire)
			return false;

		// TODO: other checks (e.g. quickdraw delay)

		return true;
	}

	/**
	 * If currently overcharged, gets the current proportion [0,1] of the bonus remaining
	 *
	 * @param world     The world to which the stack's timestamps are referenced
	 * @param stack     The stack to query
	 * @param tickDelta The partial tick to evaluate at
	 *
	 * @return The current remaining overcharge proportion
	 */
	public static Optional<Float> getOverchargeTimeRemaining(Level world, ItemStack stack, float tickDelta)
	{
		var state = getState(stack);

		var optionalStats = getStats(world, stack);
		if (optionalStats.isEmpty())
			return Optional.empty();

		var stats = optionalStats.get();
		if (stats.heat().overchargeBonus() <= 0)
			return Optional.empty();

		var overchargeStart = state.overchargeStart();
		var overchargeLength = stats.heat().overchargeBonus();
		var time = world.getGameTime() + tickDelta;

		if (time > overchargeStart + overchargeLength)
			return Optional.empty();

		if (time < overchargeStart)
			return Optional.of(1f);

		var overchargeProgress = (time - overchargeStart) / overchargeLength;
		return Optional.of(1 - overchargeProgress);
	}

	/**
	 * Calculates the current accumulated heat of the blaster based on the dissipation rate and the time passed since the last shot.
	 *
	 * @param world     The world to which the stack's timestamps are referenced
	 * @param stack     The stack to query
	 * @param tickDelta The partial tick to evaluate at
	 *
	 * @return The current heat of the blaster
	 */
	public static Optional<Float> getAccumulatedHeat(Level world, ItemStack stack, float tickDelta)
	{
		var optionalStats = getStats(world, stack);
		if (optionalStats.isEmpty())
			return Optional.empty();

		var stats = optionalStats.get();
		if (stats.heat().capacity() <= 0)
			return Optional.of(0f);

		var state = getState(stack);
		if (state.coolingMode() != CoolingMode.PASSIVE)
			return Optional.empty();

		var attachments = getAttachments(stack);

		var time = world.getGameTime() + tickDelta;

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
	 * @param world     The world to which the stack's timestamps are referenced
	 * @param stack     The stack to query
	 * @param tickDelta The partial tick to evaluate at
	 *
	 * @return The current heat of the blaster
	 */
	public static Optional<Float> getVentingHeat(Level world, ItemStack stack, float tickDelta)
	{
		var optionalStats = getStats(world, stack);
		if (optionalStats.isEmpty())
			return Optional.empty();

		var stats = optionalStats.get();
		if (stats.heat().capacity() <= 0)
			return Optional.empty();

		var state = getState(stack);
		if (state.coolingMode() == CoolingMode.PASSIVE)
			return Optional.empty();
		if (state.lastVentingHeat() <= 0)
			return Optional.empty();

		var attachments = getAttachments(stack);

		var time = world.getGameTime() + tickDelta;

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
	 * @param world     The world to which the stack's timestamps are referenced
	 * @param stack     The stack to query
	 * @param tickDelta The partial tick to evaluate at
	 *
	 * @return The cooling status of the blaster, including its current cooling mode and total accumulated or venting heat
	 */
	public static CoolingStatus getCoolingStatus(Level world, ItemStack stack, float tickDelta)
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
	private static int getScaledAutoRepeatDelay(BlasterStats stats, AttachmentsComponent attachments)
	{
		// TODO: attachment mutations
		return stats.automaticRepeatDelay();
	}

	private static float getScaledPrimaryBypassTolerance(BlasterStats stats, AttachmentsComponent attachments)
	{
		// TODO: attachment mutations
		return stats.cooling().orElseThrow().primaryBypassTolerance();
	}

	private static float getScaledSecondaryBypassTolerance(BlasterStats stats, AttachmentsComponent attachments)
	{
		// TODO: attachment mutations
		return stats.cooling().orElseThrow().secondaryBypassTolerance();
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
	private static int getScaledPassiveCooldownDelay(BlasterStats stats, AttachmentsComponent attachments)
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
	private static float getScaledHeatDrainSpeed(BlasterStats stats, AttachmentsComponent attachments)
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
	private static float getScaledOverheatDrainSpeed(BlasterStats stats, AttachmentsComponent attachments)
	{
		// TODO: attachment mutations
		return stats.heat().overheatDrainSpeed();
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel world, Entity entity, @Nullable EquipmentSlot slot)
	{
		// If the stack does not have a serial number, assign one
		if (stack.get(SERIAL) == null)
			stack.set(SERIAL, world.getRandom().nextLong());
	}

	@Override
	public Optional<Boolean> shouldSkipHandAnimationOnSwap(ItemStack from, ItemStack to)
	{
		var serialA = from.get(SERIAL);
		var serialB = to.get(SERIAL);

		if (serialA == null || serialB == null)
			return Optional.empty();

		return Optional.of(serialA == (long)serialB);
	}

	@Override
	public boolean canDestroyBlock(ItemStack stack, BlockState state, Level world, BlockPos pos, LivingEntity user)
	{
		return false;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity user)
	{
		var state = getState(stack);

		if (state.isAiming())
			return TickConstants.ONE_HOUR;

		return super.getUseDuration(stack, user);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user)
	{
		releaseUsing(stack, world, user, 0);

		return stack;
	}

	@Override
	public ItemUseAnimation getUseAnimation(ItemStack stack)
	{
		return ItemUseAnimation.NONE;
	}

	@Override
	public boolean releaseUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks)
	{
		var state = getState(stack);

		if (!world.isClientSide() && user.getTicksUsingItem() > TOGGLE_AIMING_USE_TIME_TICKS && state.isAiming())
			setAiming(stack, false);

		return super.releaseUsing(stack, world, user, remainingUseTicks);
	}

	@Override
	public InteractionResult use(Level world, Player user, InteractionHand hand)
	{
		var stack = user.getItemInHand(hand);
		if (getDefinition(world, stack).isEmpty())
			return InteractionResult.FAIL;

		var state = getState(stack);

		if (!world.isClientSide())
		{
			setAiming(stack, !state.isAiming());

			// this is required to "start using" the item instead of
			// immediately consuming it.
			user.startUsingItem(hand);

			return InteractionResult.CONSUME;
		}

		return InteractionResult.FAIL;
	}

	@Override
	public InteractionResult useLeft(Level world, LivingEntity user, InteractionHand hand, boolean repeatEvent)
	{
		// TODO: manual reload
		// TODO: dryfire sound when no ammunition

		ItemStack itemStack = user.getItemInHand(hand);
		if (getDefinition(world, itemStack).isEmpty())
			return InteractionResult.PASS;

		if (!canFire(world, user, itemStack))
			return InteractionResult.PASS;

		var state = getState(itemStack);
		var attachments = getAttachments(itemStack);

		var optionalStats = getStats(world, itemStack);
		if (optionalStats.isEmpty())
		{
			Blasters.LOGGER.warn("Blaster stats not found for blaster {}", itemStack);
			return InteractionResult.FAIL;
		}

		var stats = optionalStats.get();

		var timestamp = world.getGameTime();

		var coolingStatus = getCoolingStatus(world, itemStack, 0);
		var heatEnabled = stats.heat().capacity() > 0;

		if (coolingStatus.coolingMode() == CoolingMode.PASSIVE && state.coolingMode() != CoolingMode.PASSIVE)
			state = state.withCooling(CoolingMode.PASSIVE, timestamp);

		if (state.coolingMode().isCooling())
		{
			if (!state.coolingMode().canBypass() || repeatEvent)
			{
				itemStack.set(STATE, state);
				return InteractionResult.FAIL;
			}

			var bypass = getCoolingBypass(world, itemStack, 0);
			if (bypass.isEmpty())
			{
				world.playSound(
						user,
						user.getX(),
						user.getY(),
						user.getZ(),
						BlasterSounds.BYPASS_FAILED,
						SoundSource.PLAYERS,
						1,
						RandomHelper.floatBetween(world.getRandom(), 0.9f, 1.1f)
				);

				if (world.isClientSide())
				{
					itemStack.set(STATE, state);
					return InteractionResult.FAIL;
				}

				itemStack.set(STATE, state.withCoolingMode(CoolingMode.FAILED_OVERCHARGE));
				return InteractionResult.CONSUME;
			}
			else if (bypass.get() == CoolingBypass.PRIMARY)
			{
				world.playSound(
						user,
						user.getX(),
						user.getY(),
						user.getZ(),
						BlasterSounds.BYPASS_PRIMARY,
						SoundSource.PLAYERS,
						1,
						RandomHelper.floatBetween(world.getRandom(), 0.9f, 1.1f)
				);

				if (world.isClientSide())
				{
					itemStack.set(STATE, state);
					return InteractionResult.FAIL;
				}

				itemStack.set(
						STATE,
						state.withLastTotalHeat(0)
						     .withCooling(CoolingMode.PASSIVE, timestamp)
				);
				return InteractionResult.CONSUME;
			}
			else if (bypass.get() == CoolingBypass.SECONDARY)
			{
				// TODO: overcharge end sound

				world.playSound(
						user,
						user.getX(),
						user.getY(),
						user.getZ(),
						BlasterSounds.BYPASS_SECONDARY,
						SoundSource.PLAYERS,
						1,
						RandomHelper.floatBetween(world.getRandom(), 0.9f, 1.1f)
				);

				if (world.isClientSide())
				{
					itemStack.set(STATE, state);
					return InteractionResult.FAIL;
				}

				itemStack.set(
						STATE,
						state.withLastTotalHeat(0)
						     .withOverchargeStart(timestamp)
						     .withCooling(CoolingMode.PASSIVE, timestamp)
				);
				return InteractionResult.CONSUME;
			}
		}

		state = state.withLastFired(timestamp)
		             .withCooling(CoolingMode.PASSIVE, timestamp + getScaledPassiveCooldownDelay(stats, attachments))
		             .withFireCooldown(timestamp + getScaledAutoRepeatDelay(stats, attachments));

		var totalHeat = coolingStatus.totalHeat();

		if (heatEnabled && getOverchargeTimeRemaining(world, itemStack, 0).isEmpty())
			totalHeat += stats.heat().perRound();

		if (world instanceof ServerLevel serverWorld)
		{
			fireBolt(user, serverWorld);

			// TODO: fixed recoil mean/std pattern for first n shots

			var recoilScale = getTempRecoilScale(world, itemStack);

			var recoil = new Vector3f(
					-(float)RandomHelper.nextGaussian(world.getRandom(), 3.6, 0.2),
					-(float)RandomHelper.nextGaussian(world.getRandom(), -0.2, 0.2),
					0
			);

			if (user instanceof IRecoilEntity recoilEntity)
				recoilEntity.pswg$addRecoilVelocity(recoil.mul(recoilScale));
		}

		if (user instanceof IRecoilEntity recoilEntity)
			recoilEntity.pswg$setRecoilTime(timestamp);

		if (stats.fireSound().isPresent())
		{
			world.playSound(
					user,
					user.getX(),
					user.getY(),
					user.getZ(),
					Holder.direct(SoundEvent.createVariableRangeEvent(stats.fireSound().orElseThrow())),
					SoundSource.NEUTRAL,
					1,
					RandomHelper.floatBetween(world.getRandom(), 0.9f, 1.1f)
			);
		}

		if (heatEnabled && totalHeat > stats.heat().capacity())
		{
			world.playSound(
					user,
					user.getX(),
					user.getY(),
					user.getZ(),
					BlasterSounds.OVERHEAT,
					SoundSource.PLAYERS,
					1,
					RandomHelper.floatBetween(world.getRandom(), 0.9f, 1.1f)
			);

			state = state.withLastVentingHeat(totalHeat + stats.heat().overheatPenalty())
			             .withCooling(CoolingMode.OVERHEAT, timestamp)
			             .withBurstBoltsRemaining(0);

			totalHeat = 0;
		}

		state = state.withLastTotalHeat(totalHeat);

		itemStack.set(STATE, state);

		return InteractionResult.CONSUME;
	}

	@Override
	public ItemStack invokePrimaryAction(ItemStack stack, Level world, LivingEntity user)
	{
		var optionalStats = getStats(world, stack);
		if (optionalStats.isEmpty())
			return stack;

		var heat = optionalStats.orElseThrow().heat();
		if (heat.capacity() <= 0 || heat.overheatDrainSpeed() <= 0)
			return stack;

		var timestamp = world.getGameTime();

		var coolingStatus = getCoolingStatus(world, stack, 0);
		if (coolingStatus.coolingMode().isCooling())
			return stack;
		if (coolingStatus.totalHeat() <= 0)
			return stack;

		var state = getState(stack);
		stack.set(STATE,
		          state.withLastVentingHeat(coolingStatus.totalHeat())
		               .withCooling(CoolingMode.REQUESTED_BYPASS, timestamp)
		               .withBurstBoltsRemaining(0)
		);

		world.playSound(
				user,
				user.getX(),
				user.getY(),
				user.getZ(),
				BlasterSounds.VENT,
				SoundSource.PLAYERS,
				1,
				RandomHelper.floatBetween(world.getRandom(), 0.9f, 1.1f)
		);

		return stack;
	}

	private static void fireBolt(LivingEntity user, ServerLevel serverWorld)
	{
		var projectile = new BlasterBoltEntity(Blasters.BLASTER_BOLT_ENTITY, serverWorld);

		// TODO: abstract into bolt-creating factory
		projectile.setPos(user.getX(), user.getY() + user.getEyeHeight(user.getPose()), user.getZ());

		var pitch = user.getXRot();
		var yaw = user.getYHeadRot();

		projectile.setDeltaMovement(GMath.getForwardVector(yaw, pitch).scale(5));
		projectile.absSnapRotationTo(yaw, pitch);

		//			Vec3d vec3d = user.getMovement();
		//			projectile.setVelocity(projectile.getVelocity().add(vec3d));

		serverWorld.addFreshEntity(projectile);
	}
}
