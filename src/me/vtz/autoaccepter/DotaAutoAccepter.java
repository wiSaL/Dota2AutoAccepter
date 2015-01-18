package me.vtz.autoaccepter;

import me.vtz.autoaccepter.clicker.DAAClicker;
import me.vtz.autoaccepter.clicker.DAARobotClicker;
import me.vtz.autoaccepter.gui.DAAFrame;
import me.vtz.autoaccepter.gui.DAAGui;
import me.vtz.autoaccepter.keycodeconverter.DAAConverter;
import me.vtz.autoaccepter.keycodeconverter.DAAKeyCodeConverter;
import me.vtz.autoaccepter.keylistener.DAAKeyListener;
import me.vtz.autoaccepter.keylistener.DAAKeyboardListener;
import me.vtz.autoaccepter.settings.DAAPropSettings;
import me.vtz.autoaccepter.settings.DAASettings;

public class DotaAutoAccepter {

	public final DAAClicker clicker;
	public final DAAGui gui;
	public final DAAKeyCodeConverter converter;
	public final DAAKeyListener listener;
	public final DAASettings settings;

	public static void main(String[] args) {
		new DotaAutoAccepter();

	}

	private DotaAutoAccepter() {
		settings = new DAAPropSettings(this);
		clicker = new DAARobotClicker(this);
		listener = new DAAKeyboardListener(this);
		gui = new DAAFrame(this);
		converter = new DAAConverter(this);
		gui.buildGUI();
		if (!listener.isListenerStarted())
			listener.startListener();

	}

}
