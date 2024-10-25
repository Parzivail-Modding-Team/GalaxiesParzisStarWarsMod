package dev.pswg.networking;

import dev.pswg.Galaxies;
import dev.pswg.interaction.ClientPlayerAction;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

/**
 * Encapsulates a fire-and-forget C2S player action notification
 *
 * @param action The action to invoke
 */
public record GalaxiesPlayerActionC2SPacket(ClientPlayerAction action) implements CustomPayload
{
	public static final Id<GalaxiesPlayerActionC2SPacket> ID = new Id<>(Galaxies.id("client_player_action"));

	public static final PacketCodec<RegistryByteBuf, GalaxiesPlayerActionC2SPacket> CODEC = PacketCodec.tuple(
			GalaxiesPacketCodecs.CLIENT_PLAYER_ACTION,
			GalaxiesPlayerActionC2SPacket::action,
			GalaxiesPlayerActionC2SPacket::new
	);

	@Override
	public Id<? extends CustomPayload> getId()
	{
		return ID;
	}
}
