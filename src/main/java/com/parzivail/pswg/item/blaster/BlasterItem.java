package com.parzivail.pswg.item.blaster;

import com.google.common.collect.ImmutableMultimap;
import com.parzivail.pswg.Client;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.access.util.Matrix4fAccessUtil;
import com.parzivail.pswg.client.event.PlayerEvent;
import com.parzivail.pswg.container.SwgPackets;
import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.pswg.data.SwgBlasterManager;
import com.parzivail.pswg.item.blaster.data.BlasterDescriptor;
import com.parzivail.pswg.item.blaster.data.BlasterFiringMode;
import com.parzivail.pswg.item.blaster.data.BlasterPowerPack;
import com.parzivail.pswg.item.blaster.data.BlasterTag;
import com.parzivail.pswg.util.BlasterUtil;
import com.parzivail.util.item.*;
import com.parzivail.util.math.MathUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.world.World;

import java.util.UUID;

public class BlasterItem extends Item implements ItemStackEntityAttributeModifiers, ILeftClickConsumer, ICustomVisualItemEquality, IZoomingItem, IDefaultNbtProvider, ICooldownItem, IItemActionConsumer
{
	private static final UUID ADS_SPEED_PENALTY_MODIFIER_ID = UUID.fromString("57b2e25d-1a79-44e7-8968-6d0dbbb7f997");
	private static final EntityAttributeModifier ADS_SPEED_PENALTY_MODIFIER = new EntityAttributeModifier(ADS_SPEED_PENALTY_MODIFIER_ID, "ADS speed penalty", -0.5f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);

	private static final ImmutableMultimap<EntityAttribute, EntityAttributeModifier> ATTRIB_MODS_ADS;

	static {
		ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(EntityAttributes.GENERIC_MOVEMENT_SPEED, ADS_SPEED_PENALTY_MODIFIER);
		ATTRIB_MODS_ADS = builder.build();
	}

	public BlasterItem(Settings settings)
	{
		super(settings);
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

		return TypedActionResult.fail(stack);
	}

	@Override
	public boolean hasGlint(ItemStack stack)
	{
		return true;
	}

	public static Identifier getBlasterModel(ItemStack stack)
	{
		var tag = stack.getOrCreateNbt();

		var blasterModel = tag.getString("model");
		if (blasterModel.isEmpty())
			blasterModel = "pswg:a280";

		return new Identifier(blasterModel);
	}

	public static void nextFireMode(World world, PlayerEntity player, ItemStack stack)
	{
		var bt = new BlasterTag(stack.getOrCreateNbt());
		var bd = getBlasterDescriptor(world, stack);
		var modes = bd.firingModes;
		BlasterFiringMode currentMode;

		currentMode = bt.getFiringMode();
		var currentModeIdx = modes.indexOf(currentMode);

		currentModeIdx++;
		currentModeIdx %= modes.size();

		bt.setFiringMode(currentMode = modes.get(currentModeIdx));

		//			world.playSound(null, player.getBlockPos(), SwgSounds.Lightsaber.START_CLASSIC, SoundCategory.PLAYERS, 1f, 1f);

		bt.serializeAsSubtag(stack);

		player.sendMessage(new TranslatableText(Resources.dotModId("msg", "blaster_mode_changed"), new TranslatableText(currentMode.getTranslation())), true);
	}

	public static void bypassHeat(World world, PlayerEntity player, ItemStack stack)
	{
		BlasterTag.mutate(stack, bt -> {
			if (bt.isCooling())
				return;

			bt.coolingTimer = bt.heat;
			bt.coolingMode = BlasterTag.COOLING_MODE_FORCED_BYPASS;
			bt.canBypassCooling = false;
			bt.heat = 0;
		});
	}

	public static BlasterDescriptor getBlasterDescriptor(World world, ItemStack stack)
	{
		var blasterManager = SwgBlasterManager.get(world);
		return blasterManager.getData(getBlasterModel(stack));
	}

	public static BlasterDescriptor getBlasterDescriptorClient(ItemStack stack)
	{
		var blasterManager = Client.ResourceManagers.getBlasterManager();
		return blasterManager.getData(getBlasterModel(stack));
	}

	@Override
	public void consumeAction(World world, PlayerEntity player, ItemStack stack, ItemAction action)
	{
		switch (action)
		{
			case PRIMARY:
				nextFireMode(world, player, stack);
				break;
			case SECONDARY:
				bypassHeat(world, player, stack);
				break;
		}
	}

	@Override
	public float getCooldownProgress(PlayerEntity player, World world, ItemStack stack, float tickDelta)
	{
		var bt = new BlasterTag(stack.getOrCreateNbt());
		var bd = getBlasterDescriptor(world, stack);

		if (bt.isCooling())
			return MathHelper.clamp((bt.coolingTimer - bd.heat.overheatDrainSpeed * tickDelta) / bd.heat.capacity, 0, 1);

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
		var bd = getBlasterDescriptor(world, stack);

		var automatic = bd.firingModes.contains(BlasterFiringMode.AUTOMATIC) && bt.getFiringMode() == BlasterFiringMode.AUTOMATIC;
		var burst = bd.firingModes.contains(BlasterFiringMode.BURST) && bt.getFiringMode() == BlasterFiringMode.BURST;

		return automatic || (burst && bt.burstTimer > 0);
	}

	@Override
	public TypedActionResult<ItemStack> useLeft(World world, PlayerEntity player, Hand hand)
	{
		final var stack = player.getStackInHand(hand);

		var bd = getBlasterDescriptor(world, stack);
		var bt = new BlasterTag(stack.getOrCreateNbt());

		if (!bt.isReady())
			return TypedActionResult.fail(stack);

		bt.shotTimer = bd.automaticRepeatTime;

		if (bt.isCooling())
		{
			if (world.isClient || !bt.canBypassCooling)
			{
				bt.serializeAsSubtag(stack);
				return TypedActionResult.fail(stack);
			}

			var profile = bd.cooling;

			final var cooldownTime = bt.coolingTimer / (float)bd.heat.capacity;

			final var primaryBypassStart = profile.primaryBypassTime - profile.primaryBypassTolerance;
			final var primaryBypassEnd = profile.primaryBypassTime + profile.primaryBypassTolerance;
			final var secondaryBypassStart = profile.secondaryBypassTime - profile.secondaryBypassTolerance;
			final var secondaryBypassEnd = profile.secondaryBypassTime + profile.secondaryBypassTolerance;

			var result = TypedActionResult.fail(stack);

			if (profile.primaryBypassTolerance > 0 && cooldownTime >= primaryBypassStart && cooldownTime <= primaryBypassEnd)
			{
				// TODO: primary bypass sound
				bt.coolingTimer = 0;
				bt.coolingMode = BlasterTag.COOLING_MODE_NONE;

				result = TypedActionResult.success(stack);
			}
			else if (profile.secondaryBypassTolerance > 0 && cooldownTime >= secondaryBypassStart && cooldownTime <= secondaryBypassEnd)
			{
				// TODO: secondary bypass sound
				bt.coolingTimer = 0;
				bt.coolingMode = BlasterTag.COOLING_MODE_NONE;

				result = TypedActionResult.success(stack);
			}
			else
			{
				// TODO: failed bypass sound
				bt.canBypassCooling = false;
				bt.coolingMode = BlasterTag.COOLING_MODE_PENALTY_BYPASS;
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
					{
						world.playSound(null, player.getBlockPos(), SwgSounds.Blaster.DRYFIRE, SoundCategory.PLAYERS, 1f, 1f);
					}

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

		bt.passiveCooldownTimer = bd.heat.passiveCooldownDelay;
		bt.heat += bd.heat.perRound;
		bt.shotsRemaining--;

		if (bt.heat > bd.heat.capacity)
		{
			// TODO: overheat sound
			bt.coolingTimer = bd.heat.capacity + bd.heat.overheatPenalty;
			bt.coolingMode = BlasterTag.COOLING_MODE_OVERHEAT;
			bt.canBypassCooling = true;
			bt.heat = 0;
		}

		var burst = bd.firingModes.contains(BlasterFiringMode.BURST) && bt.getFiringMode() == BlasterFiringMode.BURST;

		if (burst)
		{
			if (bt.burstTimer == 0)
				bt.burstTimer = bd.burstSize - 1;
			else
				bt.burstTimer--;

			bt.shotTimer = bd.burstRepeatTime;
		}

		bt.timeSinceLastShot = 0;

		if (!world.isClient)
		{
			var m = new Matrix4f();
			m.loadIdentity();

			m.multiply(new Quaternion(0, -player.getYaw(), 0, true));
			m.multiply(new Quaternion(player.getPitch(), 0, 0, true));

			var hS = (world.random.nextFloat() * 2 - 1) * bd.spread.horizontal;
			var vS = (world.random.nextFloat() * 2 - 1) * bd.spread.vertical;

			// TODO: stats customization
			float horizontalSpreadCoef = 1; // - bd.getBarrel().getHorizontalSpreadReduction();
			float verticalSpreadCoef = 1; // - bd.getBarrel().getVerticalSpreadReduction();

			if (bt.isAimingDownSights)
			{
				horizontalSpreadCoef = 0;
				verticalSpreadCoef = 0;
			}

			final var entityPitch = vS * verticalSpreadCoef;
			final var entityYaw = hS * horizontalSpreadCoef;

			m.multiply(new Quaternion(0, entityYaw, 0, true));
			m.multiply(new Quaternion(entityPitch, 0, 0, true));

			var fromDir = Matrix4fAccessUtil.transform(MathUtil.POSZ, m);

			var range = bd.range;
			var damage = bd.damage;

			var shouldRecoil = true;

			switch (bt.getFiringMode())
			{
				case SEMI_AUTOMATIC:
				case BURST:
				case AUTOMATIC:
					world.playSound(null, player.getBlockPos(), SwgSounds.getOrDefault(getSound(bd.id), SwgSounds.Blaster.FIRE_A280), SoundCategory.PLAYERS, 1, 1 + (float)world.random.nextGaussian() / 20);
					BlasterUtil.fireBolt(world, player, fromDir, range, damage, entity -> {
						entity.setProperties(player, player.getPitch() + entityPitch, player.getYaw() + entityYaw, 0.0F, 4.0F, 0);
						entity.setPos(player.getX(), player.getEyeY() - entity.getHeight() / 2f, player.getZ());
						entity.setHue(bd.boltColor);
					});
					break;
				case STUN:
					world.playSound(null, player.getBlockPos(), SwgSounds.Blaster.STUN, SoundCategory.PLAYERS, 1, 1 + (float)world.random.nextGaussian() / 20);
					BlasterUtil.fireStun(world, player, fromDir, range * 0.10f, entity -> {
						entity.setProperties(player, player.getPitch() + entityPitch, player.getYaw() + entityYaw, 0.0F, 1.25f, 0);
						entity.setPos(player.getX(), player.getEyeY() - entity.getHeight() / 2f, player.getZ());
					});
					shouldRecoil = false;
					break;
				case SLUGTHROWER:
					world.playSound(null, player.getBlockPos(), SwgSounds.getOrDefault(getSound(bd.id), SwgSounds.Blaster.FIRE_CYCLER), SoundCategory.PLAYERS, 1, 1 + (float)world.random.nextGaussian() / 20);
					BlasterUtil.fireSlug(world, player, fromDir, range, damage);
					break;
				case ION:
					world.playSound(null, player.getBlockPos(), SwgSounds.getOrDefault(getSound(bd.id), SwgSounds.Blaster.FIRE_ION), SoundCategory.PLAYERS, 1, 1 + (float)world.random.nextGaussian() / 20);
					BlasterUtil.fireIon(world, player, range, entity -> {
						entity.setProperties(player, player.getPitch() + entityPitch, player.getYaw() + entityYaw, 0.0F, 4.0F, 0);
						entity.setPos(player.getX(), player.getEyeY() - entity.getHeight() / 2f, player.getZ());
						entity.setHue(bd.boltColor);
					});
					break;
			}

			if (shouldRecoil)
			{
				var passedData = PlayerEvent.createBuffer(PlayerEvent.ACCUMULATE_RECOIL);
				var horizNoise = world.random.nextGaussian();
				horizNoise = horizNoise * 0.3 + 0.7 * Math.signum(horizNoise);

				passedData.writeFloat((float)(bd.recoil.horizontal * horizNoise));
				passedData.writeFloat((float)(bd.recoil.vertical * (0.7 + 0.3 * (world.random.nextGaussian() + 1) / 2)));
				ServerPlayNetworking.send((ServerPlayerEntity)player, SwgPackets.S2C.PacketPlayerEvent, passedData);
			}

			bt.serializeAsSubtag(stack);
		}

		return TypedActionResult.success(stack);
	}

	private Identifier getSound(Identifier id)
	{
		return new Identifier(id.getNamespace(), "blaster.fire." + id.getPath());
	}

	@Override
	public String getTranslationKey(ItemStack stack)
	{
		var tag = stack.getOrCreateNbt();

		var model = tag.getString("model");
		if (model.isEmpty())
			return super.getTranslationKey(stack);

		var bdId = new Identifier(model);

		return "item." + bdId.getNamespace() + ".blaster_" + bdId.getPath();
	}

	@Override
	public NbtCompound getDefaultTag(ItemConvertible item, int count)
	{
		var tag = new NbtCompound();

		tag.putString("model", Resources.id("a280").toString());

		return tag;
	}

	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks)
	{
		if (!this.isIn(group))
			return;

		var manager = Client.ResourceManagers.getBlasterManager();

		for (var entry : manager.getData().entrySet())
			stacks.add(forType(entry.getValue()));
	}

	private ItemStack forType(BlasterDescriptor descriptor)
	{
		var stack = new ItemStack(this);

		stack.getOrCreateNbt().putString("model", descriptor.id.toString());

		var bd = getBlasterDescriptorClient(stack);

		BlasterTag.mutate(stack, blasterTag -> {
			if (bd.firingModes.isEmpty())
				blasterTag.setFiringMode(BlasterFiringMode.SEMI_AUTOMATIC);
			else
				blasterTag.setFiringMode(bd.firingModes.get(0));
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
		var bd = getBlasterDescriptor(world, stack);

		BlasterTag.mutate(stack, blasterTag -> {
			if (blasterTag.coolingTimer > 0)
			{
				blasterTag.coolingTimer -= bd.heat.overheatDrainSpeed;
				if (blasterTag.coolingTimer == 0)
					blasterTag.coolingMode = BlasterTag.COOLING_MODE_NONE;
			}

			if (blasterTag.heat > 0 && blasterTag.passiveCooldownTimer == 0)
				blasterTag.heat -= bd.heat.drainSpeed;

			blasterTag.tick();
		});
	}

	@Override
	public boolean areStacksVisuallyEqual(ItemStack original, ItemStack updated)
	{
		if (!(original.getItem() instanceof BlasterItem) || original.getItem() != updated.getItem())
			return false;

		var bt1 = new BlasterTag(original.getOrCreateNbt());
		var bt2 = new BlasterTag(updated.getOrCreateNbt());
		return bt1.serialNumber == bt2.serialNumber;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public double getFovMultiplier(ItemStack stack, World world, PlayerEntity entity)
	{
		var bt = new BlasterTag(stack.getOrCreateNbt());
		var bd = BlasterItem.getBlasterDescriptor(world, stack);

		if (bd == null)
			return 1;

		if (bt.isAimingDownSights)
			return bd.adsZoom;

		return 1;
	}

	@Override
	public ImmutableMultimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack)
	{
		if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND)
		{
			var bt = new BlasterTag(stack.getOrCreateNbt());

			if (bt.isAimingDownSights)
				return ATTRIB_MODS_ADS;
		}

		return ImmutableMultimap.of();
	}
}
