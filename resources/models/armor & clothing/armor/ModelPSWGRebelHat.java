package RebelHat.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * ModelPSWGRebel - Sindavar
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class ModelPSWGRebel<T extends Entity> extends EntityModel<T> {
    public ModelRenderer field_178720_f;
    public ModelRenderer shape15;
    public ModelRenderer shape16;

    public ModelPSWGRebel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.field_178720_f = new ModelRenderer(this, 32, 0);
        this.field_178720_f.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.field_178720_f.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.5F, 0.5F, 0.5F);
        this.shape16 = new ModelRenderer(this, 0, 7);
        this.shape16.setRotationPoint(-0.5F, -8.9F, -0.7F);
        this.shape16.addBox(-4.5F, 1.5F, -4.5F, 10.0F, 4.0F, 11.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(shape16, -0.18203784630933073F, 0.0F, 0.0F);
        this.shape15 = new ModelRenderer(this, 7, 0);
        this.shape15.setRotationPoint(-4.5F, -4.6F, -7.4F);
        this.shape15.addBox(0.0F, 0.0F, 0.0F, 9.0F, 1.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.field_178720_f.addChild(this.shape16);
        this.field_178720_f.addChild(this.shape15);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.field_178720_f).forEach((modelRenderer) -> { 
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
