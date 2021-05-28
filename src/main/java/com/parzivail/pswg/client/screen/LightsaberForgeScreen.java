package com.parzivail.pswg.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Client;
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
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
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

	private static final Identifier TEXTURE = Resources.identifier("textures/gui/container/lightsaber_forge.png");

	private MutableSlider sR;
	private MutableSlider sG;
	private MutableSlider sB;
	private MutableCheckbox cbUnstable;
	private ButtonWidget bBladeColor;
	private ButtonWidget bCoreColor;

	private int r;
	private int g;
	private int b;

	private ItemStack lightsaber = ItemStack.EMPTY;

	public LightsaberForgeScreen(LightsaberForgeScreenHandler handler, PlayerInventory inventory, Text title)
	{
		super(handler, inventory, title);
		backgroundWidth = 256;
		backgroundHeight = 241;
	}

	protected void init()
	{
		super.init();

		this.playerInventoryTitleX = 48;
		this.playerInventoryTitleY = this.backgroundHeight - 94;
		this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;

		Function<Double, String> valueFormatter = value -> String.format("%s", (int)Math.round(value * 255));

		this.addDrawableChild(sR = new MutableSlider(x + 41, y + 59, 100, 20, "R: %s", r / 255f, valueFormatter, slider -> {
			r = (int)Math.round(slider.getValue() * 255);
			commitChanges();
		}));
		this.addDrawableChild(sG = new MutableSlider(x + 41, y + 79, 100, 20, "G: %s", g / 255f, valueFormatter, slider -> {
			g = (int)Math.round(slider.getValue() * 255);
			commitChanges();
		}));
		this.addDrawableChild(sB = new MutableSlider(x + 41, y + 99, 100, 20, "B: %s", b / 255f, valueFormatter, slider -> {
			b = (int)Math.round(slider.getValue() * 255);
			commitChanges();
		}));

		this.addDrawableChild(new ButtonWidget(x + this.backgroundWidth / 2 - 20, y + 124, 40, 20, new TranslatableText("Apply"), button -> {
			var passedData = new PacketByteBuf(Unpooled.buffer());
			passedData.writeNbt(getLightsaberTag().toTag());
			ClientPlayNetworking.send(SwgPackets.C2S.PacketLightsaberForgeApply, passedData);
		}));

		this.addDrawableChild(bBladeColor = new ButtonWidget(x + 8, y + 119, 30, 20, new TranslatableText("Blade"), button -> {
			button.active = false;
			bCoreColor.active = true;
			onLightsaberChanged();
		}));

		this.addDrawableChild(bCoreColor = new ButtonWidget(x + 38, y + 119, 30, 20, new TranslatableText("Core"), button -> {
			button.active = false;
			bBladeColor.active = true;
			onLightsaberChanged();
		}));

		bBladeColor.active = false;

		this.addDrawableChild(cbUnstable = new MutableCheckbox(x + 173, y + 65, 20, 20, new TranslatableText("Unstable"), false, true, mutableCheckbox -> {
			commitChanges();
		}));

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
			if (!bBladeColor.active)
				lt.bladeColor = ColorUtil.packRgb(r, g, b);
			else if (!bCoreColor.active)
				lt.coreColor = ColorUtil.packRgb(r, g, b);

			lt.unstable = cbUnstable.isChecked();
		});
	}

	private void onLightsaberChanged()
	{
		if (lightsaber.getItem() instanceof LightsaberItem)
		{
			var lt = getLightsaberTag();

			var color = 0;

			if (!bBladeColor.active)
				color = lt.bladeColor;
			else if (!bCoreColor.active)
				color = lt.coreColor;

			r = (color & 0xFF0000) >> 16;
			g = (color & 0xFF00) >> 8;
			b = (color & 0xFF);

			cbUnstable.setChecked(lt.unstable);
		}
		else
		{
			r = 0;
			g = 0;
			b = 0;

			cbUnstable.setChecked(false);
		}

		sR.setValue(r / 255f);
		sG.setValue(g / 255f);
		sB.setValue(b / 255f);
	}

	private LightsaberTag getLightsaberTag()
	{
		return new LightsaberTag(lightsaber.getOrCreateTag());
	}

	@Override
	public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack)
	{
		switch (slotId)
		{
			case 0:
			{
				lightsaber = stack.copy();
				onLightsaberChanged();
				break;
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
		this.renderBackground(matrices);

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

		final var hiltLength = 70;

		matrices.push();
		matrices.translate(x + stencilX + hiltLength, y + stencilY + stencilHeight / 2f, 100);

		matrices.multiply(new Quaternion(0, 0, 90, true));
		matrices.multiply(new Quaternion(0, 135, 0, true));
		matrices.scale(100, -100, 100);

		var immediate = Client.minecraft.getBufferBuilders().getEntityVertexConsumers();

		if (lightsaber.getItem() instanceof LightsaberItem)
		{
			LightsaberItemRenderer.INSTANCE.renderDirect(lightsaber, ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND, matrices, immediate, 0xf000f0, 0xFFFFFF, true);
			immediate.draw();
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
		bufferBuilder.vertex(matrices.peek().getModel(), (float)x0, (float)y1, (float)z).color(r, g, b, 255).texture(u0, v1).next();
		bufferBuilder.vertex(matrices.peek().getModel(), (float)x1, (float)y1, (float)z).color(r, g, b, 255).texture(u1, v1).next();
		bufferBuilder.vertex(matrices.peek().getModel(), (float)x1, (float)y0, (float)z).color(r, g, b, 255).texture(u1, v0).next();
		bufferBuilder.vertex(matrices.peek().getModel(), (float)x0, (float)y0, (float)z).color(r, g, b, 255).texture(u0, v0).next();
		bufferBuilder.end();
		RenderSystem.disableTexture();
		BufferRenderer.draw(bufferBuilder);
		RenderSystem.enableTexture();

		matrices.pop();
	}
}
