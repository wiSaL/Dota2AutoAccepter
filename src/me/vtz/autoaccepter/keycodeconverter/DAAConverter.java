package me.vtz.autoaccepter.keycodeconverter;

import me.vtz.autoaccepter.DotaAutoAccepter;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;

interface User32 extends Library {
	public static User32 INSTANCE = (User32) Native.loadLibrary("User32",
			User32.class);

	IntByReference GetKeyboardLayout(int dwLayout);

	int MapVirtualKeyExW(int uCode, int nMapType, IntByReference dwhkl);

	int MapVirtualKey(int uCode, int nMapType);

	boolean GetKeyboardState(byte[] lpKeyState);

	int ToUnicodeEx(int wVirtKey, int wScanCode, byte[] lpKeyState,
			char[] pwszBuff, int cchBuff, int wFlags, IntByReference dwhkl);

}

public class DAAConverter implements DAAKeyCodeConverter {

	// private DotaAutoAccepter dAA;

	public DAAConverter(DotaAutoAccepter dotaAutoAccepter) {
		// dAA = dotaAutoAccepter;
	}

	public String getKeyTextTemp(int key) {
		switch (key) {
		case 192:
			return "`"; // instead of "BackQuote"
		case 160:
		case 161:
			return "Shift"; // instead of "Greater"
		case 162:
		case 163:
			return "Ctrl"; // instead of "Right Brace"
		case 164:
		case 165:
			return "Alt";
		case 44:
			return "PrtScr"; // instead of "Comma"
		case 45:
			return "Ins"; // instead of "Minus"
		case 46:
			return "Del"; // instead of "Period"
		case 219:
			return "["; // cause keyboardhook keycodes not similar to awt codes
		case 221:
			return "]";
		case 220:
			return "\\";
		case 186:
			return ";";
		case 222:
			return "'";
		case 188:
			return ",";
		case 190:
			return ".";
		case 191:
			return "/";
		case 226:
			return "\\";
		case 189:
			return "-";
		case 187:
			return "=";
		case 13:
			return "Enter";
		default:
			return java.awt.event.KeyEvent.getKeyText(key);
		}

	}

	public String getKeyText(int key) {

		// System.out.println((char)User32.INSTANCE.MapVirtualKey(key, 2));
		byte[] keystate = new byte[256];
		User32.INSTANCE.GetKeyboardState(keystate);

		IntByReference keyblayoutID = User32.INSTANCE.GetKeyboardLayout(0);
		int ScanCode = User32.INSTANCE.MapVirtualKeyExW(key, 0, keyblayoutID);

		System.out.println("MapVirtualKey : " + ScanCode);
		System.out.println("VK.getKeyText() : "
				+ java.awt.event.KeyEvent.getKeyText(ScanCode));

		char[] buff = new char[10];
		int bufflen = buff.length;
		int ret = User32.INSTANCE.ToUnicodeEx(key, ScanCode, keystate, buff,
				bufflen, 0, keyblayoutID);
		System.out.println("ToUniEx : "
				+ User32.INSTANCE.ToUnicodeEx(key, ScanCode, keystate, buff,
						bufflen, 0, keyblayoutID));
		System.out.println("Value : " + String.valueOf(buff));

		switch (ret) {
		case -1:
			return ("Error");
		case 0: // no translation
			return ("?");
		default:
			return (String.valueOf(buff).substring(0, ret).toUpperCase());
		}
	}
}
