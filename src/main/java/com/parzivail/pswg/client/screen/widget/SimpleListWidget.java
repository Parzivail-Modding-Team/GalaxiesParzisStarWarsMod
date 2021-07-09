package com.parzivail.pswg.client.screen.widget;

//public class SimpleListWidget<T> extends AlwaysSelectedEntryListWidget.Entry<SimpleListWidget.Entry<T>>
//{
//	public static class Entry<T> extends EntryListWidget.Entry<com.parzivail.pswg.client.screen.widget.SimpleListWidget.Entry<T>>
//	{
//		private final SimpleListWidget<T> parent;
//		private final T value;
//
//		public Entry(SimpleListWidget<T> parent, T value)
//		{
//			this.parent = parent;
//			this.value = value;
//		}
//
//		public T getValue()
//		{
//			return value;
//		}
//
//		@Override
//		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta)
//		{
//			assert parent.client != null;
//			drawTextWithShadow(matrices, parent.client.textRenderer, parent.entryFormatter.apply(value), x, y, 0xFFFFFF);
//		}
//
//		@Override
//		public boolean mouseClicked(double mouseX, double mouseY, int button)
//		{
//			if (button == 0)
//			{
//				parent.setSelected(this);
//				return true;
//			}
//			return false;
//		}
//	}
//
//	private final Consumer<T> onSelectionChanged;
//
//	private Function<T, Text> entryFormatter = entry -> new TranslatableText(entry.toString());
//	private Function<List<com.parzivail.pswg.client.screen.widget.SimpleListWidget.Entry<T>>, com.parzivail.pswg.client.screen.widget.SimpleListWidget.Entry<T>> entrySelector = entries -> entries.get(0);
//
//	public SimpleListWidget(MinecraftClient minecraftClient, int x, int y, int width, int key, int itemHeight, Consumer<T> onSelectionChanged)
//	{
//		super(minecraftClient, width, key, y, y + key, itemHeight);
//		this.onSelectionChanged = onSelectionChanged;
//		this.setLeftPos(x);
//		this.setRenderBackground(false);
//		this.setRenderHorizontalShadows(false);
//	}
//
//	public void setEntryFormatter(Function<T, Text> entryFormatter)
//	{
//		this.entryFormatter = entryFormatter;
//	}
//
//	public void setEntrySelector(Function<List<com.parzivail.pswg.client.screen.widget.SimpleListWidget.Entry<T>>, com.parzivail.pswg.client.screen.widget.SimpleListWidget.Entry<T>> entrySelector)
//	{
//		this.entrySelector = entrySelector;
//	}
//
//	@Override
//	public int getRowWidth()
//	{
//		return width - 4;
//	}
//
//	@Override
//	protected int getScrollbarPositionX()
//	{
//		// Fix selecting not working correctly when the left position is modified
//		return this.left + this.width / 2 + 124;
//	}
//
//	@Override
//	public void setSelected(@Nullable SimpleListWidget.Entry<T> entry)
//	{
//		super.setSelected(entry);
//		onSelectionChanged.accept(entry == null ? null : entry.getValue());
//	}
//
//	public void clear()
//	{
//		setScrollAmount(0);
//		this.setSelected(null);
//		this.clearEntries();
//	}
//
//	public void setEntries(List<T> values)
//	{
//		clear();
//
//		for (T value : values)
//			addEntry(new com.parzivail.pswg.client.screen.widget.SimpleListWidget.Entry<>(this, value));
//
//		if (!children().isEmpty())
//			setSelected(entrySelector.apply(children()));
//	}
//
//	public void setEntries(T[] values)
//	{
//		clear();
//
//		for (T value : values)
//			addEntry(new com.parzivail.pswg.client.screen.widget.SimpleListWidget.Entry<>(this, value));
//
//		if (!children().isEmpty())
//			setSelected(entrySelector.apply(children()));
//	}
//
//	public List<com.parzivail.pswg.client.screen.widget.SimpleListWidget.Entry<T>> getEntries()
//	{
//		return children();
//	}
//
//	@Override
//	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
//	{
//		GL11.glEnable(GL11.GL_SCISSOR_TEST);
//
//		Window window = client.getWindow();
//		double scaleFactor = window.getScaleFactor();
//
//		// TODO: where'd the scrollbar go?
//		GL11.glScissor((int)(left * scaleFactor), window.getHeight() - (int)(bottom * scaleFactor), (int)((right - left) * scaleFactor), (int)((bottom - top) * scaleFactor));
//
//		super.render(matrices, mouseX, mouseY, delta);
//
//		GL11.glDisable(GL11.GL_SCISSOR_TEST);
//	}
//}
