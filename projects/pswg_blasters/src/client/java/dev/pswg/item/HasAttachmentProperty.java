package dev.pswg.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record HasAttachmentProperty(Identifier attachmentSlot, Identifier attachmentId) implements BooleanProperty
{
	public static final MapCodec<HasAttachmentProperty> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					Identifier.CODEC.fieldOf("attachmentSlot").forGetter(HasAttachmentProperty::attachmentSlot),
					Identifier.CODEC.fieldOf("attachmentId").forGetter(HasAttachmentProperty::attachmentId)
			).apply(instance, HasAttachmentProperty::new)
	);

	@Override
	public boolean test(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext)
	{
		var attachments = BlasterItem.getAttachments(stack);
		return attachmentId.equals(attachments.applied().getOrDefault(attachmentSlot(), null));
	}

	@Override
	public MapCodec<HasAttachmentProperty> getCodec()
	{
		return CODEC;
	}
}
