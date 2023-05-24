package com.parzivail.pswg.features.lightsabers.addon;

import com.parzivail.pswg.features.lightsabers.data.LightsaberBladeType;
import com.parzivail.util.data.PacketByteBufHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class AddonLightsaberManager extends ByteBufResourceReloader<AddonLightsaberManager.Schema>
{
	public interface Schema
	{
		Identifier identifier();

		String owner();

		boolean unstable();

		LightsaberBladeType bladeType();

		int bladeColor();

		HashMap<String, Float> bladeLengthCoefficients();
	}

	private record Version1Schema(
			Identifier identifier,
			String owner,
			boolean unstable,
			LightsaberBladeType bladeType,
			int bladeColor,
			HashMap<String, Float> bladeLengthCoefficients
	) implements Schema
	{
		public static Schema deserialize(PacketByteBuf buf)
		{
			var domain = buf.readString();
			var id = buf.readString();
			var owner = PacketByteBufHelper.readNullable(buf, PacketByteBuf::readString);

			var unstable = buf.readBoolean();
			var bladeType = LightsaberBladeType.TYPES.get(buf.readString());
			var bladeColor = buf.readInt();

			var bladeCoefs = new HashMap<String, Float>();
			var numBladeCoefs = buf.readInt();
			for (var i = 0; i < numBladeCoefs; i++)
			{
				var socketName = buf.readString();
				var bladeCoef = buf.readFloat();
				bladeCoefs.put(socketName, bladeCoef);
			}

			return new Version1Schema(new Identifier(domain, id), owner, unstable, bladeType, bladeColor, bladeCoefs);
		}
	}

	public static final AddonLightsaberManager INSTANCE = new AddonLightsaberManager();

	public AddonLightsaberManager()
	{
		super("lightsabers", ".pswg_lightsaber");
	}

	@Override
	public Schema deserialize(Identifier entryId, Identifier resourceId, PacketByteBuf buf)
	{
		var schemaVersion = buf.readInt();
		return switch (schemaVersion)
		{
			case 1 -> Version1Schema.deserialize(buf);
			default -> throw new RuntimeException("Unsupported lightsaber schema version: %s".formatted(schemaVersion));
		};
	}
}
