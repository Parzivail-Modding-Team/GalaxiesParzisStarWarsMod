package com.parzivail.pswg.mixin;

import com.parzivail.util.entity.IPrecisionEntity;
import com.parzivail.util.network.PreciseEntityVelocityUpdateS2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
@Environment(EnvType.CLIENT)
public class ClientPlayNetworkHandlerMixin
{
	@Shadow
	private ClientWorld world;

	@Inject(method = "onEntityVelocityUpdate(Lnet/minecraft/network/packet/s2c/play/EntityVelocityUpdateS2CPacket;)V", at = @At("TAIL"))
	private void onEntityVelocityUpdate(EntityVelocityUpdateS2CPacket packet, CallbackInfo ci)
	{
		Entity entity = this.world.getEntityById(packet.getId());
		if (!(entity instanceof IPrecisionEntity ipe))
			return;

		if (packet instanceof PreciseEntityVelocityUpdateS2CPacket pevu)
		{
			entity.setPosition(pevu.getPosition());
			entity.setVelocity(pevu.getVelocity());
			ipe.onPrecisionVelocityPacket(pevu);
		}
	}
}
