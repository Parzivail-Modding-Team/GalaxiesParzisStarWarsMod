package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.item.ThermalDetonatorComponent;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.BlockPos;

import java.util.function.UnaryOperator;

public class SwgComponents
{
	public static final ComponentType<BlockPos> CableSource = build(builder -> builder
			.codec(BlockPos.CODEC)
			.packetCodec(BlockPos.PACKET_CODEC));
	public static final ComponentType<ThermalDetonatorComponent> ThermalDetonator = build(builder -> builder
			.codec(ThermalDetonatorComponent.CODEC)
			.packetCodec(ThermalDetonatorComponent.PACKET_CODEC));

	private static <T> ComponentType<T> build(UnaryOperator<ComponentType.Builder<T>> f) {
		return f.apply(ComponentType.builder()).build();
	}

	public static void register() {
		Registry.register(Registries.DATA_COMPONENT_TYPE, Resources.id("cable_source"), CableSource);
	}
}
