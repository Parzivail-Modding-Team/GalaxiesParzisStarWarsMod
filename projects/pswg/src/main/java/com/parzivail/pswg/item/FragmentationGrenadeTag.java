package com.parzivail.pswg.item;

import com.parzivail.pswg.Resources;
import com.parzivail.util.nbt.TagSerializer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class FragmentationGrenadeTag extends TagSerializer
{
	public static final Identifier SLUG = Resources.id("fragmentation_grenade");
	public boolean primed;
	public int ticksToExplosion;
	public boolean shouldRender;

	public FragmentationGrenadeTag(NbtCompound source)
	{
		super(SLUG, source);
	}

	public FragmentationGrenadeTag()
	{
		super(SLUG, new NbtCompound());
	}

	public static FragmentationGrenadeTag fromRootTag(NbtCompound tag)
	{
		var parent = new NbtCompound();
		parent.put(SLUG.toString(), tag);
		return new FragmentationGrenadeTag();
	}

	public void tick()
	{
		if (primed)
		{
			ticksToExplosion--;
		}
	}
}
