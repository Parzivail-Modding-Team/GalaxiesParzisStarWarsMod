package com.parzivail.pswg.item.blaster.data;

import java.util.HashMap;

public record BlasterAttachmentMap(int attachmentMinimum, int attachmentDefault, HashMap<Integer, BlasterAttachmentDescriptor> attachmentMap)
{
}
