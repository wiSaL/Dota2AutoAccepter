package me.vtz.autoaccepter.settings;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import me.vtz.autoaccepter.DotaAutoAccepter;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.W32APIFunctionMapper;
import com.sun.jna.win32.W32APITypeMapper;

public class DAAPropSettings implements DAASettings{
	
	static interface Shell32 extends Library {

        public static final int MAX_PATH = 260;
        public static final int CSIDL_LOCAL_APPDATA = 0x001c;
        public static final int SHGFP_TYPE_CURRENT = 0;
        public static final int SHGFP_TYPE_DEFAULT = 1;
        public static final int S_OK = 0;

        static Shell32 INSTANCE = (Shell32) Native.loadLibrary("shell32",
                Shell32.class, OPTIONS);

        public int SHGetFolderPath(HWND hwndOwner, int nFolder, HANDLE hToken,
                int dwFlags, char[] pszPath);

    }
	private static Map<String, Object> OPTIONS = new HashMap<String, Object>();
    static {
        OPTIONS.put(Library.OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE);
        OPTIONS.put(Library.OPTION_FUNCTION_MAPPER,
                W32APIFunctionMapper.UNICODE);
    }
	
    
	private String homeDir;
	private String settingsFileName;
	private final Properties props = new Properties();
	
	private int windowXPos, windowYPos;
	private String hotKeyStr, hotKey;
	private boolean isSettingsDefault, clickOnlyInDota, inTrayOnStart, playSoundOnSS;
	private int interval;
	private double clickXPos, clickYPos;
//	private DotaAutoAccepter dAA;
	
	public DAAPropSettings(DotaAutoAccepter dotaAutoAccepter) {
//		dAA = dotaAutoAccepter;
		homeDir = getAppDataPath();
		settingsFileName = homeDir + File.separator + "d2aa.properties";
		if (homeDir != "") {
			load();
		} else {
			loadDefaults();
		}
	}
	
	private void load() {
		try {
            FileInputStream input = new FileInputStream(settingsFileName);
            props.load(input);
            input.close();
            windowXPos = Integer.parseInt(props.getProperty("windowXPos", "500"));
            windowYPos = Integer.parseInt(props.getProperty("windowYPos", "500"));
            hotKey = props.getProperty("hotKey", "88 [up,alt]");
            hotKeyStr = props.getProperty("hotKeyStr", "Alt X");
            isSettingsDefault = Boolean.parseBoolean(props.getProperty("isSettingsDefault", "true"));
            clickOnlyInDota = Boolean.parseBoolean(props.getProperty("clickOnlyInDota", "true"));
            inTrayOnStart = Boolean.parseBoolean(props.getProperty("inTrayOnStart", "false"));
            playSoundOnSS = Boolean.parseBoolean(props.getProperty("playSoundOnSS", "true"));
            interval = Integer.parseInt(props.getProperty("interval", "5"));
            if (interval < 1) interval = 1;
            if (interval > 20) interval = 20;
            clickXPos = Double.parseDouble(props.getProperty("clickXPos", "40"));
            if (clickXPos < 0) clickXPos = 0;
            if (clickXPos > 100) clickXPos = 100;
            clickYPos = Double.parseDouble(props.getProperty("clickYPos", "39"));
            if (clickYPos < 0) clickYPos = 0;
            if (clickYPos > 100) clickYPos = 100;
            
        } catch(Exception ignore) {
        	System.out.println("ERROR : Settings file not found or corrupted.");
            loadDefaults();
        }
	}
	
	private void loadDefaults() {
		windowXPos = 500;  //change to bottom right corner
		windowYPos = 500;
		
		hotKey = "88 [up,alt]";
		hotKeyStr = "Alt X";
		props.setProperty("hotKey", hotKey);
		isSettingsDefault = true;
		props.setProperty("isSettingsDefault", Boolean.toString(isSettingsDefault));
		inTrayOnStart = false;
		props.setProperty("inTrayOnStart", Boolean.toString(inTrayOnStart));
		playSoundOnSS = true;
		props.setProperty("playSoundOnSS", Boolean.toString(playSoundOnSS));
		setDefaultSettings();

	}
	
	private void setDefaultSettings() {
		clickOnlyInDota = true;
		props.setProperty("clickOnlyInDota", Boolean.toString(clickOnlyInDota));
		interval = 5;
		props.setProperty("interval", Integer.toString(interval));
		clickXPos = 0.41;
		props.setProperty("clickXPos", Double.toString(clickXPos));
		clickYPos = 0.39;
		props.setProperty("clickYPos", Double.toString(clickYPos));
	}
	
	private String getAppDataPath() {
		if (com.sun.jna.Platform.isWindows()) {
            HWND hwndOwner = null;
            int nFolder = Shell32.CSIDL_LOCAL_APPDATA;
            HANDLE hToken = null;
            int dwFlags = Shell32.SHGFP_TYPE_CURRENT;
            char[] pszPath = new char[Shell32.MAX_PATH];
            int hResult = Shell32.INSTANCE.SHGetFolderPath(hwndOwner, nFolder, hToken, dwFlags, pszPath);
            if (Shell32.S_OK == hResult) {
                String path = new String(pszPath);
                int len = path.indexOf('\0');
                path = path.substring(0, len);
                return(path);
            } else {
                System.err.println("Error: " + hResult);
                return("");
            }
        }
        return ("");
	}
	
	public void saveOnExit(Rectangle frameBounds) {
        props.setProperty("windowXPos", Integer.toString(frameBounds.x));
        props.setProperty("windowYPos", Integer.toString(frameBounds.y));
        save();
	}
	
	public void save() {
		try {
            FileOutputStream output = new FileOutputStream(settingsFileName);
            props.store(output, "DOTA 2 AutoAcceper settings");
            output.close();
        } catch(Exception ignore) {
        	System.out.println("ERROR : Can't save settings.");
        }
	}
	
	public int getWindowXPos() {
		return windowXPos;
	}
	
	public int getWindowYPos() {
		return windowYPos;
	}
	
	public void setDefaults(Boolean isDef) {
		isSettingsDefault = isDef;
		if (isSettingsDefault) {
			setDefaultSettings();
		}
		props.setProperty("isSettingsDefault", Boolean.toString(isSettingsDefault));
	}
	
	public boolean isSettingsDefault() {
		return isSettingsDefault;
	}
	
	public void setHotKey(String hotKey) {
		this.hotKey = hotKey;
		props.setProperty("hotKey", hotKey);
	}
	
	public String getHotKey() {
		return hotKey;
	}
	
	public void setHotKeyStr(String hotKeyStr) {
		this.hotKeyStr = hotKeyStr;
		props.setProperty("hotKeyStr", hotKeyStr);
	}
	
	public String getHotKeyStr() {
		return hotKeyStr;
	}
	
	public void clickInDotaOnly(boolean clickOnlyInDota) {
		this.clickOnlyInDota = clickOnlyInDota;
		props.setProperty("clickOnlyInDota", Boolean.toString(clickOnlyInDota));
	}
	
	public boolean isClickInDotaOnly() {
		return clickOnlyInDota;
	}
	
	public void setInterval(int interval) {
		if (interval > 0 && interval <= 20) {
			this.interval = interval;
			props.setProperty("interval", Integer.toString(interval));
		}
	}
	
	public int getInterval() {
		return interval;
	}
	
	public void setClickXPos(double x) {
		if (x >= 0 && x <= 1d) {
			clickXPos = x;
			props.setProperty("clickXPos", Double.toString(clickXPos));
		}
	}
	
	public double getClickXPos() {
		return clickXPos;
	}
	
	public void setClickYPos(double y) {
		if (y >= 0 && y <= 1d) {
			clickYPos = y;
			props.setProperty("clickYPos", Double.toString(clickYPos));
		}
	}
	
	public double getClickYPos() {
		return clickYPos;
	}
	
	public void setInTrayOnStart(boolean inTray) {
		inTrayOnStart = inTray;
		props.setProperty("inTrayOnStart", Boolean.toString(inTrayOnStart));
	}
	
	public boolean getInTrayOnStart() {
		return inTrayOnStart;
	}
	
	public boolean getSoundOnSS() {
		return playSoundOnSS;
	}
	
	public void playSoundOnSS(boolean sound) {
		playSoundOnSS = sound;
		props.setProperty("playSoundOnSS", Boolean.toString(playSoundOnSS));
	}
}
