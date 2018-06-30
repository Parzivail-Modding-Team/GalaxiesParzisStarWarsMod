package com.parzivail.util.ui.maui;

import com.parzivail.swg.Resources;
import com.parzivail.swg.proxy.Client;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureImpl;

import java.util.function.Consumer;

public class MauiTextbox extends Gui
{
	private static final NinePatchResource texDefault = new NinePatchResource(Resources.location("textures/maui/textbox/default.png"), 7, 21, 3, 3, 1, 14);
	private static final NinePatchResource texHover = new NinePatchResource(Resources.location("textures/maui/textbox/hover.png"), 16, 23, 3, 3, 1, 14);
	//	private static final HashMap<MauiConnectedState, NinePatchResource[]> connectedStateMap = new HashMap<>();
	//
	//	static
	//	{
	//		connectedStateMap.put(MauiConnectedState.None, new NinePatchResource[] {
	//				texDefault, texHover, texActive
	//		});
	//		connectedStateMap.put(MauiConnectedState.Left, new NinePatchResource[] {
	//				texLeftDefault, texLeftHover, texLeftActive
	//		});
	//		connectedStateMap.put(MauiConnectedState.Right, new NinePatchResource[] {
	//				texRightDefault, texRightHover, texRightActive
	//		});
	//		connectedStateMap.put(MauiConnectedState.Both, new NinePatchResource[] {
	//				texBothDefault, texBothHover, texBothActive
	//		});
	//	}
	//
	//	public MauiConnectedState connectedState = MauiConnectedState.None;

	public int xPosition;
	public int yPosition;
	/**
	 * The width of this text field.
	 */
	public int width;
	public int height;
	public Consumer<MauiTextbox> onTextChange;
	/**
	 * Has the current text being edited on the textbox.
	 */
	private String text = "";
	private int maxStringLength = 32;
	private int cursorCounter;
	private boolean enableBackgroundDrawing = true;
	/**
	 * if true the textbox can lose focus by clicking elsewhere on the screen
	 */
	private boolean canLoseFocus = true;
	/**
	 * If this value is true along with isEnabled, keyTyped will process the keys.
	 */
	private boolean isFocused;
	/**
	 * If this value is true along with isFocused, keyTyped will process the keys.
	 */
	private boolean isEnabled = true;
	/**
	 * The current character index that should be used as start of the rendered text.
	 */
	private int lineScrollOffset;
	private int cursorPosition;
	/**
	 * other selection position, maybe the same as the cursor
	 */
	private int selectionEnd;
	private int enabledColor = 14737632;
	private int disabledColor = 7368816;
	/**
	 * True if this textbox is visible
	 */
	private boolean visible = true;

	public MauiTextbox(int x, int y, int w, int h)
	{
		xPosition = x;
		yPosition = y;
		width = w;
		height = h;
		onTextChange = (dummy) -> {
		};
	}

	//	@Override
	//	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	//	{
	//		ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
	//
	//		//NinePatchResource[] states = connectedStateMap.get(connectedState);
	//
	//		GL.PushAttrib(AttribMask.EnableBit);
	//		GL.Disable(EnableCap.CullFace);
	//		GL.PushMatrix();
	//		GL.Color(GLPalette.WHITE);
	//		GL.Translate(xPosition, yPosition, 0);
	//		boolean hover = Fx.Util.RectangleIntersects(xPosition, yPosition, width, height, mouseX, mouseY);
	//		boolean active = hover && Mouse.isButtonDown(0);
	//		NinePatchResource texture = active ? states[2] : hover ? states[1] : states[0];
	//		texture.draw(width, height);
	//		float width = Maui.brandonReg.getWidth(this.);
	//		GL.Scale(1f / sr.getScaleFactor());
	//		GL.Translate(Math.round((this.width * sr.getScaleFactor() - width) / 2f), Math.round((this.height * sr.getScaleFactor() - Maui.brandonReg.getHeight()) / 2f), 0);
	//		TextureImpl.bindNone();
	//		Maui.brandonReg.drawString(0, 0, this.displayString, org.newdawn.slick.Color.black);
	//		GL.PopMatrix();
	//		GL.PopAttrib();
	//	}

	/**
	 * Increments the cursor counter
	 */
	public void updateCursorCounter()
	{
		++cursorCounter;
	}

	/**
	 * Returns the contents of the textbox
	 */
	public String getText()
	{
		return text;
	}

	/**
	 * Sets the text of the textbox
	 */
	public void setText(String p_146180_1_)
	{
		if (p_146180_1_.length() > maxStringLength)
		{
			text = p_146180_1_.substring(0, maxStringLength);
		}
		else
		{
			text = p_146180_1_;
		}

		setCursorPositionEnd();
	}

	/**
	 * returns the text between the cursor and selectionEnd
	 */
	public String getSelectedText()
	{
		int i = cursorPosition < selectionEnd ? cursorPosition : selectionEnd;
		int j = cursorPosition < selectionEnd ? selectionEnd : cursorPosition;
		return text.substring(i, j);
	}

	/**
	 * replaces selected text, or inserts text at the position on the cursor
	 */
	public void writeText(String p_146191_1_)
	{
		String s1 = "";
		String s2 = ChatAllowedCharacters.filterAllowedCharacters(p_146191_1_);
		int i = cursorPosition < selectionEnd ? cursorPosition : selectionEnd;
		int j = cursorPosition < selectionEnd ? selectionEnd : cursorPosition;
		int k = maxStringLength - text.length() - (i - selectionEnd);
		boolean flag = false;

		if (text.length() > 0)
		{
			s1 = s1 + text.substring(0, i);
		}

		int l;

		if (k < s2.length())
		{
			s1 = s1 + s2.substring(0, k);
			l = k;
		}
		else
		{
			s1 = s1 + s2;
			l = s2.length();
		}

		if (text.length() > 0 && j < text.length())
		{
			s1 = s1 + text.substring(j);
		}

		text = s1;
		moveCursorBy(i - selectionEnd + l);
	}

	/**
	 * Deletes the specified number of words starting at the cursor position. Negative numbers will delete words left of
	 * the cursor.
	 */
	public void deleteWords(int p_146177_1_)
	{
		if (text.length() != 0)
		{
			if (selectionEnd != cursorPosition)
			{
				writeText("");
			}
			else
			{
				deleteFromCursor(getNthWordFromCursor(p_146177_1_) - cursorPosition);
			}
		}
	}

	/**
	 * delete the selected text, otherwsie deletes characters from either side of the cursor. params: delete num
	 */
	public void deleteFromCursor(int p_146175_1_)
	{
		if (text.length() != 0)
		{
			if (selectionEnd != cursorPosition)
			{
				writeText("");
			}
			else
			{
				boolean flag = p_146175_1_ < 0;
				int j = flag ? cursorPosition + p_146175_1_ : cursorPosition;
				int k = flag ? cursorPosition : cursorPosition + p_146175_1_;
				String s = "";

				if (j >= 0)
				{
					s = text.substring(0, j);
				}

				if (k < text.length())
				{
					s = s + text.substring(k);
				}

				text = s;

				if (flag)
				{
					moveCursorBy(p_146175_1_);
				}
			}
		}
	}

	/**
	 * see @getNthNextWordFromPos() params: N, position
	 */
	public int getNthWordFromCursor(int p_146187_1_)
	{
		return getNthWordFromPos(p_146187_1_, getCursorPosition());
	}

	/**
	 * gets the position of the nth word. N may be negative, then it looks backwards. params: N, position
	 */
	public int getNthWordFromPos(int p_146183_1_, int p_146183_2_)
	{
		return func_146197_a(p_146183_1_, getCursorPosition(), true);
	}

	public int func_146197_a(int p_146197_1_, int p_146197_2_, boolean p_146197_3_)
	{
		int k = p_146197_2_;
		boolean flag1 = p_146197_1_ < 0;
		int l = Math.abs(p_146197_1_);

		for (int i1 = 0; i1 < l; ++i1)
		{
			if (flag1)
			{
				while (p_146197_3_ && k > 0 && text.charAt(k - 1) == 32)
				{
					--k;
				}

				while (k > 0 && text.charAt(k - 1) != 32)
				{
					--k;
				}
			}
			else
			{
				int j1 = text.length();
				k = text.indexOf(32, k);

				if (k == -1)
				{
					k = j1;
				}
				else
				{
					while (p_146197_3_ && k < j1 && text.charAt(k) == 32)
					{
						++k;
					}
				}
			}
		}

		return k;
	}

	/**
	 * Moves the text cursor by a specified number of characters and clears the selection
	 */
	public void moveCursorBy(int p_146182_1_)
	{
		setCursorPosition(selectionEnd + p_146182_1_);
	}

	/**
	 * sets the cursors position to the beginning
	 */
	public void setCursorPositionZero()
	{
		setCursorPosition(0);
	}

	/**
	 * sets the cursors position to after the text
	 */
	public void setCursorPositionEnd()
	{
		setCursorPosition(text.length());
	}

	/**
	 * Call this method from your GuiScreen to process the keys into the textbox
	 */
	public boolean textboxKeyTyped(char p_146201_1_, int p_146201_2_)
	{
		if (!isFocused)
		{
			return false;
		}
		else
		{
			switch (p_146201_1_)
			{
				case 1:
					setCursorPositionEnd();
					setSelectionPos(0);
					return true;
				case 3:
					GuiScreen.setClipboardString(getSelectedText());
					return true;
				case 22:
					if (isEnabled)
					{
						writeText(GuiScreen.getClipboardString());
					}

					return true;
				case 24:
					GuiScreen.setClipboardString(getSelectedText());

					if (isEnabled)
					{
						writeText("");
					}

					return true;
				default:
					switch (p_146201_2_)
					{
						case 14:
							if (GuiScreen.isCtrlKeyDown())
							{
								if (isEnabled)
								{
									deleteWords(-1);
								}
							}
							else if (isEnabled)
							{
								deleteFromCursor(-1);
							}

							return true;
						case 199:
							if (GuiScreen.isShiftKeyDown())
							{
								setSelectionPos(0);
							}
							else
							{
								setCursorPositionZero();
							}

							return true;
						case 203:
							if (GuiScreen.isShiftKeyDown())
							{
								if (GuiScreen.isCtrlKeyDown())
								{
									setSelectionPos(getNthWordFromPos(-1, getSelectionEnd()));
								}
								else
								{
									setSelectionPos(getSelectionEnd() - 1);
								}
							}
							else if (GuiScreen.isCtrlKeyDown())
							{
								setCursorPosition(getNthWordFromCursor(-1));
							}
							else
							{
								moveCursorBy(-1);
							}

							return true;
						case 205:
							if (GuiScreen.isShiftKeyDown())
							{
								if (GuiScreen.isCtrlKeyDown())
								{
									setSelectionPos(getNthWordFromPos(1, getSelectionEnd()));
								}
								else
								{
									setSelectionPos(getSelectionEnd() + 1);
								}
							}
							else if (GuiScreen.isCtrlKeyDown())
							{
								setCursorPosition(getNthWordFromCursor(1));
							}
							else
							{
								moveCursorBy(1);
							}

							return true;
						case 207:
							if (GuiScreen.isShiftKeyDown())
							{
								setSelectionPos(text.length());
							}
							else
							{
								setCursorPositionEnd();
							}

							return true;
						case 211:
							if (GuiScreen.isCtrlKeyDown())
							{
								if (isEnabled)
								{
									deleteWords(1);
								}
							}
							else if (isEnabled)
							{
								deleteFromCursor(1);
							}

							return true;
						default:
							if (ChatAllowedCharacters.isAllowedCharacter(p_146201_1_))
							{
								if (isEnabled)
								{
									writeText(Character.toString(p_146201_1_));
								}

								return true;
							}
							else
							{
								return false;
							}
					}
			}
		}
	}

	/**
	 * Args: x, y, buttonClicked
	 */
	public void mouseClicked(int p_146192_1_, int p_146192_2_, int p_146192_3_)
	{
		boolean flag = p_146192_1_ >= xPosition && p_146192_1_ < xPosition + width && p_146192_2_ >= yPosition && p_146192_2_ < yPosition + height;

		if (canLoseFocus)
		{
			setFocused(flag);
		}

		if (isFocused && p_146192_3_ == 0)
		{
			int l = p_146192_1_ - xPosition;

			if (enableBackgroundDrawing)
			{
				l -= 4;
			}

			String s = trimStringToWidth(text.substring(lineScrollOffset), getWidth());
			setCursorPosition(trimStringToWidth(s, l).length() + lineScrollOffset);
		}
	}

	/**
	 * Draws the textbox
	 */
	public void drawTextBox()
	{
		if (getVisible())
		{
			if (getEnableBackgroundDrawing())
			{
				Gui.drawRect(xPosition - 1, yPosition - 1, xPosition + width + 1, yPosition + height + 1, -6250336);
				Gui.drawRect(xPosition, yPosition, xPosition + width, yPosition + height, -16777216);
			}

			int i = isEnabled ? enabledColor : disabledColor;
			int j = cursorPosition - lineScrollOffset;
			int k = selectionEnd - lineScrollOffset;
			String s = trimStringToWidth(text.substring(lineScrollOffset), getWidth());
			boolean flag = j >= 0 && j <= s.length();
			boolean flag1 = isFocused && cursorCounter / 6 % 2 == 0 && flag;
			int l = enableBackgroundDrawing ? xPosition + 4 : xPosition;
			int i1 = enableBackgroundDrawing ? yPosition + (height - 8) / 2 : yPosition;
			int j1 = l;

			if (k > s.length())
			{
				k = s.length();
			}

			if (s.length() > 0)
			{
				String s1 = flag ? s.substring(0, j) : s;
				j1 = drawStringWithShadow(s1, l, i1, i);
			}

			boolean flag2 = cursorPosition < text.length() || text.length() >= getMaxStringLength();
			int k1 = j1;

			if (!flag)
			{
				k1 = j > 0 ? l + width : l;
			}
			else if (flag2)
			{
				k1 = j1 - 1;
				--j1;
			}

			if (s.length() > 0 && flag && j < s.length())
			{
				drawStringWithShadow(s.substring(j), j1, i1, i);
			}

			if (flag1)
			{
				if (flag2)
				{
					Gui.drawRect(k1, i1 - 1, k1 + 1, i1 + 1 + Maui.deJaVuSans.getHeight(), -3092272);
				}
				else
				{
					drawStringWithShadow("_", k1, i1, i);
				}
			}

			if (k != j)
			{
				int l1 = l + Maui.deJaVuSans.getWidth(s.substring(0, k));
				drawCursorVertical(k1, i1 - 1, l1 - 1, i1 + 1 + Maui.deJaVuSans.getHeight());
			}
		}
	}

	private String trimStringToWidth(String str, int width, boolean reverse)
	{
		StringBuilder sb = new StringBuilder(str);
		if (reverse)
			sb = sb.reverse();

		while (Maui.deJaVuSans.getWidth(sb.toString()) > width)
			sb.deleteCharAt(sb.length() - 1);

		return sb.toString();
	}

	private String trimStringToWidth(String str, int width)
	{
		return trimStringToWidth(str, width, false);
	}

	private int drawStringWithShadow(String str, int x, int y, int color)
	{
		ScaledResolution sr = Client.resolution;
		float scale = sr.getScaleFactor();

		GL.PushAttrib(AttribMask.EnableBit);
		GL.Disable(EnableCap.CullFace);
		GL.PushMatrix();
		GL.Translate(x, y, 0);
		GL.Scale(1f / scale);
		TextureImpl.bindNone();
		Maui.deJaVuSans.drawString(0, 0, str, new org.newdawn.slick.Color(color));
		GL.PopMatrix();
		GL.PopAttrib();
		GL.Disable(EnableCap.Texture2D);

		return Maui.deJaVuSans.getWidth(str);
	}

	/**
	 * draws the vertical line cursor in the textbox
	 */
	private void drawCursorVertical(int p_146188_1_, int p_146188_2_, int p_146188_3_, int p_146188_4_)
	{
		int i1;

		if (p_146188_1_ < p_146188_3_)
		{
			i1 = p_146188_1_;
			p_146188_1_ = p_146188_3_;
			p_146188_3_ = i1;
		}

		if (p_146188_2_ < p_146188_4_)
		{
			i1 = p_146188_2_;
			p_146188_2_ = p_146188_4_;
			p_146188_4_ = i1;
		}

		if (p_146188_3_ > xPosition + width)
		{
			p_146188_3_ = xPosition + width;
		}

		if (p_146188_1_ > xPosition + width)
		{
			p_146188_1_ = xPosition + width;
		}

		Tessellator tessellator = Tessellator.instance;
		GL11.glColor4f(0.0F, 0.0F, 255.0F, 255.0F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
		GL11.glLogicOp(GL11.GL_OR_REVERSE);
		tessellator.startDrawingQuads();
		tessellator.addVertex((double)p_146188_1_, (double)p_146188_4_, 0.0D);
		tessellator.addVertex((double)p_146188_3_, (double)p_146188_4_, 0.0D);
		tessellator.addVertex((double)p_146188_3_, (double)p_146188_2_, 0.0D);
		tessellator.addVertex((double)p_146188_1_, (double)p_146188_2_, 0.0D);
		tessellator.draw();
		GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	/**
	 * returns the maximum number of character that can be contained in this textbox
	 */
	public int getMaxStringLength()
	{
		return maxStringLength;
	}

	public void setMaxStringLength(int p_146203_1_)
	{
		maxStringLength = p_146203_1_;

		if (text.length() > p_146203_1_)
		{
			text = text.substring(0, p_146203_1_);
		}
	}

	/**
	 * returns the current position of the cursor
	 */
	public int getCursorPosition()
	{
		return cursorPosition;
	}

	/**
	 * sets the position of the cursor to the provided index
	 */
	public void setCursorPosition(int p_146190_1_)
	{
		cursorPosition = p_146190_1_;
		int j = text.length();

		if (cursorPosition < 0)
		{
			cursorPosition = 0;
		}

		if (cursorPosition > j)
		{
			cursorPosition = j;
		}

		setSelectionPos(cursorPosition);
	}

	/**
	 * get enable drawing background and outline
	 */
	public boolean getEnableBackgroundDrawing()
	{
		return enableBackgroundDrawing;
	}

	/**
	 * enable drawing background and outline
	 */
	public void setEnableBackgroundDrawing(boolean p_146185_1_)
	{
		enableBackgroundDrawing = p_146185_1_;
	}

	/**
	 * Sets the text colour for this textbox (disabled text will not use this colour)
	 */
	public void setTextColor(int p_146193_1_)
	{
		enabledColor = p_146193_1_;
	}

	public void setDisabledTextColour(int p_146204_1_)
	{
		disabledColor = p_146204_1_;
	}

	/**
	 * Getter for the focused field
	 */
	public boolean isFocused()
	{
		return isFocused;
	}

	/**
	 * Sets focus to this gui element
	 */
	public void setFocused(boolean p_146195_1_)
	{
		if (p_146195_1_ && !isFocused)
		{
			cursorCounter = 0;
		}

		isFocused = p_146195_1_;
	}

	public void setEnabled(boolean p_146184_1_)
	{
		isEnabled = p_146184_1_;
	}

	/**
	 * the side of the selection that is not the cursor, may be the same as the cursor
	 */
	public int getSelectionEnd()
	{
		return selectionEnd;
	}

	/**
	 * returns the width of the textbox depending on if background drawing is enabled
	 */
	public int getWidth()
	{
		return getEnableBackgroundDrawing() ? width - 8 : width;
	}

	/**
	 * Sets the position of the selection anchor (i.e. position the selection was started at)
	 */
	public void setSelectionPos(int p_146199_1_)
	{
		int j = text.length();

		if (p_146199_1_ > j)
		{
			p_146199_1_ = j;
		}

		if (p_146199_1_ < 0)
		{
			p_146199_1_ = 0;
		}

		selectionEnd = p_146199_1_;

		if (lineScrollOffset > j)
		{
			lineScrollOffset = j;
		}

		int k = getWidth();
		String s = trimStringToWidth(text.substring(lineScrollOffset), k);
		int l = s.length() + lineScrollOffset;

		if (p_146199_1_ == lineScrollOffset)
		{
			lineScrollOffset -= trimStringToWidth(text, k).length();
		}

		if (p_146199_1_ > l)
		{
			lineScrollOffset += p_146199_1_ - l;
		}
		else if (p_146199_1_ <= lineScrollOffset)
		{
			lineScrollOffset -= lineScrollOffset - p_146199_1_;
		}

		if (lineScrollOffset < 0)
		{
			lineScrollOffset = 0;
		}

		if (lineScrollOffset > j)
		{
			lineScrollOffset = j;
		}
	}

	/**
	 * if true the textbox can lose focus by clicking elsewhere on the screen
	 */
	public void setCanLoseFocus(boolean p_146205_1_)
	{
		canLoseFocus = p_146205_1_;
	}

	/**
	 * returns true if this textbox is visible
	 */
	public boolean getVisible()
	{
		return visible;
	}

	/**
	 * Sets whether or not this textbox is visible
	 */
	public void setVisible(boolean p_146189_1_)
	{
		visible = p_146189_1_;
	}
}
