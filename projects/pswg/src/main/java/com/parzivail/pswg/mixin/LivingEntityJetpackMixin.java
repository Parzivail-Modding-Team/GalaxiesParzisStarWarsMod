package com.parzivail.pswg.mixin;

import com.parzivail.pswg.client.input.IJetpackDataContainer;
import com.parzivail.pswg.client.input.JetpackControls;
import com.parzivail.pswg.item.jetpack.JetpackItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;

@Mixin(LivingEntity.class)
public abstract class LivingEntityJetpackMixin extends Entity implements IJetpackDataContainer
{
	@Shadow
	public abstract boolean isFallFlying();

	@Shadow public abstract void updateLimbs(LivingEntity entity, boolean flutter);

	@Unique
	public short jetpackControls;

	@Unique
	public Vec3d jetpackForce = new Vec3d(0, 0, 0);

	public LivingEntityJetpackMixin(EntityType<?> type, World world)
	{
		super(type, world);
	}

	@Override
	public void pswg_setJetpackControls(EnumSet<JetpackControls> controls)
	{
		this.jetpackControls = JetpackControls.pack(controls);
	}

	@Override
	public EnumSet<JetpackControls> pswg_getJetpackControls()
	{
		return JetpackControls.unpack(this.jetpackControls);
	}

	@Override
	public void pswg_setJetpackForce(Vec3d force)
	{
		this.jetpackForce = force;
	}

	@Override
	public Vec3d pswg_getJetpackForce()
	{
		return this.jetpackForce;
	}

	@Override
	public void pswg_tryCancelFallFlying()
	{
		if (this.onGround && !this.world.isClient)
		{
			this.setFlag(Entity.FALL_FLYING_FLAG_INDEX, false);
		}
	}

	@Inject(method = "Lnet/minecraft/entity/LivingEntity;travel(Lnet/minecraft/util/math/Vec3d;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getRotationVector()Lnet/minecraft/util/math/Vec3d;", shift = At.Shift.AFTER), cancellable = true)
	public void travel(Vec3d movementInput, CallbackInfo ci)
	{
		var jetpack = JetpackItem.getEquippedJetpack((LivingEntity)(Object)this);
		if (!jetpack.isEmpty())
		{
			if (JetpackItem.travel((LivingEntity)(Object)this, jetpack))
			{
				this.updateLimbs((LivingEntity)(Object)this, this instanceof Flutterer);
				ci.cancel();
			}
		}
	}

	@SuppressWarnings("ConstantConditions")
	@Inject(method = "tick()V", at = @At("TAIL"))
	public void tick(CallbackInfo ci)
	{
		if (!isFallFlying())
			setVelocity(getVelocity().add(this.jetpackForce));

		var jetpack = JetpackItem.getEquippedJetpack((LivingEntity)(Object)this);
		if (jetpack.isEmpty())
			this.jetpackForce = new Vec3d(0, 0, 0);
	}

	@SuppressWarnings("ConstantConditions")
	@Inject(method = "tickFallFlying()V", at = @At("TAIL"))
	public void tickFallFlying(CallbackInfo ci)
	{
		if (world.isClient)
			return;

		var jetpack = JetpackItem.getEquippedJetpack((LivingEntity)(Object)this);
		if (!jetpack.isEmpty())
		{
			var option = JetpackItem.tickFallFlying((LivingEntity)(Object)this, jetpack, this.getFlag(Entity.FALL_FLYING_FLAG_INDEX));
			option.ifPresent(b -> this.setFlag(Entity.FALL_FLYING_FLAG_INDEX, b));
		}
	}
}
