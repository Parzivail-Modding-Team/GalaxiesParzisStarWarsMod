package dev.pswg.mixin.client.accesors;

import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(PoseStack.class)
public interface PoseStackAccessor
{
    @Accessor("poses")
    List<PoseStack.Pose> getPoses();

    @Accessor("lastIndex")
    int getLastIndex();
}