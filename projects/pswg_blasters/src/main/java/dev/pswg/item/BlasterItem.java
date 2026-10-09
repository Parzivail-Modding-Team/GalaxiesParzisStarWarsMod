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
import dev.pswg.entity.BlasterShot;
import dev.pswg.generated.codecs.IAttachmentsComponentCodec;
import dev.pswg.generated.codecs.ICoolingCodec;
import dev.pswg.generated.codecs.IStateComponentCodec;
import dev.pswg.generated.recordbuilders.IStateComponentBuilder;
import dev.pswg.interaction.IRecoilEntity;
import dev.pswg.interaction.BlasterActions;
import dev.pswg.item.component.StoredCharge;
import dev.pswg.math.RandomHelper;
import dev.pswg.mutablerecord.MutableRecord;
import dev.pswg.networking.GalaxiesPacketCodecs;
import dev.pswg.sound.BlasterSounds;
import dev.pswg.world.TickConstants;
import dev.pswg.world.GameTime;
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
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class BlasterItem extends Item implements ILeftClickUsable, IPrimaryActionHandler, IHandAnimationAware
{
	/**
	 * Shot interaction result.
	 */
	public enum ShotResult
	{
		/**
		 * The interaction fired a shot.
		 */
		FIRED,

		/**
		 * A charge press passed readiness checks without spending shot resources.
		 */
		READY,

		/**
		 * The interaction fed the minigame.
		 */
		COOLING_HANDLED,

		/**
		 * The interaction failed.
		 */
		BLOCKED
	}
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
	 *                            from the saved Overworld timestamp {@link GameTime#now(Level)}.
	 * @param fireCooldown        Determines the next world tick when the blaster is able to be
	 *                            fired again. It is derived from the saved Overworld timestamp {@link GameTime#now(Level)}
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
	 * Persistent mode preference.
	 */
	private static final DataComponentType<Identifier> SELECTED_MODE = Registry.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			Blasters.id("selected_mode"),
			DataComponentType.<Identifier>builder().persistent(Identifier.CODEC).networkSynchronized(Identifier.STREAM_CODEC).build()
	);

	/**
	 * If the blaster is deployed.
	 */
	private static final DataComponentType<Boolean> DEPLOYED = Registry.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			Blasters.id("deployed"),
			DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build()
	);

	/**
	 * If the blaster is folded.
	 */
	private static final DataComponentType<Boolean> FOLDED = Registry.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			Blasters.id("folded"),
			DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build()
	);
	/**
	 * The number of loaded rounds.
	 */
	private static final DataComponentType<Integer> LOADED_ROUNDS = Registry.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			Blasters.id("loaded_rounds"),
			DataComponentType.<Integer>builder().persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT).build()
	);
	/**
	 * The amount of loaded charge
	 */
	private static final DataComponentType<StoredCharge> LOADED_CHARGE = Registry.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			Blasters.id("loaded_charge"),
			DataComponentType.<StoredCharge>builder().persistent(StoredCharge.CODEC).networkSynchronized(StoredCharge.PACKET_CODEC).build()
	);
	/**
	 * Field conversion data.
	 */
	private static final DataComponentType<BlasterFieldConversion> FIELD_CONVERSION = Registry.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			Blasters.id("field_conversion"),
			DataComponentType.<BlasterFieldConversion>builder().persistent(BlasterFieldConversion.CODEC).networkSynchronized(BlasterFieldConversion.PACKET_CODEC).build()
	);

	/**
	 * @return A new instance of the item settings for this item
	 */
	public static Properties createSettings()
	{
		return new Properties()
				.stacksTo(1)
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
		stack.set(SELECTED_MODE, definition.stats().modes().defaultMode());
		var attachments = definition.attachments();
		stack.set(ATTACHMENTS, new AttachmentsComponent(attachments.hud(), attachments.defaults()));
		initializeLoadedAmmo(stack, definition.stats().ammo());

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
		if (!stack.is(Blasters.BLASTER_ITEM))
			return Optional.empty();

		var id = stack.get(ID);
		if (id == null)
			return Optional.empty();

		return Optional.ofNullable(BlasterData.get(world).blasters().get(id));
	}

	/**
	 * Reads shared effective stats for the given blaster and its current item context.
	 *
	 * @param world The world whose current blaster baked is queried
	 * @param stack The stack to query
	 *
	 * @return The modern stats, if the stack's blaster ID exists in the baked
	 */
	public static Optional<BlasterStats> getStats(Level world, ItemStack stack)
	{
		return getEffectiveStats(world, stack).map(BlasterEffectiveStats::stats);
	}

	/**
	 * Evaluates current item's stats.
	 */
	public static Optional<BlasterEffectiveStats> getEffectiveStats(Level world, ItemStack stack)
	{
		var state = getState(stack);
		var stance = state.coolingMode().isCooling() ? BlasterStanceProfile.WeaponState.VENTING
		                                             : state.isAiming() ? BlasterStanceProfile.WeaponState.ADS
		                                                                : isDeployed(stack) ? BlasterStanceProfile.WeaponState.DEPLOYED
		                                                                                    : isFolded(stack) ? BlasterStanceProfile.WeaponState.FOLDED : BlasterStanceProfile.WeaponState.HIP;

		return getEffectiveStats(world, stack, new BlasterEffectiveStats.Context(stance, isDeployed(stack), state.isAiming(), isFolded(stack)));
	}

	/**
	 * Shared query for a caller's captured runtime state or a UI's explicit preview context.
	 */
	public static Optional<BlasterEffectiveStats> getEffectiveStats(Level world, ItemStack stack, BlasterEffectiveStats.Context context)
	{
		return getLoadout(world, stack).map(loadout -> BlasterEffectiveStats.evaluate(loadout, context));
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
		return getLoadout(world, stack).map(BlasterLoadout::activeAttachments);
	}

	/**
	 * Reads one current definition snapshot and resolves the stack's component-owned loadout.
	 */
	public static Optional<BlasterLoadout> getLoadout(Level world, ItemStack stack)
	{
		if (!stack.is(Blasters.BLASTER_ITEM))
			return Optional.empty();

		var id = stack.get(ID);
		if (id == null)
			return Optional.empty();

		var snapshot = BlasterData.get(world);
		var definition = snapshot.blasters().get(id);
		if (definition == null)
			return Optional.empty();

		return snapshot.resolvedAttachments(id).map(
				options ->
				{
					var applied = getAttachments(stack).applied();
					var preference = Optional.ofNullable(stack.get(SELECTED_MODE));
					var source = BlasterLoadout.resolve(definition, options, applied, preference);
					return Optional.ofNullable(stack.get(FIELD_CONVERSION)).flatMap(conversion -> conversion.resolve(
							snapshot,
							source,
							applied,
							preference,
							world == null ? 0 : GameTime.now(world),
							getLoadedCharge(stack)
					)).orElse(source);
				}
		);
	}

	/**
	 * Selects an available base/granted mode on the logical server, leaving the stack unchanged on rejection.
	 */
	public static boolean selectMode(ServerLevel world, ItemStack stack, Identifier modeId)
	{
		var loadout = getLoadout(world, stack);
		if (loadout.isEmpty() || loadout.orElseThrow().findMode(modeId).isEmpty())
			return false;

		stack.set(SELECTED_MODE, modeId);
		refreshState(world, stack);
		refreshAimingZoom(world, stack);

		return true;
	}

	/**
	 * Gets if the blaster is deployed.
	 */
	public static boolean isDeployed(ItemStack stack)
	{
		return stack.getOrDefault(DEPLOYED, false);
	}

	/**
	 * Gets if the blaster is folded.
	 */
	public static boolean isFolded(ItemStack stack)
	{
		return stack.getOrDefault(FOLDED, false);
	}

	/**
	 * Sets if the blaster is deployed.
	 */
	public static boolean setDeployed(ServerLevel world, ItemStack stack, boolean deployed)
	{
		if (getLoadout(world, stack).isEmpty())
			return false;

		stack.set(DEPLOYED, deployed);
		refreshAimingZoom(world, stack);

		return true;
	}

	/**
	 * Sets if the blaster is folded.
	 */
	public static boolean setFolded(ServerLevel world, ItemStack stack, boolean folded)
	{
		if (getLoadout(world, stack).isEmpty())
			return false;

		stack.set(FOLDED, folded);
		refreshAimingZoom(world, stack);

		return true;
	}

	/**
	 * Gets the number of loaded rounds.
	 */
	public static int getLoadedRounds(ItemStack stack)
	{
		return stack.getOrDefault(LOADED_ROUNDS, 0);
	}

	/**
	 * Gets the amount of stored charge.
	 */
	public static Optional<StoredCharge> getLoadedCharge(ItemStack stack)
	{
		return Optional.ofNullable(stack.get(LOADED_CHARGE));
	}

	/**
	 * Gets the amount of ammo the blaster is capable of holding.
	 */
	public static int getAmmoCapacity(BlasterStats.Ammo ammo)
	{
		return switch (ammo.feed())
		{
			case BlasterStats.PerShotFeed ignored -> 0;
			case BlasterStats.MagazineFeed magazine -> magazine.magazineSize();
			case BlasterStats.ChargeStoreFeed charge -> charge.chargeCapacityUnits();
		};
	}

	/**
	 * Gets the amount of ammo the blaster currently holds.
	 */
	public static int getLoadedAmmo(ItemStack stack, BlasterStats.Ammo ammo)
	{
		return switch (ammo.feed())
		{
			case BlasterStats.PerShotFeed ignored -> 0;
			case BlasterStats.MagazineFeed ignored -> getLoadedRounds(stack);
			case BlasterStats.ChargeStoreFeed ignored -> getLoadedCharge(stack).map(StoredCharge::current).orElse(0);
		};
	}

	/**
	 * Sets the amount of rounds the blaster currently holds.
	 */
	public static boolean setLoadedRounds(ServerLevel world, ItemStack stack, int rounds)
	{
		var loadout = getLoadout(world, stack);
		if (loadout.isEmpty() || !(loadout.orElseThrow().definition().stats().ammo().feed() instanceof BlasterStats.MagazineFeed magazine)
		    || rounds < 0 || (rounds > magazine.magazineSize() && rounds > getLoadedRounds(stack)))
			return false;

		stack.set(LOADED_ROUNDS, rounds);
		refreshState(world, stack);

		return true;
	}

	/**
	 * Sets the amount of charge the blaster currently holds.
	 */
	public static boolean setLoadedCharge(ServerLevel world, ItemStack stack, int units)
	{
		var loadout = getLoadout(world, stack);
		if (loadout.isEmpty() || !(loadout.orElseThrow().definition().stats().ammo().feed() instanceof BlasterStats.ChargeStoreFeed charge)
		    || units < 0 || (units > charge.chargeCapacityUnits() && units > getLoadedCharge(stack).map(StoredCharge::current).orElse(0)))
			return false;

		stack.set(LOADED_CHARGE, new StoredCharge(units, Math.max(units, charge.chargeCapacityUnits())));
		refreshState(world, stack);

		return true;
	}

	/**
	 * Sets the applied attachments on the blaster
	 */
	public static boolean setAttachments(ServerLevel world, ItemStack stack, Map<Identifier, Identifier> applied)
	{
		var loadout = getLoadout(world, stack);
		if (loadout.isEmpty())
			return false;

		var snapshot = BlasterData.get(world);
		var definition = loadout.orElseThrow().definition();
		var options = definition.attachments().resolve(snapshot.attachments());

		if (!BlasterFieldConversion.canAttachmentsFit(options, applied))
			return false;

		stack.set(ATTACHMENTS, new AttachmentsComponent(definition.attachments().hud(), Map.copyOf(applied)));
		refreshState(world, stack);
		refreshAimingZoom(world, stack);

		return true;
	}

	/**
	 * Returns whether the loadout has an installed modifier whose result depends on folding or deployment.
	 */
	public static boolean hasContextualAttachmentModifier(BlasterLoadout loadout, boolean folded)
	{
		for (var attachment : loadout.activeOptions().values())
		{
			for (var modifier : attachment.modifiers())
			{
				if (folded ? modifier.modifierCondition().folded().isPresent() : modifier.modifierCondition().deployed().isPresent())
					return true;
			}
		}

		return false;
	}

	/**
	 * Returns whether this feed can currently be reloaded through the reload action.
	 */
	public static boolean canReload(BlasterStats stats)
	{
		return switch (stats.ammo().feed())
		{
			case BlasterStats.PerShotFeed ignored -> false;
			case BlasterStats.MagazineFeed magazine -> magazine.reloadTicks() > 0;
			case BlasterStats.ChargeStoreFeed charge -> charge.reloadTicks() > 0;
		};
	}

	/**
	 * Activates a field conversion.
	 */
	public static boolean activateConversion(ServerLevel world, ItemStack stack, Identifier optionId)
	{
		var base = getDefinition(world, stack);
		if (base.isEmpty())
			return false;

		var snapshot = BlasterData.get(world);
		var options = snapshot.resolvedAttachments(stack.get(ID)).orElseThrow();
		var applied = getAttachments(stack).applied();
		var preferred = Optional.ofNullable(stack.get(SELECTED_MODE));
		var source = BlasterLoadout.resolve(base.orElseThrow(), options, applied, preferred);
		var selection = new BlasterFieldConversion(optionId, GameTime.now(world));
		var target = selection.resolve(snapshot, source, applied, preferred, GameTime.now(world), getLoadedCharge(stack));

		if (target.isEmpty())
			return false;

		var targetAmmo = target.orElseThrow().definition().stats().ammo();
		var currentAmmo = getLoadout(world, stack).orElseThrow().definition().stats().ammo();

		if (!BlasterFieldConversion.isAmmoCompatible(currentAmmo, targetAmmo, getLoadedRounds(stack), getLoadedCharge(stack).map(StoredCharge::current).orElse(0))
		    || getLoadedAmmo(stack, targetAmmo) > getAmmoCapacity(targetAmmo))
			return false;

		stack.set(FIELD_CONVERSION, selection);
		refreshState(world, stack);
		refreshAimingZoom(world, stack);

		return true;
	}

	/**
	 * Deactivates a field conversion.
	 */
	public static boolean clearConversion(ServerLevel world, ItemStack stack)
	{
		var base = getDefinition(world, stack);
		if (base.isEmpty() || !stack.has(FIELD_CONVERSION))
			return false;

		stack.remove(FIELD_CONVERSION);
		refreshVisuals(stack, base.orElseThrow());
		refreshAimingZoom(world, stack);

		return true;
	}

	/**
	 * Initializes the loaded ammo.
	 */
	private static void initializeLoadedAmmo(ItemStack stack, BlasterStats.Ammo ammo)
	{
		if (ammo.feed() instanceof BlasterStats.MagazineFeed && !stack.has(LOADED_ROUNDS))
			stack.set(LOADED_ROUNDS, 0);

		if (ammo.feed() instanceof BlasterStats.ChargeStoreFeed charge)
		{
			var current = getLoadedCharge(stack).map(StoredCharge::current).orElse(0);
			var capacity = Math.max(current, charge.chargeCapacityUnits());
			if (getLoadedCharge(stack).map(value -> value.capacity() != capacity).orElse(true))
				stack.set(LOADED_CHARGE, new StoredCharge(current, capacity));
		}
	}

	/**
	 * Refreshes the blaster state.
	 */
	private static void refreshState(ServerLevel world, ItemStack stack)
	{
		var loadout = getLoadout(world, stack);
		if (loadout.isEmpty())
			return;

		var resolved = loadout.orElseThrow();
		if (stack.has(FIELD_CONVERSION))
		{
			if (resolved.activeConversion().isEmpty())
				stack.remove(FIELD_CONVERSION);

			refreshVisuals(stack, resolved.definition());
		}
		else
			initializeLoadedAmmo(stack, resolved.definition().stats().ammo());
	}

	/**
	 * Refreshes the blaster's visual representation.
	 */
	private static void refreshVisuals(ItemStack stack, BlasterDatapackDefinition definition)
	{
		var model = definition.stats().configuration().itemModel();
		if (!model.equals(stack.get(DataComponents.ITEM_MODEL)))
			stack.set(DataComponents.ITEM_MODEL, model);

		var attachments = getAttachments(stack);
		if (!attachments.hud().equals(definition.attachments().hud()))
			stack.set(ATTACHMENTS, new AttachmentsComponent(definition.attachments().hud(), attachments.applied()));

		initializeLoadedAmmo(stack, definition.stats().ammo());
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
	public static void setAiming(Level world, ItemStack stack, boolean aiming)
	{
		applyState(stack, state -> state.withIsAiming(aiming));

		var attrs = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);

		if (aiming)
		{
			attrs = attrs.withModifierAdded(Attributes.MOVEMENT_SPEED, ATTR_MODIFIER_AIMING_SPEED_PENALTY_ENABLED, EquipmentSlotGroup.HAND);
		}
		else
		{
			attrs = AttributeUtil.without(attrs, Attributes.MOVEMENT_SPEED, ATTR_MODIFIER_AIMING_SPEED_PENALTY_ENABLED);
			attrs = AttributeUtil.without(attrs, GalaxiesEntityAttributes.FIELD_OF_VIEW_ZOOM, ATTR_MODIFIER_AIMING_FOV_ENABLED);
		}

		stack.set(DataComponents.ATTRIBUTE_MODIFIERS, withAimingZoom(world, stack, attrs));
	}

	/**
	 * Derives this weapon's native zoom modifier from the same effective result used by gameplay/UI.
	 */
	private static ItemAttributeModifiers withAimingZoom(Level world, ItemStack stack, ItemAttributeModifiers attributes)
	{
		if (!getState(stack).isAiming())
			return AttributeUtil.without(attributes, GalaxiesEntityAttributes.FIELD_OF_VIEW_ZOOM, ATTR_MODIFIER_AIMING_FOV_ENABLED);

		var effective = getEffectiveStats(world, stack);
		if (effective.isEmpty())
			return AttributeUtil.without(attributes, GalaxiesEntityAttributes.FIELD_OF_VIEW_ZOOM, ATTR_MODIFIER_AIMING_FOV_ENABLED);

		return attributes.withModifierAdded(GalaxiesEntityAttributes.FIELD_OF_VIEW_ZOOM, new AttributeModifier(
				ATTR_MODIFIER_AIMING_FOV_ENABLED.id(),
				effective.orElseThrow().zoom() - 1.0,
				AttributeModifier.Operation.ADD_MULTIPLIED_BASE
		), EquipmentSlotGroup.HAND);
	}

	/**
	 * Updates native attributes only when their value changes, including after definition reloads.
	 */
	private static void refreshAimingZoom(Level world, ItemStack stack)
	{
		var previous = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
		var updated = withAimingZoom(world, stack, previous);

		if (!updated.equals(previous))
			stack.set(DataComponents.ATTRIBUTE_MODIFIERS, updated);
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
		var time = GameTime.now(world) + tickDelta;

		if (cooldown <= lastFired || cooldown <= time)
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

		var primaryBypassTime = stats.cooling().orElseThrow().primaryBypassTime();
		var primaryBypassTolerance = stats.cooling().orElseThrow().primaryBypassTolerance();
		if (Math.abs(ventingHeat - primaryBypassTime) <= primaryBypassTolerance)
			return Optional.of(CoolingBypass.PRIMARY);

		var secondaryBypassTime = stats.cooling().orElseThrow().secondaryBypassTime();
		var secondaryBypassTolerance = stats.cooling().orElseThrow().secondaryBypassTolerance();
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

		var isWaitingToFire = state.fireCooldown() > GameTime.now(world);
		if (isWaitingToFire)
			return false;

		if (user instanceof Player player)
		{
			var hand = player.getMainHandItem() == stack ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
			if (player.getItemInHand(hand) != stack || !BlasterActions.canFire(player, hand))
				return false;
		}

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
		var time = GameTime.now(world) + tickDelta;

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

		var time = GameTime.now(world) + tickDelta;

		var lastCommittedHeat = state.lastTotalHeat();
		var dissipationPerTick = stats.heat().drainSpeed();

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

		var time = GameTime.now(world) + tickDelta;

		var lastVentingHeat = state.lastVentingHeat();
		var dissipationPerTick = stats.heat().overheatDrainSpeed();

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

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel world, Entity entity, @Nullable EquipmentSlot slot)
	{
		// If the stack does not have a serial number, assign one
		if (stack.get(SERIAL) == null)
			stack.set(SERIAL, world.getRandom().nextLong());

		refreshState(world, stack);

		if (entity instanceof Player player && player.getMainHandItem() != stack && player.getOffhandItem() != stack)
		{
			if (isDeployed(stack))
				setDeployed(world, stack, false);

			if (getState(stack).isAiming())
				setAiming(world, stack, false);
		}

		if (getState(stack).isAiming())
			refreshAimingZoom(world, stack);
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
		return user instanceof Player && !usesCustomLeftInput(user, stack);
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
			setAiming(world, stack, false);

		return super.releaseUsing(stack, world, user, remainingUseTicks);
	}

	@Override
	public InteractionResult use(Level world, Player user, InteractionHand hand)
	{
		// The hand-qualified control protocol owns firing and ADS.
		if (dev.pswg.interaction.BlasterWield.canWield(user, hand))
		{
			// A native success calls itemUsed and lowers the hand.
			return InteractionResult.FAIL;
		}

		return InteractionResult.PASS;
	}

	@Override
	public Optional<Long> getRecoilStart(ItemStack stack)
	{
		var start = getState(stack).lastFired();

		if (start <= 0)
		{
			return Optional.empty();
		}

		return Optional.of(start);
	}

	@Override
	public boolean usesCustomLeftInput()
	{
		return true;
	}

	@Override
	public boolean usesCustomLeftInput(LivingEntity user, ItemStack stack)
	{
		return user instanceof Player player && player.getItemInHand(dev.pswg.interaction.BlasterWield.primaryHand(player)) == stack
		       && dev.pswg.interaction.BlasterWield.canWield(player, dev.pswg.interaction.BlasterWield.primaryHand(player));
	}

	@Override
	public boolean isLeftUseEnabled(LivingEntity user, ItemStack stack)
	{
		return usesCustomLeftInput(user, stack);
	}

	@Override
	public InteractionResult useLeft(Level world, LivingEntity user, InteractionHand hand, boolean repeatEvent)
	{
		return InteractionResult.FAIL;
	}

	/**
	 * Attempts a shot.
	 */
	public static ShotResult tryFire(ServerLevel world, Player user, InteractionHand hand, boolean pressed)
	{
		return tryFire(world, user, hand, pressed, -1);
	}

	/**
	 * Attempts one shot with hold duration. A negative duration denotes an ordinary, non-charge trigger.
	 */
	public static ShotResult tryFire(
			ServerLevel world,
			Player user,
			InteractionHand hand,
			boolean pressed,
			int heldChargeTicks
	)
	{
		var result = fireServer(world, user, hand, !pressed, heldChargeTicks, false);
		return result == InteractionResult.SUCCESS ? ShotResult.FIRED
				: result == InteractionResult.CONSUME ? ShotResult.COOLING_HANDLED : ShotResult.BLOCKED;
	}

	/**
	 * Checks a charge press through the normal readiness/cooling path without emitting or debiting a shot.
	 */
	public static ShotResult tryBeginCharge(ServerLevel world, Player user, InteractionHand hand)
	{
		var result = fireServer(world, user, hand, false, 0, true);
		return result == InteractionResult.SUCCESS ? ShotResult.READY
				: result == InteractionResult.CONSUME ? ShotResult.COOLING_HANDLED : ShotResult.BLOCKED;
	}

	/**
	 * Checks or executes one shot.
	 */
	private static InteractionResult fireServer(
			ServerLevel world,
			Player user,
			InteractionHand hand,
			boolean repeatEvent,
			int heldChargeTicks,
			boolean chargePress
	)
	{

		ItemStack itemStack = user.getItemInHand(hand);
		if (getDefinition(world, itemStack).isEmpty())
			return InteractionResult.PASS;

		if (!canFire(world, user, itemStack))
			return InteractionResult.PASS;

		var state = getState(itemStack);

		var optionalStats = getStats(world, itemStack);
		if (optionalStats.isEmpty())
		{
			Blasters.LOGGER.warn("Blaster stats not found for blaster {}", itemStack);
			return InteractionResult.FAIL;
		}

		var stats = optionalStats.get();

		var timestamp = GameTime.now(world);

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
						null,
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
						null,
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
						null,
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

		var shotStats = getEffectiveStats(world, itemStack, new BlasterEffectiveStats.Context(
				BlasterStanceProfile.WeaponState.FIRING,
				isDeployed(itemStack),
				state.isAiming(),
				isFolded(itemStack)
		)).orElseThrow();

		stats = shotStats.stats();

		var loadout = getLoadout(world, itemStack).orElseThrow();

		var trigger = loadout.selectedMode().trigger();
		if (trigger instanceof BlasterStats.ChargeTrigger charge)
		{
			if (!chargePress && heldChargeTicks < charge.minimumChargeTicks())
				return InteractionResult.FAIL;
		}
		else if (chargePress || heldChargeTicks >= 0)
			return InteractionResult.FAIL;

		var profile = Optional.ofNullable(BlasterData.get(world).behaviorProfiles().get(loadout.selectedMode().behaviorProfile()));
		if (profile.isEmpty())
			return InteractionResult.FAIL;

		if (!BlasterAmmo.canConsumeShot(user, itemStack, stats.ammo()))
		{
			if (!repeatEvent)
				world.playSound(null, user.getX(), user.getY(), user.getZ(), BlasterSounds.DRYFIRE, SoundSource.PLAYERS, 1, 1);

			return InteractionResult.FAIL;
		}

		if (chargePress)
			return InteractionResult.SUCCESS;

		var behavior = profile.orElseThrow();
		var chargedShot = behavior.chargedShot();
		var shot = BlasterShot.capture(stats, behavior, trigger, heldChargeTicks, getLoadedAmmo(itemStack, stats.ammo()));

		var spendAll = chargedShot.map(value -> value.consume() == BlasterBehaviorProfile.ChargeConsumer.ALL_REMAINING_COMPONENT_CHARGE).orElse(false);
		if (!BlasterAmmo.consumeShot(world, user, itemStack, stats.ammo(), spendAll))
			return InteractionResult.FAIL;

		state = state.withLastFired(timestamp)
		             .withCooling(CoolingMode.PASSIVE, timestamp + stats.heat().passiveCooldownDelay())
		             .withFireCooldown(timestamp + stats.automaticRepeatDelay());

		var totalHeat = coolingStatus.totalHeat();

		if (heatEnabled && getOverchargeTimeRemaining(world, itemStack, 0).isEmpty())
			totalHeat += stats.heat().perRound();

		fireShot(user, world, shot, state.isAiming());

		var recoil = stats.recoil();
		var pitch = state.isAiming() ? recoil.aimPitchDegrees() : recoil.hipPitchDegrees();
		var yaw = state.isAiming() ? recoil.aimYawDegrees() : recoil.hipYawDegrees();
		if (user instanceof IRecoilEntity recoilEntity)
			recoilEntity.pswg$addRecoilImpulse(
				new Vector3f(-pitch, yaw, 0),
				itemStack.getOrDefault(SERIAL, 0L),
				recoil.recoveryTicks(),
				recoil.pattern().pitchMultipliers(20),
				recoil.pattern().yawMultipliers()
			);

		if (stats.fireSound().isPresent())
		{
			world.playSound(
					null,
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
					null,
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

		return InteractionResult.SUCCESS;
	}

	@Override
	public ItemStack invokePrimaryAction(ItemStack stack, Level world, LivingEntity user)
	{
		if (world.isClientSide())
			return stack;

		if (user instanceof Player player)
		{
			var hand = player.getMainHandItem() == stack ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
			if (player.getItemInHand(hand) != stack || !BlasterActions.canFire(player, hand))
				return stack;
		}

		var optionalStats = getStats(world, stack);
		if (optionalStats.isEmpty())
			return stack;

		var heat = optionalStats.orElseThrow().heat();
		if (heat.capacity() <= 0 || heat.overheatDrainSpeed() <= 0)
			return stack;

		var timestamp = GameTime.now(world);

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

	@Override
	public boolean usesCustomPrimaryAction()
	{
		return true;
	}

	/**
	 * Fires a shot.
	 */
	private static void fireShot(LivingEntity user, ServerLevel serverWorld, BlasterShot shot, boolean aiming)
	{
		var spread = shot.stats().spread();
		var angle = aiming ? spread.aimDegrees() : spread.hipDegrees();

		if (user.getDeltaMovement().horizontalDistanceSqr() > 0.0001)
			angle *= spread.movingMultiplier();

		if (user.isSprinting())
			angle *= spread.sprintingMultiplier();

		var direction = RandomHelper.directionInCone(serverWorld.getRandom(), user.getViewVector(1).normalize(), Math.min(angle, 90));
		var origin = user.getEyePosition();

		if (shot.behavior().delivery() == BlasterBehaviorProfile.Delivery.HITSCAN)
		{
			var hit = BlasterShot.trace(serverWorld, user, origin, direction.scale(shot.stats().range()));
			shot.hit(serverWorld, user, user, hit, (float)origin.distanceTo(hit.getLocation()));
			return;
		}

		var projectile = new BlasterBoltEntity(Blasters.BLASTER_BOLT_ENTITY, serverWorld);
		projectile.setOwner(user);
		projectile.setShot(shot);
		projectile.setPos(origin);
		projectile.setDeltaMovement(direction.scale(5));
		ProjectileUtil.rotateTowardsMovement(projectile, 1);
		serverWorld.addFreshEntity(projectile);
	}
}
