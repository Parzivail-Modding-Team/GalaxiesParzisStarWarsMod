package com.parzivail.swg.dimension.tatooine;

import com.parzivail.swg.registry.BlockRegister;
import com.parzivail.swg.registry.StructureRegister;
import com.parzivail.util.world.PBiomeGenBase;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.TerrainGen;

import java.util.Random;

import static net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.*;

/**
 * Created by colby on 9/13/2017.
 */
public class BiomeTatooineDunes extends PBiomeGenBase
{
	public WorldGenerator coalGen;
	public WorldGenerator ironGen;
	public WorldGenerator goldGen;
	public WorldGenerator diamondGen;
	public WorldGenerator chromiumGen;
	public WorldGenerator titaniumGen;
	public WorldGenerator rubindumGen;
	public WorldGenerator cortosisGen;

	public BiomeTatooineDunes(int biomeId)
	{
		super(biomeId);
		setBiomeName("Tatooine");
		spawnableCreatureList.clear();
		spawnableCaveCreatureList.clear();
		spawnableMonsterList.clear();
		spawnableWaterCreatureList.clear();

		enableRain = false;
		enableSnow = false;

		// WorldGenMinable::ctor(Block block, Block blobSize)
		coalGen = new WorldGenMinable(Blocks.coal_ore, 16);
		ironGen = new WorldGenMinable(Blocks.iron_ore, 8);
		goldGen = new WorldGenMinable(Blocks.gold_ore, 8);
		diamondGen = new WorldGenMinable(Blocks.diamond_ore, 7);
		chromiumGen = new WorldGenMinable(BlockRegister.oreChromium, 8);
		titaniumGen = new WorldGenMinable(BlockRegister.oreTitanium, 8);
		rubindumGen = new WorldGenMinable(BlockRegister.oreRubindum, 7);
		cortosisGen = new WorldGenMinable(BlockRegister.oreCortosis, 7);
	}

	protected void genOre(World world, Random rand, int chunkX, int chunkZ, int amount, WorldGenerator generator, int minY, int maxY)
	{
		for (int l = 0; l < amount; ++l)
		{
			int i1 = chunkX + rand.nextInt(16);
			int j1 = rand.nextInt(maxY - minY) + minY;
			int k1 = chunkZ + rand.nextInt(16);
			generator.generate(world, rand, i1, j1, k1);
		}
	}

	@Override
	public void decorate(IChunkProvider provider, World world, Random rand, int worldX, int worldZ)
	{
		StructureRegister.structureEngine.genTiles(world, worldX, worldZ);

		if (TerrainGen.generateOre(world, rand, coalGen, worldX, worldZ, COAL))
			genOre(world, rand, worldX, worldZ, 20, coalGen, 0, 128);
		if (TerrainGen.generateOre(world, rand, ironGen, worldX, worldZ, IRON))
			genOre(world, rand, worldX, worldZ, 20, ironGen, 0, 64);
		if (TerrainGen.generateOre(world, rand, goldGen, worldX, worldZ, GOLD))
			genOre(world, rand, worldX, worldZ, 2, goldGen, 0, 32);
		if (TerrainGen.generateOre(world, rand, diamondGen, worldX, worldZ, DIAMOND))
			genOre(world, rand, worldX, worldZ, 1, diamondGen, 0, 16);

		genOre(world, rand, worldX, worldZ, 20, chromiumGen, 0, 64);
		genOre(world, rand, worldX, worldZ, 20, titaniumGen, 0, 64);
		genOre(world, rand, worldX, worldZ, 1, rubindumGen, 0, 16);
		genOre(world, rand, worldX, worldZ, 1, cortosisGen, 0, 16);
	}
}
