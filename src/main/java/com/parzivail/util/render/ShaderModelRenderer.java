package com.parzivail.util.render;

import com.parzivail.util.ui.GLPalette;
import com.parzivail.util.ui.ShaderHelper;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ShaderModelRenderer extends ModelRenderer
{
	private final int shaderId;
	public int color = GLPalette.WHITE;

	public ShaderModelRenderer(ModelBase model, int u, int v, int shaderId)
	{
		super(model, u, v);
		this.shaderId = shaderId;
	}

	@Override
	public void render(float p)
	{
		ShaderHelper.setColor(color);
		ShaderHelper.useShader(shaderId);
		super.render(p);
		ShaderHelper.releaseShader();

		GL.PushMatrix();
		GL.Translate(rotationPointX * p, rotationPointY * p, rotationPointZ * p);

		GL.Rotate(rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
		GL.Rotate(rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
		GL.Rotate(rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);

		if (childModels != null)
			for (Object childModel : childModels)
				((ModelRenderer)childModel).render(p);

		GL.PopMatrix();
	}
}
