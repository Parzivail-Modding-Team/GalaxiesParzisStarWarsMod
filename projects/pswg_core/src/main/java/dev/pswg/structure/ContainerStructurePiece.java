package dev.pswg.structure;

import dev.pswg.Galaxies;
import dev.pswg.container.GalaxiesLootTables;
import dev.pswg.container.GalaxiesBlocks;
import dev.pswg.container.structure.GalaxiesStructurePieces;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.*;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class ContainerStructurePiece extends SimpleStructurePiece
{
	private final Identifier templateId = Galaxies.id("derelict_imperial_container");

	public ContainerStructurePiece(StructureTemplateManager manager, Identifier identifier, BlockPos pos, BlockRotation rotation)
	{
		super(GalaxiesStructurePieces.DERELICT_CONTAINER, 0, manager, identifier, identifier.toString(), createPlacementData(rotation, identifier), pos);
	}

	public ContainerStructurePiece(StructureTemplateManager templateManager, NbtCompound nbt)
	{
		super(GalaxiesStructurePieces.DERELICT_CONTAINER, nbt, templateManager, identifier -> createPlacementData(BlockRotation.valueOf(nbt.getString("Rot").get()), identifier));
	}

	private static StructurePlacementData createPlacementData(BlockRotation rotation, Identifier identifier)
	{
		return new StructurePlacementData()
				.setRotation(rotation)
				.setMirror(BlockMirror.NONE)
				.setPosition(new BlockPos(0, 0, 0))
				.addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS)
				.setLiquidSettings(StructureLiquidSettings.APPLY_WATERLOGGING);
	}

	private void loadTemplate(StructureTemplateManager templateManager, NbtCompound nbtCompound)
	{
		this.template = templateManager.getTemplateOrBlank(templateId);
	}

	@Override
	protected void writeNbt(StructureContext context, NbtCompound nbt)
	{
		super.writeNbt(context, nbt);
		nbt.putString("Rot", this.placementData.getRotation().name());
	}

	@Override
	public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot)
	{
		BlockPos blockPos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, pos);
		Identifier identifier = Identifier.of(this.templateIdString);
		this.pos.withY(blockPos.getY() + random.nextBetween(-3, 0));
		StructurePlacementData structurePlacementData = createPlacementData(this.placementData.getRotation(), identifier);
		super.generate(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, pivot);
	}

	@Override
	protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox)
	{
		if ("crate".equals(metadata))
		{
			if (random.nextFloat() > 0.25f)
			{
				Block crateBlock = StructUtil.getRandomCrate(random);
				world.setBlockState(pos, crateBlock.getDefaultState(), Block.NOTIFY_ALL);
				BlockEntity be = world.getBlockEntity(pos);
				if (be instanceof LootableContainerBlockEntity lootTable)
				{
					// TODO: CHANGE LOOT HERE
					if (crateBlock.equals(GalaxiesBlocks.IMPERIAL_CORRUGATED_CRATE))
						lootTable.setLootTable(GalaxiesLootTables.IMPERIAL_CRATE);
					else if (crateBlock.equals(GalaxiesBlocks.MEDICAL_CORRUGATED_CRATE))
						lootTable.setLootTable(GalaxiesLootTables.MEDICAL_CRATE);
					else if (crateBlock.equals(GalaxiesBlocks.MINING_CORRUGATED_CRATE))
						lootTable.setLootTable(GalaxiesLootTables.MINING_CRATE);
					else
						lootTable.setLootTable(GalaxiesLootTables.GENERIC_CRATE);
					lootTable.setLootTableSeed(random.nextLong());
				}
			}
		}
	}
}