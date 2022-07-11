package com.parzivail.pswg.mixin;

import com.parzivail.pswg.entity.collision.ComplexCollisionManager;
import com.parzivail.util.entity.IFlyingVehicle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin
{
	@Shadow
	public World world;

	@Shadow
	@Nullable
	public abstract Entity getVehicle();

	@Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
	private void noShipFallDamage(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir)
	{
		if (damageSource == DamageSource.FALL && getVehicle() instanceof IFlyingVehicle)
			cir.setReturnValue(true);
	}

	@ModifyVariable(method = "move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/Entity;adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;"), ordinal = 1)
	private Vec3d move$adjustMovementForCollisions(Vec3d currentMovement)
	{
		Entity self = (Entity)(Object)this;
		return ComplexCollisionManager.adjustMovementForCollisions(self, currentMovement);
	}
}
