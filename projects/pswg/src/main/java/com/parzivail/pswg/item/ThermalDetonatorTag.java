package com.parzivail.pswg.item;

import com.parzivail.pswg.Resources;
import com.parzivail.util.nbt.TagSerializer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class ThermalDetonatorTag extends TagSerializer
{
	public static final Identifier SLUG = Resources.id("thermal_detonator");
	public boolean primed;
	public int ticksToExplosion;
	public boolean shouldRender;

	public ThermalDetonatorTag(NbtCompound source)
	{
		super(SLUG, source);
	}

	public ThermalDetonatorTag()
	{
		super(SLUG, new NbtCompound());
	}

	public static ThermalDetonatorTag fromRootTag(NbtCompound tag)
	{
		var parent = new NbtCompound();
		parent.put(SLUG.toString(), tag);
		return new ThermalDetonatorTag();
	}

	public void tick()
	{
		if (primed)
		{
			ticksToExplosion--;
		}
	}
}
