package com.parzivail.util.world;

import net.minecraft.world.gen.chunk.StructuresConfig;

import java.util.Map;
import java.util.Optional;

public class EmptyStructuresConfig extends StructuresConfig
{
	public EmptyStructuresConfig()
	{
		super(Optional.empty(), Map.of());
	}
}
