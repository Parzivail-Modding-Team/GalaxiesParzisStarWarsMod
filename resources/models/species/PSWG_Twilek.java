package Java;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * PSWG_Twilek - Sindavar
 * Created using Tabula 7.1.0
 */
public class PSWG_Twilek extends ModelBase {
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
    public ModelRenderer FrontalL;
    public ModelRenderer FrontalR;
    public ModelRenderer SpikeL;
    public ModelRenderer TailBaseR;
    public ModelRenderer TailBaseL;
    public ModelRenderer SpikeR;
    public ModelRenderer TailMidR;
    public ModelRenderer TailLowerL;
    public ModelRenderer TailMidL;
    public ModelRenderer TailLowerL_1;
    public ModelRenderer Chest;

    public PSWG_Twilek() {
        this.textureWidth = 96;
        this.textureHeight = 96;
        this.leftsleeve = new ModelRenderer(this, 48, 48);
        this.leftsleeve.mirror = true;
        this.leftsleeve.setRotationPoint(5.0F, 2.0F, 0.0F);
        this.leftsleeve.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, 0.2F);
        this.TailLowerL_1 = new ModelRenderer(this, 47, 66);
        this.TailLowerL_1.setRotationPoint(0.5F, 11.5F, 1.7F);
        this.TailLowerL_1.addBox(0.0F, 0.0F, 0.0F, 2, 6, 2, 0.0F);
        this.setRotateAngle(TailLowerL_1, -0.08866272600131193F, 0.0F, 0.0F);
        this.TailMidR = new ModelRenderer(this, 33, 66);
        this.TailMidR.mirror = true;
        this.TailMidR.setRotationPoint(0.4F, -0.2F, 0.9F);
        this.TailMidR.addBox(0.0F, 1.9F, 1.1F, 3, 10, 3, 0.0F);
        this.setRotateAngle(TailMidR, -0.08325220532012952F, 0.0F, 0.0F);
        this.leftleg = new ModelRenderer(this, 16, 48);
        this.leftleg.setRotationPoint(1.9F, 12.0F, 0.0F);
        this.leftleg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
        this.rightpantleg = new ModelRenderer(this, 0, 32);
        this.rightpantleg.setRotationPoint(-2.0F, 12.0F, 0.0F);
        this.rightpantleg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.2F);
        this.leftpantleg = new ModelRenderer(this, 0, 48);
        this.leftpantleg.setRotationPoint(2.0F, 12.0F, 0.0F);
        this.leftpantleg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.2F);
        this.FrontalR = new ModelRenderer(this, 2, 79);
        this.FrontalR.mirror = true;
        this.FrontalR.setRotationPoint(-4.3F, -8.6F, -4.4F);
        this.FrontalR.addBox(0.0F, 0.0F, 0.0F, 4, 4, 4, 0.0F);
        this.TailBaseR = new ModelRenderer(this, 14, 66);
        this.TailBaseR.mirror = true;
        this.TailBaseR.setRotationPoint(-4.5F, -7.2F, -0.2F);
        this.TailBaseR.addBox(0.0F, 0.0F, 0.0F, 4, 5, 5, 0.0F);
        this.setRotateAngle(TailBaseR, 0.18203784098300857F, 0.0F, 0.0F);
        this.SpikeR = new ModelRenderer(this, 4, 71);
        this.SpikeR.mirror = true;
        this.SpikeR.setRotationPoint(-4.8F, -3.5F, -1.8F);
        this.SpikeR.addBox(0.0F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
        this.setRotateAngle(SpikeR, 0.0F, 0.0F, -0.4461061568097506F);
        this.FrontalL = new ModelRenderer(this, 2, 79);
        this.FrontalL.setRotationPoint(0.3F, -8.6F, -4.4F);
        this.FrontalL.addBox(0.0F, 0.0F, 0.0F, 4, 4, 4, 0.0F);
        this.TailLowerL = new ModelRenderer(this, 47, 66);
        this.TailLowerL.mirror = true;
        this.TailLowerL.setRotationPoint(0.5F, 11.5F, 1.7F);
        this.TailLowerL.addBox(0.0F, 0.0F, 0.0F, 2, 6, 2, 0.0F);
        this.setRotateAngle(TailLowerL, -0.08866272600131193F, 0.0F, 0.0F);
        this.TailBaseL = new ModelRenderer(this, 14, 66);
        this.TailBaseL.setRotationPoint(0.5F, -7.2F, -0.1F);
        this.TailBaseL.addBox(0.0F, 0.0F, 0.0F, 4, 5, 5, 0.0F);
        this.setRotateAngle(TailBaseL, 0.18203784098300857F, 0.0F, 0.0F);
        this.head = new ModelRenderer(this, 0, 0);
        this.head.mirror = true;
        this.head.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
        this.SpikeL = new ModelRenderer(this, 4, 71);
        this.SpikeL.setRotationPoint(4.8F, -3.5F, -1.8F);
        this.SpikeL.addBox(-2.0F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
        this.setRotateAngle(SpikeL, 0.0F, 0.0F, 0.4406956361285682F);
        this.torso = new ModelRenderer(this, 16, 16);
        this.torso.mirror = true;
        this.torso.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.torso.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);
        this.rightarm = new ModelRenderer(this, 40, 16);
        this.rightarm.setRotationPoint(-5.0F, 2.0F, 0.0F);
        this.rightarm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F);
        this.rightsleeve = new ModelRenderer(this, 40, 32);
        this.rightsleeve.setRotationPoint(-5.0F, 2.0F, 0.0F);
        this.rightsleeve.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, 0.2F);
        this.TailMidL = new ModelRenderer(this, 33, 66);
        this.TailMidL.setRotationPoint(0.6F, -0.2F, 0.9F);
        this.TailMidL.addBox(0.0F, 1.9F, 1.1F, 3, 10, 3, 0.0F);
        this.setRotateAngle(TailMidL, -0.08325220532012952F, 0.0F, 0.0F);
        this.helmet = new ModelRenderer(this, 32, 0);
        this.helmet.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.helmet.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.2F);
        this.jacket = new ModelRenderer(this, 16, 32);
        this.jacket.mirror = true;
        this.jacket.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.jacket.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.2F);
        this.leftarm = new ModelRenderer(this, 32, 48);
        this.leftarm.setRotationPoint(5.0F, 2.0F, 0.0F);
        this.leftarm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F);
        this.rightleg = new ModelRenderer(this, 0, 16);
        this.rightleg.setRotationPoint(-1.9F, 12.0F, 0.0F);
        this.rightleg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
        this.Chest = new ModelRenderer(this, 0, 65);
        this.Chest.setRotationPoint(0.0F, -0.1F, 1.0F);
        this.Chest.addBox(-3.0F, 2.0F, -4.0F, 6, 3, 2, 0.0F);
        this.TailMidL.addChild(this.TailLowerL_1);
        this.TailBaseR.addChild(this.TailMidR);
        this.head.addChild(this.FrontalR);
        this.head.addChild(this.TailBaseR);
        this.head.addChild(this.SpikeR);
        this.head.addChild(this.FrontalL);
        this.TailMidR.addChild(this.TailLowerL);
        this.head.addChild(this.TailBaseL);
        this.head.addChild(this.SpikeL);
        this.TailBaseL.addChild(this.TailMidL);
        this.torso.addChild(this.Chest);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.leftsleeve.render(f5);
        this.leftleg.render(f5);
        this.rightpantleg.render(f5);
        this.leftpantleg.render(f5);
        this.head.render(f5);
        this.torso.render(f5);
        this.rightarm.render(f5);
        this.rightsleeve.render(f5);
        this.helmet.render(f5);
        this.jacket.render(f5);
        this.leftarm.render(f5);
        this.rightleg.render(f5);
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
