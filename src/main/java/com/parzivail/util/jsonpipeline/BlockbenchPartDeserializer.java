package com.parzivail.util.jsonpipeline;

import com.google.common.collect.Maps;
import com.google.gson.*;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.JsonUtils;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;

public class BlockbenchPartDeserializer implements JsonDeserializer<BlockbenchPart>
{
	public BlockbenchPart deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
	{
		JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
		Vector3f vector3f = this.parsePositionFrom(jsonobject);
		Vector3f vector3f1 = this.parsePositionTo(jsonobject);
		BlockPartRotation blockpartrotation = this.parseRotation(jsonobject);
		Vector3f blockpartrotated = this.parseRotated(jsonobject);
		QuadData extra = this.parseExtra(jsonobject);
		Map<EnumFacing, BlockPartFace> map = this.parseFacesCheck(p_deserialize_3_, jsonobject);

		boolean flag = JsonUtils.getBoolean(jsonobject, "shade", true);
		return new BlockbenchPart(vector3f, vector3f1, map, blockpartrotation, blockpartrotated, flag, extra);
	}

	private QuadData parseExtra(JsonObject object)
	{
		QuadData extra = QuadData.DEFAULT;

		if (object.has("quadData"))
		{
			JsonObject jsonobject = JsonUtils.getJsonObject(object, "quadData");
			boolean lit = JsonUtils.getBoolean(jsonobject, "lit", false);
			int lightColor = JsonUtils.getInt(jsonobject, "lightColor", -1);
			extra = new QuadData(lit, lightColor);
		}

		return extra;
	}

	@Nullable
	private BlockPartRotation parseRotation(JsonObject object)
	{
		BlockPartRotation blockpartrotation = null;

		if (object.has("rotation"))
		{
			JsonObject jsonobject = JsonUtils.getJsonObject(object, "rotation");
			Vector3f vector3f = this.parsePosition(jsonobject, "origin");
			vector3f.scale(0.0625F);
			EnumFacing.Axis enumfacing$axis = this.parseAxis(jsonobject);
			float f = this.parseAngle(jsonobject);
			boolean flag = JsonUtils.getBoolean(jsonobject, "rescale", false);
			blockpartrotation = new BlockPartRotation(vector3f, enumfacing$axis, f, flag);
		}

		return blockpartrotation;
	}

	@Nullable
	private Vector3f parseRotated(JsonObject object)
	{
		Vector3f blockpartrotation = null;

		if (object.has("rotated"))
			blockpartrotation = this.parsePosition(object, "rotated");

		return blockpartrotation;
	}

	private float parseAngle(JsonObject object)
	{
		return JsonUtils.getFloat(object, "angle");
	}

	private EnumFacing.Axis parseAxis(JsonObject object)
	{
		String s = JsonUtils.getString(object, "axis");
		EnumFacing.Axis enumfacing$axis = EnumFacing.Axis.byName(s.toLowerCase(Locale.ROOT));

		if (enumfacing$axis == null)
		{
			throw new JsonParseException("Invalid rotation axis: " + s);
		}
		else
		{
			return enumfacing$axis;
		}
	}

	private Map<EnumFacing, BlockPartFace> parseFacesCheck(JsonDeserializationContext deserializationContext, JsonObject object)
	{
		Map<EnumFacing, BlockPartFace> map = this.parseFaces(deserializationContext, object);

		if (map.isEmpty())
		{
			throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
		}
		else
		{
			return map;
		}
	}

	private Map<EnumFacing, BlockPartFace> parseFaces(JsonDeserializationContext deserializationContext, JsonObject object)
	{
		Map<EnumFacing, BlockPartFace> map = Maps.newEnumMap(EnumFacing.class);
		JsonObject jsonobject = JsonUtils.getJsonObject(object, "faces");

		for (Map.Entry<String, JsonElement> entry : jsonobject.entrySet())
		{
			EnumFacing enumfacing = this.parseEnumFacing(entry.getKey());
			map.put(enumfacing, deserializationContext.deserialize(entry.getValue(), BlockPartFace.class));
		}

		return map;
	}

	private EnumFacing parseEnumFacing(String name)
	{
		EnumFacing enumfacing = EnumFacing.byName(name);

		if (enumfacing == null)
		{
			throw new JsonParseException("Unknown facing: " + name);
		}
		else
		{
			return enumfacing;
		}
	}

	private Vector3f parsePositionTo(JsonObject object)
	{
		return this.parsePosition(object, "to");
	}

	private Vector3f parsePositionFrom(JsonObject object)
	{
		return this.parsePosition(object, "from");
	}

	private Vector3f parsePosition(JsonObject object, String memberName)
	{
		if (!object.has(memberName))
			return null;

		JsonArray jsonarray = JsonUtils.getJsonArray(object, memberName);

		if (jsonarray.size() != 3)
		{
			throw new JsonParseException("Expected 3 " + memberName + " values, found: " + jsonarray.size());
		}
		else
		{
			float[] afloat = new float[3];

			for (int i = 0; i < afloat.length; ++i)
			{
				afloat[i] = JsonUtils.getFloat(jsonarray.get(i), memberName + "[" + i + "]");
			}

			return new Vector3f(afloat[0], afloat[1], afloat[2]);
		}
	}
}
