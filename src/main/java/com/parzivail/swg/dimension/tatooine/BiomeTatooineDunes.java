package com.parzivail.swg.dimension.tatooine;

import com.parzivail.swg.registry.StructureRegister;
import com.parzivail.util.world.PBiomeGenBase;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.Random;

/**
 * Created by colby on 9/13/2017.
 */
public class BiomeTatooineDunes extends PBiomeGenBase
{
	public BiomeTatooineDunes(int biomeId)
	{
		super(biomeId);
		this.setBiomeName("Tatooine");
		this.spawnableCreatureList.clear();

		this.enableRain = false;
		this.enableSnow = false;
	}

	@Override
	public void decorate(IChunkProvider provider, World world, Random rand, int worldX, int worldZ)
	{
		StructureRegister.genTiles(world, worldX, worldZ);
	}
}