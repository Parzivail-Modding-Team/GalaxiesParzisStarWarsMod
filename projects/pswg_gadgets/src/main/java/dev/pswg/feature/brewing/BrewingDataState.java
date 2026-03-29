package dev.pswg.feature.brewing;

import com.mojang.serialization.Codec;
import dev.pswg.Gadgets;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import java.util.HashMap;
import java.util.Map;

public class BrewingDataState extends SavedData
{
	public BrewingDataState(Map<String, PlayerBrewingData> map)
	{
		this.players = map;
	}

	public BrewingDataState()
	{

		this.players = new HashMap<>();
	}

	public Map<String, PlayerBrewingData> players;

	public static final Codec<BrewingDataState> CODEC = Codec.unboundedMap(ExtraCodecs.NON_EMPTY_STRING, PlayerBrewingData.createCodec()).xmap(
			BrewingDataState::new,
			BrewingDataState::getPlayerData
	);

	private static final SavedDataType<BrewingDataState> TYPE = new SavedDataType<>(
			Gadgets.id("player_brewing_data"),
			BrewingDataState::new,
			CODEC,
			null
	);

	// TODO : Sync with the client when getting, since it often is also when an update happens
	public static BrewingDataState getBrewingDataState(MinecraftServer server)
	{
		ServerLevel world = server.getLevel(Level.OVERWORLD);
		if (world == null)
			return new BrewingDataState();
		BrewingDataState brewingData = world.getDataStorage().computeIfAbsent(TYPE);
		brewingData.setDirty();
		return brewingData;
	}

	public Map<String, PlayerBrewingData> getPlayerData()
	{
		return players;
	}
}
