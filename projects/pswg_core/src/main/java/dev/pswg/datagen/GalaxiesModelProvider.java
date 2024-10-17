package dev.pswg.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.ModelIds;
import net.minecraft.data.client.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

/**
 * Provides extra utilities to register block and item models
 */
public abstract class GalaxiesModelProvider extends FabricModelProvider
{
	public GalaxiesModelProvider(FabricDataOutput output)
	{
		super(output);
	}

	/**
	 * Generate a model for an item that points to a texture that does not derive from
	 * the item's identifier
	 *
	 * @param itemModelGenerator The generator into which the item will be uploaded
	 * @param item               The item to generate a model for
	 * @param texture            The texture to assign to the item
	 * @param model              The model that will be uploaded with the given texture
	 */
	protected static void register(ItemModelGenerator itemModelGenerator, Item item, Identifier texture, Model model)
	{
		model.upload(ModelIds.getItemModelId(item), TextureMap.layer0(texture), itemModelGenerator.writer);
	}
}
