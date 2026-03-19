package dev.pswg.feature.brewing;

import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;

public class PlayerBrewingData
{
	public PlayerBrewingData(Map<Holder<MobEffect>, Boolean> map)
	{

	}

	public HashMap<Holder<MobEffect>, Boolean> foundEffects;

	public static Codec<PlayerBrewingData> createCodec()
	{
		return Codec.unboundedMap(MobEffect.CODEC, Codec.BOOL).xmap(PlayerBrewingData::new, PlayerBrewingData::getFoundEffects);
	}

	public HashMap<Holder<MobEffect>, Boolean> getFoundEffects()
	{
		return foundEffects;
	}
}
