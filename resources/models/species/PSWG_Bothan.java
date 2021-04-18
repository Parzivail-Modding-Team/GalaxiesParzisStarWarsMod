package Java;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * PSWG_Bothan - Sindavar
 * Created using Tabula 7.1.0
 */
public class PSWG_Bothan extends ModelBase {
    public ModelRenderer helmet;
    public ModelRenderer leftsleeve;
    public ModelRenderer leftarm;
    public ModelRenderer rightsleeve;
    public ModelRenderer rightarm;
    public ModelRenderer leftpantleg;
    public ModelRenderer leftleg;
    public ModelRenderer rightpantleg;
    public ModelRenderer rightleg;
    public ModelRenderer Hair;
    public ModelRenderer Head;
    public ModelRenderer jacket;
    public ModelRenderer torso;
    public ModelRenderer EarL;
    public ModelRenderer SnoutLower;
    public ModelRenderer EarR;
    public ModelRenderer EarTipL;
    public ModelRenderer SnoutUpper;
    public ModelRenderer SnoutLower_1;
    public ModelRenderer EarTipR;
    public ModelRenderer Chest;

    public PSWG_Bothan() {
        this.textureWidth = 96;
        this.textureHeight = 96;
        this.rightarm = new ModelRenderer(this, 40, 16);
        this.rightarm.setRotationPoint(-5.0F, 2.0F, 0.0F);
        this.rightarm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F);
        this.rightleg = new ModelRenderer(this, 0, 16);
        this.rightleg.setRotationPoint(-1.9F, 12.0F, 0.0F);
        this.rightleg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
        this.SnoutLower_1 = new ModelRenderer(this, 63, 71);
        this.SnoutLower_1.setRotationPoint(-0.6F, 1.7F, -0.1F);
        this.SnoutLower_1.addBox(0.0F, 0.0F, 0.0F, 5, 3, 4, 0.0F);
        this.Chest = new ModelRenderer(this, 0, 65);
        this.Chest.setRotationPoint(0.0F, -0.1F, 1.0F);
        this.Chest.addBox(-3.0F, 2.0F, -4.0F, 6, 3, 2, 0.0F);
        this.rightsleeve = new ModelRenderer(this, 40, 32);
        this.rightsleeve.setRotationPoint(-5.0F, 2.0F, 0.0F);
        this.rightsleeve.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, 0.2F);
        this.rightpantleg = new ModelRenderer(this, 0, 32);
        this.rightpantleg.setRotationPoint(-2.0F, 12.0F, 0.0F);
        this.rightpantleg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.2F);
        this.EarTipR = new ModelRenderer(this, 55, 68);
        this.EarTipR.setRotationPoint(0.0F, 0.0F, 4.0F);
        this.EarTipR.addBox(-1.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
        this.jacket = new ModelRenderer(this, 16, 32);
        this.jacket.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.jacket.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.2F);
        this.EarTipL = new ModelRenderer(this, 55, 68);
        this.EarTipL.setRotationPoint(0.0F, 0.0F, 4.0F);
        this.EarTipL.addBox(0.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
        this.Hair = new ModelRenderer(this, 11, 65);
        this.Hair.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Hair.addBox(-4.0F, -8.0F, -4.0F, 8, 17, 8, 0.1F);
        this.Head = new ModelRenderer(this, 0, 0);
        this.Head.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
        this.leftpantleg = new ModelRenderer(this, 0, 48);
        this.leftpantleg.setRotationPoint(2.0F, 12.0F, 0.0F);
        this.leftpantleg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.2F);
        this.leftarm = new ModelRenderer(this, 32, 48);
        this.leftarm.setRotationPoint(5.0F, 2.0F, 0.0F);
        this.leftarm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F);
        this.leftleg = new ModelRenderer(this, 16, 48);
        this.leftleg.setRotationPoint(1.9F, 12.0F, 0.0F);
        this.leftleg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
        this.helmet = new ModelRenderer(this, 32, 0);
        this.helmet.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.helmet.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.2F);
        this.leftsleeve = new ModelRenderer(this, 48, 48);
        this.leftsleeve.setRotationPoint(5.0F, 2.0F, 0.0F);
        this.leftsleeve.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, 0.2F);
        this.torso = new ModelRenderer(this, 16, 16);
        this.torso.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.torso.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);
        this.SnoutUpper = new ModelRenderer(this, 66, 81);
        this.SnoutUpper.setRotationPoint(0.5F, 0.0F, 0.0F);
        this.SnoutUpper.addBox(0.0F, 0.0F, 0.0F, 3, 2, 6, 0.0F);
        this.setRotateAngle(SnoutUpper, 0.38275070496235647F, 0.0F, 0.0F);
        this.EarL = new ModelRenderer(this, 46, 69);
        this.EarL.setRotationPoint(3.0F, -4.9F, -2.1F);
        this.EarL.addBox(0.0F, 0.0F, 0.0F, 1, 2, 4, 0.0F);
        this.setRotateAngle(EarL, 0.6373942428283291F, 0.36425021489121656F, 0.18203784098300857F);
        this.SnoutLower = new ModelRenderer(this, 45, 80);
        this.SnoutLower.setRotationPoint(-2.0F, -1.1F, -7.0F);
        this.SnoutLower.addBox(0.0F, 0.0F, 0.0F, 4, 4, 5, 0.0F);
        this.setRotateAngle(SnoutLower, 0.6373942428283291F, 0.0F, 0.0F);
        this.EarR = new ModelRenderer(this, 46, 69);
        this.EarR.setRotationPoint(-3.0F, -4.9F, -2.1F);
        this.EarR.addBox(-1.0F, 0.0F, 0.0F, 1, 2, 4, 0.0F);
        this.setRotateAngle(EarR, 0.6373942428283291F, -0.36425021489121656F, -0.18203784098300857F);
        this.SnoutLower.addChild(this.SnoutLower_1);
        this.torso.addChild(this.Chest);
        this.EarR.addChild(this.EarTipR);
        this.EarL.addChild(this.EarTipL);
        this.SnoutLower.addChild(this.SnoutUpper);
        this.Head.addChild(this.EarL);
        this.Head.addChild(this.SnoutLower);
        this.Head.addChild(this.EarR);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.rightarm.render(f5);
        this.rightleg.render(f5);
        this.rightsleeve.render(f5);
        this.rightpantleg.render(f5);
        this.jacket.render(f5);
        this.Hair.render(f5);
        this.Head.render(f5);
        this.leftpantleg.render(f5);
        this.leftarm.render(f5);
        this.leftleg.render(f5);
        this.helmet.render(f5);
        this.leftsleeve.render(f5);
        this.torso.render(f5);
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
