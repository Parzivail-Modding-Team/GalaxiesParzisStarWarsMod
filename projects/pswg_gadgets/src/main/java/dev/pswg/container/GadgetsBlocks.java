package dev.pswg.container;

import dev.pswg.Gadgets;
import dev.pswg.block.*;
import dev.pswg.datagen.*;
import dev.pswg.feature.brewing.MixerBlock;
import dev.pswg.feature.scrapping.table.ScrappingTableBlock;
import dev.pswg.registry.Registrar;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class GadgetsBlocks
{
	public static final class Tags
	{
		public static final TagKey<Block> FRAGMENTATION_GRENADE_DESTROY = TagKey.create(Registries.BLOCK, Gadgets.id("fragmentation_destroy"));
		public static final TagKey<Block> DETONATES_GRENADE = TagKey.create(Registries.BLOCK, Gadgets.id("detonates_grenade"));
		public static final TagKey<Block> GAS_PASS_THROUGH = TagKey.create(Registries.BLOCK, Gadgets.id("gas_pass_through"));
		public static final TagKey<Block> INFERNO_CHAR = TagKey.create(Registries.BLOCK, Gadgets.id("inferno_char"));
		public static final TagKey<Block> INFERNO_DESTROY = TagKey.create(Registries.BLOCK, Gadgets.id("inferno_destroy"));
	}

	@DataGenBlock(model = DataGenBlockModel.NONE)
	public static final ThermalDetonatorBlock THERMAL_DETONATOR_BLOCK = Registrar.block(Gadgets.id("thermal_detonator_block"), ThermalDetonatorBlock::new, BlockBehaviour.Properties.of());
	@DataGenBlock(model = DataGenBlockModel.NONE)
	public static final FragmentationGrenadeBlock FRAGMENTATION_GRENADE_BLOCK = Registrar.block(Gadgets.id("fragmentation_grenade_block"), FragmentationGrenadeBlock::new, BlockBehaviour.Properties.of());

	@DataGenBlock
	public static final FertileDirt FERTILE_DIRT_BLOCK = Registrar.block(Gadgets.id("fertile_dirt"), FertileDirt::new, BlockBehaviour.Properties.of().randomTicks());
	@DataGenBlock(itemGroup = DataGenItemGroup.WORLD_GEN_BLOCK)
	public static final Block CHARRED_BLOCK = Registrar.block(Gadgets.id("charred_block"), Block::new, BlockBehaviour.Properties.of().noLootTable().instabreak());

	@DataGenBlock(model = DataGenBlockModel.NONE)
	public static final Block SCRAPPING_TABLE_BLOCK = Registrar.block(Gadgets.id("scrapping_table"), ScrappingTableBlock::new, BlockBehaviour.Properties.of());
	@DataGenBlock(model = DataGenBlockModel.NONE)
	public static final Block MIXER_BLOCK = Registrar.block(Gadgets.id("mixer"), MixerBlock::new, BlockBehaviour.Properties.of());

	public static void register()
	{
	}
}
