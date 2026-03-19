package dev.pswg.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.DyedItemColor;

public record SwgDrinkTintSource(int defaultColor) implements ItemTintSource
{
	public static final MapCodec<SwgDrinkTintSource> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(ExtraCodecs.RGB_COLOR_CODEC.fieldOf("default").forGetter(SwgDrinkTintSource::defaultColor)).apply(instance, SwgDrinkTintSource::new)
	);

	public SwgDrinkTintSource()
	{
		this(-13083194);
	}

	@Override
	public int calculate(ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity user)
	{
		PotionContents potionContentsComponent = stack.get(DataComponents.POTION_CONTENTS);
		DyedItemColor dyedColorComponent = stack.get(DataComponents.DYED_COLOR);
		if
		(dyedColorComponent != null)
			return DyedItemColor.getOrDefault(stack, this.defaultColor);
		return potionContentsComponent != null ? ARGB.opaque(getColor(this.defaultColor, potionContentsComponent)) : ARGB.opaque(this.defaultColor);
	}

	public int getColor(int defaultColor, PotionContents component)
	{
		return component.customColor().isPresent() ? component.customColor().get() : mixColors(component.getAllEffects()).orElse(defaultColor);
	}

	public static OptionalInt mixColors(Iterable<MobEffectInstance> effects)
	{
		int i = 0;
		int j = 0;
		int k = 0;
		int l = 0;

		for (MobEffectInstance statusEffectInstance : effects)
		{
			int m = statusEffectInstance.getEffect().value().getColor();
			int n = statusEffectInstance.getAmplifier() + 1;
			i += n * ARGB.red(m);
			j += n * ARGB.green(m);
			k += n * ARGB.blue(m);
			l += n;
		}

		return l == 0 ? OptionalInt.empty() : OptionalInt.of(ARGB.color(i / l, j / l, k / l));
	}

	@Override
	public MapCodec<? extends ItemTintSource> type()
	{
		return CODEC;
	}
}
