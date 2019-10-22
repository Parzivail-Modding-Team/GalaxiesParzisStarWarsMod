package com.parzivail.swg.item;

import com.google.common.collect.Multimap;
import com.parzivail.swg.Resources;
import com.parzivail.swg.item.data.LightsaberData;
import com.parzivail.swg.item.data.LightsaberDescriptor;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemLightsaber extends SwgItem
{
	public ItemLightsaber()
	{
		super("lightsaber");
		setMaxStackSize(1);
		setMaxDamage(0);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);

		if (player.isSneaking())
		{
			if (!world.isRemote)
			{
				LightsaberData bd = new LightsaberData(stack);

				if (bd.openingState == 0)
				{
					bd.isOpen = !bd.isOpen;

					//					if (bd.isOpen)
					//						Sfx.play(player, Resources.modColon("swg.fx.saber.start"), 1, 1);
					//					else
					//						Sfx.play(player, Resources.modColon("swg.fx.saber.stop"), 1, 1);
				}

				bd.serialize(stack.getTagCompound());
			}
		}

		return super.onItemRightClick(world, player, hand);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> text, ITooltipFlag flag)
	{
		LightsaberData ld = new LightsaberData(stack);
		LightsaberDescriptor d = ld.descriptor;

		if (d == null)
		{
			text.add(I18n.format(Resources.guiDot("lightsaber.blank")));
		}
		else
		{
			text.add(String.format("%s: %s", I18n.format(Resources.guiDot("lightsaber.coreColor")), String.format("#%06X", d.coreColor & 0xFFFFFF)));
			text.add(String.format("%s: %s", I18n.format(Resources.guiDot("lightsaber.bladeColor")), String.format("#%06X", d.bladeColor & 0xFFFFFF)));
			text.add(String.format("%s: %s", I18n.format(Resources.guiDot("lightsaber.bladeLength")), d.bladeLength));
			text.add(String.format("%s: %s", I18n.format(Resources.guiDot("lightsaber.unstable")), d.unstable));
		}
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
	{
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);

		if (slot == EntityEquipmentSlot.MAINHAND)
		{
			LightsaberData bd = new LightsaberData(stack);

			if (bd.isOpen)
				multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 15, 0));
			else
				multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 1, 0));
		}

		return multimap;
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		LightsaberData bd = new LightsaberData(stack);

		if (bd.isOpen)
		{
			if (bd.openAnimation < 4)
			{
				bd.openAnimation++;
				bd.openingState = 1;
			}
			else
			{
				bd.openAnimation = 4;
				bd.openingState = 0;
			}
		}
		else
		{
			if (bd.openAnimation > 0)
			{
				bd.openAnimation--;
				bd.openingState = -1;
			}
			else
			{
				bd.openAnimation = 0;
				bd.openingState = 0;
			}
		}

		bd.serialize(stack.getTagCompound());
	}
}
