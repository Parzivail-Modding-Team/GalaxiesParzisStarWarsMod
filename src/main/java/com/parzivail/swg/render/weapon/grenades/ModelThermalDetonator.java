package com.parzivail.swg.render.weapon.grenades;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

/**
 * Thermal detonator - PA3OP
 * Created using Tabula 4.1.1
 */
public class ModelThermalDetonator extends ModelBase
{
	public ModelRenderer shape1;
	public ModelRenderer shape2;
	public ModelRenderer shape3;

	public ModelThermalDetonator()
	{
		textureWidth = 64;
		textureHeight = 32;
		shape1 = new ModelRenderer(this, 7, 0);
		shape1.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1.addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3, 0.0F);
		shape3 = new ModelRenderer(this, 0, 0);
		shape3.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape3.addBox(-3.1F, -9.6F, -3.3F, 1, 1, 1, 0.0F);
		shape2 = new ModelRenderer(this, 0, 4);
		shape2.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape2.addBox(-1.0F, -3.5F, -0.5F, 2, 1, 1, 0.0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		shape1.render(f5);
		GL11.glPushMatrix();
		GL11.glTranslatef(shape3.offsetX, shape3.offsetY, shape3.offsetZ);
		GL11.glTranslatef(shape3.rotationPointX * f5, shape3.rotationPointY * f5, shape3.rotationPointZ * f5);
		GL11.glScaled(0.2D, 0.2D, 0.2D);
		GL11.glTranslatef(-shape3.offsetX, -shape3.offsetY, -shape3.offsetZ);
		GL11.glTranslatef(-shape3.rotationPointX * f5, -shape3.rotationPointY * f5, -shape3.rotationPointZ * f5);
		shape3.render(f5);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glTranslatef(shape2.offsetX, shape2.offsetY, shape2.offsetZ);
		GL11.glTranslatef(shape2.rotationPointX * f5, shape2.rotationPointY * f5, shape2.rotationPointZ * f5);
		GL11.glScaled(0.8D, 0.6D, 1.0D);
		GL11.glTranslatef(-shape2.offsetX, -shape2.offsetY, -shape2.offsetZ);
		GL11.glTranslatef(-shape2.rotationPointX * f5, -shape2.rotationPointY * f5, -shape2.rotationPointZ * f5);
		shape2.render(f5);
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
