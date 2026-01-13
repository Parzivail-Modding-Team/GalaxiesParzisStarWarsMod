package dev.pswg.container.structure;

import dev.pswg.structure.ContainerStructurePiece;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.structure.StructurePieceType;

import java.util.Locale;

public class GalaxiesStructurePieces
{
	public static final StructurePieceType DERELICT_CONTAINER = registerPiece(ContainerStructurePiece::new, "DerelictImpContainer");

	private static StructurePieceType registerPiece(StructurePieceType.ManagerAware type, String id)
	{
		return registerPiece((StructurePieceType)type, id);
	}

	private static StructurePieceType registerPiece(StructurePieceType type, String id)
	{
		return Registry.register(Registries.STRUCTURE_PIECE, id.toLowerCase(Locale.ROOT), type);
	}

	public static void register()
	{

	}
}
