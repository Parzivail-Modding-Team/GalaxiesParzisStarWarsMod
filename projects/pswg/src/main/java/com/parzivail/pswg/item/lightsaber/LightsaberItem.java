package com.parzivail.pswg.item.lightsaber;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.parzivail.pswg.Client;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.container.SwgEntities;
import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.pswg.data.SwgLightsaberManager;
import com.parzivail.pswg.entity.ThrownLightsaberEntity;
import com.parzivail.pswg.item.lightsaber.data.LightsaberDescriptor;
import com.parzivail.pswg.item.lightsaber.data.LightsaberTag;
import com.parzivail.util.client.TextUtil;
import com.parzivail.util.item.*;
import com.parzivail.util.math.Ease;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LightsaberItem extends SwordItem implements ItemStackEntityAttributeModifiers, ICustomVisualItemEquality, IDefaultNbtProvider, IItemEntityStackSetListener, IItemActionListener, IItemHotbarListener
{
	private final ImmutableMultimap<EntityAttribute, EntityAttributeModifier> attribModsOff;
	private final ImmutableMultimap<EntityAttribute, EntityAttributeModifier> attribModsOnMainhand;
	private final ImmutableMultimap<EntityAttribute, EntityAttributeModifier> attribModsOnOffhand;

	public LightsaberItem(Settings settings)
	{
		super(ToolMaterials.DIAMOND, 1, -2.4f, settings);

		ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", -2.4f, EntityAttributeModifier.Operation.ADDITION));
		this.attribModsOff = builder.build();

		builder = ImmutableMultimap.builder();
		builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", -2.4f, EntityAttributeModifier.Operation.ADDITION));
		builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", 15, EntityAttributeModifier.Operation.ADDITION));
		this.attribModsOnMainhand = builder.build();

		builder = ImmutableMultimap.builder();
		builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", -2.4f, EntityAttributeModifier.Operation.ADDITION));
		builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", 15, EntityAttributeModifier.Operation.ADDITION));
		this.attribModsOnOffhand = builder.build();
	}

	private static boolean isActive(ItemStack stack)
	{
		return !stack.isEmpty() && new LightsaberTag(stack.getOrCreateNbt()).active;
	}

	// Synthetic override of IrisItemLightProvider::getLightEmission
	public int getLightEmission(PlayerEntity player, ItemStack stack)
	{
		var config = Resources.CONFIG.getConfig();
		var lt = new LightsaberTag(stack.getOrCreateNbt());
		return (int)Math.ceil(config.view.lightsaberShaderBrightness * Ease.outCubic(lt.getLinearSize(1)));
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
				toggle(world, player, stack);
				break;
			case SECONDARY:
//				throwAsEntity(world, player, stack);
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
		return Text.translatable(this.getTranslationKey(stack), lt.owner);
	}

	@Override
	public NbtCompound getDefaultTag(ItemConvertible item, int count)
	{
		return new LightsaberTag().toSubtag();
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
	{
		super.appendTooltip(stack, world, tooltip, context);
		tooltip.add(Text.translatable("tooltip.pswg.lightsaber.info"));
//		tooltip.add(Text.translatable("tooltip.pswg.lightsaber.controls", TextUtil.stylizeKeybind(Client.KEY_PRIMARY_ITEM_ACTION.getBoundKeyLocalizedText()), TextUtil.stylizeKeybind(Client.KEY_SECONDARY_ITEM_ACTION.getBoundKeyLocalizedText())));
		tooltip.add(Text.translatable("tooltip.pswg.lightsaber.controls", TextUtil.stylizeKeybind(Client.KEY_PRIMARY_ITEM_ACTION.getBoundKeyLocalizedText())));
	}

	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks)
	{
		if (!this.isIn(group))
			return;

		for (var entry : SwgLightsaberManager.INSTANCE.getData().entrySet())
			stacks.add(forType(entry.getValue()));
	}

	private ItemStack forType(LightsaberDescriptor descriptor)
	{
		var stack = new ItemStack(this);

		LightsaberTag.mutate(stack, lightsaberTag -> {
			lightsaberTag.owner = descriptor.owner;
			lightsaberTag.hilt = new Identifier(descriptor.hilt);
			lightsaberTag.bladeHue = descriptor.bladeHue;
			lightsaberTag.bladeSaturation = descriptor.bladeSaturation;
			lightsaberTag.bladeValue = descriptor.bladeValue;
		});

		return stack;
	}

	@Override
	public ImmutableMultimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack)
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
	public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand)
	{
		final var stack = player.getStackInHand(hand);
		return new TypedActionResult<>(ActionResult.PASS, stack);
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
			if (!player.world.isClient && tag.active)
			{
				tag.active = false;
				tag.finalizeMovement();
				LightsaberItem.playSound(player.world, player, tag);
			}
		});
		return true;
	}
}
