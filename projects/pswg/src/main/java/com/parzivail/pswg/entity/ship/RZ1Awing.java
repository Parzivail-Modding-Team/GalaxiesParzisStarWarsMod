package com.parzivail.pswg.entity.ship;

import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.pswg.entity.rigs.RigRZ1;
import com.parzivail.pswg.entity.rigs.RigT65B;
import com.parzivail.pswg.features.blasters.BlasterUtil;
import com.parzivail.util.entity.collision.CapsuleVolume;
import com.parzivail.util.entity.collision.ICollisionVolume;
import com.parzivail.util.entity.collision.IComplexEntityHitbox;
import com.parzivail.util.entity.collision.SweptTriangleVolume;
import com.parzivail.util.math.ColorUtil;
import com.parzivail.util.math.MathUtil;
import com.parzivail.util.math.QuatUtil;
import com.parzivail.util.math.Transform;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix4f;

public class RZ1Awing extends ShipEntity implements IComplexEntityHitbox
{
	private static final CapsuleVolume COCKPIT = new CapsuleVolume(new Vec3d(0, -0.077873, -1.0924), new Vec3d(0, 0.172123, 2.7826), 0.8);
	private static final CapsuleVolume VOL_CANNON_RIGHT = new CapsuleVolume(new Vec3d(4, -0.702874, -1.0924), new Vec3d(4, -0.702874, 2.4076), 0.3);
	private static final CapsuleVolume VOL_CANNON_LEFT = new CapsuleVolume(new Vec3d(-4, -0.702874, -1.0924), new Vec3d(-4, -0.702874, 2.4076), 0.3);
	private static final CapsuleVolume VOL_ENGINE_RIGHT = new CapsuleVolume(new Vec3d(-2.1875, -1.015378, 4.5326), new Vec3d(-2.1875, -1.01538, 0.9076), 0.9);
	private static final CapsuleVolume VOL_ENGINE_LEFT = new CapsuleVolume(new Vec3d(2.1875, -1.015378, 4.5326), new Vec3d(2.1875, -1.01538, 0.9076), 0.9);
	private static final SweptTriangleVolume VOL_BODY_TOP_RIGHT_FRONTA = new SweptTriangleVolume(new Vec3d(-1.125, 0.047132, -1.2174), new Vec3d(-2.5, -0.702869, -1.3424), new Vec3d(-1.0625, -0.702869, -5.4674), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_RIGHT_FRONTB = new SweptTriangleVolume(new Vec3d(-1.125, -0.765371, -5.4674), new Vec3d(-2.5, -0.702869, -1.3424), new Vec3d(-1.0625, -0.702869, -5.4674), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_RIGHT_MID1A = new SweptTriangleVolume(new Vec3d(-1.125, 0.047132, -1.2174), new Vec3d(-2.5, -0.702869, -1.3424), new Vec3d(-1.375, 0.172126, 0.4076), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_RIGHT_MID1B = new SweptTriangleVolume(new Vec3d(-3.5, -0.452875, 0.4076), new Vec3d(-2.5, -0.702869, -1.3424), new Vec3d(-1.375, 0.172126, 0.4076), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_RIGHT_MID2A = new SweptTriangleVolume(new Vec3d(-3.5, -0.452875, 0.4076), new Vec3d(-2.0, 0.297125, 1.6576), new Vec3d(-1.375, 0.172126, 0.4076), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_RIGHT_MID2B = new SweptTriangleVolume(new Vec3d(-3.5, -0.452875, 0.4076), new Vec3d(-2.0, 0.297125, 1.6576), new Vec3d(-3.5, -0.452875, 1.6576), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_RIGHT_BACKA = new SweptTriangleVolume(new Vec3d(-2.0, 0.29711, 1.7826), new Vec3d(-2.0, 0.297125, 1.6576), new Vec3d(-3.5, -0.452875, 1.6576), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_RIGHT_BACKB = new SweptTriangleVolume(new Vec3d(-2.0, 0.29711, 1.7826), new Vec3d(-3.5, -0.45289, 1.7826), new Vec3d(-3.5, -0.452875, 1.6576), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_LEFT_FRONTA = new SweptTriangleVolume(new Vec3d(1.125, 0.047132, -1.2174), new Vec3d(2.5, -0.702869, -1.3424), new Vec3d(1.0625, -0.702869, -5.4674), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_LEFT_FRONTB = new SweptTriangleVolume(new Vec3d(1.125, -0.765371, -5.4674), new Vec3d(2.5, -0.702869, -1.3424), new Vec3d(1.0625, -0.702869, -5.4674), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_LEFT_MID1A = new SweptTriangleVolume(new Vec3d(1.125, 0.047132, -1.2174), new Vec3d(2.5, -0.702869, -1.3424), new Vec3d(1.375, 0.172126, 0.4076), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_LEFT_MID1B = new SweptTriangleVolume(new Vec3d(3.5, -0.452875, 0.4076), new Vec3d(2.5, -0.702869, -1.3424), new Vec3d(1.375, 0.172126, 0.4076), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_LEFT_MID2A = new SweptTriangleVolume(new Vec3d(3.5, -0.452875, 0.4076), new Vec3d(2.0, 0.297125, 1.6576), new Vec3d(1.375, 0.172126, 0.4076), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_LEFT_MID2B = new SweptTriangleVolume(new Vec3d(3.5, -0.452875, 0.4076), new Vec3d(2.0, 0.297125, 1.6576), new Vec3d(3.5, -0.452875, 1.6576), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_LEFT_BACKA = new SweptTriangleVolume(new Vec3d(2.0, 0.29711, 1.7826), new Vec3d(2.0, 0.297125, 1.6576), new Vec3d(3.5, -0.452875, 1.6576), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_LEFT_BACKB = new SweptTriangleVolume(new Vec3d(2.0, 0.29711, 1.7826), new Vec3d(3.5, -0.45289, 1.7826), new Vec3d(3.5, -0.452875, 1.6576), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_MIDDLE_FRONT_RIGHTA = new SweptTriangleVolume(new Vec3d(-1.125, 0.047132, -1.2174), new Vec3d(-1.0625, -0.702869, -5.4674), new Vec3d(-0.375, -0.077873, -1.9674), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_MIDDLE_FRONT_RIGHTB = new SweptTriangleVolume(new Vec3d(0, -0.70289, -5.4674), new Vec3d(-1.0625, -0.702869, -5.4674), new Vec3d(-0.375, -0.077873, -1.9674), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_MIDDLE_FRONT_RIGHTC = new SweptTriangleVolume(new Vec3d(0, -0.70289, -5.4674), new Vec3d(0, -0.077873, -1.9674), new Vec3d(-0.375, -0.077873, -1.9674), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_MIDDLE_FRONT_MID_RIGHT_FRONT = new SweptTriangleVolume(new Vec3d(-1.125, 0.047132, -1.2174), new Vec3d(-0.625, 0.048733, -0.8424), new Vec3d(-0.375, -0.077873, -1.9674), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_MIDDLE_FRONT_MID_RIGHT_MIDA = new SweptTriangleVolume(new Vec3d(-1.125, 0.047132, -1.2174), new Vec3d(-0.625, 0.048733, -0.8424), new Vec3d(-0.75, 0.173732, 0.4076), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_MIDDLE_FRONT_MID_RIGHT_MIDB = new SweptTriangleVolume(new Vec3d(-1.125, 0.047132, -1.2174), new Vec3d(-1.375, 0.172126, 0.4076), new Vec3d(-0.75, 0.173732, 0.4076), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_MIDDLE_FRONT_LEFTA = new SweptTriangleVolume(new Vec3d(1.125, 0.047132, -1.2174), new Vec3d(1.0625, -0.702869, -5.4674), new Vec3d(0.375, -0.077873, -1.9674), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_MIDDLE_FRONT_LEFTB = new SweptTriangleVolume(new Vec3d(0, -0.70289, -5.4674), new Vec3d(1.0625, -0.702869, -5.4674), new Vec3d(0.375, -0.077873, -1.9674), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_MIDDLE_FRONT_LEFTC = new SweptTriangleVolume(new Vec3d(0, -0.70289, -5.4674), new Vec3d(0, -0.077873, -1.9674), new Vec3d(0.375, -0.077873, -1.9674), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_MIDDLE_FRONT_MID_LEFT_FRONT = new SweptTriangleVolume(new Vec3d(1.125, 0.047132, -1.2174), new Vec3d(0.625, 0.048733, -0.8424), new Vec3d(0.375, -0.077873, -1.9674), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_MIDDLE_FRONT_MID_LEFT_MIDA = new SweptTriangleVolume(new Vec3d(1.125, 0.047132, -1.2174), new Vec3d(0.625, 0.048733, -0.8424), new Vec3d(0.75, 0.173732, 0.4076), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_MIDDLE_FRONT_MID_LEFT_MIDB = new SweptTriangleVolume(new Vec3d(1.125, 0.047132, -1.2174), new Vec3d(1.375, 0.172126, 0.4076), new Vec3d(0.75, 0.173732, 0.4076), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_MIDDLE_BACK_FRONTA = new SweptTriangleVolume(new Vec3d(-1.375, 0.172126, 0.4076), new Vec3d(1.375, 0.172126, 0.4076), new Vec3d(2, 0.297125, 1.6576), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_MIDDLE_BACK_FRONTB = new SweptTriangleVolume(new Vec3d(-1.375, 0.172126, 0.4076), new Vec3d(-2.0, 0.297125, 1.6576), new Vec3d(2, 0.297125, 1.6576), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_MIDDLE_BACK_BACKA = new SweptTriangleVolume(new Vec3d(-2.0, 0.297123, 3.5326), new Vec3d(-2.0, 0.297125, 1.6576), new Vec3d(2, 0.297125, 1.6576), 0.2);
	private static final SweptTriangleVolume VOL_BODY_TOP_MIDDLE_BACK_BACKB = new SweptTriangleVolume(new Vec3d(-2.0, 0.297123, 3.5326), new Vec3d(2.0, 0.297123, 3.5326), new Vec3d(2, 0.297125, 1.6576), 0.2);
	private static final SweptTriangleVolume VOL_BODY_SIDE_FRONT_RIGHTA = new SweptTriangleVolume(new Vec3d(-1.125, -0.827873, -5.4674), new Vec3d(-1.0, -0.827873, -5.4674), new Vec3d(-1.125, -0.765371, -5.4674), 0.2);
	private static final SweptTriangleVolume VOL_BODY_SIDE_FRONT_RIGHTB = new SweptTriangleVolume(new Vec3d(-1.0625, -0.702869, -5.4674), new Vec3d(-1.0, -0.827873, -5.4674), new Vec3d(-1.125, -0.765371, -5.4674), 0.2);
	private static final SweptTriangleVolume VOL_BODY_SIDE_FRONT_LEFTA = new SweptTriangleVolume(new Vec3d(1.125, -0.827873, -5.4674), new Vec3d(1.0, -0.827873, -5.4674), new Vec3d(1.125, -0.765371, -5.4674), 0.2);
	private static final SweptTriangleVolume VOL_BODY_SIDE_FRONT_LEFTB = new SweptTriangleVolume(new Vec3d(1.0625, -0.702869, -5.4674), new Vec3d(1.0, -0.827873, -5.4674), new Vec3d(1.125, -0.765371, -5.4674), 0.2);
	private static final SweptTriangleVolume VOL_BODY_SIDE_FRONTA = new SweptTriangleVolume(new Vec3d(-1.0625, -0.702869, -5.4674), new Vec3d(-1.0, -0.827873, -5.4674), new Vec3d(1.0, -0.827873, -5.4674), 0.2);
	private static final SweptTriangleVolume VOL_BODY_SIDE_FRONTB = new SweptTriangleVolume(new Vec3d(-1.0625, -0.702869, -5.4674), new Vec3d(1.0625, -0.70289, -5.4674), new Vec3d(1.0, -0.827873, -5.4674), 0.2);
	private static final SweptTriangleVolume VOL_BODY_SIDE_RIGHT_FRONTA = new SweptTriangleVolume(new Vec3d(-2.5, -0.702869, -1.3424), new Vec3d(-1.125, -0.827873, -5.4674), new Vec3d(-1.125, -0.765371, -5.4674), 0.2);
	private static final SweptTriangleVolume VOL_BODY_SIDE_RIGHT_FRONTB = new SweptTriangleVolume(new Vec3d(-2.5, -0.702869, -1.3424), new Vec3d(-1.125, -0.827873, -5.4674), new Vec3d(-2.5, -1.07787, -1.3424), 0.2);
	private static final SweptTriangleVolume VOL_BODY_SIDE_RIGHT_MIDDLEA = new SweptTriangleVolume(new Vec3d(-2.5, -0.702869, -1.3424), new Vec3d(-3.5, -1.07788, 0.4076), new Vec3d(-2.5, -1.07787, -1.3424), 0.2);
	private static final SweptTriangleVolume VOL_BODY_SIDE_RIGHT_MIDDLEB = new SweptTriangleVolume(new Vec3d(-2.5, -0.702869, -1.3424), new Vec3d(-3.5, -1.07788, 0.4076), new Vec3d(-3.5, -0.452875, 0.4076), 0.2);
	private static final SweptTriangleVolume VOL_BODY_SIDE_RIGHT_BACKA = new SweptTriangleVolume(new Vec3d(-3.5, -1.07788, 1.6576), new Vec3d(-3.5, -1.07788, 0.4076), new Vec3d(-3.5, -0.452875, 0.4076), 0.2);
	private static final SweptTriangleVolume VOL_BODY_SIDE_RIGHT_BACKB = new SweptTriangleVolume(new Vec3d(-3.5, -1.07788, 1.6576), new Vec3d(3.5, -0.452875, 1.6576), new Vec3d(-3.5, -0.452875, 0.4076), 0.2);
	private static final SweptTriangleVolume VOL_BODY_SIDE_LEFT_FRONTA = new SweptTriangleVolume(new Vec3d(2.5, -0.702869, -1.3424), new Vec3d(1.125, -0.827873, -5.4674), new Vec3d(1.125, -0.765371, -5.4674), 0.2);
	private static final SweptTriangleVolume VOL_BODY_SIDE_LEFT_FRONTB = new SweptTriangleVolume(new Vec3d(2.5, -0.702869, -1.3424), new Vec3d(1.125, -0.827873, -5.4674), new Vec3d(2.5, -1.07787, -1.3424), 0.2);
	private static final SweptTriangleVolume VOL_BODY_SIDE_LEFT_MIDDLEA = new SweptTriangleVolume(new Vec3d(2.5, -0.702869, -1.3424), new Vec3d(3.5, -1.07788, 0.4076), new Vec3d(2.5, -1.07787, -1.3424), 0.2);
	private static final SweptTriangleVolume VOL_BODY_SIDE_LEFT_MIDDLEB = new SweptTriangleVolume(new Vec3d(2.5, -0.702869, -1.3424), new Vec3d(3.5, -1.07788, 0.4076), new Vec3d(3.5, -0.452875, 0.4076), 0.2);
	private static final SweptTriangleVolume VOL_BODY_SIDE_LEFT_BACKA = new SweptTriangleVolume(new Vec3d(3.5, -1.07788, 1.6576), new Vec3d(3.5, -1.07788, 0.4076), new Vec3d(3.5, -0.452875, 0.4076), 0.2);
	private static final SweptTriangleVolume VOL_BODY_SIDE_LEFT_BACKB = new SweptTriangleVolume(new Vec3d(3.5, -1.07788, 1.6576), new Vec3d(3.5, -0.452875, 1.6576), new Vec3d(3.5, -0.452875, 0.4076), 0.2);
	private static final SweptTriangleVolume VOL_BODY_BACK_RIGHTA = new SweptTriangleVolume(new Vec3d(-3.5, -1.07788, 1.6576), new Vec3d(-3.5, -0.452875, 1.6576), new Vec3d(-2.5, -1.70288, 1.6576), 0.2);
	private static final SweptTriangleVolume VOL_BODY_BACK_RIGHTB = new SweptTriangleVolume(new Vec3d(-2.0, 0.297125, 1.6576), new Vec3d(-3.5, -0.452875, 1.6576), new Vec3d(-2.5, -1.70288, 1.6576), 0.2);
	private static final SweptTriangleVolume VOL_BODY_BACK_LEFTA = new SweptTriangleVolume(new Vec3d(3.5, -1.07788, 1.6576), new Vec3d(3.5, -0.452875, 1.6576), new Vec3d(2.5, -1.70288, 1.6576), 0.2);
	private static final SweptTriangleVolume VOL_BODY_BACK_LEFTB = new SweptTriangleVolume(new Vec3d(2.0, 0.297125, 1.6576), new Vec3d(3.5, -0.452875, 1.6576), new Vec3d(2.5, -1.70288, 1.6576), 0.2);
	private static final SweptTriangleVolume VOL_BODY_BACK_MIDA = new SweptTriangleVolume(new Vec3d(-2.0, 0.17212, 3.4076), new Vec3d(2.4375, -1.57788, 3.4076), new Vec3d(-2.4375, -1.57788, 3.4076), 0.2);
	private static final SweptTriangleVolume VOL_BODY_BACK_MIDB = new SweptTriangleVolume(new Vec3d(-2.0, 0.17212, 3.4076), new Vec3d(2.4375, -1.57788, 3.4076), new Vec3d(2.0, 0.172122, 3.4076), 0.2);
	private static final SweptTriangleVolume VOL_BODY_BOTTOM_RIGHT_FRONT = new SweptTriangleVolume(new Vec3d(-1.125, -0.827873, -5.4674), new Vec3d(-1.25, -1.45287, -2.3424), new Vec3d(-2.5, -1.07787, -1.3424), 0.2);
	private static final SweptTriangleVolume VOL_BODY_BOTTOM_RIGHT_MIDA = new SweptTriangleVolume(new Vec3d(-1.875, -1.70287, 0.5326), new Vec3d(-1.25, -1.45287, -2.3424), new Vec3d(-2.5, -1.07787, -1.3424), 0.2);
	private static final SweptTriangleVolume VOL_BODY_BOTTOM_RIGHT_MIDB = new SweptTriangleVolume(new Vec3d(-1.875, -1.70287, 0.5326), new Vec3d(-3.5, -1.07788, 0.4076), new Vec3d(-2.5, -1.07787, -1.3424), 0.2);
	private static final SweptTriangleVolume VOL_BODY_BOTTOM_RIGHT_BACKA = new SweptTriangleVolume(new Vec3d(-1.875, -1.70287, 0.5326), new Vec3d(-2.5, -1.70289, 1.6576), new Vec3d(-2.5, -1.07787, -1.3424), 0.2);
	private static final SweptTriangleVolume VOL_BODY_BOTTOM_RIGHT_BACKB = new SweptTriangleVolume(new Vec3d(-3.5, -1.07789, 1.6576), new Vec3d(-2.5, -1.70289, 1.6576), new Vec3d(-2.5, -1.07787, -1.3424), 0.2);
	private static final SweptTriangleVolume VOL_BODY_BOTTOM_LEFT_FRONT = new SweptTriangleVolume(new Vec3d(1.125, -0.827873, -5.4674), new Vec3d(1.25, -1.45287, -2.3424), new Vec3d(2.5, -1.07787, -1.3424), 0.2);
	private static final SweptTriangleVolume VOL_BODY_BOTTOM_LEFT_MIDA = new SweptTriangleVolume(new Vec3d(1.875, -1.70287, 0.5326), new Vec3d(1.25, -1.45287, -2.3424), new Vec3d(2.5, -1.07787, -1.3424), 0.2);
	private static final SweptTriangleVolume VOL_BODY_BOTTOM_LEFT_MIDB = new SweptTriangleVolume(new Vec3d(1.875, -1.70287, 0.5326), new Vec3d(3.5, -1.07788, 0.4076), new Vec3d(2.5, -1.07787, -1.3424), 0.2);
	private static final SweptTriangleVolume VOL_BODY_BOTTOM_LEFT_BACKA = new SweptTriangleVolume(new Vec3d(1.875, -1.70287, 0.5326), new Vec3d(2.5, -1.70289, 1.6576), new Vec3d(2.5, -1.07787, -1.3424), 0.2);
	private static final SweptTriangleVolume VOL_BODY_BOTTOM_LEFT_BACKB = new SweptTriangleVolume(new Vec3d(3.5, -1.07789, 1.6576), new Vec3d(2.5, -1.70289, 1.6576), new Vec3d(2.5, -1.07787, -1.3424), 0.2);
	private static final SweptTriangleVolume VOL_BODY_BOTTOM_MID_FRONTA = new SweptTriangleVolume(new Vec3d(-1.125, -0.827873, -5.4674), new Vec3d(-1.25, -1.45287, -2.3424), new Vec3d(1.25, -1.45287, -2.3424), 0.2);
	private static final SweptTriangleVolume VOL_BODY_BOTTOM_MID_FRONTB = new SweptTriangleVolume(new Vec3d(-1.125, -0.827873, -5.4674), new Vec3d(1.125, -0.827873, -5.4674), new Vec3d(1.25, -1.45287, -2.3424), 0.2);
	private static final SweptTriangleVolume VOL_BODY_BOTTOM_MID_MIDA = new SweptTriangleVolume(new Vec3d(-1.25, -1.45287, -2.3424), new Vec3d(-1.875, -1.70287, 0.5326), new Vec3d(1.25, -1.45287, -2.3424), 0.2);
	private static final SweptTriangleVolume VOL_BODY_BOTTOM_MID_MIDB = new SweptTriangleVolume(new Vec3d(1.875, -1.70287, 0.532598), new Vec3d(-1.875, -1.70287, 0.5326), new Vec3d(1.25, -1.45287, -2.3424), 0.2);
	private static final TrackedData<Byte> CANNON_BITS = DataTracker.registerData(RZ1Awing.class, TrackedDataHandlerRegistry.BYTE);
	private static final int CANNON_STATE_MASK = 0b00000001;
	private static final String[] CANNON_ORDER = { RigRZ1.CANNON_LEFT, RigRZ1.CANNON_RIGHT };

	public RZ1Awing(EntityType<?> type, World world)
	{
		super(type, world);
	}

	@Override
	protected void initDataTracker()
	{
		super.initDataTracker();
		getDataTracker().startTracking(CANNON_BITS, (byte)0);
	}

	@Override
	public void acceptFireInput()
	{
		var passenger = getControllingPassenger();
		if (!(passenger instanceof PlayerEntity player))
			return;

		var pos = this.getPos();
		var rotation = getRotation();
		var stack = new Transform();

		var cannonState = getCannonState();
		var p = RigRZ1.INSTANCE.getWorldPosition(stack, this, rotation, CANNON_ORDER[cannonState], 0).add(pos);

		var convergenceDistance = 40;
		var forward = QuatUtil.rotate(MathUtil.V3D_NEG_Z.multiply(convergenceDistance), rotation);
		var boltRotation = QuatUtil.lookAt(p, pos.add(forward));

		var pDir = QuatUtil.rotate(MathUtil.V3D_POS_Z.multiply(5f), boltRotation);

		BlasterUtil.fireBolt(getWorld(), player, pDir.normalize(), 100, distance -> (double)50, true, blasterBoltEntity -> {
			blasterBoltEntity.setVelocity(pDir);
			blasterBoltEntity.setPos(p.x, p.y, p.z);
			blasterBoltEntity.setColor(ColorUtil.packHsv(0.98f, 1, 1));
		});

		getWorld().playSound(null, player.getBlockPos(), SwgSounds.Ship.XWINGT65B_FIRE, SoundCategory.PLAYERS, 1, 1 + (float)getWorld().random.nextGaussian() / 10);

		cannonState++;
		setCannonState(cannonState);
	}

	public byte getCannonState()
	{
		return (byte)(getDataTracker().get(CANNON_BITS) & CANNON_STATE_MASK);
	}

	public void setCannonState(byte cannonState)
	{
		byte cannons = getDataTracker().get(CANNON_BITS);

		cannons &= ~CANNON_STATE_MASK;
		cannons |= cannonState & CANNON_STATE_MASK;

		getDataTracker().set(CANNON_BITS, cannons);
	}

	@Override
	public ICollisionVolume[] getCollision()
	{
		var pos = this.getPos();
		var rot = getRotation();
		var posMat = new Matrix4f().translate((float)pos.x, (float)pos.y, (float)pos.z);
		return new ICollisionVolume[] {
				COCKPIT.transform(rot).transform(posMat),
				VOL_CANNON_RIGHT.transform(rot).transform(posMat),
				VOL_CANNON_LEFT.transform(rot).transform(posMat),
				VOL_ENGINE_RIGHT.transform(rot).transform(posMat),
				VOL_ENGINE_LEFT.transform(rot).transform(posMat),
				VOL_BODY_TOP_RIGHT_FRONTA.transform(rot).transform(posMat),
				VOL_BODY_TOP_RIGHT_FRONTB.transform(rot).transform(posMat),
				VOL_BODY_TOP_RIGHT_MID1A.transform(rot).transform(posMat),
				VOL_BODY_TOP_RIGHT_MID1B.transform(rot).transform(posMat),
				VOL_BODY_TOP_RIGHT_MID2A.transform(rot).transform(posMat),
				VOL_BODY_TOP_RIGHT_MID2B.transform(rot).transform(posMat),
				VOL_BODY_TOP_RIGHT_BACKA.transform(rot).transform(posMat),
				VOL_BODY_TOP_RIGHT_BACKB.transform(rot).transform(posMat),
				VOL_BODY_TOP_LEFT_FRONTA.transform(rot).transform(posMat),
				VOL_BODY_TOP_LEFT_FRONTB.transform(rot).transform(posMat),
				VOL_BODY_TOP_LEFT_MID1A.transform(rot).transform(posMat),
				VOL_BODY_TOP_LEFT_MID1B.transform(rot).transform(posMat),
				VOL_BODY_TOP_LEFT_MID2A.transform(rot).transform(posMat),
				VOL_BODY_TOP_LEFT_MID2B.transform(rot).transform(posMat),
				VOL_BODY_TOP_LEFT_BACKA.transform(rot).transform(posMat),
				VOL_BODY_TOP_LEFT_BACKB.transform(rot).transform(posMat),
				VOL_BODY_TOP_MIDDLE_FRONT_RIGHTA.transform(rot).transform(posMat),
				VOL_BODY_TOP_MIDDLE_FRONT_RIGHTB.transform(rot).transform(posMat),
				VOL_BODY_TOP_MIDDLE_FRONT_RIGHTC.transform(rot).transform(posMat),
				VOL_BODY_TOP_MIDDLE_FRONT_MID_RIGHT_FRONT.transform(rot).transform(posMat),
				VOL_BODY_TOP_MIDDLE_FRONT_MID_RIGHT_MIDA.transform(rot).transform(posMat),
				VOL_BODY_TOP_MIDDLE_FRONT_MID_RIGHT_MIDB.transform(rot).transform(posMat),
				VOL_BODY_TOP_MIDDLE_FRONT_LEFTA.transform(rot).transform(posMat),
				VOL_BODY_TOP_MIDDLE_FRONT_LEFTB.transform(rot).transform(posMat),
				VOL_BODY_TOP_MIDDLE_FRONT_LEFTC.transform(rot).transform(posMat),
				VOL_BODY_TOP_MIDDLE_FRONT_MID_LEFT_FRONT.transform(rot).transform(posMat),
				VOL_BODY_TOP_MIDDLE_FRONT_MID_LEFT_MIDA.transform(rot).transform(posMat),
				VOL_BODY_TOP_MIDDLE_FRONT_MID_LEFT_MIDB.transform(rot).transform(posMat),
				VOL_BODY_TOP_MIDDLE_BACK_FRONTA.transform(rot).transform(posMat),
				VOL_BODY_TOP_MIDDLE_BACK_FRONTB.transform(rot).transform(posMat),
				VOL_BODY_TOP_MIDDLE_BACK_BACKA.transform(rot).transform(posMat),
				VOL_BODY_TOP_MIDDLE_BACK_BACKB.transform(rot).transform(posMat),
				VOL_BODY_SIDE_FRONT_RIGHTA.transform(rot).transform(posMat),
				VOL_BODY_SIDE_FRONT_RIGHTB.transform(rot).transform(posMat),
				VOL_BODY_SIDE_FRONT_LEFTA.transform(rot).transform(posMat),
				VOL_BODY_SIDE_FRONT_LEFTB.transform(rot).transform(posMat),
				VOL_BODY_SIDE_FRONTA.transform(rot).transform(posMat),
				VOL_BODY_SIDE_FRONTB.transform(rot).transform(posMat),
				VOL_BODY_SIDE_RIGHT_FRONTA.transform(rot).transform(posMat),
				VOL_BODY_SIDE_RIGHT_FRONTB.transform(rot).transform(posMat),
				VOL_BODY_SIDE_RIGHT_MIDDLEA.transform(rot).transform(posMat),
				VOL_BODY_SIDE_RIGHT_MIDDLEB.transform(rot).transform(posMat),
				VOL_BODY_SIDE_RIGHT_BACKA.transform(rot).transform(posMat),
				VOL_BODY_SIDE_RIGHT_BACKB.transform(rot).transform(posMat),
				VOL_BODY_SIDE_LEFT_FRONTA.transform(rot).transform(posMat),
				VOL_BODY_SIDE_LEFT_FRONTB.transform(rot).transform(posMat),
				VOL_BODY_SIDE_LEFT_MIDDLEA.transform(rot).transform(posMat),
				VOL_BODY_SIDE_LEFT_MIDDLEB.transform(rot).transform(posMat),
				VOL_BODY_SIDE_LEFT_BACKA.transform(rot).transform(posMat),
				VOL_BODY_SIDE_LEFT_BACKB.transform(rot).transform(posMat),
				VOL_BODY_BACK_RIGHTA.transform(rot).transform(posMat),
				VOL_BODY_BACK_RIGHTB.transform(rot).transform(posMat),
				VOL_BODY_BACK_LEFTA.transform(rot).transform(posMat),
				VOL_BODY_BACK_LEFTB.transform(rot).transform(posMat),
				VOL_BODY_BACK_MIDA.transform(rot).transform(posMat),
				VOL_BODY_BACK_MIDB.transform(rot).transform(posMat),
				VOL_BODY_BOTTOM_RIGHT_FRONT.transform(rot).transform(posMat),
				VOL_BODY_BOTTOM_RIGHT_MIDA.transform(rot).transform(posMat),
				VOL_BODY_BOTTOM_RIGHT_MIDB.transform(rot).transform(posMat),
				VOL_BODY_BOTTOM_RIGHT_BACKA.transform(rot).transform(posMat),
				VOL_BODY_BOTTOM_RIGHT_BACKB.transform(rot).transform(posMat),
				VOL_BODY_BOTTOM_LEFT_FRONT.transform(rot).transform(posMat),
				VOL_BODY_BOTTOM_LEFT_MIDA.transform(rot).transform(posMat),
				VOL_BODY_BOTTOM_LEFT_MIDB.transform(rot).transform(posMat),
				VOL_BODY_BOTTOM_LEFT_BACKA.transform(rot).transform(posMat),
				VOL_BODY_BOTTOM_LEFT_BACKB.transform(rot).transform(posMat),
				VOL_BODY_BOTTOM_MID_FRONTA.transform(rot).transform(posMat),
				VOL_BODY_BOTTOM_MID_FRONTB.transform(rot).transform(posMat),
				VOL_BODY_BOTTOM_MID_MIDA.transform(rot).transform(posMat),
				VOL_BODY_BOTTOM_MID_MIDB.transform(rot).transform(posMat),
				};
	}
}