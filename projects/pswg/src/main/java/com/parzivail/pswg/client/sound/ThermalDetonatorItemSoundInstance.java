package com.parzivail.pswg.client.sound;

import com.parzivail.pswg.container.SwgComponents;
import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.pswg.item.ThermalDetonatorComponent;
import com.parzivail.pswg.item.ThermalDetonatorItem;
import com.parzivail.util.sound.DopplerSoundInstance;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;

@Environment(EnvType.CLIENT)
public class ThermalDetonatorItemSoundInstance extends DopplerSoundInstance implements ISoftRepeatSound
{
	private final PlayerEntity player;

	public ThermalDetonatorItemSoundInstance(PlayerEntity player)
	{
		super(player, SwgSounds.Explosives.THERMAL_DETONATOR_BEEP, SoundCategory.PLAYERS, SoundInstance.createRandom());
		this.player = player;
		this.repeat = true;
		this.repeatDelay = 0;
		this.volume = 0.75F;
		this.x = (float)player.getX();
		this.y = (float)player.getY();
		this.z = (float)player.getZ();
	}

	@Override
	public boolean canPlay()
	{
		return !this.player.isSilent();
	}

	@Override
	public boolean shouldAlwaysPlay()
	{
		return true;
	}

	public PlayerEntity getPlayer()
	{
		return player;
	}

	@Override
	public void tick()
	{
		super.tick();

		if (this.player.isRemoved())
		{
			this.setDone();
			return;
		}

		var foundDetonator = false;

		if (player.getMainHandStack().getItem() instanceof ThermalDetonatorItem && isPrimed(player.getMainHandStack()))
			foundDetonator = true;

		if (!foundDetonator && player.getOffHandStack().getItem() instanceof ThermalDetonatorItem && isPrimed(player.getOffHandStack()))
			foundDetonator = true;

		if (!areConditionsMet(player))
		{
			this.setDone();
			return;
		}
		if (foundDetonator)
			volume = 0.75f;
		else
			volume = 0.25f;

		this.x = (float)this.player.getX();
		this.y = (float)this.player.getY();
		this.z = (float)this.player.getZ();
	}

	public static boolean areConditionsMet(PlayerEntity player)
	{
		for (int i = 0; i < player.getInventory().size(); i++)
			if (player.getInventory().getStack(i).getItem() instanceof ThermalDetonatorItem && isPrimed(player.getInventory().getStack(i)))
					return true;
		return false;
	}

	private static boolean isPrimed(ItemStack stack)
	{
		ThermalDetonatorComponent tdc = stack.get(SwgComponents.ThermalDetonator);
		if (tdc == null)
			return false;

		return tdc.primed();
	}
}
