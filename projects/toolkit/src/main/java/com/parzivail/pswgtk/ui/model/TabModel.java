package com.parzivail.pswgtk.ui.model;

import javax.swing.*;

public interface TabModel
{
	String getTitle();

	boolean tryClose();

	JComponent getContents();
}
