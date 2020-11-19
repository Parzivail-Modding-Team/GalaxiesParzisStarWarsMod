package com.parzivail.util.client;

import com.parzivail.pswg.mixin.PlayerEntityModelMixin;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;

public class PlayerEntityModelUtil
{
	public static <T extends LivingEntity> void setLeftSleeve(PlayerEntityModel<T> model, ModelPart value)
	{
		((PlayerEntityModelMixin)model).setLeftSleeve(value);
	}

	public static <T extends LivingEntity> void setRightSleeve(PlayerEntityModel<T> model, ModelPart value)
	{
		((PlayerEntityModelMixin)model).setRightSleeve(value);
	}

	public static <T extends LivingEntity> void setLeftPantLeg(PlayerEntityModel<T> model, ModelPart value)
	{
		((PlayerEntityModelMixin)model).setLeftPantLeg(value);
	}

	public static <T extends LivingEntity> void setRightPantLeg(PlayerEntityModel<T> model, ModelPart value)
	{
		((PlayerEntityModelMixin)model).setRightPantLeg(value);
	}

	public static <T extends LivingEntity> void setJacket(PlayerEntityModel<T> model, ModelPart value)
	{
		((PlayerEntityModelMixin)model).setJacket(value);
	}
}
