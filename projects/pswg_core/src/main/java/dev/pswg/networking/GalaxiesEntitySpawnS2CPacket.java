package dev.pswg.networking;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.pswg.Galaxies;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

/**
 * A spawn packet that serializes full-precision values for entity velocity and angles, and
 * allows for custom payload values
 */
public class GalaxiesEntitySpawnS2CPacket /* TODO: extends ClientboundAddEntityPacket*/ implements CustomPacketPayload
{
	/**
	 * The name of the NBT element that contains the custom spawn data
	 */
	private static final String CUSTOM_DATA_FIELD = "custom_data";

	public static final CustomPacketPayload.Type<GalaxiesEntitySpawnS2CPacket> ID = new CustomPacketPayload.Type<>(Galaxies.id("entity_spawn"));
	public static final StreamCodec<RegistryFriendlyByteBuf, GalaxiesEntitySpawnS2CPacket> CODEC = StreamCodec.ofMember(GalaxiesEntitySpawnS2CPacket::write, GalaxiesEntitySpawnS2CPacket::new);

	private final int id;
	private final java.util.UUID uuid;
	private final EntityType<?> entityType;
	private final double x;
	private final double y;
	private final double z;
	private final int data;
	private final float yHeadRot;
	private final Vec3 velocity;
	private final float yaw;
	private final float pitch;
	private final CompoundTag customData;

	/**
	 * Constructs a new {@link GalaxiesEntitySpawnS2CPacket}
	 *
	 * @param entity             The entity that generated the packet
	 * @param entityTrackerEntry The tracker entry for the entity
	 */
	public <T> GalaxiesEntitySpawnS2CPacket(Entity entity, ServerEntity entityTrackerEntry, Codec<T> codec, T value)
	{
		var vanillaPacket = new ClientboundAddEntityPacket(entity, entityTrackerEntry);
		this.id = vanillaPacket.getId();
		this.uuid = vanillaPacket.getUUID();
		this.entityType = vanillaPacket.getType();
		this.x = vanillaPacket.getX();
		this.y = vanillaPacket.getY();
		this.z = vanillaPacket.getZ();
		this.data = vanillaPacket.getData();
		this.yHeadRot = vanillaPacket.getYHeadRot();
		this.velocity = entity.getDeltaMovement();
		this.yaw = entity.getYRot();
		this.pitch = entity.getXRot();
		customData = (CompoundTag)codec
				.fieldOf(CUSTOM_DATA_FIELD)
				.codec()
				.encode(value, entity.registryAccess().createSerializationContext(NbtOps.INSTANCE), new CompoundTag())
				.getOrThrow();
	}

	public GalaxiesEntitySpawnS2CPacket(Entity entity, ServerEntity entityTrackerEntry)
	{
		this(entity, entityTrackerEntry, Codec.EMPTY.codec(), null);
	}

	public GalaxiesEntitySpawnS2CPacket(RegistryFriendlyByteBuf buf)
	{
		this.id = buf.readVarInt();
		this.uuid = buf.readUUID();
		this.entityType = ByteBufCodecs.registry(Registries.ENTITY_TYPE).decode(buf);
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
		this.data = buf.readVarInt();
		this.yHeadRot = buf.readFloat();
		this.velocity = buf.readVec3();
		this.yaw = buf.readFloat();
		this.pitch = buf.readFloat();
		this.customData = buf.readNbt();
	}

	// TODO: reintegrate with ClientboundAddEntityPacket
//	@Override
	public void write(RegistryFriendlyByteBuf buf)
	{
		buf.writeVarInt(this.id);
		buf.writeUUID(this.uuid);
		ByteBufCodecs.registry(Registries.ENTITY_TYPE).encode(buf, this.entityType);
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
		buf.writeVarInt(this.data);
		buf.writeFloat(this.yHeadRot);
		buf.writeVec3(this.velocity);
		buf.writeFloat(this.yaw);
		buf.writeFloat(this.pitch);
		buf.writeNbt(this.customData);
	}

	/**
	 * @return The entity ID represented by this packet
	 */
	public int getEntityId()
	{
		return this.id;
	}

	/**
	 * @return The full-precision velocity represented by this packet
	 */
	// TODO: reintegrate with ClientboundAddEntityPacket
//	@Override
	public Vec3 getMovement()
	{
		return velocity;
	}

	/**
	 * @return The full-precision velocity represented by this packet
	 */
	public Vec3 getVelocity()
	{
		return this.velocity;
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
				.parse(entity.registryAccess().createSerializationContext(NbtOps.INSTANCE), customData);
	}

	// TODO: reintegrate with ClientboundAddEntityPacket
//	@Override
	public float getYRot()
	{
		return yaw;
	}

	/**
	 * @return The exact yaw represented by this packet
	 */
	public float getYaw()
	{
		return this.yaw;
	}

	// TODO: reintegrate with ClientboundAddEntityPacket
//	@Override
	public float getXRot()
	{
		return pitch;
	}

	/**
	 * @return The exact pitch represented by this packet
	 */
	public float getPitch()
	{
		return this.pitch;
	}

	/**
	 * @return The vanilla add-entity packet represented by this payload
	 */
	public ClientboundAddEntityPacket toVanillaPacket()
	{
		return new ClientboundAddEntityPacket(
				this.id,
				this.uuid,
				this.x,
				this.y,
				this.z,
				this.pitch,
				this.yaw,
				this.entityType,
				this.data,
				this.velocity,
				this.yHeadRot
		);
	}

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
	{
		return ID;
	}
}
