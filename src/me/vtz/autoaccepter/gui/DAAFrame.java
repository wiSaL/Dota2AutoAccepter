package me.vtz.autoaccepter.gui;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatter;

import me.vtz.autoaccepter.DotaAutoAccepter;

public class DAAFrame extends JFrame implements DAAGui {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2057196683737605548L;
	
	public static final String VERSION = "v0.3 Alpha";
	public static final String APP_NAME = "Dota 2 AutoAccepter";
	
	TrayIcon trayIcon;
	SystemTray tray;

	private DotaAutoAccepter dAA;

	private JTextField hotKeyField;
	private JCheckBox defaultsBox, onlyInDotaBox, playSoundBox;
	private JPanel settingsPanel;
	private JLabel ssLabel, intervalLabel, coordLabel, authorLabel;
	private JSpinner intervalSpinner;
	private Font defaultFont, authorFont;
	private DAAPercentField pXField, pYField;
	private Image iconImage, iconImageRunning;
	
	public DAAFrame(DotaAutoAccepter dotaAutoAccepter) {
		super(APP_NAME + " " + VERSION);
		dAA = dotaAutoAccepter;
		setVisible(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		iconImage = Toolkit.getDefaultToolkit().getImage(
				getClass().getResource("/res/trayIcon.png"));
		iconImageRunning = Toolkit.getDefaultToolkit().getImage(
				getClass().getResource("/res/trayIcon_running.png"));
		setIconImage(iconImage);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dAA.settings.setInTrayOnStart(false);
				dAA.settings.saveOnExit(getBounds());
				System.exit(0);
			}
		});
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("mi vse umrem");
		}
		if (SystemTray.isSupported()) {
			tray = SystemTray.getSystemTray();
			PopupMenu popup = new PopupMenu();
			MenuItem exitItem = new MenuItem("Exit");
			exitItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dAA.settings.setInTrayOnStart(true);
					dAA.settings.saveOnExit(getBounds());
					System.exit(0);
				}
			});
			popup.add(exitItem);
			MenuItem openItem = new MenuItem("Open");
			openItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setVisible(true);
					setExtendedState(JFrame.NORMAL);
				}
			});
			popup.add(openItem);
			trayIcon = new TrayIcon(iconImage, APP_NAME, popup);
			trayIcon.setImageAutoSize(true);
			trayIcon.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					setVisible(true);
					setExtendedState(JFrame.NORMAL);
				}

			});
		} else {
			System.out.println("system tray not supported");
		}
		
		addWindowStateListener(new WindowStateListener() {
			public void windowStateChanged(WindowEvent e) {
//				System.out.println("qweqwe : " + e.getNewState());
				if (e.getNewState() == ICONIFIED || e.getNewState() == 7) { //max to min
					try {
						tray.add(trayIcon);
						setVisible(false);
					} catch (AWTException ex) {
						System.out.println("unable to add to tray");
					}
				}
				if (e.getNewState() == MAXIMIZED_BOTH) {
					tray.remove(trayIcon);
					setVisible(true);
				}
				if (e.getNewState() == NORMAL) {
					tray.remove(trayIcon);
					setVisible(true);
				}
			}
		});

	}

	public void buildGUI() {

		setBounds(dAA.settings.getWindowXPos(), dAA.settings.getWindowYPos(),
				315, 225);
		setLayout(null);

		// GUI elements bounds
		Rectangle ssLabelRect = new Rectangle(10, 10, 150, 20);
		Rectangle hotKeyFieldRect = new Rectangle(150, 10, 150, 20);
		Rectangle defaultsBoxRect = new Rectangle(10, 40, 130, 20);
		Rectangle playSoundBoxRect = new Rectangle(150, 40 , 100, 20);/////
		Rectangle settingsPanelRect = new Rectangle(10, 70, 290, 110);
		Rectangle onlyDotaBoxRect = new Rectangle(10, 20, 200, 20);
		Rectangle intervalLabelRect = new Rectangle(10, 50, 140, 20);
		Rectangle intervalSpinnerRect = new Rectangle(150, 50, 60, 20);
		Rectangle coordLabelRect = new Rectangle(10, 80, 150, 20);
		Rectangle pXFieldRect = new Rectangle(150, 80, 60, 20);
		Rectangle pYFieldRect = new Rectangle(215, 80, 60, 20);
		Rectangle authorLabelRect = new Rectangle(230, 180, 70, 15);

		defaultFont = new Font("Calibri", Font.PLAIN, 16);
		authorFont = new Font("Calibri", Font.ITALIC, 12);

		ssLabel = new JLabel(" Start/Stop Hotkey : ");
		ssLabel.setFont(defaultFont);
		ssLabel.setHorizontalAlignment(JLabel.LEFT);
		ssLabel.setBounds(ssLabelRect);
		add(ssLabel);

		hotKeyField = new JTextField(dAA.settings.getHotKeyStr());
		hotKeyField.setFont(defaultFont);
		hotKeyField.setHorizontalAlignment(JTextField.CENTER);
		hotKeyField.setCaretColor(Color.WHITE);
		hotKeyField.setFocusable(false);
		hotKeyField.setEditable(false);
		hotKeyField.setBounds(hotKeyFieldRect);
		hotKeyField.setCaretColor(Color.WHITE);
		add(hotKeyField);

		defaultsBox = new JCheckBox("Default settings");
		defaultsBox.setFont(defaultFont);
		defaultsBox.setHorizontalAlignment(JCheckBox.LEFT);
		defaultsBox.setSelected(dAA.settings.isSettingsDefault());
		defaultsBox.setMnemonic(KeyEvent.VK_D);
		defaultsBox.setBounds(defaultsBoxRect);
		add(defaultsBox);
		
		playSoundBox = new JCheckBox("Play sound");
		playSoundBox.setFont(defaultFont);
		playSoundBox.setHorizontalAlignment(JCheckBox.LEFT);
		playSoundBox.setSelected(dAA.settings.getSoundOnSS());
		playSoundBox.setMnemonic(KeyEvent.VK_S);
		playSoundBox.setBounds(playSoundBoxRect);
		add(playSoundBox);
		

		settingsPanel = new JPanel();
		Border titled = BorderFactory.createTitledBorder("Settings");
		settingsPanel.setBorder(titled);
		settingsPanel.setLayout(null);
		settingsPanel.setBounds(settingsPanelRect);
		if (defaultsBox.isSelected()) {
			settingsPanel.setEnabled(false);
		}
		add(settingsPanel);

		onlyInDotaBox = new JCheckBox("Click only in DOTA");
		onlyInDotaBox.setFont(defaultFont);
		onlyInDotaBox.setHorizontalAlignment(JCheckBox.LEFT);
		onlyInDotaBox.setSelected(dAA.settings.isClickInDotaOnly());
		onlyInDotaBox.setMnemonic(KeyEvent.VK_C);
		onlyInDotaBox.setBounds(onlyDotaBoxRect);
		if (defaultsBox.isSelected()) {
			onlyInDotaBox.setEnabled(false);
		}
		settingsPanel.add(onlyInDotaBox);

		intervalLabel = new JLabel("Click interval (sec) : ");
		intervalLabel.setFont(defaultFont);
		intervalLabel.setHorizontalAlignment(JLabel.LEFT);
		intervalLabel.setBounds(intervalLabelRect);
		if (defaultsBox.isSelected()) {
			intervalLabel.setEnabled(false);
		}
		settingsPanel.add(intervalLabel);

		intervalSpinner = new JSpinner(new SpinnerNumberModel(
				dAA.settings.getInterval(), 1, 20, 1));
		intervalSpinner.setFont(defaultFont);
		intervalSpinner.setToolTipText("Interval between clicks");
		((DefaultEditor) intervalSpinner.getEditor()).getTextField()
				.setEditable(false);
		((DefaultEditor) intervalSpinner.getEditor()).getTextField()
				.setHorizontalAlignment(JTextField.CENTER);

		JComponent comp = intervalSpinner.getEditor();
		JFormattedTextField field = (JFormattedTextField) comp.getComponent(0);
		DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
		formatter.setCommitsOnValidEdit(true);

		intervalSpinner.setBounds(intervalSpinnerRect);
		if (defaultsBox.isSelected()) {
			intervalSpinner.setEnabled(false);
		}
		settingsPanel.add(intervalSpinner);

		coordLabel = new JLabel("Click coordinates : ");
		coordLabel.setFont(defaultFont);
		coordLabel.setHorizontalAlignment(JLabel.LEFT);
		coordLabel.setBounds(coordLabelRect);
		if (defaultsBox.isSelected()) {
			coordLabel.setEnabled(false);
		}
		settingsPanel.add(coordLabel);

		pXField = new DAAPercentField();
		pXField.setFont(defaultFont);
		pXField.getSpinner().setToolTipText(
				"X coordinate in percents of you screen width.");
		pXField.setBounds(pXFieldRect);
		pXField.setPercent(dAA.settings.getClickXPos());
		if (defaultsBox.isSelected()) {
			pXField.setEnabled(false);
		}
		settingsPanel.add(pXField);

		pYField = new DAAPercentField();
		pYField.setFont(defaultFont);
		pYField.getSpinner().setToolTipText(
				"Y coordinate in percents of you screen height.");
		pYField.setBounds(pYFieldRect);
		pYField.setPercent(dAA.settings.getClickYPos());
		if (defaultsBox.isSelected()) {
			pYField.setEnabled(false);
		}
		settingsPanel.add(pYField);

		// byCAH EDITION
		authorLabel = new JLabel(
				"<html><font color='purple'>by SKT.byCAH</font></html>");
		authorLabel.setFont(authorFont);
		authorLabel.setHorizontalAlignment(JLabel.LEFT);
		authorLabel.setBounds(authorLabelRect);
		add(authorLabel);

		hotKeyField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				hotKeyField.setEditable(true);
				hotKeyField.setText("Choose hotkey...");
				hotKeyField.setFocusable(true);
				hotKeyField.requestFocus();
			}
		});
		hotKeyField.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				hotKeyField.setEditable(false);
				hotKeyField.setText(dAA.settings.getHotKeyStr());
				hotKeyField.setForeground(Color.BLACK);
				hotKeyField.setFocusable(false);
				dAA.listener.stopWaiting();
				if (!dAA.listener.isListenerStarted())
					dAA.listener.startListener();
			}

			@Override
			public void focusGained(FocusEvent e) {

				if (dAA.listener.isListenerStarted())
					dAA.listener.stopListener();
				dAA.listener.waitKeyPressed();
			}
		});
		hotKeyField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				hotKeyField.setForeground(Color.WHITE);

			}
		});

		defaultsBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (defaultsBox.isSelected()) {
					setSettingsPanelEnabled(false);
				} else {
					setSettingsPanelEnabled(true);
				}
				onlyInDotaBox.setSelected(dAA.settings.isClickInDotaOnly());
				((DefaultEditor) intervalSpinner.getEditor()).getTextField()
						.setText(Integer.toString(dAA.settings.getInterval()));
				pXField.setPercent(dAA.settings.getClickXPos());
				pYField.setPercent(dAA.settings.getClickYPos());
			}

		});
		
		playSoundBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dAA.settings.playSoundOnSS(playSoundBox.isSelected());
			}
		});

		onlyInDotaBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dAA.settings.clickInDotaOnly(onlyInDotaBox.isSelected());
			}
		});

		intervalSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				dAA.settings.setInterval(Integer.parseInt(intervalSpinner
						.getValue().toString()));
			}
		});

		pXField.getSpinner().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				dAA.settings.setClickXPos(pXField.getPercent());
			}
		});
		pYField.getSpinner().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				dAA.settings.setClickYPos(pYField.getPercent());
			}
		});
		if (dAA.settings.getInTrayOnStart()) {
			try {
				tray.add(trayIcon);
				setVisible(false);
			} catch (AWTException ex) {
				System.out.println("unable to add to tray");
			}
		} else {
			setVisible(true);
		}
	}

	public void waitedKeyPressed() {
		hotKeyField.setFocusable(false);
	}
	
	public void setStatusRunning (boolean running) {
		if (running) {
			setIconImage(iconImageRunning);
			trayIcon.setImage(iconImageRunning);
		} else {
			setIconImage(iconImage);
			trayIcon.setImage(iconImage);
		}
		if (dAA.settings.getSoundOnSS()) playSound();
	}
	
	private void playSound() {
		try {
		    
		    AudioInputStream ais = AudioSystem.getAudioInputStream(getClass()
		    		.getResourceAsStream("/res/startstop.wav"));
		    
		    Clip clip = AudioSystem.getClip();
		    clip.open(ais);
		    
		    clip.setFramePosition(0);
		    clip.start();

		} catch(IOException | UnsupportedAudioFileException | LineUnavailableException exc) {
		    exc.printStackTrace();
		}
	}

	private void setSettingsPanelEnabled(boolean b) {
		dAA.settings.setDefaults(!b);
		settingsPanel.setEnabled(b);
		onlyInDotaBox.setEnabled(b);
		intervalLabel.setEnabled(b);
		intervalSpinner.setEnabled(b);
		coordLabel.setEnabled(b);
		pXField.setEnabled(b);
		pYField.setEnabled(b);
	}

}
