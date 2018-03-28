package com.parzivail.swg.render;

import com.parzivail.util.ui.Fx;
import com.parzivail.util.ui.GLPalette;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

import java.util.Objects;

public class Decal
{
	public static final int BULLET_IMPACT = 0;

	public final float x;
	public final float y;
	public final float z;
	public final EnumFacing direction;

	private final int type;

	public Decal(int type, float x, float y, float z, EnumFacing direction)
	{
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
		this.direction = direction;
	}

	public void render(Vec3 playerPos)
	{
		GL.Disable(EnableCap.Texture2D);
		GLPalette.glColorI(hashCode());
		Fx.D2.DrawSolidCircle(0, 0, 0.05f);
		GL.Enable(EnableCap.Texture2D);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(x, y, z);
	}
}
