package com.parzivail.pswg.item.lightsaber;

import com.parzivail.pswg.Resources;
import com.parzivail.util.math.Ease;
import com.parzivail.util.nbt.TagSerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class LightsaberTag extends TagSerializer
{
	public static final Identifier SLUG = Resources.identifier("lightsaber");
	public static final byte TRANSITION_TICKS = 8;

	public boolean active;

	public byte transition;

	public int coreColor;
	public int bladeColor;
	public boolean unstable;

	public Identifier hilt;

	public LightsaberTag(CompoundTag source)
	{
		super(SLUG, source);
	}

	public LightsaberTag()
	{
		super(SLUG, new CompoundTag());

		active = false;
		transition = 0;

		coreColor = 0xFFFFFF;
		bladeColor = 0x0020FF;
		unstable = false;

		hilt = Resources.identifier("luke/rotj");
	}

	public static LightsaberTag fromRootTag(CompoundTag tag)
	{
		CompoundTag parent = new CompoundTag();
		parent.put(SLUG.toString(), tag);
		return new LightsaberTag(parent);
	}

	public static void mutate(ItemStack stack, Consumer<LightsaberTag> action)
	{
		CompoundTag nbt = stack.getOrCreateTag();
		LightsaberTag t = new LightsaberTag(nbt);
		action.accept(t);

		t.serializeAsSubtag(stack);
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
			return Ease.outCubic(1 - (transition - partialTicks) / TRANSITION_TICKS);

		return Ease.inCubic(-(transition + partialTicks) / TRANSITION_TICKS);
	}
}
