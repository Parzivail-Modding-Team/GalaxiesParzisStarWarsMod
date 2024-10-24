public class aqualish.nem
public aqualish.nem
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
jacket.texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8F, 12F, 4F, 0.2F, false);
body = new ModelRenderer(this);
body.setPos(0.0F, 0.0F, 0.0F);
setRotationAngle(body, 0.0F, 0.0F, 0.0F);
body.texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8F, 12F, 4F, 0.0F, false);
chest = new ModelRenderer(this);
chest.setPos(0.0F, -0.1F, 1.0F);
body.addChild(chest);
setRotationAngle(chest, 0.0F, 0.0F, 0.0F);
chest.texOffs(0, 65).addBox(-3.0F, 2.0F, -4.0F, 6F, 3F, 2F, 0.0F, false);
left_sleeve = new ModelRenderer(this);
left_sleeve.setPos(5.0F, 2.0F, 0.0F);
setRotationAngle(left_sleeve, 0.0F, 0.0F, 0.0F);
left_sleeve.texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 3F, 12F, 4F, 0.2F, false);
head = new ModelRenderer(this);
head.setPos(0.0F, 0.0F, 0.0F);
setRotationAngle(head, 0.0F, 0.0F, 0.0F);
head.texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8F, 8F, 8F, 0.0F, false);
nose = new ModelRenderer(this);
nose.setPos(0.0F, -4.6F, -4.4F);
head.addChild(nose);
setRotationAngle(nose, -0.091106184F, 0.0F, 0.0F);
nose.texOffs(0, 73).addBox(-2.0F, 0.0F, 0.0F, 4F, 4F, 2F, 0.0F, false);
left_tooth = new ModelRenderer(this);
left_tooth.setPos(0.1F, 3.2F, 0.1F);
nose.addChild(left_tooth);
setRotationAngle(left_tooth, 0.18203785F, 0.0F, 0.0F);
left_tooth.texOffs(13, 74).addBox(0.0F, 0.0F, 0.0F, 2F, 2F, 2F, 0.0F, false);
right_tooth = new ModelRenderer(this);
right_tooth.setPos(-0.1F, 3.2F, 0.1F);
nose.addChild(right_tooth);
setRotationAngle(right_tooth, 0.18203785F, 0.0F, 0.0F);
right_tooth.texOffs(13, 74).addBox(-2.0F, 0.0F, 0.0F, 2F, 2F, 2F, 0.0F, false);
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
