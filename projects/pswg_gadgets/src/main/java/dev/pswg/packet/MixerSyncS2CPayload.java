package dev.pswg.packet;

import dev.pswg.Gadgets;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public record MixerSyncS2CPayload(ArrayList<StatusEffectInstance> drinkEffects) implements CustomPayload
{
	public static final Identifier MIXER_SYNC_PAYLOAD_ID = Gadgets.id("mixer_sync");
	public static final CustomPayload.Id<MixerSyncS2CPayload> ID = new CustomPayload.Id<>(MIXER_SYNC_PAYLOAD_ID);
	public static final PacketCodec<RegistryByteBuf, MixerSyncS2CPayload> CODEC = PacketCodec.tuple(PacketCodecs.collection(ArrayList<StatusEffectInstance>::new, StatusEffectInstance.PACKET_CODEC), MixerSyncS2CPayload::drinkEffects, MixerSyncS2CPayload::new);

	@Override
	public Id<? extends CustomPayload> getId()
	{
		return ID;
	}
}
