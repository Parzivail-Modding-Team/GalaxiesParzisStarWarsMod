package com.parzivail.pswg.mixin;

import com.parzivail.pswg.GalaxiesMain;
import com.parzivail.pswg.entity.ShipEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin
{
	@Shadow
	private ClientWorld world;

	@Inject(method = "onEntitySpawn(Lnet/minecraft/client/network/packet/EntitySpawnS2CPacket;)V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/network/packet/EntitySpawnS2CPacket;getEntityTypeId()Lnet/minecraft/entity/EntityType;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private void onEntitySpawn(EntitySpawnS2CPacket packet, CallbackInfo ci, double x, double y, double z, EntityType<?> type)
	{
		Entity entity = null;
		if (type == GalaxiesMain.EntityTypeShip)
		{
			entity = ShipEntity.create(this.world);
		}
		if (entity != null)
		{
			int i = packet.getId();
			entity.setVelocity(Vec3d.ZERO);
			entity.updatePosition(x, y, z);
			entity.updateTrackedPosition(x, y, z);
			entity.pitch = (float)(packet.getPitch() * 360) / 256.0F;
			entity.yaw = (float)(packet.getYaw() * 360) / 256.0F;
			entity.setEntityId(i);
			entity.setUuid(packet.getUuid());
			this.world.addEntity(i, entity);
			ci.cancel();
		}
	}
}
