package com.parzivail.pswg.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerPlayerEntity.class)
public interface ServerPlayerEntityAccessor
{
	@Invoker("incrementScreenHandlerSyncId")
	void invokeIncrementScreenHandlerSyncId();

	@Invoker("onScreenHandlerOpened")
	void invokeOnScreenHandlerOpened(ScreenHandler screenHandler);

	@Accessor("screenHandlerSyncId")
	int getScreenHandlerSyncId();
}
