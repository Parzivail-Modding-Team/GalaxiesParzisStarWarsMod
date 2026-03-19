package dev.pswg.networking;

import dev.pswg.Galaxies;
import dev.pswg.interaction.ServerPlayerAction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Encapsulates a fire-and-forget S2C player action notification
 *
 * @param action The action to invoke
 */
public record GalaxiesPlayerActionS2CPacket(ServerPlayerAction action) implements CustomPacketPayload
{
	public static final CustomPacketPayload.Type<GalaxiesPlayerActionS2CPacket> ID = new CustomPacketPayload.Type<>(Galaxies.id("server_player_action"));

	public static final StreamCodec<RegistryFriendlyByteBuf, GalaxiesPlayerActionS2CPacket> CODEC = StreamCodec.composite(
			GalaxiesPacketCodecs.SERVER_PLAYER_ACTION,
			GalaxiesPlayerActionS2CPacket::action,
			GalaxiesPlayerActionS2CPacket::new
	);

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
	{
		return ID;
	}
}
