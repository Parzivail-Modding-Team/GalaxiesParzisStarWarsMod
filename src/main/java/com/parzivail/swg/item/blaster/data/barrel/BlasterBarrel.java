package com.parzivail.swg.item.blaster.data.barrel;

import com.parzivail.swg.Resources;
import com.parzivail.swg.item.blaster.data.BlasterAttachment;
import com.parzivail.swg.item.blaster.data.BlasterAttachmentType;
import com.parzivail.util.math.MathFormat;
import net.minecraft.client.resources.I18n;

public abstract class BlasterBarrel extends BlasterAttachment
{
	public BlasterBarrel(String name, int price)
	{
		super(BlasterAttachmentType.BARREL, Resources.modDot("blaster", "barrel", name), price);
	}

	@Override
	public String getInfoText()
	{
		float vRR = getVerticalRecoilReduction();
		float hRR = getHorizontalRecoilReduction();
		float vSR = getVerticalSpreadReduction();
		float hSR = getHorizontalSpreadReduction();
		float nR = getNoiseReduction();
		float rI = getRangeIncrease();
		return I18n.format(Resources.guiDot("barrelStats"), MathFormat.DEC2.format(vRR), MathFormat.DEC2.format(hRR), MathFormat.DEC2.format(vSR), MathFormat.DEC2.format(hSR), MathFormat.DEC2.format(nR), MathFormat.DEC2.format(rI));
	}

	public abstract float getHorizontalRecoilReduction();

	public abstract float getHorizontalSpreadReduction();

	public abstract float getVerticalRecoilReduction();

	public abstract float getVerticalSpreadReduction();

	public abstract float getNoiseReduction();

	public abstract float getRangeIncrease();
}
