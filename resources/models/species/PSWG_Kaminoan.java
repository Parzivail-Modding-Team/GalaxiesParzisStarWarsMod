package Java;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * PSWG_Kaminoan - Sindavar
 * Created using Tabula 7.1.0
 */
public class PSWG_Kaminoan extends ModelBase {
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
    public ModelRenderer neck;
    public ModelRenderer neckoverlay;
    public ModelRenderer Head_1;
    public ModelRenderer Chest;
    public ModelRenderer torsolower;

    public PSWG_Kaminoan() {
        this.textureWidth = 96;
        this.textureHeight = 96;
        this.jacket = new ModelRenderer(this, 21, 36);
        this.jacket.setRotationPoint(0.0F, -4.6F, 1.0F);
        this.jacket.addBox(-3.0F, 0.0F, -2.0F, 6, 6, 2, 0.2F);
        this.torsolower = new ModelRenderer(this, 22, 25);
        this.torsolower.setRotationPoint(0.0F, 5.4F, -0.5F);
        this.torsolower.addBox(-2.5F, 0.0F, -1.5F, 5, 7, 2, 0.0F);
        this.rightsleeve = new ModelRenderer(this, 40, 30);
        this.rightsleeve.setRotationPoint(-4.3F, -3.0F, 0.0F);
        this.rightsleeve.addBox(-1.0F, -1.5F, -1.0F, 2, 15, 2, 0.2F);
        this.Chest = new ModelRenderer(this, 0, 69);
        this.Chest.setRotationPoint(0.5F, -1.4F, 1.0F);
        this.Chest.addBox(-3.0F, 2.0F, -4.0F, 5, 3, 2, 0.0F);
        this.Head = new ModelRenderer(this, 0, 0);
        this.Head.setRotationPoint(0.2F, -12.5F, -0.5F);
        this.Head.addBox(-2.5F, -5.0F, -2.5F, 5, 5, 5, 0.0F);
        this.torso = new ModelRenderer(this, 21, 15);
        this.torso.setRotationPoint(0.0F, -4.6F, 1.0F);
        this.torso.addBox(-3.0F, 0.0F, -2.0F, 6, 6, 2, 0.0F);
        this.rightarm = new ModelRenderer(this, 40, 12);
        this.rightarm.setRotationPoint(-4.3F, -3.0F, 0.0F);
        this.rightarm.addBox(-1.0F, -1.5F, -1.0F, 2, 15, 2, 0.0F);
        this.helmet = new ModelRenderer(this, 32, 0);
        this.helmet.setRotationPoint(0.0F, -12.5F, -0.5F);
        this.helmet.addBox(-2.5F, -5.0F, -2.5F, 5, 5, 5, 0.2F);
        this.leftpantleg = new ModelRenderer(this, 9, 48);
        this.leftpantleg.setRotationPoint(0.5F, 8.0F, 1.0F);
        this.leftpantleg.addBox(0.0F, 0.0F, -2.0F, 2, 16, 2, 0.2F);
        this.leftleg = new ModelRenderer(this, 19, 48);
        this.leftleg.setRotationPoint(0.5F, 7.9F, 1.0F);
        this.leftleg.addBox(0.0F, 0.0F, -2.0F, 2, 16, 2, 0.0F);
        this.rightpantleg = new ModelRenderer(this, 9, 30);
        this.rightpantleg.setRotationPoint(-0.5F, 8.0F, 1.0F);
        this.rightpantleg.addBox(-2.0F, 0.0F, -2.0F, 2, 16, 2, 0.2F);
        this.neckoverlay = new ModelRenderer(this, 51, 14);
        this.neckoverlay.setRotationPoint(0.0F, -1.1F, 0.0F);
        this.neckoverlay.addBox(-1.0F, -13.0F, -1.0F, 2, 13, 2, 0.2F);
        this.neck = new ModelRenderer(this, 51, 32);
        this.neck.setRotationPoint(0.0F, -1.1F, 0.0F);
        this.neck.addBox(-1.0F, -13.0F, -1.0F, 2, 13, 2, 0.0F);
        this.leftarm = new ModelRenderer(this, 29, 48);
        this.leftarm.setRotationPoint(4.3F, -3.0F, 0.0F);
        this.leftarm.addBox(-1.0F, -1.5F, -1.0F, 2, 15, 2, 0.0F);
        this.rightleg = new ModelRenderer(this, 8, 12);
        this.rightleg.setRotationPoint(-0.5F, 7.9F, 1.0F);
        this.rightleg.addBox(-2.0F, 0.0F, -2.0F, 2, 16, 2, 0.0F);
        this.leftsleeve = new ModelRenderer(this, 40, 48);
        this.leftsleeve.setRotationPoint(4.3F, -3.0F, 0.0F);
        this.leftsleeve.addBox(-1.0F, -1.5F, -1.0F, 2, 15, 2, 0.2F);
        this.Head_1 = new ModelRenderer(this, 56, 0);
        this.Head_1.setRotationPoint(0.0F, -6.5F, -1.0F);
        this.Head_1.addBox(0.0F, 0.0F, 0.0F, 0, 7, 5, 0.0F);
        this.torso.addChild(this.torsolower);
        this.torso.addChild(this.Chest);
        this.Head.addChild(this.Head_1);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.jacket.render(f5);
        this.rightsleeve.render(f5);
        this.Head.render(f5);
        this.torso.render(f5);
        this.rightarm.render(f5);
        this.helmet.render(f5);
        this.leftpantleg.render(f5);
        this.leftleg.render(f5);
        this.rightpantleg.render(f5);
        this.neckoverlay.render(f5);
        this.neck.render(f5);
        this.leftarm.render(f5);
        this.rightleg.render(f5);
        this.leftsleeve.render(f5);
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
