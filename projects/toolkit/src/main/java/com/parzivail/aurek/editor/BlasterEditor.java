package com.parzivail.aurek.editor;

import com.parzivail.pswg.api.BlasterTransformer;
import com.parzivail.pswg.api.PswgContent;
import com.parzivail.pswg.client.render.item.BlasterItemRenderer;
import com.parzivail.pswg.client.render.p3d.P3dModel;
import com.parzivail.pswg.item.blaster.BlasterItem;
import com.parzivail.pswg.item.blaster.data.BlasterDescriptor;
import com.parzivail.pswg.item.blaster.data.BlasterTag;
import com.parzivail.util.math.MatrixStackUtil;
import com.rits.cloning.Cloner;
import imgui.flag.ImGuiDataType;
import imgui.internal.ImGui;
import imgui.type.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;

import java.util.HashMap;
import java.util.Map;

public class BlasterEditor implements IDirectItemEditor
{
	private static class Transformer implements BlasterTransformer
	{
		public float yaw;
		public float pitch;
		public float roll;

		public float x;
		public float y;
		public float z;

		public float sx;
		public float sy;
		public float sz;

		public float handScale = 1;

		@Override
		public void transformHand(MatrixStack matrices, P3dModel model, BlasterTag bt, BlasterDescriptor bd, BlasterItemRenderer.AttachmentSuperset attachments, ModelTransformation.Mode renderMode, int light, float tickDelta, float opacity)
		{
			MatrixStackUtil.scalePos(matrices, handScale, handScale, handScale);
		}

		@Override
		public void preTransform(MatrixStack matrices, P3dModel model, BlasterTag bt, BlasterDescriptor bd, BlasterItemRenderer.AttachmentSuperset attachmentSet, ModelTransformation.Mode mode, int light, float tickDelta, float opacity)
		{
			matrices.translate(x, y, z);
			matrices.multiply(new Quaternionf().rotationYXZ(
					yaw * MathHelper.RADIANS_PER_DEGREE,
					pitch * MathHelper.RADIANS_PER_DEGREE,
					roll * MathHelper.RADIANS_PER_DEGREE
			));
			MatrixStackUtil.scalePos(matrices, sx, sy, sz);
		}

		@Override
		public void postTransform(MatrixStack matrices, P3dModel model, BlasterTag bt, BlasterDescriptor bd, BlasterItemRenderer.AttachmentSuperset attachments, ModelTransformation.Mode mode, int light, float tickDelta, float opacity)
		{

		}
	}

	public static final Transformer TRANSFORMER = new Transformer();

	private static final Cloner CLONER = new Cloner();
	private static Map<Identifier, BlasterDescriptor> originalPresets = null;

	private BlasterEditor()
	{
	}

	@Override
	public void process(MinecraftClient client, ItemStack stack)
	{
		if (originalPresets == null)
		{
			originalPresets = new HashMap<>();

			for (var entry : PswgContent.getBlasterPresets().entrySet())
				// We directly modify the blaster descriptor in memory, so a deep clone is required to enable "reset to default"
				originalPresets.put(entry.getKey(), CLONER.shallowClone(entry.getValue()));
		}

		if (!(stack.getItem() instanceof BlasterItem blaster))
			return;

		if (ImGui.beginTabBar("scopes"))
		{
			if (ImGui.beginTabItem("Global Render Data"))
			{
				var f = new ImFloat(TRANSFORMER.yaw);
				if (ImGui.inputFloat("Yaw", f, 0.5f))
					TRANSFORMER.yaw = f.get();

				f.set(TRANSFORMER.pitch);
				if (ImGui.inputFloat("Pitch", f, 0.5f))
					TRANSFORMER.pitch = f.get();

				f.set(TRANSFORMER.roll);
				if (ImGui.inputFloat("Roll", f, 0.5f))
					TRANSFORMER.roll = f.get();

				f.set(TRANSFORMER.x);
				if (ImGui.inputFloat("X", f, 0.05f))
					TRANSFORMER.x = f.get();

				f.set(TRANSFORMER.y);
				if (ImGui.inputFloat("Y", f, 0.05f))
					TRANSFORMER.y = f.get();

				f.set(TRANSFORMER.z);
				if (ImGui.inputFloat("Z", f, 0.05f))
					TRANSFORMER.z = f.get();

				f.set(TRANSFORMER.sx);
				if (ImGui.inputFloat("Scale X", f, 0.05f))
					TRANSFORMER.sx = f.get();

				f.set(TRANSFORMER.sy);
				if (ImGui.inputFloat("Scale Y", f, 0.05f))
					TRANSFORMER.sy = f.get();

				f.set(TRANSFORMER.sz);
				if (ImGui.inputFloat("Scale Z", f, 0.05f))
					TRANSFORMER.sz = f.get();

				f.set(TRANSFORMER.handScale);
				if (ImGui.inputFloat("Hand Scale", f, 0.05f))
					TRANSFORMER.handScale = f.get();

				ImGui.endTabItem();
			}

			var bd = BlasterItem.getBlasterDescriptor(stack, false);

			if (ImGui.beginTabItem(String.format("%s Type Data", bd.id.toString())))
			{
				var f = new ImFloat(bd.damage);
				if (ImGui.inputFloat("Damage", f, 0.5f))
					bd.damage = f.get();

				f.set(bd.range);
				if (ImGui.inputFloat("Range", f, 1))
					bd.range = f.get();

				f.set(bd.weight);
				if (ImGui.inputFloat("Weight", f, 0.5f))
					bd.weight = f.get();

				f.set(bd.boltHue);
				if (ImGui.inputFloat("Bolt Hue", f, 0.05f))
					bd.boltHue = f.get();

				f.set(bd.boltLength);
				if (ImGui.inputFloat("Bolt Length", f, 0.25f))
					bd.boltLength = f.get();

				f.set(bd.boltRadius);
				if (ImGui.inputFloat("Bolt Radius", f, 0.25f))
					bd.boltRadius = f.get();

				var i = new ImInt(bd.magazineSize);
				if (ImGui.inputInt("Magazine Size", i))
					bd.magazineSize = i.get();

				var iPtr = new int[1];
				iPtr[0] = bd.automaticRepeatTime;
				if (ImGui.sliderInt("Auto Repeat Time", iPtr, 1, 100))
					bd.automaticRepeatTime = iPtr[0];

				iPtr[0] = bd.burstRepeatTime;
				if (ImGui.sliderInt("Burst Repeat Time", iPtr, 1, 100))
					bd.burstRepeatTime = iPtr[0];

				iPtr[0] = bd.burstSize;
				if (ImGui.sliderInt("Burst Size", iPtr, 1, 10))
					bd.burstSize = iPtr[0];

				iPtr[0] = bd.burstGap;
				if (ImGui.sliderInt("Burst Gap", iPtr, 1, 10))
					bd.burstGap = iPtr[0];

				iPtr[0] = bd.quickdrawDelay;
				if (ImGui.sliderInt("Quickdraw Delay", iPtr, 1, 100))
					bd.quickdrawDelay = iPtr[0];

				if (ImGui.button("Discard Changes"))
					CLONER.copyPropertiesOfInheritedClass(originalPresets.get(bd.id), bd);

				ImGui.endTabItem();
			}

			if (ImGui.beginTabItem("Stack Data"))
			{
				var bt = new BlasterTag(stack.getOrCreateNbt());

				var b = new ImBoolean(bt.isAimingDownSights);
				if (ImGui.checkbox("Aiming Down Sights", b))
					bt.isAimingDownSights = b.get();

				b.set(bt.canBypassCooling);
				if (ImGui.checkbox("Can Bypass Cooling", b))
					bt.canBypassCooling = b.get();

				var i = new ImInt(bt.shotsRemaining);
				if (ImGui.inputInt("Shots Remaining", i))
					bt.shotsRemaining = i.get();

				i.set(bt.heat);
				if (ImGui.inputInt("Heat", i))
					bt.heat = i.get();

				i.set(bt.ventingHeat);
				if (ImGui.inputInt("Venting Heat", i))
					bt.ventingHeat = i.get();

				var s = new ImShort(bt.burstCounter);
				if (ImGui.inputScalar("Burst Counter", ImGuiDataType.U16, s))
					bt.burstCounter = s.get();

				s.set(bt.shotTimer);
				if (ImGui.inputScalar("Shot Timer", ImGuiDataType.U16, s))
					bt.shotTimer = s.get();

				s.set(bt.timeSinceLastShot);
				if (ImGui.inputScalar("Time Since Last Shot", ImGuiDataType.U16, s))
					bt.timeSinceLastShot = s.get();

				s.set(bt.passiveCooldownTimer);
				if (ImGui.inputScalar("Passive Cooldown Timer", ImGuiDataType.U16, s))
					bt.passiveCooldownTimer = s.get();

				s.set(bt.overchargeTimer);
				if (ImGui.inputScalar("Overcharge Timer", ImGuiDataType.U16, s))
					bt.overchargeTimer = s.get();

				s.set(bt.readyTimer);
				if (ImGui.inputScalar("Ready Timer", ImGuiDataType.U16, s))
					bt.readyTimer = s.get();

				var l = new ImLong(bt.uid);
				if (ImGui.inputScalar("Serial Number", ImGuiDataType.U64, l))
					bt.uid = l.get();

				i.set(bt.attachmentBitmask);
				if (ImGui.inputInt("Attachment Bitmask", i))
					bt.attachmentBitmask = i.get();

				bt.serializeAsSubtag(stack);
				ImGui.endTabItem();
			}

			ImGui.endTabBar();
		}
	}

	public static void register()
	{
		EDITORS.put(BlasterItem.class, new BlasterEditor());
	}
}
