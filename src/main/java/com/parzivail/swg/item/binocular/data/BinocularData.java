package com.parzivail.swg.item.binocular.data;

import com.parzivail.util.item.ItemUtils;
import com.parzivail.util.item.NbtSerializable;
import net.minecraft.item.ItemStack;

public class BinocularData extends NbtSerializable<BinocularData>
{
	public boolean isZooming;
	public int zoomLevel;

	public BinocularData(ItemStack stack)
	{
		ItemUtils.ensureNbt(stack);
		deserialize(stack.stackTagCompound);
	}
}
