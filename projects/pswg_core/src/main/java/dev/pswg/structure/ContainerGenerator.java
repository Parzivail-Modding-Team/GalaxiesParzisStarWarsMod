package dev.pswg.structure;

import dev.pswg.Galaxies;
import net.minecraft.structure.StructurePiecesHolder;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class ContainerGenerator
{
	private static final Identifier templateId = Galaxies.id("derelict_imperial_container");

	public static void addPieces(StructureTemplateManager manager, BlockPos pos, BlockRotation rotation, StructurePiecesHolder holder, Random random)
	{

		holder.addPiece(new ContainerStructurePiece(manager, templateId, pos, rotation));
	}
}
