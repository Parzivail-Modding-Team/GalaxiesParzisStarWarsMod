package com.parzivail.util.binary.ned;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class NedNode
{
	private static final HashMap<Integer, NodeType> types = new HashMap<>();

	static
	{
		types.put(-1, NodeType.None);
		types.put(0, NodeType.Start);
		types.put(1, NodeType.End);
		types.put(2, NodeType.NpcDialogue);
		types.put(3, NodeType.PlayerDialogue);
		types.put(4, NodeType.HasFlag);
		types.put(5, NodeType.SetFlag);
		types.put(6, NodeType.ClearFlag);
		types.put(7, NodeType.HasQuest);
		types.put(8, NodeType.StartQuest);
		types.put(9, NodeType.CompleteQuest);
		types.put(10, NodeType.TriggerEvent);
	}

	public final UUID id;
	public final NodeType type;
	public final ArrayList<NedConnection> outputs;

	public NedNode(UUID id, int type, ArrayList<NedConnection> outputs)
	{
		this.id = id;
		this.type = types.get(type);
		this.outputs = outputs;
	}
}
