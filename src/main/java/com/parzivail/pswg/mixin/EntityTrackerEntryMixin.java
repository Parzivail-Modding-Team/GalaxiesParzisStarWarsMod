package com.parzivail.pswg.mixin;

import com.parzivail.pswg.container.SwgPackets;
import com.parzivail.util.entity.IPrecisionEntity;
import com.parzivail.util.network.PreciseEntityVelocityUpdateS2CPacket;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
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
		if (!(entity instanceof IPrecisionEntity))
			return;

		if (this.entity.velocityModified || this.entity.age == 0)
		{
			var passedData = new PacketByteBuf(Unpooled.buffer());
			new PreciseEntityVelocityUpdateS2CPacket(this.entity).write(passedData);
			this.sendSyncPacket(ServerPlayNetworking.createS2CPacket(SwgPackets.S2C.PacketPreciseEntityVelocityUpdate, passedData));
			this.entity.velocityModified = false;
		}
		ci.cancel();
	}

	@Shadow
	protected abstract void sendSyncPacket(Packet<?> packet);
}
