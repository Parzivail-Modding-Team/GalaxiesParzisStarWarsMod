package com.parzivail.swg;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import java.io.IOException;
import java.net.URISyntaxException;

public class NotAGameMain {
    public static void main(String[] args) {
        JEditorPane pane = new JEditorPane("text/html", "<html><body style=\"font-family: sans-serif;\">This is a Forge mod for Minecraft 1.7.10. Please install it as one, do not run this file directly. For more information, see: <a rel=\"nofollow\" href=\"https://minecraft.gamepedia.com/Mods/Installing_Forge_mods\">https://minecraft.gamepedia.com/Mods/Installing_Forge_mods</a></body></html>");
        pane.setEditable(false);
        pane.setBorder(null);
        pane.setBackground(null);
        pane.addHyperlinkListener(e -> {
            if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                try {
                    java.awt.Desktop.getDesktop().browse(e.getURL().toURI());
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        });
        JOptionPane.showMessageDialog(null, pane, "Not a program", JOptionPane.ERROR_MESSAGE);
    }
}
