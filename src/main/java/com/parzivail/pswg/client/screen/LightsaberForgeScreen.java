package com.parzivail.pswg.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.render.item.LightsaberItemRenderer;
import com.parzivail.pswg.container.SwgPackets;
import com.parzivail.pswg.item.lightsaber.LightsaberItem;
import com.parzivail.pswg.item.lightsaber.data.LightsaberTag;
import com.parzivail.pswg.screen.LightsaberForgeScreenHandler;
import com.parzivail.util.client.ColorUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;

import java.util.function.Consumer;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class LightsaberForgeScreen extends HandledScreen<LightsaberForgeScreenHandler> implements ScreenHandlerListener
{
	private class MutableSlider extends SliderWidget
	{
		private final String translationKey;
		private final Function<Double, String> valueFormatter;
		private final Consumer<MutableSlider> callback;

		public MutableSlider(int x, int y, int width, int height, String translationKey, double value, Function<Double, String> valueFormatter, Consumer<MutableSlider> callback)
		{
			super(x, y, width, height, LiteralText.EMPTY, value);
			this.translationKey = translationKey;
			this.valueFormatter = valueFormatter;
			this.callback = callback;
			updateMessage();
		}

		@Override
		protected void updateMessage()
		{
			setMessage(new TranslatableText(translationKey, valueFormatter.apply(value)));
		}

		@Override
		protected void applyValue()
		{
			callback.accept(this);
		}

		public double getValue()
		{
			return value;
		}

		public void setValue(double value)
		{
			this.value = value;
			applyValue();
			updateMessage();
		}
	}

	private static class MutableCheckbox extends CheckboxWidget
	{
		private final Consumer<MutableCheckbox> callback;

		public MutableCheckbox(int x, int y, int width, int height, Text text, boolean checked, boolean showLabel, Consumer<MutableCheckbox> callback)
		{
			super(x, y, width, height, text, checked, showLabel);
			this.callback = callback;
		}

		public void setChecked(boolean checked)
		{
			if (isChecked() != checked)
				onPress();
		}

		@Override
		public void onPress()
		{
			super.onPress();
			callback.accept(this);
		}
	}

	private static final Identifier TEXTURE = Resources.id("textures/gui/container/lightsaber_forge.png");

	private final PlayerEntity player;

	private MutableSlider sliderHue;
	private MutableSlider sliderSat;
	private MutableSlider sliderVal;
	private MutableCheckbox cbUnstable;

	private float hue;
	private float sat;
	private float val;

	private ItemStack lightsaber = ItemStack.EMPTY;

	public LightsaberForgeScreen(LightsaberForgeScreenHandler handler, PlayerInventory inventory, Text title)
	{
		super(handler, inventory, title);
		backgroundWidth = 256;
		backgroundHeight = 241;
		player = inventory.player;
	}

	protected void init()
	{
		super.init();

		this.playerInventoryTitleX = 48;
		this.playerInventoryTitleY = this.backgroundHeight - 94;
		this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;

		Function<Double, String> valueFormatter = value -> String.format("%.3f", (int)Math.round(value * 1000) / 1000f);

		this.addDrawableChild(sliderHue = new MutableSlider(x + 41, y + 59, 100, 20, "Hue: %s", hue, valueFormatter, slider -> {
			hue = (float)slider.getValue();
			commitChanges();
		}));

		this.addDrawableChild(sliderSat = new MutableSlider(x + 41, y + 82, 100, 20, "Saturation: %s", sat, valueFormatter, slider -> {
			sat = (float)slider.getValue();
			commitChanges();
		}));

		this.addDrawableChild(sliderVal = new MutableSlider(x + 41, y + 105, 100, 20, "Value: %s", val, valueFormatter, slider -> {
			val = (float)slider.getValue();
			commitChanges();
		}));

		this.addDrawableChild(new ButtonWidget(x + 173, y + 90, 40, 20, new TranslatableText("Apply"), button -> {
			var passedData = new PacketByteBuf(Unpooled.buffer());
			passedData.writeNbt(getLightsaberTag().toTag());
			ClientPlayNetworking.send(SwgPackets.C2S.PacketLightsaberForgeApply, passedData);
		}));

		this.addDrawableChild(cbUnstable = new MutableCheckbox(x + 173, y + 65, 20, 20, new TranslatableText("Unstable"), false, true, mutableCheckbox -> commitChanges()));

		this.handler.addListener(this);
	}

	public void removed()
	{
		super.removed();
		this.handler.removeListener(this);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
	{
		if (this.getFocused() != null && this.isDragging() && button == 0 && this.getFocused().mouseDragged(mouseX, mouseY, button, deltaX, deltaY))
			return true;

		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	private void commitChanges()
	{
		LightsaberTag.mutate(lightsaber, (lt) ->
		{
			lt.owner = player.getEntityName();
			lt.bladeHue = hue;
			lt.bladeSaturation = sat;
			lt.bladeValue = val;
			lt.unstable = cbUnstable.isChecked();
		});
	}

	private void onLightsaberChanged()
	{
		if (lightsaber.getItem() instanceof LightsaberItem)
		{
			var lt = getLightsaberTag();

			hue = lt.bladeHue;
			sat = lt.bladeSaturation;
			val = lt.bladeValue;

			cbUnstable.setChecked(lt.unstable);
		}
		else
		{
			hue = 0;
			sat = 0;
			val = 0;
			cbUnstable.setChecked(false);
		}

		sliderHue.setValue(hue);
		sliderSat.setValue(sat);
		sliderVal.setValue(val);
	}

	private LightsaberTag getLightsaberTag()
	{
		return new LightsaberTag(lightsaber.getOrCreateNbt());
	}

	@Override
	public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack)
	{
		switch (slotId)
		{
			case 0 -> {
				lightsaber = stack.copy();
				onLightsaberChanged();
			}
		}
	}

	@Override
	public void onPropertyUpdate(ScreenHandler handler, int property, int value)
	{
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
	}

	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY)
	{
		var minecraft = MinecraftClient.getInstance();

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		var i = (this.width - this.backgroundWidth) / 2;
		var j = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);

		final var stencilX = 9;
		final var stencilY = 17;
		final var stencilWidth = 238;
		final var stencilHeight = 39;

		final var hiltLength = 60;

		matrices.push();
		matrices.translate(x + stencilX + hiltLength, y + stencilY + stencilHeight / 2f, 500);

		matrices.scale(-100, 100, 100);
		matrices.multiply(new Quaternion(0, 0, 90, true));
		matrices.multiply(new Quaternion(15, 0, 0, true));
		matrices.multiply(new Quaternion(0, -60, 0, true));

		var immediate = minecraft.getBufferBuilders().getEntityVertexConsumers();

		var rgb = ColorUtil.fromHSV(hue, sat, val);
		var b = rgb & 0xFF;
		var g = (rgb >> 8) & 0xFF;
		var r = (rgb >> 16) & 0xFF;

		if (lightsaber.getItem() instanceof LightsaberItem)
		{
			DiffuseLighting.disableGuiDepthLighting();
			DiffuseLighting.enableForLevel(matrices.peek().getPositionMatrix());

			LightsaberItemRenderer.INSTANCE.renderDirect(lightsaber, ModelTransformation.Mode.NONE, matrices, immediate, 0xFFFFFF, OverlayTexture.DEFAULT_UV, true);
			immediate.draw();

			DiffuseLighting.enableGuiDepthLighting();
		}

		matrices.pop();

		matrices.push();

		var x0 = x + 7;
		var x1 = x + 7 + 30;
		var y0 = y + 84;
		var y1 = y + 84 + 30;

		var u0 = 0;
		var u1 = 10;
		var v0 = 0;
		var v1 = 10;

		var z = 0;

		var bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float)x0, (float)y1, (float)z).color(r, g, b, 255).texture(u0, v1).next();
		bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float)x1, (float)y1, (float)z).color(r, g, b, 255).texture(u1, v1).next();
		bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float)x1, (float)y0, (float)z).color(r, g, b, 255).texture(u1, v0).next();
		bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float)x0, (float)y0, (float)z).color(r, g, b, 255).texture(u0, v0).next();
		bufferBuilder.end();

		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		BufferRenderer.draw(bufferBuilder);

		matrices.pop();
	}
}
