package com.parzivail.pswg.item.blaster;

import com.google.common.collect.ImmutableMultimap;
import com.parzivail.pswg.Client;
import com.parzivail.pswg.Galaxies;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.event.PlayerEvent;
import com.parzivail.pswg.compat.gravitychanger.GravityChangerCompat;
import com.parzivail.pswg.container.SwgPackets;
import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.pswg.item.blaster.data.*;
import com.parzivail.pswg.util.BlasterUtil;
import com.parzivail.tarkin.api.TarkinLang;
import com.parzivail.util.client.TextUtil;
import com.parzivail.util.client.TooltipUtil;
import com.parzivail.util.item.*;
import com.parzivail.util.math.MathUtil;
import com.parzivail.util.math.Matrix4fUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.*;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class BlasterItem extends Item implements ILeftClickConsumer, ICustomVisualItemEquality, IZoomingItem, IDefaultNbtProvider, ICooldownItem, IItemActionListener, IItemHotbarListener, IItemEntityTickListener
{
	@TarkinLang
	public static final String I18N_TOOLTIP_BLASTER_TYPE = Resources.tooltip("blaster.type");
	@TarkinLang
	public static final String I18N_TOOLTIP_BLASTER_CONTROLS = Resources.tooltip("blaster.controls");
	@TarkinLang
	public static final String I18N_TOOLTIP_BLASTER_NO_STATS = Resources.tooltip("blaster.stats.unknown");
	@TarkinLang
	public static final String I18N_TOOLTIP_BLASTER_STATS_HEAT = Resources.tooltip("blaster.stats.heat");
	@TarkinLang
	public static final String I18N_TOOLTIP_BLASTER_STATS_RECOIL = Resources.tooltip("blaster.stats.recoil");
	@TarkinLang
	public static final String I18N_TOOLTIP_BLASTER_STATS_SPREAD = Resources.tooltip("blaster.stats.spread");
	@TarkinLang
	public static final String I18N_TOOLTIP_BLASTER_STATS_DAMAGE = Resources.tooltip("blaster.stats.damage");
	@TarkinLang
	public static final String I18N_TOOLTIP_BLASTER_STATS_RANGE = Resources.tooltip("blaster.stats.range");
	@TarkinLang
	public static final String I18N_MESSAGE_MODE_CHANGED = Resources.msg("blaster_mode_changed");

	private static final UUID ADS_SPEED_PENALTY_MODIFIER_ID = UUID.fromString("57b2e25d-1a79-44e7-8968-6d0dbbb7f997");
	private static final EntityAttributeModifier ADS_SPEED_PENALTY_MODIFIER = new EntityAttributeModifier(ADS_SPEED_PENALTY_MODIFIER_ID, "ADS speed penalty", -0.5f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);

	private static final ImmutableMultimap<EntityAttribute, EntityAttributeModifier> ATTRIB_MODS_ADS =
			ImmutableMultimap.<EntityAttribute, EntityAttributeModifier>builder()
			                 .put(EntityAttributes.GENERIC_MOVEMENT_SPEED, ADS_SPEED_PENALTY_MODIFIER)
			                 .build();

	private static final HashMap<BlasterAttachmentFunction, Float> SPREAD_MAP = Util.make(new HashMap<>(), (h) -> {
		h.put(BlasterAttachmentFunction.REDUCE_SPREAD, 0.4f);
	});

	private static final HashMap<BlasterAttachmentFunction, Float> RECOIL_MAP = Util.make(new HashMap<>(), (h) -> {
		h.put(BlasterAttachmentFunction.REDUCE_RECOIL, 0.7f);
	});

	private final Identifier model;
	private final BlasterDescriptor descriptor;

	public BlasterItem(Settings settings, Identifier model, BlasterDescriptor descriptor)
	{
		super(settings);
		this.model = model;
		this.descriptor = descriptor;
	}

	public static TranslatableTextContent getAttachmentTranslation(Identifier model, BlasterAttachmentDescriptor descriptor)
	{
		return new TranslatableTextContent(String.format("blaster.%s.%s.attachment.%s", model.getNamespace(), model.getPath(), descriptor.id));
	}

	@Override
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner)
	{
		return false;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
	{
		final var stack = player.getStackInHand(hand);

		if (hand != Hand.MAIN_HAND)
			return TypedActionResult.pass(stack);

		if (!world.isClient)
			BlasterTag.mutate(stack, BlasterTag::toggleAds);

		player.setCurrentHand(hand);
		return TypedActionResult.pass(stack);
	}

	@Override
	public boolean hasGlint(ItemStack stack)
	{
		return true;
	}

	public static Identifier getBlasterModel(ItemStack stack)
	{
		if (stack.getItem() instanceof BlasterItem bi)
			return bi.model;
		return null;
	}

	public static void nextFireMode(World world, PlayerEntity player, ItemStack stack)
	{
		var bt = new BlasterTag(stack.getOrCreateNbt());
		var bd = getBlasterDescriptor(stack);
		var modes = bd.firingModes;
		BlasterFiringMode currentMode;

		currentMode = bt.getFiringMode();
		var currentModeIdx = modes.indexOf(currentMode);

		currentModeIdx++;
		currentModeIdx %= modes.size();

		bt.setFiringMode(currentMode = modes.get(currentModeIdx));

		//			world.playSound(null, player.getBlockPos(), SwgSounds.Lightsaber.START_CLASSIC, SoundCategory.PLAYERS, 1f, 1f);

		bt.serializeAsSubtag(stack);

		player.sendMessage(Text.translatable(I18N_MESSAGE_MODE_CHANGED, Text.translatable(currentMode.getTranslation())), true);
	}

	public static void bypassHeat(World world, PlayerEntity player, ItemStack stack)
	{
		BlasterTag.mutate(stack, bt -> {
			if (bt.isCooling())
				return;

			bt.ventingHeat = bt.heat;
			bt.coolingMode = BlasterTag.COOLING_MODE_FORCED_BYPASS;
			bt.canBypassCooling = false;
			bt.heat = 0;
		});
	}

	public static BlasterDescriptor getBlasterDescriptor(ItemStack stack)
	{
		return getBlasterDescriptor(stack, false);
	}

	public static BlasterDescriptor getBlasterDescriptor(ItemStack stack, boolean allowNull)
	{
		if (stack.getItem() instanceof BlasterItem bi)
			return bi.descriptor;
		if (allowNull)
			return null;

		var j = CrashReport.create(new NullPointerException("Cannot get blaster descriptor for unknown stack " + stack.toString()), "Getting blaster descriptor");
		throw new CrashException(j);
	}

	@Override
	public void onItemAction(World world, PlayerEntity player, ItemStack stack, ItemAction action)
	{
		switch (action)
		{
			case PRIMARY -> nextFireMode(world, player, stack);
			case SECONDARY -> bypassHeat(world, player, stack);
		}
	}

	@Override
	public float getCooldownProgress(PlayerEntity player, World world, ItemStack stack, float tickDelta)
	{
		var bt = new BlasterTag(stack.getOrCreateNbt());
		var bd = getBlasterDescriptor(stack, true);

		if (bd == null)
			return 0;

		if (bt.isCooling())
			return MathHelper.clamp((bt.ventingHeat - bd.heat.overheatDrainSpeed * tickDelta) / bd.heat.capacity, 0, 1);

		var heatDelta = 0f;

		if (bt.passiveCooldownTimer == 0)
			heatDelta = bd.heat.drainSpeed * tickDelta;

		return MathHelper.clamp((bt.heat - heatDelta) / bd.heat.capacity, 0, 1);
	}

	@Override
	public boolean allowRepeatedLeftHold(World world, PlayerEntity player, Hand mainHand)
	{
		final var stack = player.getStackInHand(mainHand);
		var bt = new BlasterTag(stack.getOrCreateNbt());
		var bd = getBlasterDescriptor(stack);

		var automatic = bd.firingModes.contains(BlasterFiringMode.AUTOMATIC) && bt.getFiringMode() == BlasterFiringMode.AUTOMATIC;
		var burst = bd.firingModes.contains(BlasterFiringMode.BURST) && bt.getFiringMode() == BlasterFiringMode.BURST;

		return automatic || (burst && bt.burstCounter > 0);
	}

	@Override
	public int getMaxUseTime(ItemStack stack)
	{
		return 72000;
	}

	@Override
	public UseAction getUseAction(ItemStack stack)
	{
		return UseAction.NONE;
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
	{
		var bt = new BlasterTag(stack.getOrCreateNbt());

		if (!world.isClient && user.getItemUseTime() > 3 && bt.isAimingDownSights)
			BlasterTag.mutate(stack, BlasterTag::toggleAds);
	}

	@Override
	public TypedActionResult<ItemStack> useLeft(World world, PlayerEntity player, Hand hand, boolean isRepeatEvent)
	{
		final var stack = player.getStackInHand(hand);

		var bd = getBlasterDescriptor(stack);
		var bt = new BlasterTag(stack.getOrCreateNbt());

		if (!bt.isReady())
			return TypedActionResult.fail(stack);

		bt.shotTimer = (short)bd.automaticRepeatTime;

		if (bt.isCooling())
		{
			if (world.isClient || !bt.canBypassCooling || isRepeatEvent)
			{
				bt.serializeAsSubtag(stack);
				return TypedActionResult.fail(stack);
			}

			var profile = bd.cooling;

			final var cooldownTime = bt.ventingHeat / (float)bd.heat.capacity;

			final var primaryBypassStart = profile.primaryBypassTime - profile.primaryBypassTolerance;
			final var primaryBypassEnd = profile.primaryBypassTime + profile.primaryBypassTolerance;
			final var secondaryBypassStart = profile.secondaryBypassTime - profile.secondaryBypassTolerance;
			final var secondaryBypassEnd = profile.secondaryBypassTime + profile.secondaryBypassTolerance;

			var result = TypedActionResult.fail(stack);

			if (profile.primaryBypassTolerance > 0 && cooldownTime >= primaryBypassStart && cooldownTime <= primaryBypassEnd)
			{
				bt.ventingHeat = 0;
				bt.coolingMode = BlasterTag.COOLING_MODE_NONE;

				world.playSound(null, player.getBlockPos(), SwgSounds.Blaster.BYPASS_PRIMARY, SoundCategory.PLAYERS, 1, 1);
				result = TypedActionResult.success(stack);
			}
			else if (profile.secondaryBypassTolerance > 0 && cooldownTime >= secondaryBypassStart && cooldownTime <= secondaryBypassEnd)
			{
				bt.ventingHeat = 0;
				bt.coolingMode = BlasterTag.COOLING_MODE_NONE;

				bt.overchargeTimer = (short)bd.heat.overchargeBonus;

				world.playSound(null, player.getBlockPos(), SwgSounds.Blaster.BYPASS_SECONDARY, SoundCategory.PLAYERS, 1, 1);
				result = TypedActionResult.success(stack);
			}
			else
			{
				bt.canBypassCooling = false;
				bt.coolingMode = BlasterTag.COOLING_MODE_PENALTY_BYPASS;

				world.playSound(null, player.getBlockPos(), SwgSounds.Blaster.BYPASS_FAILED, SoundCategory.PLAYERS, 1, 1);
			}

			bt.serializeAsSubtag(stack);

			return result;
		}

		if (!player.isCreative())
		{
			if (bt.shotsRemaining <= 0)
			{
				var nextPack = getAnotherPack(player);

				if (nextPack == null)
				{
					if (!world.isClient)
						world.playSound(null, player.getBlockPos(), SwgSounds.Blaster.DRYFIRE, SoundCategory.PLAYERS, 1f, 1f);

					bt.serializeAsSubtag(stack);
					return TypedActionResult.fail(stack);
				}
				else if (!world.isClient)
				{
					bt.shotsRemaining = nextPack.getRight().numShots();
					player.getInventory().removeStack(nextPack.getLeft(), 1);
					world.playSound(null, player.getBlockPos(), SwgSounds.Blaster.RELOAD, SoundCategory.PLAYERS, 1f, 1f);
				}
			}
		}

		boolean canFireUnderwater = bt.mapWithAttachment(bd, BlasterAttachmentFunction.WATERPROOF_FIRING, true)
		                              .orElse(bd.waterBehavior == BlasterWaterBehavior.CAN_FIRE_UNDERWATER);

		if (!canFireUnderwater && player.isSubmergedInWater())
		{
			if (!world.isClient)
				world.playSound(null, player.getBlockPos(), SwgSounds.Blaster.DRYFIRE, SoundCategory.PLAYERS, 1f, 1f);

			bt.serializeAsSubtag(stack);
			return TypedActionResult.fail(stack);
		}

		bt.passiveCooldownTimer = (short)bd.heat.passiveCooldownDelay;
		bt.shotsRemaining--;

		if (bt.overchargeTimer == 0)
			bt.heat += bd.heat.perRound;

		if (bt.heat > bd.heat.capacity)
		{
			world.playSound(null, player.getBlockPos(), SwgSounds.Blaster.OVERHEAT, SoundCategory.PLAYERS, 1, 1);
			bt.ventingHeat = bd.heat.capacity + bd.heat.overheatPenalty;
			bt.coolingMode = BlasterTag.COOLING_MODE_OVERHEAT;
			bt.canBypassCooling = true;
			bt.heat = 0;
		}

		var burst = bd.firingModes.contains(BlasterFiringMode.BURST) && bt.getFiringMode() == BlasterFiringMode.BURST;

		if (burst)
		{
			if (bt.burstCounter == 0)
				bt.burstCounter = (short)bd.burstSize;

			bt.burstCounter--;
			bt.shotTimer = (short)bd.burstRepeatTime;
		}

		bt.timeSinceLastShot = 0;

		if (!world.isClient)
		{
			var m = new Matrix4f();

			m.rotateY(MathUtil.toRadians(-player.getYaw()));
			m.rotateX(MathUtil.toRadians(player.getPitch()));

			var hS = (world.random.nextFloat() * 2 - 1) * bd.spread.horizontal;
			var vS = (world.random.nextFloat() * 2 - 1) * bd.spread.vertical;

			// TODO: custom spread reduction?
			var spread = bt.mapWithAttachment(bd, SPREAD_MAP).orElse(1f);
			float horizontalSpreadCoef = spread;
			float verticalSpreadCoef = spread;

			if (bt.isAimingDownSights)
			{
				horizontalSpreadCoef = 0;
				verticalSpreadCoef = 0;
			}

			final var entityPitch = vS * verticalSpreadCoef;
			final var entityYaw = hS * horizontalSpreadCoef;

			m.rotateY(MathUtil.toRadians(entityYaw));
			m.rotateX(MathUtil.toRadians(entityPitch));

			var fromDir = GravityChangerCompat.vecPlayerToWorld(player, Matrix4fUtil.transform(MathUtil.POSZ, m).normalize());

			var range = bd.range;
			Function<Double, Double> damage = (x) -> bd.damage * bd.damageFalloff.apply(x / range);

			var shouldRecoil = true;

			var heatPitchIncrease = 0.15f * (bt.heat / (float)bd.heat.capacity);

			boolean passThroughWater = bt.mapWithAttachment(bd, BlasterAttachmentFunction.WATERPROOF_BOLTS, true)
			                             .orElse(bd.waterBehavior != BlasterWaterBehavior.NONE);

			switch (bt.getFiringMode())
			{
				case SEMI_AUTOMATIC, BURST, AUTOMATIC ->
				{
					world.playSound(null, player.getBlockPos(), SwgSounds.getOrDefault(modelIdToSoundId(bd.sound), SwgSounds.Blaster.FIRE_A280), SoundCategory.PLAYERS, 1, 1 + (float)world.random.nextGaussian() / 30 + heatPitchIncrease);
					BlasterUtil.fireBolt(world, player, fromDir, range, damage, passThroughWater, entity -> {
						entity.setVelocity(player, player.getPitch() + entityPitch, player.getYaw() + entityYaw, 0.0F, 5.0F, 0);
						entity.setPosition(player.getPos().add(GravityChangerCompat.vecPlayerToWorld(player, new Vec3d(0, player.getStandingEyeHeight() - entity.getHeight() / 2f, 0))));
						entity.setHue(bd.boltColor);
					});
				}
				case STUN ->
				{
					world.playSound(null, player.getBlockPos(), SwgSounds.Blaster.STUN, SoundCategory.PLAYERS, 1, 1 + (float)world.random.nextGaussian() / 20);
					BlasterUtil.fireStun(world, player, fromDir, range * 0.10f, passThroughWater, entity -> {
						entity.setVelocity(player, player.getPitch() + entityPitch, player.getYaw() + entityYaw, 0.0F, 1.25f, 0);
						entity.setPosition(player.getPos().add(GravityChangerCompat.vecPlayerToWorld(player, new Vec3d(0, player.getStandingEyeHeight() - entity.getHeight() / 2f, 0))));
					});
					shouldRecoil = false;
				}
				case SLUGTHROWER ->
				{
					world.playSound(null, player.getBlockPos(), SwgSounds.getOrDefault(modelIdToSoundId(bd.sound), SwgSounds.Blaster.FIRE_CYCLER), SoundCategory.PLAYERS, 1, 1 + (float)world.random.nextGaussian() / 40 + heatPitchIncrease);
					BlasterUtil.fireSlug(world, player, fromDir, range, damage, passThroughWater);
				}
				case ION ->
				{
					world.playSound(null, player.getBlockPos(), SwgSounds.getOrDefault(modelIdToSoundId(bd.sound), SwgSounds.Blaster.FIRE_ION), SoundCategory.PLAYERS, 1, 1 + (float)world.random.nextGaussian() / 40 + heatPitchIncrease);
					BlasterUtil.fireIon(world, player, range, passThroughWater, entity -> {
						entity.setVelocity(player, player.getPitch() + entityPitch, player.getYaw() + entityYaw, 0.0F, 5.0F, 0);
						entity.setPosition(player.getPos().add(GravityChangerCompat.vecPlayerToWorld(player, new Vec3d(0, player.getStandingEyeHeight() - entity.getHeight() / 2f, 0))));
						entity.setHue(bd.boltColor);
					});
				}
			}

			if (shouldRecoil)
			{
				var passedData = PlayerEvent.createBuffer(PlayerEvent.ACCUMULATE_RECOIL);
				var horizNoise = world.random.nextGaussian();
				horizNoise = horizNoise * 0.3 + 0.7 * Math.signum(horizNoise);

				var recoilAmount = bt.mapWithAttachment(bd, RECOIL_MAP).orElse(1f);

				passedData.writeFloat(recoilAmount * (float)(bd.recoil.horizontal * horizNoise));
				passedData.writeFloat(recoilAmount * (float)(bd.recoil.vertical * (0.7 + 0.3 * (world.random.nextGaussian() + 1) / 2)));
				ServerPlayNetworking.send((ServerPlayerEntity)player, SwgPackets.S2C.PlayerEvent, passedData);
			}

			bt.serializeAsSubtag(stack);
		}

		return TypedActionResult.success(stack);
	}

	public static Identifier modelIdToSoundId(Identifier id)
	{
		return new Identifier(id.getNamespace(), "blaster.fire." + id.getPath());
	}

	@Override
	public String getTranslationKey(ItemStack stack)
	{
		var model = getBlasterModel(stack);
		if (model != null)
			return getTranslationKeyForModel(model);

		return super.getTranslationKey(stack);
	}

	@NotNull
	public static String getTranslationKeyForModel(Identifier model)
	{
		return "blaster." + model.getNamespace() + "." + model.getPath();
	}

	@Override
	public NbtCompound getDefaultTag(ItemConvertible item, int count)
	{
		var blasterTag = new BlasterTag(new NbtCompound());
		if (descriptor.firingModes.isEmpty())
			blasterTag.setFiringMode(BlasterFiringMode.SEMI_AUTOMATIC);
		else
			blasterTag.setFiringMode(descriptor.firingModes.get(0));

		blasterTag.attachmentBitmask = descriptor.attachmentDefault;
		blasterTag.shotTimer = (short)descriptor.automaticRepeatTime;

		var tag = new NbtCompound();
		blasterTag.serializeAsSubtag(tag);
		return tag;
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
	{
		super.appendTooltip(stack, world, tooltip, context);
		tooltip.add(Text.translatable(I18N_TOOLTIP_BLASTER_CONTROLS, TextUtil.stylizeKeybind(Client.KEY_PRIMARY_ITEM_ACTION.getBoundKeyLocalizedText()), TextUtil.stylizeKeybind(Client.KEY_SECONDARY_ITEM_ACTION.getBoundKeyLocalizedText())));

		var bd = getBlasterDescriptor(stack, true);
		if (bd == null)
			tooltip.add(TooltipUtil.note(Text.translatable(I18N_TOOLTIP_BLASTER_NO_STATS)));
		else
		{
			tooltip.add(TooltipUtil.note(Text.translatable(I18N_TOOLTIP_BLASTER_TYPE, bd.type.getLangKey())));
			tooltip.add(TooltipUtil.note(Text.translatable(I18N_TOOLTIP_BLASTER_STATS_HEAT, bd.heat.capacity, bd.heat.drainSpeed)));
			tooltip.add(TooltipUtil.note(Text.translatable(I18N_TOOLTIP_BLASTER_STATS_RECOIL, bd.recoil.horizontal, bd.recoil.vertical)));
			tooltip.add(TooltipUtil.note(Text.translatable(I18N_TOOLTIP_BLASTER_STATS_SPREAD, bd.spread.horizontal, bd.spread.vertical)));
			tooltip.add(TooltipUtil.note(Text.translatable(I18N_TOOLTIP_BLASTER_STATS_DAMAGE, bd.damage)));
			tooltip.add(TooltipUtil.note(Text.translatable(I18N_TOOLTIP_BLASTER_STATS_RANGE, bd.range)));
		}
	}

	private ItemStack forType(BlasterDescriptor descriptor)
	{
		var stack = new ItemStack(this);

		stack.getOrCreateNbt().putString("model", descriptor.id.toString());

		BlasterTag.mutate(stack, blasterTag -> {
			if (descriptor.firingModes.isEmpty())
				blasterTag.setFiringMode(BlasterFiringMode.SEMI_AUTOMATIC);
			else
				blasterTag.setFiringMode(descriptor.firingModes.get(0));

			blasterTag.attachmentBitmask = descriptor.attachmentDefault;
		});

		return stack;
	}

	private Pair<Integer, BlasterPowerPack> getAnotherPack(PlayerEntity player)
	{
		for (var i = 0; i < player.getInventory().size(); i++)
		{
			var s = player.getInventory().getStack(i);
			var a = BlasterPowerPackItem.getPackType(s);
			if (a == null)
				continue;

			return new Pair<>(i, a);
		}
		return null;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
	{
		var bd = getBlasterDescriptor(stack, true);
		if (bd == null)
		{
			// remove erroring blasters -- assume an addon has been removed
			stack.decrement(stack.getCount());
			Galaxies.LOG.warn("Removed unknown blaster %s from %s", getBlasterModel(stack), entity);
			return;
		}

		if (!world.isClient)
			BlasterTag.mutate(stack, blasterTag -> blasterTag.tick(bd));
	}

	@Override
	public boolean areStacksVisuallyEqual(ItemStack original, ItemStack updated)
	{
		if (!(original.getItem() instanceof BlasterItem) || original.getItem() != updated.getItem())
			return false;

		var bt1 = new BlasterTag(original.getOrCreateNbt());
		var bt2 = new BlasterTag(updated.getOrCreateNbt());

		return bt1.uid == bt2.uid;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public double getFovMultiplier(ItemStack stack, World world, PlayerEntity entity)
	{
		var bt = new BlasterTag(stack.getOrCreateNbt());
		var bd = BlasterItem.getBlasterDescriptor(stack);

		if (bd == null)
			return 1;

		if (bt.isAimingDownSights)
			return bd.adsZoom;

		return 1;
	}

	@Override
	public ImmutableMultimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot)
	{
		if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND)
		{
			var bt = new BlasterTag(stack.getOrCreateNbt());

			if (bt.isAimingDownSights)
				return ATTRIB_MODS_ADS;
		}

		return ImmutableMultimap.of();
	}

	@Override
	public boolean onItemSelected(PlayerEntity player, ItemStack stack)
	{
		var bd = getBlasterDescriptor(stack);
		BlasterTag.mutate(stack, tag -> {
			// Prevent stacking your hotbar to spamming shots by adding a pullout penalty
			tag.readyTimer = (short)bd.quickdrawDelay;
		});
		return true;
	}

	@Override
	public boolean onItemDeselected(PlayerEntity player, ItemStack stack)
	{
		BlasterTag.mutate(stack, tag -> {
			tag.isAimingDownSights = false;
		});
		return true;
	}

	@Override
	public boolean onItemEntityTick(ItemEntity entity, ItemStack stack)
	{
		var oldNbtHashcode = stack.getOrCreateNbt().hashCode();

		var bd = getBlasterDescriptor(stack, true);
		if (bd == null)
		{
			Galaxies.LOG.warn("Removed unknown blaster %s from item entity %s", getBlasterModel(stack), entity);
			entity.discard();
			return false;
		}

		BlasterTag.mutate(stack, blasterTag -> blasterTag.tick(bd));
		var changed = stack.getOrCreateNbt().hashCode() != oldNbtHashcode;
		if (changed)
			Galaxies.LOG.debug("changed");
		return changed;
	}
}
