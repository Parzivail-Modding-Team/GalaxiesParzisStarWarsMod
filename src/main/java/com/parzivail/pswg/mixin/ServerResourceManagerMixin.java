package com.parzivail.pswg.mixin;

import com.parzivail.pswg.Galaxies;
import com.parzivail.pswg.access.IServerResourceManagerAccess;
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
	Galaxies.ResourceManagers resourceManagers;

	@Shadow
	@Final
	private ReloadableResourceManager resourceManager;

	@Inject(method = "<init>(Lnet/minecraft/util/registry/DynamicRegistryManager;Lnet/minecraft/server/command/CommandManager$RegistrationEnvironment;I)V", at = @At("TAIL"))
	private void init(DynamicRegistryManager registryManager, CommandManager.RegistrationEnvironment commandEnvironment, int functionPermissionLevel, CallbackInfo ci)
	{
		resourceManagers = new Galaxies.ResourceManagers(resourceManager);
	}

	@Override
	public Galaxies.ResourceManagers getResourceManagers()
	{
		return resourceManagers;
	}
}
