// Made with Blockbench 4.1.4
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports


public class PSWG_Stormtrooper<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "pswg_stormtrooper"), "main");
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

	public PSWG_Stormtrooper(ModelPart root) {
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

		PartDefinition helmet = partdefinition.addOrReplaceChild("helmet", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.6F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition leftsleeve = partdefinition.addOrReplaceChild("leftsleeve", CubeListBuilder.create().texOffs(52, 28).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(5.0F, 2.0F, 0.0F));

		PartDefinition leftarm = partdefinition.addOrReplaceChild("leftarm", CubeListBuilder.create().texOffs(52, 44).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(5.0F, 2.0F, 0.0F));

		PartDefinition leftarm2 = leftarm.addOrReplaceChild("leftarm2", CubeListBuilder.create().texOffs(15, 82).addBox(-1.5F, -2.75F, 0.0F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.75F, 0.25F, -3.0F, 0.0F, -0.3054F, 0.0F));

		PartDefinition rightsleeve = partdefinition.addOrReplaceChild("rightsleeve", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition rightarm = partdefinition.addOrReplaceChild("rightarm", CubeListBuilder.create().texOffs(56, 0).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition leftpantleg = partdefinition.addOrReplaceChild("leftpantleg", CubeListBuilder.create().texOffs(44, 12).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(2.0F, 12.0F, 0.0F));

		PartDefinition leftleg = partdefinition.addOrReplaceChild("leftleg", CubeListBuilder.create().texOffs(20, 44).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(1.9F, 12.0F, 0.0F));

		PartDefinition rightpantleg = partdefinition.addOrReplaceChild("rightpantleg", CubeListBuilder.create().texOffs(36, 44).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(-2.0F, 12.0F, 0.0F));

		PartDefinition rightleg = partdefinition.addOrReplaceChild("rightleg", CubeListBuilder.create().texOffs(32, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(-1.9F, 12.0F, 0.0F));

		PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition Head2 = Head.addOrReplaceChild("Head2", CubeListBuilder.create().texOffs(9, 59).addBox(0.25F, 0.0F, 0.25F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -1.6F, -5.4F, 0.3187F, 0.0911F, 0.0F));

		PartDefinition Head3 = Head.addOrReplaceChild("Head3", CubeListBuilder.create().texOffs(9, 59).mirror().addBox(-3.25F, 0.0F, 0.25F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-1.5F, -1.6F, -5.4F, 0.3187F, -0.0911F, 0.0F));

		PartDefinition jacket = partdefinition.addOrReplaceChild("jacket", CubeListBuilder.create().texOffs(0, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition torso = partdefinition.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(28, 28).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition torso3 = torso.addOrReplaceChild("torso3", CubeListBuilder.create().texOffs(35, 88).addBox(-5.5F, -5.75F, 11.0F, 9.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(75, 109).addBox(-5.5F, -5.75F, 12.0F, 9.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(86, 85).addBox(-6.5F, -9.75F, 11.5F, 1.0F, 16.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(68, 84).addBox(3.5F, -6.75F, 11.5F, 1.0F, 11.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(58, 105).addBox(-7.5F, 4.25F, 12.5F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(82, 39).addBox(-1.5F, 0.25F, 12.0F, 4.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(68, 63).addBox(-5.0F, 1.5F, 11.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(63, 76).addBox(-4.5F, 3.5F, 12.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(17, 102).addBox(-3.5F, -0.75F, 11.75F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(88, 56).addBox(-3.75F, -5.75F, 14.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(68, 52).addBox(-1.5F, -4.75F, 14.0F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 6.25F, -8.5F));

		PartDefinition torso2 = torso.addOrReplaceChild("torso2", CubeListBuilder.create().texOffs(20, 68).addBox(-8.5F, -0.75F, -10.0F, 14.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 7.0F));

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