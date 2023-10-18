// Made with Blockbench 4.2.4
// Exported for Minecraft version 1.17 - 1.18 with Mojang mappings
// Paste this class into your mod and generate all required imports


public class PSWG_Stormtrooper<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "pswg_stormtrooper"), "main");
	private final ModelPart hat;
	private final ModelPart left_arm;
	private final ModelPart right_arm;
	private final ModelPart left_leg;
	private final ModelPart right_leg;
	private final ModelPart head;
	private final ModelPart body;

	public PSWG_Stormtrooper(ModelPart root) {
		this.hat = root.getChild("hat");
		this.left_arm = root.getChild("left_arm");
		this.right_arm = root.getChild("right_arm");
		this.left_leg = root.getChild("left_leg");
		this.right_leg = root.getChild("right_leg");
		this.head = root.getChild("head");
		this.body = root.getChild("body");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.6F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5.0F, 2.0F, 0.0F));

		PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));

		PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition forehead = head.addOrReplaceChild("forehead", CubeListBuilder.create().texOffs(29, 29).addBox(-4.25F, -32.25F, -4.25F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.3F))
		.texOffs(0, 32).addBox(1.25F, -32.25F, -4.25F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition lekku1 = head.addOrReplaceChild("lekku1", CubeListBuilder.create().texOffs(4, 0).addBox(4.75F, 9.85F, 0.25F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.3F)), PartPose.offset(-2.75F, -6.5F, 4.0F));

		PartDefinition head_r1 = lekku1.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(24, 16).addBox(-2.0F, -2.0218F, -2.0167F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.25F, 1.0F, -0.75F, 0.2618F, 0.0F, 0.0F));

		PartDefinition head_r2 = lekku1.addOrReplaceChild("head_r2", CubeListBuilder.create().texOffs(20, 32).addBox(-1.0F, 0.2835F, -1.0561F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(5.25F, 2.225F, 0.0F, 0.0873F, 0.0F, 0.0F));

		PartDefinition lekku2 = head.addOrReplaceChild("lekku2", CubeListBuilder.create().texOffs(4, 0).mirror().addBox(-5.75F, 9.85F, 0.25F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.3F)).mirror(false), PartPose.offset(2.75F, -6.5F, 4.0F));

		PartDefinition head_r3 = lekku2.addOrReplaceChild("head_r3", CubeListBuilder.create().texOffs(24, 16).mirror().addBox(-2.0F, -2.0218F, -2.0167F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-5.25F, 1.0F, -0.75F, 0.2618F, 0.0F, 0.0F));

		PartDefinition head_r4 = lekku2.addOrReplaceChild("head_r4", CubeListBuilder.create().texOffs(20, 32).mirror().addBox(-1.0F, 0.2835F, -1.0561F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.3F)).mirror(false), PartPose.offsetAndRotation(-5.25F, 2.225F, 0.0F, 0.0873F, 0.0F, 0.0F));

		PartDefinition ear1 = head.addOrReplaceChild("ear1", CubeListBuilder.create(), PartPose.offsetAndRotation(3.725F, -3.75F, -0.25F, 0.0F, 0.0F, -0.2182F));

		PartDefinition bone2 = ear1.addOrReplaceChild("bone2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

		PartDefinition head_r5 = bone2.addOrReplaceChild("head_r5", CubeListBuilder.create().texOffs(0, 5).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.4F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

		PartDefinition ear2 = head.addOrReplaceChild("ear2", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.725F, -3.75F, -0.25F, 0.0F, 0.0F, 0.2182F));

		PartDefinition bone5 = ear2.addOrReplaceChild("bone5", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition head_r6 = bone5.addOrReplaceChild("head_r6", CubeListBuilder.create().texOffs(0, 5).mirror().addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.4F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		hat.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		left_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		right_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		left_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		right_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}