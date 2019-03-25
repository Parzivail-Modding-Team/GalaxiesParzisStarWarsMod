package com.parzivail.util.binary.ned;

import com.google.common.io.LittleEndianDataInputStream;
import com.google.gson.Gson;
import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.util.binary.PIO;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InvalidObjectException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class NedGraph
{
	public final int version;
	public final List<NedNode> nodes;

	public NedGraph(int version, List<NedNode> nodes)
	{
		this.version = version;
		this.nodes = nodes;
	}

	public static NedGraph LoadNedx(ResourceLocation filename)
	{
		try
		{
			InputStream fs = PIO.getResource(StarWarsGalaxy.class, filename);
			LittleEndianDataInputStream s = new LittleEndianDataInputStream(fs);

			byte[] identr = new byte[4];
			int read = s.read(identr);
			String ident = new String(identr);
			if (!"NEDX".equals(ident) || read != identr.length)
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
			return new NedGraph(version, nodes);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static NedGraph LoadJson(ResourceLocation filename)
	{
		try
		{
			InputStream fs = PIO.getResource(StarWarsGalaxy.class, filename);
			Reader r = new InputStreamReader(fs, StandardCharsets.UTF_8);
			Gson gson = new Gson();
			NedNode[] nodes = gson.fromJson(r, NedNode[].class);

			return new NedGraph(1, Arrays.asList(nodes));
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
			if (node.getType() == NodeType.Start)
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
