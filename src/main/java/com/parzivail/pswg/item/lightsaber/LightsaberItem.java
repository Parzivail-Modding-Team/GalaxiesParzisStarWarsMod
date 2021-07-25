package com.parzivail.pswg.item.lightsaber;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.parzivail.pswg.Client;
import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.pswg.item.lightsaber.data.LightsaberDescriptor;
import com.parzivail.pswg.item.lightsaber.data.LightsaberTag;
import com.parzivail.util.item.ICustomVisualItemEquality;
import com.parzivail.util.item.IDefaultNbtProvider;
import com.parzivail.util.item.IItemEntityConsumer;
import com.parzivail.util.item.ItemStackEntityAttributeModifiers;
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
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class LightsaberItem extends SwordItem implements ItemStackEntityAttributeModifiers, ICustomVisualItemEquality, IDefaultNbtProvider, IItemEntityConsumer
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
		builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", 15, EntityAttributeModifier.Operation.ADDITION));
		this.attribModsOnMainhand = builder.build();

		builder = ImmutableMultimap.builder();
		builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", 15, EntityAttributeModifier.Operation.ADDITION));
		this.attribModsOnOffhand = builder.build();
	}

	private static boolean isActive(ItemStack stack)
	{
		return !stack.isEmpty() && new LightsaberTag(stack.getOrCreateTag()).active;
	}

	public static void toggle(World world, PlayerEntity player, ItemStack stack)
	{
		LightsaberTag.mutate(stack, lightsaberTag -> {
			var success = lightsaberTag.toggle();

			if (success && !world.isClient)
			{
				if (lightsaberTag.active)
					world.playSound(null, player.getBlockPos(), SwgSounds.Lightsaber.START_CLASSIC, SoundCategory.PLAYERS, 1f, 1f);
				else
					world.playSound(null, player.getBlockPos(), SwgSounds.Lightsaber.STOP_CLASSIC, SoundCategory.PLAYERS, 1f, 1f);
			}
		});
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
	{
		LightsaberTag.mutate(stack, LightsaberTag::tick);
	}

	@Override
	public void onItemEntityCreated(ItemEntity entity, ItemStack stack)
	{
		LightsaberTag.mutate(stack, lightsaberTag -> {
			lightsaberTag.finalizeMovement();
		});
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
		var lt = new LightsaberTag(stack.getOrCreateTag());
		return new TranslatableText(this.getTranslationKey(stack), lt.owner);
	}

	@Override
	public NbtCompound getDefaultTag(ItemConvertible item, int count)
	{
		return new LightsaberTag().toSubtag();
	}

	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks)
	{
		if (!this.isIn(group))
			return;

		var manager = Client.ResourceManagers.getLightsaberManager();

		for (var entry : manager.getData().entrySet())
			stacks.add(forType(entry.getValue()));
	}

	private ItemStack forType(LightsaberDescriptor descriptor)
	{
		var stack = new ItemStack(this);

		LightsaberTag.mutate(stack, lightsaberTag -> {
			lightsaberTag.owner = descriptor.owner;
			lightsaberTag.hilt = new Identifier(descriptor.hilt);
			lightsaberTag.bladeHue = descriptor.bladeHue;
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
		if (player.isSneaking())
		{
			toggle(world, player, stack);
			return new TypedActionResult<>(ActionResult.CONSUME, stack);
		}
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
		return true;
	}

	@Override
	public boolean areStacksVisuallyEqual(ItemStack original, ItemStack updated)
	{
		return true;
	}
}
