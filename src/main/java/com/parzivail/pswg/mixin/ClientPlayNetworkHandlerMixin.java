package com.parzivail.pswg.mixin;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.sound.LightsaberThrownSoundInstance;
import com.parzivail.pswg.container.SwgEntities;
import com.parzivail.pswg.entity.ThrownLightsaberEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.text.*;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPlayNetworkHandler.class)
@Environment(EnvType.CLIENT)
public class ClientPlayNetworkHandlerMixin
{
	@Shadow
	private MinecraftClient client;

	@Shadow
	private ClientWorld world;

	@Inject(method = "onEntitySpawn(Lnet/minecraft/network/packet/s2c/play/EntitySpawnS2CPacket;)V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/network/packet/s2c/play/EntitySpawnS2CPacket;getEntityTypeId()Lnet/minecraft/entity/EntityType;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private void onEntitySpawn(EntitySpawnS2CPacket packet, CallbackInfo ci, double x, double y, double z, EntityType<?> type)
	{
		if (SwgEntities.entityTypes.contains(type))
		{
			var entity = type.create(this.world);

			assert entity != null;

			if (entity instanceof ThrownLightsaberEntity)
				Client.minecraft.getSoundManager().play(new LightsaberThrownSoundInstance((ThrownLightsaberEntity)entity));

			var i = packet.getId();
			entity.setVelocity(Vec3d.ZERO);
			entity.setPosition(x, y, z);
			entity.updateTrackedPosition(x, y, z);
			entity.setPitch((float)(packet.getPitch() * 360) / 256.0F);
			entity.setYaw((float)(packet.getYaw() * 360) / 256.0F);
			entity.setEntityId(i);
			entity.setUuid(packet.getUuid());
			this.world.addEntity(i, entity);
			ci.cancel();
		}
	}

	@Inject(method = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;onGameJoin(Lnet/minecraft/network/packet/s2c/play/GameJoinS2CPacket;)V", at = @At("TAIL"))
	private void onJoinWorld(GameJoinS2CPacket packet, CallbackInfo ci)
	{
		if (client.player != null && Resources.REMOTE_VERSION != null)
		{
			Text versionText = new LiteralText(Resources.REMOTE_VERSION.name)
					.styled((style) -> style
					        .withItalic(true)
					);
			Text urlText = new LiteralText("https://pswg.dev/download.html")
					.styled((style) -> style
							.withColor(TextColor.fromRgb(0x5bc0de))
							.withUnderline(true)
							.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://pswg.dev/download.html"))
							.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("PSWG Homepage")))
					);
			client.player.sendMessage(new TranslatableText("msg.pswg.update", versionText, urlText), false);
		}
	}
}
