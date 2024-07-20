package com.parzivail.aurek.model.nemi;

import java.util.List;

public record NemiPart(String parent, NemiEuler rot, List<NemiBox> boxes, NemiVec3 pos, boolean mirrored)
{
}
