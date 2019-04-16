package com.parzivail.swg.dimension.tatooine;

import com.parzivail.swg.Resources;
import com.parzivail.util.world.PBiomeGenBase;

public class BiomeTatooine extends PBiomeGenBase
{
	public BiomeTatooine()
	{
		super(new BiomeProperties("Tatooine").setBaseBiome("tatooine").setRainDisabled().setTemperature(1));
		this.setRegistryName(Resources.modColon(this.getBiomeName().toLowerCase()));
	}
}
