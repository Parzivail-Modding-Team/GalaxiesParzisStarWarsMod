package com.parzivail.pswgtk.ui;

import com.google.gson.Gson;
import com.parzivail.pswg.Resources;
import com.parzivail.pswgtk.model.nemi.NemiModel;
import com.parzivail.pswgtk.screen.JComponentScreen;
import com.parzivail.pswgtk.swing.EventHelper;
import com.parzivail.pswgtk.util.DialogUtil;
import com.parzivail.pswgtk.util.FileUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.NbtIo;
import net.minecraft.text.Text;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class NemiCompilerScreen extends JComponentScreen
{
	private static final Gson gson = new Gson();
	private static final String I18N_TOOLKIT_NEMI_COMPILER = Resources.screen("nemi_compiler");

	private NemiModel model;

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
		          .ifPresent(paths -> openModel(paths[0]));
		DialogUtil.saveFile("Save Model", "*.nem")
		          .ifPresent(this::saveModel);
	}

	private void saveModel(String path)
	{
		path = FileUtil.ensureExtension(path, ".nem");

		var nem = model.createNem();

		try
		{
			var file = new File(path);
			file.delete();
			NbtIo.write(nem, file);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void setModel(NemiModel model)
	{
		this.model = model;
	}

	private void openModel(String path)
	{
		try (Reader reader = Files.newBufferedReader(Path.of(path)))
		{
			setModel(gson.fromJson(reader, NemiModel.class));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
