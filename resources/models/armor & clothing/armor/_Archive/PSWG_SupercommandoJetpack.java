package Java;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * PSWGSupercommandoJetpack - Sindavar
 * Created using Tabula 7.1.0
 */
public class PSWG_SupercommandoJetpack extends ModelBase {
    public ModelRenderer shape32;
    public ModelRenderer shape32_1;
    public ModelRenderer shape32_2;
    public ModelRenderer shape32_3;
    public ModelRenderer shape32_4;

    public PSWG_SupercommandoJetpack() {
        this.textureWidth = 16;
        this.textureHeight = 16;
        this.shape32 = new ModelRenderer(this, 0, 0);
        this.shape32.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shape32.addBox(-2.5F, 0.0F, 0.0F, 5, 4, 1, 0.2F);
        this.shape32_1 = new ModelRenderer(this, 0, 5);
        this.shape32_1.mirror = true;
        this.shape32_1.setRotationPoint(1.2F, -0.9F, 0.7F);
        this.shape32_1.addBox(0.0F, 0.0F, 0.0F, 2, 7, 1, 0.2F);
        this.shape32_4 = new ModelRenderer(this, 10, 12);
        this.shape32_4.setRotationPoint(0.0F, 0.1F, 1.1F);
        this.shape32_4.addBox(-0.5F, 0.0F, -1.0F, 1, 2, 2, 0.2F);
        this.setRotateAngle(shape32_4, 0.0F, -1.5707963267948966F, 0.0F);
        this.shape32_2 = new ModelRenderer(this, 0, 5);
        this.shape32_2.setRotationPoint(-1.2F, -0.9F, 0.7F);
        this.shape32_2.addBox(-2.0F, 0.0F, 0.0F, 2, 7, 1, 0.2F);
        this.shape32_3 = new ModelRenderer(this, 7, 5);
        this.shape32_3.setRotationPoint(0.0F, -1.3F, 0.8F);
        this.shape32_3.addBox(-0.5F, 0.0F, -0.5F, 1, 7, 1, 0.2F);
        this.setRotateAngle(shape32_3, 0.0F, -0.7853981633974483F, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.shape32.render(f5);
        this.shape32_1.render(f5);
        this.shape32_4.render(f5);
        this.shape32_2.render(f5);
        this.shape32_3.render(f5);
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
