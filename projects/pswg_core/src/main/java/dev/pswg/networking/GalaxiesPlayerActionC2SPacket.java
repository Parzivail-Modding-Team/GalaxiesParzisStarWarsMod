package dev.pswg.networking;

import dev.pswg.Galaxies;
import dev.pswg.interaction.ClientPlayerAction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Encapsulates a fire-and-forget C2S player action notification
 *
 * @param action The action to invoke
 */
public record GalaxiesPlayerActionC2SPacket(ClientPlayerAction action) implements CustomPacketPayload
{
	public static final CustomPacketPayload.Type<GalaxiesPlayerActionC2SPacket> ID = new CustomPacketPayload.Type<>(Galaxies.id("client_player_action"));

	public static final StreamCodec<RegistryFriendlyByteBuf, GalaxiesPlayerActionC2SPacket> CODEC = StreamCodec.composite(
			GalaxiesPacketCodecs.CLIENT_PLAYER_ACTION,
			GalaxiesPlayerActionC2SPacket::action,
			GalaxiesPlayerActionC2SPacket::new
	);

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
	{
		return ID;
	}
}
