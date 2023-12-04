package com.parzivail.pswg.entity;

import com.ibm.icu.text.MessagePattern;
import com.parzivail.pswg.container.SwgParticles;
import com.parzivail.pswg.container.SwgTags;
import com.parzivail.util.entity.IPrecisionSpawnEntity;
import com.parzivail.util.entity.IPrecisionVelocityEntity;
import com.parzivail.util.network.PreciseEntitySpawnS2CPacket;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

public class ThermalDetonatorEntity extends ThrownEntity implements IPrecisionSpawnEntity, IPrecisionVelocityEntity
{

	private static final TrackedData<Integer> LIFE = DataTracker.registerData(ThermalDetonatorEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> PRIMED = DataTracker.registerData(ThermalDetonatorEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	int delay = 0;
	boolean shouldExplode= false;
	float explosionPower =5f;
	public boolean shouldRenderVar = true;


	public ThermalDetonatorEntity(EntityType<ThermalDetonatorEntity> type, World world)
	{
		super(type, world);
	}

	@Override
	protected void initDataTracker()
	{
		dataTracker.startTracking(LIFE, 75);
		dataTracker.startTracking(PRIMED, false);
	}

	@Override
	public void onSpawnPacket(EntitySpawnS2CPacket packet)
	{
		super.onSpawnPacket(packet);

		if (packet instanceof PreciseEntitySpawnS2CPacket pes)
		{
			this.setVelocity(pes.getVelocity());
			this.readSpawnData(pes.getData());
		}
	}


	@Override
	public void writeSpawnData(NbtCompound tag)
	{
		tag.putInt("life", getLife());
		tag.putBoolean("primed", isPrimed());
	}
	public int getLife()
	{
		return dataTracker.get(LIFE);
	}
	public boolean isPrimed(){
		return dataTracker.get(PRIMED);
	}
	public void setLife(int life)
	{
		dataTracker.set(LIFE, life);
	}
	public void setPrimed(boolean isPrimed){
			dataTracker.set(PRIMED, isPrimed);
	}

	@Override
	public void tick() {

		if(shouldExplode){
			this.delay--;
			if(this.delay <= 0){
				this.explode();
			}
		}
		if(this.isInLava()){
			this.explode();
		}
		if (!getWorld().isClient && this.age > this.getLife() && isPrimed())
		{
			this.explode();
		}
		this.speed = this.speed*0.95f;

		super.tick();
	}
	private void createParticles(ServerWorld world, double x, double y, double z){
		int m= (int)explosionPower;
		for (int i = 0; i < world.getPlayers().size(); ++i) {
			ServerPlayerEntity player =world.getPlayers().get(i);
			world.spawnParticles(player, ParticleTypes.FLASH, true, x, y, z, 1, 0, 0,0, 0);

			world.spawnParticles(player,ParticleTypes.LARGE_SMOKE, true, x, y, z, 40*m, 1.5, 1.5, 1.5, 0);
			world.spawnParticles(player,ParticleTypes.SMOKE, true, x, y, z, 30*m, 1.5*m/2, 1.5*m/2, 1.5*m/2, 0);

			world.spawnParticles(player,SwgParticles.EXPLOSION_SMOKE, true, x, y, z, 10*m, 0.125*m/2, 0.25*m/2, 0.125*m/2, 0.03);
			world.spawnParticles(player,SwgParticles.EXPLOSION_SMOKE, true, x, y, z, 10*m, 0.25*m/2, 0.125*m/2, 0.125*m/2, 0.025);
			world.spawnParticles(player,SwgParticles.EXPLOSION_SMOKE, true, x, y, z, 10*m, 0.125*m/2, 0.125*m/2, 0.25*m/2, 0.02);
			world.spawnParticles(player,SwgParticles.EXPLOSION_SMOKE, true, x, y, z, 5*m, 0.1*m/2, 0.2*m/2, 0.1*m/2, 0.0075);
			world.spawnParticles(player,SwgParticles.EXPLOSION_SMOKE, true, x, y, z, 5*m, 0.2*m/2, 0.1*m/2, 0.25*m/2, 0.01);
			world.spawnParticles(player, SwgParticles.EXPLOSION_SMOKE, true, x, y, z, 5 * m, 0.25 * m / 2, 0.5 * m / 2, 0.3 * m / 2, 0.015);

			world.spawnParticles(player,ParticleTypes.FLAME, true, x, y, z, 15*m, 0.5*m/2, 0.5*m/2, 0.5*m/2, 0.1);
			world.spawnParticles(player,ParticleTypes.SMALL_FLAME, true, x, y, z, 15*m, 1.2*m/2, 1.2*m/2, 1.2*m/2, 0.125);
			world.spawnParticles(player,ParticleTypes.SMALL_FLAME, true, x, y, z, 10*m, 1.1*m/2, 1.3*m/2, 1.25*m/2, 0.125);
		}
	}
	private void explode() {
		if (!this.getWorld().isClient) {
			this.discard();
			this.getWorld().createExplosion(this, (DamageSource)null, (ExplosionBehavior)null, this.getX(), this.getY(), this.getZ(), explosionPower, true, World.ExplosionSourceType.TNT, true);
	}
		if(this.getWorld() instanceof ServerWorld serverWorld){
			createParticles(serverWorld, this.getX(), this.getY(), this.getZ());
		}else if(this.getWorld() instanceof ClientWorld clientWorld){
		}
	}

	public void setExplosionPower(float explosionPower)
	{
		this.explosionPower = explosionPower;
	}

	@Override
	public void readSpawnData(NbtCompound tag)
	{
		setLife(tag.getInt("life"));
	}
	@Override
	public void writeCustomDataToNbt(NbtCompound tag)
	{
		super.writeCustomDataToNbt(tag);
		writeSpawnData(tag);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound tag)
	{
		super.readCustomDataFromNbt(tag);
		readSpawnData(tag);
	}
	@Override
	public boolean shouldRender(double distance)
	{
		return true;
	}

	@Override
	public boolean hasNoGravity()
	{
		return false;
	}
	@Override
	protected void onCollision(HitResult hitResult)
	{
		if (hitResult.getType() == HitResult.Type.BLOCK)
		{
			if(hitResult.squaredDistanceTo(this)<0.01){
				this.setVelocity(0,0,0);
			}
			BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
			BlockHitResult blockHitResult = (BlockHitResult)hitResult;

			Vec3d velocity = this.getVelocity();
			Vec3d pos = hitResult.getPos();

			double modX = 0.9f;
			double modY = 0.9f;
			double modZ = 0.9f;
			if(blockHitResult.getSide()==Direction.UP){
				modY=-0.2f;
			}else if(blockHitResult.getSide()==Direction.DOWN){
				modY=-1.2f;
			}else {
				if (this.getX() - pos.getX() > 0 && this.getZ() - pos.getZ() > 0)
				{
					if (blockHitResult.getSide() == Direction.EAST) {
						modX = modX * -1;
					}else {
						modZ = modZ * -1;
					}
				}
				else if (this.getX() - pos.getX() < 0 && this.getZ() - pos.getZ() > 0)
				{
					if (blockHitResult.getSide() == Direction.SOUTH) {
						modZ = modZ * -1;
					}else {
						modX = modX * -1;
					}
				}
				else if (this.getX() - pos.getX() < 0 && this.getZ() - pos.getZ() < 0)
				{
					if (blockHitResult.getSide() == Direction.WEST) {
						modX = modX * -1;
					}else {
						modZ = modZ * -1;
					}
				}
				else if (this.getX() - pos.getX() > 0 && this.getZ() - pos.getZ() < 0)
				{
					if (blockHitResult.getSide() == Direction.NORTH)
					{
						modZ = modZ * -1;
					}else {
						modX = modX * -1;
					}
				}
			}

			this.setVelocity(velocity.x * modX, velocity.y * modY, velocity.z * modZ);

			super.onCollision(hitResult);
		}
	}

	@Override
	public boolean canExplosionDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float explosionPower)
	{
		if(state.isIn(SwgTags.Blocks.DETONATOR_EXPLODE)||state.isIn(BlockTags.LEAVES)||state.isIn(BlockTags.CROPS)||state.isIn(BlockTags.SMALL_FLOWERS)||state.isIn(BlockTags.TALL_FLOWERS)||state.isOf(Blocks.GRASS)||state.isOf(Blocks.TALL_GRASS)){
			return true;
		}
		return false;
	}


	@Override
	public boolean isAttackable()
	{
		return  false;
	}
	@Override
	public boolean isInvulnerable()
	{
		return false;
	}


	@Override
	public boolean damage(DamageSource source, float amount)
	{
		if(source.isIn(DamageTypeTags.IS_EXPLOSION)||source.isIn(DamageTypeTags.IS_FIRE)||source.isIn(DamageTypeTags.IS_LIGHTNING))
		{
			if(!this.shouldExplode){
				this.delay =2;
				this.shouldExplode=true;
			}

			//this.discard();
		}
		return super.damage(source, amount);
	}
}
