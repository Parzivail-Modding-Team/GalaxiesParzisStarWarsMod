package com.parzivail.pswg.features.blasters.data;

import java.util.HashMap;

public record BlasterAttachmentMap(int attachmentMinimum, int attachmentDefault, HashMap<Integer, BlasterAttachmentDescriptor> attachmentMap)
{
}
