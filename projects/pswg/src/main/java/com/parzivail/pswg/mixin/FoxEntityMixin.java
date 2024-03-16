package com.parzivail.pswg.mixin;

import com.parzivail.pswg.entity.rodent.SandSkitterEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.FoxEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoxEntity.class)
public class FoxEntityMixin
{
	@Unique
	private Goal followSandSkitterGoal;

	@Inject(method = "initGoals", at = @At("HEAD"))
	private void pswm_addAdditionalGoals(CallbackInfo ci) {
		FoxEntity $this = (FoxEntity)(Object) this;
		this.followSandSkitterGoal = new ActiveTargetGoal<>($this, SandSkitterEntity.class, 10, false, false, (entity) -> true);
	}

	@Inject(method = "addTypeSpecificGoals", at = @At("HEAD"))
	private void pswm_addAdditionalSpecificGoals(CallbackInfo ci) {
		FoxEntity $this = (FoxEntity)(Object) this;
		MobEntityAccessor accessor = (MobEntityAccessor)$this;
		if ($this.getVariant() == FoxEntity.Type.RED) {
			accessor.getTargetSelector().add(4, this.followSandSkitterGoal);
		} else {
			accessor.getTargetSelector().add(6, this.followSandSkitterGoal);
		}
	}

}
