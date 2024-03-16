package com.parzivail.pswg.mixin;

import com.parzivail.pswg.entity.mammal.BanthaEntity;
import com.parzivail.pswg.entity.rodent.SandSkitterEntity;
import net.minecraft.entity.ai.goal.UntamedActiveTargetGoal;
import net.minecraft.entity.passive.WolfEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WolfEntity.class)
public abstract class WolfEntityMixin
{

	@Inject(method = "initGoals", at = @At("TAIL"))
	private void pswm_addAdditionalGoals(CallbackInfo ci) {
		WolfEntity $this = (WolfEntity)(Object) this;
		MobEntityAccessor accessor = (MobEntityAccessor)$this;
		accessor.getTargetSelector().add(6, new UntamedActiveTargetGoal<>($this, SandSkitterEntity.class, false, entity -> true));
		accessor.getTargetSelector().add(7, new UntamedActiveTargetGoal<>($this, BanthaEntity.class, false, entity -> true));
	}

}
