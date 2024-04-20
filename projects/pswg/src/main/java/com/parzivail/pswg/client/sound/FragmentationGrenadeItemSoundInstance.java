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
		super(player, SwgSounds.Explosives.FRAGMENTATION_GRENADE_BEEP);
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

	@Override
	public void adjustVolume(boolean foundItem)
	{
		if (foundItem)
		{
			volume = 0.3f;
		}
		else
		{
			volume = 0.1f;
		}
	}

	;
}
