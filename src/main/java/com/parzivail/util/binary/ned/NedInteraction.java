package com.parzivail.util.binary.ned;

import com.parzivail.swg.player.PswgExtProp;
import com.parzivail.util.common.Lumberjack;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

public class NedInteraction
{
	private final EntityPlayer player;
	private final NedGraph tree;
	public boolean done;
	public NedNode node;

	public NedInteraction(EntityPlayer player, NedGraph tree, NedNode root)
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
			Lumberjack.debug("Attempted to advance quest down null path");
			return null;
		}

		UUID nextCxn = node.outputs.get(pathIndex).connectId;
		return tree.getNodeById(nextCxn);
	}

	public void advance(int pathIndex)
	{
		if (node.outputs.get(pathIndex) == null)
		{
			Lumberjack.debug("Attempted to advance quest down null path");
			return;
		}

		UUID nextCxn = node.outputs.get(pathIndex).connectId;
		node = tree.getNodeById(nextCxn);

		switch (node.getType())
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
		Lumberjack.debug("triggerEvent %s", node.outputs.get(0).text);
	}

	private void completeQuest(NedNode node)
	{
		Lumberjack.debug("completeQuest %s", node.outputs.get(0).text);

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
		Lumberjack.debug("startQuest %s", node.outputs.get(0).text);

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
		PswgExtProp props = PswgExtProp.get(player);
		if (props != null)
			return props.hasQuest(node.outputs.get(0).text);
		return false;
	}

	private void clearFlag(NedNode node)
	{
		if (!player.worldObj.isRemote)
		{
			PswgExtProp props = PswgExtProp.get(player);
			if (props != null)
				props.clearFlag(node.outputs.get(0).text);
		}
	}

	private void setFlag(NedNode node)
	{
		if (!player.worldObj.isRemote)
		{
			PswgExtProp props = PswgExtProp.get(player);
			if (props != null)
				props.setFlag(node.outputs.get(0).text);
		}
	}

	private boolean hasFlag(NedNode node)
	{
		String flag = node.outputs.get(0).text;
		if (flag.startsWith("!"))
			return hasSpecialFlag(node.outputs.get(0));
		PswgExtProp props = PswgExtProp.get(player);
		if (props != null)
			return props.hasFlag(flag);
		return false;
	}

	private boolean hasSpecialFlag(NedConnection connection)
	{
		String flag = connection.text;
		if (connection.payload == null)
		{
			Lumberjack.debug("Attempted to check special flag with no payload");
			return false;
		}

		String[] extras = connection.payload;
		switch (flag)
		{
			case "!item":
			{
				boolean any = false;
				if (extras.length < 1)
					return false;
				if ("any".equals(extras[0]))
					any = true;
			}
		}
		return false;
	}
}
