package com.parzivail.pswg.features.lightsabers;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.parzivail.pswg.Client;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.container.SwgEntities;
import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.pswg.features.lightsabers.client.ThrownLightsaberEntity;
import com.parzivail.pswg.features.lightsabers.data.LightsaberDescriptor;
import com.parzivail.pswg.features.lightsabers.data.LightsaberTag;
import com.parzivail.tarkin.api.TarkinLang;
import com.parzivail.util.client.TextUtil;
import com.parzivail.util.item.*;
import com.parzivail.util.lang.ImplicitOverride;
import com.parzivail.util.math.ColorUtil;
import com.parzivail.util.math.Ease;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.List;

public class LightsaberItem extends SwordItem implements ICustomVisualItemEquality, IDefaultNbtProvider, IItemEntityStackSetListener, IItemActionListener, IItemHotbarListener
{
	@TarkinLang
	public static final String I18N_TOOLTIP_LIGHTSABER_INFO = Resources.tooltip("lightsaber.info");
	@TarkinLang
	public static final String I18N_TOOLTIP_LIGHTSABER_CONTROLS = Resources.tooltip("lightsaber.controls");

	private final ImmutableMultimap<EntityAttribute, EntityAttributeModifier> attribModsOff;
	private final ImmutableMultimap<EntityAttribute, EntityAttributeModifier> attribModsOnMainhand;
	private final ImmutableMultimap<EntityAttribute, EntityAttributeModifier> attribModsOnOffhand;
	private final Identifier model;
	private final LightsaberDescriptor descriptor;

	public LightsaberItem(Settings settings, Identifier model, LightsaberDescriptor descriptor)
	{
		super(ToolMaterials.DIAMOND, 1, -2.4f, settings);

		this.model = model;
		this.descriptor = descriptor;

		ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", -2.4f, EntityAttributeModifier.Operation.ADDITION));
		this.attribModsOff = builder.build();

		builder = ImmutableMultimap.builder();
		builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", -2.4f, EntityAttributeModifier.Operation.ADDITION));
		builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", 22, EntityAttributeModifier.Operation.ADDITION));
		this.attribModsOnMainhand = builder.build();

		builder = ImmutableMultimap.builder();
		builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", -2.4f, EntityAttributeModifier.Operation.ADDITION));
		builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", 22, EntityAttributeModifier.Operation.ADDITION));
		this.attribModsOnOffhand = builder.build();
	}

	public LightsaberDescriptor getDescriptor()
	{
		return descriptor;
	}

	private static boolean isActive(ItemStack stack)
	{
		return !stack.isEmpty() && new LightsaberTag(stack.getOrCreateNbt()).active;
	}

	@Override
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner)
	{
		return false;
	}

	@Override
	public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner)
	{
		return false;
	}

	@ImplicitOverride("IrisItemLightProvider::getLightEmission")
	public int getLightEmission(PlayerEntity player, ItemStack stack)
	{
		var config = Resources.CONFIG.getConfig();
		var lt = new LightsaberTag(stack.getOrCreateNbt());
		return (int)Math.ceil(config.view.lightsaberShaderBrightness * Ease.outCubic(lt.getLinearSize(1)));
	}

	@ImplicitOverride("IrisItemLightProvider::getLightColor")
	public Vector3f getLightColor(PlayerEntity player, ItemStack stack)
	{
		var lt = new LightsaberTag(stack.getOrCreateNbt());
		return ColorUtil.hsvToRgb(ColorUtil.hsvGetH(lt.bladeColor), ColorUtil.hsvGetS(lt.bladeColor), ColorUtil.hsvGetV(lt.bladeColor));
	}

	public static void toggle(World world, PlayerEntity player, ItemStack stack)
	{
		LightsaberTag.mutate(stack, lightsaberTag -> {
			var success = lightsaberTag.toggle();

			if (success && !world.isClient)
			{
				playSound(world, player, lightsaberTag);
			}
		});
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
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
	{
		final var stack = player.getStackInHand(hand);

		player.setCurrentHand(hand);
		return TypedActionResult.fail(stack);
	}

	public static void playSound(World world, PlayerEntity player, LightsaberTag lightsaberTag)
	{
		if (lightsaberTag.active)
			world.playSound(null, player.getBlockPos(), SwgSounds.Lightsaber.START_CLASSIC, SoundCategory.PLAYERS, 1f, 1f);
		else
			world.playSound(null, player.getBlockPos(), SwgSounds.Lightsaber.STOP_CLASSIC, SoundCategory.PLAYERS, 1f, 1f);
	}

	public static void throwAsEntity(World world, PlayerEntity player, ItemStack stack)
	{
		final var entity = new ThrownLightsaberEntity(SwgEntities.Misc.ThrownLightsaber, player, world, new LightsaberTag(stack.getOrCreateNbt()));
		entity.setVelocity(player, MathHelper.clamp(player.getPitch(), -89.9f, 89.9f), player.getYaw(), 0.0F, 0.6f, 0);
		entity.velocityModified = entity.velocityDirty = true;
		world.spawnEntity(entity);

		if (!player.getAbilities().creativeMode)
			stack.decrement(1);
	}

	@Override
	public void onItemAction(World world, PlayerEntity player, ItemStack stack, ItemAction action)
	{
		switch (action)
		{
			case PRIMARY:
				// TODO: hold button, show radial menu
				toggle(world, player, stack);
				break;
			case SECONDARY:
				break;
		}
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
	{
		LightsaberTag.mutate(stack, LightsaberTag::tick);
	}

	@Override
	public void onItemEntityStackSet(ItemEntity entity, ItemStack stack)
	{
		LightsaberTag.mutate(stack, LightsaberTag::finalizeMovement);
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker)
	{
		// maybe energy cost? otherwise leave empty, this is important to
		// prevent SwordItem from applying damage
		return true;
	}

	@Override
	public Text getName(ItemStack stack)
	{
		var lt = new LightsaberTag(stack.getOrCreateNbt());
		return Text.translatable(this.getTranslationKey(lt, stack), lt.owner);
	}

	public String getTranslationKey(LightsaberTag tag, ItemStack stack)
	{
		if (tag.owner == null)
			return getTranslationKey(tag.hilt);
		return "item.pswg.lightsaber";
	}

	public static String getTranslationKey(Identifier hiltId)
	{
		return "item.%s.lightsaber.%s".formatted(hiltId.getNamespace(), hiltId.getPath());
	}

	@Override
	public NbtCompound getDefaultTag(ItemConvertible item, int count)
	{
		var lightsaberTag = new LightsaberTag(new NbtCompound());
		lightsaberTag.owner = descriptor.ownerName;
		lightsaberTag.hilt = descriptor.id;
		lightsaberTag.bladeColor = descriptor.bladeColor;
		lightsaberTag.unstable = descriptor.defaultUnstable;

		var tag = new NbtCompound();
		lightsaberTag.serializeAsSubtag(tag);
		return tag;
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type)
	{
		super.appendTooltip(stack, context, tooltip, type);
		tooltip.add(Text.translatable(I18N_TOOLTIP_LIGHTSABER_INFO));
		//		tooltip.add(Text.translatable("tooltip.pswg.lightsaber.controls", TextUtil.stylizeKeybind(Client.KEY_PRIMARY_ITEM_ACTION.getBoundKeyLocalizedText()), TextUtil.stylizeKeybind(Client.KEY_SECONDARY_ITEM_ACTION.getBoundKeyLocalizedText())));
		tooltip.add(Text.translatable(I18N_TOOLTIP_LIGHTSABER_CONTROLS, TextUtil.stylizeKeybind(Client.KEY_PRIMARY_ITEM_ACTION.getBoundKeyLocalizedText())));
	}

	@Override
	public ImmutableMultimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot)
	{
		return switch (slot)
		{
			case MAINHAND -> isActive(stack) ? attribModsOnMainhand : attribModsOff;
			case OFFHAND -> isActive(stack) ? attribModsOnOffhand : attribModsOff;
			default -> ImmutableMultimap.of();
		};
	}

	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot)
	{
		return switch (slot)
		{
			case MAINHAND, OFFHAND -> attribModsOff;
			default -> ImmutableMultimap.of();
		};
	}

	@Override
	public boolean isDamageable()
	{
		return false;
	}

	@Override
	public boolean isEnchantable(ItemStack stack)
	{
		return false;
	}

	@Override
	public boolean areStacksVisuallyEqual(ItemStack original, ItemStack updated)
	{
		if (!(original.getItem() instanceof LightsaberItem && original.getItem() == updated.getItem()))
			return false;

		var origTag = new LightsaberTag(original.getOrCreateNbt());
		var newTag = new LightsaberTag(updated.getOrCreateNbt());

		return origTag.uid == newTag.uid;
	}

	@Override
	public boolean onItemDeselected(PlayerEntity player, ItemStack stack)
	{
		LightsaberTag.mutate(stack, tag -> {
			if (!player.getWorld().isClient && tag.active)
			{
				tag.active = false;
				tag.finalizeMovement();
				LightsaberItem.playSound(player.getWorld(), player, tag);
			}
		});
		return true;
	}
}
