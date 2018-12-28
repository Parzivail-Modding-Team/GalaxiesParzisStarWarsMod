package com.parzivail.util.render;

import com.parzivail.swg.proxy.Client;
import com.parzivail.util.common.Lumberjack;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.apache.commons.io.FileUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.IntBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AtlasUtils
{
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

	/**
	 * Saves the provided texture atlas as a file in the "atlases" directory
	 *
	 * @param map The texture atlas to save
	 */
	@SideOnly(Side.CLIENT)
	public static void saveTextureAtlas(TextureMap map)
	{
		try
		{
			int texId = ReflectionHelper.getPrivateValue(AbstractTexture.class, map, "glTextureId", "field_110553_a", "a");

			int width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
			int height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

			File file2 = new File(Client.mc.mcDataDir, "atlases");
			if (!file2.mkdir() && map.getTextureType() == 0)
				FileUtils.cleanDirectory(file2);

			int k = width * height;

			IntBuffer pixelBuffer = BufferUtils.createIntBuffer(k);
			int[] pixelValues = new int[k];

			GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
			GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
			pixelBuffer.clear();

			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
			GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);

			pixelBuffer.get(pixelValues);
			TextureUtil.func_147953_a(pixelValues, width, height);
			BufferedImage bufferedimage = null;

			bufferedimage = new BufferedImage(width, height, 1);
			bufferedimage.setRGB(0, 0, width, height, pixelValues, 0, width);

			File file3;

			file3 = getTimestampedPNGFileForDirectory(file2);

			ImageIO.write(bufferedimage, "png", file3);
		}
		catch (Exception exception)
		{
			Lumberjack.err("Failed to write texture atlas");
		}
	}

	private static File getTimestampedPNGFileForDirectory(File p_74290_0_)
	{
		String s = dateFormat.format(new Date());
		int i = 1;

		while (true)
		{
			File file2 = new File(p_74290_0_, s + (i == 1 ? "" : "_" + i) + ".png");

			if (!file2.exists())
			{
				return file2;
			}

			++i;
		}
	}
}
