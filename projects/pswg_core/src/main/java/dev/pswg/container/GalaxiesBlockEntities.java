package dev.pswg.container;

import dev.pswg.Galaxies;
import dev.pswg.blockEntity.CrateCorrugatedBlockEntity;
import dev.pswg.registry.Registrar;
import dev.pswg.util.BlockUtil;
import net.minecraft.block.entity.BlockEntityType;

public class GalaxiesBlockEntities
{
	public static final BlockEntityType<CrateCorrugatedBlockEntity> CORRUGATED_CRATE_BLOCK_ENTITY = Registrar.blockEntity(Galaxies.id("corrugated_crate"), CrateCorrugatedBlockEntity::new, BlockUtil.concat(GalaxiesBlocks.CORRUGATED_CRATE, GalaxiesBlocks.MEDICAL_CORRUGATED_CRATE, GalaxiesBlocks.MINING_CORRUGATED_CRATE, GalaxiesBlocks.IMPERIAL_CORRUGATED_CRATE));
	public static void register()
	{
	}
}
