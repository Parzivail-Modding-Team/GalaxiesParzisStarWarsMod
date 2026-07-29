package dev.pswg.item.drill;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.pswg.container.GadgetsItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public record HasCapsuleProperty(Identifier capsuleId) implements ConditionalItemModelProperty
{
	public static final MapCodec<HasCapsuleProperty> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					Identifier.CODEC.fieldOf("capsule").forGetter(HasCapsuleProperty::capsuleId)
			).apply(instance, HasCapsuleProperty::new)
	);

	@Override
	public MapCodec<? extends ConditionalItemModelProperty> type()
	{
		return CODEC;
	}

	@Override
	public boolean get(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed, ItemDisplayContext displayContext)
	{
		DrillProperties properties = itemStack.get(GadgetsItems.Components.DRILL_EXTRACTOR_PROPERTIES);
		if(properties == null)
			return false;
		return Objects.equals(properties.capsule().id(), capsuleId);
	}
}
