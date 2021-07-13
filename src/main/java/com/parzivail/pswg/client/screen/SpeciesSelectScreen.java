package com.parzivail.pswg.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.screen.widget.EventCheckboxWidget;
import com.parzivail.pswg.client.screen.widget.SimpleListWidget;
import com.parzivail.pswg.component.SwgEntityComponents;
import com.parzivail.pswg.component.SwgPersistentComponents;
import com.parzivail.pswg.container.SwgPackets;
import com.parzivail.pswg.container.SwgSpeciesRegistry;
import com.parzivail.pswg.mixin.EntityRenderDispatcherAccessor;
import com.parzivail.pswg.species.SpeciesGender;
import com.parzivail.pswg.species.SpeciesVariable;
import com.parzivail.pswg.species.SwgSpecies;
import com.parzivail.util.math.Ease;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.glfw.GLFW;

import java.util.List;

@Environment(EnvType.CLIENT)
public class SpeciesSelectScreen extends Screen
{
	//	public static final Identifier BACKGROUND = Resources.identifier("textures/block/");
	public static final Identifier BACKGROUND = new Identifier("textures/gui/options_background.png");

	public static final Identifier CAROUSEL = Resources.id("textures/gui/species_select/carousel.png");

	private final Screen parent;

	private SimpleListWidget<SwgSpecies> speciesListWidget;
	private SimpleListWidget<SpeciesVariable> speciesVariableListWidget;

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
	}

	@Override
	protected void init()
	{
		SwgPersistentComponents c = SwgEntityComponents.getPersistent(client.player);
		playerSpecies = c.getSpecies();

		if (playerSpecies != null)
			this.gender = playerSpecies.getGender();

		speciesVariableListWidget = new SimpleListWidget<>(client, width / 2 + 128, height / 2 - 91, 80, 182, 15, entry -> {
			updateAbility();
		});
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
			return entries.stream().filter(swgSpeciesEntry -> playerSpecies.isSameSpecies(swgSpeciesEntry.getValue())).findFirst().orElse(null);
		});

		this.addSelectableChild(speciesVariableListWidget);
		this.addSelectableChild(speciesListWidget);

		this.addSelectableChild(new ButtonWidget(this.width / 2 - 120, this.height / 2 - 10, 20, 20, new LiteralText("<"), (button) -> {
			moveToNextVariableOption(true);
		}));

		this.addSelectableChild(new ButtonWidget(this.width / 2 + 100, this.height / 2 - 10, 20, 20, new LiteralText(">"), (button) -> {
			moveToNextVariableOption(false);
		}));

		this.addSelectableChild(new ButtonWidget(this.width / 2 - 100 - 75, this.height - 26, 95, 20, ScreenTexts.BACK, (button) -> {
			this.client.openScreen(this.parent);
		}));

		this.addSelectableChild(new ButtonWidget(this.width / 2 - 60, this.height - 26, 120, 20, new TranslatableText("gui.pswg.apply"), (button) -> {
			if (speciesListWidget.getSelectedOrNull() == null)
				return;

			PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());

			SwgSpecies selected = speciesListWidget.getSelectedOrNull().getValue();

			if (selected == null)
				passedData.writeString("minecraft:none");
			else
			{
				if (!selected.isSameSpecies(playerSpecies))
					this.playerSpecies = SwgSpeciesRegistry.deserialize(selected.serialize());

				if (speciesVariableListWidget.getSelectedOrNull() == null)
					return;

				SpeciesVariable selectedVariable = speciesVariableListWidget.getSelectedOrNull().getValue();

				this.playerSpecies.setVariable(selectedVariable, selected.getVariable(selectedVariable));

				passedData.writeString(this.playerSpecies.serialize());
			}

			ClientPlayNetworking.send(SwgPackets.C2S.PacketSetOwnSpecies, passedData);
		}));

		this.addSelectableChild(new EventCheckboxWidget(this.width / 2 + 105 - 25, this.height - 26, 20, 20, new TranslatableText("gui.pswg.use_female_model"), this.gender == SpeciesGender.FEMALE, (checked) -> {
			gender = checked ? SpeciesGender.FEMALE : SpeciesGender.MALE;
			if (this.playerSpecies != null)
				this.playerSpecies.setGender(gender);
		}));

		speciesListWidget.setEntries(availableSpecies);
		speciesListWidget.setSelected(speciesListWidget.getEntries().get(1));
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
		SimpleListWidget.Entry<SwgSpecies> speciesEntry = speciesListWidget.getSelectedOrNull();
		SimpleListWidget.Entry<SpeciesVariable> selectedVariableEntry = speciesVariableListWidget.getSelectedOrNull();
		if (speciesEntry == null || selectedVariableEntry == null)
			return false;

		SwgSpecies selectedSpecies = speciesEntry.getValue();
		SpeciesVariable selectedVariable = selectedVariableEntry.getValue();
		String[] values = selectedVariable.getPossibleValues();
		String selectedValue = selectedSpecies.getVariable(selectedVariable);

		int nextIndex = ArrayUtils.indexOf(values, selectedValue);

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

		looping = nextIndex == values.length || nextIndex == -1;

		selectedSpecies.setVariable(selectedVariable, values[(values.length + nextIndex) % values.length]);
		return true;
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

		int x = width / 2;
		int y = height / 2;
		int modelSize = 60;

		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.setShaderTexture(0, CAROUSEL);
		drawTexture(matrices, width / 2 - 128, height / 2 - 91, 0, 0, 256, 182);

		SimpleListWidget.Entry<SwgSpecies> speciesEntry = speciesListWidget.getSelectedOrNull();
		SimpleListWidget.Entry<SpeciesVariable> selectedVariableEntry = speciesVariableListWidget.getSelectedOrNull();
		if (speciesEntry != null && selectedVariableEntry != null)
		{
			SwgSpecies selectedSpecies = speciesEntry.getValue();
			SpeciesVariable selectedVariable = selectedVariableEntry.getValue();

			String[] values = selectedVariable.getPossibleValues();
			String selectedValue = selectedSpecies.getVariable(selectedVariable);

			if (selectedSpecies.isSameSpecies(this.playerSpecies))
				selectedSpecies.copy(this.playerSpecies);

			selectedSpecies.setGender(gender);

			int selectedIndex = ArrayUtils.indexOf(values, selectedValue);

			drawCenteredText(matrices, this.textRenderer, new TranslatableText(selectedVariable.getTranslationFor(selectedValue)), this.width / 2, height / 2 + 70, 16777215);

			matrices.push();

			for (int j = 0; j < values.length; j++)
			{
				String value = values[j];
				selectedSpecies.setVariable(selectedVariable, value);

				matrices.push();

				float offsetTimer = j - selectedIndex;
				float timer = (carouselTimer - (delta * Math.signum(carouselTimer))) / (float)CAROUSEL_TIMER_MAX;

				if (looping)
					timer = MathHelper.lerp(timer, 0, -1 - (values.length - 1) / 20f);

				offsetTimer += Ease.inCubic(timer);
				double offset = Math.signum(offsetTimer) * Math.pow(Math.abs((offsetTimer * 0.8f)), 0.7f) * modelSize;

				matrices.translate((int)(x + offset), y, 0);

				float scale = -Math.abs(offsetTimer / 3f) + 1;
				matrices.translate(0, offset / 2f * (scale + 0.3f), 0);
				matrices.scale(scale, scale, scale);
				matrices.translate(0, modelSize, 0);

				drawEntity(matrices, selectedSpecies.serialize(), 0, 0, modelSize, (float)(x - mouseX + offset), y - modelSize / 2f - mouseY);

				matrices.pop();
			}

			matrices.pop();

			selectedSpecies.setVariable(selectedVariable, selectedValue);
		}

		super.render(matrices, mouseX, mouseY, delta);
	}

	public void drawEntity(MatrixStack matrixStack, String speciesString, int x, int y, int size, float mouseX, float mouseY)
	{
		if (speciesString == null)
			return;

		matrixStack.push();
//		RenderSystem.pushMatrix();

		PlayerEntity entity = client.player;
		float f = (float)Math.atan(mouseX / 40.0F);
		float g = (float)Math.atan(mouseY / 40.0F);
//		RenderSystem.translatef((float)x, (float)y, 1000.0F);
//		RenderSystem.scalef(1.0F, 1.0F, -1.0F);
		matrixStack.translate(0.0D, 0.0D, 500.0D);
		matrixStack.scale((float)size, (float)size, -(float)size);
		Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
		Quaternion quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(g * 20.0F);
		quaternion.hamiltonProduct(quaternion2);
		matrixStack.multiply(quaternion);
		float h = entity.bodyYaw;
		float i = entity.getYaw();
		float j = entity.getYaw();
		float k = entity.prevHeadYaw;
		float l = entity.headYaw;
		entity.bodyYaw = 180.0F + f * 20.0F;
		entity.setYaw(180.0F + f * 40.0F);
		entity.setPitch(-g * 20.0F);
		entity.headYaw = entity.getYaw();
		entity.prevHeadYaw = entity.getYaw();

		VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();

		EntityRenderDispatcherAccessor erda = (EntityRenderDispatcherAccessor)client.getEntityRenderDispatcher();
		var renderers = erda.getModelRenderers();

		SwgSpecies species = SwgSpeciesRegistry.deserialize(speciesString);
		var renderer = renderers.get("default" /*species.getModel().toString()*/);

		// TODO: cast renderer to PlayerEntityRendererWithModel

//		renderer.renderWithTexture(SwgSpeciesModels.getTexture(species), client.player, 1, 1, matrixStack, immediate, 0xf000f0);
		renderer.render(client.player, 1, 1, matrixStack, immediate, 0xF000F0);

		immediate.draw();
		entity.bodyYaw = h;
		entity.setYaw(i);
		entity.setPitch(j);
		entity.prevHeadYaw = k;
		entity.headYaw = l;

		matrixStack.pop();
//		RenderSystem.popMatrix();
	}

	public void renderBackgroundTexture(int vOffset)
	{
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.setShaderTexture(0, BACKGROUND);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 32.0F;
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(0.0D, this.height, 0.0D).texture(0.0F, (float)this.height / 32.0F + (float)vOffset).color(64, 64, 64, 255).next();
		bufferBuilder.vertex(this.width, this.height, 0.0D).texture((float)this.width / 32.0F, (float)this.height / 32.0F + (float)vOffset).color(64, 64, 64, 255).next();
		bufferBuilder.vertex(this.width, 0.0D, 0.0D).texture((float)this.width / 32.0F, (float)vOffset).color(64, 64, 64, 255).next();
		bufferBuilder.vertex(0.0D, 0.0D, 0.0D).texture(0.0F, (float)vOffset).color(64, 64, 64, 255).next();
		tessellator.draw();
	}
}
