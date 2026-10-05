package dev.pswg.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record HasAttachmentProperty(Identifier attachmentSlot, Identifier attachmentId) implements ConditionalItemModelProperty
{
	public static final MapCodec<HasAttachmentProperty> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					Identifier.CODEC.fieldOf("attachmentSlot").forGetter(HasAttachmentProperty::attachmentSlot),
					Identifier.CODEC.fieldOf("attachmentId").forGetter(HasAttachmentProperty::attachmentId)
			).apply(instance, HasAttachmentProperty::new)
	);

	@Override
	public boolean get(ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext)
	{
		var level = world != null ? world : Minecraft.getInstance().level;
		return attachmentId.equals(BlasterItem.getAttachments(stack).applied().get(attachmentSlot))
		       && BlasterItem.getActiveAttachments(level, stack).map(active -> active.containsKey(attachmentSlot)).orElse(false);
	}

	@Override
	public MapCodec<HasAttachmentProperty> type()
	{
		return CODEC;
	}
}
