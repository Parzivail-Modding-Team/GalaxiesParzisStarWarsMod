package dev.pswg.item.drill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record DrillProperties(DrillComponents.Extractor extractor, DrillComponents.Drill drill, DrillComponents.Capsule capsule)
{
	public static final Codec<DrillProperties> CODEC = RecordCodecBuilder.create(drillPropertiesInstance ->
		drillPropertiesInstance.group(
				DrillComponents.Extractor.CODEC.fieldOf("extractor").forGetter(DrillProperties::extractor),
				DrillComponents.Drill.CODEC.fieldOf("drill").forGetter(DrillProperties::drill),
				DrillComponents.Capsule.CODEC.fieldOf("capsule").forGetter(DrillProperties::capsule)
		).apply(drillPropertiesInstance, DrillProperties::new)
	);
	public static final StreamCodec<RegistryFriendlyByteBuf, DrillProperties> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.fromCodec(DrillComponents.Extractor.CODEC),
			DrillProperties::extractor,
			ByteBufCodecs.fromCodec(DrillComponents.Drill.CODEC),
			DrillProperties::drill,
			ByteBufCodecs.fromCodec(DrillComponents.Capsule.CODEC),
			DrillProperties::capsule,
			DrillProperties::new
	);
	public DrillComponents.Drill getDrill(){
		return drill;
	}
	public DrillComponents.Extractor getExtractor(){
		return extractor;
	}
	public DrillComponents.Capsule getCapsule(){
		return capsule;
	}
	public int getDurability(){
		int base = getDrill().durabilityBase;
		int add = getCapsule().durabilityBoost;
		float mod = getExtractor().durabilityModifier;
		return (int)(base * mod + add);
	}
}
