package com.parzivail.pswg.mixin;

import com.parzivail.util.entity.IFlyingVehicle;
import com.parzivail.util.entity.collision.ComplexCollisionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin
{
	@Shadow
	public World world;

	@Shadow
	@Nullable
	public abstract Entity getVehicle();

	@Shadow
	public abstract boolean damage(DamageSource source, float amount);

	@Shadow
	public abstract void move(MovementType movementType, Vec3d movement);

	@Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
	private void noShipFallDamage(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir)
	{
		if (damageSource.isIn(DamageTypeTags.IS_FALL) && getVehicle() instanceof IFlyingVehicle)
			cir.setReturnValue(true);
	}

	@ModifyArg(method = "move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;adjustMovementForSneaking(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/entity/MovementType;)Lnet/minecraft/util/math/Vec3d;"), index = 0)
	private Vec3d adjustMovementForCollisions(Vec3d currentMovement)
	{
		return ComplexCollisionManager
				.adjustMovementForCollisions((Entity)(Object)this, currentMovement)
				.orElse(currentMovement);
	}
}
