package com.parzivail.util.render.decal;

import com.parzivail.swg.Resources;
import com.parzivail.util.ui.Fx.D2;
import com.parzivail.util.ui.Fx.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Objects;

public class Decal
{
	public static final int BULLET_IMPACT = 0;
	private static final HashMap<Integer, ResourceLocation[]> decalTextures = new HashMap<>();

	static
	{
		decalTextures.put(BULLET_IMPACT, new ResourceLocation[] {
				new ResourceLocation(Resources.MODID, "textures/decal/burn1.png"),
				new ResourceLocation(Resources.MODID, "textures/decal/burn2.png"),
				new ResourceLocation(Resources.MODID, "textures/decal/burn3.png"),
				new ResourceLocation(Resources.MODID, "textures/decal/burn4.png")
		});
	}

	public final float x;
	public final float y;
	public final float z;
	public final int blockX;
	public final int blockY;
	public final int blockZ;
	public final float size;
	public final float rotation;
	public final EnumFacing direction;

	private final int type;
	private final int textureIdx;

	private final long createTime;
	private final long deathTime;

	public Decal(int type, int blockX, int blockY, int blockZ, float x, float y, float z, float size, EnumFacing direction)
	{
		this.blockX = blockX;
		this.blockY = blockY;
		this.blockZ = blockZ;
		this.x = x;
		this.y = y;
		this.z = z;
		this.direction = direction;
		this.size = size;

		int hash = Math.abs(hashCode());
		this.type = type;
		textureIdx = hash % decalTextures.get(type).length;
		rotation = hash % 360;

		createTime = Util.GetMillis();
		deathTime = 20000;
	}

	public boolean shouldDie()
	{
		return getLifeRemaining() <= 0;
	}

	public float getLifeRemaining()
	{
		return 1 - (Util.GetMillis() - createTime) / (float)deathTime;
	}

	public void render()
	{
		Minecraft.getMinecraft().renderEngine.bindTexture(decalTextures.get(type)[textureIdx]);
		D2.DrawSolidRectangle(-1, -1, 2, 2);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(x, y, z);
	}
}
