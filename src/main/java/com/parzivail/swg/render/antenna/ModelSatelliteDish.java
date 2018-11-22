package com.parzivail.swg.render.antenna;

import com.parzivail.swg.tile.antenna.TileSatelliteDish;
import com.parzivail.util.entity.EntityTilePassthrough;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelSatelliteDish - Undefined
 * Created using P-Tabula 4.1.1
 */
public class ModelSatelliteDish extends ModelBase
{
	public ModelRenderer base;
	public ModelRenderer yawGimbal;
	public ModelRenderer armatureFront;
	public ModelRenderer armatureFront_1;
	public ModelRenderer pitchGimbal;
	public ModelRenderer armatureBack;
	public ModelRenderer armatureCap;
	public ModelRenderer armatureBack_1;
	public ModelRenderer armatureCap_1;
	public ModelRenderer dish;
	public ModelRenderer dishArm;
	public ModelRenderer dishArm_1;
	public ModelRenderer counterweightArm;
	public ModelRenderer counterweightArm_1;
	public ModelRenderer counterweight;
	public ModelRenderer antenna;
	public ModelRenderer antennaArm;
	public ModelRenderer border1;
	public ModelRenderer border1_1;
	public ModelRenderer border1_2;
	public ModelRenderer border1_3;

	public ModelSatelliteDish()
	{
		textureWidth = 256;
		textureHeight = 128;
		border1_1 = new ModelRenderer(this, 0, 96);
		border1_1.setRotationPoint(0.0F, 0.0F, 0.0F);
		border1_1.addBox(12.0F, -6.0F, -13.0F, 1, 1, 26, 0.0F);
		setRotateAngle(border1_1, 0.0F, 1.5707963267948966F, 0.0F);
		pitchGimbal = new ModelRenderer(this, 66, 43);
		pitchGimbal.setRotationPoint(0.0F, -15.25F, 0.0F);
		pitchGimbal.addBox(-5.5F, -0.5F, -0.5F, 11, 1, 1, 0.0F);
		armatureBack_1 = new ModelRenderer(this, 98, 87);
		armatureBack_1.setRotationPoint(0.0F, 0.0F, 0.5F);
		armatureBack_1.addBox(-0.5F, 0.0F, 0.0F, 1, 18, 1, 0.0F);
		setRotateAngle(armatureBack_1, 0.6632251157578453F, 0.0F, 0.0F);
		armatureFront = new ModelRenderer(this, 82, 107);
		armatureFront.setRotationPoint(-5.5F, -16.0F, -0.5F);
		armatureFront.addBox(-0.5F, 0.0F, -0.5F, 1, 18, 1, 0.0F);
		setRotateAngle(armatureFront, -0.33161255787892263F, 0.0F, 0.0F);
		armatureCap = new ModelRenderer(this, 88, 49);
		armatureCap.setRotationPoint(-1.0F, 0.0F, -0.5F);
		armatureCap.addBox(0.0F, -0.1F, 0.0F, 2, 2, 2, 0.0F);
		setRotateAngle(armatureCap, 0.33161255787892263F, 0.0F, 0.0F);
		dishArm = new ModelRenderer(this, 66, 27);
		dishArm.setRotationPoint(0.0F, 0.0F, 0.0F);
		dishArm.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 13, 0.0F);
		setRotateAngle(dishArm, 0.3665191429188092F, 0.0F, 0.0F);
		border1 = new ModelRenderer(this, 0, 67);
		border1.setRotationPoint(0.0F, 0.0F, 0.0F);
		border1.addBox(12.0F, -6.0F, -13.0F, 1, 1, 26, 0.0F);
		dish = new ModelRenderer(this, 0, 0);
		dish.setRotationPoint(0.0F, 0.0F, 0.0F);
		dish.addBox(-12.0F, -5.0F, -12.0F, 24, 1, 24, 0.0F);
		base = new ModelRenderer(this, 0, 27);
		base.setRotationPoint(-8.0F, 20.0F, -8.0F);
		base.addBox(0.0F, 0.0F, 0.0F, 16, 4, 16, 0.0F);
		counterweightArm = new ModelRenderer(this, 56, 67);
		counterweightArm.setRotationPoint(0.0F, 0.0F, 0.0F);
		counterweightArm.addBox(-0.5F, 5.5F, -6.5F, 1, 1, 17, 0.0F);
		setRotateAngle(counterweightArm, 0.890117918517108F, 0.0F, 0.0F);
		yawGimbal = new ModelRenderer(this, 0, 49);
		yawGimbal.setRotationPoint(8.0F, -2.0F, 8.0F);
		yawGimbal.addBox(-7.0F, 0.0F, -7.0F, 14, 2, 14, 0.0F);
		counterweightArm_1 = new ModelRenderer(this, 56, 87);
		counterweightArm_1.setRotationPoint(0.0F, 0.0F, 0.0F);
		counterweightArm_1.addBox(-0.5F, -6.5F, -6.5F, 1, 1, 17, 0.0F);
		setRotateAngle(counterweightArm_1, 2.251474735072685F, 0.0F, 0.0F);
		antennaArm = new ModelRenderer(this, 104, 66);
		antennaArm.setRotationPoint(0.0F, -23.5F, 2.0F);
		antennaArm.addBox(-0.5F, 0.0F, -0.5F, 1, 21, 1, 0.0F);
		setRotateAngle(antennaArm, 0.45378560551852565F, 0.0F, 0.0F);
		border1_3 = new ModelRenderer(this, 98, 29);
		border1_3.setRotationPoint(0.0F, 0.0F, 0.0F);
		border1_3.addBox(12.0F, -6.0F, -13.0F, 1, 1, 26, 0.0F);
		setRotateAngle(border1_3, 0.0F, -1.5707963267948966F, 0.0F);
		armatureBack = new ModelRenderer(this, 88, 107);
		armatureBack.setRotationPoint(0.0F, 0.0F, 0.5F);
		armatureBack.addBox(-0.5F, 0.0F, 0.0F, 1, 18, 1, 0.0F);
		setRotateAngle(armatureBack, 0.6632251157578453F, 0.0F, 0.0F);
		border1_2 = new ModelRenderer(this, 98, 0);
		border1_2.setRotationPoint(0.0F, 0.0F, 0.0F);
		border1_2.addBox(12.0F, -6.0F, -13.0F, 1, 1, 26, 0.0F);
		setRotateAngle(border1_2, 0.0F, 3.141592653589793F, 0.0F);
		antenna = new ModelRenderer(this, 98, 58);
		antenna.setRotationPoint(-2.0F, -10.0F, -2.0F);
		antenna.addBox(-0.5F, -14.0F, -0.5F, 5, 1, 5, 0.0F);
		armatureCap_1 = new ModelRenderer(this, 88, 55);
		armatureCap_1.setRotationPoint(-1.0F, 0.0F, -0.5F);
		armatureCap_1.addBox(0.0F, -0.1F, 0.0F, 2, 2, 2, 0.0F);
		setRotateAngle(armatureCap_1, 0.33161255787892263F, 0.0F, 0.0F);
		armatureFront_1 = new ModelRenderer(this, 98, 66);
		armatureFront_1.setRotationPoint(5.5F, -16.0F, -0.5F);
		armatureFront_1.addBox(-0.5F, 0.0F, -0.5F, 1, 18, 1, 0.0F);
		setRotateAngle(armatureFront_1, -0.33161255787892263F, 0.0F, 0.0F);
		counterweight = new ModelRenderer(this, 56, 107);
		counterweight.setRotationPoint(0.0F, 0.0F, 0.0F);
		counterweight.addBox(-2.5F, 5.0F, -3.5F, 5, 6, 7, 0.0F);
		dishArm_1 = new ModelRenderer(this, 58, 49);
		dishArm_1.setRotationPoint(0.0F, 0.0F, 0.0F);
		dishArm_1.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 13, 0.0F);
		setRotateAngle(dishArm_1, 2.7750735106709836F, 0.0F, 0.0F);
		dish.addChild(border1_1);
		yawGimbal.addChild(pitchGimbal);
		armatureFront_1.addChild(armatureBack_1);
		yawGimbal.addChild(armatureFront);
		armatureFront.addChild(armatureCap);
		pitchGimbal.addChild(dishArm);
		dish.addChild(border1);
		pitchGimbal.addChild(dish);
		pitchGimbal.addChild(counterweightArm);
		base.addChild(yawGimbal);
		pitchGimbal.addChild(counterweightArm_1);
		dish.addChild(antennaArm);
		dish.addChild(border1_3);
		armatureFront.addChild(armatureBack);
		dish.addChild(border1_2);
		dish.addChild(antenna);
		armatureFront_1.addChild(armatureCap_1);
		yawGimbal.addChild(armatureFront_1);
		pitchGimbal.addChild(counterweight);
		pitchGimbal.addChild(dishArm_1);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		if (entity instanceof EntityTilePassthrough && ((EntityTilePassthrough)entity).tileEntity instanceof TileSatelliteDish)
		{
			TileSatelliteDish dish = (TileSatelliteDish)((EntityTilePassthrough)entity).tileEntity;

			pitchGimbal.rotateAngleX = (float)(dish.getPitch() / 2 * Math.PI);

			yawGimbal.rotateAngleY = (float)((90 * dish.getFacing() + 180) / 180f * Math.PI);
		}
		else
		{
			pitchGimbal.rotateAngleX = 0;
			yawGimbal.rotateAngleY = 0;
		}
		base.render(f5);
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
