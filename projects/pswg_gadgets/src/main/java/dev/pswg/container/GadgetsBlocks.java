package dev.pswg.container;

import dev.pswg.Gadgets;
import dev.pswg.block.*;
import dev.pswg.datagen.*;
import dev.pswg.feature.brewing.MixerBlock;
import dev.pswg.feature.scrapping.table.ScrappingTableBlock;
import dev.pswg.registry.Registrar;
import net.minecraft.block.*;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class GadgetsBlocks
{
	public static final class Tags
	{
		public static final TagKey<Block> FRAGMENTATION_GRENADE_DESTROY = TagKey.of(RegistryKeys.BLOCK, Gadgets.id("fragmentation_destroy"));
		public static final TagKey<Block> DETONATES_GRENADE = TagKey.of(RegistryKeys.BLOCK, Gadgets.id("detonates_grenade"));
		public static final TagKey<Block> BOUNCY = TagKey.of(RegistryKeys.BLOCK, Gadgets.id("bouncy"));
		public static final TagKey<Block> GAS_PASS_THROUGH = TagKey.of(RegistryKeys.BLOCK, Gadgets.id("gas_pass_through"));
		public static final TagKey<Block> INFERNO_CHAR = TagKey.of(RegistryKeys.BLOCK, Gadgets.id("inferno_char"));
		public static final TagKey<Block> INFERNO_DESTROY = TagKey.of(RegistryKeys.BLOCK, Gadgets.id("inferno_destroy"));
	}

	@DataGenBlock(model = DataGenBlockModel.None)
	public static final ThermalDetonatorBlock THERMAL_DETONATOR_BLOCK = Registrar.block(Gadgets.id("thermal_detonator_block"), ThermalDetonatorBlock::new, Block.Settings.create());
	@DataGenBlock(model = DataGenBlockModel.None)
	public static final FragmentationGrenadeBlock FRAGMENTATION_GRENADE_BLOCK = Registrar.block(Gadgets.id("fragmentation_grenade_block"), FragmentationGrenadeBlock::new, Block.Settings.create());

	@DataGenBlock
	public static final FertileDirt FERTILE_DIRT_BLOCK = Registrar.block(Gadgets.id("fertile_dirt"), FertileDirt::new, Block.Settings.create().ticksRandomly());
	@DataGenBlock(itemGroup = DataGenItemGroup.WorldGenBlock)
	public static final Block CHARRED_BLOCK = Registrar.block(Gadgets.id("charred_block"), Block::new, AbstractBlock.Settings.create().dropsNothing().breakInstantly());

	@DataGenBlock(model = DataGenBlockModel.None)
	public static final Block SCRAPPING_TABLE_BLOCK = Registrar.block(Gadgets.id("scrapping_table"), ScrappingTableBlock::new, AbstractBlock.Settings.create());
	@DataGenBlock(model = DataGenBlockModel.None)
	public static final Block MIXER_BLOCK = Registrar.block(Gadgets.id("mixer"), MixerBlock::new, AbstractBlock.Settings.create());

	public static void register()
	{
	}
}
