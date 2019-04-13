package com.parzivail.swg.model;

import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.util.List;

public class GenericModelReference
{
	public final PModelBlock modelCustom;
	public final ModelBlock modelVanilla;

	public GenericModelReference(PModelBlock model)
	{
		modelCustom = model;
		modelVanilla = null;
	}

	public GenericModelReference(ModelBlock model)
	{
		modelCustom = null;
		modelVanilla = model;
	}

	public List<BlockPart> getElements()
	{
		if (modelCustom != null)
			return modelCustom.getElements();
		if (modelVanilla != null)
			return modelVanilla.getElements();
		throw new IllegalStateException("Null reference!");
	}

	public boolean isAmbientOcclusion()
	{
		if (modelCustom != null)
			return modelCustom.isAmbientOcclusion();
		if (modelVanilla != null)
			return modelVanilla.isAmbientOcclusion();
		throw new IllegalStateException("Null reference!");
	}

	public boolean isResolved()
	{
		if (modelCustom != null)
			return modelCustom.isAmbientOcclusion();
		if (modelVanilla != null)
			return modelVanilla.isAmbientOcclusion();
		throw new IllegalStateException("Null reference!");
	}

	public String resolveTextureName(String textureName, PModelBlock.Bookkeep bookkeep)
	{
		if (modelCustom != null)
			return modelCustom.resolveTextureName(textureName, bookkeep);
		if (modelVanilla != null)
			return modelVanillaResolveTextureName(modelVanilla, textureName, bookkeep);
		throw new IllegalStateException("Null reference!");
	}

	private String modelVanillaResolveTextureName(ModelBlock thus, String textureName, PModelBlock.Bookkeep bookkeep)
	{
		if (startsWithHash(textureName))
		{
			if (thus == bookkeep.modelExt)
			{
				//LOGGER.warn("Unable to resolve texture due to upward reference: {} in {}", textureName, thus.name);
				return "missingno";
			}
			else
			{
				String s = thus.textures.get(textureName.substring(1));

				if (s == null && thus.parent != null)
				{
					s = modelVanillaResolveTextureName(thus.parent, textureName, bookkeep);
				}

				bookkeep.modelExt = this;

				if (s != null && startsWithHash(s))
				{
					s = bookkeep.model.resolveTextureName(s, bookkeep);
				}

				return s != null && !startsWithHash(s) ? s : "missingno";
			}
		}
		else
		{
			return textureName;
		}
	}

	private boolean startsWithHash(String hash)
	{
		return hash.charAt(0) == '#';
	}

	public Object getRootModel()
	{
		if (modelCustom != null)
			return modelCustom.getRootModel();
		if (modelVanilla != null)
			return modelVanilla.getRootModel();
		throw new IllegalStateException("Null reference!");
	}

	public ItemTransformVec3f getTransform(ItemCameraTransforms.TransformType type)
	{
		if (modelCustom != null)
			return modelCustom.getTransform(type);
		if (modelVanilla != null)
			return modelVanillaGetTransform(modelVanilla, type);
		throw new IllegalStateException("Null reference!");
	}

	private ItemTransformVec3f modelVanillaGetTransform(ModelBlock thus, ItemCameraTransforms.TransformType type)
	{
		ItemCameraTransforms cameraTransforms = ReflectionHelper.getPrivateValue(ModelBlock.class, thus, "cameraTransforms", "field_177557_a", "a");
		return thus.parent != null && !cameraTransforms.hasCustomTransform(type) ? modelVanillaGetTransform(thus.parent, type) : cameraTransforms.getTransform(type);
	}
}
