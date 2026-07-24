package dev.pswg.item.drill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record DrillProperties(String extractorId, String drillId, String capsuleId)
{
	public static final Codec<DrillProperties> CODEC = RecordCodecBuilder.create(drillPropertiesInstance ->
		drillPropertiesInstance.group(
				Codec.STRING.fieldOf("extractorId").forGetter(DrillProperties::extractorId),
				Codec.STRING.fieldOf("drillId").forGetter(DrillProperties::drillId),
				Codec.STRING.fieldOf("capsuleId").forGetter(DrillProperties::capsuleId)
		).apply(drillPropertiesInstance, DrillProperties::new)
	);
	public static final StreamCodec<RegistryFriendlyByteBuf, DrillProperties> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8,
			DrillProperties::extractorId,
			ByteBufCodecs.STRING_UTF8,
			DrillProperties::drillId,
			ByteBufCodecs.STRING_UTF8,
			DrillProperties::capsuleId,
			DrillProperties::new
	);
	public DrillComponents.Drill getDrill(){
		return DrillComponents.drillMap.getOrDefault(drillId, DrillComponents.getDefaultDrill());
	}
	public DrillComponents.Extractor getExtractor(){
		return DrillComponents.extractorMap.getOrDefault(extractorId, DrillComponents.getDefaultExtractor());
	}
	public DrillComponents.Capsule getCapsule(){
		return DrillComponents.capsuleMap.getOrDefault(capsuleId, DrillComponents.getDefaultCapsule());
	}
	public int getDurability(){
		int base = getDrill().durabilityBase;
		int add = getCapsule().durabilityBoost;
		float mod = getExtractor().durabilityModifier;
		return (int)(base * mod + add);
	}
}
