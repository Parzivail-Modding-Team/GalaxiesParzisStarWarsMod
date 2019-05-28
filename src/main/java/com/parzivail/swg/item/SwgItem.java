package com.parzivail.swg.item;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.UUID;

public class SwgItem extends Item
{
	private static final UUID precisionMovementSlowdownUUID = UUID.fromString("65a1759e-43ea-476f-85d1-717fc2a573f7");
	private static final AttributeModifier precisionMovementSlowdown = (new AttributeModifier(precisionMovementSlowdownUUID, "Precise Movement", -0.5D, 2)).setSaved(false);

	public final String name;

	public SwgItem(String name)
	{
		this.name = name;
		setUnlocalizedName(name);
		setRegistryName(Resources.modColon(this.name));
		setCreativeTab(StarWarsGalaxy.tab);
	}

	public static void applyPrecisionMovement(EntityPlayer player, boolean apply)
	{
		IAttributeInstance movement = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);

		if (apply)
		{
			if (movement.getModifier(precisionMovementSlowdownUUID) == null)
				movement.applyModifier(precisionMovementSlowdown);
		}
		else
		{
			if (movement.getModifier(precisionMovementSlowdownUUID) != null)
				movement.removeModifier(precisionMovementSlowdown);
		}
	}

	public boolean shouldUsePrecisionMovement(ItemStack stack, World world, EntityPlayer player)
	{
		return false;
	}

	public float getZoomLevel(ItemStack stack, World world, EntityPlayer player)
	{
		return 1;
	}
}
