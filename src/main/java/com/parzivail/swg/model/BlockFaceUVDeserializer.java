package com.parzivail.swg.model;

import com.google.gson.*;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.util.JsonUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

public class BlockFaceUVDeserializer implements JsonDeserializer<BlockFaceUV>
{
	public BlockFaceUV deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
	{
		JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
		float[] afloat = this.parseUV(jsonobject);
		int i = this.parseRotation(jsonobject);
		return new BlockFaceUV(afloat, i);
	}

	protected int parseRotation(JsonObject object)
	{
		int i = JsonUtils.getInt(object, "rotation", 0);

		if (i >= 0 && i % 90 == 0 && i / 90 <= 3)
		{
			return i;
		}
		else
		{
			throw new JsonParseException("Invalid rotation " + i + " found, only 0/90/180/270 allowed");
		}
	}

	@Nullable
	private float[] parseUV(JsonObject object)
	{
		if (!object.has("uv"))
		{
			return null;
		}
		else
		{
			JsonArray jsonarray = JsonUtils.getJsonArray(object, "uv");

			if (jsonarray.size() != 4)
			{
				throw new JsonParseException("Expected 4 uv values, found: " + jsonarray.size());
			}
			else
			{
				float[] afloat = new float[4];

				for (int i = 0; i < afloat.length; ++i)
				{
					afloat[i] = JsonUtils.getFloat(jsonarray.get(i), "uv[" + i + "]");
				}

				return afloat;
			}
		}
	}
}
