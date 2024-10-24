package dev.pswg.mixin.client;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ClientPlayerInteractionManager.class)
public interface Accessor$ClientPlayerInteractionManager
{
	@Invoker
	void invokeSyncSelectedSlot();
}
