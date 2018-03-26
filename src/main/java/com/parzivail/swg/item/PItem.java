package com.parzivail.swg.item;

import com.parzivail.swg.Resources;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class PItem extends Item
{
	public final String name;
	private final String[] variants;
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	public PItem(String name)
	{
		this(name, new String[0]);
	}

	public PItem(String name, String[] variants)
	{
		this.name = name;
		this.variants = variants;
		this.setHasSubtypes(variants.length != 0);
		this.setTextureName(Resources.modColon(this.name));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		if (this.variants.length == 0)
			return Resources.itemDot(this.name);
		return Resources.itemDot(this.name, this.variants[stack.getItemDamage()]);
	}

	@SideOnly(Side.CLIENT)
	public boolean shouldRequestRenderState()
	{
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister)
	{
		if (this.variants.length == 0)
		{
			super.registerIcons(iconRegister);
			return;
		}

		this.icons = new IIcon[this.variants.length];
		for (int i = 0; i < this.icons.length; i++)
			this.icons[i] = iconRegister.registerIcon(Resources.MODID + ":" + this.name + "_" + variants[i]);
	}
}
