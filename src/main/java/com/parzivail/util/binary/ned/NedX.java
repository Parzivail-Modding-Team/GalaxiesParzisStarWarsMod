package com.parzivail.util.binary.ned;

import com.google.common.io.LittleEndianDataInputStream;
import com.parzivail.util.binary.PIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import java.io.InputStream;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.UUID;

public class NedX
{
	public final int version;
	public final ArrayList<NedNode> nodes;

	public NedX(int version, ArrayList<NedNode> nodes)
	{
		this.version = version;
		this.nodes = nodes;
	}

	public static NedX Load(ResourceLocation filename)
	{
		try
		{
			IResource res = Minecraft.getMinecraft().getResourceManager().getResource(filename);
			InputStream fs = res.getInputStream();
			LittleEndianDataInputStream s = new LittleEndianDataInputStream(fs);

			byte[] identr = new byte[4];
			int read = s.read(identr);
			String ident = new String(identr);
			if (!ident.equals("NEDX") || read != identr.length)
				throw new InvalidObjectException("Input file not NEDX quest");

			int version = s.readInt();

			int numNodes = s.readInt();

			ArrayList<NedNode> nodes = new ArrayList<>();
			for (int i = 0; i < numNodes; i++)
			{
				UUID id = PIO.readGuid(s);

				int type = s.readInt();
				int numOutputs = s.readInt();

				ArrayList<NedConnection> outputs = new ArrayList<>();
				for (int j = 0; j < numOutputs; j++)
				{
					String text = PIO.readLengthPrefixedString(s);
					boolean hasConnectingNode = s.readBoolean();
					UUID connectedId = null;
					if (hasConnectingNode)
						connectedId = PIO.readGuid(s);

					outputs.add(new NedConnection(text, connectedId));
				}

				nodes.add(new NedNode(id, type, outputs));
			}

			s.close();
			fs.close();
			return new NedX(version, nodes);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public NedInteraction createInteraction(EntityPlayer player)
	{
		return new NedInteraction(player, this, getStartNode());
	}

	public NedNode getStartNode()
	{
		for (NedNode node : nodes)
			if (node.type.equals(NodeType.Start))
				return node;
		return null;
	}

	public NedNode getNodeById(UUID id)
	{
		for (NedNode node : nodes)
			if (node.id.equals(id))
				return node;
		return null;
	}
}
