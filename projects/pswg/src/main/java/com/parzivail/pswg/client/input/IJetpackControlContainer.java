package com.parzivail.pswg.client.input;

import java.util.EnumSet;

public interface IJetpackControlContainer
{
	void pswg_setJetpackControls(EnumSet<JetpackControls> controls);

	EnumSet<JetpackControls> pswg_getJetpackControls();
}
