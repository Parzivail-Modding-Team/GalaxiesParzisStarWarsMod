package com.parzivail.pswg.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Client;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.render.camera.CameraHelper;
import com.parzivail.pswg.client.render.player.PlayerSpeciesModelRenderer;
import com.parzivail.pswg.client.species.SwgSpeciesIcons;
import com.parzivail.pswg.client.species.SwgSpeciesRenderer;
import com.parzivail.pswg.container.SwgSpeciesRegistry;
import com.parzivail.pswg.mixin.EntityRenderDispatcherAccessor;
import com.parzivail.util.client.screen.blit.*;
import com.parzivail.util.math.MathUtil;
import com.parzivail.util.math.MatrixStackUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class CharacterScreen extends Screen
{
	private static final Identifier OPTIONS_BACKGROUND = new Identifier("textures/gui/options_background.png");
	private static final Identifier BACKGROUND = Resources.id("textures/gui/character/background.png");

	private static final BlitRectangle BACKGROUND_PATCH = new BlitRectangle(
			new BlittableAsset(10, 241, 8, 8, 512, 512),
			7, 25, 79, 190
	);
	private static final Blittable3Patch SCROLLBAR_TRACK = new Blittable3Patch(
			new BlittableAsset(0, 241, 3, 3, 512, 512),
			new BlittableAsset(5, 241, 3, 8, 512, 512),
			new BlittableAsset(0, 246, 3, 3, 512, 512),
			1, 1
	);
	private static final BlitScrollbar SCROLLBAR = new BlitScrollbar(
			new HoverableBlittableAsset(
					new BlittableAsset(0, 251, 7, 15, 512, 512),
					new BlittableAsset(7, 251, 7, 15, 512, 512)
			),
			88, 24, 192
	);

	private static final BlittableAsset SLIDER_TRACK = new BlittableAsset(0, 424, 8, 4, 512, 512);
	private static final Blittable3Patch SLIDER_WHITE = new Blittable3Patch(
			new BlittableAsset(0, 430, 4, 4, 512, 512),
			SLIDER_TRACK,
			new BlittableAsset(5, 430, 5, 4, 512, 512),
			2, 2
	);
	private static final Blittable3Patch SLIDER_RED = new Blittable3Patch(
			new BlittableAsset(0, 436, 4, 4, 512, 512),
			SLIDER_TRACK,
			new BlittableAsset(5, 436, 5, 4, 512, 512),
			2, 2
	);
	private static final Blittable3Patch SLIDER_GREEN = new Blittable3Patch(
			new BlittableAsset(0, 442, 4, 4, 512, 512),
			SLIDER_TRACK,
			new BlittableAsset(5, 442, 5, 4, 512, 512),
			2, 2
	);
	private static final Blittable3Patch SLIDER_BLUE = new Blittable3Patch(
			new BlittableAsset(0, 448, 4, 4, 512, 512),
			SLIDER_TRACK,
			new BlittableAsset(5, 448, 5, 4, 512, 512),
			2, 2
	);
	private static final HoverableBlittableAsset SLIDER_THUMB = new HoverableBlittableAsset(
			new BlittableAsset(0, 268, 6, 8, 512, 512),
			new BlittableAsset(6, 268, 6, 8, 512, 512)
	);

	private static final BlitRectangle LEFT_ARROW = new BlitRectangle(
			new HoverableBlittableAsset(
					new BlittableAsset(0, 278, 7, 11, 512, 512),
					new BlittableAsset(7, 278, 7, 11, 512, 512)
			),
			126,
			110
	);
	private static final BlitRectangle RIGHT_ARROW = new BlitRectangle(
			new HoverableBlittableAsset(
					new BlittableAsset(0, 291, 7, 11, 512, 512),
					new BlittableAsset(7, 291, 7, 11, 512, 512)
			),
			233,
			110
	);
	private static final BlitRectangle NEXT_PAGE_BTN = new BlitRectangle(new HoverableBlittableAsset(
			new BlittableAsset(0, 304, 42, 18, 512, 512),
			new BlittableAsset(42, 304, 42, 18, 512, 512)
	), 313, 210);
	private static final BlitRectangle APPLY_BTN = new BlitRectangle(new HoverableBlittableAsset(
			new BlittableAsset(0, 324, 42, 18, 512, 512),
			new BlittableAsset(0, 324, 42, 18, 512, 512)
	), 313, 210);
	private static final BlitRectangle RANDOM_BUTTON = new BlitRectangle(new HoverableBlittableAsset(
			new BlittableAsset(0, 344, 20, 18, 512, 512),
			new BlittableAsset(20, 344, 20, 18, 512, 512)
	), 128, 210);
	private static final BlitRectangle GENDER_TOGGLE = new BlitRectangle(new TogglableBlittableAsset(
			new HoverableBlittableAsset(
					new BlittableAsset(0, 404, 20, 18, 512, 512),
					new BlittableAsset(20, 404, 20, 18, 512, 512)
			),
			new HoverableBlittableAsset(
					new BlittableAsset(40, 404, 20, 18, 512, 512),
					new BlittableAsset(60, 404, 20, 18, 512, 512)
			)
	), 158, 210);
	private static final BlitRectangle SAVE_BUTTON = new BlitRectangle(new HoverableBlittableAsset(
			new BlittableAsset(0, 364, 20, 18, 512, 512),
			new BlittableAsset(20, 364, 20, 18, 512, 512)
	), 188, 210);
	private static final BlitRectangle EXPORT_BUTTON = new BlitRectangle(new HoverableBlittableAsset(
			new BlittableAsset(0, 384, 20, 18, 512, 512),
			new BlittableAsset(20, 384, 20, 18, 512, 512)
	), 218, 210);

	private final Screen parent;

	public CharacterScreen(Screen parent)
	{
		super(Text.translatable("screen.pswg.character"));
		this.parent = parent;
	}

	private void updateAbility()
	{
	}

	@Override
	protected void init()
	{
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		//		if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_RIGHT)
		//			return moveToNextVariableOption(keyCode == GLFW.GLFW_KEY_LEFT);

		//		else if (this.speciesListWidget.getSelectedOrNull() != null)
		//			return this.speciesListWidget.keyPressed(keyCode, scanCode, modifiers);
		//		else if (this.speciesVariableListWidget.getSelectedOrNull() != null)
		//			return this.speciesVariableListWidget.keyPressed(keyCode, scanCode, modifiers);
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if (SCROLLBAR.contains((int)mouseX, (int)mouseY))
			SCROLLBAR.setScrolling(true);

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button)
	{
		//		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
		//		{
		//			sliderR.commit();
		//			sliderG.commit();
		//			sliderB.commit();
		//		}
		SCROLLBAR.setScrolling(false);
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public void tick()
	{
		super.tick();
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount)
	{
		if (BACKGROUND_PATCH.contains((int)mouseX, (int)mouseY) || SCROLLBAR.contains((int)mouseX, (int)mouseY))
			SCROLLBAR.inputScroll(amount);
		return super.mouseScrolled(mouseX, mouseY, amount);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		this.renderBackground(matrices);

		var x = width / 2 - 213;
		var y = height / 2 - 120;

		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.setShaderTexture(0, BACKGROUND);

		BACKGROUND_PATCH.setOrigin(x, y);
		BACKGROUND_PATCH.blit(matrices);

		var allSpecies = SwgSpeciesRegistry.ALL_SPECIES.get();
		SCROLLBAR.setScrollInputFactor(allSpecies.size());

		var lineHeight = 25;
		var contentHeight = lineHeight * allSpecies.size();
		var areaHeight = 190;
		var scrollAmount = Math.max(0, contentHeight - areaHeight);

		var lineContentHeight = 20;
		var lineOffsetY = (lineHeight - lineContentHeight) / 2;

		var iconY = -(int)(scrollAmount * SCROLLBAR.getScroll());
		for (var entry : allSpecies)
		{
			if (iconY >= -lineHeight && iconY <= areaHeight)
			{
				var hovering = entry.getSlug().equals(SwgSpeciesRegistry.SPECIES_TOGRUTA);

				SwgSpeciesIcons.renderLarge(matrices, x + 7, y + 25 + iconY + lineOffsetY, entry.getSlug(), hovering);
			}
			iconY += lineHeight;
		}

		iconY = -(int)(scrollAmount * SCROLLBAR.getScroll());
		for (var entry : allSpecies)
		{
			if (iconY >= -lineHeight && iconY <= areaHeight)
			{
				var hovering = entry.getSlug().equals(SwgSpeciesRegistry.SPECIES_TOGRUTA) ||
				               (!SCROLLBAR.isScrolling() && MathUtil.rectContains(x + 7, y + 25 + iconY + lineOffsetY, 80, lineContentHeight, mouseX, mouseY));

				var translatedText = Text.translatable(SwgSpeciesRegistry.getTranslationKey(entry.getSlug()));
				var wrapped = this.textRenderer.wrapLines(translatedText, 60);
				this.textRenderer.draw(matrices, wrapped.get(0), x + 30, y + 31 + iconY + lineOffsetY, hovering ? 0xFFFFFF : 0x000000);
			}

			iconY += lineHeight;
		}

		RenderSystem.setShaderTexture(0, BACKGROUND);
		drawTexture(matrices, x, y, 0, 0, 427, 240, 512, 512);

		SCROLLBAR.setOrigin(x, y);
		LEFT_ARROW.setOrigin(x, y);
		RIGHT_ARROW.setOrigin(x, y);
		RANDOM_BUTTON.setOrigin(x, y);
		GENDER_TOGGLE.setOrigin(x, y);
		NEXT_PAGE_BTN.setOrigin(x, y);
		SAVE_BUTTON.setOrigin(x, y);
		EXPORT_BUTTON.setOrigin(x, y);

		SCROLLBAR.updateMouseState(mouseX, mouseY);
		LEFT_ARROW.updateMouseState(mouseX, mouseY);
		RIGHT_ARROW.updateMouseState(mouseX, mouseY);
		RANDOM_BUTTON.updateMouseState(mouseX, mouseY);
		GENDER_TOGGLE.updateMouseState(mouseX, mouseY);
		NEXT_PAGE_BTN.updateMouseState(mouseX, mouseY);
		SAVE_BUTTON.updateMouseState(mouseX, mouseY);
		EXPORT_BUTTON.updateMouseState(mouseX, mouseY);

		SCROLLBAR_TRACK.blitVertical(matrices, x + 90, y + 25, 190);
		SCROLLBAR.blit(matrices);
		LEFT_ARROW.blit(matrices);
		RIGHT_ARROW.blit(matrices);
		RANDOM_BUTTON.blit(matrices);
		GENDER_TOGGLE.blit(matrices);
		NEXT_PAGE_BTN.blit(matrices);
		SAVE_BUTTON.blit(matrices);
		EXPORT_BUTTON.blit(matrices);

		var rsm = RenderSystem.getModelViewStack();
		rsm.push();
		rsm.translate(x + 182, y + 190, 50);
		rsm.multiply(new Quaternion(-22, 0, 0, true));
		rsm.multiply(new Quaternion(0, -45, 0, true));
		drawEntity(rsm, null, 0, 0, 80, 0, 0);
		rsm.pop();
		RenderSystem.applyModelViewMatrix();

		this.textRenderer.draw(matrices, this.title, x + 9, y + 9, 0x404040);

		matrices.push();
		matrices.translate(x + 334, y + 15, 0);
		matrices.scale(2, 2, 2);
		var text = Text.literal("Togruta");
		var orderedText = text.asOrderedText();
		textRenderer.draw(matrices, orderedText, -textRenderer.getWidth(orderedText) / 2f, 0, 0x000000);
		matrices.pop();

		var lines = this.textRenderer.wrapLines(
				Text.literal("The ")
				    .append(Text.literal("Togruta").formatted(Formatting.BOLD))
				    .append(Text.literal(
						            " were a sentient humanoid species, " +
						            "characterized by their colorful skin tones, large montrals, " +
						            "head tails, and white facial pigments. They hailed from the" +
						            " planet "
				            )
				    )
				    .append(Text.literal("Shili").formatted(Formatting.GOLD))
				    .append(Text.literal(
						            ", in the Expansion Region. Although Shili was " +
						            "their homeworld, they also had a colony of some 50,000 " +
						            "individuals on the planet "
				            )
				    )
				    .append(Text.literal("Kiros").formatted(Formatting.GOLD))
				    .append(Text.literal(", also in the Expansion Region. Some well known Togruta included the "))
				    .append(Text.literal("Jedi Master Shaak Ti").formatted(Formatting.BLUE))
				    .append(Text.literal(", "))
				    .append(Text.literal("Jedi Master Jora Malli").formatted(Formatting.BLUE))
				    .append(Text.literal(", "))
				    .append(Text.literal("Supreme Chancellor Kirames Kaj").formatted(Formatting.DARK_GREEN))
				    .append(Text.literal(", and "))
				    .append(Text.literal("Padawan Ahsoka Tano").formatted(Formatting.YELLOW))
				    .append(Text.literal(".")),
				150
		);

		for (var m = 0; m < lines.size(); ++m)
			this.textRenderer.draw(matrices, lines.get(m), x + 260, y + 40 + m * textRenderer.fontHeight, 0);

		super.render(matrices, mouseX, mouseY, delta);
	}

	public void drawEntity(MatrixStack matrixStack, String speciesString, int x, int y, int size, float mouseX, float mouseY)
	{
		matrixStack.push();

		PlayerEntity entity = client.player;
		var mouseYaw = (float)Math.atan(mouseX / 40.0F);
		var mousePitch = (float)Math.atan(mouseY / 40.0F);
		MatrixStackUtil.scalePos(matrixStack, size, size, -size);
		RenderSystem.applyModelViewMatrix();

		var matrixStack2 = new MatrixStack();
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

		entity.limbDistance = 0;

		var immediate = client.getBufferBuilders().getEntityVertexConsumers();

		var erda = (EntityRenderDispatcherAccessor)client.getEntityRenderDispatcher();
		var renderers = erda.getModelRenderers();

		DiffuseLighting.method_34742();
		var entityRenderDispatcher = client.getEntityRenderDispatcher();
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
				SwgSpeciesRenderer.mutateModel(client.player, species, renderer);

				if (renderer instanceof PlayerSpeciesModelRenderer perwm)
				{
					var texture = SwgSpeciesRenderer.getTexture(entity, species);

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

	@Override
	public void renderBackgroundTexture(int vOffset)
	{
		var tessellator = Tessellator.getInstance();
		var bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.setShaderTexture(0, OPTIONS_BACKGROUND);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		var f = 32.0F;
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(0.0D, this.height, 0.0D).texture(0.0F, (float)this.height / 32.0F + (float)vOffset).color(64, 64, 64, 255).next();
		bufferBuilder.vertex(this.width, this.height, 0.0D).texture((float)this.width / 32.0F, (float)this.height / 32.0F + (float)vOffset).color(64, 64, 64, 255).next();
		bufferBuilder.vertex(this.width, 0.0D, 0.0D).texture((float)this.width / 32.0F, (float)vOffset).color(64, 64, 64, 255).next();
		bufferBuilder.vertex(0.0D, 0.0D, 0.0D).texture(0.0F, (float)vOffset).color(64, 64, 64, 255).next();
		tessellator.draw();
	}
}
