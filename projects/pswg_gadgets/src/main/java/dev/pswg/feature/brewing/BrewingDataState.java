package dev.pswg.feature.brewing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.pswg.Gadgets;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

	private static final PersistentStateType<BrewingDataState> type = new PersistentStateType<BrewingDataState>(
			"player_brewing_data",
			BrewingDataState::new,
			CODEC,
			null
	);

	public static BrewingDataState getBrewingDataState(MinecraftServer server)
	{
		ServerWorld world = server.getWorld(World.OVERWORLD);
		if (world == null)
			return new BrewingDataState();
		BrewingDataState brewingData = world.getPersistentStateManager().getOrCreate(type);
		brewingData.markDirty();
		return brewingData;
	}

	public Map<String, PlayerBrewingData> getPlayerData()
	{
		return players;
	}
}
