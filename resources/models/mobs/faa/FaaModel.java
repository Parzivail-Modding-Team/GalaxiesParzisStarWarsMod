package idkwhatyouwantmetoputhereosrry;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Faa - Coda
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class FaaModel<T extends Entity> extends EntityModel<T> {
    public ModelRenderer body;
    public ModelRenderer hornRight;
    public ModelRenderer hornLeft;
    public ModelRenderer tailBase;
    public ModelRenderer analFin;
    public ModelRenderer pectoralFinRight;
    public ModelRenderer pectoralFinLeft;
    public ModelRenderer crest;
    public ModelRenderer eyeLeft;
    public ModelRenderer eyeRight;
    public ModelRenderer tail;
    public ModelRenderer caudalFin;

    public FaaModel() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.body = new ModelRenderer(this, 0, 0);
        this.body.setRotationPoint(0.0F, 21.0F, 0.0F);
        this.body.addBox(-1.5F, -3.0F, -2.5F, 3.0F, 6.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.pectoralFinLeft = new ModelRenderer(this, 17, 0);
        this.pectoralFinLeft.setRotationPoint(1.5F, 1.5F, -1.0F);
        this.pectoralFinLeft.addBox(0.0F, -1.0F, 0.0F, 3.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.hornLeft = new ModelRenderer(this, 16, 3);
        this.hornLeft.setRotationPoint(0.5F, -2.0F, -2.5F);
        this.hornLeft.addBox(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(hornLeft, -0.3490658503988659F, -0.3490658503988659F, 0.0F);
        this.analFin = new ModelRenderer(this, 0, -1);
        this.analFin.setRotationPoint(0.0F, 1.5F, 2.5F);
        this.analFin.addBox(0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(analFin, -0.2617993877991494F, 0.0F, 0.0F);
        this.eyeLeft = new ModelRenderer(this, 0, 2);
        this.eyeLeft.setRotationPoint(-1.3F, -1.5F, -2.2F);
        this.eyeLeft.addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(eyeLeft, -0.3490658503988659F, 0.4363323129985824F, -0.2617993877991494F);
        this.hornRight = new ModelRenderer(this, 16, 3);
        this.hornRight.setRotationPoint(-0.5F, -2.0F, -2.5F);
        this.hornRight.addBox(-1.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(hornRight, -0.3490658503988659F, 0.3490658503988659F, 0.0F);
        this.crest = new ModelRenderer(this, 14, 7);
        this.crest.setRotationPoint(0.0F, -3.0F, -0.5F);
        this.crest.addBox(-0.5F, -1.0F, -2.0F, 1.0F, 1.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.eyeRight = new ModelRenderer(this, 0, 2);
        this.eyeRight.setRotationPoint(1.3F, -1.5F, -2.2F);
        this.eyeRight.addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(eyeRight, -0.3490658503988659F, -0.4363323129985824F, 0.2617993877991494F);
        this.tailBase = new ModelRenderer(this, 11, 0);
        this.tailBase.setRotationPoint(0.0F, -0.5F, 2.5F);
        this.tailBase.addBox(-1.0F, -1.5F, 0.0F, 2.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.tail = new ModelRenderer(this, 0, 11);
        this.tail.setRotationPoint(0.0F, 0.0F, 0.5F);
        this.tail.addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(tail, -0.17453292519943295F, 0.0F, 0.0F);
        this.caudalFin = new ModelRenderer(this, 6, 7);
        this.caudalFin.mirror = true;
        this.caudalFin.setRotationPoint(0.0F, 0.0F, 3.5F);
        this.caudalFin.addBox(0.0F, -1.5F, -0.5F, 0.0F, 3.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.pectoralFinRight = new ModelRenderer(this, 17, 0);
        this.pectoralFinRight.mirror = true;
        this.pectoralFinRight.setRotationPoint(-1.5F, 1.5F, -1.0F);
        this.pectoralFinRight.addBox(-3.0F, -1.0F, 0.0F, 3.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(pectoralFinRight, -0.03909537541112055F, 0.0F, 0.0F);
        this.body.addChild(this.pectoralFinLeft);
        this.body.addChild(this.hornLeft);
        this.body.addChild(this.analFin);
        this.body.addChild(this.eyeLeft);
        this.body.addChild(this.hornRight);
        this.body.addChild(this.crest);
        this.body.addChild(this.eyeRight);
        this.body.addChild(this.tailBase);
        this.tailBase.addChild(this.tail);
        this.tail.addChild(this.caudalFin);
        this.body.addChild(this.pectoralFinRight);
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
