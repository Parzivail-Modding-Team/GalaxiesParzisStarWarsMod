package com.parzivail.pswg.mixin;

import com.parzivail.pswg.Client;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLoginNetworkHandler.class)
public class ClientLoginNetworkHandlerMixin
{
	@Inject(method = "onSuccess(Lnet/minecraft/network/packet/s2c/login/LoginSuccessS2CPacket;)V", at = @At("TAIL"))
	private void onLoginSuccess(LoginSuccessS2CPacket packet, CallbackInfo ci)
	{
		Client.ResourceManagers.registerNonreloadableManagers();
	}
}
