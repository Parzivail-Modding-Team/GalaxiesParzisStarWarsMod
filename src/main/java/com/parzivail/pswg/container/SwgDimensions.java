package com.parzivail.pswg.container;

//public class SwgDimensions
//{
//	public static class Tatooine
//	{
//		public static final TatooineBiome BIOME = new TatooineBiome();
//		public static FabricDimensionType DIMENSION_TYPE;
//
//		public static void registerDimension()
//		{
//			final Identifier identifier = Resources.identifier("tatooine");
//			Registry.register(Registry.BIOME, identifier, BIOME);
//			DIMENSION_TYPE = FabricDimensionType.builder()
//			                                    .defaultPlacer((oldEntity, destinationWorld, portalDir, horizontalOffset, verticalOffset) -> new BlockPattern.TeleportTarget(new Vec3d(destinationWorld.getTopPosition(Heightmap.Type.WORLD_SURFACE, BlockPos.ORIGIN)), oldEntity.getVelocity(), (int)oldEntity.yaw))
//			                                    .factory(TatooineDimension::new)
//			                                    .skyLight(true)
//			                                    .buildAndRegister(identifier);
//		}
//	}
//}
