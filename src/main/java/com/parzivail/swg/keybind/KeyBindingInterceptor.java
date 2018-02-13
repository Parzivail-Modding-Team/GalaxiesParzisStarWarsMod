package com.parzivail.swg.keybind;

/**
 * The purpose of this class is to intercept key presses (especially left and right mouse button clicks) and allow
 * greater flexibility in responding to them.
 * The class replaces KeyBindings in GameSettings.  When interception is on:
 * .isPressed() is overridden to return false so that the vanilla code never receives the clicks.
 * .pressed is always false.
 * The true .isPressed() and .pressed are available using .retrieveClick() and .isKeyDown()
 * Usage:
 * (1) replace KeyBinding with a newly generated interceptor
 * eg
 * KeyBindingInterceptor attackButtonInterceptor(GameSettings.keyBindAttack);
 * GameSettings.keyBindAttack = attackButtonInterceptor;
 * This creates an interceptor linked to the existing keyBindAttack.  The original keyBindAttack remains in the
 * KeyBinding hashmap and keyBindArray.
 * (2) Set the interception mode (eg true = on)
 * eg  setInterceptionActive(false);
 * (3) read the underlying clicks using .retrieveClick() or .isKeyDown();
 * (4) when Interceptor is no longer required, call .getOriginalKeyBinding();
 * eg GameSettings.keyBindAttack = attackButtonInterceptor.getOriginalKeyBinding();
 * <p>
 * NOTES -
 * (a) the interceptor does not update the .pressed field until .isPressed() is called.  The vanilla Minecraft.runTick
 * currently always accesses .isPressed() before attempting to read .pressed.
 * (b) In the current vanilla code, if the bindings are changed it will affect the original keybinding.  The new binding will
 * be copied to the interceptor at the first call to .retrieveClick(), .isKeyDown(), or .isPressed().
 * (c) Will not work in GUI
 * 7 Nov 2013: bugfix (.retrieveClick)
 */

import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class KeyBindingInterceptor extends KeyBinding
{
	private static final String[] REFL_PRESS_TIME = new String[] { "pressTime", "field_151474_i", "i" };
	private static final String[] REFL_PRESSED = new String[] { "pressed", "field_74513_e", "h" };
	private static final String[] REFL_KEYBINDARRAY = new String[] { "keybindArray", "field_74516_a", "a" };

	protected KeyBinding interceptedKeyBinding;
	private boolean interceptionActive;
	private int interceptedPressTime;

	/**
	 * Create an Interceptor based on an existing binding.
	 * The initial interception mode is OFF.
	 * If existingKeyBinding is already a KeyBindingInterceptor, a reinitialised copy will be created but no further effect.
	 *
	 * @param existingKeyBinding - the binding that will be intercepted.
	 */
	public KeyBindingInterceptor(KeyBinding existingKeyBinding)
	{
		super(existingKeyBinding.getKeyDescription(), existingKeyBinding.getKeyCode(), existingKeyBinding.getKeyCategory());
		// the base constructor automatically adds the class to the keybindArray and hash, which we don't want, so undo it

		ArrayList keyBindArray = ReflectionHelper.getPrivateValue(KeyBinding.class, this, REFL_KEYBINDARRAY);
		if (keyBindArray != null)
			keyBindArray.remove(this);

		this.interceptionActive = false;
		this.setPressed(false);
		this.setPressTime(0);
		this.interceptedPressTime = 0;

		if (existingKeyBinding instanceof KeyBindingInterceptor)
		{
			interceptedKeyBinding = ((KeyBindingInterceptor)existingKeyBinding).getOriginalKeyBinding();
		}
		else
		{
			interceptedKeyBinding = existingKeyBinding;
		}

		KeyBinding.resetKeyBindingArrayAndHash();
	}

	public void setInterceptionActive(boolean newMode)
	{
		if (newMode && !interceptionActive)
		{
			this.interceptedPressTime = 0;
		}
		interceptionActive = newMode;
	}

	public boolean getIsKeyPressed()
	{
		copyKeyCodeToOriginal();
		return interceptedKeyBinding.getIsKeyPressed();
	}

	/**
	 * @return returns false if interception isn't active.  Otherwise, retrieves one of the clicks (true) or false if no clicks left
	 */
	public boolean retrieveClick()
	{
		copyKeyCodeToOriginal();
		if (interceptionActive)
		{
			copyClickInfoFromOriginal();

			if (this.interceptedPressTime == 0)
			{
				return false;
			}
			else
			{
				--this.interceptedPressTime;
				return true;
			}
		}
		else
		{
			return false;
		}
	}

	/**
	 * A better name for this method would be retrieveClick.
	 * If interception is on, resets .pressed and .pressTime to zero.
	 * Otherwise, copies these from the intercepted KeyBinding.
	 *
	 * @return If interception is on, this will return false; Otherwise, it will pass on any clicks in the intercepted KeyBinding
	 */
	@Override
	public boolean isPressed()
	{
		copyKeyCodeToOriginal();
		copyClickInfoFromOriginal();

		if (interceptionActive)
		{
			this.setPressTime(0);
			this.setPressed(false);
			return false;
		}
		else
		{
			if (this.getPressTime() == 0)
			{
				return false;
			}
			else
			{
				this.setPressTime(this.getPressTime() - 1);
				return true;
			}
		}
	}

	private int getPressTime()
	{
		return getPressTime(this);
	}

	private void setPressTime(int n)
	{
		setPressTime(this, n);
	}

	private int getPressTime(KeyBinding k)
	{
		return ReflectionHelper.getPrivateValue(KeyBinding.class, k, REFL_PRESS_TIME);
	}

	private void setPressTime(KeyBinding k, int n)
	{
		ReflectionHelper.setPrivateValue(KeyBinding.class, k, n, REFL_PRESS_TIME);
	}

	private void setPressed(boolean n)
	{
		ReflectionHelper.setPrivateValue(KeyBinding.class, this, n, REFL_PRESSED);
	}

	public KeyBinding getOriginalKeyBinding()
	{
		return interceptedKeyBinding;
	}

	protected void copyClickInfoFromOriginal()
	{
		this.setPressTime(this.getPressTime() + getPressTime(interceptedKeyBinding));
		this.interceptedPressTime += getPressTime(interceptedKeyBinding);
		setPressTime(interceptedKeyBinding, 0);
		this.setPressed(interceptedKeyBinding.getIsKeyPressed());
	}

	protected void copyKeyCodeToOriginal()
	{
		// only copy if necessary
		if (this.getKeyCode() != interceptedKeyBinding.getKeyCode())
		{
			this.setKeyCode(interceptedKeyBinding.getKeyCode());
			resetKeyBindingArrayAndHash();
		}
	}

}