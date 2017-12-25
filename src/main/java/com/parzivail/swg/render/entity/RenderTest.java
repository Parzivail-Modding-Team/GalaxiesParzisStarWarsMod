package com.parzivail.swg.render.entity;

import com.parzivail.swg.Resources;
import com.parzivail.util.binary.SwgModel;
import com.parzivail.util.binary.SwgPart;
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
		GL.Scale(0.0001f);

		for (SwgPart p : model.parts)
		{
			bindTexture(p.textures[frame].texture);
			GL.CallList(model.partRenderLists.get(p.name)[frame]);
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
