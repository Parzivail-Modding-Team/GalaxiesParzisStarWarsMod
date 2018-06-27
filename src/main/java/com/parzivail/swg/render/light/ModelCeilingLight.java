package com.parzivail.swg.render.light;

import com.parzivail.util.math.MathUtil;
import com.parzivail.util.ui.ShaderHelper;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

/**
 * HothCeilingLight - Weaston
 * Created using Tabula 4.1.1
 */
public class ModelCeilingLight extends ModelBase
{
	public ModelRenderer shape1;
	public ModelRenderer Light;
	public ModelRenderer shape1_1;
	public ModelRenderer shape1_2;
	public ModelRenderer shape1_3;

	public ModelCeilingLight()
	{
		textureWidth = 512;
		textureHeight = 512;
		shape1_1 = new ModelRenderer(this, 39, 0);
		shape1_1.setRotationPoint(4.0F, 0.0F, 0.0F);
		shape1_1.addBox(-0.5F, -0.5F, -2.5F, 1, 1, 5, 0.0F);
		shape1 = new ModelRenderer(this, 0, 0);
		shape1.setRotationPoint(-2.0F, 8.5F, 0.0F);
		shape1.addBox(-0.5F, -0.5F, -2.5F, 1, 1, 5, 0.0F);
		Light = new ModelRenderer(this, 0, 73);
		Light.setRotationPoint(1.0F, 0.5F, 0.0F);
		Light.addBox(-2.5F, 8.0F, -1.5F, 3, 1, 3, 0.0F);
		shape1_3 = new ModelRenderer(this, 0, 38);
		shape1_3.setRotationPoint(1.0F, 0.0F, 0.0F);
		shape1_3.addBox(-0.5F, -0.5F, 1.5F, 3, 1, 1, 0.0F);
		shape1_2 = new ModelRenderer(this, 79, 0);
		shape1_2.setRotationPoint(1.0F, 0.0F, 0.0F);
		shape1_2.addBox(-0.5F, -0.5F, -2.5F, 3, 1, 1, 0.0F);
		shape1.addChild(shape1_1);
		shape1.addChild(shape1_3);
		shape1.addChild(shape1_2);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		shape1.render(f5);

		GL11.glPushMatrix();
		if (MathUtil.oneIn(50))
			ShaderHelper.setColor(1, 1, 1, 1);
		else
			ShaderHelper.setColor(0.9f, 0.9f, 1, 1);
		ShaderHelper.useShader(ShaderHelper.glowSolid);
		Light.render(f5);
		ShaderHelper.releaseShader();
		GL11.glPopMatrix();
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z)
	{
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
