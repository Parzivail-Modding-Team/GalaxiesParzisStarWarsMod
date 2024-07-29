package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.BlockPos;

public class SwgComponents
{
	public static final ComponentType<BlockPos> CableSource = ComponentType.<BlockPos>builder()
	                                                                       .codec(BlockPos.CODEC)
	                                                                       .packetCodec(BlockPos.PACKET_CODEC)
	                                                                       .build();

	public static void register() {
		Registry.register(Registries.DATA_COMPONENT_TYPE, Resources.id("cable_source"), CableSource);
	}
}
