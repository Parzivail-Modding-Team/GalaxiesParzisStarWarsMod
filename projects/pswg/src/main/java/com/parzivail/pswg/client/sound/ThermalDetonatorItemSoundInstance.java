package com.parzivail.pswg.client.sound;

import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.pswg.item.ThermalDetonatorItem;
import com.parzivail.pswg.item.ThrowableExplosiveTag;
import com.parzivail.util.sound.DopplerSoundInstance;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;

@Environment(EnvType.CLIENT)
public class ThermalDetonatorItemSoundInstance extends GrenadeItemSoundInstance implements ISoftRepeatSound
{
	public ThermalDetonatorItemSoundInstance(PlayerEntity player)
	{
		super(player, SwgSounds.Explosives.THERMAL_DETONATOR_BEEP);
	}

	@Override
	public boolean isItem(ItemStack stack)
	{
		if (stack.getItem() instanceof ThermalDetonatorItem)
		{
			return true;
		}
		return false;
	}

	;
}
