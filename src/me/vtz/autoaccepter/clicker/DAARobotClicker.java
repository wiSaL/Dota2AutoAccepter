package me.vtz.autoaccepter.clicker;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.util.Timer;
import java.util.TimerTask;

import me.vtz.autoaccepter.DotaAutoAccepter;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.Pointer;

public class DAARobotClicker implements DAAClicker {
	static class Psapi {
		static {
			Native.register("psapi");
		}

		public static native int GetModuleBaseNameW(Pointer hProcess,
				Pointer hmodule, char[] lpBaseName, int size);
	}

	static class Kernel32 {
		static {
			Native.register("kernel32");
		}
		public static int PROCESS_QUERY_INFORMATION = 0x0400;
		public static int PROCESS_VM_READ = 0x0010;

		public static native Pointer OpenProcess(int dwDesiredAccess,
				boolean bInheritHandle, Pointer pointer);
	}

	static class User32DLL {
		static {
			Native.register("user32");
		}

		public static native int GetWindowThreadProcessId(HWND hWnd,
				PointerByReference pref);

		public static native HWND GetForegroundWindow();

	}

	class clickTask extends TimerTask {

		@Override
		public void run() {
			try {
				if (clickOnlyInDota) {
					if (checkDota()) {
						click(windowRect);
					}
				} else {
					click();
				}
			} catch (Exception e) {
				System.out.println("Error. zdes.");
				e.printStackTrace();
			}
		}
	}

	private static final int MAX_TITLE_LENGTH = 1024;

	private Timer timer;
	private Rectangle windowRect;
	private boolean clickOnlyInDota;
	private Robot bot;
	private DotaAutoAccepter dAA;

	public DAARobotClicker(DotaAutoAccepter dotaAutoAccepter) {
		dAA = dotaAutoAccepter;
		try {
			bot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
			System.out.print("pivas");
		}
	}

	public void startClick() {
		clickOnlyInDota = dAA.settings.isClickInDotaOnly();
		timer = new Timer();
		timer.schedule(new clickTask(), 2 * 1000,
				dAA.settings.getInterval() * 1000);
	}

	public void stopClick() {
		timer.cancel();
		timer.purge();
		timer = null;
	}

	public boolean isStarted() {
		return timer == null ? false : true;
	}

	private void click(Rectangle rect) throws AWTException {
		bot.mouseMove(rect.x + (int) (rect.width * dAA.settings.getClickXPos()),
				rect.y + (int) (rect.height * dAA.settings.getClickYPos()));
		bot.mousePress(InputEvent.BUTTON1_MASK);
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		bot.mouseRelease(InputEvent.BUTTON1_MASK);
	}

	private void click() throws AWTException {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		click(new Rectangle(0, 0, (int) screenSize.getWidth(),
				(int) screenSize.getHeight()));
	}

	private boolean checkDota() throws Exception {
		char[] buffer = new char[MAX_TITLE_LENGTH * 2];
		HWND hwnd = User32.INSTANCE.GetForegroundWindow();
		User32.INSTANCE.GetWindowText(hwnd, buffer, MAX_TITLE_LENGTH);
		String windowTitle = Native.toString(buffer);

		RECT rect = new RECT();
		User32.INSTANCE.GetWindowRect(hwnd, rect);
		windowRect = rect.toRectangle();

		PointerByReference pointer = new PointerByReference();
		User32DLL.GetWindowThreadProcessId(User32DLL.GetForegroundWindow(),
				pointer);
		Pointer process = Kernel32.OpenProcess(
				Kernel32.PROCESS_QUERY_INFORMATION | Kernel32.PROCESS_VM_READ,
				false, pointer.getValue());
		Psapi.GetModuleBaseNameW(process, null, buffer, MAX_TITLE_LENGTH);
		String windowProcess = Native.toString(buffer);

		return (windowTitle.equals("DOTA 2") || windowProcess
				.equals("dota.exe")) ? true : false;

	}
}