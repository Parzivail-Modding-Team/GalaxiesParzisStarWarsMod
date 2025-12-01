package dev.pswg.feature.brewing;

import com.mojang.serialization.Codec;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.dynamic.Codecs;

import java.util.HashMap;
import java.util.Map;

public class PlayerBrewingData
{
	public PlayerBrewingData(Map<RegistryEntry<StatusEffect>, Boolean> map)
	{

	}

	public HashMap<RegistryEntry<StatusEffect>, Boolean> foundEffects;

	public static Codec<PlayerBrewingData> createCodec()
	{
		return Codec.unboundedMap(StatusEffect.ENTRY_CODEC, Codec.BOOL).xmap(PlayerBrewingData::new, PlayerBrewingData::getFoundEffects);
	}

	public HashMap<RegistryEntry<StatusEffect>, Boolean> getFoundEffects()
	{
		return foundEffects;
	}
}
