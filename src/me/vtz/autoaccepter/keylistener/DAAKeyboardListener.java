package me.vtz.autoaccepter.keylistener;

import me.vtz.autoaccepter.DotaAutoAccepter;
import de.ksquared.system.keyboard.GlobalKeyListener;
import de.ksquared.system.keyboard.KeyAdapter;
import de.ksquared.system.keyboard.KeyEvent;

public class DAAKeyboardListener implements DAAKeyListener {
	private GlobalKeyListener keyListener;
	private KeyAdapter waitingKey;
	private KeyAdapter waitingHotKey;
	private boolean listenerStarted = false;
	private DotaAutoAccepter dAA;

	public DAAKeyboardListener(DotaAutoAccepter dotaAutoAccepter) {
		dAA = dotaAutoAccepter;
		keyListener = new GlobalKeyListener();
		waitingKey = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent event) {
				String hotKeyStr = new String("");
				if (event.isCtrlPressed())
					hotKeyStr = "Ctrl";
				if (event.isShiftPressed()) {
					hotKeyStr += add(hotKeyStr, "Shift");
				}
				if (event.isAltPressed()) {
					hotKeyStr += add(hotKeyStr, "Alt");
				} 
//				String hk = java.awt.event.KeyEvent.getKeyText(event
//						.getVirtualKeyCode());
				System.out.println(event.getVirtualKeyCode());
				String hk = dAA.converter.getKeyTextTemp(event.getVirtualKeyCode());
				if (hotKeyStr.isEmpty())
					hotKeyStr = hk;
				else
					hotKeyStr = hotKeyStr + " " + hk;
				dAA.settings.setHotKeyStr(hotKeyStr);
				dAA.settings.setHotKey(event.toString());
				dAA.gui.waitedKeyPressed();
			}
		};

		waitingHotKey = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent event) {
				if (event.toString().equals(dAA.settings.getHotKey())) {
					if (dAA.clicker.isStarted())
						dAA.clicker.stopClick();
					else
						dAA.clicker.startClick();
				}

			}
		};
	}

	public void waitKeyPressed() {
		keyListener.addKeyListener(waitingKey);
	}

	public void stopWaiting() {
		keyListener.removeKeyListener(waitingKey);
	}

	public void startListener() {
		keyListener.addKeyListener(waitingHotKey);
		listenerStarted = true;
	}

	public void stopListener() {
		keyListener.removeKeyListener(waitingHotKey);
		listenerStarted = false;
	}

	public boolean isListenerStarted() {
		return listenerStarted;
	}
	
	private String add(String a, String b) {
		return a.isEmpty() ? b : " " + b; 

	}
}
