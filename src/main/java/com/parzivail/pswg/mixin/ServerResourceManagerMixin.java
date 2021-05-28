package com.parzivail.pswg.mixin;

import com.parzivail.pswg.access.IServerResourceManagerAccess;
import com.parzivail.pswg.data.SwgBlasterManager;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.registry.DynamicRegistryManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerResourceManager.class)
public class ServerResourceManagerMixin implements IServerResourceManagerAccess
{
	@Unique
	SwgBlasterManager blasterLoader;

	@Shadow
	@Final
	private ReloadableResourceManager resourceManager;

	@Inject(method = "Lnet/minecraft/resource/ServerResourceManager;<init>(Lnet/minecraft/util/registry/DynamicRegistryManager;Lnet/minecraft/server/command/CommandManager$RegistrationEnvironment;I)V", at = @At("TAIL"))
	private void init(DynamicRegistryManager registryManager, CommandManager.RegistrationEnvironment commandEnvironment, int functionPermissionLevel, CallbackInfo ci)
	{
		resourceManager.registerReloader(blasterLoader = new SwgBlasterManager());
	}

	@Override
	public SwgBlasterManager getBlasterLoader()
	{
		return blasterLoader;
	}
}
