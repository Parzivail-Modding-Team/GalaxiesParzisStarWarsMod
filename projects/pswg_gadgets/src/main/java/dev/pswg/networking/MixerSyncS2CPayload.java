package dev.pswg.networking;

import dev.pswg.Gadgets;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public record MixerSyncS2CPayload(ArrayList<StatusEffectInstance> drinkEffects, ArrayList<Integer> drinkColors, ArrayList<ItemStack> drinkFoods) implements CustomPayload
{
	public static final Identifier MIXER_SYNC_PAYLOAD_ID = Gadgets.id("mixer_sync");
	public static final CustomPayload.Id<MixerSyncS2CPayload> ID = new CustomPayload.Id<>(MIXER_SYNC_PAYLOAD_ID);
	public static final PacketCodec<RegistryByteBuf, MixerSyncS2CPayload> CODEC = PacketCodec.of(MixerSyncS2CPayload::toPacket, MixerSyncS2CPayload::fromPacket);

	private void toPacket(RegistryByteBuf buf)
	{
		PacketCodecs.collection(ArrayList::new, StatusEffectInstance.PACKET_CODEC).encode(buf, drinkEffects);
		PacketCodecs.collection(ArrayList::new, PacketCodecs.INTEGER).encode(buf, drinkColors);
		PacketCodecs.collection(ArrayList::new, ItemStack.PACKET_CODEC).encode(buf, drinkFoods);
	}

	private static MixerSyncS2CPayload fromPacket(RegistryByteBuf buf)
	{
		ArrayList<StatusEffectInstance> effects = PacketCodecs.collection(ArrayList::new, StatusEffectInstance.PACKET_CODEC).decode(buf);
		ArrayList<Integer> colours = PacketCodecs.collection(ArrayList::new, PacketCodecs.INTEGER).decode(buf);
		ArrayList<ItemStack> foods = PacketCodecs.collection(ArrayList::new, ItemStack.PACKET_CODEC).decode(buf);
		return new MixerSyncS2CPayload(effects, colours, foods);
	}

	@Override
	public Id<? extends CustomPayload> getId()
	{
		return ID;
	}
}
