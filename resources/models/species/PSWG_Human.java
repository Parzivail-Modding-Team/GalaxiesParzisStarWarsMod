package Java;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * PSWG_Human - Sindavar
 * Created using Tabula 7.1.0
 */
public class PSWG_Human extends ModelBase {
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
    public ModelRenderer Chest;

    public PSWG_Human() {
        this.textureWidth = 96;
        this.textureHeight = 96;
        this.Head = new ModelRenderer(this, 0, 0);
        this.Head.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
        this.leftpantleg = new ModelRenderer(this, 0, 48);
        this.leftpantleg.setRotationPoint(2.0F, 12.0F, 0.0F);
        this.leftpantleg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.2F);
        this.leftleg = new ModelRenderer(this, 16, 48);
        this.leftleg.setRotationPoint(1.9F, 12.0F, 0.0F);
        this.leftleg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
        this.Hair = new ModelRenderer(this, 11, 65);
        this.Hair.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Hair.addBox(-4.0F, -8.0F, -4.0F, 8, 17, 8, 0.1F);
        this.rightpantleg = new ModelRenderer(this, 0, 32);
        this.rightpantleg.setRotationPoint(-2.0F, 12.0F, 0.0F);
        this.rightpantleg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.2F);
        this.rightleg = new ModelRenderer(this, 0, 16);
        this.rightleg.setRotationPoint(-1.9F, 12.0F, 0.0F);
        this.rightleg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
        this.rightsleeve = new ModelRenderer(this, 40, 32);
        this.rightsleeve.setRotationPoint(-5.0F, 2.0F, 0.0F);
        this.rightsleeve.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, 0.2F);
        this.jacket = new ModelRenderer(this, 16, 32);
        this.jacket.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.jacket.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.2F);
        this.leftsleeve = new ModelRenderer(this, 48, 48);
        this.leftsleeve.setRotationPoint(5.0F, 2.0F, 0.0F);
        this.leftsleeve.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, 0.2F);
        this.Chest = new ModelRenderer(this, 0, 65);
        this.Chest.setRotationPoint(0.0F, -0.1F, 1.0F);
        this.Chest.addBox(-3.0F, 2.0F, -4.0F, 6, 3, 2, 0.0F);
        this.rightarm = new ModelRenderer(this, 40, 16);
        this.rightarm.setRotationPoint(-5.0F, 2.0F, 0.0F);
        this.rightarm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F);
        this.torso = new ModelRenderer(this, 16, 16);
        this.torso.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.torso.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);
        this.leftarm = new ModelRenderer(this, 32, 48);
        this.leftarm.setRotationPoint(5.0F, 2.0F, 0.0F);
        this.leftarm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F);
        this.helmet = new ModelRenderer(this, 32, 0);
        this.helmet.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.helmet.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.2F);
        this.torso.addChild(this.Chest);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.Head.render(f5);
        this.leftpantleg.render(f5);
        this.leftleg.render(f5);
        this.Hair.render(f5);
        this.rightpantleg.render(f5);
        this.rightleg.render(f5);
        this.rightsleeve.render(f5);
        this.jacket.render(f5);
        this.leftsleeve.render(f5);
        this.rightarm.render(f5);
        this.torso.render(f5);
        this.leftarm.render(f5);
        this.helmet.render(f5);
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
