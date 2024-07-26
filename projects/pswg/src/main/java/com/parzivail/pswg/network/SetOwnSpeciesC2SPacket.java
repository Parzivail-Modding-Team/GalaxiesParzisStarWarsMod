package com.parzivail.pswg.network;

import com.parzivail.pswg.container.SwgPackets;
import net.minecraft.network.packet.CustomPayload;

public record SetOwnSpeciesC2SPacket(String species) implements CustomPayload
{
	@Override
	public Id<SetOwnSpeciesC2SPacket> getId()
	{
		return SwgPackets.C2S.SetOwnSpecies;
	}
}
