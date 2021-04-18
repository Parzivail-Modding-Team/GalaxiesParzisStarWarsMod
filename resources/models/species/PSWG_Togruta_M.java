package Java;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * PSWG_Togruta_M - Sindavar
 * Created using Tabula 7.1.0
 */
public class PSWG_Togruta_M extends ModelBase {
    public ModelRenderer helmet;
    public ModelRenderer head;
    public ModelRenderer jacket;
    public ModelRenderer torso;
    public ModelRenderer leftsleeve;
    public ModelRenderer leftarm;
    public ModelRenderer rightsleeve;
    public ModelRenderer rightarm;
    public ModelRenderer leftpantleg;
    public ModelRenderer leftleg;
    public ModelRenderer rightpantleg;
    public ModelRenderer rightleg;
    public ModelRenderer TailBaseL;
    public ModelRenderer TailBaseR;
    public ModelRenderer TailBaseB;
    public ModelRenderer SkullElevation;
    public ModelRenderer TailMidL;
    public ModelRenderer TaiUpperL;
    public ModelRenderer TailLowerL;
    public ModelRenderer TaiTipL;
    public ModelRenderer TailMidR;
    public ModelRenderer TailUpperR;
    public ModelRenderer TailLowerR;
    public ModelRenderer TailTipR;
    public ModelRenderer TailMidB;
    public ModelRenderer TailLowerB;

    public PSWG_Togruta_M() {
        this.textureWidth = 96;
        this.textureHeight = 96;
        this.TailLowerR = new ModelRenderer(this, 32, 71);
        this.TailLowerR.mirror = true;
        this.TailLowerR.setRotationPoint(-0.9F, 6.4F, 0.5F);
        this.TailLowerR.addBox(-2.0F, 0.0F, 0.0F, 2, 4, 2, 0.0F);
        this.setRotateAngle(TailLowerR, 0.31869712141416456F, 0.0F, -0.27314402793711257F);
        this.TailBaseL = new ModelRenderer(this, 0, 71);
        this.TailBaseL.setRotationPoint(1.4F, -10.1F, -2.6F);
        this.TailBaseL.addBox(0.0F, 0.0F, 0.0F, 4, 7, 6, 0.0F);
        this.setRotateAngle(TailBaseL, -0.22759093446006054F, -0.14398966328953217F, 0.0F);
        this.torso = new ModelRenderer(this, 16, 16);
        this.torso.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.torso.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);
        this.TailMidB = new ModelRenderer(this, 34, 81);
        this.TailMidB.mirror = true;
        this.TailMidB.setRotationPoint(1.0F, -1.4F, 2.5F);
        this.TailMidB.addBox(0.0F, 0.0F, -0.9F, 4, 11, 3, 0.0F);
        this.leftarm = new ModelRenderer(this, 32, 48);
        this.leftarm.setRotationPoint(5.0F, 2.0F, 0.0F);
        this.leftarm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F);
        this.TailLowerB = new ModelRenderer(this, 49, 83);
        this.TailLowerB.mirror = true;
        this.TailLowerB.setRotationPoint(1.0F, 10.0F, -0.4F);
        this.TailLowerB.addBox(0.0F, 0.0F, 0.0F, 2, 4, 2, 0.0F);
        this.helmet = new ModelRenderer(this, 32, 0);
        this.helmet.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.helmet.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.2F);
        this.TailBaseR = new ModelRenderer(this, 0, 71);
        this.TailBaseR.mirror = true;
        this.TailBaseR.setRotationPoint(-1.4F, -10.1F, -2.6F);
        this.TailBaseR.addBox(-4.0F, 0.0F, 0.0F, 4, 7, 6, 0.0F);
        this.setRotateAngle(TailBaseR, -0.22759093446006054F, 0.14398966328953217F, 0.0F);
        this.rightsleeve = new ModelRenderer(this, 40, 32);
        this.rightsleeve.setRotationPoint(-5.0F, 2.0F, 0.0F);
        this.rightsleeve.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, 0.2F);
        this.leftpantleg = new ModelRenderer(this, 0, 48);
        this.leftpantleg.setRotationPoint(2.0F, 12.0F, 0.0F);
        this.leftpantleg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.2F);
        this.rightarm = new ModelRenderer(this, 40, 16);
        this.rightarm.setRotationPoint(-5.0F, 2.0F, 0.0F);
        this.rightarm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F);
        this.jacket = new ModelRenderer(this, 16, 32);
        this.jacket.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.jacket.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.2F);
        this.TailBaseB = new ModelRenderer(this, 10, 85);
        this.TailBaseB.setRotationPoint(-3.0F, -7.7F, 0.4F);
        this.TailBaseB.addBox(0.0F, 0.0F, 0.0F, 6, 5, 5, 0.0F);
        this.TaiTipL = new ModelRenderer(this, 54, 72);
        this.TaiTipL.setRotationPoint(0.0F, -2.1F, 0.5F);
        this.TaiTipL.addBox(0.0F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
        this.setRotateAngle(TaiTipL, 0.0F, 0.0F, -0.27314402793711257F);
        this.TailLowerL = new ModelRenderer(this, 32, 71);
        this.TailLowerL.setRotationPoint(0.9F, 6.4F, 0.5F);
        this.TailLowerL.addBox(0.0F, 0.0F, 0.0F, 2, 4, 2, 0.0F);
        this.setRotateAngle(TailLowerL, 0.31869712141416456F, 0.0F, 0.27314402793711257F);
        this.leftsleeve = new ModelRenderer(this, 48, 48);
        this.leftsleeve.setRotationPoint(5.0F, 2.0F, 0.0F);
        this.leftsleeve.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, 0.2F);
        this.leftleg = new ModelRenderer(this, 16, 48);
        this.leftleg.setRotationPoint(1.9F, 12.0F, 0.0F);
        this.leftleg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
        this.head = new ModelRenderer(this, 0, 0);
        this.head.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
        this.SkullElevation = new ModelRenderer(this, 60, 80);
        this.SkullElevation.setRotationPoint(-4.0F, -9.1F, -2.0F);
        this.SkullElevation.addBox(0.0F, 0.0F, 0.0F, 8, 2, 6, 0.0F);
        this.TaiUpperL = new ModelRenderer(this, 41, 71);
        this.TaiUpperL.setRotationPoint(1.2F, -1.0F, -1.3F);
        this.TaiUpperL.addBox(0.0F, 0.0F, 0.0F, 3, 5, 3, 0.0F);
        this.setRotateAngle(TaiUpperL, 0.8196066167365371F, 0.0F, 0.27314402793711257F);
        this.TailTipR = new ModelRenderer(this, 54, 72);
        this.TailTipR.mirror = true;
        this.TailTipR.setRotationPoint(0.0F, -2.1F, 0.5F);
        this.TailTipR.addBox(-2.0F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
        this.setRotateAngle(TailTipR, 0.0F, 0.0F, 0.27314402793711257F);
        this.TailUpperR = new ModelRenderer(this, 41, 71);
        this.TailUpperR.mirror = true;
        this.TailUpperR.setRotationPoint(-1.2F, -1.0F, -1.3F);
        this.TailUpperR.addBox(-3.0F, 0.0F, 0.0F, 3, 5, 3, 0.0F);
        this.setRotateAngle(TailUpperR, 0.8196066167365371F, 0.0F, -0.27314402793711257F);
        this.TailMidR = new ModelRenderer(this, 20, 71);
        this.TailMidR.mirror = true;
        this.TailMidR.setRotationPoint(-1.0F, 2.7F, 2.3F);
        this.TailMidR.addBox(-3.0F, 0.0F, 0.0F, 3, 7, 3, 0.0F);
        this.setRotateAngle(TailMidR, -0.22759093446006054F, -0.136659280431156F, 0.091106186954104F);
        this.rightleg = new ModelRenderer(this, 0, 16);
        this.rightleg.setRotationPoint(-1.9F, 12.0F, 0.0F);
        this.rightleg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
        this.rightpantleg = new ModelRenderer(this, 0, 32);
        this.rightpantleg.setRotationPoint(-2.0F, 12.0F, 0.0F);
        this.rightpantleg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.2F);
        this.TailMidL = new ModelRenderer(this, 20, 71);
        this.TailMidL.setRotationPoint(1.0F, 2.7F, 2.3F);
        this.TailMidL.addBox(0.0F, 0.0F, 0.0F, 3, 7, 3, 0.0F);
        this.setRotateAngle(TailMidL, -0.22759093446006054F, 0.136659280431156F, -0.091106186954104F);
        this.TailMidR.addChild(this.TailLowerR);
        this.head.addChild(this.TailBaseL);
        this.TailBaseB.addChild(this.TailMidB);
        this.TailMidB.addChild(this.TailLowerB);
        this.head.addChild(this.TailBaseR);
        this.head.addChild(this.TailBaseB);
        this.TaiUpperL.addChild(this.TaiTipL);
        this.TailMidL.addChild(this.TailLowerL);
        this.head.addChild(this.SkullElevation);
        this.TailBaseL.addChild(this.TaiUpperL);
        this.TailUpperR.addChild(this.TailTipR);
        this.TailBaseR.addChild(this.TailUpperR);
        this.TailBaseR.addChild(this.TailMidR);
        this.TailBaseL.addChild(this.TailMidL);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.torso.render(f5);
        this.leftarm.render(f5);
        this.helmet.render(f5);
        this.rightsleeve.render(f5);
        this.leftpantleg.render(f5);
        this.rightarm.render(f5);
        this.jacket.render(f5);
        this.leftsleeve.render(f5);
        this.leftleg.render(f5);
        this.head.render(f5);
        this.rightleg.render(f5);
        this.rightpantleg.render(f5);
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
