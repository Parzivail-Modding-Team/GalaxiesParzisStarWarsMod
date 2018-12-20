package com.parzivail.swg.item.lightsaber;

import com.parzivail.swg.item.blaster.data.BlasterData;
import com.parzivail.util.item.ItemUtils;
import com.parzivail.util.item.NbtSave;
import com.parzivail.util.item.NbtSerializable;
import net.minecraft.item.ItemStack;

public class LightsaberData extends NbtSerializable<BlasterData>
{
	@NbtSave
	public boolean isOpen;
	@NbtSave
	public int openingState;
	@NbtSave
	public int openAnimation;
	@NbtSave
	public LightsaberDescriptor descriptor;

	public LightsaberData(ItemStack stack)
	{
		ItemUtils.ensureNbt(stack);
		deserialize(stack.stackTagCompound);
	}
}
