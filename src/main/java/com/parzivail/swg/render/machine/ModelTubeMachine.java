package com.parzivail.swg.render.machine;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelTubeMachine - parzi
 * Created using P-Tabula 4.1.1
 */
public class ModelTubeMachine extends ModelBase
{
	public ModelRenderer basePlate;
	public ModelRenderer fillet;
	public ModelRenderer pipe;
	public ModelRenderer pipe_1;
	public ModelRenderer pipe_2;
	public ModelRenderer pipe_3;
	public ModelRenderer pipe_4;
	public ModelRenderer pipe_5;
	public ModelRenderer pipe_6;
	public ModelRenderer pipe_7;
	public ModelRenderer body;
	public ModelRenderer body_1;
	public ModelRenderer interior;
	public ModelRenderer top;
	public ModelRenderer tube;
	public ModelRenderer tube_1;
	public ModelRenderer tube_2;
	public ModelRenderer tube_3;
	public ModelRenderer center;
	public ModelRenderer smallTube;
	public ModelRenderer shroud;
	public ModelRenderer smallTube_1;
	public ModelRenderer shroud_1;
	public ModelRenderer smallTube_2;
	public ModelRenderer shroud_2;
	public ModelRenderer smallTube_3;
	public ModelRenderer shroud_3;
	public ModelRenderer bend;
	public ModelRenderer bend_1;
	public ModelRenderer bend_2;
	public ModelRenderer bend_3;
	public ModelRenderer bend_4;
	public ModelRenderer bend_5;
	public ModelRenderer bend_6;
	public ModelRenderer pipe_8;
	public ModelRenderer bend_7;
	public ModelRenderer bend_8;
	public ModelRenderer bend_9;
	public ModelRenderer bend_10;
	public ModelRenderer pipe_9;
	public ModelRenderer bend_11;
	public ModelRenderer bend_12;

	public ModelTubeMachine()
	{
		textureWidth = 128;
		textureHeight = 128;
		pipe_1 = new ModelRenderer(this, 56, 89);
		pipe_1.setRotationPoint(-3.0F, -24.0F, 5.0F);
		pipe_1.addBox(0.0F, 0.0F, 0.0F, 2, 25, 2, 0.0F);
		center = new ModelRenderer(this, 56, 48);
		center.setRotationPoint(4.0F, -37.0F, 4.0F);
		center.addBox(0.0F, 0.0F, 0.0F, 2, 37, 2, 0.0F);
		smallTube = new ModelRenderer(this, 106, 48);
		smallTube.setRotationPoint(-2.5F, -10.0F, -2.5F);
		smallTube.addBox(0.0F, 0.0F, 0.0F, 1, 10, 1, 0.0F);
		shroud_2 = new ModelRenderer(this, 42, 83);
		shroud_2.setRotationPoint(0.0F, -6.0F, 0.0F);
		shroud_2.addBox(0.0F, 0.0F, 0.0F, 2, 6, 2, 0.0F);
		shroud_1 = new ModelRenderer(this, 42, 73);
		shroud_1.setRotationPoint(0.0F, -6.0F, 0.0F);
		shroud_1.addBox(0.0F, 0.0F, 0.0F, 2, 6, 2, 0.0F);
		bend_4 = new ModelRenderer(this, 100, 37);
		bend_4.setRotationPoint(0.0F, -2.0F, 0.0F);
		bend_4.addBox(0.0F, 0.0F, 0.0F, 6, 2, 2, 0.0F);
		pipe_5 = new ModelRenderer(this, 76, 88);
		pipe_5.setRotationPoint(6.0F, -17.0F, -2.0F);
		pipe_5.addBox(0.0F, 0.0F, 0.0F, 2, 18, 2, 0.0F);
		setRotateAngle(pipe_5, 0.0F, -1.5707963267948966F, 0.0F);
		pipe_7 = new ModelRenderer(this, 86, 81);
		pipe_7.setRotationPoint(19.0F, -31.0F, 7.5F);
		pipe_7.addBox(0.0F, 0.0F, 0.0F, 2, 32, 2, 0.0F);
		setRotateAngle(pipe_7, 0.0F, 3.141592653589793F, 0.0F);
		pipe_4 = new ModelRenderer(this, 76, 48);
		pipe_4.setRotationPoint(4.0F, -35.0F, 19.0F);
		pipe_4.addBox(0.0F, 0.0F, 0.0F, 2, 36, 2, 0.0F);
		setRotateAngle(pipe_4, 0.0F, 1.5707963267948966F, 0.0F);
		body_1 = new ModelRenderer(this, 50, 19);
		body_1.setRotationPoint(1.0F, -34.0F, 1.0F);
		body_1.addBox(0.0F, 0.0F, 0.0F, 12, 15, 12, 0.0F);
		smallTube_3 = new ModelRenderer(this, 106, 61);
		smallTube_3.setRotationPoint(-2.5F, -10.0F, -2.5F);
		smallTube_3.addBox(0.0F, 0.0F, 0.0F, 1, 10, 1, 0.0F);
		bend_2 = new ModelRenderer(this, 100, 25);
		bend_2.setRotationPoint(0.0F, -2.0F, 0.0F);
		bend_2.addBox(0.0F, 0.0F, 0.0F, 5, 2, 2, 0.0F);
		tube_1 = new ModelRenderer(this, 14, 102);
		tube_1.setRotationPoint(5.0F, -13.0F, 5.0F);
		tube_1.addBox(-4.5F, 0.0F, -4.5F, 3, 13, 3, 0.0F);
		setRotateAngle(tube_1, 0.0F, 1.5707963267948966F, 0.0F);
		bend_5 = new ModelRenderer(this, 0, 78);
		bend_5.setRotationPoint(0.0F, -2.0F, 0.0F);
		bend_5.addBox(0.0F, 0.0F, 0.0F, 6, 2, 2, 0.0F);
		basePlate = new ModelRenderer(this, 0, 0);
		basePlate.setRotationPoint(-8.0F, 23.0F, -8.0F);
		basePlate.addBox(0.0F, 0.0F, 0.0F, 16, 1, 16, 0.0F);
		bend_9 = new ModelRenderer(this, 0, 90);
		bend_9.setRotationPoint(0.0F, -2.0F, 0.0F);
		bend_9.addBox(0.0F, 0.0F, 0.0F, 5, 2, 2, 0.0F);
		smallTube_2 = new ModelRenderer(this, 118, 48);
		smallTube_2.setRotationPoint(-2.5F, -10.0F, -2.5F);
		smallTube_2.addBox(0.0F, 0.0F, 0.0F, 1, 10, 1, 0.0F);
		pipe_9 = new ModelRenderer(this, 96, 48);
		pipe_9.setRotationPoint(0.0F, 0.0F, -3.0F);
		pipe_9.addBox(0.0F, 0.0F, 0.0F, 2, 19, 2, 0.0F);
		pipe_2 = new ModelRenderer(this, 42, 103);
		pipe_2.setRotationPoint(10.0F, -35.0F, 18.0F);
		pipe_2.addBox(0.0F, 0.0F, 0.0F, 2, 16, 2, 0.0F);
		setRotateAngle(pipe_2, 0.0F, 1.5707963267948966F, 0.0F);
		tube = new ModelRenderer(this, 0, 102);
		tube.setRotationPoint(5.0F, -15.0F, 5.0F);
		tube.addBox(-4.5F, 0.0F, -4.5F, 3, 15, 3, 0.0F);
		pipe = new ModelRenderer(this, 66, 48);
		pipe.setRotationPoint(-4.0F, -35.0F, 10.0F);
		pipe.addBox(0.0F, 0.0F, 0.0F, 2, 36, 2, 0.0F);
		pipe_8 = new ModelRenderer(this, 76, 110);
		pipe_8.setRotationPoint(0.0F, 0.0F, -3.0F);
		pipe_8.addBox(0.0F, 0.0F, 0.0F, 2, 9, 2, 0.0F);
		pipe_3 = new ModelRenderer(this, 66, 88);
		pipe_3.setRotationPoint(7.0F, -35.0F, 19.0F);
		pipe_3.addBox(0.0F, 0.0F, 0.0F, 2, 36, 2, 0.0F);
		setRotateAngle(pipe_3, 0.0F, 1.5707963267948966F, 0.0F);
		tube_2 = new ModelRenderer(this, 28, 102);
		tube_2.setRotationPoint(5.0F, -17.0F, 5.0F);
		tube_2.addBox(-4.5F, 0.0F, -4.5F, 3, 17, 3, 0.0F);
		setRotateAngle(tube_2, 0.0F, 3.141592653589793F, 0.0F);
		fillet = new ModelRenderer(this, 66, 0);
		fillet.setRotationPoint(1.0F, -1.0F, 1.0F);
		fillet.addBox(0.0F, 0.0F, 0.0F, 14, 1, 14, 0.0F);
		bend = new ModelRenderer(this, 100, 19);
		bend.setRotationPoint(0.0F, -2.0F, 0.0F);
		bend.addBox(0.0F, 0.0F, 0.0F, 7, 2, 2, 0.0F);
		bend_11 = new ModelRenderer(this, 0, 96);
		bend_11.setRotationPoint(0.0F, -2.0F, 0.0F);
		bend_11.addBox(0.0F, 0.0F, 0.0F, 5, 2, 2, 0.0F);
		bend_8 = new ModelRenderer(this, 14, 120);
		bend_8.setRotationPoint(0.0F, 9.0F, 0.0F);
		bend_8.addBox(0.0F, 0.0F, 0.0F, 2, 2, 3, 0.0F);
		shroud_3 = new ModelRenderer(this, 42, 93);
		shroud_3.setRotationPoint(0.0F, -6.0F, 0.0F);
		shroud_3.addBox(0.0F, 0.0F, 0.0F, 2, 6, 2, 0.0F);
		bend_6 = new ModelRenderer(this, 0, 84);
		bend_6.setRotationPoint(0.0F, -2.0F, 0.0F);
		bend_6.addBox(0.0F, 0.0F, 0.0F, 5, 2, 2, 0.0F);
		bend_10 = new ModelRenderer(this, 16, 90);
		bend_10.setRotationPoint(0.0F, -2.0F, 0.0F);
		bend_10.addBox(0.0F, 0.0F, 0.0F, 5, 2, 2, 0.0F);
		bend_1 = new ModelRenderer(this, 18, 78);
		bend_1.setRotationPoint(0.0F, -2.0F, 0.0F);
		bend_1.addBox(0.0F, 0.0F, 0.0F, 5, 2, 2, 0.0F);
		interior = new ModelRenderer(this, 0, 48);
		interior.setRotationPoint(2.0F, -19.0F, 2.0F);
		interior.addBox(0.0F, 0.0F, 0.0F, 10, 4, 10, 0.0F);
		bend_12 = new ModelRenderer(this, 42, 66);
		bend_12.setRotationPoint(0.0F, 19.0F, 0.0F);
		bend_12.addBox(0.0F, 0.0F, 0.0F, 2, 2, 3, 0.0F);
		bend_3 = new ModelRenderer(this, 100, 31);
		bend_3.setRotationPoint(0.0F, 16.0F, 0.0F);
		bend_3.addBox(0.0F, 0.0F, 0.0F, 5, 2, 2, 0.0F);
		top = new ModelRenderer(this, 0, 64);
		top.setRotationPoint(2.0F, -36.0F, 2.0F);
		top.addBox(0.0F, 0.0F, 0.0F, 10, 2, 10, 0.0F);
		shroud = new ModelRenderer(this, 118, 37);
		shroud.setRotationPoint(0.0F, -6.0F, 0.0F);
		shroud.addBox(0.0F, 0.0F, 0.0F, 2, 6, 2, 0.0F);
		smallTube_1 = new ModelRenderer(this, 112, 48);
		smallTube_1.setRotationPoint(-2.5F, -10.0F, -2.5F);
		smallTube_1.addBox(0.0F, 0.0F, 0.0F, 1, 10, 1, 0.0F);
		tube_3 = new ModelRenderer(this, 42, 48);
		tube_3.setRotationPoint(5.0F, -13.0F, 5.0F);
		tube_3.addBox(-4.5F, 0.0F, -4.5F, 3, 13, 3, 0.0F);
		setRotateAngle(tube_3, 0.0F, -1.5707963267948966F, 0.0F);
		bend_7 = new ModelRenderer(this, 16, 84);
		bend_7.setRotationPoint(0.0F, -2.0F, 0.0F);
		bend_7.addBox(0.0F, 0.0F, 0.0F, 5, 2, 2, 0.0F);
		body = new ModelRenderer(this, 0, 19);
		body.setRotationPoint(1.0F, -15.0F, 1.0F);
		body.addBox(0.0F, 0.0F, 0.0F, 12, 15, 12, 0.0F);
		pipe_6 = new ModelRenderer(this, 86, 48);
		pipe_6.setRotationPoint(12.0F, -28.0F, -3.0F);
		pipe_6.addBox(0.0F, 0.0F, 0.0F, 2, 29, 2, 0.0F);
		setRotateAngle(pipe_6, 0.0F, -1.5707963267948966F, 0.0F);
		basePlate.addChild(pipe_1);
		top.addChild(center);
		tube.addChild(smallTube);
		smallTube_2.addChild(shroud_2);
		smallTube_1.addChild(shroud_1);
		pipe_3.addChild(bend_4);
		basePlate.addChild(pipe_5);
		basePlate.addChild(pipe_7);
		basePlate.addChild(pipe_4);
		fillet.addChild(body_1);
		tube_3.addChild(smallTube_3);
		pipe_2.addChild(bend_2);
		top.addChild(tube_1);
		pipe_4.addChild(bend_5);
		pipe_6.addChild(bend_9);
		tube_2.addChild(smallTube_2);
		pipe_7.addChild(pipe_9);
		basePlate.addChild(pipe_2);
		top.addChild(tube);
		basePlate.addChild(pipe);
		pipe_5.addChild(pipe_8);
		basePlate.addChild(pipe_3);
		top.addChild(tube_2);
		basePlate.addChild(fillet);
		pipe.addChild(bend);
		pipe_9.addChild(bend_11);
		pipe_8.addChild(bend_8);
		smallTube_3.addChild(shroud_3);
		pipe_5.addChild(bend_6);
		pipe_7.addChild(bend_10);
		pipe_1.addChild(bend_1);
		fillet.addChild(interior);
		pipe_9.addChild(bend_12);
		pipe_2.addChild(bend_3);
		fillet.addChild(top);
		smallTube.addChild(shroud);
		tube_1.addChild(smallTube_1);
		top.addChild(tube_3);
		pipe_8.addChild(bend_7);
		fillet.addChild(body);
		basePlate.addChild(pipe_6);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		basePlate.render(f5);
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
