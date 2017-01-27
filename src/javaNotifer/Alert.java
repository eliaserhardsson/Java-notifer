package javaNotifer;

import jaco.mp3.player.MP3Player;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Alert {
	
    private JFrame f;
    private JLabel label;
    private JLabel label2;
    private int timecount;
    private int time;
    private Timer timer;
    
    private MP3Player soundPlayer;

	public static boolean isWindows()
	{
		return System.getProperty("os.name").startsWith("Windows");
	}
	
	public Alert() {
	    this.f = new JFrame();
	    this.label = new JLabel();
	    this.label2 = new JLabel();
	    
	    this.soundPlayer = new MP3Player(this.getClass().getResource("/Sound.mp3"));
	}

	/**
	 * Displays alert on the screen.
	 * @param headtext title text
	 * @param undertext message
	 * @param timeInMs time before alert disappears
	 * @param pos screen position
	 */
	public void display(String headtext, String undertext, int timeInMs, int pos) {
		this.time = timeInMs;
		timecount = 0;
        label.setOpaque(true);
        label.setFont(new Font("Arial black", Font.BOLD, 14));
        label.setText(headtext);
        label2.setOpaque(true);
        label2.setText(undertext);
        label2.setFont(new Font("Lucida console", Font.PLAIN, 12));
        
        final JLabel plabel = label;
        final JLabel plabel2 = label2;
	    
	    JPanel p = new JPanel() {
	        /**
			 * 
			 */
			private static final long serialVersionUID = -7511892525754890913L;

			public void paintComponent(Graphics g) {
	        	
	            Graphics2D g2d = (Graphics2D) g;
	            g2d.clearRect(1, 2, 300, 56);
	            g2d.setBackground(new Color(255, 255, 255, 0));
	            g2d.clearRect(0, 0, 360, 100);
	            this.remove(plabel);
	            this.remove(plabel2);
	            g2d.drawRect(1, 2, 300, 56);
	            

	            this.add(plabel).setBounds(4, 8, 280, 20);
	            this.add(plabel2).setBounds(4, 32, 280, 20);
	        }
	    };
	    p.setSize(310, 70);
	    p.setOpaque(false);
	    p.setFocusable(false);
	    p.add(label);
	    p.add(label2);
	    f.add(p);
	    
	    if (!f.isDisplayable())
	    	f.setUndecorated(true);
	    
	    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
	    Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
	    int x;
	    int y;
	    switch (pos) {
	    	case 0: // Top left
	    		f.setLocation(0, 0);
	    	break;
	    	case 1: // Top Right
	    		x = (int)rect.getMaxX() - p.getWidth();
	    		f.setLocation(x, 0);
	    	break;
	    	case 2: // Bottom left
	    		y = (int)rect.getMaxY() - p.getHeight();
	    		f.setLocation(0, y);
	    	break;
	    	case 3: // Bottom right
	    		x = (int)rect.getMaxX() - p.getWidth();
	    		y = (int)rect.getMaxY() - p.getHeight();
	    		f.setLocation(x, y);
	    	break;
	    	
	    	default: // Top right
	    		f.setLocation(0, 0);
	    	break;
	    }
	    
	    f.setBackground(new Color(255, 255, 255, 0));
	    f.setSize(360, 100);
	    f.setFocusableWindowState(false);
	    f.setAlwaysOnTop(true);
	    f.setVisible(true);
	    
	    soundPlayer.play();
	    
	    ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
	        	f.toFront();
	        	if (isWindows())
	        		f.repaint(); // needs for windows 7s  game fullscreen
	        	
	        	timecount += 33;
	        	if (timecount >= time) {
	        		f.setVisible(false);
	        		timer.stop();
	        	}
            }
        };
        this.timer = new Timer(33 ,taskPerformer);
        timer.start();
	    
	}
}
