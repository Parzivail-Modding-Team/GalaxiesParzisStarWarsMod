package com.parzivail.pswg.client.render.player;

import com.parzivail.pswg.character.SpeciesGender;
import com.parzivail.pswg.character.SwgSpecies;
import com.parzivail.pswg.client.render.armor.ArmorRenderer;
import com.parzivail.pswg.component.PlayerData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class PlayerSpeciesModelRenderer extends PlayerEntityRenderer
{
	@FunctionalInterface
	public interface Animator
	{
		void animateModel(AbstractClientPlayerEntity entity, PlayerEntityModel<AbstractClientPlayerEntity> model, PlayerSpeciesModelRenderer renderer, float tickDelta);
	}

	private Supplier<PlayerEntityModel<AbstractClientPlayerEntity>> modelSupplier;
	private final Animator animator;

	private SwgSpecies overrideSpecies;
	private Identifier overrideTexture;

	public PlayerSpeciesModelRenderer(EntityRendererFactory.Context ctx, boolean slim, Supplier<PlayerEntityModel<AbstractClientPlayerEntity>> model, Animator animator)
	{
		super(ctx, slim);
		this.modelSupplier = model;
		this.animator = animator;
	}

	@Override
	public PlayerEntityModel<AbstractClientPlayerEntity> getModel()
	{
		// This works because this method is called
		// when the pose is set, before this.model is
		// used directly
		if (modelSupplier != null)
		{
			this.model = modelSupplier.get();
			this.modelSupplier = null;
		}
		return super.getModel();
	}

	@Override
	public Identifier getTexture(AbstractClientPlayerEntity abstractClientPlayerEntity)
	{
		if (overrideTexture != null)
			return overrideTexture;

		return super.getTexture(abstractClientPlayerEntity);
	}

	@Override
	public void render(AbstractClientPlayerEntity player, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int light)
	{
		if (animator != null)
			animator.animateModel(player, model, this, tickDelta);

		transformChestCube(player);
		transformHairCube(player);

		super.render(player, yaw, tickDelta, matrices, vertexConsumerProvider, light);
	}

	private void transformHairCube(AbstractClientPlayerEntity player)
	{
		var model = getModel();

		var species = overrideSpecies;
		if (species == null)
		{
			var components = PlayerData.getPersistentPublic(player);
			species = components.getCharacter();
			if (species == null)
				return;
		}

		var cubeVisible = true;

		var armorPair = ArmorRenderer.getModArmor(player, EquipmentSlot.HEAD);
		if (armorPair != null)
		{
			var metadata = ArmorRenderer.getMetadata(armorPair.getLeft());
			cubeVisible = metadata.hairAction() == ArmorRenderer.CubeAction.KEEP;
		}
		else
		{
			var vanillaArmor = ArmorRenderer.getVanillaArmor(player, EquipmentSlot.HEAD);
			if (vanillaArmor != null)
			{
				// TODO: How should vanilla armor be handles?
			}
		}

		model.hat.visible = cubeVisible;

		if (model.head.hasChild("hair"))
			model.head.getChild("hair").visible = cubeVisible;
	}

	private void transformChestCube(AbstractClientPlayerEntity player)
	{
		var model = getModel();

		if (!model.body.hasChild("chest"))
			return;

		var species = overrideSpecies;
		if (species == null)
		{
			var components = PlayerData.getPersistentPublic(player);
			species = components.getCharacter();
			if (species == null)
				return;
		}

		var chest = model.body.getChild("chest");
		if (chest == null)
			return;

		var isFemale = species.getGender() == SpeciesGender.FEMALE;
		var armorHidesCube = false;

		var armorPair = ArmorRenderer.getModArmor(player, EquipmentSlot.CHEST);
		if (armorPair != null)
		{
			var metadata = ArmorRenderer.getMetadata(armorPair.getLeft());
			armorHidesCube = metadata.femaleModelAction() == ArmorRenderer.FemaleChestplateAction.HIDE_CUBE;
		}
		else
		{
			var vanillaArmor = ArmorRenderer.getVanillaArmor(player, EquipmentSlot.CHEST);
			if (vanillaArmor != null)
				armorHidesCube = true;
		}

		chest.visible = isFemale && !armorHidesCube;
	}

	public void renderWithOverrides(SwgSpecies species, Identifier texture, AbstractClientPlayerEntity abstractClientPlayerEntity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i)
	{
		overrideSpecies = species;
		overrideTexture = texture;

		this.render(abstractClientPlayerEntity, yaw, tickDelta, matrixStack, vertexConsumerProvider, i);

		overrideSpecies = null;
		overrideTexture = null;
	}
}
