package com.parzivail.pswg.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Client;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.render.camera.CameraHelper;
import com.parzivail.pswg.client.render.player.PlayerSpeciesModelRenderer;
import com.parzivail.pswg.client.species.SwgSpeciesRenderer;
import com.parzivail.pswg.container.SwgSpeciesRegistry;
import com.parzivail.pswg.mixin.EntityRenderDispatcherAccessor;
import com.parzivail.util.client.screen.Blittable3Patch;
import com.parzivail.util.client.screen.BlittableAsset;
import com.parzivail.util.math.MatrixStackUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class CharacterScreen extends Screen
{
	private static final Identifier OPTIONS_BACKGROUND = new Identifier("textures/gui/options_background.png");
	private static final Identifier BACKGROUND = Resources.id("textures/gui/character/background.png");

	private static final BlittableAsset SCROLLBAR_TRACK = new BlittableAsset(0, 241, 3, 8, 512, 512);
	private static final BlittableAsset SCROLLBAR_THUMB = new BlittableAsset(0, 251, 7, 15, 512, 512);
	private static final BlittableAsset SCROLLBAR_THUMB_HOVER = new BlittableAsset(7, 251, 7, 15, 512, 512);

	private static final BlittableAsset SLIDER_TRACK = new BlittableAsset(0, 424, 8, 4, 512, 512);
	private static final BlittableAsset SLIDER_CAP_WHITE_L = new BlittableAsset(0, 430, 4, 4, 512, 512);
	private static final BlittableAsset SLIDER_CAP_WHITE_R = new BlittableAsset(5, 430, 5, 4, 512, 512);
	private static final Blittable3Patch SLIDER_WHITE = new Blittable3Patch(SLIDER_CAP_WHITE_L, SLIDER_TRACK, SLIDER_CAP_WHITE_R, 2, 2);
	private static final BlittableAsset SLIDER_CAP_RED_L = new BlittableAsset(0, 436, 4, 4, 512, 512);
	private static final BlittableAsset SLIDER_CAP_RED_R = new BlittableAsset(5, 436, 5, 4, 512, 512);
	private static final Blittable3Patch SLIDER_RED = new Blittable3Patch(SLIDER_CAP_RED_L, SLIDER_TRACK, SLIDER_CAP_RED_R, 2, 2);
	private static final BlittableAsset SLIDER_CAP_GREEN_L = new BlittableAsset(0, 442, 4, 4, 512, 512);
	private static final BlittableAsset SLIDER_CAP_GREEN_R = new BlittableAsset(5, 442, 5, 4, 512, 512);
	private static final Blittable3Patch SLIDER_GREEN = new Blittable3Patch(SLIDER_CAP_GREEN_L, SLIDER_TRACK, SLIDER_CAP_GREEN_R, 2, 2);
	private static final BlittableAsset SLIDER_CAP_BLUE_L = new BlittableAsset(0, 448, 4, 4, 512, 512);
	private static final BlittableAsset SLIDER_CAP_BLUE_R = new BlittableAsset(5, 448, 5, 4, 512, 512);
	private static final Blittable3Patch SLIDER_BLUE = new Blittable3Patch(SLIDER_CAP_BLUE_L, SLIDER_TRACK, SLIDER_CAP_BLUE_R, 2, 2);
	private static final BlittableAsset SLIDER_THUMB = new BlittableAsset(0, 268, 6, 8, 512, 512);
	private static final BlittableAsset SLIDER_THUMB_HOVER = new BlittableAsset(6, 268, 6, 8, 512, 512);

	private static final BlittableAsset LEFT_ARROW = new BlittableAsset(0, 278, 7, 11, 512, 512);
	private static final BlittableAsset LEFT_ARROW_HOVER = new BlittableAsset(7, 278, 7, 11, 512, 512);
	private static final BlittableAsset RIGHT_ARROW = new BlittableAsset(0, 291, 7, 11, 512, 512);
	private static final BlittableAsset RIGHT_ARROW_HOVER = new BlittableAsset(7, 291, 7, 11, 512, 512);
	private static final BlittableAsset NEXT_PAGE_BTN = new BlittableAsset(0, 304, 42, 18, 512, 512);
	private static final BlittableAsset NEXT_PAGE_BTN_HOVER = new BlittableAsset(42, 304, 42, 18, 512, 512);
	private static final BlittableAsset APPLY_BTN = new BlittableAsset(0, 324, 42, 18, 512, 512);
	private static final BlittableAsset APPLY_BTN_HOVER = new BlittableAsset(42, 324, 42, 18, 512, 512);
	private static final BlittableAsset RANDOM_BUTTON = new BlittableAsset(0, 344, 20, 18, 512, 512);
	private static final BlittableAsset RANDOM_BUTTON_HOVER = new BlittableAsset(20, 344, 20, 18, 512, 512);
	private static final BlittableAsset SAVE_BUTTON = new BlittableAsset(0, 364, 20, 18, 512, 512);
	private static final BlittableAsset SAVE_BUTTON_HOVER = new BlittableAsset(20, 364, 20, 18, 512, 512);
	private static final BlittableAsset EXIT_BUTTON = new BlittableAsset(0, 384, 20, 18, 512, 512);
	private static final BlittableAsset EXIT_BUTTON_HOVER = new BlittableAsset(20, 384, 20, 18, 512, 512);
	private static final BlittableAsset GENDER_TOGGLE_MALE = new BlittableAsset(0, 404, 20, 18, 512, 512);
	private static final BlittableAsset GENDER_TOGGLE_MALE_HOVER = new BlittableAsset(20, 404, 20, 18, 512, 512);
	private static final BlittableAsset GENDER_TOGGLE_FEMALE = new BlittableAsset(40, 404, 20, 18, 512, 512);
	private static final BlittableAsset GENDER_TOGGLE_FEMALE_HOVER = new BlittableAsset(60, 404, 20, 18, 512, 512);

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
	public boolean mouseReleased(double mouseX, double mouseY, int button)
	{
//		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
//		{
//			sliderR.commit();
//			sliderG.commit();
//			sliderB.commit();
//		}
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
		drawTexture(matrices, x, y, 0, 0, 427, 240, 512, 512);

		SCROLLBAR_TRACK.blit(matrices, x + 90, y + 25, SCROLLBAR_TRACK.width(), 190);
		LEFT_ARROW.blit(matrices, x + 124, y + 110);
		RIGHT_ARROW.blit(matrices, x + 233, y + 110);
		RANDOM_BUTTON.blit(matrices, x + 158, y + 210);
		GENDER_TOGGLE_FEMALE.blit(matrices, x + 188, y + 210);
		NEXT_PAGE_BTN.blit(matrices, x + 313, y + 210);

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
		matrices.translate(x + 334, y + 20, 0);
		matrices.scale(2, 2, 2);
		drawCenteredText(matrices, this.textRenderer, Text.literal("Togruta"), 0, 0, 0xFFFFFF);
		matrices.pop();

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

		entity.limbDistance = 0;

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
