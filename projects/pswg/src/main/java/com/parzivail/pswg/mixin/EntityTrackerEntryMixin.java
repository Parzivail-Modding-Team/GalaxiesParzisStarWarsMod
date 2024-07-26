package com.parzivail.pswg.mixin;

import com.parzivail.util.entity.IPrecisionVelocityEntity;
import com.parzivail.util.network.PreciseEntityVelocityUpdateS2CPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.EntityTrackerEntry;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityTrackerEntry.class)
public abstract class EntityTrackerEntryMixin
{
	@Final
	@Shadow
	private Entity entity;

	@Inject(method = "tick()V", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/server/network/EntityTrackerEntry;trackingTick:I"), cancellable = true)
	void tick(CallbackInfo ci)
	{
		if (!(entity instanceof IPrecisionVelocityEntity))
			return;

		if (this.entity.velocityModified)
		{
			this.sendSyncPacket(ServerPlayNetworking.createS2CPacket(new PreciseEntityVelocityUpdateS2CPacket(this.entity)));
			this.entity.velocityModified = false;
		}
		ci.cancel();
	}

	@Shadow
	protected abstract void sendSyncPacket(Packet<?> packet);
}
