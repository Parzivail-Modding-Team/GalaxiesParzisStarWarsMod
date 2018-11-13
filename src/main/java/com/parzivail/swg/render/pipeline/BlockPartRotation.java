package com.parzivail.swg.render.pipeline;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.lwjgl.util.vector.Vector3f;

@SideOnly(Side.CLIENT)
public class BlockPartRotation
{
	public final Vector3f origin;
	public final EnumFacingAxis axis;
	public final float angle;
	public final boolean rescale;

	public BlockPartRotation(Vector3f originIn, EnumFacingAxis axisIn, float angleIn, boolean rescaleIn)
	{
		origin = originIn;
		axis = axisIn;
		angle = angleIn;
		rescale = rescaleIn;
	}
}
