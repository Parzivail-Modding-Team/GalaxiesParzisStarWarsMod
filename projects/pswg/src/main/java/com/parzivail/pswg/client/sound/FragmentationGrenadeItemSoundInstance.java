package com.parzivail.pswg.client.sound;

import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.pswg.item.FragmentationGrenadeItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class FragmentationGrenadeItemSoundInstance extends GrenadeItemSoundInstance implements ISoftRepeatSound
{
	public FragmentationGrenadeItemSoundInstance(PlayerEntity player)
	{
		super(player, SwgSounds.Explosives.THERMAL_DETONATOR_BEEP);
	}

	@Override
	public boolean isItem(ItemStack stack)
	{
		if (stack.getItem() instanceof FragmentationGrenadeItem)
		{
			return true;
		}
		return false;
	}

	;
}
