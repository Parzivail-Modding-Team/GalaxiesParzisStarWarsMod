package com.parzivail.pswg.item.material;

import com.google.common.base.Suppliers;
import com.parzivail.pswg.container.SwgItems;
import net.minecraft.block.Block;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.TagKey;

import java.util.function.Supplier;

public enum PswgToolMaterials implements ToolMaterial
{
	// FIXME: null won't work ofc, temporary hack
	BESKAR(null, 2000, 10.0F, 5.0F, 16, () -> Ingredient.ofItems(SwgItems.Material.BeskarIngot)),
	DURASTEEL(null, 500, 7.0F, 2.5F, 10, () -> Ingredient.ofItems(SwgItems.Material.DurasteelIngot)),
	TITANIUM(null, 1000, 8.0F, 3.5F, 12, () -> Ingredient.ofItems(SwgItems.Material.TitaniumIngot)),
	;
	private final TagKey<Block> inverseTag;
	private final int itemDurability;
	private final float miningSpeed;
	private final float attackDamage;
	private final int enchantability;
	private final Supplier<Ingredient> repairIngredient;

	PswgToolMaterials(
			final TagKey<Block> inverseTag,
			final int itemDurability,
			final float miningSpeed,
			final float attackDamage,
			final int enchantability,
			final Supplier<Ingredient> repairIngredient
	) {
		this.inverseTag = inverseTag;
		this.itemDurability = itemDurability;
		this.miningSpeed = miningSpeed;
		this.attackDamage = attackDamage;
		this.enchantability = enchantability;
		this.repairIngredient = Suppliers.memoize(repairIngredient::get);
	}

	@Override
	public int getDurability() {
		return this.itemDurability;
	}

	@Override
	public float getMiningSpeedMultiplier() {
		return this.miningSpeed;
	}

	@Override
	public float getAttackDamage() {
		return this.attackDamage;
	}

	@Override
	public TagKey<Block> getInverseTag() {
		return this.inverseTag;
	}

	@Override
	public int getEnchantability() {
		return this.enchantability;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return this.repairIngredient.get();
	}
}
