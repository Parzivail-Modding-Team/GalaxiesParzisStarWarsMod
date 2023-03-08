package com.parzivail.util.client.render.armor;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class BipedEntityArmorModel<T extends LivingEntity> extends BipedEntityModel<T>
{
	public static final String PART_LEFT_BOOT = "left_boot";
	public static final String PART_RIGHT_BOOT = "right_boot";

	private final ModelPart root;

	public BipedEntityArmorModel(ModelPart root)
	{
		super(root);
		this.root = root;
	}

	public ModelPart getRoot()
	{
		return root;
	}

	@Override
	protected Iterable<ModelPart> getBodyParts()
	{
		var list = new ArrayList<>((Collection<ModelPart>)super.getBodyParts());
		getLeftBoot().ifPresent(list::add);
		getRightBoot().ifPresent(list::add);
		return ImmutableList.copyOf(list);
	}

	public Optional<ModelPart> getLeftBoot()
	{
		return getPart(PART_LEFT_BOOT);
	}

	public Optional<ModelPart> getRightBoot()
	{
		return getPart(PART_RIGHT_BOOT);
	}

	public Optional<ModelPart> getPart(String part)
	{
		if (root.hasChild(part))
			return Optional.of(root.getChild(part));
		return Optional.empty();
	}
}
