package dev.pswg.structure;

import dev.pswg.Galaxies;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class ContainerGenerator
{
	private static final Identifier templateId = Galaxies.id("derelict_imperial_container");

	public static void addPieces(StructureTemplateManager manager, BlockPos pos, Rotation rotation, StructurePiecesBuilder holder, RandomSource random)
	{

		holder.addPiece(new ContainerStructurePiece(manager, templateId, pos, rotation));
	}
}
