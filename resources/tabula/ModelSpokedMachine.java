package com.parzivail.swg;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelSpokedMachine - parzi
 * Created using P-Tabula 4.1.1
 */
public class ModelSpokedMachine extends ModelBase {
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

    public ModelSpokedMachine() {
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.spoke_2 = new ModelRenderer(this, 14, 102);
        this.spoke_2.setRotationPoint(8.0F, -5.9F, 8.0F);
        this.spoke_2.addBox(-0.5F, 0.0F, -9.9F, 1, 3, 4, 0.0F);
        this.setRotateAngle(spoke_2, 0.6373942428283291F, 3.141592653589793F, 0.0F);
        this.angleSpoke_1 = new ModelRenderer(this, 26, 71);
        this.angleSpoke_1.setRotationPoint(8.0F, -5.9F, 8.0F);
        this.angleSpoke_1.addBox(-0.5F, -1.0F, -12.3F, 1, 3, 5, 0.0F);
        this.setRotateAngle(angleSpoke_1, 0.6373942428283291F, 2.356194490192345F, 0.0F);
        this.ring = new ModelRenderer(this, 0, 19);
        this.ring.setRotationPoint(-0.5F, 0.5F, -0.5F);
        this.ring.addBox(0.0F, 0.0F, 0.0F, 10, 2, 10, 0.0F);
        this.angleSpoke_3 = new ModelRenderer(this, 0, 102);
        this.angleSpoke_3.setRotationPoint(8.0F, -5.9F, 8.0F);
        this.angleSpoke_3.addBox(-0.5F, -1.0F, -12.3F, 1, 3, 5, 0.0F);
        this.setRotateAngle(angleSpoke_3, 0.6373942428283291F, -0.7853981633974483F, 0.0F);
        this.spoke_3 = new ModelRenderer(this, 26, 102);
        this.spoke_3.setRotationPoint(8.0F, -5.9F, 8.0F);
        this.spoke_3.addBox(-0.5F, 0.0F, -9.9F, 1, 3, 4, 0.0F);
        this.setRotateAngle(spoke_3, 0.6373942428283291F, -1.5707963267948966F, 0.0F);
        this.knobs = new ModelRenderer(this, 102, 19);
        this.knobs.setRotationPoint(4.5F, 5.0F, 5.5F);
        this.knobs.addBox(-1.0F, 0.0F, -6.0F, 2, 2, 10, 0.0F);
        this.ringSupport_3 = new ModelRenderer(this, 20, 119);
        this.ringSupport_3.setRotationPoint(3.0F, 4.0F, 3.0F);
        this.ringSupport_3.addBox(-0.5F, 0.0F, -4.0F, 1, 5, 1, 0.0F);
        this.setRotateAngle(ringSupport_3, 0.0F, -1.5707963267948966F, 0.0F);
        this.ring_1 = new ModelRenderer(this, 66, 0);
        this.ring_1.setRotationPoint(-1.0F, 9.5F, -1.0F);
        this.ring_1.addBox(0.0F, 0.0F, 0.0F, 11, 3, 11, 0.0F);
        this.centerShaft = new ModelRenderer(this, 0, 85);
        this.centerShaft.setRotationPoint(5.0F, -21.0F, 5.0F);
        this.centerShaft.addBox(0.0F, 0.0F, 0.0F, 6, 9, 6, 0.0F);
        this.ring_3 = new ModelRenderer(this, 72, 19);
        this.ring_3.setRotationPoint(-0.5F, 5.0F, -0.5F);
        this.ring_3.addBox(0.0F, 0.0F, 0.0F, 7, 1, 7, 0.0F);
        this.angleSpoke_2 = new ModelRenderer(this, 26, 85);
        this.angleSpoke_2.setRotationPoint(8.0F, -5.9F, 8.0F);
        this.angleSpoke_2.addBox(-0.5F, -1.0F, -12.3F, 1, 3, 5, 0.0F);
        this.setRotateAngle(angleSpoke_2, 0.6373942428283291F, 3.9269908169872414F, 0.0F);
        this.baseShaft = new ModelRenderer(this, 0, 48);
        this.baseShaft.setRotationPoint(3.5F, -12.0F, 3.5F);
        this.baseShaft.addBox(0.0F, 0.0F, 0.0F, 9, 12, 9, 0.0F);
        this.ringSupport = new ModelRenderer(this, 14, 111);
        this.ringSupport.setRotationPoint(3.0F, 4.0F, 3.0F);
        this.ringSupport.addBox(-0.5F, 0.0F, -4.0F, 1, 5, 1, 0.0F);
        this.ring_2 = new ModelRenderer(this, 42, 19);
        this.ring_2.setRotationPoint(-0.5F, 7.0F, -0.5F);
        this.ring_2.addBox(0.0F, 0.0F, 0.0F, 7, 1, 7, 0.0F);
        this.angleSpoke = new ModelRenderer(this, 112, 0);
        this.angleSpoke.setRotationPoint(8.0F, -5.9F, 8.0F);
        this.angleSpoke.addBox(-0.5F, -1.0F, -12.3F, 1, 3, 5, 0.0F);
        this.setRotateAngle(angleSpoke, 0.6373942428283291F, 0.7853981633974483F, 0.0F);
        this.spoke_1 = new ModelRenderer(this, 0, 112);
        this.spoke_1.setRotationPoint(8.0F, -5.9F, 8.0F);
        this.spoke_1.addBox(-0.5F, 0.0F, -9.9F, 1, 3, 4, 0.0F);
        this.setRotateAngle(spoke_1, 0.6373942428283291F, 1.5707963267948966F, 0.0F);
        this.basePlate = new ModelRenderer(this, 0, 0);
        this.basePlate.setRotationPoint(-8.0F, 23.0F, -8.0F);
        this.basePlate.addBox(0.0F, 0.0F, 0.0F, 16, 1, 16, 0.0F);
        this.knobs_1 = new ModelRenderer(this, 0, 71);
        this.knobs_1.setRotationPoint(5.5F, 5.0F, 4.5F);
        this.knobs_1.addBox(-1.0F, 0.0F, -6.0F, 2, 2, 10, 0.0F);
        this.setRotateAngle(knobs_1, 0.0F, 1.5707963267948966F, 0.0F);
        this.glandEnd = new ModelRenderer(this, 0, 33);
        this.glandEnd.setRotationPoint(3.0F, -20.0F, 3.0F);
        this.glandEnd.addBox(0.0F, 0.0F, 0.0F, 10, 3, 10, 0.0F);
        this.ringSupport_2 = new ModelRenderer(this, 20, 111);
        this.ringSupport_2.setRotationPoint(3.0F, 4.0F, 3.0F);
        this.ringSupport_2.addBox(-0.5F, 0.0F, -4.0F, 1, 5, 1, 0.0F);
        this.setRotateAngle(ringSupport_2, 0.0F, 3.141592653589793F, 0.0F);
        this.spoke = new ModelRenderer(this, 112, 10);
        this.spoke.setRotationPoint(8.0F, -5.9F, 8.0F);
        this.spoke.addBox(-0.5F, 0.0F, -9.9F, 1, 3, 4, 0.0F);
        this.setRotateAngle(spoke, 0.6373942428283291F, 0.0F, 0.0F);
        this.ringSupport_1 = new ModelRenderer(this, 14, 119);
        this.ringSupport_1.setRotationPoint(3.0F, 4.0F, 3.0F);
        this.ringSupport_1.addBox(-0.5F, 0.0F, -4.0F, 1, 5, 1, 0.0F);
        this.setRotateAngle(ringSupport_1, 0.0F, 1.5707963267948966F, 0.0F);
        this.basePlate.addChild(this.spoke_2);
        this.basePlate.addChild(this.angleSpoke_1);
        this.baseShaft.addChild(this.ring);
        this.basePlate.addChild(this.angleSpoke_3);
        this.basePlate.addChild(this.spoke_3);
        this.baseShaft.addChild(this.knobs);
        this.centerShaft.addChild(this.ringSupport_3);
        this.baseShaft.addChild(this.ring_1);
        this.basePlate.addChild(this.centerShaft);
        this.centerShaft.addChild(this.ring_3);
        this.basePlate.addChild(this.angleSpoke_2);
        this.basePlate.addChild(this.baseShaft);
        this.centerShaft.addChild(this.ringSupport);
        this.centerShaft.addChild(this.ring_2);
        this.basePlate.addChild(this.angleSpoke);
        this.basePlate.addChild(this.spoke_1);
        this.baseShaft.addChild(this.knobs_1);
        this.basePlate.addChild(this.glandEnd);
        this.centerShaft.addChild(this.ringSupport_2);
        this.basePlate.addChild(this.spoke);
        this.centerShaft.addChild(this.ringSupport_1);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.basePlate.render(f5);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
