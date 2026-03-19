package dev.pswg.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record HasAttachmentProperty(ResourceLocation attachmentSlot, ResourceLocation attachmentId) implements ConditionalItemModelProperty
{
	public static final MapCodec<HasAttachmentProperty> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					ResourceLocation.CODEC.fieldOf("attachmentSlot").forGetter(HasAttachmentProperty::attachmentSlot),
					ResourceLocation.CODEC.fieldOf("attachmentId").forGetter(HasAttachmentProperty::attachmentId)
			).apply(instance, HasAttachmentProperty::new)
	);

	@Override
	public boolean get(ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext)
	{
		var attachments = BlasterItem.getAttachments(stack);
		return attachmentId.equals(attachments.applied().getOrDefault(attachmentSlot(), null));
	}

	@Override
	public MapCodec<HasAttachmentProperty> type()
	{
		return CODEC;
	}
}
