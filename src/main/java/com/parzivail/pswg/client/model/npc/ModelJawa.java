package com.parzivail.pswg.client.model.npc;

import com.parzivail.util.client.ModelPartUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;

/**
 * PSWGJawa - Sindavar
 * Created using Tabula 7.1.0
 */
@Environment(EnvType.CLIENT)
public class ModelJawa<T extends LivingEntity> extends PlayerEntityModel<T>
{
	private static final int TEXTURE_WIDTH = 96;
	private static final int TEXTURE_HEIGHT = 96;

	public ModelPart Hair;
	public ModelPart Hood;
	public ModelPart Hood2;

	public ModelJawa(float scale)
	{
		super(scale, true);

		textureWidth = TEXTURE_WIDTH;
		textureHeight = TEXTURE_HEIGHT;

		this.body = new ModelPart(this, 16, 16);
		this.body.setPivot(0.0F, 0.0F, 0.0F);
		this.body.addCuboid(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);
		this.hat = new ModelPart(this, 32, 0);
		this.hat.setPivot(0.0F, 0.0F, 0.0F);
		this.hat.addCuboid(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.2F);
		this.Hood2 = new ModelPart(this, 50, 74);
		this.Hood2.setPivot(0.0F, 0.0F, 7.0F);
		this.Hood2.addCuboid(-1.0F, 0.0F, 0.0F, 2, 2, 1, 0.0F);
		ModelPartUtil.setRotateAngle(Hood2, -0.136659280431156F, 0.0F, 0.0F);
		this.Hood = new ModelPart(this, 50, 80);
		this.Hood.setPivot(0.0F, -8.8F, -2.1F);
		this.Hood.addCuboid(-2.5F, 0.0F, 0.0F, 5, 6, 7, 0.0F);
		ModelPartUtil.setRotateAngle(Hood, 0.091106186954104F, 0.0F, 0.0F);
		this.Hair = new ModelPart(this, 11, 65);
		this.Hair.setPivot(0.0F, 0.0F, 0.0F);
		this.Hair.addCuboid(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.1F);
		this.Hood.addChild(this.Hood2);
		this.hat.addChild(this.Hood);
		this.head.addChild(this.Hair);
	}

	@Override
	public void accept(ModelPart modelPart)
	{
		modelPart.setTextureSize(TEXTURE_WIDTH, TEXTURE_HEIGHT);
		super.accept(modelPart);
	}

	public void setArmAngle(Arm arm, MatrixStack matrices)
	{
		ModelPart modelPart = this.getArm(arm);

		float f = 0.5f * 0.7f * (float)(arm == Arm.RIGHT ? 1 : -1);
		modelPart.pivotX += f;

		matrices.translate(0, 0.44f, 0);
		matrices.scale(0.7f, 0.7f, 0.7f);

		modelPart.rotate(matrices);

		modelPart.pivotX -= f;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha)
	{
		matrices.push();

		matrices.translate(0, 0.44f, 0);
		matrices.scale(0.7f, 0.7f, 0.7f);

		super.render(matrices, vertices, light, overlay, red, green, blue, alpha);

		matrices.pop();
	}
}
