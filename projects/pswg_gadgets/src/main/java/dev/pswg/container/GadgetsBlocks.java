package dev.pswg.container;

import dev.pswg.Gadgets;
import dev.pswg.block.*;
import dev.pswg.datagen.DataGenBlock;
import dev.pswg.datagen.DataGenBlockModel;
import dev.pswg.feature.scrapping.ScrappingTableBlock;
import dev.pswg.registry.Registrar;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallBlock;
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
	@DataGenBlock
	public static final Block CHARRED_BLOCK = Registrar.block(Gadgets.id("charred_block"), Block::new, AbstractBlock.Settings.create().dropsNothing().breakInstantly());

	@DataGenBlock(model = DataGenBlockModel.None)
	public static final Block SCRAPPING_TABLE_BLOCK = Registrar.block(Gadgets.id("scrapping_table"), ScrappingTableBlock::new, AbstractBlock.Settings.create());
	//public static final StairsBlock CHARRED_STAIRS = Registrar.block(Gadgets.id("charred_stairs"), Block::new, Block.Settings.create().dropsNothing().breakInstantly());

	@DataGenBlock
	public static final StoneProducts CANYON = new StoneProducts(AbstractBlock.Settings.create().strength(0.5F), "canyon_stone");
	@DataGenBlock
	public static final Block CANYON_BRICKS = createSimpleBlock("canyone_stone_bricks", AbstractBlock.Settings.create().strength(0.5F));
	@DataGenBlock
	public static final Block POLISHED_CANYON = createSimpleBlock("polished_canyon_stone", AbstractBlock.Settings.create().strength(0.5F));
	@DataGenBlock
	public static final Block CHISELED_CANYON = createSimpleBlock("chiseled_canyon_stone", AbstractBlock.Settings.create().strength(0.5F));
	@DataGenBlock
	public static final StoneProducts CANYON_COBBLE = new StoneProducts(AbstractBlock.Settings.create().strength(1.25F).requiresTool(), "canyon_cobblestone");
	@DataGenBlock
	public static final StoneProducts POURSTONE = new StoneProducts(AbstractBlock.Settings.create().strength(1.25F).requiresTool(), "pourstone");
	@DataGenBlock
	public static final StoneProducts SMOOTH_POURSTONE = new StoneProducts(AbstractBlock.Settings.create().strength(1.25F).requiresTool(), "smooth_pourstone");
	@DataGenBlock
	public static final StoneProducts CRACKED_POURSOTNE = new StoneProducts(AbstractBlock.Settings.create().strength(1.0F).requiresTool(), "cracked_pourstone");

	public static Block createSimpleBlock(String key, AbstractBlock.Settings settings)
	{
		return Registrar.block(Gadgets.id(key), Block::new, settings);
	}
	public static void register()
	{
	}
}
