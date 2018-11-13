package com.parzivail.swg.render.pipeline;

import com.google.gson.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

@SideOnly(Side.CLIENT)
public class BlockPartFace
{
	public static final EnumFacing FACING_DEFAULT = null;
	public final EnumFacing cullFace;
	public final int tintIndex;
	public final String texture;
	public final BlockFaceUV blockFaceUV;

	public BlockPartFace(@Nullable EnumFacing cullFaceIn, int tintIndexIn, String textureIn, BlockFaceUV blockFaceUVIn)
	{
		cullFace = cullFaceIn;
		tintIndex = tintIndexIn;
		texture = textureIn;
		blockFaceUV = blockFaceUVIn;
	}

	@SideOnly(Side.CLIENT)
	static class Deserializer implements JsonDeserializer<BlockPartFace>
	{
		public BlockPartFace deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
		{
			JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
			EnumFacing enumfacing = parseCullFace(jsonobject);
			int i = parseTintIndex(jsonobject);
			String s = parseTexture(jsonobject);
			BlockFaceUV blockfaceuv = p_deserialize_3_.deserialize(jsonobject, BlockFaceUV.class);
			return new BlockPartFace(enumfacing, i, s, blockfaceuv);
		}

		protected int parseTintIndex(JsonObject object)
		{
			return JsonUtils.getInt(object, "tintindex", -1);
		}

		private String parseTexture(JsonObject object)
		{
			return JsonUtils.getString(object, "texture");
		}

		@Nullable
		private EnumFacing parseCullFace(JsonObject object)
		{
			String s = JsonUtils.getString(object, "cullface", "");
			if (s.isEmpty())
				return null;
			return EnumFacing.valueOf(s.toUpperCase());
		}
	}
}
