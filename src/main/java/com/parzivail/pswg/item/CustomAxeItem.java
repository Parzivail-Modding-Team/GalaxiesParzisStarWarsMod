package com.parzivail.pswg.item;

import net.minecraft.item.AxeItem;
import net.minecraft.item.ToolMaterial;

public class CustomAxeItem extends AxeItem
{
	public CustomAxeItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
		super(material, attackDamage, attackSpeed, settings);
	}
}