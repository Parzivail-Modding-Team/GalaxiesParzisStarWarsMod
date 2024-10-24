package dev.pswg.interaction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.pswg.Galaxies;
import net.minecraft.entity.LivingEntity;

public record LeftClickingEntityAttachment(boolean isUsingItemLeft, int itemLeftUseTimeLeft)
{
	public static final Codec<LeftClickingEntityAttachment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.fieldOf("isUsingItemLeft").forGetter(LeftClickingEntityAttachment::isUsingItemLeft),
			Codec.INT.fieldOf("itemLeftUseTimeLeft").forGetter(LeftClickingEntityAttachment::itemLeftUseTimeLeft)
	).apply(instance, LeftClickingEntityAttachment::new));

	public static LeftClickingEntityAttachment get(LivingEntity entity)
	{
		return entity.getAttachedOrCreate(Galaxies.LEFT_CLICKING_ATTACHMENT, () -> new LeftClickingEntityAttachment(
				false,
				0
		));
	}

	public LeftClickingEntityAttachment withIsLeftUsing(boolean isLeftUsing)
	{
		return new LeftClickingEntityAttachment(isLeftUsing, itemLeftUseTimeLeft);
	}
}
