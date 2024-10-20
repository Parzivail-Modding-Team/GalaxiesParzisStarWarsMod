public class twilek.nem
public twilek.nem
texWidth = 150;
texHeight = 150;
right_arm = new ModelRenderer(this);
right_arm.setPos(-5.0F, 2.0F, 0.0F);
setRotationAngle(right_arm, 0.0F, 0.0F, 0.0F);
right_arm.texOffs(40, 16).addBox(-2.0F, -2.0F, -2.0F, 3F, 12F, 4F, 0.0F, false);
left_leg = new ModelRenderer(this);
left_leg.setPos(1.9F, 12.0F, 0.0F);
setRotationAngle(left_leg, 0.0F, 0.0F, 0.0F);
left_leg.texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4F, 12F, 4F, 0.0F, false);
jacket = new ModelRenderer(this);
jacket.setPos(0.0F, 0.0F, 0.0F);
setRotationAngle(jacket, 0.0F, 0.0F, 0.0F);
jacket.texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8F, 12F, 4F, 0.2F, true);
body = new ModelRenderer(this);
body.setPos(0.0F, 0.0F, 0.0F);
setRotationAngle(body, 0.0F, 0.0F, 0.0F);
body.texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8F, 12F, 4F, 0.0F, true);
chest = new ModelRenderer(this);
chest.setPos(0.0F, -0.1F, 1.0F);
body.addChild(chest);
setRotationAngle(chest, 0.0F, 0.0F, 0.0F);
chest.texOffs(0, 65).addBox(-3.0F, 2.0F, -4.0F, 6F, 3F, 2F, 0.0F, false);
head = new ModelRenderer(this);
head.setPos(0.0F, 0.0F, 0.0F);
setRotationAngle(head, 0.0F, 0.0F, 0.0F);
head.texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8F, 8F, 8F, 0.0F, true);
SpikeR = new ModelRenderer(this);
SpikeR.setPos(-4.8F, -3.5F, -1.8F);
head.addChild(SpikeR);
setRotationAngle(SpikeR, 0.0F, 0.0F, -0.44610617F);
SpikeR.texOffs(4, 71).addBox(0.0F, 0.0F, 0.0F, 2F, 3F, 2F, 0.0F, true);
TailBaseR = new ModelRenderer(this);
TailBaseR.setPos(-2.5F, -7.7F, 2.3F);
head.addChild(TailBaseR);
setRotationAngle(TailBaseR, 0.18203785F, 0.0F, 0.0F);
TailBaseR.texOffs(14, 66).addBox(-2.0F, 0.0F, -2.5F, 4F, 5F, 5F, 0.0F, true);
TailMidR = new ModelRenderer(this);
TailMidR.setPos(-0.1F, 1.9F, 0.8F);
TailBaseR.addChild(TailMidR);
setRotationAngle(TailMidR, -0.08325221F, 0.0F, 0.0F);
TailMidR.texOffs(33, 66).addBox(-1.5F, 0.0F, -1.5F, 3F, 10F, 3F, 0.0F, true);
TailLowerR = new ModelRenderer(this);
TailLowerR.setPos(0.0F, 9.7F, 0.0F);
TailMidR.addChild(TailLowerR);
setRotationAngle(TailLowerR, -0.08866273F, 0.0F, 0.0F);
TailLowerR.texOffs(47, 66).addBox(-1.0F, 0.0F, -1.0F, 2F, 6F, 2F, 0.0F, true);
FrontalR = new ModelRenderer(this);
FrontalR.setPos(-4.3F, -8.6F, -4.4F);
head.addChild(FrontalR);
setRotationAngle(FrontalR, 0.0F, 0.0F, 0.0F);
FrontalR.texOffs(2, 79).addBox(0.0F, 0.0F, 0.0F, 4F, 4F, 4F, 0.0F, true);
SpikeL = new ModelRenderer(this);
SpikeL.setPos(4.8F, -3.5F, -1.8F);
head.addChild(SpikeL);
setRotationAngle(SpikeL, 0.0F, 0.0F, 0.44069564F);
SpikeL.texOffs(4, 71).addBox(-2.0F, 0.0F, 0.0F, 2F, 3F, 2F, 0.0F, false);
TailBaseL = new ModelRenderer(this);
TailBaseL.setPos(2.5F, -7.7F, 2.3F);
head.addChild(TailBaseL);
setRotationAngle(TailBaseL, 0.18203785F, 0.0F, 0.0F);
TailBaseL.texOffs(14, 66).addBox(-2.0F, 0.0F, -2.5F, 4F, 5F, 5F, 0.0F, false);
TailMidL = new ModelRenderer(this);
TailMidL.setPos(0.1F, 1.9F, 0.8F);
TailBaseL.addChild(TailMidL);
setRotationAngle(TailMidL, -0.08325221F, 0.0F, 0.0F);
TailMidL.texOffs(33, 66).addBox(-1.5F, 0.0F, -1.5F, 3F, 10F, 3F, 0.0F, false);
TailLowerL = new ModelRenderer(this);
TailLowerL.setPos(0.0F, 9.7F, 0.0F);
TailMidL.addChild(TailLowerL);
setRotationAngle(TailLowerL, -0.08866273F, 0.0F, 0.0F);
TailLowerL.texOffs(47, 66).addBox(-1.0F, 0.0F, -1.0F, 2F, 6F, 2F, 0.0F, false);
FrontalL = new ModelRenderer(this);
FrontalL.setPos(0.3F, -8.6F, -4.4F);
head.addChild(FrontalL);
setRotationAngle(FrontalL, 0.0F, 0.0F, 0.0F);
FrontalL.texOffs(2, 79).addBox(0.0F, 0.0F, 0.0F, 4F, 4F, 4F, 0.0F, false);
left_sleeve = new ModelRenderer(this);
left_sleeve.setPos(5.0F, 2.0F, 0.0F);
setRotationAngle(left_sleeve, 0.0F, 0.0F, 0.0F);
left_sleeve.texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 3F, 12F, 4F, 0.2F, true);
right_sleeve = new ModelRenderer(this);
right_sleeve.setPos(-5.0F, 2.0F, 0.0F);
setRotationAngle(right_sleeve, 0.0F, 0.0F, 0.0F);
right_sleeve.texOffs(40, 32).addBox(-2.0F, -2.0F, -2.0F, 3F, 12F, 4F, 0.2F, false);
left_arm = new ModelRenderer(this);
left_arm.setPos(5.0F, 2.0F, 0.0F);
setRotationAngle(left_arm, 0.0F, 0.0F, 0.0F);
left_arm.texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 3F, 12F, 4F, 0.0F, false);
right_leg = new ModelRenderer(this);
right_leg.setPos(-1.9F, 12.0F, 0.0F);
setRotationAngle(right_leg, 0.0F, 0.0F, 0.0F);
right_leg.texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4F, 12F, 4F, 0.0F, false);
cloak = new ModelRenderer(this);
cloak.setPos(0.0F, 0.0F, 0.0F);
setRotationAngle(cloak, 0.0F, 0.0F, 0.0F);
ear = new ModelRenderer(this);
ear.setPos(0.0F, 0.0F, 0.0F);
setRotationAngle(ear, 0.0F, 0.0F, 0.0F);
right_pants = new ModelRenderer(this);
right_pants.setPos(-2.0F, 12.0F, 0.0F);
setRotationAngle(right_pants, 0.0F, 0.0F, 0.0F);
right_pants.texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4F, 12F, 4F, 0.2F, false);
hat = new ModelRenderer(this);
hat.setPos(0.0F, 0.0F, 0.0F);
setRotationAngle(hat, 0.0F, 0.0F, 0.0F);
hat.texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8F, 8F, 8F, 0.2F, false);
left_pants = new ModelRenderer(this);
left_pants.setPos(2.0F, 12.0F, 0.0F);
setRotationAngle(left_pants, 0.0F, 0.0F, 0.0F);
left_pants.texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4F, 12F, 4F, 0.2F, false);
