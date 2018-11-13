package com.parzivail.swg.render.pipeline;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class ModelBlock
{
	private static final Logger LOGGER = LogManager.getLogger();
	@VisibleForTesting
	static final Gson SERIALIZER = (new GsonBuilder()).registerTypeAdapter(ModelBlock.class, new ModelBlock.Deserializer()).registerTypeAdapter(BlockPart.class, new BlockPart.Deserializer()).registerTypeAdapter(BlockPartFace.class, new BlockPartFace.Deserializer()).registerTypeAdapter(BlockFaceUV.class, new BlockFaceUV.Deserializer()).create();
	private final List<BlockPart> elements;
	private final boolean gui3d;
	public final boolean ambientOcclusion;
	public String name = "";
	@VisibleForTesting
	public final Map<String, String> textures;
	@VisibleForTesting
	public ModelBlock parent;
	@VisibleForTesting
	protected ResourceLocation parentLocation;

	public static ModelBlock deserialize(Reader readerIn)
	{
		return JsonUtils.gsonDeserialize(SERIALIZER, readerIn, ModelBlock.class, false);
	}

	public static ModelBlock deserialize(String jsonString)
	{
		return deserialize(new StringReader(jsonString));
	}

	public ModelBlock(@Nullable ResourceLocation parentLocationIn, List<BlockPart> elementsIn, Map<String, String> texturesIn, boolean ambientOcclusionIn, boolean gui3dIn)
	{
		elements = elementsIn;
		ambientOcclusion = ambientOcclusionIn;
		gui3d = gui3dIn;
		textures = texturesIn;
		parentLocation = parentLocationIn;
	}

	public List<BlockPart> getElements()
	{
		return elements.isEmpty() && hasParent() ? parent.getElements() : elements;
	}

	private boolean hasParent()
	{
		return parent != null;
	}

	public boolean isAmbientOcclusion()
	{
		return hasParent() ? parent.isAmbientOcclusion() : ambientOcclusion;
	}

	public boolean isGui3d()
	{
		return gui3d;
	}

	public boolean isResolved()
	{
		return parentLocation == null || parent != null && parent.isResolved();
	}

	public void getParentFromMap(Map<ResourceLocation, ModelBlock> p_178299_1_)
	{
		if (parentLocation != null)
		{
			parent = p_178299_1_.get(parentLocation);
		}
	}

	public boolean isTexturePresent(String textureName)
	{
		return !"missingno".equals(resolveTextureName(textureName));
	}

	public String resolveTextureName(String textureName)
	{
		if (!startsWithHash(textureName))
		{
			textureName = '#' + textureName;
		}

		return resolveTextureName(textureName, new ModelBlock.Bookkeep(this));
	}

	private String resolveTextureName(String textureName, ModelBlock.Bookkeep p_178302_2_)
	{
		if (startsWithHash(textureName))
		{
			if (this == p_178302_2_.modelExt)
			{
				LOGGER.warn("Unable to resolve texture due to upward reference: {} in {}", textureName, name);
				return "missingno";
			}
			else
			{
				String s = textures.get(textureName.substring(1));

				if (s == null && hasParent())
				{
					s = parent.resolveTextureName(textureName, p_178302_2_);
				}

				p_178302_2_.modelExt = this;

				if (s != null && startsWithHash(s))
				{
					s = p_178302_2_.model.resolveTextureName(s, p_178302_2_);
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

	@Nullable
	public ResourceLocation getParentLocation()
	{
		return parentLocation;
	}

	public ModelBlock getRootModel()
	{
		return hasParent() ? parent.getRootModel() : this;
	}

	public static void checkModelHierarchy(Map<ResourceLocation, ModelBlock> p_178312_0_)
	{
		for (ModelBlock modelblock : p_178312_0_.values())
		{
			try
			{
				ModelBlock modelblock1 = modelblock.parent;

				for (ModelBlock modelblock2 = modelblock1.parent; modelblock1 != modelblock2; modelblock2 = modelblock2.parent.parent)
				{
					modelblock1 = modelblock1.parent;
				}

				throw new ModelBlock.LoopException();
			}
			catch (NullPointerException var5)
			{
			}
		}
	}

	@SideOnly(Side.CLIENT)
	static final class Bookkeep
	{
		public final ModelBlock model;
		public ModelBlock modelExt;

		private Bookkeep(ModelBlock modelIn)
		{
			model = modelIn;
		}
	}

	@SideOnly(Side.CLIENT)
	public static class Deserializer implements JsonDeserializer<ModelBlock>
	{
		public ModelBlock deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
		{
			JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
			List<BlockPart> list = getModelElements(p_deserialize_3_, jsonobject);
			String s = getParent(jsonobject);
			Map<String, String> map = getTextures(jsonobject);
			boolean flag = getAmbientOcclusionEnabled(jsonobject);

			ResourceLocation resourcelocation = s.isEmpty() ? null : new ResourceLocation(s);
			return new ModelBlock(resourcelocation, list, map, flag, true);
		}

		private Map<String, String> getTextures(JsonObject object)
		{
			Map<String, String> map = Maps.newHashMap();

			if (object.has("textures"))
			{
				if (JsonUtils.isJsonArray(object, "textures"))
				{
					JsonArray jsonobject = object.getAsJsonArray("textures");

					int i = 0;
					for (JsonElement entry : jsonobject)
					{
						map.put(String.format("#%s", i), entry.getAsString());
						i++;
					}
				}
				else
				{
					JsonObject jsonobject = object.getAsJsonObject("textures");

					for (Map.Entry<String, JsonElement> entry : jsonobject.entrySet())
					{
						map.put(entry.getKey(), entry.getValue().getAsString());
					}
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

		protected List<BlockPart> getModelElements(JsonDeserializationContext deserializationContext, JsonObject object)
		{
			List<BlockPart> list = Lists.newArrayList();

			if (object.has("elements"))
			{
				for (JsonElement jsonelement : JsonUtils.getJsonArray(object, "elements"))
				{
					list.add(deserializationContext.deserialize(jsonelement, BlockPart.class));
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
