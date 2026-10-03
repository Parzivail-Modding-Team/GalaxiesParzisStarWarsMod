package dev.pswg.rendering.g3d;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.pswg.model.g3d.G3dPose;
import dev.pswg.model.g3d.G3dRig;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

/**
 * Binds an G3D armor rig to a vanilla-animated humanoid.
 */
public final class G3dArmorPose
{
	/**
	 * Native parts and the equipment slots represented by their G3D anchors.
	 */
	private enum Anchor
	{
		HEAD(EquipmentSlot.HEAD),
		BODY(EquipmentSlot.CHEST),
		WAIST(EquipmentSlot.LEGS),
		RIGHT_ARM(EquipmentSlot.CHEST),
		LEFT_ARM(EquipmentSlot.CHEST),
		RIGHT_LEG(EquipmentSlot.LEGS),
		LEFT_LEG(EquipmentSlot.LEGS),
		RIGHT_BOOT(EquipmentSlot.FEET),
		LEFT_BOOT(EquipmentSlot.FEET);

		/**
		 * The armor item that owns this anchor's geometry.
		 */
		private final EquipmentSlot _slot;

		/**
		 * Associates an anchor with one worn armor slot.
		 */
		Anchor(EquipmentSlot slot)
		{
			_slot = slot;
		}

		/**
		 * Resolves the matching part without modifying the shared humanoid model.
		 */
		private ModelPart part(HumanoidModel<?> model)
		{
			return switch (this)
			{
				case HEAD -> model.head;
				case BODY, WAIST -> model.body;
				case RIGHT_ARM -> model.rightArm;
				case LEFT_ARM -> model.leftArm;
				case RIGHT_LEG, RIGHT_BOOT -> model.rightLeg;
				case LEFT_LEG, LEFT_BOOT -> model.leftLeg;
			};
		}
	}

	/**
	 * Matches the group names used by the G3D humanoid armor template.
	 */
	private static Anchor anchor(String name)
	{
		return switch (name)
		{
			case "head" -> Anchor.HEAD;
			case "body" -> Anchor.BODY;
			case "waist" -> Anchor.WAIST;
			case "right_arm" -> Anchor.RIGHT_ARM;
			case "left_arm" -> Anchor.LEFT_ARM;
			case "right_leg" -> Anchor.RIGHT_LEG;
			case "left_leg" -> Anchor.LEFT_LEG;
			case "right_boot" -> Anchor.RIGHT_BOOT;
			case "left_boot" -> Anchor.LEFT_BOOT;
			default -> null;
		};
	}

	/**
	 * The loaded rig whose identity invalidates an armor renderer's binding cache.
	 */
	private final G3dRig _rig;

	/**
	 * Named anchors; null entries inherit their parent's posed transform.
	 */
	private final Anchor[] _anchors;

	/**
	 * Authored model-space rotation and scale retained at each bound pivot.
	 */
	private final Matrix4fc[] _bases;

	/**
	 * Inherited visibility masks: bit one is wide, bit two is slim.
	 */
	private final int[] _variants;

	/**
	 * Precomputes anchor data once for a loaded model.
	 */
	public G3dArmorPose(G3dRig rig)
	{
		_rig = rig;
		_anchors = new Anchor[rig.nodes().size()];
		_bases = new Matrix4fc[_anchors.length];
		_variants = new int[_anchors.length];

		var rest = new G3dPose(rig);

		for (int index = 0; index < _anchors.length; index++)
		{
			var node = rig.nodes().get(index);
			_anchors[index] = anchor(node.id());
			_variants[index] = node.parent() < 0 ? 3 : _variants[node.parent()];

			if (node.id().equals("slim") || node.id().endsWith("_slim"))
				_variants[index] &= 2;

			if (node.id().equals("default") || node.id().endsWith("_default"))
				_variants[index] &= 1;

			if (_anchors[index] != null)
				_bases[index] = new Matrix4f(rest.nodeMatrix(index)).setTranslation(0, 0, 0);
		}
	}

	/**
	 * Gets the exact reload-bound rig used by this binding.
	 */
	public G3dRig rig()
	{
		return _rig;
	}

	/**
	 * Captures independent matrices for a queued armor submission. The native root,
	 * part translations, ZYX rotations, and scales come from ModelPart itself.
	 * Flipping X and Y converts G3D's upright, feet-origin frame into the
	 * native humanoid frame without reflecting triangle winding. Hidden slots and
	 * inactive slim/default branches collapse before geometry reaches the renderer.
	 */
	public Matrix4fc[] capture(HumanoidModel<?> model, EquipmentSlot slot, boolean slim)
	{
		var result = new Matrix4fc[_anchors.length];
		var stack = new PoseStack();

		model.root().translateAndRotate(stack);
		var local = new Matrix4f();

		for (int index = 0; index < result.length; index++)
		{
			var node = _rig.nodes().get(index);
			var anchor = _anchors[index];
			var matrix = new Matrix4f();
			boolean variant = (_variants[index] & (slim ? 2 : 1)) != 0;

			if (anchor != null && anchor._slot == slot && model.root().visible && anchor.part(model).visible && variant)
			{
				stack.pushPose();
				try
				{
					anchor.part(model).translateAndRotate(stack);
					stack.scale(-1, -1, 1);
					matrix.set(stack.last().pose()).mul(_bases[index]);
				}
				finally
				{
					stack.popPose();
				}
			}
			else if (anchor == null && node.parent() >= 0 && variant)
			{
				node.restTransform().matrix(local);
				result[node.parent()].mul(local, matrix);
			}
			else
			{
				matrix.scaling(0);
			}

			result[index] = matrix;
		}

		return result;
	}
}
