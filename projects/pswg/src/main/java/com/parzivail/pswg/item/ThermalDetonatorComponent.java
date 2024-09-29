package com.parzivail.pswg.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record ThermalDetonatorComponent(boolean primed, int ticksToExplosion, boolean shouldRender)
{
	public static final Codec<ThermalDetonatorComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.fieldOf("primed").forGetter(ThermalDetonatorComponent::primed),
			Codec.INT.fieldOf("ticksToExplosion").forGetter(ThermalDetonatorComponent::ticksToExplosion),
			Codec.BOOL.fieldOf("shouldRender").forGetter(ThermalDetonatorComponent::shouldRender)
	).apply(instance, ThermalDetonatorComponent::new));
	public static final PacketCodec<PacketByteBuf, ThermalDetonatorComponent> PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.BOOL, ThermalDetonatorComponent::primed,
			PacketCodecs.VAR_INT, ThermalDetonatorComponent::ticksToExplosion,
			PacketCodecs.BOOL, ThermalDetonatorComponent::shouldRender,
			ThermalDetonatorComponent::new
	);

	public ThermalDetonatorComponent withPrimed(boolean primed) {
		return new ThermalDetonatorComponent(primed, ticksToExplosion, shouldRender);
	}

	public ThermalDetonatorComponent withTicksToExplosion(int ticksToExplosion) {
		return new ThermalDetonatorComponent(primed, ticksToExplosion, shouldRender);
	}

	public ThermalDetonatorComponent withShouldRender(boolean shouldRender)
	{
		return new ThermalDetonatorComponent(primed, ticksToExplosion, shouldRender);
	}
}
