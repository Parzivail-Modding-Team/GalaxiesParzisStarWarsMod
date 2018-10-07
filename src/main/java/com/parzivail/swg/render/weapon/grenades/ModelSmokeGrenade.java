package com.parzivail.swg.render.weapon.grenades;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

/**
 * grenade flash - PA3OP
 * Created using Tabula 4.1.1
 */
public class ModelSmokeGrenade extends ModelBase
{
	public ModelRenderer shape1;
	public ModelRenderer shape2;
	public ModelRenderer shape3;
	public ModelRenderer shape3_1;
	public ModelRenderer shape2_1;
	public ModelRenderer shape6;
	public ModelRenderer shape6_1;
	public ModelRenderer shape6_2;
	public ModelRenderer shape9;
	public ModelRenderer shape10;
	public ModelRenderer shape11;

	public ModelSmokeGrenade()
	{
		textureWidth = 64;
		textureHeight = 64;
		shape6_2 = new ModelRenderer(this, 13, 17);
		shape6_2.setRotationPoint(0.9F, 2.0F, 2.9F);
		shape6_2.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
		shape10 = new ModelRenderer(this, 5, 0);
		shape10.setRotationPoint(0.9F, 0.4F, -0.7F);
		shape10.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, -0.1F);
		shape3 = new ModelRenderer(this, 0, 8);
		shape3.setRotationPoint(0.9F, 2.0F, -0.15F);
		shape3.addBox(0.0F, 0.0F, 0.0F, 1, 4, 1, 0.0F);
		shape1 = new ModelRenderer(this, 0, 20);
		shape1.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1.addBox(0.0F, 0.0F, 0.0F, 3, 7, 3, 0.0F);
		shape6_1 = new ModelRenderer(this, 18, 21);
		shape6_1.setRotationPoint(1.6F, 2.0F, 2.9F);
		shape6_1.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
		shape9 = new ModelRenderer(this, 14, 13);
		shape9.setRotationPoint(0.4F, -0.1F, 0.4F);
		shape9.addBox(0.0F, 0.0F, 0.0F, 2, 1, 2, 0.0F);
		shape6 = new ModelRenderer(this, 13, 21);
		shape6.setRotationPoint(0.7F, 2.0F, 1.9F);
		shape6.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
		shape3_1 = new ModelRenderer(this, 5, 8);
		shape3_1.setRotationPoint(0.35F, 2.5F, 1.4F);
		shape3_1.addBox(-0.5F, -0.5F, 0.0F, 3, 4, 1, 0.0F);
		shape11 = new ModelRenderer(this, 0, 0);
		shape11.setRotationPoint(-0.7F, 0.4F, 0.85F);
		shape11.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, -0.1F);
		shape2 = new ModelRenderer(this, 0, 14);
		shape2.setRotationPoint(-0.15F, 0.0F, -0.15F);
		shape2.addBox(0.0F, 0.0F, 0.0F, 3, 2, 3, 0.0F);
		shape2_1 = new ModelRenderer(this, 13, 25);
		shape2_1.setRotationPoint(-0.15F, 5.9F, -0.15F);
		shape2_1.addBox(0.0F, 0.0F, 0.0F, 3, 2, 3, 0.0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef(shape6_2.offsetX, shape6_2.offsetY, shape6_2.offsetZ);
		GL11.glTranslatef(shape6_2.rotationPointX * f5, shape6_2.rotationPointY * f5, shape6_2.rotationPointZ * f5);
		GL11.glScaled(0.2D, 1.0D, 0.2D);
		GL11.glTranslatef(-shape6_2.offsetX, -shape6_2.offsetY, -shape6_2.offsetZ);
		GL11.glTranslatef(-shape6_2.rotationPointX * f5, -shape6_2.rotationPointY * f5, -shape6_2.rotationPointZ * f5);
		shape6_2.render(f5);
		GL11.glPopMatrix();
		shape10.render(f5);
		shape3.render(f5);
		GL11.glPushMatrix();
		GL11.glTranslatef(shape1.offsetX, shape1.offsetY, shape1.offsetZ);
		GL11.glTranslatef(shape1.rotationPointX * f5, shape1.rotationPointY * f5, shape1.rotationPointZ * f5);
		GL11.glScaled(0.9D, 1.0D, 0.9D);
		GL11.glTranslatef(-shape1.offsetX, -shape1.offsetY, -shape1.offsetZ);
		GL11.glTranslatef(-shape1.rotationPointX * f5, -shape1.rotationPointY * f5, -shape1.rotationPointZ * f5);
		shape1.render(f5);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glTranslatef(shape6_1.offsetX, shape6_1.offsetY, shape6_1.offsetZ);
		GL11.glTranslatef(shape6_1.rotationPointX * f5, shape6_1.rotationPointY * f5, shape6_1.rotationPointZ * f5);
		GL11.glScaled(0.2D, 1.0D, 0.2D);
		GL11.glTranslatef(-shape6_1.offsetX, -shape6_1.offsetY, -shape6_1.offsetZ);
		GL11.glTranslatef(-shape6_1.rotationPointX * f5, -shape6_1.rotationPointY * f5, -shape6_1.rotationPointZ * f5);
		shape6_1.render(f5);
		GL11.glPopMatrix();
		shape9.render(f5);
		GL11.glPushMatrix();
		GL11.glTranslatef(shape6.offsetX, shape6.offsetY, shape6.offsetZ);
		GL11.glTranslatef(shape6.rotationPointX * f5, shape6.rotationPointY * f5, shape6.rotationPointZ * f5);
		GL11.glScaled(1.3D, 1.0D, 1.0D);
		GL11.glTranslatef(-shape6.offsetX, -shape6.offsetY, -shape6.offsetZ);
		GL11.glTranslatef(-shape6.rotationPointX * f5, -shape6.rotationPointY * f5, -shape6.rotationPointZ * f5);
		shape6.render(f5);
		GL11.glPopMatrix();
		shape3_1.render(f5);
		shape11.render(f5);
		shape2.render(f5);
		shape2_1.render(f5);
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
