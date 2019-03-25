package com.parzivail.swg.registry;

import com.parzivail.swg.Resources;
import com.parzivail.util.binary.ned.NedGraph;

public class QuestRegister
{
	public static NedGraph complexQuest;

	public static void register()
	{
		complexQuest = NedGraph.LoadJson(Resources.location("quests/testquest.nedj"));
	}
}
