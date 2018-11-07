package com.parzivail.swg.item.blaster.data;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public abstract class BlasterAttachment
{
	public final BlasterAttachmentType type;
	public final String name;
	public final int price;
	private String infoText;

	public BlasterAttachment(BlasterAttachmentType type, String name, int price)
	{
		this.type = type;
		this.name = name;
		this.price = price;
	}

	public int getId()
	{
		return name.hashCode();
	}

	public abstract String getInfoText();

	@SideOnly(Side.CLIENT)
	public void drawInfoCard(ScaledResolution sr, EntityPlayer player, ItemStack stack)
	{
	}
}
