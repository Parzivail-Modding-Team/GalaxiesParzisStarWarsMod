package com.parzivail.pswg.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerEntityModel.class)
@Environment(EnvType.CLIENT)
public interface PlayerEntityModelMixin
{
	@Mutable
	@Accessor("leftSleeve")
	void setLeftSleeve(ModelPart value);

	@Mutable
	@Accessor("rightSleeve")
	void setRightSleeve(ModelPart value);

	@Mutable
	@Accessor("leftPants")
	void setLeftPantLeg(ModelPart value);

	@Mutable
	@Accessor("rightPants")
	void setRightPantLeg(ModelPart value);

	@Mutable
	@Accessor("jacket")
	void setJacket(ModelPart value);
}
