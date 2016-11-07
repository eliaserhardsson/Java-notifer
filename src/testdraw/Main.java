package TestDraw;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main {
	static int a = 0;

	public static void main(String[] args) {
	    JFrame f = new JFrame();
	    JPanel p = new JPanel() {
	        @Override
	        public void paintComponent(Graphics g) {
	        	String str = "hej";
	        	Image img1 = Toolkit.getDefaultToolkit().getImage("Black.jpg");
	        	
	            Graphics2D g2d = (Graphics2D) g;
	            FontMetrics fm = g.getFontMetrics();
	            Rectangle2D rect = fm.getStringBounds(str, g);
	            g2d.setComposite(AlphaComposite.SrcOver.derive(0.5f));
	            //g2d.setBackground(new Color(255, 255, 255, 0));
	            g2d.clearRect(0, 0, f.getWidth(), f.getHeight());
	            //g2d.setColor(Color.BLACK);
	            //g2d.drawRect(1, 2, 60, 60);
	            g2d.drawImage(img1, 0, 0, this);
	            //g2d.fillRect(8, 16-fm.getAscent(), (int)rect.getWidth(), (int)rect.getHeight());
	            //g2d.setColor(Color.WHITE);
	            //g2d.drawString(str, 8, 16);
	            //g2d.drawRect(a, a++, 60, 60);
	            if (a > 100)
	            	a = 0;
	        }
	    };
	    p.setOpaque(false);
	    f.add(p);
	    f.setUndecorated(true);
	    f.setBackground(new Color(255, 255, 255, 0));
	    f.setSize(128, 128);
	    f.setVisible(true);
	    f.createBufferStrategy(2);
	    f.setAlwaysOnTop(true);

	    BufferStrategy bs = f.getBufferStrategy();
	    while (true) {
	        try {
	            Thread.sleep(33);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	        if (System.getProperty("os.name").contains("indows ")) {
	        	//int sta = f.getExtendedState() & ~JFrame.ICONIFIED & JFrame.NORMAL;
	        	//f.setExtendedState(sta);
	        	f.toFront();
	        	f.repaint();
	            p.repaint();
	        } else {
	            Graphics g = null;
	            do {
	                try {
	                    g = bs.getDrawGraphics();
	                    p.update(g);
	                    //f.setFocusable(true);
	                } finally {
	                    g.dispose();
	                }
	                bs.show();
	            } while (bs.contentsLost());
	            Toolkit.getDefaultToolkit().sync();
	        }
	    }
	}
}
