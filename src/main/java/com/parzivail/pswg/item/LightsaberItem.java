package com.parzivail.pswg.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.parzivail.util.item.ItemStackEntityAttributeModifiers;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class LightsaberItem extends SwordItem implements ItemStackEntityAttributeModifiers
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

	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks)
	{
		if (this.isIn(group))
		{
			// Custom lightsaber configurations for creative mode are added here (once it is configurable)
			stacks.add(new ItemStack(this));
		}
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker)
	{
		// maybe energy cost? otherwise leave empty, this is important to
		// prevent SwordItem from applying damage
		return true;
	}

	@Override
	public ImmutableMultimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack)
	{
		if (isActive(stack))
		{
			if (slot == EquipmentSlot.MAINHAND)
				return attribModsOnMainhand;
			else
				return attribModsOnOffhand;
		}
		else
			return attribModsOff;
	}

	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot)
	{
		if (slot == EquipmentSlot.MAINHAND)
			return attribModsOnMainhand;
		else
			return attribModsOnOffhand;
	}

	private static boolean isActive(ItemStack stack)
	{
		return !stack.isEmpty() && stack.getTag() != null && stack.getTag().getBoolean("active");
	}

	@Override
	public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
		final ItemStack stack = player.getStackInHand(hand);
		if (player.isSneaking()) {
			switchActive(stack);
			return new TypedActionResult<>(ActionResult.SUCCESS, stack);
		}
		return new TypedActionResult<>(ActionResult.PASS, stack);
	}

	private static void switchActive(ItemStack stack)
	{
		stack.getOrCreateTag().putBoolean("active", !isActive(stack));
	}

	@Override
	public boolean isDamageable() {
		return false;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) { return true; }
}
