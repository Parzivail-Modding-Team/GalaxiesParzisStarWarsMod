package dev.pswg.item.drill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record DrillExtractionInstance(int blocksExtracted, int blocksToExtract, List<BlockPos> markedBlocks)
{
	public static final Codec<DrillExtractionInstance> CODEC = RecordCodecBuilder.create(drillPropertiesInstance ->
		drillPropertiesInstance.group(
				Codec.INT.fieldOf("blocksExtracted").forGetter(DrillExtractionInstance::blocksExtracted),
				Codec.INT.fieldOf("blocksToExtract").forGetter(DrillExtractionInstance::blocksToExtract),
				BlockPos.CODEC.listOf().fieldOf("markedBlocks").forGetter(DrillExtractionInstance::markedBlocks)
		).apply(drillPropertiesInstance, DrillExtractionInstance::new)
	);
	public static final StreamCodec<RegistryFriendlyByteBuf, DrillExtractionInstance> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.INT,
			DrillExtractionInstance::blocksExtracted,
			ByteBufCodecs.INT,
			DrillExtractionInstance::blocksToExtract,
			ByteBufCodecs.fromCodec(BlockPos.CODEC.listOf()),
			DrillExtractionInstance::markedBlocks,
			DrillExtractionInstance::new
	);
	public static class Builder {
		private int blocksExtracted;
		private int blocksToExtract;
		private List<BlockPos> markedBlocks;

		public DrillExtractionInstance.Builder blocksExtracted(final int blocksExtracted) {
			this.blocksExtracted = blocksExtracted;
			return this;
		}
		public DrillExtractionInstance.Builder blocksToExtract(final int blocksToExtract) {
			this.blocksToExtract = blocksToExtract;
			return this;
		}
		public DrillExtractionInstance.Builder markedBlocks(final List<BlockPos> markedBlocks){
			this.markedBlocks = markedBlocks;
			return this;
		}

		public DrillExtractionInstance build() {
			return new DrillExtractionInstance(this.blocksExtracted, this.blocksToExtract, this.markedBlocks);
		}
	}
}
