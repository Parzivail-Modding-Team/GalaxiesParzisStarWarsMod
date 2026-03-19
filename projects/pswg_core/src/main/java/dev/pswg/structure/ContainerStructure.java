package dev.pswg.structure;

import com.mojang.serialization.MapCodec;
import dev.pswg.container.worldgen.GalaxiesStructureTypes;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class ContainerStructure extends Structure
{
	public static final MapCodec<ContainerStructure> CODEC = simpleCodec(ContainerStructure::new);

	protected ContainerStructure(StructureSettings config)
	{
		super(config);
	}

	@Override
	protected Optional<GenerationStub> findGenerationPoint(GenerationContext context)
	{
		return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, structurePiecesCollector -> addPieces(structurePiecesCollector, context));

	}

	private void addPieces(StructurePiecesBuilder collector, Structure.GenerationContext context)
	{
		ChunkPos chunkPos = context.chunkPos();
		WorldgenRandom chunkRandom = context.random();
		BlockPos blockPos = new BlockPos(chunkPos.getMinBlockX(), 60, chunkPos.getMinBlockZ());
		Rotation blockRotation = Rotation.getRandom(chunkRandom);
		ContainerGenerator.addPieces(context.structureTemplateManager(), blockPos, blockRotation, collector, chunkRandom);
	}

	@Override
	public StructureType<?> type()
	{
		return GalaxiesStructureTypes.CONTAINER_STRUCTURE;
	}
}
