package com.parzivail.pswg.item;

import com.parzivail.pswg.container.SwgEntities;
import com.parzivail.pswg.container.SwgItems;
import com.parzivail.pswg.entity.ThermalDetonatorEntity;
import com.parzivail.util.item.ILeftClickConsumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ThermalDetonatorItem extends Item implements ILeftClickConsumer
{
	public ThermalDetonatorItem(Settings settings)
	{
		super(settings);
	}
	public ThermalDetonatorEntity createThermalDetonator(World world, ItemStack stack, int life, boolean primed, PlayerEntity playerEntity) {
		ThermalDetonatorEntity thermalDetonatorEntity = new ThermalDetonatorEntity(SwgEntities.Misc.ThermalDetonator, world);
		thermalDetonatorEntity.setLife(life);
		thermalDetonatorEntity.setPrimed(primed);
		thermalDetonatorEntity.onSpawnPacket(new EntitySpawnS2CPacket(thermalDetonatorEntity.getId(), thermalDetonatorEntity.getUuid(), playerEntity.getX(), playerEntity.getY()+1, playerEntity.getZ(), -playerEntity.getPitch(), -playerEntity.getYaw(), thermalDetonatorEntity.getType(),0, new Vec3d(0f, 0f ,0f), playerEntity.getHeadYaw()));
		return thermalDetonatorEntity;
	}
	int ticksToExplosion=1000000;
	boolean isPrimed=false;

	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks)
	{
		((PlayerEntity)user).getItemCooldownManager().getCooldownProgress(this, remainingUseTicks);

		super.usageTick(world, user, stack, remainingUseTicks);
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user)
	{

		//ThermalDetonatorEntity thermalDetonator = this.createThermalDetonator(world, stack);
		//thermalDetonator.setLife(1);
		//thermalDetonator.setPrimed(true);
		//thermalDetonator.onSpawnPacket(new EntitySpawnS2CPacket(thermalDetonator.getId(), thermalDetonator.getUuid(), user.getX(), user.getY()+1, user.getZ(), -user.getPitch(), -user.getYaw(), thermalDetonator.getType(),0, new Vec3d(0f, 0f ,0f), user.getHeadYaw()));
		//world.spawnEntity(thermalDetonator);
		//world.createExplosion(user, user.getX(), user.getY(), user.getZ(), 1f, World.ExplosionSourceType.MOB);
		PlayerEntity player = (PlayerEntity)user;
		if(!player.isCreative()){
			//stack.decrement(1);
		}
		return super.finishUsing(stack, world, user);
	}
	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
	{
		if(entity.isPlayer()){
			PlayerEntity player = (PlayerEntity)entity;
		}
		if(this.isPrimed){
			this.ticksToExplosion--;
		}
		if(entity.isInLava()||entity.isOnFire()){
			PlayerEntity playerEntity = (PlayerEntity)entity;
			ThermalDetonatorItem ThermalDetonatorItem = (ThermalDetonatorItem)(stack.getItem() instanceof ThermalDetonatorItem ? stack.getItem() : SwgItems.Explosives.ThermalDetonator);
			ThermalDetonatorEntity thermalDetonatorEntity = ThermalDetonatorItem.createThermalDetonator(world, stack, 0, true, playerEntity);
			thermalDetonatorEntity.shouldRenderVar=false;
			float power =playerEntity.getInventory().count(this);
			for(float i=power;i>=0;i--){
				playerEntity.getInventory().removeOne(stack);
			}

			thermalDetonatorEntity.setExplosionPower(power*2f);
			world.spawnEntity(thermalDetonatorEntity);
		}

		if((this.isPrimed&&this.ticksToExplosion<=0)){
			PlayerEntity playerEntity = (PlayerEntity)entity;
			ThermalDetonatorItem ThermalDetonatorItem = (ThermalDetonatorItem)(stack.getItem() instanceof ThermalDetonatorItem ? stack.getItem() : SwgItems.Explosives.ThermalDetonator);
			ThermalDetonatorEntity thermalDetonatorEntity = ThermalDetonatorItem.createThermalDetonator(world, stack, 0, true, playerEntity);
			thermalDetonatorEntity.shouldRenderVar=false;
			world.spawnEntity(thermalDetonatorEntity);
			this.ticksToExplosion=150;
			this.isPrimed=false;
			if(entity.isPlayer()){
				PlayerEntity player = (PlayerEntity)entity;
				player.getItemCooldownManager().set(stack.getItem(), 0);
				if(!player.isCreative()){
					stack.decrement(1);
				}
			}


		}
		super.inventoryTick(stack, world, entity, slot, selected);
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		if (user instanceof PlayerEntity playerEntity) {
			boolean inCreative = playerEntity.getAbilities().creativeMode ;
			ItemStack itemStack = playerEntity.getStackInHand(Hand.MAIN_HAND);
			if (!itemStack.isEmpty() || inCreative) {
				if (itemStack.isEmpty()) {
					itemStack = new ItemStack(SwgItems.Explosives.ThermalDetonator);
				}
					if (!world.isClient) {
						ThermalDetonatorItem ThermalDetonatorItem = (ThermalDetonatorItem)(itemStack.getItem() instanceof ThermalDetonatorItem ? itemStack.getItem() : SwgItems.Explosives.ThermalDetonator);
						ThermalDetonatorEntity thermalDetonatorEntity = ThermalDetonatorItem.createThermalDetonator(world, itemStack, this.ticksToExplosion/2, this.isPrimed, playerEntity);
						thermalDetonatorEntity.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), playerEntity.getRoll(),  1.0F, 1.0F);
						thermalDetonatorEntity.setOwner(playerEntity);

						world.spawnEntity(thermalDetonatorEntity);
						playerEntity.getItemCooldownManager().remove(itemStack.getItem());
						this.isPrimed=false;
					}

					world.playSound((PlayerEntity)null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ITEM_BUNDLE_DROP_CONTENTS, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F));
					if(!inCreative){
						stack.decrement(1);
					}
					playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
			}
		}
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 2000;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.NONE;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

		ItemStack itemStack = user.getStackInHand(hand);
		user.setCurrentHand(hand);

		return TypedActionResult.consume(itemStack);
	}


	@Override
	public TypedActionResult<ItemStack> useLeft(World world, PlayerEntity user, Hand hand, boolean isRepeatEvent)
	{
		if(!this.isPrimed){
			this.isPrimed=true;
			user.sendMessage(Text.of("Explosive primed"), true);
			user.getItemCooldownManager().set(user.getMainHandStack().getItem(), 75);
			this.ticksToExplosion=150;
		}

		//ThermalDetonatorItem.
		return TypedActionResult.success(user.getMainHandStack());
	}


	@Override
	public boolean allowRepeatedLeftHold(World world, PlayerEntity player, Hand mainHand)
	{
		return false;
	}
}
