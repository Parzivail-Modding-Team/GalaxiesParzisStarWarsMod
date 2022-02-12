// Made with Blockbench 4.1.4
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports


public class PSWG_Imperial_Pilot<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "pswg_imperial_pilot"), "main");
	private final ModelPart helmet;
	private final ModelPart leftsleeve;
	private final ModelPart leftarm;
	private final ModelPart rightsleeve;
	private final ModelPart rightarm;
	private final ModelPart leftpantleg;
	private final ModelPart leftleg;
	private final ModelPart rightpantleg;
	private final ModelPart rightleg;
	private final ModelPart Head;
	private final ModelPart jacket;
	private final ModelPart torso;

	public PSWG_Imperial_Pilot(ModelPart root) {
		this.helmet = root.getChild("helmet");
		this.leftsleeve = root.getChild("leftsleeve");
		this.leftarm = root.getChild("leftarm");
		this.rightsleeve = root.getChild("rightsleeve");
		this.rightarm = root.getChild("rightarm");
		this.leftpantleg = root.getChild("leftpantleg");
		this.leftleg = root.getChild("leftleg");
		this.rightpantleg = root.getChild("rightpantleg");
		this.rightleg = root.getChild("rightleg");
		this.Head = root.getChild("Head");
		this.jacket = root.getChild("jacket");
		this.torso = root.getChild("torso");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition helmet = partdefinition.addOrReplaceChild("helmet", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.6F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition leftsleeve = partdefinition.addOrReplaceChild("leftsleeve", CubeListBuilder.create().texOffs(54, 12).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(5.0F, 2.0F, 0.0F, 0.0F, 0.0F, -0.182F));

		PartDefinition leftarm = partdefinition.addOrReplaceChild("leftarm", CubeListBuilder.create().texOffs(52, 51).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(5.0F, 2.0F, 0.0F, 0.0F, 0.0F, -0.182F));

		PartDefinition rightsleeve = partdefinition.addOrReplaceChild("rightsleeve", CubeListBuilder.create().texOffs(30, 55).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(-5.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.182F));

		PartDefinition rightarm = partdefinition.addOrReplaceChild("rightarm", CubeListBuilder.create().texOffs(16, 55).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(-5.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.182F));

		PartDefinition leftpantleg = partdefinition.addOrReplaceChild("leftpantleg", CubeListBuilder.create().texOffs(24, 39).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(2.0F, 12.0F, 0.0F, 0.182F, 0.0F, 0.0F));

		PartDefinition leftleg = partdefinition.addOrReplaceChild("leftleg", CubeListBuilder.create().texOffs(0, 51).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(1.9F, 12.0F, 0.0F, 0.182F, 0.0F, 0.0F));

		PartDefinition rightpantleg = partdefinition.addOrReplaceChild("rightpantleg", CubeListBuilder.create().texOffs(40, 39).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(-2.0F, 12.0F, 0.0F, -0.182F, 0.0F, 0.0F));

		PartDefinition rightleg = partdefinition.addOrReplaceChild("rightleg", CubeListBuilder.create().texOffs(38, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(-1.9F, 12.0F, 0.0F, -0.182F, 0.0F, 0.0F));

		PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition shape30 = Head.addOrReplaceChild("shape30", CubeListBuilder.create().texOffs(65, 65).addBox(-1.5F, 0.0F, 5.0F, 3.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(63, 42).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -9.2F, -5.1F));

		PartDefinition shape31_1 = shape30.addOrReplaceChild("shape31_1", CubeListBuilder.create().texOffs(24, 0).addBox(0.0F, 0.0F, 0.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 2.4F, -0.3F, 0.4554F, 0.0F, 0.0F));

		PartDefinition shape28_1 = Head.addOrReplaceChild("shape28_1", CubeListBuilder.create().texOffs(56, 0).addBox(-3.5F, 0.0F, 0.25F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -1.6F, -5.4F, 0.3187F, -0.0911F, 0.0F));

		PartDefinition shape31_3 = shape28_1.addOrReplaceChild("shape31_3", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0263F, -0.0981F, -0.4785F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.4F, 1.2F, -0.2F, 0.0911F, 0.0F, 0.0456F));

		PartDefinition shape28 = Head.addOrReplaceChild("shape28", CubeListBuilder.create().texOffs(49, 28).addBox(0.5F, 0.0F, 0.25F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -1.6F, -5.4F, 0.3187F, 0.0911F, 0.0F));

		PartDefinition shape31_2 = shape28.addOrReplaceChild("shape31_2", CubeListBuilder.create().texOffs(4, 0).addBox(0.0263F, -0.0981F, -0.4785F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.4F, 1.3F, -0.2F, 0.0911F, 0.0F, -0.0456F));

		PartDefinition shape31 = Head.addOrReplaceChild("shape31", CubeListBuilder.create().texOffs(0, 32).addBox(-4.5F, 0.0F, 0.0F, 9.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.7F, -3.7F, 0.4554F, 0.0F, 0.0F));

		PartDefinition jacket = partdefinition.addOrReplaceChild("jacket", CubeListBuilder.create().texOffs(0, 35).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition torso = partdefinition.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition shape31_4 = torso.addOrReplaceChild("shape31_4", CubeListBuilder.create().texOffs(52, 36).addBox(-3.5F, 0.0F, 0.0F, 7.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.9F, -3.8F));

		PartDefinition shape31_5 = shape31_4.addOrReplaceChild("shape31_5", CubeListBuilder.create().texOffs(24, 16).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, 0.3F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		helmet.render(poseStack, buffer, packedLight, packedOverlay);
		leftsleeve.render(poseStack, buffer, packedLight, packedOverlay);
		leftarm.render(poseStack, buffer, packedLight, packedOverlay);
		rightsleeve.render(poseStack, buffer, packedLight, packedOverlay);
		rightarm.render(poseStack, buffer, packedLight, packedOverlay);
		leftpantleg.render(poseStack, buffer, packedLight, packedOverlay);
		leftleg.render(poseStack, buffer, packedLight, packedOverlay);
		rightpantleg.render(poseStack, buffer, packedLight, packedOverlay);
		rightleg.render(poseStack, buffer, packedLight, packedOverlay);
		Head.render(poseStack, buffer, packedLight, packedOverlay);
		jacket.render(poseStack, buffer, packedLight, packedOverlay);
		torso.render(poseStack, buffer, packedLight, packedOverlay);
	}
}