package dev.pswg.item.drill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record DrillExtractorProperties(float extractionSpeed, float extractionMultiplier, int maxBlockCount)
{
	public static final Codec<DrillExtractorProperties> CODEC = RecordCodecBuilder.create(drillPropertiesInstance -> 
		drillPropertiesInstance.group(
				Codec.FLOAT.fieldOf("extractionSpeed").forGetter(DrillExtractorProperties::extractionSpeed),
				Codec.FLOAT.fieldOf("extractionMultiplier").forGetter(DrillExtractorProperties::extractionMultiplier),
				Codec.INT.fieldOf("blocksExtracted").forGetter(DrillExtractorProperties::maxBlockCount)
		).apply(drillPropertiesInstance, DrillExtractorProperties::new)
	);
	public static final StreamCodec<RegistryFriendlyByteBuf, DrillExtractorProperties> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.FLOAT,
			DrillExtractorProperties::extractionSpeed,
			ByteBufCodecs.FLOAT,
			DrillExtractorProperties::extractionMultiplier,
			ByteBufCodecs.INT,
			DrillExtractorProperties::maxBlockCount,
			DrillExtractorProperties::new
	);
	public static class Builder {
		private float extractionSpeed;
		private float extractionMultiplier;
		private int maxBlockCount;

		public DrillExtractorProperties.Builder extractionSpeed(final float extractionSpeed) {
			this.extractionSpeed = extractionSpeed;
			return this;
		}

		public DrillExtractorProperties.Builder extractionMultiplier(final float extractionMultiplier) {
			this.extractionMultiplier = extractionMultiplier;
			return this;
		}

		public DrillExtractorProperties.Builder maxBlockCount(final int maxBlockCount) {
			this.maxBlockCount = maxBlockCount;
			return this;
		}

		public DrillExtractorProperties build() {
			return new DrillExtractorProperties(this.extractionSpeed, this.extractionMultiplier, this.maxBlockCount);
		}
	}
}
