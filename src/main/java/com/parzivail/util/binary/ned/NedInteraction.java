package com.parzivail.util.binary.ned;

import com.parzivail.swg.player.PswgExtProp;
import com.parzivail.util.common.Lumberjack;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

public class NedInteraction
{
	private final EntityPlayer player;
	private final NedX tree;
	public boolean done;
	public NedNode node;

	public NedInteraction(EntityPlayer player, NedX tree, NedNode root)
	{
		this.player = player;
		this.tree = tree;
		node = root;
		advance(0);
	}

	public NedNode getNextNode(int pathIndex)
	{
		if (node.outputs.get(pathIndex) == null)
		{
			Lumberjack.log("Attempted to advance quest down null path");
			return null;
		}

		UUID nextCxn = node.outputs.get(pathIndex).connectedId;
		return tree.getNodeById(nextCxn);
	}

	public void advance(int pathIndex)
	{
		if (node.outputs.get(pathIndex) == null)
		{
			Lumberjack.log("Attempted to advance quest down null path");
			return;
		}

		UUID nextCxn = node.outputs.get(pathIndex).connectedId;
		node = tree.getNodeById(nextCxn);

		switch (node.type)
		{
			case None:
			case Start:
			case End:
				done = true;
				break;
			case NpcDialogue:
			case PlayerDialogue:
				break;
			case HasFlag:
				if (hasFlag(node))
					advance(0);
				else
					advance(1);
				break;
			case SetFlag:
				setFlag(node);
				advance(0);
				break;
			case ClearFlag:
				clearFlag(node);
				advance(0);
				break;
			case HasQuest:
				if (hasQuest(node))
					advance(0);
				else
					advance(1);
				break;
			case StartQuest:
				startQuest(node);
				advance(0);
				break;
			case CompleteQuest:
				completeQuest(node);
				advance(0);
				break;
			case TriggerEvent:
				triggerEvent(node);
				advance(0);
				break;
		}
	}

	private void triggerEvent(NedNode node)
	{
		Lumberjack.log("triggerEvent %s", node.outputs.get(0).text);
	}

	private void completeQuest(NedNode node)
	{
		Lumberjack.log("completeQuest %s", node.outputs.get(0).text);

		if (!player.worldObj.isRemote)
		{
			PswgExtProp props = PswgExtProp.get(player);
			if (props != null)
				props.completeQuest(node.outputs.get(0).text);
		}
		else
		{
			// show "achievement get" thing for completing quest
		}
	}

	private void startQuest(NedNode node)
	{
		Lumberjack.log("startQuest %s", node.outputs.get(0).text);

		if (!player.worldObj.isRemote)
		{
			PswgExtProp props = PswgExtProp.get(player);
			if (props != null)
				props.startQuest(node.outputs.get(0).text);
		}
		else
		{
			// show "achievement get" thing for starting quest
		}
	}

	private boolean hasQuest(NedNode node)
	{
		return false;
	}

	private void clearFlag(NedNode node)
	{
		Lumberjack.log("clearFlag %s", node.outputs.get(0).text);
	}

	private void setFlag(NedNode node)
	{
		Lumberjack.log("setFlag %s", node.outputs.get(0).text);
	}

	private boolean hasFlag(NedNode node)
	{
		return false;
	}
}
