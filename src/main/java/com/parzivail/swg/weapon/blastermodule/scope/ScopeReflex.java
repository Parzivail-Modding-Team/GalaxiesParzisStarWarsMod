package com.parzivail.swg.weapon.blastermodule.scope;

import com.parzivail.util.ui.Fx;
import com.parzivail.util.ui.gltk.TessCallback;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.GLUtessellator;

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

		// Type 1
		//		GL.Begin(PrimitiveType.LineLoop);
		//		for (int i = 360; i > 0; i--)
		//		{
		//			float p = (float)(i / 180f * Math.PI);
		//			if ((i >= 60 && i <= 120) || (i > 240 && i <= 300))
		//				GL.Vertex2(MathHelper.cos(p) * 80, MathHelper.sin(p) * 80);
		//			if (i > 120 && i <= 240)
		//				GL.Vertex2(MathHelper.cos(p) * 80 - 15, MathHelper.sin(p) * 80);
		//			if (i > 300 || i < 60)
		//				GL.Vertex2(MathHelper.cos(p) * 80 + 15, MathHelper.sin(p) * 80);
		//		}
		//		GL.End();

		// Type 2
		//		GL.Begin(PrimitiveType.Polygon);
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
		//		GL.End();

		// Tess demo
		double[] box = {
				-50, 0, 0, 0, 0, 0, 0, -50, 0, -50, -50, 0
		};

		double[] box2 = {
				-40, -10, 0, 0, 0, 0, -10, -40, 0, -40, -40, 0
		};

		GLUtessellator t = GLU.gluNewTess();
		t.gluTessCallback(GLU.GLU_TESS_BEGIN, TessCallback.INSTANCE);
		t.gluTessCallback(GLU.GLU_TESS_END, TessCallback.INSTANCE);
		t.gluTessCallback(GLU.GLU_TESS_VERTEX, TessCallback.INSTANCE);
		t.gluTessCallback(GLU.GLU_TESS_COMBINE, TessCallback.INSTANCE);
		t.gluTessCallback(GLU.GLU_TESS_ERROR, TessCallback.INSTANCE);

		t.gluTessBeginPolygon(null);

		t.gluTessBeginContour();
		t.gluTessVertex(box, 0, new double[] { box[0], box[1], box[2] });
		t.gluTessVertex(box, 3, new double[] { box[3], box[4], box[5] });
		t.gluTessVertex(box, 6, new double[] { box[6], box[7], box[8] });
		t.gluTessVertex(box, 9, new double[] { box[9], box[10], box[11] });
		t.gluTessEndContour();

		t.gluTessBeginContour();
		t.gluTessVertex(box2, 0, new double[] { box2[0], box2[1], box2[2] });
		t.gluTessVertex(box2, 3, new double[] { box2[3], box2[4], box2[5] });
		t.gluTessVertex(box2, 6, new double[] { box2[6], box2[7], box2[8] });
		t.gluTessVertex(box2, 9, new double[] { box2[9], box2[10], box2[11] });
		t.gluTessEndContour();

		t.gluTessEndPolygon();
	}

	@Override
	public float getZoomLevel()
	{
		return 1;
	}
}
