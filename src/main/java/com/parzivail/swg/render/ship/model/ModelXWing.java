package com.parzivail.swg.render.ship.model;

import com.parzivail.swg.Resources;
import com.parzivail.swg.render.ship.IEntityRenderer;
import com.parzivail.util.binary.Swg3.SwgModel;
import com.parzivail.util.binary.Swg3.SwgPart;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class ModelXWing implements IEntityRenderer
{
	private static final SwgModel model;

	static
	{
		ResourceLocation r = new ResourceLocation(Resources.MODID, "models/test.swg3");
		model = SwgModel.Load(r);
	}

	@Override
	public void doRender(RenderManager renderManager, Entity entity, double viewX, double viewY, double viewZ, float partialTicks)
	{
		int frame = 0;

		for (SwgPart p : model.parts)
		{
			GL.PushMatrix();
			//			if (p.name.equals("x_wing01") || p.name.equals("x_wing04"))
			//			{
			//				GL.Translate(0, 0, data.verticalCenteringOffset - data.verticalGroundingOffset);
			//				GL.Rotate(-13, 1, 0, 0);
			//				GL.Translate(0, 0, -data.verticalCenteringOffset + data.verticalGroundingOffset);
			//			}
			//			if (p.name.equals("x_wing02") || p.name.equals("x_wing03"))
			//			{
			//				GL.Translate(0, 0, data.verticalCenteringOffset - data.verticalGroundingOffset);
			//				GL.Rotate(13, 1, 0, 0);
			//				GL.Translate(0, 0, -data.verticalCenteringOffset + data.verticalGroundingOffset);
			//			}
			renderManager.renderEngine.bindTexture(p.textures[frame].texture);
			GL.Scale(0.0004f);
			GL.CallList(model.partRenderLists.get(p.name)[frame]);
			GL.PopMatrix();
		}
	}
}
