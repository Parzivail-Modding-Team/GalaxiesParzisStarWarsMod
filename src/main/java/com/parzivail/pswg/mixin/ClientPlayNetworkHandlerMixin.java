package com.parzivail.pswg.mixin;

import com.parzivail.pswg.Resources;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.text.*;
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
	private MinecraftClient client;

	@Inject(method = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;onGameJoin(Lnet/minecraft/network/packet/s2c/play/GameJoinS2CPacket;)V", at = @At("TAIL"))
	private void onJoinWorld(GameJoinS2CPacket packet, CallbackInfo ci)
	{
		if (client.player != null && Resources.REMOTE_VERSION != null)
		{
			Text versionText = new LiteralText(Resources.REMOTE_VERSION.name)
					.styled((style) -> style
					        .withItalic(true)
					);
			Text urlText = new LiteralText("https://www.curseforge.com/minecraft/mc-mods/pswg")
					.styled((style) -> style
							.withColor(TextColor.fromRgb(0x5bc0de))
							.withUnderline(true)
							.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.curseforge.com/minecraft/mc-mods/pswg"))
							.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("PSWG on CurseForge")))
					);
			client.player.sendMessage(new TranslatableText("msg.pswg.update", versionText, urlText), false);
		}
	}
}
