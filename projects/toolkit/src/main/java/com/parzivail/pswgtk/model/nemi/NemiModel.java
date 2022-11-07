package com.parzivail.pswgtk.model.nemi;

import com.parzivail.pswgtk.util.NbtUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public final class NemiModel
{
	private final String exporter;
	private final String name;
	private final NemiTexSize tex;
	private final HashMap<String, NemiPart> parts;

	public NemiModel(String exporter, String name, NemiTexSize tex, HashMap<String, NemiPart> parts)
	{
		this.exporter = exporter;
		this.name = name;
		this.tex = tex;
		this.parts = parts;
	}

	public String exporter()
	{
		return exporter;
	}

	public String name()
	{
		return name;
	}

	public NemiTexSize tex()
	{
		return tex;
	}

	public HashMap<String, NemiPart> parts()
	{
		return parts;
	}

	public NbtCompound createNem()
	{
		var tag = new NbtCompound();

		tag.put("tex", NbtUtil.tag(nbt -> {
			nbt.putInt("w", (int)this.tex.w());
			nbt.putInt("h", (int)this.tex.h());
		}));

		var uniqueNames = new HashMap<NemiPart, String>();

		var parts = new NbtCompound();
		for (var part : this.parts.entrySet())
		{
			// Only insert root parts
			if (part.getValue().parent() != null)
				continue;

			collectParts(uniqueNames, parts, this.parts, part.getKey(), part.getValue());
		}
		tag.put("parts", parts);

		return tag;
	}

	private void collectParts(HashMap<NemiPart, String> uniqueNames, NbtCompound parent, HashMap<String, NemiPart> parts, String id, NemiPart part)
	{
		var partName = getUniqueName(uniqueNames, id, part);

		var partTag = NbtUtil.tag(root -> {
			root.put("tex", NbtUtil.tag(tex -> {
				tex.putBoolean("mirrored", part.mirrored());
			}));
			root.put("rot", NbtUtil.tag(rot -> {
				rot.putFloat("pitch", (float)part.rot().pitch() * MathHelper.RADIANS_PER_DEGREE);
				rot.putFloat("yaw", (float)part.rot().yaw() * MathHelper.RADIANS_PER_DEGREE);
				rot.putFloat("roll", (float)part.rot().roll() * MathHelper.RADIANS_PER_DEGREE);
			}));
			root.put("pos", NbtUtil.tag(pos -> {
				pos.putFloat("x", (float)part.pos().x());
				pos.putFloat("y", (float)part.pos().y());
				pos.putFloat("z", (float)part.pos().z());
			}));
		});

		var childTag = new NbtCompound();
		for (var child : parts.entrySet())
			if (child.getValue().parent() != null && child.getValue().parent().equals(id))
				collectParts(uniqueNames, childTag, parts, child.getKey(), child.getValue());

		if (!childTag.isEmpty())
			partTag.put("children", childTag);

		var cuboids = new NbtList();

		List<NemiBox> boxes = part.boxes();
		for (int i = 0; i < boxes.size(); i++)
		{
			NemiBox cube = boxes.get(i);
			cuboids.add(i, NbtUtil.tag(nbt -> {
				nbt.put("size", NbtUtil.tag(size -> {
					size.putInt("x", (int)cube.size().x());
					size.putInt("y", (int)cube.size().y());
					size.putInt("z", (int)cube.size().z());
				}));
				nbt.put("pos", NbtUtil.tag(pos -> {
					pos.putFloat("x", (float)cube.pos().x());
					pos.putFloat("y", (float)cube.pos().y());
					pos.putFloat("z", (float)cube.pos().z());
				}));
				nbt.put("expand", NbtUtil.tag(expand -> {
					expand.putFloat("x", (float)cube.inflate());
					expand.putFloat("y", (float)cube.inflate());
					expand.putFloat("z", (float)cube.inflate());
				}));
				nbt.put("tex", NbtUtil.tag(expand -> {
					expand.putInt("u", (int)cube.tex().u());
					expand.putInt("v", (int)cube.tex().v());
					expand.putBoolean("mirrored", cube.tex().mirrored());
				}));
			}));
		}

		partTag.put("cuboids", cuboids);
		parent.put(partName, partTag);
	}

	private String getUniqueName(HashMap<NemiPart, String> existingNames, String partName, NemiPart part)
	{
		if (existingNames.containsKey(part))
			return existingNames.get(part);

		var index = 0;
		while (existingNames.containsValue(nameAtIndex(partName, index)))
			index++;

		var name = nameAtIndex(partName, index);
		existingNames.put(part, name);
		return name;
	}

	private static String nameAtIndex(String name, int index)
	{
		if (index == 0)
			return name;
		return String.format("%s_%d", name, index);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;
		if (obj == null || obj.getClass() != this.getClass())
			return false;
		var that = (NemiModel)obj;
		return Objects.equals(this.exporter, that.exporter) &&
		       Objects.equals(this.name, that.name) &&
		       Objects.equals(this.tex, that.tex) &&
		       Objects.equals(this.parts, that.parts);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(exporter, name, tex, parts);
	}

	@Override
	public String toString()
	{
		return "NemiModel[" +
		       "exporter=" + exporter + ", " +
		       "name=" + name + ", " +
		       "tex=" + tex + ", " +
		       "parts=" + parts + ']';
	}
}
