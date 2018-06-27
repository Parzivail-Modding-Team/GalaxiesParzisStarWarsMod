package com.parzivail.swg.render.machine;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelSpokedMachine - parzi
 * Created using P-Tabula 4.1.1
 */
public class ModelSpokedMachine extends ModelBase
{
	public ModelRenderer basePlate;
	public ModelRenderer baseShaft;
	public ModelRenderer spoke;
	public ModelRenderer angleSpoke;
	public ModelRenderer centerShaft;
	public ModelRenderer glandEnd;
	public ModelRenderer spoke_1;
	public ModelRenderer spoke_2;
	public ModelRenderer spoke_3;
	public ModelRenderer angleSpoke_1;
	public ModelRenderer angleSpoke_2;
	public ModelRenderer angleSpoke_3;
	public ModelRenderer knobs;
	public ModelRenderer knobs_1;
	public ModelRenderer ring;
	public ModelRenderer ring_1;
	public ModelRenderer ring_2;
	public ModelRenderer ringSupport;
	public ModelRenderer ring_3;
	public ModelRenderer ringSupport_1;
	public ModelRenderer ringSupport_2;
	public ModelRenderer ringSupport_3;

	public ModelSpokedMachine()
	{
		textureWidth = 128;
		textureHeight = 128;
		spoke_2 = new ModelRenderer(this, 14, 102);
		spoke_2.setRotationPoint(8.0F, -5.9F, 8.0F);
		spoke_2.addBox(-0.5F, 0.0F, -9.9F, 1, 3, 4, 0.0F);
		setRotateAngle(spoke_2, 0.6373942428283291F, 3.141592653589793F, 0.0F);
		angleSpoke_1 = new ModelRenderer(this, 26, 71);
		angleSpoke_1.setRotationPoint(8.0F, -5.9F, 8.0F);
		angleSpoke_1.addBox(-0.5F, -1.0F, -12.3F, 1, 3, 5, 0.0F);
		setRotateAngle(angleSpoke_1, 0.6373942428283291F, 2.356194490192345F, 0.0F);
		ring = new ModelRenderer(this, 0, 19);
		ring.setRotationPoint(-0.5F, 0.5F, -0.5F);
		ring.addBox(0.0F, 0.0F, 0.0F, 10, 2, 10, 0.0F);
		angleSpoke_3 = new ModelRenderer(this, 0, 102);
		angleSpoke_3.setRotationPoint(8.0F, -5.9F, 8.0F);
		angleSpoke_3.addBox(-0.5F, -1.0F, -12.3F, 1, 3, 5, 0.0F);
		setRotateAngle(angleSpoke_3, 0.6373942428283291F, -0.7853981633974483F, 0.0F);
		spoke_3 = new ModelRenderer(this, 26, 102);
		spoke_3.setRotationPoint(8.0F, -5.9F, 8.0F);
		spoke_3.addBox(-0.5F, 0.0F, -9.9F, 1, 3, 4, 0.0F);
		setRotateAngle(spoke_3, 0.6373942428283291F, -1.5707963267948966F, 0.0F);
		knobs = new ModelRenderer(this, 102, 19);
		knobs.setRotationPoint(4.5F, 5.0F, 5.5F);
		knobs.addBox(-1.0F, 0.0F, -6.0F, 2, 2, 10, 0.0F);
		ringSupport_3 = new ModelRenderer(this, 20, 119);
		ringSupport_3.setRotationPoint(3.0F, 4.0F, 3.0F);
		ringSupport_3.addBox(-0.5F, 0.0F, -4.0F, 1, 5, 1, 0.0F);
		setRotateAngle(ringSupport_3, 0.0F, -1.5707963267948966F, 0.0F);
		ring_1 = new ModelRenderer(this, 66, 0);
		ring_1.setRotationPoint(-1.0F, 9.5F, -1.0F);
		ring_1.addBox(0.0F, 0.0F, 0.0F, 11, 3, 11, 0.0F);
		centerShaft = new ModelRenderer(this, 0, 85);
		centerShaft.setRotationPoint(5.0F, -21.0F, 5.0F);
		centerShaft.addBox(0.0F, 0.0F, 0.0F, 6, 9, 6, 0.0F);
		ring_3 = new ModelRenderer(this, 72, 19);
		ring_3.setRotationPoint(-0.5F, 5.0F, -0.5F);
		ring_3.addBox(0.0F, 0.0F, 0.0F, 7, 1, 7, 0.0F);
		angleSpoke_2 = new ModelRenderer(this, 26, 85);
		angleSpoke_2.setRotationPoint(8.0F, -5.9F, 8.0F);
		angleSpoke_2.addBox(-0.5F, -1.0F, -12.3F, 1, 3, 5, 0.0F);
		setRotateAngle(angleSpoke_2, 0.6373942428283291F, 3.9269908169872414F, 0.0F);
		baseShaft = new ModelRenderer(this, 0, 48);
		baseShaft.setRotationPoint(3.5F, -12.0F, 3.5F);
		baseShaft.addBox(0.0F, 0.0F, 0.0F, 9, 12, 9, 0.0F);
		ringSupport = new ModelRenderer(this, 14, 111);
		ringSupport.setRotationPoint(3.0F, 4.0F, 3.0F);
		ringSupport.addBox(-0.5F, 0.0F, -4.0F, 1, 5, 1, 0.0F);
		ring_2 = new ModelRenderer(this, 42, 19);
		ring_2.setRotationPoint(-0.5F, 7.0F, -0.5F);
		ring_2.addBox(0.0F, 0.0F, 0.0F, 7, 1, 7, 0.0F);
		angleSpoke = new ModelRenderer(this, 112, 0);
		angleSpoke.setRotationPoint(8.0F, -5.9F, 8.0F);
		angleSpoke.addBox(-0.5F, -1.0F, -12.3F, 1, 3, 5, 0.0F);
		setRotateAngle(angleSpoke, 0.6373942428283291F, 0.7853981633974483F, 0.0F);
		spoke_1 = new ModelRenderer(this, 0, 112);
		spoke_1.setRotationPoint(8.0F, -5.9F, 8.0F);
		spoke_1.addBox(-0.5F, 0.0F, -9.9F, 1, 3, 4, 0.0F);
		setRotateAngle(spoke_1, 0.6373942428283291F, 1.5707963267948966F, 0.0F);
		basePlate = new ModelRenderer(this, 0, 0);
		basePlate.setRotationPoint(-8.0F, 23.0F, -8.0F);
		basePlate.addBox(0.0F, 0.0F, 0.0F, 16, 1, 16, 0.0F);
		knobs_1 = new ModelRenderer(this, 0, 71);
		knobs_1.setRotationPoint(5.5F, 5.0F, 4.5F);
		knobs_1.addBox(-1.0F, 0.0F, -6.0F, 2, 2, 10, 0.0F);
		setRotateAngle(knobs_1, 0.0F, 1.5707963267948966F, 0.0F);
		glandEnd = new ModelRenderer(this, 0, 33);
		glandEnd.setRotationPoint(3.0F, -20.0F, 3.0F);
		glandEnd.addBox(0.0F, 0.0F, 0.0F, 10, 3, 10, 0.0F);
		ringSupport_2 = new ModelRenderer(this, 20, 111);
		ringSupport_2.setRotationPoint(3.0F, 4.0F, 3.0F);
		ringSupport_2.addBox(-0.5F, 0.0F, -4.0F, 1, 5, 1, 0.0F);
		setRotateAngle(ringSupport_2, 0.0F, 3.141592653589793F, 0.0F);
		spoke = new ModelRenderer(this, 112, 10);
		spoke.setRotationPoint(8.0F, -5.9F, 8.0F);
		spoke.addBox(-0.5F, 0.0F, -9.9F, 1, 3, 4, 0.0F);
		setRotateAngle(spoke, 0.6373942428283291F, 0.0F, 0.0F);
		ringSupport_1 = new ModelRenderer(this, 14, 119);
		ringSupport_1.setRotationPoint(3.0F, 4.0F, 3.0F);
		ringSupport_1.addBox(-0.5F, 0.0F, -4.0F, 1, 5, 1, 0.0F);
		setRotateAngle(ringSupport_1, 0.0F, 1.5707963267948966F, 0.0F);
		basePlate.addChild(spoke_2);
		basePlate.addChild(angleSpoke_1);
		baseShaft.addChild(ring);
		basePlate.addChild(angleSpoke_3);
		basePlate.addChild(spoke_3);
		baseShaft.addChild(knobs);
		centerShaft.addChild(ringSupport_3);
		baseShaft.addChild(ring_1);
		basePlate.addChild(centerShaft);
		centerShaft.addChild(ring_3);
		basePlate.addChild(angleSpoke_2);
		basePlate.addChild(baseShaft);
		centerShaft.addChild(ringSupport);
		centerShaft.addChild(ring_2);
		basePlate.addChild(angleSpoke);
		basePlate.addChild(spoke_1);
		baseShaft.addChild(knobs_1);
		basePlate.addChild(glandEnd);
		centerShaft.addChild(ringSupport_2);
		basePlate.addChild(spoke);
		centerShaft.addChild(ringSupport_1);
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
