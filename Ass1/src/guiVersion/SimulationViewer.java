package src.guiVersion;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.*;

//Class for simulate the GUI of BouncingBalls
public class SimulationViewer extends JFrame {
    
	//Variable for layout of the GUI
	private static final long serialVersionUID = 1L;
	private VisualiserPanel panel;
    private Container p;
    private BouncingBalls game;
    
    /**
     * Creates a view of the specified size (in pixels)
     * @param w
     * @param h
     */
    
    //Constructor of SimulationViewer
    public SimulationViewer(int w, int h, BouncingBalls controller){
    	this.game = controller;
    	setTitle("Bodies Simulation");
        setSize(w,h);
        setResizable(false);
        JFrame myFrame = new JFrame();
        myFrame.setSize(new Dimension(w,h+75));
        myFrame.setLayout(new BorderLayout());
        panel = new VisualiserPanel(w,h);
        p = getContentPane();
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        JButton start = new JButton("Start");
        JButton stop = new JButton("Stop");
        start.addActionListener(e -> {
        	new Thread(() ->{
            	game.begin();
            	this.setVisible(false);;
        	}).start();
        });
        start.setAlignmentX(Component.RIGHT_ALIGNMENT);
        stop.addActionListener(e -> {
        	new Thread(() ->{
            	game.stop();
        	}).start();
        });
        stop.setAlignmentX(Component.RIGHT_ALIGNMENT);
        p.add(Box.createRigidArea(new Dimension(0,10)));
        p.add(start);
        p.add(stop);
        p.add(Box.createRigidArea(new Dimension(0,10)));
        p.add(panel);
        myFrame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent ev){
				System.exit(-1);
			}
			public void windowClosed(WindowEvent ev){
				System.exit(-1);
			}
		});
        myFrame.getContentPane().add(p);
        myFrame.setVisible(true);
    }
    //Method for display GUI
	public void display(ArrayList<Body> bodies, double vt, long iter) {
				panel.display(bodies, vt, iter);
	}
	
	//Class for create Panel with all balls
    public static class VisualiserPanel extends JPanel {
        
    	/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private ArrayList<Body> bodies = new ArrayList<Body>();
    	private long nIter;
    	private double vt;
    	
        private long dx;
        private long dy;
        
        //Constructor with: width and height of panel
        public VisualiserPanel(int w, int h){
            setSize(w,h);
            dx = w/2 - 20;
            dy = h/2 - 20;
        }

        //Method for paint all the balls
        public void paint(Graphics g){
    		Graphics2D g2 = (Graphics2D) g;
    		
    		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
    		          RenderingHints.VALUE_ANTIALIAS_ON);
    		g2.setRenderingHint(RenderingHints.KEY_RENDERING,
    		          RenderingHints.VALUE_RENDER_QUALITY);
    		g2.clearRect(0,0,this.getWidth(),this.getHeight());
	        
    		bodies.forEach( b -> {
	        	Position p = b.getPos();
	            double rad = b.getRadius();
	            int x0 = (int)(dx + p.getX()*dx);
		        int y0 = (int)(dy - p.getY()*dy);
		        g2.drawOval(x0,y0, (int)(rad*dx*2), (int)(rad*dy*2));
		    });		    
    		String time = String.format("%.2f", vt);
    		g2.drawString("Bodies: " + bodies.size() + " - vt: " + time + " - nIter: " + nIter, 2, 20);
        }
        
        //Method who display all balls into the GUI
        public void display(ArrayList<Body> bodies, double vt, long iter){
            this.bodies = bodies;
            this.vt = vt;
            this.nIter = iter;
        	repaint();
        }
    }
}
