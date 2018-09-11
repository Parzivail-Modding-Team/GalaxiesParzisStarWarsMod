package com.parzivail.swg.item.blaster.data;

import com.parzivail.swg.item.blaster.data.barrel.*;
import com.parzivail.swg.item.blaster.data.grip.BlasterGrip;
import com.parzivail.swg.item.blaster.data.grip.GripFore;
import com.parzivail.swg.item.blaster.data.grip.GripNone;
import com.parzivail.swg.item.blaster.data.powerpack.BlasterPowerPack;
import com.parzivail.swg.item.blaster.data.powerpack.PowerPackSmallGasCanister;
import com.parzivail.swg.item.blaster.data.scope.*;
import com.parzivail.swg.player.PswgExtProp;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlasterAttachments
{
	public static final HashMap<Integer, BlasterAttachment> ATTACHMENTS = new HashMap<>();

	public static final List<BlasterAttachment> SCOPES = new ArrayList<>();
	public static final List<BlasterAttachment> BARRELS = new ArrayList<>();
	public static final List<BlasterAttachment> GRIPS = new ArrayList<>();
	public static final List<BlasterAttachment> POWERPACKS = new ArrayList<>();

	public static BlasterScope scopeIronsights = new ScopeIronsights();
	public static BlasterGrip gripNone = new GripNone();
	public static BlasterBarrel barrelDefault = new BarrelDefault();
	public static BlasterPowerPack packNone = new PowerPackSmallGasCanister();

	static
	{
		register(scopeIronsights, SCOPES);
		register(new ScopeRedDot(), SCOPES);
		register(new ScopeReflex(), SCOPES);
		register(new ScopeRingReticle(), SCOPES);
		register(new ScopeAcog(), SCOPES);
		register(new ScopeSpitfire(), SCOPES);

		register(barrelDefault, BARRELS);
		register(new BlasterBarrelCompensator(), BARRELS);
		register(new BlasterBarrelExtendedBarrel(), BARRELS);
		register(new BlasterBarrelLaserUnderbarrel(), BARRELS);
		register(new BlasterBarrelMuzzleBrake(), BARRELS);
		register(new BlasterBarrelSuppressor(), BARRELS);

		register(gripNone, GRIPS);
		register(new GripFore(), GRIPS);

		register(packNone, POWERPACKS);
		register(new PowerPackSmallGasCanister(), POWERPACKS);
	}

	private static void register(BlasterAttachment attachment, List<BlasterAttachment> collection)
	{
		ATTACHMENTS.put(attachment.getId(), attachment);
		collection.add(attachment);
	}

	public static boolean doesPlayerOwn(EntityPlayer player, BlasterAttachment attachment)
	{
		if (attachment == scopeIronsights || attachment == gripNone || attachment == barrelDefault)
			return true;

		PswgExtProp props = PswgExtProp.get(player);
		return props != null && props.isBlasterAttachmentUnlocked(attachment.getId());
	}

	public static boolean isEquipped(ItemStack blaster, BlasterAttachment attachment)
	{
		BlasterData bd = new BlasterData(blaster);

		switch (attachment.type)
		{
			case SCOPE:
				return bd.getScope() == attachment;
			case BARREL:
				return bd.getBarrel() == attachment;
			case GRIP:
				return bd.getGrip() == attachment;
		}
		return false;
	}
}
