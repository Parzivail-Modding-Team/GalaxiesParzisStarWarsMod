package dev.pswg.networking;

/**
 * Applied to entities that consume a {@link GalaxiesEntitySpawnS2CPacket}
 * after the vanilla add-entity packet has created them on the client.
 */
public interface IPreciseSpawnDataEntity
{
	/**
	 * Applies the custom spawn payload to the client entity instance.
	 *
	 * @param packet The custom payload accompanying the vanilla spawn packet
	 */
	void applySpawnData(GalaxiesEntitySpawnS2CPacket packet);
}
