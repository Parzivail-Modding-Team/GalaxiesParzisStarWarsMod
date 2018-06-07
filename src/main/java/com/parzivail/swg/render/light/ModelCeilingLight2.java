package com.parzivail.swg.render.light;

import com.parzivail.util.math.MathUtil;
import com.parzivail.util.ui.ShaderHelper;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

/**
 * HothCeilingLight2 - Weaston
 * Created using P-Tabula 4.1.1
 */
public class ModelCeilingLight2 extends ModelBase
{
	public ModelRenderer shape1;
	public ModelRenderer Light1;
	public ModelRenderer Light2;
	public ModelRenderer Light3;
	public ModelRenderer Light4;
	public ModelRenderer shape2;
	public ModelRenderer shape3;
	public ModelRenderer shape4;
	public ModelRenderer shape5;
	public ModelRenderer shape6;
	public ModelRenderer shape7;

	public ModelCeilingLight2()
	{
		this.textureWidth = 512;
		this.textureHeight = 512;
		this.shape3 = new ModelRenderer(this, 0, 63);
		this.shape3.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape3.addBox(4.0F, 1.0F, -12.0F, 2, 6, 24, 0.0F);
		this.shape2 = new ModelRenderer(this, 0, 31);
		this.shape2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape2.addBox(-6.0F, 1.0F, -12.0F, 2, 6, 24, 0.0F);
		this.Light3 = new ModelRenderer(this, 254, 0);
		this.Light3.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.Light3.addBox(-2.5F, 9.5F, -5.5F, 5, 6, 5, 0.0F);
		this.Light4 = new ModelRenderer(this, 254, 13);
		this.Light4.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.Light4.addBox(-2.5F, 9.5F, 0.5F, 5, 6, 5, 0.0F);
		this.Light2 = new ModelRenderer(this, 232, 13);
		this.Light2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.Light2.addBox(-2.5F, 9.5F, -11.5F, 5, 6, 5, 0.0F);
		this.shape6 = new ModelRenderer(this, 82, 0);
		this.shape6.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape6.addBox(-5.0F, -1.3F, -12.0F, 10, 2, 24, 0.0F);
		this.shape7 = new ModelRenderer(this, 152, 0);
		this.shape7.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape7.addBox(-3.0F, -2.3F, -9.0F, 6, 2, 18, 0.0F);
		this.shape1 = new ModelRenderer(this, 0, 0);
		this.shape1.setRotationPoint(0.0F, 8.0F, 0.0F);
		this.shape1.addBox(-6.0F, 0.0F, -14.0F, 12, 1, 28, 0.0F);
		this.shape4 = new ModelRenderer(this, 202, 0);
		this.shape4.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape4.addBox(-6.0F, 1.0F, -14.0F, 12, 6, 2, 0.0F);
		this.Light1 = new ModelRenderer(this, 232, 0);
		this.Light1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.Light1.addBox(-2.5F, 9.5F, 6.5F, 5, 6, 5, 0.0F);
		this.shape5 = new ModelRenderer(this, 202, 10);
		this.shape5.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape5.addBox(-6.0F, 1.0F, 12.0F, 12, 6, 2, 0.0F);
		this.shape1.addChild(this.shape3);
		this.shape1.addChild(this.shape2);
		this.shape1.addChild(this.shape6);
		this.shape1.addChild(this.shape7);
		this.shape1.addChild(this.shape4);
		this.shape1.addChild(this.shape5);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		this.shape1.render(f5);
		GL11.glPushMatrix();
		if (MathUtil.oneIn(250))
			ShaderHelper.setColor(1, 1, 1, 1);
		else
			ShaderHelper.setColor(0.9f, 0.9f, 1, 1);
		ShaderHelper.useShader(ShaderHelper.glowSolid);
		this.Light1.render(f5);
		ShaderHelper.releaseShader();
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		if (MathUtil.oneIn(250))
			ShaderHelper.setColor(1, 1, 1, 1);
		else
			ShaderHelper.setColor(0.9f, 0.9f, 1, 1);
		ShaderHelper.useShader(ShaderHelper.glowSolid);
		this.Light2.render(f5);
		ShaderHelper.releaseShader();
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		if (MathUtil.oneIn(250))
			ShaderHelper.setColor(1, 1, 1, 1);
		else
			ShaderHelper.setColor(0.9f, 0.9f, 1, 1);
		ShaderHelper.useShader(ShaderHelper.glowSolid);
		this.Light3.render(f5);
		ShaderHelper.releaseShader();
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		if (MathUtil.oneIn(250))
			ShaderHelper.setColor(1, 1, 1, 1);
		else
			ShaderHelper.setColor(0.9f, 0.9f, 1, 1);
		ShaderHelper.useShader(ShaderHelper.glowSolid);
		this.Light4.render(f5);
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
