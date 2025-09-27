package dev.pswg.tints;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.render.item.tint.PotionTintSource;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;

public record SwgDrinkTintSource(int defaultColor) implements TintSource
{
	public static final MapCodec<SwgDrinkTintSource> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(Codecs.RGB.fieldOf("default").forGetter(SwgDrinkTintSource::defaultColor)).apply(instance, SwgDrinkTintSource::new)
	);

	public SwgDrinkTintSource()
	{
		this(-13083194);
	}

	@Override
	public int getTint(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user)
	{
		PotionContentsComponent potionContentsComponent = stack.get(DataComponentTypes.POTION_CONTENTS);
		DyedColorComponent dyedColorComponent = stack.get(DataComponentTypes.DYED_COLOR);
		if (dyedColorComponent != null)
			return DyedColorComponent.getColor(stack, this.defaultColor);
		return potionContentsComponent != null ? ColorHelper.fullAlpha(potionContentsComponent.getColor(this.defaultColor)) : ColorHelper.fullAlpha(this.defaultColor);
	}

	@Override
	public MapCodec<? extends TintSource> getCodec()
	{
		return CODEC;
	}
}
