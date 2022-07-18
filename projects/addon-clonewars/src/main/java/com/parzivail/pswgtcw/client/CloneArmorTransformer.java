package com.parzivail.pswgtcw.client;

import com.parzivail.pswg.client.render.armor.ArmorRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;

public class CloneArmorTransformer implements ArmorRenderer.ArmorRenderTransformer
{
	private static final String PART_HEAD_PHASE1 = "phase1";
	private static final String PART_HEAD_PHASE2 = "phase2";
	private static final String PART_HEAD_MACROBINOCULARS = "macrobinoculars";
	private static final String PART_HEAD_RANGEFINDER = "rangefinder";
	private static final String PART_HEAD_VISOR = "visor";

	private static final String PART_BODY_KAMA = "kama";
	private static final String PART_BODY_LEFT_PAULDRON = "left_pauldron";
	private static final String PART_BODY_RIGHT_PAULDRON = "right_pauldron";

	private static final String PART_LEFT_ARM_DEFAULT = "left_arm_default";
	private static final String PART_LEFT_ARM_SLIM = "left_arm_slim";
	private static final String PART_RIGHT_ARM_DEFAULT = "right_arm_default";
	private static final String PART_RIGHT_ARM_SLIM = "right_arm_slim";

	private final boolean isPhase2;

	public CloneArmorTransformer(boolean isPhase2)
	{
		this.isPhase2 = isPhase2;
	}

	@Override
	public void transform(LivingEntity entity, boolean slim, BipedEntityModel<LivingEntity> armorModel)
	{
		var helmetPhase1 = armorModel.head.getChild(PART_HEAD_PHASE1);
		var helmetPhase2 = armorModel.head.getChild(PART_HEAD_PHASE2);
		var macrobinoculars = armorModel.head.getChild(PART_HEAD_MACROBINOCULARS);
		var rangefinder = armorModel.head.getChild(PART_HEAD_RANGEFINDER);
		var visor = armorModel.head.getChild(PART_HEAD_VISOR);

		var kama = armorModel.body.getChild(PART_BODY_KAMA);
		var pauldronL = armorModel.body.getChild(PART_BODY_LEFT_PAULDRON);
		var pauldronR = armorModel.body.getChild(PART_BODY_RIGHT_PAULDRON);

		var armLD = armorModel.leftArm.getChild(PART_LEFT_ARM_DEFAULT);
		var armLS = armorModel.leftArm.getChild(PART_LEFT_ARM_SLIM);
		var armRD = armorModel.rightArm.getChild(PART_RIGHT_ARM_DEFAULT);
		var armRS = armorModel.rightArm.getChild(PART_RIGHT_ARM_SLIM);

		helmetPhase1.visible = !isPhase2;
		helmetPhase2.visible = isPhase2;

		macrobinoculars.visible = false;
		rangefinder.visible = false;
		visor.visible = false;

		kama.visible = false;
		pauldronL.visible = false;
		pauldronR.visible = false;

		armLD.visible = armRD.visible = !slim;
		armLS.visible = armRS.visible = slim;
	}
}
