package dev.pswg.networking;

import dev.pswg.Galaxies;
import dev.pswg.interaction.LeftClickingEntityAttachment;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

/**
 * Encapsulates a fire-and-forget S2C update that syncs the player's
 * left-using state
 *
 * @param attachment The attachment being synced to the player
 */
public record PlayerLeftUsingStateS2CPacket(LeftClickingEntityAttachment attachment) implements CustomPayload
{
	public static final Id<PlayerLeftUsingStateS2CPacket> ID = new Id<>(Galaxies.id("left_using_state"));

	public static final PacketCodec<RegistryByteBuf, PlayerLeftUsingStateS2CPacket> CODEC = PacketCodec.tuple(
			LeftClickingEntityAttachment.PACKET_CODEC,
			PlayerLeftUsingStateS2CPacket::attachment,
			PlayerLeftUsingStateS2CPacket::new
	);

	@Override
	public Id<? extends CustomPayload> getId()
	{
		return ID;
	}
}
