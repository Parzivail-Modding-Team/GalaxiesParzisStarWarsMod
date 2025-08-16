package dev.pswg.structure;

import com.mojang.serialization.MapCodec;
import dev.pswg.container.GadgetsStructureTypes;
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
		return getStructurePosition(context, Heightmap.Type.WORLD_SURFACE_WG, structurePiecesCollector -> {
		});
	}

	@Override
	public StructureType<?> getType()
	{
		return GadgetsStructureTypes.CONTAINER_STRUCTURE;
	}
}
