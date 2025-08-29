package dev.pswg.container;

import dev.pswg.Gadgets;
import dev.pswg.blockEntity.CrateCorrugatedBlockEntity;
import dev.pswg.feature.scrapping.table.ScrappingTableBlockEntity;
import dev.pswg.registry.Registrar;
import dev.pswg.util.BlockUtil;
import net.minecraft.block.entity.BlockEntityType;

public class GadgetsBlockEntities
{
	public static final BlockEntityType<ScrappingTableBlockEntity> SCRAPPING_TABLE_BLOCK_ENTITY = Registrar.blockEntity(Gadgets.id("scrapping_table"), ScrappingTableBlockEntity::new, GadgetsBlocks.SCRAPPING_TABLE_BLOCK);

	public static final BlockEntityType<CrateCorrugatedBlockEntity> CORRUGATED_CRATE_BLOCK_ENTITY = Registrar.blockEntity(Gadgets.id("corrugated_crate"), CrateCorrugatedBlockEntity::new, BlockUtil.concat(GadgetsBlocks.CORRUGATED_CRATE, GadgetsBlocks.MEDICAL_CORRUGATED_CRATE, GadgetsBlocks.MINING_CORRUGATED_CRATE, GadgetsBlocks.IMPERIAL_CORRUGATED_CRATE));

	public static void register()
	{
	}
}
