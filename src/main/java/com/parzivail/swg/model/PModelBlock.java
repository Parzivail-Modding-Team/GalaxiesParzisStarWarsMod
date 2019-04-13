package com.parzivail.swg.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.*;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;

@SideOnly(Side.CLIENT)
public class PModelBlock
{
	private static final Logger LOGGER = LogManager.getLogger();
	@VisibleForTesting
	static final Gson SERIALIZER = (new GsonBuilder()).registerTypeAdapter(PModelBlock.class, new Deserializer()).registerTypeAdapter(PBlockPart.class, new BlockPartDeserializer()).registerTypeAdapter(BlockPartFace.class, new BlockPartFaceDeserializer()).registerTypeAdapter(BlockFaceUV.class, new BlockFaceUVDeserializer()).registerTypeAdapter(ItemTransformVec3f.class, new ItemTransformVec3fDeserializer()).registerTypeAdapter(ItemCameraTransforms.class, new ItemCameraTransformsDeserializer()).registerTypeAdapter(ItemOverride.class, new ItemOverrideDeserializer()).create();
	private final List<PBlockPart> elements;
	private final boolean gui3d;
	public final boolean ambientOcclusion;
	private final ItemCameraTransforms cameraTransforms;
	private final List<ItemOverride> overrides;
	public String name = "";
	@VisibleForTesting
	public final Map<String, String> textures;
	@VisibleForTesting
	public PModelBlock parent;
	@VisibleForTesting
	protected ResourceLocation parentLocation;

	public static PModelBlock deserialize(Reader readerIn)
	{
		return JsonUtils.gsonDeserialize(SERIALIZER, readerIn, PModelBlock.class, false);
	}

	public static PModelBlock deserialize(String jsonString)
	{
		return deserialize(new StringReader(jsonString));
	}

	public PModelBlock(@Nullable ResourceLocation parentLocationIn, List<PBlockPart> elementsIn, Map<String, String> texturesIn, boolean ambientOcclusionIn, boolean gui3dIn, ItemCameraTransforms cameraTransformsIn, List<ItemOverride> overridesIn)
	{
		this.elements = elementsIn;
		this.ambientOcclusion = ambientOcclusionIn;
		this.gui3d = gui3dIn;
		this.textures = texturesIn;
		this.parentLocation = parentLocationIn;
		this.cameraTransforms = cameraTransformsIn;
		this.overrides = overridesIn;
	}

	public List<PBlockPart> getElements()
	{
		return this.elements.isEmpty() && this.hasParent() ? this.parent.getElements() : this.elements;
	}

	private boolean hasParent()
	{
		return this.parent != null;
	}

	public boolean isAmbientOcclusion()
	{
		return this.hasParent() ? this.parent.isAmbientOcclusion() : this.ambientOcclusion;
	}

	public boolean isGui3d()
	{
		return this.gui3d;
	}

	public boolean isResolved()
	{
		return this.parentLocation == null || this.parent != null && this.parent.isResolved();
	}

	public void getParentFromMap(Map<ResourceLocation, PModelBlock> p_178299_1_)
	{
		if (this.parentLocation != null)
		{
			this.parent = p_178299_1_.get(this.parentLocation);
		}
	}

	public Collection<ResourceLocation> getOverrideLocations()
	{
		Set<ResourceLocation> set = Sets.newHashSet();

		for (ItemOverride itemoverride : this.overrides)
		{
			set.add(itemoverride.getLocation());
		}

		return set;
	}

	public List<ItemOverride> getOverrides()
	{
		return this.overrides;
	}

	public ItemOverrideList createOverrides()
	{
		return this.overrides.isEmpty() ? ItemOverrideList.NONE : new ItemOverrideList(this.overrides);
	}

	public boolean isTexturePresent(String textureName)
	{
		return !"missingno".equals(this.resolveTextureName(textureName));
	}

	public String resolveTextureName(String textureName)
	{
		if (!this.startsWithHash(textureName))
		{
			textureName = '#' + textureName;
		}

		return this.resolveTextureName(textureName, new Bookkeep(this));
	}

	String resolveTextureName(String textureName, Bookkeep p_178302_2_)
	{
		if (this.startsWithHash(textureName))
		{
			if (this == p_178302_2_.modelExt)
			{
				LOGGER.warn("Unable to resolve texture due to upward reference: {} in {}", textureName, this.name);
				return "missingno";
			}
			else
			{
				String s = this.textures.get(textureName.substring(1));

				if (s == null && this.hasParent())
				{
					s = this.parent.resolveTextureName(textureName, p_178302_2_);
				}

				p_178302_2_.modelExt = this;

				if (s != null && this.startsWithHash(s))
				{
					s = p_178302_2_.model.resolveTextureName(s, p_178302_2_);
				}

				return s != null && !this.startsWithHash(s) ? s : "missingno";
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

	@Nullable
	public ResourceLocation getParentLocation()
	{
		return this.parentLocation;
	}

	public Object getRootModel()
	{
		return this.hasParent() ? this.parent.getRootModel() : this;
	}

	public ItemCameraTransforms getAllTransforms()
	{
		ItemTransformVec3f itemtransformvec3f = this.getTransform(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND);
		ItemTransformVec3f itemtransformvec3f1 = this.getTransform(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
		ItemTransformVec3f itemtransformvec3f2 = this.getTransform(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND);
		ItemTransformVec3f itemtransformvec3f3 = this.getTransform(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND);
		ItemTransformVec3f itemtransformvec3f4 = this.getTransform(ItemCameraTransforms.TransformType.HEAD);
		ItemTransformVec3f itemtransformvec3f5 = this.getTransform(ItemCameraTransforms.TransformType.GUI);
		ItemTransformVec3f itemtransformvec3f6 = this.getTransform(ItemCameraTransforms.TransformType.GROUND);
		ItemTransformVec3f itemtransformvec3f7 = this.getTransform(ItemCameraTransforms.TransformType.FIXED);
		return new ItemCameraTransforms(itemtransformvec3f, itemtransformvec3f1, itemtransformvec3f2, itemtransformvec3f3, itemtransformvec3f4, itemtransformvec3f5, itemtransformvec3f6, itemtransformvec3f7);
	}

	ItemTransformVec3f getTransform(ItemCameraTransforms.TransformType type)
	{
		return this.parent != null && !this.cameraTransforms.hasCustomTransform(type) ? this.parent.getTransform(type) : this.cameraTransforms.getTransform(type);
	}

	public static void checkModelHierarchy(Map<ResourceLocation, net.minecraft.client.renderer.block.model.ModelBlock> p_178312_0_)
	{
		for (net.minecraft.client.renderer.block.model.ModelBlock modelblock : p_178312_0_.values())
		{
			try
			{
				net.minecraft.client.renderer.block.model.ModelBlock modelblock1 = modelblock.parent;

				for (net.minecraft.client.renderer.block.model.ModelBlock modelblock2 = modelblock1.parent; modelblock1 != modelblock2; modelblock2 = modelblock2.parent.parent)
				{
					modelblock1 = modelblock1.parent;
				}

				throw new net.minecraft.client.renderer.block.model.ModelBlock.LoopException();
			}
			catch (NullPointerException var5)
			{
			}
		}
	}

	public ModelBlock makeModelBlock()
	{
		return new ModelBlock(parentLocation, makeNormalElements(), textures, ambientOcclusion, gui3d, cameraTransforms, overrides);
	}

	private List<BlockPart> makeNormalElements()
	{
		return new ArrayList<>(elements);
	}

	@SideOnly(Side.CLIENT)
	static final class Bookkeep
	{
		public final PModelBlock model;
		public Object modelExt;

		private Bookkeep(PModelBlock modelIn)
		{
			this.model = modelIn;
		}
	}

	@SideOnly(Side.CLIENT)
	public static class Deserializer implements JsonDeserializer<PModelBlock>
	{
		public PModelBlock deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
		{
			JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
			List<PBlockPart> list = this.getModelElements(p_deserialize_3_, jsonobject);
			String s = this.getParent(jsonobject);
			Map<String, String> map = this.getTextures(jsonobject);
			boolean flag = this.getAmbientOcclusionEnabled(jsonobject);
			ItemCameraTransforms itemcameratransforms = ItemCameraTransforms.DEFAULT;

			if (jsonobject.has("display"))
			{
				JsonObject jsonobject1 = JsonUtils.getJsonObject(jsonobject, "display");
				itemcameratransforms = p_deserialize_3_.deserialize(jsonobject1, ItemCameraTransforms.class);
			}

			List<ItemOverride> list1 = this.getItemOverrides(p_deserialize_3_, jsonobject);
			ResourceLocation resourcelocation = s.isEmpty() ? null : new ResourceLocation(s);
			return new PModelBlock(resourcelocation, list, map, flag, true, itemcameratransforms, list1);
		}

		protected List<ItemOverride> getItemOverrides(JsonDeserializationContext deserializationContext, JsonObject object)
		{
			List<ItemOverride> list = Lists.newArrayList();

			if (object.has("overrides"))
			{
				for (JsonElement jsonelement : JsonUtils.getJsonArray(object, "overrides"))
				{
					list.add(deserializationContext.deserialize(jsonelement, ItemOverride.class));
				}
			}

			return list;
		}

		private Map<String, String> getTextures(JsonObject object)
		{
			Map<String, String> map = Maps.newHashMap();

			if (object.has("textures"))
			{
				JsonObject jsonobject = object.getAsJsonObject("textures");

				for (Entry<String, JsonElement> entry : jsonobject.entrySet())
				{
					map.put(entry.getKey(), entry.getValue().getAsString());
				}
			}

			return map;
		}

		private String getParent(JsonObject object)
		{
			return JsonUtils.getString(object, "parent", "");
		}

		protected boolean getAmbientOcclusionEnabled(JsonObject object)
		{
			return JsonUtils.getBoolean(object, "ambientocclusion", true);
		}

		protected List<PBlockPart> getModelElements(JsonDeserializationContext deserializationContext, JsonObject object)
		{
			List<PBlockPart> list = Lists.newArrayList();

			if (object.has("elements"))
			{
				for (JsonElement jsonelement : JsonUtils.getJsonArray(object, "elements"))
				{
					list.add(deserializationContext.deserialize(jsonelement, PBlockPart.class));
				}
			}

			return list;
		}
	}

	@SideOnly(Side.CLIENT)
	public static class LoopException extends RuntimeException
	{
	}
}
