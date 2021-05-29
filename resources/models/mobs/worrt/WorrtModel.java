package yeah;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Worrt - Coda
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class WorrtModel<T extends Entity> extends EntityModel<T> {
    public ModelRenderer body;
    public ModelRenderer jaw;
    public ModelRenderer armRight1;
    public ModelRenderer armLeft1;
    public ModelRenderer legLeft1;
    public ModelRenderer legRight1;
    public ModelRenderer spikes;
    public ModelRenderer spikes_1;
    public ModelRenderer spikes_2;
    public ModelRenderer head;
    public ModelRenderer tongue;
    public ModelRenderer tongueTip;
    public ModelRenderer armRight2;
    public ModelRenderer handRight;
    public ModelRenderer armLeft2;
    public ModelRenderer handLeft;
    public ModelRenderer legLeft2;
    public ModelRenderer footLeft;
    public ModelRenderer legRight2;
    public ModelRenderer footRight;
    public ModelRenderer eyeRight;
    public ModelRenderer eyeLeft;
    public ModelRenderer antennaLeft1;
    public ModelRenderer antennaRight1;
    public ModelRenderer antennaLeft2;
    public ModelRenderer antennaRight2;

    public WorrtModel() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.handRight = new ModelRenderer(this, 17, 26);
        this.handRight.setRotationPoint(0.0F, 5.0F, 0.0F);
        this.handRight.addBox(-1.5F, -1.0F, -0.5F, 3.0F, 4.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(handRight, 0.08726646259971647F, 0.0F, 0.0F);
        this.head = new ModelRenderer(this, 25, 0);
        this.head.setRotationPoint(0.0F, -0.5F, -4.0F);
        this.head.addBox(-3.0F, -2.5F, -3.0F, 6.0F, 3.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.antennaRight1 = new ModelRenderer(this, 0, 0);
        this.antennaRight1.setRotationPoint(-2.0F, -3.0F, 0.0F);
        this.antennaRight1.addBox(-0.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(antennaRight1, 0.4363323129985824F, 0.6981317007977318F, 0.0F);
        this.legLeft2 = new ModelRenderer(this, 12, 17);
        this.legLeft2.mirror = true;
        this.legLeft2.setRotationPoint(0.0F, -5.5F, -1.5F);
        this.legLeft2.addBox(-1.0F, 0.0F, -0.5F, 2.0F, 6.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(legLeft2, -0.7853981633974483F, 0.0F, 0.0F);
        this.legRight2 = new ModelRenderer(this, 12, 17);
        this.legRight2.setRotationPoint(0.0F, -5.5F, -1.5F);
        this.legRight2.addBox(-1.0F, 0.0F, -0.5F, 2.0F, 6.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(legRight2, -0.7853981633974483F, 0.0F, 0.0F);
        this.handLeft = new ModelRenderer(this, 17, 26);
        this.handLeft.mirror = true;
        this.handLeft.setRotationPoint(0.0F, 5.0F, 0.0F);
        this.handLeft.addBox(-1.5F, -1.0F, -0.5F, 3.0F, 4.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(handLeft, 0.08726646259971647F, 0.0F, 0.0F);
        this.spikes_2 = new ModelRenderer(this, 0, 20);
        this.spikes_2.setRotationPoint(2.0F, -4.0F, 0.0F);
        this.spikes_2.addBox(0.0F, -4.0F, -4.0F, 0.0F, 4.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(spikes_2, 0.0F, -0.08726646259971647F, 0.2617993877991494F);
        this.eyeLeft = new ModelRenderer(this, 34, 8);
        this.eyeLeft.setRotationPoint(3.5F, -1.0F, -0.5F);
        this.eyeLeft.addBox(-1.5F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(eyeLeft, 0.0F, -0.3490658503988659F, 0.0F);
        this.armRight1 = new ModelRenderer(this, 22, 17);
        this.armRight1.setRotationPoint(-3.5F, 0.0F, -2.0F);
        this.armRight1.addBox(-2.0F, -1.0F, -1.0F, 2.0F, 6.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(armRight1, -0.17453292519943295F, 0.0F, 0.17453292519943295F);
        this.tongue = new ModelRenderer(this, 44, 8);
        this.tongue.setRotationPoint(0.0F, -0.5F, 0.0F);
        this.tongue.addBox(-0.5F, -0.5F, -1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(tongue, 0.0F, 3.141592653589793F, 0.0F);
        this.tongueTip = new ModelRenderer(this, 46, 8);
        this.tongueTip.setRotationPoint(0.0F, 0.0F, -1.0F);
        this.tongueTip.addBox(-1.0F, -1.0F, -2.0F, 2.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(tongueTip, 0.0F, 3.141592653589793F, 0.0F);
        this.armLeft1 = new ModelRenderer(this, 22, 17);
        this.armLeft1.setRotationPoint(3.5F, 0.0F, -2.0F);
        this.armLeft1.addBox(0.0F, -1.0F, -1.0F, 2.0F, 6.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(armLeft1, -0.17453292519943295F, 0.0F, -0.17453292519943295F);
        this.eyeRight = new ModelRenderer(this, 34, 8);
        this.eyeRight.mirror = true;
        this.eyeRight.setRotationPoint(-3.5F, -1.0F, -0.5F);
        this.eyeRight.addBox(-1.5F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(eyeRight, 0.0F, 0.3490658503988659F, 0.0F);
        this.armLeft2 = new ModelRenderer(this, 30, 17);
        this.armLeft2.setRotationPoint(1.0F, 5.0F, 0.0F);
        this.armLeft2.addBox(-1.0F, -1.0F, -1.0F, 2.0F, 6.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(armLeft2, -1.3089969389957472F, 0.08726646259971647F, 0.0F);
        this.footRight = new ModelRenderer(this, 17, 26);
        this.footRight.setRotationPoint(0.0F, 6.0F, 1.5F);
        this.footRight.addBox(-1.5F, -1.0F, -0.5F, 3.0F, 4.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(footRight, -1.0821041362364843F, 0.0F, 0.0F);
        this.body = new ModelRenderer(this, 0, 0);
        this.body.setRotationPoint(0.0F, 18.0F, 1.0F);
        this.body.addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 9.0F, 0.0F, 0.0F, 0.0F);
        this.spikes_1 = new ModelRenderer(this, 0, 22);
        this.spikes_1.setRotationPoint(-2.0F, -4.0F, 0.0F);
        this.spikes_1.addBox(0.0F, -2.0F, -4.0F, 0.0F, 2.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(spikes_1, 0.0F, 0.08726646259971647F, -0.2617993877991494F);
        this.antennaLeft1 = new ModelRenderer(this, 0, 0);
        this.antennaLeft1.setRotationPoint(2.0F, -3.0F, 0.0F);
        this.antennaLeft1.addBox(-0.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(antennaLeft1, 0.4363323129985824F, -0.6981317007977318F, 0.0F);
        this.spikes = new ModelRenderer(this, 26, 22);
        this.spikes.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.spikes.addBox(0.0F, -2.0F, -4.0F, 0.0F, 2.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.antennaLeft2 = new ModelRenderer(this, 0, 2);
        this.antennaLeft2.setRotationPoint(0.0F, -3.0F, 0.5F);
        this.antennaLeft2.addBox(-0.5F, -3.0F, -1.0F, 1.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(antennaLeft2, 0.8991936386169619F, 0.0F, 0.0F);
        this.armRight2 = new ModelRenderer(this, 30, 17);
        this.armRight2.setRotationPoint(-1.0F, 5.0F, 0.0F);
        this.armRight2.addBox(-1.0F, -1.0F, -1.0F, 2.0F, 6.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(armRight2, -1.3089969389957472F, -0.08726646259971647F, 0.0F);
        this.footLeft = new ModelRenderer(this, 17, 26);
        this.footLeft.mirror = true;
        this.footLeft.setRotationPoint(0.0F, 6.0F, 1.5F);
        this.footLeft.addBox(-1.5F, -1.0F, -0.5F, 3.0F, 4.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(footLeft, -1.0821041362364843F, 0.0F, 0.0F);
        this.antennaRight2 = new ModelRenderer(this, 0, 2);
        this.antennaRight2.setRotationPoint(0.0F, -3.0F, 0.5F);
        this.antennaRight2.addBox(-0.5F, -3.0F, -1.0F, 1.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(antennaRight2, 0.8991936386169619F, 0.0F, 0.0F);
        this.jaw = new ModelRenderer(this, 39, 2);
        this.jaw.setRotationPoint(0.0F, 0.5F, -4.0F);
        this.jaw.addBox(-3.5F, -0.5F, -3.5F, 7.0F, 2.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.legLeft1 = new ModelRenderer(this, 0, 17);
        this.legLeft1.setRotationPoint(3.5F, 4.0F, 4.0F);
        this.legLeft1.addBox(-1.5F, -6.0F, -2.0F, 3.0F, 6.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(legLeft1, 0.3490658503988659F, -0.6981317007977318F, 0.0F);
        this.legRight1 = new ModelRenderer(this, 0, 17);
        this.legRight1.setRotationPoint(-3.5F, 4.0F, 4.0F);
        this.legRight1.addBox(-1.5F, -6.0F, -2.0F, 3.0F, 6.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(legRight1, 0.3490658503988659F, 0.6981317007977318F, 0.0F);
        this.armRight2.addChild(this.handRight);
        this.body.addChild(this.head);
        this.head.addChild(this.antennaRight1);
        this.legLeft1.addChild(this.legLeft2);
        this.legRight1.addChild(this.legRight2);
        this.armLeft2.addChild(this.handLeft);
        this.body.addChild(this.spikes_2);
        this.head.addChild(this.eyeLeft);
        this.body.addChild(this.armRight1);
        this.jaw.addChild(this.tongue);
        this.tongue.addChild(this.tongueTip);
        this.body.addChild(this.armLeft1);
        this.head.addChild(this.eyeRight);
        this.armLeft1.addChild(this.armLeft2);
        this.legRight2.addChild(this.footRight);
        this.body.addChild(this.spikes_1);
        this.head.addChild(this.antennaLeft1);
        this.body.addChild(this.spikes);
        this.antennaLeft1.addChild(this.antennaLeft2);
        this.armRight1.addChild(this.armRight2);
        this.legLeft2.addChild(this.footLeft);
        this.antennaRight1.addChild(this.antennaRight2);
        this.body.addChild(this.jaw);
        this.body.addChild(this.legLeft1);
        this.body.addChild(this.legRight1);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.body).forEach((modelRenderer) -> { 
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
