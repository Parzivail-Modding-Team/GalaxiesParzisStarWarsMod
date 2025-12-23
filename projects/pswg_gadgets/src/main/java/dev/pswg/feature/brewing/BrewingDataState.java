package dev.pswg.feature.brewing;

import com.mojang.serialization.Codec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class BrewingDataState extends PersistentState
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

	public static final Codec<BrewingDataState> CODEC = Codec.unboundedMap(Codecs.NON_EMPTY_STRING, PlayerBrewingData.createCodec()).xmap(
			BrewingDataState::new,
			BrewingDataState::getPlayerData
	);

	private static final PersistentStateType<BrewingDataState> TYPE = new PersistentStateType<>(
			"player_brewing_data",
			BrewingDataState::new,
			CODEC,
			null
	);

	// TODO : Sync with the client when getting, since it often is also when an update happens
	public static BrewingDataState getBrewingDataState(MinecraftServer server)
	{
		ServerWorld world = server.getWorld(World.OVERWORLD);
		if (world == null)
			return new BrewingDataState();
		BrewingDataState brewingData = world.getPersistentStateManager().getOrCreate(TYPE);
		brewingData.markDirty();
		return brewingData;
	}

	public Map<String, PlayerBrewingData> getPlayerData()
	{
		return players;
	}
}
