package com.parzivail.pswg.mixin;

import com.parzivail.pswg.entity.rodent.SandSkitterEntity;
import net.minecraft.entity.ai.goal.UntamedActiveTargetGoal;
import net.minecraft.entity.passive.CatEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CatEntity.class)
public abstract class CatEntityMixin
{

	@Inject(method = "initGoals", at = @At("TAIL"))
	private void pswm_addAdditionalGoals(CallbackInfo ci) {
		CatEntity $this = (CatEntity)(Object) this;
		MobEntityAccessor accessor = (MobEntityAccessor)$this;
		accessor.getTargetSelector().add(6, new UntamedActiveTargetGoal<>($this, SandSkitterEntity.class, false, entity -> true));
	}

}

