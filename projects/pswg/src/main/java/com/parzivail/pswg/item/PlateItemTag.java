package com.parzivail.pswg.item;

import com.parzivail.pswg.Resources;
import com.parzivail.util.nbt.TagSerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class PlateItemTag extends TagSerializer
{
	public List<ItemStack> FOODS = new ArrayList<>();
	public static final Identifier SLUG = Resources.id("plate");

	public PlateItemTag(NbtCompound source)
	{
		super(SLUG, source);
	}

	public PlateItemTag()
	{
		super(SLUG, new NbtCompound());
	}

	public static PlateItemTag fromRootTag(NbtCompound tag)
	{
		var parent = new NbtCompound();
		parent.put(SLUG.toString(), tag);
		return new PlateItemTag();
	}
}
