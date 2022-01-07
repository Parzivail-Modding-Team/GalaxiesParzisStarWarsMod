package Java;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * PSWG_Rebel_Pilot - Sindavar
 * Created using Tabula 7.1.0
 */
public class PSWG_Rebel_Pilot extends ModelBase {
    public ModelRenderer helmet;
    public ModelRenderer leftsleeve;
    public ModelRenderer leftarm;
    public ModelRenderer rightsleeve;
    public ModelRenderer rightarm;
    public ModelRenderer leftpantleg;
    public ModelRenderer leftleg;
    public ModelRenderer rightpantleg;
    public ModelRenderer rightleg;
    public ModelRenderer Head;
    public ModelRenderer jacket;
    public ModelRenderer torso;
    public ModelRenderer shape30;
    public ModelRenderer shape31;
    public ModelRenderer shape31_1;

    public PSWG_Rebel_Pilot() {
        this.textureWidth = 96;
        this.textureHeight = 96;
        this.rightsleeve = new ModelRenderer(this, 40, 32);
        this.rightsleeve.setRotationPoint(-5.0F, 2.0F, 0.0F);
        this.rightsleeve.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, 0.5F);
        this.setRotateAngle(rightsleeve, 0.0F, 0.0F, 0.18203784098300857F);
        this.leftsleeve = new ModelRenderer(this, 48, 48);
        this.leftsleeve.setRotationPoint(5.0F, 2.0F, 0.0F);
        this.leftsleeve.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, 0.5F);
        this.setRotateAngle(leftsleeve, 0.0F, 0.0F, -0.18203784098300857F);
        this.rightarm = new ModelRenderer(this, 40, 16);
        this.rightarm.setRotationPoint(-5.0F, 2.0F, 0.0F);
        this.rightarm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, 0.3F);
        this.setRotateAngle(rightarm, 0.0F, 0.0F, 0.18203784098300857F);
        this.leftpantleg = new ModelRenderer(this, 0, 48);
        this.leftpantleg.setRotationPoint(2.0F, 12.0F, 0.0F);
        this.leftpantleg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.5F);
        this.setRotateAngle(leftpantleg, 0.18203784098300857F, 0.0F, 0.0F);
        this.jacket = new ModelRenderer(this, 16, 32);
        this.jacket.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.jacket.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.5F);
        this.shape30 = new ModelRenderer(this, 0, 64);
        this.shape30.setRotationPoint(0.0F, -9.2F, -5.1F);
        this.shape30.addBox(-1.0F, 0.0F, 0.0F, 2, 7, 10, 0.0F);
        this.shape31 = new ModelRenderer(this, 16, 67);
        this.shape31.setRotationPoint(-1.5F, 3.6F, -0.4F);
        this.shape31.addBox(0.0F, 0.0F, 0.0F, 3, 2, 1, 0.0F);
        this.leftleg = new ModelRenderer(this, 16, 48);
        this.leftleg.setRotationPoint(1.9F, 12.0F, 0.0F);
        this.leftleg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.3F);
        this.setRotateAngle(leftleg, 0.18203784098300857F, 0.0F, 0.0F);
        this.rightpantleg = new ModelRenderer(this, 0, 32);
        this.rightpantleg.setRotationPoint(-2.0F, 12.0F, 0.0F);
        this.rightpantleg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.5F);
        this.setRotateAngle(rightpantleg, -0.18203784098300857F, 0.0F, 0.0F);
        this.rightleg = new ModelRenderer(this, 0, 16);
        this.rightleg.setRotationPoint(-1.9F, 12.0F, 0.0F);
        this.rightleg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.3F);
        this.setRotateAngle(rightleg, -0.18203784098300857F, 0.0F, 0.0F);
        this.helmet = new ModelRenderer(this, 32, 0);
        this.helmet.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.helmet.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.6F);
        this.torso = new ModelRenderer(this, 16, 16);
        this.torso.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.torso.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.3F);
        this.shape31_1 = new ModelRenderer(this, 24, 65);
        this.shape31_1.setRotationPoint(0.0F, 2.7F, -3.3F);
        this.shape31_1.addBox(-2.0F, 0.0F, 0.0F, 4, 4, 1, 0.0F);
        this.leftarm = new ModelRenderer(this, 32, 48);
        this.leftarm.setRotationPoint(5.0F, 2.0F, 0.0F);
        this.leftarm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, 0.3F);
        this.setRotateAngle(leftarm, 0.0F, 0.0F, -0.18203784098300857F);
        this.Head = new ModelRenderer(this, 0, 0);
        this.Head.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.3F);
        this.Head.addChild(this.shape30);
        this.shape30.addChild(this.shape31);
        this.torso.addChild(this.shape31_1);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.rightsleeve.render(f5);
        this.leftsleeve.render(f5);
        this.rightarm.render(f5);
        this.leftpantleg.render(f5);
        this.jacket.render(f5);
        this.leftleg.render(f5);
        this.rightpantleg.render(f5);
        this.rightleg.render(f5);
        this.helmet.render(f5);
        this.torso.render(f5);
        this.leftarm.render(f5);
        this.Head.render(f5);
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
