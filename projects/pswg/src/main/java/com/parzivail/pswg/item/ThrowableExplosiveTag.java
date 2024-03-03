package com.parzivail.pswg.item;

import com.parzivail.pswg.Resources;
import com.parzivail.util.nbt.TagSerializer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class ThrowableExplosiveTag extends TagSerializer
{
	public static final Identifier SLUG = Resources.id("throwable_explosive");
	public boolean primed;
	public int ticksToExplosion;
	public boolean shouldRender;

	public ThrowableExplosiveTag(NbtCompound source)
	{
		super(SLUG, source);
	}

	public ThrowableExplosiveTag()
	{
		super(SLUG, new NbtCompound());
	}

	public static ThrowableExplosiveTag fromRootTag(NbtCompound tag)
	{
		var parent = new NbtCompound();
		parent.put(SLUG.toString(), tag);
		return new ThrowableExplosiveTag();
	}

	public void tick()
	{
		if (primed)
		{
			ticksToExplosion--;
		}
	}
}
