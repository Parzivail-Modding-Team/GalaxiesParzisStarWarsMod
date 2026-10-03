package dev.pswg.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.pswg.Galaxies;
import dev.pswg.mixin.client.accesors.PoseStackAccessor;
import dev.pswg.model.g3d.G3dCompiler;
import dev.pswg.model.g3d.G3dResources;
import dev.pswg.model.g3d.G3dRig;
import dev.pswg.rendering.g3d.G3dClientModels;
import dev.pswg.rendering.g3d.G3dRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4fc;

import java.util.List;

public class GalaxiesArmorRenderer implements ArmorRenderer
{
	@Override
	public void render(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, ItemStack itemStack, HumanoidRenderState humanoidRenderState, EquipmentSlot equipmentSlot, int i, HumanoidModel<HumanoidRenderState> humanoidModel)
	{
		PoseStackAccessor accessor = (PoseStackAccessor) poseStack;

		List<PoseStack.Pose> poses = accessor.getPoses();

		Matrix4fc[] matrices = new Matrix4fc[accessor.getLastIndex() + 1];

		for (int j = 0; j <= accessor.getLastIndex(); j++)
			matrices[j] = poses.get(j).pose();

		//humanoidModel.body.

		var model = G3dClientModels.get(Galaxies.id("armor/stormtrooper")).get().model();
		var node = model.rig().nodes().get(0);
		var renderer = G3dClientModels.get(Galaxies.id("armor/stormtrooper")).get();

		renderer.submit(renderer.restPose(), poseStack, submitNodeCollector, humanoidRenderState.lightCoords, humanoidRenderState.outlineColor, 0, new int[0], false, false, 0);
	}
}
