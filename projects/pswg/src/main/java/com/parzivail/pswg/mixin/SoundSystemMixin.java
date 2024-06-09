package com.parzivail.pswg.mixin;

import com.parzivail.pswg.client.sound.ISoftRepeatSound;
import com.parzivail.pswg.client.sound.timeline.SoundTimelineManager;
import net.minecraft.client.sound.Channel;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.Map;

@Mixin(SoundSystem.class)
public class SoundSystemMixin
{
	@Shadow
	private int ticks;

	@Shadow
	@Final
	private Map<SoundInstance, Integer> startTicks;

	@Inject(method = "tick()V", at = @At("TAIL"))
	public void tick(CallbackInfo ci)
	{
		SoundTimelineManager.tick(ticks);
	}

	@Inject(method = "canRepeatInstantly(Lnet/minecraft/client/sound/SoundInstance;)Z", at = @At("HEAD"), cancellable = true)
	private static void isRepeatDelayed(SoundInstance sound, CallbackInfoReturnable<Boolean> cir)
	{
		if (sound instanceof ISoftRepeatSound)
			// Soft-repeating sounds repeat in software and not
			// using OpenAL so we set this to true so the sound
			// manager repeats it manually, as if it had a delay
			cir.setReturnValue(true);
	}

	@Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Multimap;put(Ljava/lang/Object;Ljava/lang/Object;)Z", shift = At.Shift.AFTER, remap = false))
	public void play(SoundInstance sound, CallbackInfo ci)
	{
		SoundTimelineManager.track(ticks, sound);
	}

	@Inject(method = "tick()V", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
	public void repeat(CallbackInfo ci, Iterator<Map.Entry<SoundInstance, Channel.SourceManager>> iterator, Map.Entry<SoundInstance, Channel.SourceManager> entry, Channel.SourceManager sourceManager2, SoundInstance soundInstance, float h, int i)
	{
		SoundTimelineManager.track(startTicks.get(soundInstance), soundInstance);
	}
}
