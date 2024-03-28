package com.parzivail.pswg.client.sound;

import com.parzivail.pswg.item.ThrowableExplosiveTag;
import com.parzivail.util.sound.DopplerSoundInstance;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

@Environment(EnvType.CLIENT)
public class GrenadeItemSoundInstance extends DopplerSoundInstance implements ISoftRepeatSound
{
	private final PlayerEntity player;

	public GrenadeItemSoundInstance(PlayerEntity player, SoundEvent soundEvent)
	{
		super(player, soundEvent, SoundCategory.PLAYERS, SoundInstance.createRandom());
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

		var foundItem = false;

		if (isItem(player.getMainHandStack()) && isPrimed(player.getMainHandStack()))
			foundItem = true;

		if (isItem(player.getOffHandStack()) && isPrimed(player.getOffHandStack()))
			foundItem = true;

		if (!areConditionsMet(player))
		{
			this.setDone();
			return;
		}
		if (foundItem)
			volume = 0.75f;
		else
			volume = 0.25f;

		this.x = (float)this.player.getX();
		this.y = (float)this.player.getY();
		this.z = (float)this.player.getZ();
	}

	public boolean areConditionsMet(PlayerEntity player)
	{
		for (int i = 0; i < player.getInventory().size(); i++)
			if (isItem(player.getInventory().getStack(i)) && isPrimed(player.getInventory().getStack(i)))
				return true;
		return false;
	}

	public boolean isItem(ItemStack stack)
	{
		return false;
	}

	;

	private boolean isPrimed(ItemStack stack)
	{
		if (!(isItem(stack)))
			return false;

		var tet = new ThrowableExplosiveTag(stack.getOrCreateNbt());
		return tet.primed;
	}
}
