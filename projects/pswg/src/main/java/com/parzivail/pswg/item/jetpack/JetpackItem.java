package com.parzivail.pswg.item.jetpack;

import com.parzivail.pswg.client.input.IJetpackDataContainer;
import com.parzivail.pswg.client.input.JetpackControls;
import com.parzivail.pswg.entity.ship.ShipEntity;
import com.parzivail.pswg.network.JetpackControlsC2SPacket;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class JetpackItem extends TrinketItem
{
	public record Stats()
	{
	}

	private final Stats stats;

	public JetpackItem(Settings settings, Stats stats)
	{
		super(settings);
		this.stats = stats;
	}

	public static ItemStack getEquippedJetpack(LivingEntity entity)
	{
		var t = TrinketsApi.getTrinketComponent(entity);
		if (t.isEmpty())
			return ItemStack.EMPTY;

		var c = t.get();
		var item = c.getEquipped(stack -> stack.getItem() instanceof JetpackItem);
		if (item.isEmpty())
			return ItemStack.EMPTY;

		return item.getFirst().getRight();
	}

	private static void setVerticalVelocity(LivingEntity player, double y)
	{
		var motion = player.getVelocity();
		player.setVelocity(motion.x, y, motion.z);
	}

	public static void handleControlPacket(JetpackControlsC2SPacket packet, ServerPlayNetworking.Context context)
	{
		((IJetpackDataContainer)context.player()).pswg_setJetpackControls(packet.jetpackControls());
	}

	public static Optional<Boolean> tickFallFlying(LivingEntity entity, ItemStack jetpack, boolean flyFalling)
	{
		if (ShipEntity.getShip(entity) != null)
			return Optional.empty();

//		var jt = new JetpackTag(jetpack.getOrCreateNbt());
		var jc = ((IJetpackDataContainer)entity);
		var controls = jc.pswg_getJetpackControls();

		if (entity.isOnGround() || controls.contains(JetpackControls.DESCEND))
			return Optional.of(false);

		if (controls.contains(JetpackControls.MODE))
			return Optional.of(true);

		return Optional.empty();
	}

	public static boolean travel(LivingEntity entity, ItemStack jetpack)
	{
		if (ShipEntity.getShip(entity) != null)
			return false;

		var jc = ((IJetpackDataContainer)entity);
		var controls = jc.pswg_getJetpackControls();
		if (!controls.contains(JetpackControls.ASCEND))
			return false;

		var velocity = entity.getVelocity();
		if (velocity.y > -0.5)
		{
			entity.fallDistance = 1.0F;
		}

		var acceleration = entity.getRotationVector();
		acceleration = acceleration.multiply(jc.pswg_getJetpackForce().y);

		entity.setVelocity(velocity.multiply(0.95f).add(acceleration));

		entity.move(MovementType.SELF, entity.getVelocity());
		if (entity.horizontalCollision && !entity.getWorld().isClient)
		{
			var o = entity.getVelocity().horizontalLength();
			if (o > 0.0F)
			{
				entity.playSound(entity.getFallSounds().small(), 1.0F, 1.0F);
				entity.damage(entity.getDamageSources().flyIntoWall(), (float)o);
			}
		}

		jc.pswg_tryCancelFallFlying();

		return true;
	}

	@Override
	public void tick(ItemStack stack, SlotReference slot, LivingEntity living)
	{
		var world = living.getWorld();

		if (!stack.isEmpty())
		{
			if (true)
			{
				var jc = ((IJetpackDataContainer)living);
				var force = jc.pswg_getJetpackForce();
				var controls = jc.pswg_getJetpackControls();

				var isHoldingUp = controls.contains(JetpackControls.ASCEND);
				var isHoldingDown = controls.contains(JetpackControls.DESCEND);
				var isHoldingForward = controls.contains(JetpackControls.FORWARD);
				var isHoldingBackward = controls.contains(JetpackControls.BACKWARD);
				var isHoldingLeft = controls.contains(JetpackControls.LEFT);
				var isHoldingRight = controls.contains(JetpackControls.RIGHT);
				var isHoldingMode = controls.contains(JetpackControls.MODE);

//				var oldThrottle = jt.throttle
//				if (isHoldingUp)
//					jt.throttle += 0.1f;
//				else if (isHoldingDown)
//					jt.throttle -= 0.1f;
//
//				jt.throttle = MathHelper.clamp(jt.throttle, 0, 1);
//				if (jt.throttle != oldThrottle)
//					jt.serializeAsSubtag(stack);

				if (isHoldingUp)
				{
					if (living.isOnGround() || force.equals(Vec3d.ZERO))
						// cancel out falling acceleration, gravity is 0.08 blocks/tick^2
						force = new Vec3d(0, 0.08f, 0);

					force = force.add(new Vec3d(0, 0.004f, 0));

					living.fallDistance = 0;
				}
				else if (isHoldingDown || !living.isFallFlying())
				{
					force = Vec3d.ZERO;
				}
				else if (isHoldingMode && !living.isOnGround() && !living.isFallFlying())
				{
					if (world.isClient && living instanceof ClientPlayerEntity playerEntity)
						playerEntity.networkHandler.sendPacket(new ClientCommandC2SPacket(living, ClientCommandC2SPacket.Mode.START_FALL_FLYING));

					if (living instanceof PlayerEntity playerEntity)
						playerEntity.startFallFlying();
				}

				var maxThrust = 0.15f;
				if (living.isFallFlying())
					maxThrust /= 2;
				jc.pswg_setJetpackForce(new Vec3d(0, MathHelper.clamp(force.y, 0, maxThrust), 0));
			}
		}
	}
}
