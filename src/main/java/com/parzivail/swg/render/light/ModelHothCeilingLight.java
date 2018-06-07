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
public class ModelHothCeilingLight extends ModelBase
{
	public ModelRenderer shape1;
	public ModelRenderer Light;
	public ModelRenderer shape1_1;
	public ModelRenderer shape1_2;
	public ModelRenderer shape1_3;

	public ModelHothCeilingLight()
	{
		this.textureWidth = 512;
		this.textureHeight = 512;
		this.shape1_1 = new ModelRenderer(this, 39, 0);
		this.shape1_1.setRotationPoint(4.0F, 0.0F, 0.0F);
		this.shape1_1.addBox(-0.5F, -0.5F, -2.5F, 1, 1, 5, 0.0F);
		this.shape1 = new ModelRenderer(this, 0, 0);
		this.shape1.setRotationPoint(-2.0F, 8.5F, 0.0F);
		this.shape1.addBox(-0.5F, -0.5F, -2.5F, 1, 1, 5, 0.0F);
		this.Light = new ModelRenderer(this, 0, 73);
		this.Light.setRotationPoint(1.0F, 0.5F, 0.0F);
		this.Light.addBox(-2.5F, 8.0F, -1.5F, 3, 1, 3, 0.0F);
		this.shape1_3 = new ModelRenderer(this, 0, 38);
		this.shape1_3.setRotationPoint(1.0F, 0.0F, 0.0F);
		this.shape1_3.addBox(-0.5F, -0.5F, 1.5F, 3, 1, 1, 0.0F);
		this.shape1_2 = new ModelRenderer(this, 79, 0);
		this.shape1_2.setRotationPoint(1.0F, 0.0F, 0.0F);
		this.shape1_2.addBox(-0.5F, -0.5F, -2.5F, 3, 1, 1, 0.0F);
		this.shape1.addChild(this.shape1_1);
		this.shape1.addChild(this.shape1_3);
		this.shape1.addChild(this.shape1_2);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		this.shape1.render(f5);

		GL11.glPushMatrix();
		if (MathUtil.oneIn(50))
			ShaderHelper.setColor(1, 1, 1, 1);
		else
			ShaderHelper.setColor(0.9f, 0.9f, 1, 1);
		ShaderHelper.useShader(ShaderHelper.glowSolid);
		this.Light.render(f5);
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
