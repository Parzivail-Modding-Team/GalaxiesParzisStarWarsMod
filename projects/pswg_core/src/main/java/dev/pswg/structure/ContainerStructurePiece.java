package dev.pswg.structure;

import dev.pswg.Galaxies;
import dev.pswg.container.GalaxiesLootTables;
import dev.pswg.container.GalaxiesBlocks;
import dev.pswg.container.worldgen.GalaxiesStructurePieces;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class ContainerStructurePiece extends TemplateStructurePiece
{
	private final Identifier templateId = Galaxies.id("derelict_imperial_container");

	public ContainerStructurePiece(StructureTemplateManager manager, Identifier identifier, BlockPos pos, Rotation rotation)
	{
		super(GalaxiesStructurePieces.DERELICT_CONTAINER, 0, manager, identifier, identifier.toString(), createPlacementData(rotation, identifier), pos);
	}

	public ContainerStructurePiece(StructureTemplateManager templateManager, CompoundTag nbt)
	{
		super(GalaxiesStructurePieces.DERELICT_CONTAINER, nbt, templateManager, identifier -> createPlacementData(Rotation.valueOf(nbt.getString("Rot").get()), identifier));
	}

	private static StructurePlaceSettings createPlacementData(Rotation rotation, Identifier identifier)
	{
		return new StructurePlaceSettings()
				.setRotation(rotation)
				.setMirror(Mirror.NONE)
				.setRotationPivot(new BlockPos(0, 0, 0))
				.addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK)
				.setLiquidSettings(LiquidSettings.APPLY_WATERLOGGING);
	}

	@Override
	protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag nbt)
	{
		super.addAdditionalSaveData(context, nbt);
		nbt.putString("Rot", this.placeSettings.getRotation().name());
	}

	@Override
	public void postProcess(WorldGenLevel world, StructureManager structureAccessor, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox chunkBox, ChunkPos chunkPos, BlockPos pivot)
	{
		BlockPos blockPos = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, templatePosition);
		Identifier identifier = Identifier.parse(this.templateName);
		this.templatePosition.atY(blockPos.getY() + random.nextIntBetweenInclusive(-3, 0));
		StructurePlaceSettings structurePlacementData = createPlacementData(this.placeSettings.getRotation(), identifier);
		super.postProcess(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, pivot);
	}

	@Override
	protected void handleDataMarker(String metadata, BlockPos pos, ServerLevelAccessor world, RandomSource random, BoundingBox boundingBox)
	{
		if ("crate".equals(metadata))
		{
			if (random.nextFloat() > 0.33f)
			{
				Block crateBlock = StructUtil.getRandomCrate(random);
				world.setBlock(pos, crateBlock.defaultBlockState(), Block.UPDATE_ALL);
				BlockEntity be = world.getBlockEntity(pos);
				if (be instanceof RandomizableContainerBlockEntity container)
				{
					// TODO: CHANGE LOOT HERE
					if (crateBlock.equals(GalaxiesBlocks.IMPERIAL_CORRUGATED_CRATE))
						container.setLootTable(GalaxiesLootTables.IMPERIAL_CRATE);
					else if (crateBlock.equals(GalaxiesBlocks.MEDICAL_CORRUGATED_CRATE))
						container.setLootTable(GalaxiesLootTables.MEDICAL_CRATE);
					else if (crateBlock.equals(GalaxiesBlocks.MINING_CORRUGATED_CRATE))
						container.setLootTable(GalaxiesLootTables.MINING_CRATE);
					else
					{
						int lootTable = random.nextIntBetweenInclusive(1, 7);
						switch (lootTable)
						{
							case 1, 2, 3:
								container.setLootTable(GalaxiesLootTables.GENERIC_FOOD_CRATE);
								break;
							case 4, 5, 6:
								container.setLootTable(GalaxiesLootTables.GENERIC_TECH_CRATE);
								break;
							default:
								container.setLootTable(GalaxiesLootTables.GENERIC_WEAPONS_CRATE);
						}
					}
					container.setLootTableSeed(random.nextLong());
				}
			}
		}
	}
}
