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

		PartDefinition rightsleeve = partdefinition.addOrReplaceChild("rightsleeve", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition rightarm = partdefinition.addOrReplaceChild("rightarm", CubeListBuilder.create().texOffs(56, 0).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition leftpantleg = partdefinition.addOrReplaceChild("leftpantleg", CubeListBuilder.create().texOffs(44, 12).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(2.0F, 12.0F, 0.0F));

		PartDefinition leftleg = partdefinition.addOrReplaceChild("leftleg", CubeListBuilder.create().texOffs(20, 44).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(1.9F, 12.0F, 0.0F));

		PartDefinition rightpantleg = partdefinition.addOrReplaceChild("rightpantleg", CubeListBuilder.create().texOffs(36, 44).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(-2.0F, 12.0F, 0.0F));

		PartDefinition rightleg = partdefinition.addOrReplaceChild("rightleg", CubeListBuilder.create().texOffs(32, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(-1.9F, 12.0F, 0.0F));

		PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition helmet1 = Head.addOrReplaceChild("helmet1", CubeListBuilder.create().texOffs(9, 59).addBox(0.2273F, 0.078F, -0.5136F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -1.6F, -5.4F, 0.3187F, 0.0911F, 0.0F));

		PartDefinition helmet2 = Head.addOrReplaceChild("helmet2", CubeListBuilder.create().texOffs(24, 16).addBox(-3.2273F, 0.078F, -0.5136F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -1.6F, -5.4F, 0.3187F, -0.0911F, 0.0F));

		PartDefinition helmet3 = Head.addOrReplaceChild("helmet3", CubeListBuilder.create().texOffs(43, 79).addBox(-0.5F, -1.25F, -1.25F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.9939F, -4.37F, 0.2182F, 0.0F, 0.0F));

		PartDefinition helmet11 = Head.addOrReplaceChild("helmet11", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.7826F, -4.2479F, -0.1309F, 0.0F, 0.0F));

		PartDefinition helmet4 = Head.addOrReplaceChild("helmet4", CubeListBuilder.create().texOffs(14, 77).addBox(-3.75F, -2.0F, -5.0F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(55, 72).addBox(-3.75F, -2.0F, 0.0F, 3.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(2.25F, -7.0F, 0.0F));

		PartDefinition helmet5 = Head.addOrReplaceChild("helmet5", CubeListBuilder.create().texOffs(75, 26).addBox(-4.75F, -0.8161F, -5.0908F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(93, 26).addBox(7.75F, -0.8161F, -5.0908F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -1.25F, 0.0F, 0.2618F, 0.0F, 0.0F));

		PartDefinition helmet6 = Head.addOrReplaceChild("helmet6", CubeListBuilder.create().texOffs(77, 38).addBox(-4.25F, 1.5F, 0.75F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(71, 64).addBox(5.25F, 1.5F, 0.75F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(90, 55).addBox(-1.25F, -2.5F, 0.75F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(75, 26).addBox(-4.25F, -2.5F, 0.75F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(77, 46).addBox(8.25F, -2.5F, 0.75F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 17).addBox(5.25F, -2.5F, 0.75F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, 1.5F, -6.5F, -0.2618F, 0.0F, 0.0F));

		PartDefinition helmet7 = Head.addOrReplaceChild("helmet7", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.0F, -1.0661F, 6.6592F, -0.0436F, 0.0F, 0.0F));

		PartDefinition shape29_9_r1 = helmet7.addOrReplaceChild("shape29_9_r1", CubeListBuilder.create().texOffs(97, 44).addBox(7.75F, -1.0F, -5.75F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.5091F, -0.7439F, -0.2618F, 0.0F, 0.0F));

		PartDefinition helmet9 = Head.addOrReplaceChild("helmet9", CubeListBuilder.create(), PartPose.offsetAndRotation(-15.0F, -1.0661F, 6.6592F, -0.0436F, 0.0F, 0.0F));

		PartDefinition shape210_10_r1 = helmet9.addOrReplaceChild("shape210_10_r1", CubeListBuilder.create().texOffs(97, 44).addBox(8.25F, -1.0F, -5.75F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.5091F, -0.7439F, -0.2618F, 0.0F, 0.0F));

		PartDefinition helmet8 = Head.addOrReplaceChild("helmet8", CubeListBuilder.create().texOffs(115, 57).addBox(-1.1763F, -0.7829F, -2.4255F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.5F, -0.0661F, 7.1592F, 0.277F, -0.1292F, 0.3928F));

		PartDefinition helmet10 = Head.addOrReplaceChild("helmet10", CubeListBuilder.create().texOffs(115, 66).mirror().addBox(0.1763F, -0.7829F, -2.4255F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-6.5F, -0.0661F, 7.1592F, 0.277F, 0.1292F, -0.3928F));

		PartDefinition jacket = partdefinition.addOrReplaceChild("jacket", CubeListBuilder.create().texOffs(0, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition torso = partdefinition.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(28, 28).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition torso1 = torso.addOrReplaceChild("torso1", CubeListBuilder.create().texOffs(83, 76).addBox(-4.0F, -24.0F, 4.5F, 8.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 85).addBox(1.5F, -20.75F, 6.0F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(42, 61).addBox(-1.5F, -20.75F, 4.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(34, 61).addBox(-3.5F, -20.75F, 4.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(70, 87).addBox(1.5F, -20.75F, 4.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(12, 70).addBox(-3.5F, -20.75F, 6.0F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(41, 87).addBox(-1.0F, -20.75F, 6.0F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(59, 88).addBox(-2.0F, -22.25F, 3.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(84, 66).addBox(-1.5F, -24.5F, 4.0F, 3.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(80, 86).addBox(-5.0F, -23.5F, 5.0F, 10.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, -0.75F));

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