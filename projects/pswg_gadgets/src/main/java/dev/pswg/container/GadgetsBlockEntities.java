package dev.pswg.container;

import dev.pswg.Gadgets;
import dev.pswg.feature.brewing.MixerBlockEntity;
import dev.pswg.feature.scrapping.table.ScrappingTableBlockEntity;
import dev.pswg.registry.Registrar;
import net.minecraft.block.entity.BlockEntityType;

public class GadgetsBlockEntities
{
	public static final BlockEntityType<MixerBlockEntity> MIXER_BLOCK_ENTITY = Registrar.blockEntity(Gadgets.id("mixer"), MixerBlockEntity::new, GadgetsBlocks.MIXER_BLOCK);
	public static final BlockEntityType<ScrappingTableBlockEntity> SCRAPPING_TABLE_BLOCK_ENTITY = Registrar.blockEntity(Gadgets.id("scrapping_table"), ScrappingTableBlockEntity::new, GadgetsBlocks.SCRAPPING_TABLE_BLOCK);

	public static void register()
	{
	}
}
