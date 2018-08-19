package com.parzivail.swg.registry;

import com.parzivail.swg.Resources;
import com.parzivail.util.binary.ned.NedX;

public class QuestRegister
{
	public static NedX testQuest;
	public static NedX complexQuest;

	public static void register()
	{
		testQuest = NedX.Load(Resources.location("quests/basic.nedx"));
		complexQuest = NedX.Load(Resources.location("quests/complexdialogue.nedx"));
	}
}
