package dev.pswg.structure;

import com.mojang.serialization.MapCodec;
import dev.pswg.container.structure.GalaxiesStructureTypes;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.Optional;

public class ContainerStructure extends Structure
{
	public static final MapCodec<ContainerStructure> CODEC = createCodec(ContainerStructure::new);

	protected ContainerStructure(Config config)
	{
		super(config);
	}

	@Override
	protected Optional<StructurePosition> getStructurePosition(Context context)
	{
		return getStructurePosition(context, Heightmap.Type.WORLD_SURFACE_WG, structurePiecesCollector -> addPieces(structurePiecesCollector, context));

	}

	private void addPieces(StructurePiecesCollector collector, Structure.Context context)
	{
		ChunkPos chunkPos = context.chunkPos();
		ChunkRandom chunkRandom = context.random();
		BlockPos blockPos = new BlockPos(chunkPos.getStartX(), 60, chunkPos.getStartZ());
		BlockRotation blockRotation = BlockRotation.random(chunkRandom);
		ContainerGenerator.addPieces(context.structureTemplateManager(), blockPos, blockRotation, collector, chunkRandom);
	}

	@Override
	public StructureType<?> getType()
	{
		return GalaxiesStructureTypes.CONTAINER_STRUCTURE;
	}
}
