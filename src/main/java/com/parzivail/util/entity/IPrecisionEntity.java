package com.parzivail.util.entity;

import com.parzivail.util.network.PreciseEntityVelocityUpdateS2CPacket;

/**
 * Tag interface for entities which require precise velocity updates
 */
public interface IPrecisionEntity
{
	default void onPrecisionVelocityPacket(PreciseEntityVelocityUpdateS2CPacket packet)
	{
	}
}
