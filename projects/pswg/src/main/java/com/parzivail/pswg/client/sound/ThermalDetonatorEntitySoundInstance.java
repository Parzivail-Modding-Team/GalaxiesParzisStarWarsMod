package com.parzivail.pswg.client.sound;

import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.pswg.entity.ThermalDetonatorEntity;
import com.parzivail.pswg.item.ThermalDetonatorItem;
import com.parzivail.pswg.item.ThermalDetonatorTag;
import com.parzivail.util.sound.DopplerSoundInstance;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ThermalDetonatorEntitySoundInstance extends MovingSoundInstance
{
	private final ThermalDetonatorEntity detonatorEntity;

	public ThermalDetonatorEntitySoundInstance(ThermalDetonatorEntity detonatorEntity)
	{
		super(SwgSounds.Explosives.THERMAL_DETONATOR_BEEP, SoundCategory.PLAYERS, SoundInstance.createRandom());
		this.detonatorEntity = detonatorEntity;
		this.repeat = true;
		this.repeatDelay = 0;
		this.volume = 1F;
		this.x = (float)detonatorEntity.getX();
		this.y = (float)detonatorEntity.getY();
		this.z = (float)detonatorEntity.getZ();
	}

	@Override
	public void tick()
	{

		if (detonatorEntity.isRemoved() || !areConditionsMet(detonatorEntity))
		{
			setDone();
			return;
		}
		x = (float)this.detonatorEntity.getX();
		y = (float)this.detonatorEntity.getY();
		z = (float)this.detonatorEntity.getZ();
		float distanceToPlayer32 = 32;
		if (detonatorEntity.getWorld().getClosestPlayer(detonatorEntity, 32) != null)
		{
			distanceToPlayer32 = detonatorEntity.getWorld().getClosestPlayer(detonatorEntity, 32).distanceTo(detonatorEntity);
		}
		volume = ((32 - distanceToPlayer32) / 32);
		//detonatorEntity.getWorld().getClosestPlayer(detonatorEntity, 32).sendMessage(Text.of(""+volume), true);

	}

	public static boolean areConditionsMet(ThermalDetonatorEntity tde)
	{
		if (tde.isPrimed())
		{
			return true;
		}
		return false;
	}
}
