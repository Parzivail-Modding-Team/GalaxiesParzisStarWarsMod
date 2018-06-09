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
		this.textureWidth = 128;
		this.textureHeight = 128;
		this.pipe_1 = new ModelRenderer(this, 56, 89);
		this.pipe_1.setRotationPoint(-3.0F, -24.0F, 5.0F);
		this.pipe_1.addBox(0.0F, 0.0F, 0.0F, 2, 25, 2, 0.0F);
		this.center = new ModelRenderer(this, 56, 48);
		this.center.setRotationPoint(4.0F, -37.0F, 4.0F);
		this.center.addBox(0.0F, 0.0F, 0.0F, 2, 37, 2, 0.0F);
		this.smallTube = new ModelRenderer(this, 106, 48);
		this.smallTube.setRotationPoint(-2.5F, -10.0F, -2.5F);
		this.smallTube.addBox(0.0F, 0.0F, 0.0F, 1, 10, 1, 0.0F);
		this.shroud_2 = new ModelRenderer(this, 42, 83);
		this.shroud_2.setRotationPoint(0.0F, -6.0F, 0.0F);
		this.shroud_2.addBox(0.0F, 0.0F, 0.0F, 2, 6, 2, 0.0F);
		this.shroud_1 = new ModelRenderer(this, 42, 73);
		this.shroud_1.setRotationPoint(0.0F, -6.0F, 0.0F);
		this.shroud_1.addBox(0.0F, 0.0F, 0.0F, 2, 6, 2, 0.0F);
		this.bend_4 = new ModelRenderer(this, 100, 37);
		this.bend_4.setRotationPoint(0.0F, -2.0F, 0.0F);
		this.bend_4.addBox(0.0F, 0.0F, 0.0F, 6, 2, 2, 0.0F);
		this.pipe_5 = new ModelRenderer(this, 76, 88);
		this.pipe_5.setRotationPoint(6.0F, -17.0F, -2.0F);
		this.pipe_5.addBox(0.0F, 0.0F, 0.0F, 2, 18, 2, 0.0F);
		this.setRotateAngle(pipe_5, 0.0F, -1.5707963267948966F, 0.0F);
		this.pipe_7 = new ModelRenderer(this, 86, 81);
		this.pipe_7.setRotationPoint(19.0F, -31.0F, 7.5F);
		this.pipe_7.addBox(0.0F, 0.0F, 0.0F, 2, 32, 2, 0.0F);
		this.setRotateAngle(pipe_7, 0.0F, 3.141592653589793F, 0.0F);
		this.pipe_4 = new ModelRenderer(this, 76, 48);
		this.pipe_4.setRotationPoint(4.0F, -35.0F, 19.0F);
		this.pipe_4.addBox(0.0F, 0.0F, 0.0F, 2, 36, 2, 0.0F);
		this.setRotateAngle(pipe_4, 0.0F, 1.5707963267948966F, 0.0F);
		this.body_1 = new ModelRenderer(this, 50, 19);
		this.body_1.setRotationPoint(1.0F, -34.0F, 1.0F);
		this.body_1.addBox(0.0F, 0.0F, 0.0F, 12, 15, 12, 0.0F);
		this.smallTube_3 = new ModelRenderer(this, 106, 61);
		this.smallTube_3.setRotationPoint(-2.5F, -10.0F, -2.5F);
		this.smallTube_3.addBox(0.0F, 0.0F, 0.0F, 1, 10, 1, 0.0F);
		this.bend_2 = new ModelRenderer(this, 100, 25);
		this.bend_2.setRotationPoint(0.0F, -2.0F, 0.0F);
		this.bend_2.addBox(0.0F, 0.0F, 0.0F, 5, 2, 2, 0.0F);
		this.tube_1 = new ModelRenderer(this, 14, 102);
		this.tube_1.setRotationPoint(5.0F, -13.0F, 5.0F);
		this.tube_1.addBox(-4.5F, 0.0F, -4.5F, 3, 13, 3, 0.0F);
		this.setRotateAngle(tube_1, 0.0F, 1.5707963267948966F, 0.0F);
		this.bend_5 = new ModelRenderer(this, 0, 78);
		this.bend_5.setRotationPoint(0.0F, -2.0F, 0.0F);
		this.bend_5.addBox(0.0F, 0.0F, 0.0F, 6, 2, 2, 0.0F);
		this.basePlate = new ModelRenderer(this, 0, 0);
		this.basePlate.setRotationPoint(-8.0F, 23.0F, -8.0F);
		this.basePlate.addBox(0.0F, 0.0F, 0.0F, 16, 1, 16, 0.0F);
		this.bend_9 = new ModelRenderer(this, 0, 90);
		this.bend_9.setRotationPoint(0.0F, -2.0F, 0.0F);
		this.bend_9.addBox(0.0F, 0.0F, 0.0F, 5, 2, 2, 0.0F);
		this.smallTube_2 = new ModelRenderer(this, 118, 48);
		this.smallTube_2.setRotationPoint(-2.5F, -10.0F, -2.5F);
		this.smallTube_2.addBox(0.0F, 0.0F, 0.0F, 1, 10, 1, 0.0F);
		this.pipe_9 = new ModelRenderer(this, 96, 48);
		this.pipe_9.setRotationPoint(0.0F, 0.0F, -3.0F);
		this.pipe_9.addBox(0.0F, 0.0F, 0.0F, 2, 19, 2, 0.0F);
		this.pipe_2 = new ModelRenderer(this, 42, 103);
		this.pipe_2.setRotationPoint(10.0F, -35.0F, 18.0F);
		this.pipe_2.addBox(0.0F, 0.0F, 0.0F, 2, 16, 2, 0.0F);
		this.setRotateAngle(pipe_2, 0.0F, 1.5707963267948966F, 0.0F);
		this.tube = new ModelRenderer(this, 0, 102);
		this.tube.setRotationPoint(5.0F, -15.0F, 5.0F);
		this.tube.addBox(-4.5F, 0.0F, -4.5F, 3, 15, 3, 0.0F);
		this.pipe = new ModelRenderer(this, 66, 48);
		this.pipe.setRotationPoint(-4.0F, -35.0F, 10.0F);
		this.pipe.addBox(0.0F, 0.0F, 0.0F, 2, 36, 2, 0.0F);
		this.pipe_8 = new ModelRenderer(this, 76, 110);
		this.pipe_8.setRotationPoint(0.0F, 0.0F, -3.0F);
		this.pipe_8.addBox(0.0F, 0.0F, 0.0F, 2, 9, 2, 0.0F);
		this.pipe_3 = new ModelRenderer(this, 66, 88);
		this.pipe_3.setRotationPoint(7.0F, -35.0F, 19.0F);
		this.pipe_3.addBox(0.0F, 0.0F, 0.0F, 2, 36, 2, 0.0F);
		this.setRotateAngle(pipe_3, 0.0F, 1.5707963267948966F, 0.0F);
		this.tube_2 = new ModelRenderer(this, 28, 102);
		this.tube_2.setRotationPoint(5.0F, -17.0F, 5.0F);
		this.tube_2.addBox(-4.5F, 0.0F, -4.5F, 3, 17, 3, 0.0F);
		this.setRotateAngle(tube_2, 0.0F, 3.141592653589793F, 0.0F);
		this.fillet = new ModelRenderer(this, 66, 0);
		this.fillet.setRotationPoint(1.0F, -1.0F, 1.0F);
		this.fillet.addBox(0.0F, 0.0F, 0.0F, 14, 1, 14, 0.0F);
		this.bend = new ModelRenderer(this, 100, 19);
		this.bend.setRotationPoint(0.0F, -2.0F, 0.0F);
		this.bend.addBox(0.0F, 0.0F, 0.0F, 7, 2, 2, 0.0F);
		this.bend_11 = new ModelRenderer(this, 0, 96);
		this.bend_11.setRotationPoint(0.0F, -2.0F, 0.0F);
		this.bend_11.addBox(0.0F, 0.0F, 0.0F, 5, 2, 2, 0.0F);
		this.bend_8 = new ModelRenderer(this, 14, 120);
		this.bend_8.setRotationPoint(0.0F, 9.0F, 0.0F);
		this.bend_8.addBox(0.0F, 0.0F, 0.0F, 2, 2, 3, 0.0F);
		this.shroud_3 = new ModelRenderer(this, 42, 93);
		this.shroud_3.setRotationPoint(0.0F, -6.0F, 0.0F);
		this.shroud_3.addBox(0.0F, 0.0F, 0.0F, 2, 6, 2, 0.0F);
		this.bend_6 = new ModelRenderer(this, 0, 84);
		this.bend_6.setRotationPoint(0.0F, -2.0F, 0.0F);
		this.bend_6.addBox(0.0F, 0.0F, 0.0F, 5, 2, 2, 0.0F);
		this.bend_10 = new ModelRenderer(this, 16, 90);
		this.bend_10.setRotationPoint(0.0F, -2.0F, 0.0F);
		this.bend_10.addBox(0.0F, 0.0F, 0.0F, 5, 2, 2, 0.0F);
		this.bend_1 = new ModelRenderer(this, 18, 78);
		this.bend_1.setRotationPoint(0.0F, -2.0F, 0.0F);
		this.bend_1.addBox(0.0F, 0.0F, 0.0F, 5, 2, 2, 0.0F);
		this.interior = new ModelRenderer(this, 0, 48);
		this.interior.setRotationPoint(2.0F, -19.0F, 2.0F);
		this.interior.addBox(0.0F, 0.0F, 0.0F, 10, 4, 10, 0.0F);
		this.bend_12 = new ModelRenderer(this, 42, 66);
		this.bend_12.setRotationPoint(0.0F, 19.0F, 0.0F);
		this.bend_12.addBox(0.0F, 0.0F, 0.0F, 2, 2, 3, 0.0F);
		this.bend_3 = new ModelRenderer(this, 100, 31);
		this.bend_3.setRotationPoint(0.0F, 16.0F, 0.0F);
		this.bend_3.addBox(0.0F, 0.0F, 0.0F, 5, 2, 2, 0.0F);
		this.top = new ModelRenderer(this, 0, 64);
		this.top.setRotationPoint(2.0F, -36.0F, 2.0F);
		this.top.addBox(0.0F, 0.0F, 0.0F, 10, 2, 10, 0.0F);
		this.shroud = new ModelRenderer(this, 118, 37);
		this.shroud.setRotationPoint(0.0F, -6.0F, 0.0F);
		this.shroud.addBox(0.0F, 0.0F, 0.0F, 2, 6, 2, 0.0F);
		this.smallTube_1 = new ModelRenderer(this, 112, 48);
		this.smallTube_1.setRotationPoint(-2.5F, -10.0F, -2.5F);
		this.smallTube_1.addBox(0.0F, 0.0F, 0.0F, 1, 10, 1, 0.0F);
		this.tube_3 = new ModelRenderer(this, 42, 48);
		this.tube_3.setRotationPoint(5.0F, -13.0F, 5.0F);
		this.tube_3.addBox(-4.5F, 0.0F, -4.5F, 3, 13, 3, 0.0F);
		this.setRotateAngle(tube_3, 0.0F, -1.5707963267948966F, 0.0F);
		this.bend_7 = new ModelRenderer(this, 16, 84);
		this.bend_7.setRotationPoint(0.0F, -2.0F, 0.0F);
		this.bend_7.addBox(0.0F, 0.0F, 0.0F, 5, 2, 2, 0.0F);
		this.body = new ModelRenderer(this, 0, 19);
		this.body.setRotationPoint(1.0F, -15.0F, 1.0F);
		this.body.addBox(0.0F, 0.0F, 0.0F, 12, 15, 12, 0.0F);
		this.pipe_6 = new ModelRenderer(this, 86, 48);
		this.pipe_6.setRotationPoint(12.0F, -28.0F, -3.0F);
		this.pipe_6.addBox(0.0F, 0.0F, 0.0F, 2, 29, 2, 0.0F);
		this.setRotateAngle(pipe_6, 0.0F, -1.5707963267948966F, 0.0F);
		this.basePlate.addChild(this.pipe_1);
		this.top.addChild(this.center);
		this.tube.addChild(this.smallTube);
		this.smallTube_2.addChild(this.shroud_2);
		this.smallTube_1.addChild(this.shroud_1);
		this.pipe_3.addChild(this.bend_4);
		this.basePlate.addChild(this.pipe_5);
		this.basePlate.addChild(this.pipe_7);
		this.basePlate.addChild(this.pipe_4);
		this.fillet.addChild(this.body_1);
		this.tube_3.addChild(this.smallTube_3);
		this.pipe_2.addChild(this.bend_2);
		this.top.addChild(this.tube_1);
		this.pipe_4.addChild(this.bend_5);
		this.pipe_6.addChild(this.bend_9);
		this.tube_2.addChild(this.smallTube_2);
		this.pipe_7.addChild(this.pipe_9);
		this.basePlate.addChild(this.pipe_2);
		this.top.addChild(this.tube);
		this.basePlate.addChild(this.pipe);
		this.pipe_5.addChild(this.pipe_8);
		this.basePlate.addChild(this.pipe_3);
		this.top.addChild(this.tube_2);
		this.basePlate.addChild(this.fillet);
		this.pipe.addChild(this.bend);
		this.pipe_9.addChild(this.bend_11);
		this.pipe_8.addChild(this.bend_8);
		this.smallTube_3.addChild(this.shroud_3);
		this.pipe_5.addChild(this.bend_6);
		this.pipe_7.addChild(this.bend_10);
		this.pipe_1.addChild(this.bend_1);
		this.fillet.addChild(this.interior);
		this.pipe_9.addChild(this.bend_12);
		this.pipe_2.addChild(this.bend_3);
		this.fillet.addChild(this.top);
		this.smallTube.addChild(this.shroud);
		this.tube_1.addChild(this.smallTube_1);
		this.top.addChild(this.tube_3);
		this.pipe_8.addChild(this.bend_7);
		this.fillet.addChild(this.body);
		this.basePlate.addChild(this.pipe_6);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		this.basePlate.render(f5);
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
