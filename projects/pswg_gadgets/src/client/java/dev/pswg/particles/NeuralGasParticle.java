package dev.pswg.particles;

import dev.pswg.PswgGadgetsRenderLayers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

@Environment(value = EnvType.CLIENT)
public class NeuralGasParticle extends SpriteBillboardParticle
{
	private final int variant;
	final int NUM_VARIANTS = 7;

	protected NeuralGasParticle(ClientWorld clientWorld, double x, double y, double z, double vX, double vY, double vZ, SpriteProvider spriteProvider)
	{
		super(clientWorld, x, y, z);
		scale(16f);

		setBoundingBoxSpacing(0.25f, 0.25f);
		this.setAlpha(0.95f);
		variant = random.nextInt(NUM_VARIANTS);
		velocityX = random.nextFloat() / 10f * (random.nextBoolean() ? 1 : -1);
		velocityZ = random.nextFloat() / 10f * (random.nextBoolean() ? 1 : -1);
		this.setColor(1, 0.8f, 1);
	}

	@Override
	public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta)
	{
		var texture = this.sprite.getAtlasId();
		var mc = MinecraftClient.getInstance();
		var v = mc.getBufferBuilders().getEffectVertexConsumers().getBuffer(PswgGadgetsRenderLayers.pswgParticle(texture, true));
		super.render(v, camera, tickDelta);
	}

	@Override
	public void tick()
	{

		prevPosX = x;
		prevPosY = y;
		prevPosZ = z;
		age++;
		if (alpha <= 0.0f)
		{
			markDead();
			return;
		}
		if (age >= 100)
		{
			alpha -= 0.001f;
		}
		if (alpha <= 0.25f)
			velocityY += 1 / 2000f;
		velocityX *= 0.95;
		velocityZ *= 0.95;
		move(velocityX, velocityY, velocityZ);
	}

	@Override
	public ParticleTextureSheet getType()
	{
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Environment(value = EnvType.CLIENT)
	public static class Factory implements ParticleFactory<SimpleParticleType>
	{
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}

		@Nullable
		@Override
		public Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ)
		{
			NeuralGasParticle neuralGasParticle = new NeuralGasParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);
			neuralGasParticle.setSprite(spriteProvider);
			return neuralGasParticle;
		}
	}
}
