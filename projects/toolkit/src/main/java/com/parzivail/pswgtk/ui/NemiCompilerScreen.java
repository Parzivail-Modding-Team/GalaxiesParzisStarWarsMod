package com.parzivail.pswgtk.ui;

import com.google.gson.Gson;
import com.parzivail.pswg.Resources;
import com.parzivail.pswgtk.ToolkitClient;
import com.parzivail.pswgtk.model.nemi.NemiModel;
import com.parzivail.pswgtk.screen.JComponentScreen;
import com.parzivail.pswgtk.swing.EventHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.Reader;
import java.nio.file.Files;

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
		final JFileChooser fc = new JFileChooser();

		Action details = fc.getActionMap().get("viewTypeDetails");
		details.actionPerformed(null);

		fc.setFileFilter(new FileNameExtensionFilter("NEMi Models", "nemi"));
		int returnVal = fc.showOpenDialog(getSource());

		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();
			try (Reader reader = Files.newBufferedReader(file.toPath()))
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
}
