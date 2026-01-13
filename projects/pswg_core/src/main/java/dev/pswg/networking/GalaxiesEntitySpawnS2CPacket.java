package dev.pswg.networking;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.pswg.Galaxies;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.util.math.Vec3d;

/**
 * A spawn packet that serializes full-precision values for entity velocity and angles, and
 * allows for custom payload values
 */
public class GalaxiesEntitySpawnS2CPacket extends EntitySpawnS2CPacket implements CustomPayload
{
	/**
	 * The name of the NBT element that contains the custom spawn data
	 */
	private static final String CUSTOM_DATA_FIELD = "custom_data";

	public static final CustomPayload.Id<GalaxiesEntitySpawnS2CPacket> ID = new Id<>(Galaxies.id("entity_spawn"));
	public static final PacketCodec<RegistryByteBuf, GalaxiesEntitySpawnS2CPacket> CODEC = PacketCodec.of(GalaxiesEntitySpawnS2CPacket::write, GalaxiesEntitySpawnS2CPacket::new);

	private final Vec3d velocity;
	private final float yaw;
	private final float pitch;
	private final NbtCompound customData;

	/**
	 * Constructs a new {@link GalaxiesEntitySpawnS2CPacket}
	 *
	 * @param entity             The entity that generated the packet
	 * @param entityTrackerEntry The tracker entry for the entity
	 */
	public <T> GalaxiesEntitySpawnS2CPacket(Entity entity, EntityTrackerEntry entityTrackerEntry, Codec<T> codec, T value)
	{
		super(entity, entityTrackerEntry);

		this.velocity = entity.getVelocity();
		this.yaw = entity.getYaw();
		this.pitch = entity.getPitch();
		customData = (NbtCompound)codec
				.fieldOf(CUSTOM_DATA_FIELD)
				.codec()
				.encode(value, entity.getRegistryManager().getOps(NbtOps.INSTANCE), new NbtCompound())
				.getOrThrow();
	}

	public GalaxiesEntitySpawnS2CPacket(Entity entity, EntityTrackerEntry entityTrackerEntry)
	{
		this(entity, entityTrackerEntry, Codec.EMPTY.codec(), null);
	}

	public GalaxiesEntitySpawnS2CPacket(RegistryByteBuf buf)
	{
		super(buf);

		this.velocity = buf.readVec3d();
		this.yaw = buf.readFloat();
		this.pitch = buf.readFloat();
		this.customData = buf.readNbt();
	}

	@Override
	public void write(RegistryByteBuf buf)
	{
		super.write(buf);

		buf.writeVec3d(this.velocity);
		buf.writeFloat(this.yaw);
		buf.writeFloat(this.pitch);
		buf.writeNbt(this.customData);
	}

	/**
	 * @return The full-precision velocity represented by this packet
	 */
	@Override
	public Vec3d getVelocity()
	{
		return velocity;
	}

	/**
	 * A field within the custom payload serialized for this entity
	 *
	 * @param entity The entity from which a registry manager will be derived
	 * @param codec  The map codec that will be used to deserialize the data
	 * @param <T>    The type of data to deserialize
	 *
	 * @return The deserialized data
	 */
	public <T> DataResult<T> getCustomData(Entity entity, Codec<T> codec)
	{
		return codec
				.fieldOf(CUSTOM_DATA_FIELD)
				.codec()
				.parse(entity.getRegistryManager().getOps(NbtOps.INSTANCE), customData);
	}

	@Override
	public float getYaw()
	{
		return yaw;
	}

	@Override
	public float getPitch()
	{
		return pitch;
	}

	@Override
	public Id<? extends CustomPayload> getId()
	{
		return ID;
	}
}
