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
		ShaderHelper.setColor(this.color);
		ShaderHelper.useShader(shaderId);
		super.render(p);
		ShaderHelper.releaseShader();

		GL.PushMatrix();
		GL.Translate(this.rotationPointX * p, this.rotationPointY * p, this.rotationPointZ * p);

		GL.Rotate(this.rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
		GL.Rotate(this.rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
		GL.Rotate(this.rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);

		if (this.childModels != null)
			for (Object childModel : this.childModels)
				((ModelRenderer)childModel).render(p);

		GL.PopMatrix();
	}
}
