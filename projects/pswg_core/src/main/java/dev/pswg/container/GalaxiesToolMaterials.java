package dev.pswg.container;

import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;

public class GalaxiesToolMaterials
{
	//TODO: MAKE CUSTOM BLOCK MINING TAGS
	public static final ToolMaterial BESKAR = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2000, 10.0F, 5.0F, 16, GalaxiesItems.Tags.BESKAR_TOOL_MATERIALS_TAG);
	public static final ToolMaterial DURASTEEL = new ToolMaterial(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 500, 7.0F, 2.5F, 10, GalaxiesItems.Tags.DURASTEEL_TOOL_MATERIALS_TAG);
	public static final ToolMaterial TITANIUM = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 1000, 8.0F, 3.5F, 12, GalaxiesItems.Tags.TITANIUM_TOOL_MATERIALS_TAG);

	public static void register()
	{

	}
}
