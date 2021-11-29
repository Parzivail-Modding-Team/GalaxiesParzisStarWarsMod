package com.parzivail.pswg.world.tatooine;

//public class TatooineBiomeSource extends BiomeSource implements BackingBiomeSource
//{
//	private static final List<BiomeEntry> BIOME_VERTICES = ImmutableList.of(
//			new BiomeEntry(SwgDimensions.Tatooine.BIOME_DUNES_KEY, 0.6f, 0.20f),
//			new BiomeEntry(SwgDimensions.Tatooine.BIOME_CANYONS_KEY, 0.566f, 0.767f),
//			new BiomeEntry(SwgDimensions.Tatooine.BIOME_PLATEAU_KEY, 0.202f, 0.867f),
//			new BiomeEntry(SwgDimensions.Tatooine.BIOME_MUSHMESA_KEY, 0.351f, 0.631f),
//			new BiomeEntry(SwgDimensions.Tatooine.BIOME_WASTES_KEY, 0.706f, 0.536f),
//			new BiomeEntry(SwgDimensions.Tatooine.BIOME_MOUNTAINS_KEY, 0.819f, 0.72f),
//			new BiomeEntry(SwgDimensions.Tatooine.BIOME_SALTFLATS_KEY, 0.15f, 0.325f),
//			new BiomeEntry(SwgDimensions.Tatooine.BIOME_OASIS_KEY, 0.155f, 0.089f)
//	);
//
//	public static final Codec<TatooineBiomeSource> CODEC = RecordCodecBuilder.create(instance -> instance
//			.group(
//					RegistryLookupCodec.of(Registry.BIOME_KEY).forGetter(source -> source.biomeRegistry)
//			)
//			.apply(instance, instance.stable(TatooineBiomeSource::new)));
//
//	private final SimplexBiomeSampler backingSampler;
//
//	private final Registry<Biome> biomeRegistry;
//	private final long seed;
//
//	public TatooineBiomeSource(Registry<Biome> biomeRegistry)
//	{
//		super(BIOME_VERTICES.stream().map((entry) -> () -> (Biome)biomeRegistry.getOrThrow(entry.key())));
//		this.biomeRegistry = biomeRegistry;
//		this.seed = 0;
//
//		this.backingSampler = new SimplexBiomeSampler(biomeRegistry, BIOME_VERTICES, seed);
//	}
//
//	@Override
//	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ)
//	{
//		return getBiomeAt(biomeX, biomeZ);
//	}
//
//	public Biome getBiomeAt(int biomeX, int biomeZ)
//	{
//		var key = this.biomeRegistry.getKey(this.backingSampler.sample(this.biomeRegistry, biomeX << 2, biomeZ << 2)).get();
//
//		return this.biomeRegistry.get(key);
//	}
//
//	@Override
//	protected Codec<? extends BiomeSource> getCodec()
//	{
//		return CODEC;
//	}
//
//	@Override
//	public BiomeSource withSeed(long l)
//	{
//		return new TatooineBiomeSource(this.biomeRegistry);
//	}
//
//	@Override
//	public RegistryKey<Biome> getBiome(int x, int z)
//	{
//		return this.biomeRegistry.getKey(this.backingSampler.sample(this.biomeRegistry, x, z)).get();
//	}
//}
