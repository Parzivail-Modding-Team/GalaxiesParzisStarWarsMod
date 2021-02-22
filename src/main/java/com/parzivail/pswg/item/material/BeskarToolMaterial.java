package com.parzivail.pswg.item.material;

import com.parzivail.pswg.container.SwgItems;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public class BeskarToolMaterial implements ToolMaterial
{
	public static final BeskarToolMaterial INSTANCE = new BeskarToolMaterial();

	@Override
	public int getDurability()
	{
		return 2000;
	}

	@Override
	public float getMiningSpeedMultiplier()
	{
		return 10.0F;
	}

	@Override
	public float getAttackDamage()
	{
		return 5.0F;
	}

	@Override
	public int getMiningLevel()
	{
		return 5;
	}

	@Override
	public int getEnchantability()
	{
		return 16;
	}

	@Override
	public Ingredient getRepairIngredient()
	{
		return Ingredient.ofItems(SwgItems.Ingot.Beskar);
	}
}
