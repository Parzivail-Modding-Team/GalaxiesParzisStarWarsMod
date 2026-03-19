package dev.pswg.structure;

import dev.pswg.Galaxies;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class ContainerGenerator
{
	private static final ResourceLocation templateId = Galaxies.id("derelict_imperial_container");

	public static void addPieces(StructureTemplateManager manager, BlockPos pos, Rotation rotation, StructurePieceAccessor holder, RandomSource random)
	{

		holder.addPiece(new ContainerStructurePiece(manager, templateId, pos, rotation));
	}
}
