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
	
//	private DotaAutoAccepter dAA;
	
	public DAAConverter(DotaAutoAccepter dotaAutoAccepter) {
//		dAA = dotaAutoAccepter;
	}
	
	public static String getKeyType(int key) {
		
//		System.out.println((char)User32.INSTANCE.MapVirtualKey(key, 2));
		byte[] keystate = new byte[256];
		User32.INSTANCE.GetKeyboardState(keystate);

		IntByReference keyblayoutID = User32.INSTANCE.GetKeyboardLayout(0);
		int ScanCode = User32.INSTANCE.MapVirtualKeyExW(key, 0, keyblayoutID);
		
		System.out.println("MapVirtualKey" + User32.INSTANCE.MapVirtualKeyExW(key, 0, keyblayoutID));
		
		char[] buff = new char[10];
		int bufflen = buff.length;
		int ret = User32.INSTANCE.ToUnicodeEx(key, ScanCode, keystate, buff,
				bufflen, 0, keyblayoutID);

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
