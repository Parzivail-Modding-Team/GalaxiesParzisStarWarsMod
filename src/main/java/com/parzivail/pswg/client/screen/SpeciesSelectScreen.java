package com.parzivail.pswg.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Client;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.character.SpeciesColorVariable;
import com.parzivail.pswg.character.SpeciesGender;
import com.parzivail.pswg.character.SpeciesVariable;
import com.parzivail.pswg.character.SwgSpecies;
import com.parzivail.pswg.client.render.camera.CameraHelper;
import com.parzivail.pswg.client.render.player.PlayerSpeciesModelRenderer;
import com.parzivail.pswg.client.screen.widget.EventCheckboxWidget;
import com.parzivail.pswg.client.screen.widget.LocalTextureButtonWidget;
import com.parzivail.pswg.client.screen.widget.SimpleListWidget;
import com.parzivail.pswg.client.screen.widget.SimpleSliderWidget;
import com.parzivail.pswg.client.species.SwgSpeciesModels;
import com.parzivail.pswg.component.SwgEntityComponents;
import com.parzivail.pswg.container.SwgPackets;
import com.parzivail.pswg.container.SwgSpeciesRegistry;
import com.parzivail.pswg.mixin.EntityRenderDispatcherAccessor;
import com.parzivail.util.client.ColorUtil;
import com.parzivail.util.math.Ease;
import com.parzivail.util.math.MatrixStackUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.lwjgl.glfw.GLFW;

import java.util.List;

@Environment(EnvType.CLIENT)
public class SpeciesSelectScreen extends Screen
{
	//	public static final Identifier BACKGROUND = Resources.identifier("textures/block/");
	public static final Identifier BACKGROUND = new Identifier("textures/gui/options_background.png");

	public static final Identifier CAROUSEL = Resources.id("textures/gui/species_select/carousel.png");
	public static final String I18N_USE_FEMALE_MODEL = Resources.screen("species_select.use_female_model");

	private final Screen parent;

	private SimpleListWidget<SwgSpecies> speciesListWidget;
	private SimpleListWidget<SpeciesVariable> speciesVariableListWidget;

	private SimpleSliderWidget sliderR;
	private SimpleSliderWidget sliderG;
	private SimpleSliderWidget sliderB;
	private LocalTextureButtonWidget buttonColorApply;

	private static final int CAROUSEL_TIMER_MAX = 6;
	private int carouselTimer = 0;
	private boolean looping = false;

	private final List<SwgSpecies> availableSpecies;
	private SwgSpecies playerSpecies;
	private SpeciesGender gender = SpeciesGender.MALE;

	public SpeciesSelectScreen(Screen parent)
	{
		super(new TranslatableText("screen.pswg.species_select"));
		this.parent = parent;

		availableSpecies = SwgSpeciesRegistry.getSpecies();

		availableSpecies.add(0, null);
	}

	private void updateAbility()
	{
		var selectedVarEntry = speciesVariableListWidget.getSelectedOrNull();
		if (selectedVarEntry != null)
		{
			var selectedVar = selectedVarEntry.getValue();
			if (selectedVar instanceof SpeciesColorVariable)
			{
				var color = Integer.parseUnsignedInt(playerSpecies.getVariable(selectedVar), 16);
				sliderR.setValue(((color & 0xFF0000) >> 16) / 255f);
				sliderG.setValue(((color & 0xFF00) >> 8) / 255f);
				sliderB.setValue((color & 0xFF) / 255f);
			}
		}
	}

	@Override
	protected void init()
	{
		var c = SwgEntityComponents.getPersistent(client.player);
		playerSpecies = c.getSpecies();

		if (playerSpecies != null)
			this.gender = playerSpecies.getGender();

		speciesVariableListWidget = new SimpleListWidget<>(client, width / 2 + 128, height / 2 - 91, 80, 100, 15, entry -> updateAbility());
		speciesVariableListWidget.setEntryFormatter(speciesVariable -> new TranslatableText(speciesVariable.getTranslationKey()));

		speciesListWidget = new SimpleListWidget<>(client, width / 2 - 128 - 80, height / 2 - 91, 80, 182, 15, entry -> {
			if (entry != null)
			{
				speciesVariableListWidget.setEntries(entry.getVariables());
			}
			else
				speciesVariableListWidget.clear();
			updateAbility();
		});
		speciesListWidget.setEntryFormatter(species -> new TranslatableText(SwgSpeciesRegistry.getTranslationKey(species)));
		speciesListWidget.setEntrySelector(entries -> {
			if (playerSpecies == null)
				return entries.get(1);
			var sameSpecies = entries.stream().filter(swgSpeciesEntry -> playerSpecies.isSameSpecies(swgSpeciesEntry.getValue())).findFirst().orElse(null);
			if (sameSpecies != null)
				sameSpecies.getValue().copy(playerSpecies);
			return sameSpecies;
		});

		this.addDrawableChild(speciesVariableListWidget);
		this.addDrawableChild(speciesListWidget);

		this.addDrawableChild(new ButtonWidget(this.width / 2 - 120, this.height / 2 - 10, 20, 20, new LiteralText("<"), (button) -> moveToNextVariableOption(true)));

		this.addDrawableChild(new ButtonWidget(this.width / 2 + 100, this.height / 2 - 10, 20, 20, new LiteralText(">"), (button) -> moveToNextVariableOption(false)));

		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100 - 75, this.height - 26, 95, 20, ScreenTexts.BACK, (button) -> this.client.setScreen(this.parent)));

		this.addDrawableChild(new ButtonWidget(this.width / 2 - 60, this.height - 26, 120, 20, new TranslatableText(Resources.I18N_SCREEN_APPLY), (button) -> {
			if (speciesListWidget.getSelectedOrNull() == null)
				return;

			var passedData = new PacketByteBuf(Unpooled.buffer());

			var selected = speciesListWidget.getSelectedOrNull().getValue();

			if (selected == null)
				passedData.writeString("minecraft:none");
			else
			{
				this.playerSpecies = SwgSpeciesRegistry.deserialize(selected.serialize());

				if (speciesVariableListWidget.getSelectedOrNull() == null)
					return;

				var selectedVariable = speciesVariableListWidget.getSelectedOrNull().getValue();

				this.playerSpecies.setVariable(selectedVariable, selected.getVariable(selectedVariable));

				passedData.writeString(this.playerSpecies.serialize());
			}

			// TODO: verify species variables on server
			ClientPlayNetworking.send(SwgPackets.C2S.SetOwnSpecies, passedData);
		}));

		this.addDrawableChild(new EventCheckboxWidget(this.width / 2 + 105 - 25, this.height - 26, 20, 20, new TranslatableText(I18N_USE_FEMALE_MODEL), this.gender == SpeciesGender.FEMALE, true, (checked) -> {
			gender = checked ? SpeciesGender.FEMALE : SpeciesGender.MALE;
			if (this.playerSpecies != null)
				this.playerSpecies.setGender(gender);
		}));

		this.addDrawableChild(sliderR = new SimpleSliderWidget(this.width / 2 + 132, this.height / 2 + 58, 75, 4, 0, 184, 8, 189, 0, 189, 6, 8, 256, 256, simpleSliderWidget -> {
			onSliderColorChanged();
		}));

		this.addDrawableChild(sliderG = new SimpleSliderWidget(this.width / 2 + 132, this.height / 2 + 68, 75, 4, 0, 184, 8, 189, 0, 189, 6, 8, 256, 256, simpleSliderWidget -> {
			onSliderColorChanged();
		}));

		this.addDrawableChild(sliderB = new SimpleSliderWidget(this.width / 2 + 132, this.height / 2 + 78, 75, 4, 0, 184, 8, 189, 0, 189, 6, 8, 256, 256, simpleSliderWidget -> {
			onSliderColorChanged();
		}));

		this.addDrawableChild(buttonColorApply = new LocalTextureButtonWidget(this.width / 2 + 194, this.height / 2 + 35, 11, 10, 90, 184, 77, 184, this::onPress));

		sliderR.setTexture(CAROUSEL);
		sliderG.setTexture(CAROUSEL);
		sliderB.setTexture(CAROUSEL);
		buttonColorApply.setTexture(CAROUSEL);

		speciesListWidget.setEntries(availableSpecies);
	}

	private void onSliderColorApplied()
	{
		var speciesEntry = speciesListWidget.getSelectedOrNull();
		var selectedVariableEntry = speciesVariableListWidget.getSelectedOrNull();
		if (speciesEntry == null || selectedVariableEntry == null)
			return;

		var selectedSpecies = speciesEntry.getValue();
		var selectedVariable = selectedVariableEntry.getValue();

		if (!(selectedVariable instanceof SpeciesColorVariable))
			return;

		var r = sliderR.getValue();
		var g = sliderG.getValue();
		var b = sliderB.getValue();
		selectedSpecies.setVariable(selectedVariable, ColorUtil.toResourceId(ColorUtil.packFloatRgb(r, g, b)));
	}

	private void onSliderColorChanged()
	{
	}

	public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_RIGHT)
			return moveToNextVariableOption(keyCode == GLFW.GLFW_KEY_LEFT);

		if (super.keyPressed(keyCode, scanCode, modifiers))
			return true;
		else if (this.speciesListWidget.getSelectedOrNull() != null)
			return this.speciesListWidget.keyPressed(keyCode, scanCode, modifiers);
		else if (this.speciesVariableListWidget.getSelectedOrNull() != null)
			return this.speciesVariableListWidget.keyPressed(keyCode, scanCode, modifiers);
		else
			return false;
	}

	private boolean moveToNextVariableOption(boolean left)
	{
		var speciesEntry = speciesListWidget.getSelectedOrNull();
		var selectedVariableEntry = speciesVariableListWidget.getSelectedOrNull();
		if (speciesEntry == null || selectedVariableEntry == null)
			return false;

		var selectedSpecies = speciesEntry.getValue();
		var selectedVariable = selectedVariableEntry.getValue();

		if (selectedVariable instanceof SpeciesColorVariable && selectedVariable.getPossibleValues().isEmpty())
			return false;

		List<String> values = selectedVariable.getPossibleValues();
		var selectedValue = selectedSpecies.getVariable(selectedVariable);

		var nextIndex = values.indexOf(selectedValue);

		if (left)
		{
			nextIndex--;
			carouselTimer = -CAROUSEL_TIMER_MAX;
		}
		else
		{
			nextIndex++;
			carouselTimer = CAROUSEL_TIMER_MAX;
		}

		looping = nextIndex == values.size() || nextIndex == -1;

		selectedSpecies.setVariable(selectedVariable, values.get((values.size() + nextIndex) % values.size()));
		return true;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button)
	{
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
		{
			sliderR.commit();
			sliderG.commit();
			sliderB.commit();
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public void tick()
	{
		super.tick();

		if (carouselTimer > 0)
			carouselTimer--;
		else if (carouselTimer < 0)
			carouselTimer++;
	}

	public boolean mouseScrolled(double mouseX, double mouseY, double amount)
	{
		if (Math.abs(mouseX - width / 2) < 128 && Math.abs(mouseY - height / 2) < 91)
		{
			moveToNextVariableOption(amount > 0);
			return true;
		}

		return super.mouseScrolled(mouseX, mouseY, amount);
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		this.renderBackground(matrices);
		this.speciesListWidget.render(matrices, mouseX, mouseY, delta);
		this.speciesVariableListWidget.render(matrices, mouseX, mouseY, delta);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 15, 16777215);

		var x = width / 2;
		var y = height / 2;
		var modelSize = 60;

		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.setShaderTexture(0, CAROUSEL);
		drawTexture(matrices, width / 2 - 128, height / 2 - 91, 0, 0, 256, 182);

		sliderR.visible = false;
		sliderG.visible = false;
		sliderB.visible = false;
		buttonColorApply.visible = false;

		var speciesEntry = speciesListWidget.getSelectedOrNull();
		var selectedVariableEntry = speciesVariableListWidget.getSelectedOrNull();
		if (speciesEntry != null)
		{
			var selectedSpecies = speciesEntry.getValue();
			SpeciesVariable selectedVariable = null;

			List<String> values;
			String selectedValue;
			int selectedIndex;

			if (selectedSpecies != null)
			{
				selectedVariable = selectedVariableEntry.getValue();

				if (selectedVariable instanceof SpeciesColorVariable && selectedVariable.getPossibleValues().isEmpty())
				{
					sliderR.visible = true;
					sliderG.visible = true;
					sliderB.visible = true;
					buttonColorApply.visible = true;

					renderColorPreview(x, y);

					values = List.of(selectedSpecies.getVariable(selectedVariable));
				}
				else
					values = selectedVariable.getPossibleValues();

				selectedValue = selectedSpecies.getVariable(selectedVariable);

				selectedSpecies.setGender(gender);

				selectedIndex = Math.max(0, values.indexOf(selectedValue));

				drawCenteredText(matrices, this.textRenderer, new TranslatableText(selectedVariable.getTranslationFor(selectedValue)), this.width / 2, height / 2 + 70, 16777215);
			}
			else
			{
				values = List.of(SpeciesVariable.NONE);
				selectedValue = null;
				selectedIndex = 0;
			}

			matrices.push();

			var mat2 = RenderSystem.getModelViewStack();

			for (var j = 0; j < values.size(); j++)
			{
				var value = values.get(j);

				if (selectedSpecies != null)
					selectedSpecies.setVariable(selectedVariable, value);

				mat2.push();

				float offsetTimer = j - selectedIndex;
				var timer = carouselTimer / (float)CAROUSEL_TIMER_MAX;

				if (looping)
					timer = MathHelper.lerp(timer, 0, -1 - (values.size() - 1) / 20f);

				offsetTimer += Ease.inCubic(timer);
				var offset = Math.signum(offsetTimer) * Math.pow(Math.abs((offsetTimer * 0.8f)), 0.7f) * modelSize;

				mat2.translate((int)(x + offset), y, 0);

				var scale = -Math.abs(offsetTimer / 3f) + 1;
				mat2.translate(0, offset / 2f * (scale + 0.3f), 0);
				MatrixStackUtil.scalePos(mat2, scale, scale, scale);
				mat2.translate(0, modelSize, 0);

				if (scale > 0)
					drawEntity(mat2, selectedSpecies == null ? null : selectedSpecies.serialize(), 0, 0, modelSize, (float)(x - mouseX + offset), y - modelSize / 2f - mouseY);

				mat2.pop();
			}

			RenderSystem.applyModelViewMatrix();

			matrices.pop();

			if (selectedSpecies != null)
				selectedSpecies.setVariable(selectedVariable, selectedValue);
		}

		super.render(matrices, mouseX, mouseY, delta);
	}

	private void renderColorPreview(int x, int y)
	{
		var x0 = x + 132;
		var x1 = x0 + 60;
		var y0 = y + 30;
		var y1 = y0 + 20;

		var u0 = 0;
		var u1 = 10;
		var v0 = 0;
		var v1 = 10;

		var z = 0;

		var r = sliderR.getValue();
		var g = sliderG.getValue();
		var b = sliderB.getValue();

		var bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex((float)x0, (float)y1, (float)z).color(r, g, b, 1).texture(u0, v1).next();
		bufferBuilder.vertex((float)x1, (float)y1, (float)z).color(r, g, b, 1).texture(u1, v1).next();
		bufferBuilder.vertex((float)x1, (float)y0, (float)z).color(r, g, b, 1).texture(u1, v0).next();
		bufferBuilder.vertex((float)x0, (float)y0, (float)z).color(r, g, b, 1).texture(u0, v0).next();
		bufferBuilder.end();

		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		BufferRenderer.draw(bufferBuilder);
	}

	public void drawEntity(MatrixStack matrixStack, String speciesString, int x, int y, int size, float mouseX, float mouseY)
	{
		matrixStack.push();

		PlayerEntity entity = client.player;
		var mouseYaw = (float)Math.atan(mouseX / 40.0F);
		var mousePitch = (float)Math.atan(mouseY / 40.0F);
		matrixStack.translate(0.0D, 0.0D, 100.0D);
		MatrixStackUtil.scalePos(matrixStack, size, size, -size);
		RenderSystem.applyModelViewMatrix();

		MatrixStack matrixStack2 = new MatrixStack();
		var quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
		var quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(mousePitch * 20.0F);
		quaternion.hamiltonProduct(quaternion2);
		matrixStack2.multiply(quaternion);
		var h = entity.bodyYaw;
		var i = entity.getYaw();
		var j = entity.getPitch();
		var k = entity.prevHeadYaw;
		var l = entity.headYaw;
		entity.bodyYaw = 180.0F + mouseYaw * 20.0F;
		entity.setYaw(180.0F + mouseYaw * 40.0F);
		entity.setPitch(-mousePitch * 20.0F);
		entity.headYaw = entity.getYaw();
		entity.prevHeadYaw = entity.getYaw();

		var immediate = client.getBufferBuilders().getEntityVertexConsumers();

		var erda = (EntityRenderDispatcherAccessor)client.getEntityRenderDispatcher();
		var renderers = erda.getModelRenderers();

		DiffuseLighting.method_34742();
		EntityRenderDispatcher entityRenderDispatcher = client.getEntityRenderDispatcher();
		quaternion2.conjugate();
		entityRenderDispatcher.setRotation(quaternion2);
		entityRenderDispatcher.setRenderShadows(false);
		RenderSystem.runAsFancy(() -> {
			if (speciesString == null)
			{
				entityRenderDispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixStack2, immediate, 0xf000f0);
			}
			else
			{
				CameraHelper.forcePlayerRender = true;

				var species = SwgSpeciesRegistry.deserialize(speciesString);

				var renderer = renderers.get(species.getModel().toString());
				SwgSpeciesModels.mutateModel(client.player, species, renderer);

				if (renderer instanceof PlayerSpeciesModelRenderer perwm)
				{
					var texture = SwgSpeciesModels.getTexture(entity, species);

					if (!texture.equals(Client.TEX_TRANSPARENT))
						perwm.renderWithTexture(texture, client.player, 1, 1, matrixStack2, immediate, 0xf000f0);

				}
				else if (renderer != null)
					renderer.render(client.player, 1, 1, matrixStack2, immediate, 0xf000f0);

				CameraHelper.forcePlayerRender = false;
			}
		});
		immediate.draw();
		entityRenderDispatcher.setRenderShadows(true);

		immediate.draw();
		entity.bodyYaw = h;
		entity.setYaw(i);
		entity.setPitch(j);
		entity.prevHeadYaw = k;
		entity.headYaw = l;

		matrixStack.pop();

		RenderSystem.applyModelViewMatrix();
		DiffuseLighting.enableGuiDepthLighting();
	}

	public void renderBackgroundTexture(int vOffset)
	{
		var tessellator = Tessellator.getInstance();
		var bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.setShaderTexture(0, BACKGROUND);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		var f = 32.0F;
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(0.0D, this.height, 0.0D).texture(0.0F, (float)this.height / 32.0F + (float)vOffset).color(64, 64, 64, 255).next();
		bufferBuilder.vertex(this.width, this.height, 0.0D).texture((float)this.width / 32.0F, (float)this.height / 32.0F + (float)vOffset).color(64, 64, 64, 255).next();
		bufferBuilder.vertex(this.width, 0.0D, 0.0D).texture((float)this.width / 32.0F, (float)vOffset).color(64, 64, 64, 255).next();
		bufferBuilder.vertex(0.0D, 0.0D, 0.0D).texture(0.0F, (float)vOffset).color(64, 64, 64, 255).next();
		tessellator.draw();
	}

	private void onPress(ButtonWidget button)
	{
		onSliderColorApplied();
	}
}
