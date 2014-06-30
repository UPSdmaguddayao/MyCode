package cs315.yourname.hwk6;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;


/**
 * A quick wrapper for writing a RayTracer
 * @author joel
 * @version Nov 12, 2013
 */
public class RayTracerWindow extends JPanel
{
	private RayTracer tracer;
	
	public RayTracerWindow(int w, int h)
	{
		this.setPreferredSize(new Dimension(w,h));

		tracer = new RayTracer(w,h);
		
		JFrame frame = new JFrame("Ray Tracing");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this);
		frame.pack();
		frame.setVisible(true);

		tracer.render();
		repaint();
	}

	public void paintComponent(Graphics g)
	{
		g.drawImage(tracer.getImage(), 0, 0, null);
	}

	public static void main(String[] args)
	{
		new RayTracerWindow(400,400);
	}
}
