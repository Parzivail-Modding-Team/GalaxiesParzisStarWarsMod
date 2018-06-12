package com.parzivail.swg.weapon.blastermodule.scope;

import com.parzivail.util.ui.Fx;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class ScopeReflex extends BlasterScope
{
	public ScopeReflex()
	{
		super("reflex", 150);
	}

	@Override
	public void draw(ScaledResolution sr, EntityPlayer player, ItemStack stack)
	{
		GL11.glColor4f(1, 0, 0, 1);

		float d = 3;
		GL11.glLineWidth(2);
		Fx.D2.DrawWireCircle(0, 0, d);
		Fx.D2.DrawPoint(0, 0, 5);

		Fx.D2.DrawLine(0, d, 0, d + 1.5f);
		Fx.D2.DrawLine(0, -d, 0, -d - 1.5f);
		Fx.D2.DrawLine(d, 0, d + 1.5f, 0);
		Fx.D2.DrawLine(-d, 0, -d - 1.5f, 0);

		// Type 2
		GL.PushAttrib(AttribMask.EnableBit);
		GL.Disable(EnableCap.CullFace);
		GL.Enable(EnableCap.Blend);

		GL.Color(0.1f, 0.1f, 0.15f, 0.9f);

		GL.PushMatrix();
		GL.Translate(0, 0, -100);

		GL.TessBeginPolygon(null);
		GL.TessNextContour();

		GL.Vertex2(-sr.getScaledWidth_double() / 2, -sr.getScaledHeight_double() / 2);
		GL.Vertex2(sr.getScaledWidth_double() / 2, -sr.getScaledHeight_double() / 2);
		GL.Vertex2(sr.getScaledWidth_double() / 2, sr.getScaledHeight_double() / 2);
		GL.Vertex2(-sr.getScaledWidth_double() / 2, sr.getScaledHeight_double() / 2);

		GL.TessNextContour();

		// Type 2
		//		GL.Vertex2(0, 0);
		//		for (int i = 360; i >= 0; i--)
		//		{
		//			float p = (float)(i / 180f * Math.PI);
		//			if (i == 60 || i == 120 || i == 240 || i == 300)
		//				GL.Vertex2(MathHelper.cos(p) * 80, MathHelper.sin(p) * 80);
		//			if (i > 120 && i < 240)
		//				GL.Vertex2(MathHelper.cos(p) * 95 - 5, MathHelper.sin(p) * 95);
		//			if (i > 300 || i < 60)
		//				GL.Vertex2(MathHelper.cos(p) * 95 + 5, MathHelper.sin(p) * 95);
		//		}

		// Type 1
		GL.Vertex2(0, 0);
		for (int i = 360; i >= 0; i--)
		{
			float p = (float)(i / 180f * Math.PI);
			if ((i >= 60 && i <= 120) || (i > 240 && i <= 300))
				GL.Vertex2(MathHelper.cos(p) * 80, MathHelper.sin(p) * 80);
			if (i > 120 && i <= 240)
				GL.Vertex2(MathHelper.cos(p) * 80 - 15, MathHelper.sin(p) * 80);
			if (i > 300 || i < 60)
				GL.Vertex2(MathHelper.cos(p) * 80 + 15, MathHelper.sin(p) * 80);
		}

		GL.TessEndPolygon();
		GL.PopAttrib();
		GL.PopMatrix();
	}

	@Override
	public float getZoomLevel()
	{
		return 1;
	}
}
