package com.parzivail.pswg.client.render.player;

import com.parzivail.util.client.CameraUtil;
import com.parzivail.util.client.PositionNormal3f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import org.joml.Vector3f;

import java.util.HashMap;

public class PlayerSocket
{
	private static final HashMap<String, PositionNormal3f> SOCKETS = new HashMap<>();

	private static String getSocketId(PlayerEntity player, String socketName)
	{
		return player.getId() + "/" + socketName;
	}

	public static void save(PlayerEntity player, String socketName, MatrixStack matrices, Vector3f normal)
	{
		SOCKETS.put(getSocketId(player, socketName), CameraUtil.unproject(matrices, normal));
	}

	public static PositionNormal3f getSocket(PlayerEntity player, String socketName)
	{
		return SOCKETS.get(getSocketId(player, socketName));
	}
}
