package com.parzivail.pswg.api;

import com.parzivail.p3d.P3dModel;
import com.parzivail.pswg.features.blasters.client.BlasterItemRenderer;
import com.parzivail.pswg.features.blasters.data.BlasterDescriptor;
import com.parzivail.pswg.features.blasters.data.BlasterTag;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;

public interface BlasterTransformer
{
	/**
	 * Do not use this directly.
	 */
	ArrayList<BlasterTransformer> REGISTRY = new ArrayList<>();

	static void register(BlasterTransformer transformer)
	{
		REGISTRY.add(transformer);
	}

	void transformHand(MatrixStack matrices, P3dModel model, BlasterTag bt, BlasterDescriptor bd, BlasterItemRenderer.AttachmentSuperset attachments, ModelTransformation.Mode renderMode, int light, float tickDelta, float opacity);

	void preTransform(MatrixStack matrices, P3dModel model, BlasterTag bt, BlasterDescriptor bd, BlasterItemRenderer.AttachmentSuperset attachmentSet, ModelTransformation.Mode renderMode, int light, float tickDelta, float opacity);

	void postTransform(MatrixStack matrices, P3dModel model, BlasterTag bt, BlasterDescriptor bd, BlasterItemRenderer.AttachmentSuperset attachments, ModelTransformation.Mode renderMode, int light, float tickDelta, float opacity);
}
