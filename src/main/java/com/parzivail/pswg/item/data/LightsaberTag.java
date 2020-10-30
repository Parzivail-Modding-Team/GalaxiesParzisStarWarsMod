package com.parzivail.pswg.item.data;

import com.parzivail.util.nbt.TagSerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import java.util.function.Consumer;

public class LightsaberTag extends TagSerializer
{
	public static final byte TRANSITION_TICKS = 8;

	public boolean active;

	public byte transition;

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

	public boolean toggle()
	{
		if (transition != 0)
			return false;

		transition = active ? -TRANSITION_TICKS : TRANSITION_TICKS;
		active = !active;
		return true;
	}

	public void tick()
	{
		if (transition > 0)
			transition--;

		if (transition < 0)
			transition++;
	}

	public float getSize(float partialTicks)
	{
		if (transition == 0)
			return active ? 1 : 0;

		if (transition > 0)
			return 1 - (transition - partialTicks) / TRANSITION_TICKS;

		return -(transition + partialTicks) / TRANSITION_TICKS;
	}
}
