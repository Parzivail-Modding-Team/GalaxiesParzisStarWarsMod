package my.first.mod.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Laa - Coda
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class LaaModel<T extends Entity> extends EntityModel<T> {
    public ModelRenderer body;
    public ModelRenderer head;
    public ModelRenderer pectoralFinLeft;
    public ModelRenderer analFin;
    public ModelRenderer tail;
    public ModelRenderer flailTop;
    public ModelRenderer flailBottom;
    public ModelRenderer pectoralFinLeft_1;
    public ModelRenderer mouth;
    public ModelRenderer eyeRight;
    public ModelRenderer eyeLeft;
    public ModelRenderer tail_1;

    public LaaModel() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.flailBottom = new ModelRenderer(this, 0, 9);
        this.flailBottom.setRotationPoint(0.0F, 1.0F, -2.5F);
        this.flailBottom.addBox(0.0F, 0.0F, -1.0F, 0.0F, 3.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(flailBottom, -0.3490658503988659F, 0.0F, 0.0F);
        this.mouth = new ModelRenderer(this, 15, 6);
        this.mouth.setRotationPoint(0.0F, 1.0F, -1.0F);
        this.mouth.addBox(-0.5F, -1.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.eyeLeft = new ModelRenderer(this, 0, 0);
        this.eyeLeft.setRotationPoint(0.8F, -0.5F, -1.9F);
        this.eyeLeft.addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(eyeLeft, -0.17453292519943295F, -0.6981317007977318F, 0.0F);
        this.pectoralFinLeft_1 = new ModelRenderer(this, 0, 18);
        this.pectoralFinLeft_1.setRotationPoint(-1.5F, -0.5F, -1.5F);
        this.pectoralFinLeft_1.addBox(0.0F, -0.5F, 0.0F, 0.0F, 2.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(pectoralFinLeft_1, 0.2617993877991494F, -0.3490658503988659F, 0.0F);
        this.tail_1 = new ModelRenderer(this, 0, 3);
        this.tail_1.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.tail_1.addBox(0.0F, -2.5F, 0.0F, 0.0F, 5.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.head = new ModelRenderer(this, 12, 11);
        this.head.setRotationPoint(0.0F, -0.5F, -2.0F);
        this.head.addBox(-1.0F, -1.5F, -2.0F, 2.0F, 3.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.analFin = new ModelRenderer(this, 0, 1);
        this.analFin.setRotationPoint(0.0F, 0.5F, 2.0F);
        this.analFin.addBox(0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(analFin, -0.2617993877991494F, 0.0F, 0.0F);
        this.tail = new ModelRenderer(this, 14, 0);
        this.tail.setRotationPoint(0.0F, -1.0F, 2.0F);
        this.tail.addBox(-0.5F, -1.0F, 0.0F, 1.0F, 2.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.flailTop = new ModelRenderer(this, 0, 13);
        this.flailTop.setRotationPoint(0.0F, -3.0F, -1.0F);
        this.flailTop.addBox(0.0F, -3.0F, -0.5F, 0.0F, 3.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.body = new ModelRenderer(this, 0, 0);
        this.body.setRotationPoint(0.0F, 22.0F, 0.0F);
        this.body.addBox(-1.5F, -3.0F, -2.0F, 3.0F, 5.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.pectoralFinLeft = new ModelRenderer(this, 0, 18);
        this.pectoralFinLeft.setRotationPoint(1.5F, -0.5F, -1.5F);
        this.pectoralFinLeft.addBox(0.0F, -0.5F, 0.0F, 0.0F, 2.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(pectoralFinLeft, 0.2617993877991494F, 0.3490658503988659F, 0.0F);
        this.eyeRight = new ModelRenderer(this, 0, 0);
        this.eyeRight.mirror = true;
        this.eyeRight.setRotationPoint(-0.8F, -0.5F, -1.9F);
        this.eyeRight.addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(eyeRight, -0.17453292519943295F, 0.6981317007977318F, 0.0F);
        this.body.addChild(this.flailBottom);
        this.head.addChild(this.mouth);
        this.head.addChild(this.eyeLeft);
        this.body.addChild(this.pectoralFinLeft_1);
        this.tail.addChild(this.tail_1);
        this.body.addChild(this.head);
        this.body.addChild(this.analFin);
        this.body.addChild(this.tail);
        this.body.addChild(this.flailTop);
        this.body.addChild(this.pectoralFinLeft);
        this.head.addChild(this.eyeRight);
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
