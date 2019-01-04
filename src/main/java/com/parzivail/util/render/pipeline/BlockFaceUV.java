package com.parzivail.util.render.pipeline;

import com.google.gson.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

@SideOnly(Side.CLIENT)
public class BlockFaceUV
{
	public float[] uvs;
	public final int rotation;

	public BlockFaceUV(@Nullable float[] uvsIn, int rotationIn)
	{
		uvs = uvsIn;
		rotation = rotationIn;
	}

	public float getVertexU(int p_178348_1_)
	{
		if (uvs == null)
		{
			throw new NullPointerException("uvs");
		}
		else
		{
			int i = getVertexRotated(p_178348_1_);
			return i != 0 && i != 1 ? uvs[2] : uvs[0];
		}
	}

	public float getVertexV(int p_178346_1_)
	{
		if (uvs == null)
		{
			throw new NullPointerException("uvs");
		}
		else
		{
			int i = getVertexRotated(p_178346_1_);
			return i != 0 && i != 3 ? uvs[3] : uvs[1];
		}
	}

	private int getVertexRotated(int p_178347_1_)
	{
		return (p_178347_1_ + rotation / 90) % 4;
	}

	public int getVertexRotatedRev(int p_178345_1_)
	{
		return (p_178345_1_ + (4 - rotation / 90)) % 4;
	}

	public void setUvs(float[] uvsIn)
	{
		if (uvs == null)
		{
			uvs = uvsIn;
		}
	}

	@SideOnly(Side.CLIENT)
	static class Deserializer implements JsonDeserializer<BlockFaceUV>
	{
		public BlockFaceUV deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
		{
			JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
			float[] afloat = parseUV(jsonobject);
			int i = parseRotation(jsonobject);
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
}
