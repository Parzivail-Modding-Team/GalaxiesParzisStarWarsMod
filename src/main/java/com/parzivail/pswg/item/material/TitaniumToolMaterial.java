package com.parzivail.pswg.item.material;

import com.parzivail.pswg.container.SwgItems;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public class TitaniumToolMaterial implements ToolMaterial
{

	public static final TitaniumToolMaterial INSTANCE = new TitaniumToolMaterial();

	@Override
	public int getDurability() {
		return 1000;
	}
	@Override
	public float getMiningSpeedMultiplier() {
		return 8.0F;
	}
	@Override
	public float getAttackDamage() {
		return 3.5F;
	}
	@Override
	public int getMiningLevel() {
		return 4;
	}
	@Override
	public int getEnchantability() {
		return 12;
	}
	@Override
	public Ingredient getRepairIngredient() {
		return Ingredient.ofItems(SwgItems.Ingot.Titanium);
	}
}