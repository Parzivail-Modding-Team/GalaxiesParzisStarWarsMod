package com.parzivail.pswg.mixin;

import com.parzivail.pswg.entity.collision.ComplexCollisionManager;
import com.parzivail.util.entity.IFlyingVehicle;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.collection.ReusableStream;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.stream.Stream;

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

	@Inject(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void adjustMovementForCollisions(Vec3d movement, CallbackInfoReturnable<Vec3d> cir, Box box, ShapeContext shapeContext, Stream<VoxelShape> stream, Stream<VoxelShape> stream2, ReusableStream<VoxelShape> reusableStream, Vec3d vec3d)
	{
		Entity self = (Entity)(Object)this;
		ComplexCollisionManager.adjustMovementForCollisions(self, movement, cir, box, vec3d);
	}
}
