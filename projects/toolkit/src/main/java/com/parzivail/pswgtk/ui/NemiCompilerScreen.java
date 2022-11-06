package com.parzivail.pswgtk.ui;

import com.google.gson.Gson;
import com.parzivail.pswg.Resources;
import com.parzivail.pswgtk.ToolkitClient;
import com.parzivail.pswgtk.model.nemi.NemiModel;
import com.parzivail.pswgtk.screen.JComponentScreen;
import com.parzivail.pswgtk.swing.EventHelper;
import com.parzivail.pswgtk.util.DialogUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class NemiCompilerScreen extends JComponentScreen
{
	public static final String I18N_TOOLKIT_NEMI_COMPILER = Resources.screen("nemi_compiler");

	protected NemiCompilerScreen(Screen parent)
	{
		super(parent, Text.translatable(I18N_TOOLKIT_NEMI_COMPILER));
	}

	@Override
	protected JComponent getRootComponent()
	{
		return EventHelper.click(new JButton("Test"), this::click);
	}

	private void click(MouseEvent mouseEvent)
	{
		DialogUtil.openFile("Open Model", false, "*.nemi")
		          .ifPresent(NemiCompilerScreen::openModel);
	}

	private static void openModel(String path)
	{
		try (Reader reader = Files.newBufferedReader(Path.of(path)))
		{
			var gson = new Gson();
			var nemi = gson.fromJson(reader, NemiModel.class);
			ToolkitClient.LOG.info(nemi);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
