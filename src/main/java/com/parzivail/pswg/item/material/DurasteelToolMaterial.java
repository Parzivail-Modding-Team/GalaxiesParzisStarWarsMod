package com.parzivail.pswg.item.material;

import com.parzivail.pswg.container.SwgItems;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public class DurasteelToolMaterial implements ToolMaterial
{

	public static final DurasteelToolMaterial INSTANCE = new DurasteelToolMaterial();

	@Override
	public int getDurability()
	{
		return 500;
	}

	@Override
	public float getMiningSpeedMultiplier()
	{
		return 7.0F;
	}

	@Override
	public float getAttackDamage()
	{
		return 2.5F;
	}

	@Override
	public int getMiningLevel()
	{
		return 3;
	}

	@Override
	public int getEnchantability()
	{
		return 10;
	}

	@Override
	public Ingredient getRepairIngredient()
	{
		return Ingredient.ofItems(SwgItems.Material.DurasteelIngot);
	}
}
