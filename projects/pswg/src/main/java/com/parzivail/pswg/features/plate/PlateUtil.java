package com.parzivail.pswg.features.plate;

import com.mojang.serialization.Codec;
import com.parzivail.p3d.P3dManager;
import com.parzivail.p3d.P3dModel;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.container.SwgTags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import java.util.List;

public class PlateUtil
{
	public static final Identifier MODEL = Resources.id("block/food/plate");
	private static final P3dModel FALLBACK_MODEL = P3dManager.INSTANCE.get(Resources.id("block/food/haroun_bread"));
	private static final Identifier FALLBACK_TEXTURE = Resources.id("textures/block/model/food/haroun_bread.png");
	public static final Identifier TEXTURE = Resources.id("textures/block/model/food/plate.png");
	public static float getHeightOverride(String s){
		String food = s.substring(16);
		float override = 0;
		switch (food){
			case "meiloorun": return (5f/16f);
			case "bantha_cookie": return (3f/16f);
			case "pallie_fruit": return (5f/16f);
			case "deb_deb": return (8f/16f);
			default: return 0;
		}
	}


	public static int calculateCurrentFood(List<ItemStack> foodList, int i)
	{
		int sT = 0;
		int layerT = 0;
		for (int j = 0; j <= i; j++)
		{
			if (sT == getLayerMaxPosition(layerT))
				layerT++;
			if (foodList.get(j).isIn(SwgTags.Items.MAIN_COURSE))
				sT += (5 - layerT);
			else
				sT++;
		}
		return sT;
	}

	public static int calculateLayerFood(List<ItemStack> foodList, int layer)
	{
		for (int i = 0; i < foodList.size(); i++)
		{
			if (calculateCurrentFood(foodList, i) == getLayerMaxPosition(layer))
				return calculateCurrentFood(foodList, i);
			else if (i == foodList.size() - 1)
				return calculateCurrentFood(foodList, i);
		}
		return 0;
	}

	public static int calculateFood(List<ItemStack> foodList)
	{
		int sT = 0;
		int layerT = 0;
		for (int i = 0; i < foodList.size(); i++)
		{
			if (foodList.get(i).isIn(SwgTags.Items.MAIN_COURSE))
				sT += (5 - layerT);
			else
				sT++;
			if (sT == getLayerMaxPosition(layerT))
				layerT++;
		}
		return sT;
	}

	public static int calculateLayer(int foodCount)
	{
		for (int l = 0; l < 5; l++)
		{
			if (foodCount > 5 - l)
				foodCount -= (5 - l);
			else
				return l;
		}
		return 0;
	}

	public static int getLayerMaxPosition(int layer)
	{
		int s = 0;
		for (int i = 0; i <= layer; i++)
			s += (5 - i);
		return s;
	}

	public static int calculateLayerSize(int layer, int foodCount, List<ItemStack> foodList, int i)
	{
		if (foodList.get(i).isIn(SwgTags.Items.MAIN_COURSE))
			return 1;
		for (int j = 0; j < layer; j++)
			foodCount -= (5 - j);
		if (foodCount != 0)
			return foodCount;

		return 1;
	}

	@Environment(EnvType.CLIENT)
	public static void renderPlate(List<ItemStack> foodList, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, boolean isItem)
	{
		matrices.push();
		matrices.translate(0f, 1f / 16f, 0f);
		if (!foodList.isEmpty() && !isItem)
			matrices.translate(0.3f, 0f, 0.3f);
		if (isItem)
		{
			matrices.translate(-0.25f, 0f, -0.25f);
		}
		int foodCount = PlateUtil.calculateFood(foodList);
		int layer = PlateUtil.calculateLayer(foodCount);

		double addedHeight = 0;
		boolean adjustedMiddle = false;
		for (int i = 0; i < foodList.size(); i++)
		{
			boolean changeLayer = false;
			var registryKey = foodList.get(i).getItem().getRegistryEntry().getKey().get().getValue().getPath();
			var id = Resources.id("block/food/" + registryKey);
			var foodModel = P3dManager.INSTANCE.get(id);
			var foodTexture = new Identifier(id.getNamespace(), "textures/block/model/food/" + registryKey + ".png");
			if (foodModel == null)
			{
				foodModel = FALLBACK_MODEL;
				foodTexture = FALLBACK_TEXTURE;
			}
			Identifier finalFoodTexture = foodTexture;
			int currentFoodCount = PlateUtil.calculateCurrentFood(foodList, i);
			int currentLayer = PlateUtil.calculateLayer(currentFoodCount);
			if (currentFoodCount == PlateUtil.getLayerMaxPosition(currentLayer))
				changeLayer = true;
			matrices.translate(0.2, 0, 0.2);
			int layerFoodCount = PlateUtil.calculateLayerFood(foodList, currentLayer);
			float currentLayerSize = PlateUtil.calculateLayerSize(currentLayer, layerFoodCount, foodList, i);
			if (currentLayerSize == 1)
			{
				matrices.translate(0.2, 0, 0.2);
				adjustedMiddle = true;
			}
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(360f / currentLayerSize), 0f, 0, 0f);
			matrices.translate(-0.2, 0, -0.2);

			float override = getHeightOverride(id.toShortTranslationKey());
			if(override!=0)
			{
				if (addedHeight < override)
					addedHeight = override;
			}else if (addedHeight < foodModel.bounds().getLengthZ())
				addedHeight = foodModel.bounds().getLengthZ();
			foodModel.render(matrices, vertexConsumers, null, null, (v, tag, obj) -> v.getBuffer(RenderLayer.getEntityCutout(finalFoodTexture)), light, 0, 255, 255, 255, 255);
			if (changeLayer)
			{
				matrices.translate(0, addedHeight, 0);
				addedHeight = 0;
			}
			if (adjustedMiddle)
			{
				matrices.translate(-0.2, 0, -0.2);
				adjustedMiddle = false;
			}
		}
		matrices.pop();
	}
}
