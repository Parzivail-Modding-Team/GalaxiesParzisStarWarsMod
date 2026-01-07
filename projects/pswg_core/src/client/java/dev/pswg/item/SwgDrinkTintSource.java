package dev.pswg.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;

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
		if
		(dyedColorComponent != null)
			return DyedColorComponent.getColor(stack, this.defaultColor);
		return potionContentsComponent != null ? ColorHelper.fullAlpha(getColor(this.defaultColor, potionContentsComponent)) : ColorHelper.fullAlpha(this.defaultColor);
	}

	public int getColor(int defaultColor, PotionContentsComponent component)
	{
		return component.customColor().isPresent() ? component.customColor().get() : mixColors(component.getEffects()).orElse(defaultColor);
	}

	public static OptionalInt mixColors(Iterable<StatusEffectInstance> effects)
	{
		int i = 0;
		int j = 0;
		int k = 0;
		int l = 0;

		for (StatusEffectInstance statusEffectInstance : effects)
		{
			int m = statusEffectInstance.getEffectType().value().getColor();
			int n = statusEffectInstance.getAmplifier() + 1;
			i += n * ColorHelper.getRed(m);
			j += n * ColorHelper.getGreen(m);
			k += n * ColorHelper.getBlue(m);
			l += n;
		}

		return l == 0 ? OptionalInt.empty() : OptionalInt.of(ColorHelper.getArgb(i / l, j / l, k / l));
	}

	@Override
	public MapCodec<? extends TintSource> getCodec()
	{
		return CODEC;
	}
}
