package dev.pswg.container.worldgen;

import dev.pswg.structure.ContainerStructurePiece;
import java.util.Locale;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public class GalaxiesStructurePieces
{
	public static final StructurePieceType DERELICT_CONTAINER = registerPiece(ContainerStructurePiece::new, "DerelictImpContainer");

	private static StructurePieceType registerPiece(StructurePieceType.StructureTemplateType type, String id)
	{
		return registerPiece((StructurePieceType)type, id);
	}

	private static StructurePieceType registerPiece(StructurePieceType type, String id)
	{
		return Registry.register(BuiltInRegistries.STRUCTURE_PIECE, id.toLowerCase(Locale.ROOT), type);
	}

	public static void register()
	{

	}
}
