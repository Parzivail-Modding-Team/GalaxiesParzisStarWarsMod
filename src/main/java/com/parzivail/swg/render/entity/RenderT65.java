package com.parzivail.swg.render.entity;

import com.parzivail.swg.Resources;
import com.parzivail.swg.ship.BasicFlightModel;
import com.parzivail.util.binary.SwgModel;
import com.parzivail.util.binary.SwgPart;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.util.ResourceLocation;

/**
 * Created by colby on 12/25/2017.
 */
public class RenderT65 extends RenderBasicFlightModel
{
	private static SwgModel model;

	static
	{
		ResourceLocation r = new ResourceLocation(Resources.MODID, "models/test.swg3");
		model = SwgModel.Load(r);
	}

	public RenderT65()
	{
		super(1.684f);
	}

	@Override
	public void doRender(BasicFlightModel ship, float partialTicks)
	{
		int frame = 0;
		GL.Translate(0, 0, 0.82f);

		for (SwgPart p : model.parts)
		{
			GL.PushMatrix();
			//			if (p.name.equals("x_wing01") || p.name.equals("x_wing04"))
			//			{
			//				GL.Translate(0, 0, 0.2f);
			//				GL.Rotate(-13 * t, 1, 0, 0);
			//				GL.Translate(0, 0, -0.2f);
			//			}
			//			if (p.name.equals("x_wing02") || p.name.equals("x_wing03"))
			//			{
			//				GL.Translate(0, 0, 0.2f);
			//				GL.Rotate(13 * t, 1, 0, 0);
			//				GL.Translate(0, 0, -0.2f);
			//			}
			bindTexture(p.textures[frame].texture);
			GL.Scale(0.0004f);
			GL.CallList(model.partRenderLists.get(p.name)[frame]);
			GL.PopMatrix();
		}

		//Fx.D3.DrawLine(-10, 1.5f, y, 10, 1.5f, y);
	}
}
