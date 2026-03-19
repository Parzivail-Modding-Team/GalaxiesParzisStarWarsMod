package dev.pswg.networking;

import dev.pswg.Gadgets;
import java.util.ArrayList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;

public record MixerSyncS2CPayload(ArrayList<MobEffectInstance> drinkEffects, ArrayList<Integer> drinkColors, ArrayList<ItemStack> drinkFoods) implements CustomPacketPayload
{
	public static final ResourceLocation MIXER_SYNC_PAYLOAD_ID = Gadgets.id("mixer_sync");
	public static final CustomPacketPayload.Type<MixerSyncS2CPayload> ID = new CustomPacketPayload.Type<>(MIXER_SYNC_PAYLOAD_ID);
	public static final StreamCodec<RegistryFriendlyByteBuf, MixerSyncS2CPayload> CODEC = StreamCodec.ofMember(MixerSyncS2CPayload::toPacket, MixerSyncS2CPayload::fromPacket);

	private void toPacket(RegistryFriendlyByteBuf buf)
	{
		ByteBufCodecs.collection(ArrayList::new, MobEffectInstance.STREAM_CODEC).encode(buf, drinkEffects);
		ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.INT).encode(buf, drinkColors);
		ByteBufCodecs.collection(ArrayList::new, ItemStack.STREAM_CODEC).encode(buf, drinkFoods);
	}

	private static MixerSyncS2CPayload fromPacket(RegistryFriendlyByteBuf buf)
	{
		ArrayList<MobEffectInstance> effects = ByteBufCodecs.collection(ArrayList::new, MobEffectInstance.STREAM_CODEC).decode(buf);
		ArrayList<Integer> colours = ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.INT).decode(buf);
		ArrayList<ItemStack> foods = ByteBufCodecs.collection(ArrayList::new, ItemStack.STREAM_CODEC).decode(buf);
		return new MixerSyncS2CPayload(effects, colours, foods);
	}

	@Override
	public Type<? extends CustomPacketPayload> type()
	{
		return ID;
	}
}
