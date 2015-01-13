package me.vtz.autoaccepter.settings;

import java.awt.Rectangle;

public interface DAASettings {
		
	public void saveOnExit(Rectangle frameBounds);
	
	public void save();
	
	public int getWindowXPos();
	
	public int getWindowYPos();
	
	public void setDefaults(Boolean isDef); 
	
	public boolean isSettingsDefault();
	
	public void setHotKey(String hotKey);
	
	public String getHotKey();
	
	public void setHotKeyStr(String hotKeyStr);
	
	public String getHotKeyStr(); 
	
	public void clickInDotaOnly(boolean clickOnlyInDota); 
	
	public boolean isClickInDotaOnly(); 
	
	public void setInterval(int interval); 
	
	public int getInterval();
	
	public void setClickXPos(double x); 
	
	public double getClickXPos(); 
	
	public void setClickYPos(double y);
	
	public double getClickYPos(); 
	
	public void setInTrayOnStart(boolean inTray);
	
	public boolean getInTrayOnStart(); 
}
