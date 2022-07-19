package com.parzivail.pswg.item.jetpack.data;

import com.parzivail.pswg.Resources;
import com.parzivail.util.nbt.TagSerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class JetpackTag extends TagSerializer
{
	public static final Identifier SLUG = Resources.id("jetpack");

	public boolean enabled;
	public boolean isHovering;
	public float throttle;

	public JetpackTag(NbtCompound source)
	{
		super(SLUG, source);
	}

	public JetpackTag()
	{
		super(SLUG, new NbtCompound());

		this.enabled = false;
		this.isHovering = false;
		this.throttle = 0;
	}

	public static JetpackTag fromRootTag(NbtCompound tag)
	{
		var parent = new NbtCompound();
		parent.put(SLUG.toString(), tag);
		return new JetpackTag(parent);
	}

	public static void mutate(ItemStack stack, Consumer<JetpackTag> action)
	{
		var nbt = stack.getOrCreateNbt();
		var t = new JetpackTag(nbt);
		action.accept(t);

		t.serializeAsSubtag(stack);
	}
}
