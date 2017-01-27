package javaNotifer;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Main {
	
	public static Timer timer;
	public static TCPSocket socket;
	public static Alert alert;
	
	// config
	public static int c_hostType;
	public static String c_host;
	public static int c_port;
	public static int c_loc;
	public static int c_rate;
	
	public static void getConfig() {
		Properties prop = new Properties();
		InputStream input = null;
		
			try {
				input = new FileInputStream("settings.conf");
				prop.load(input);
				
				c_hostType = Integer.parseInt(prop.getProperty("hostType"));
				c_host = prop.getProperty("host");
				c_port = Integer.parseInt(prop.getProperty("port"));
				c_loc = Integer.parseInt(prop.getProperty("loc"));
				c_rate = Integer.parseInt(prop.getProperty("rate"));
			} catch (IOException e) {
				c_hostType = 0;
				c_host = "127.0.0.1";
				c_port = 80;
				c_rate = 1000;
				saveConfig();
			}
			
		
	}
	
	public static void saveConfig() {
		Properties prop = new Properties();
		OutputStream output = null;
		
		try {
			output = new FileOutputStream("settings.conf");
			
			prop.setProperty("hostType", Integer.toString(c_hostType));
			prop.setProperty("host", c_host);
			prop.setProperty("port", Integer.toString(c_port));
			prop.setProperty("loc", Integer.toString(c_loc));
			prop.setProperty("rate", Integer.toString(c_rate));
			
			prop.store(output, null);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// fix alternative to SystemTray when it is not supported
	    if(!SystemTray.isSupported()){
	        System.out.println("System tray is not supported, exiting.");
	        System.exit(0);
	    }
	    
	    getConfig(); // reads config on startup
	    
	    alert = new Alert();
	    
	    initTimer();
	    initSystemTray();
	}
	
	/**
	 * Creates a timer that checks asynchronous events as network package.
	 * Single threaded
	 */
	private static void initTimer() {
	    ActionListener packageListener = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	switch (c_hostType) {
            	case 0:
            	try {
            		BufferedReader reader = socket.getReader();
            		if(reader.ready()) {
            		String message = reader.readLine();
					String[] str = message.split("\\|");
					alert.display(str[0], str[1], Integer.parseInt(str[2]), c_loc);
            		}
				} catch (IOException e) {
					System.out.println("error 2");
					timer.stop();
				}
            	break;
            	case 1:
            		try {
						String message = HttpRequest.getRequest(c_host, c_port);
						if (message.length() > 0) {
						String[] str = message.split("\\|");
						alert.display(str[0], str[1], Integer.parseInt(str[2]), c_loc);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
            	break;
            }
            }
        };
	    timer = new Timer(c_rate, packageListener);
	}
	
	/**
	 * Creates a java tray icon.
	 */
	private static void initSystemTray() {
	    SystemTray systemTray = SystemTray.getSystemTray();
	    Image image = Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/food2.png"));
	    
	    PopupMenu trayPopupMenu = new PopupMenu();
	    
	    // connect button
	    MenuItem connect = new MenuItem("Connect");
	    connect.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            connect(c_hostType, c_host, c_port, c_loc, c_rate);
	        }
	    });     
	    trayPopupMenu.add(connect);
	    
	    // create settings dialog		
		final JDialog dialog = new JDialog();
		dialog.setSize(250, 400);
		dialog.setIconImage(image);
		dialog.setLocationByPlatform(true);
		dialog.setFocusable(true);
		dialog.requestFocusInWindow();
		dialog.setResizable(false);
		dialog.setAlwaysOnTop(true);
		dialog.setTitle("Java Notify - Settings");
		
		JPanel panel = settPanel();
		dialog.getContentPane().add(panel);
	    
		// settings button
	    MenuItem settings = new MenuItem("Settings");
	    settings.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	if (dialog.isVisible())
	        		dialog.setVisible(false);
	        	else
	        		dialog.setVisible(true);
	        }
	    });     
	    trayPopupMenu.add(settings);
	    
	    // close button
	    MenuItem close = new MenuItem("Close");
	    close.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            System.exit(0);             
	        }
	    });
	    trayPopupMenu.add(close);
	    
	    TrayIcon trayIcon = new TrayIcon(image, "Java Notify", trayPopupMenu);
	    trayIcon.setImageAutoSize(true);
	    
	    
	    try {
			systemTray.add(trayIcon);
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Trying to establish a connection to server.
	 * @param type the connection type
	 * @param host hostname or url
	 * @param port port number
	 * @param loc screen alert position
	 * @param ref refresh rate only used for http request
	 */
	private static void connect(int type, String host, int port, int loc, int ref) {
		switch (type) {
		  case 0: // TCP socket
			  try {
			  socket = new TCPSocket(host, port);
			  timer.start();
			  } catch (Exception e) {
			System.out.println("connection error");
			e.printStackTrace();
			  }
		  break;
		  
		  case 1: // http request
			  timer.start();
		  break;
		}
	}
	
	/**
	 * Creates the settings panel.
	 * @return settings JPanel
	 */
	public static JPanel settPanel() {
		final JPanel panel = new JPanel();
		panel.setFocusable(true);
		panel.setLayout(null);
		
		panel.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		        JComponent clicked = (JComponent)e.getSource();
		        clicked.requestFocusInWindow();
		    }
		});
		
		JLabel l_hostType = new JLabel("Host Type:");
		Dimension size = l_hostType.getPreferredSize();
		l_hostType.setBounds(75, 6, size.width, size.height);
		panel.add(l_hostType);
		
		final JComboBox<String> hostType = new JComboBox<String>();
		hostType.addItem("TCP socket");
		hostType.addItem("HTTP request - Rest API");
		size = hostType.getPreferredSize();
		hostType.setBounds(40, 30, size.width, size.height);
		hostType.setSelectedIndex(c_hostType);
		panel.add(hostType);
		
		JLabel l_ref = new JLabel("Refresh rate(ms)");
		size = l_ref.getPreferredSize();
		l_ref.setBounds(135, 120, size.width, size.height);
		panel.add(l_ref);
		
		final JTextField txt_ref = new JTextField();
		txt_ref.setBounds(165, 140, 50, 20);
		txt_ref.setText(Integer.toString(c_rate));
		if (c_hostType == 1)
			txt_ref.setEnabled(true);
		else
			txt_ref.setEnabled(false);
		panel.add(txt_ref);
		
		hostType.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		        if (hostType.getSelectedIndex() == 1)
		        	txt_ref.setEnabled(true);
		        else
		        	txt_ref.setEnabled(false);
		    }
		});
		
		JLabel l_host = new JLabel("Hostname:");
		size = l_host.getPreferredSize();
		l_host.setBounds(45, 70, size.width, size.height);
		panel.add(l_host);
		
		final JTextField txt_host = new JTextField();
		txt_host.setBounds(20, 90, 120, 20);
		txt_host.setText(c_host);
		panel.add(txt_host);
		
		JLabel l_port = new JLabel("Port:");
		size = l_port.getPreferredSize();
		l_port.setBounds(170, 70, size.width, size.height);
		panel.add(l_port);
		
		final JTextField txt_port = new JTextField();
		txt_port.setBounds(165, 90, 50, 20);
		txt_port.setText(Integer.toString(c_port));
		panel.add(txt_port);
		
		JLabel l_pos = new JLabel("Notification location");
		size = l_pos.getPreferredSize();
		l_pos.setBounds(20, 120, size.width, size.height);
		panel.add(l_pos);
		
		final JComboBox<String> pos = new JComboBox<String>();
		pos.addItem("Top left");
		pos.addItem("Top right");
		pos.addItem("Bottom left");
		pos.addItem("Bottom right");
		size = pos.getPreferredSize();
		pos.setBounds(25, 140, size.width, size.height);
		pos.setSelectedIndex(c_loc);
		panel.add(pos);
		
		JButton but = new JButton("Save");
		size = but.getPreferredSize();
		but.setBounds(85, 180, size.width, size.height);
		panel.add(but);
		
		but.addActionListener(new ActionListener() {
		  public void actionPerformed(ActionEvent e)
		  {
			c_host = txt_host.getText();
			c_hostType = hostType.getSelectedIndex();
			c_port = Integer.parseInt(txt_port.getText());
			c_loc = pos.getSelectedIndex();
			c_rate = Integer.parseInt(txt_ref.getText());
			
			  if (c_rate < 1 && c_hostType == 1)
				  c_rate = 500;
			
			timer.setDelay(c_rate);
			
			saveConfig();
			
			SwingUtilities.getWindowAncestor(panel).setVisible(false);
		  }
		});
		
		return panel;
	}
}
