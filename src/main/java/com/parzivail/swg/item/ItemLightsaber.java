package com.parzivail.swg.item;

import com.google.common.collect.Multimap;
import com.parzivail.swg.Resources;
import com.parzivail.swg.item.data.LightsaberData;
import com.parzivail.swg.item.data.LightsaberDescriptor;
import com.parzivail.swg.register.SoundRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemLightsaber extends SwgItem
{
	/**
	 * For item creation only. Use the descriptor of the LightsaberData for the stack instead.
	 */
	@Deprecated
	private final LightsaberDescriptor descriptor;

	public ItemLightsaber(LightsaberDescriptor d)
	{
		this(d, d.name);
	}

	public ItemLightsaber(LightsaberDescriptor d, String name)
	{
		super("lightsaber_" + name);
		descriptor = d;
		setMaxStackSize(1);
		setMaxDamage(0);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);

		if (player.isSneaking())
		{
			LightsaberData ld = new LightsaberData(stack);

			if (ld.openingState == 0)
			{
				ld.isOpen = !ld.isOpen;

				if (ld.isOpen)
				{
					SoundEvent sound = SoundRegister.getSound("lightsaber.start." + ld.descriptor.sounds.start);
					if (sound != null)
						world.playSound(player, player.getPosition(), sound, SoundCategory.PLAYERS, 1, 1);
				}
				else
				{
					SoundEvent sound = SoundRegister.getSound("lightsaber.stop." + ld.descriptor.sounds.stop);
					if (sound != null)
						world.playSound(player, player.getPosition(), sound, SoundCategory.PLAYERS, 1, 1);
				}
			}

			if (!world.isRemote)
				ld.serialize(stack.getTagCompound());
		}

		return super.onItemRightClick(world, player, hand);
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack)
	{
		if (!(entityLiving instanceof EntityPlayer))
			return super.onEntitySwing(entityLiving, stack);

		EntityPlayer player = (EntityPlayer)entityLiving;
		World world = player.world;

		LightsaberData ld = new LightsaberData(stack);
		SoundEvent sound = SoundRegister.getSound("lightsaber.swing." + ld.descriptor.sounds.swing);
		if (sound != null)
			world.playSound(player, player.getPosition(), sound, SoundCategory.PLAYERS, 1, 1 + (float)world.rand.nextGaussian() / 10);

		return super.onEntitySwing(entityLiving, stack);
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
			text.add(String.format("%s: %s", I18n.format(Resources.guiDot("lightsaber.coreColor")), String.format("#%06X", d.blade.coreColor & 0xFFFFFF)));
			text.add(String.format("%s: %s", I18n.format(Resources.guiDot("lightsaber.bladeColor")), String.format("#%06X", d.blade.glowColor & 0xFFFFFF)));
			text.add(String.format("%s: %s", I18n.format(Resources.guiDot("lightsaber.bladeLength")), d.blade.length));
			text.add(String.format("%s: %s", I18n.format(Resources.guiDot("lightsaber.unstable")), d.unstable));
		}
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
	{
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);

		if (slot == EntityEquipmentSlot.MAINHAND)
		{
			LightsaberData ld = new LightsaberData(stack);

			if (ld.isOpen)
				multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 15, 0));
			else
				multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 1, 0));
		}

		return multimap;
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		LightsaberData ld = new LightsaberData(stack);

		if (ld.descriptor == null)
			ld.descriptor = descriptor;

		if (ld.isOpen)
		{
			if (ld.openAnimation < 4)
			{
				ld.openAnimation++;
				ld.openingState = 1;
			}
			else
			{
				ld.openAnimation = 4;
				ld.openingState = 0;
			}
		}
		else
		{
			if (ld.openAnimation > 0)
			{
				ld.openAnimation--;
				ld.openingState = -1;
			}
			else
			{
				ld.openAnimation = 0;
				ld.openingState = 0;
			}
		}

		ld.serialize(stack.getTagCompound());
	}
}
