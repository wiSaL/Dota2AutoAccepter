package me.vtz.autoaccepter.keylistener;

public interface DAAKeyListener {
	
	public void waitKeyPressed();
	
	public void stopWaiting();
	
	public void startListener();
	
	public void stopListener();
	
	public boolean isListenerStarted();
}
