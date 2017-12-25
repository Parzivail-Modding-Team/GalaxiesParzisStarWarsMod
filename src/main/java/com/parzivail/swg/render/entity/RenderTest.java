package com.parzivail.swg.render.entity;

import com.parzivail.swg.Resources;
import com.parzivail.util.binary.SwgModel;
import com.parzivail.util.binary.SwgPart;
import com.parzivail.util.ui.Fx;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * Created by colby on 12/25/2017.
 */
public class RenderTest extends Render
{
	static SwgModel model;

	static
	{
		ResourceLocation r = new ResourceLocation(Resources.MODID, "models/test.swg3");
		model = SwgModel.Load(r);
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float unknown, float partialTicks)
	{
		int frame = 0;
		GL.Disable(EnableCap.CullFace);
		//GL.Disable(EnableCap.Texture2D);
		GL.PushMatrix();

		GL.Translate(x, y, z);
		GL.Rotate(-90, 1, 0, 0);

		float t = Fx.Util.HzPercent(1);

		for (SwgPart p : model.parts)
		{
			GL.PushMatrix();
			if (p.name.equals("x_wing01") || p.name.equals("x_wing04"))
			{
				GL.Translate(0, 0, 0.2f);
				GL.Rotate(-13 * t, 1, 0, 0);
				GL.Translate(0, 0, -0.2f);
			}
			if (p.name.equals("x_wing02") || p.name.equals("x_wing03"))
			{
				GL.Translate(0, 0, 0.2f);
				GL.Rotate(13 * t, 1, 0, 0);
				GL.Translate(0, 0, -0.2f);
			}
			bindTexture(p.textures[frame].texture);
			GL.Scale(0.0001f);
			GL.CallList(model.partRenderLists.get(p.name)[frame]);
			GL.PopMatrix();
		}

		GL.PopMatrix();
		//GL.Enable(EnableCap.Texture2D);
		GL.Enable(EnableCap.CullFace);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_)
	{
		return null;
	}
}
