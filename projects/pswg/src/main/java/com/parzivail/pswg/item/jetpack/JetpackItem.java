package com.parzivail.pswg.item.jetpack;

import com.parzivail.pswg.client.input.IJetpackDataContainer;
import com.parzivail.pswg.client.input.JetpackControls;
import com.parzivail.pswg.item.jetpack.data.JetpackTag;
import com.parzivail.util.item.IDefaultNbtProvider;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class JetpackItem extends TrinketItem implements IDefaultNbtProvider
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

		return item.get(0).getRight();
	}

	@Override
	public NbtCompound getDefaultTag(ItemConvertible item, int count)
	{
		return new JetpackTag().toSubtag();
	}

	private static void setVerticalVelocity(LivingEntity player, double y)
	{
		var motion = player.getVelocity();
		player.setVelocity(motion.x, y, motion.z);
	}

	public static void handeControlPacket(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender)
	{
		var controls = buf.readShort();
		server.execute(() -> ((IJetpackDataContainer)player).pswg_setJetpackControls(JetpackControls.unpack(controls)));
	}

	public static Optional<Boolean> tickFallFlying(LivingEntity entity, ItemStack jetpack, boolean flyFalling)
	{
		var jt = new JetpackTag(jetpack.getOrCreateNbt());
		var jc = ((IJetpackDataContainer)entity);
		var controls = jc.pswg_getJetpackControls();

		return Optional.of(controls.contains(JetpackControls.DESCEND) && !entity.isOnGround());
	}

	public static boolean travel(LivingEntity entity, ItemStack jetpack)
	{
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

		entity.setVelocity(velocity.multiply(0.9f).add(acceleration));

		entity.move(MovementType.SELF, entity.getVelocity());
		if (entity.horizontalCollision && !entity.world.isClient)
		{
			var o = entity.getVelocity().horizontalLength();
			if (o > 0.0F)
			{
				entity.playSound(entity.getFallSounds().small(), 1.0F, 1.0F);
				entity.damage(DamageSource.FLY_INTO_WALL, (float)o);
			}
		}

		jc.pswg_tryCancelFallFlying();

		return true;
	}

	/*
			Based on jetpack code from Iron Jetpacks (MIT licensed)
		 */
	@Override
	public void tick(ItemStack stack, SlotReference slot, LivingEntity living)
	{
		var world = living.world;

		if (!stack.isEmpty())
		{
			var jt = new JetpackTag(stack.getOrCreateNbt());

			if (jt.enabled || true)
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
				var isHoldingTurbo = controls.contains(JetpackControls.TURBO);

				if (isHoldingUp)
				{
					if (living.isOnGround() || force.equals(Vec3d.ZERO))
						// cancel out falling acceleration, gravity is 0.08 blocks/tick^2
						force = new Vec3d(0, 0.08f, 0);

					force = force.add(new Vec3d(0, 0.004f, 0));

					living.fallDistance = 0;
				}
				else
				{
					if (!force.equals(Vec3d.ZERO) && living instanceof ClientPlayerEntity playerEntity)
						playerEntity.networkHandler.sendPacket(new ClientCommandC2SPacket(living, ClientCommandC2SPacket.Mode.START_FALL_FLYING));

					force = Vec3d.ZERO;
				}

				var maxThrust = 0.15f;
//				if (living.isFallFlying())
//					maxThrust /= 2;
				jc.pswg_setJetpackForce(new Vec3d(0, MathHelper.clamp(force.y, 0, maxThrust), 0));
			}
		}
	}
}
