package com.parzivail.pswg.item;

import com.google.common.collect.Multimap;
import com.parzivail.util.item.ItemStackEntityAttributeModifiers;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class LightsaberItem extends SwordItem implements ItemStackEntityAttributeModifiers
{
	public LightsaberItem(Settings settings)
	{
		super(ToolMaterials.DIAMOND, 1, -2.4f, settings);
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
	public void getAttributeModifiers(EquipmentSlot slot, ItemStack stack, Multimap<String, EntityAttributeModifier> attributes)
	{
		if (slot == EquipmentSlot.MAINHAND && isActive(stack)) {
			attributes.removeAll(EntityAttributes.ATTACK_DAMAGE.getId());
			//attributes.removeAll(EntityAttributes.ATTACK_SPEED.getId());

			attributes.put(EntityAttributes.ATTACK_DAMAGE.getId(), new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_UUID, "Weapon modifier", 15, EntityAttributeModifier.Operation.ADDITION));
			//attributes.put(EntityAttributes.ATTACK_SPEED.getId(), new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_UUID, "Weapon modifier", 3, EntityAttributeModifier.Operation.ADDITION));
		}
	}

	@Override
	public Multimap<String, EntityAttributeModifier> getModifiers(EquipmentSlot slot)
	{
		Multimap<String, EntityAttributeModifier> multimap = super.getModifiers(slot);
		if (slot == EquipmentSlot.MAINHAND) {
		   multimap.put(EntityAttributes.ATTACK_SPEED.getId(), new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_UUID, "Weapon modifier", -2.4f, EntityAttributeModifier.Operation.ADDITION));
		}

		return multimap;
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
