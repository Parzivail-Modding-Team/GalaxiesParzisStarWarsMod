package com.parzivail.pswg.mixin;

import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftServer.class)
public interface MinecraftServerMixin
{
	@Accessor("serverResourceManager")
	ServerResourceManager getServerResourceManager();
}
