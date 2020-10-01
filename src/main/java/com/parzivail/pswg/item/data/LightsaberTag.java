package com.parzivail.pswg.item.data;

import com.parzivail.util.nbt.TagSerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import java.util.function.Consumer;

public class LightsaberTag extends TagSerializer
{
	public boolean active;

	public LightsaberTag(CompoundTag source)
	{
		super(source);
	}

	public static void mutate(ItemStack stack, Consumer<LightsaberTag> action)
	{
		CompoundTag nbt = stack.getOrCreateTag();
		LightsaberTag t = new LightsaberTag(nbt);
		action.accept(t);
		stack.setTag(t.serialize());
	}
}
