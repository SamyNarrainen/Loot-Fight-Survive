import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Observer;

import javax.swing.*;

/**
 * A custom button build with a JLabel with two states 'unselected' and 'selected'.
 * @author Samy Narrrainen
 *
 */
public class CustomButton extends JLabel  {
	//Has the cursor entered the JLabel?
	private boolean entered = false; 
	//Should we hide the button?
	private boolean hide = false;
	//So the button is able to call actions independently
	private Actions actions; 
	//Assigns the button a specific action
	private int action; 
	//General positioning and size
	private int x, y, width, height;
	//Two states of the button
	private BufferedImage unselected, selected;
	//The observer calling this method
	private ImageObserver observer;
	
	/**
	 * Constructor
	 */
	public CustomButton(int x, int y, int width, int height, BufferedImage unselected, BufferedImage selected, ImageObserver o, Actions a,  int specifiedAction) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.unselected = unselected;
		this.selected = selected;
		this.observer = o;
		this.actions = a;
		this.action = specifiedAction;
		
		this.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				//System.out.println("CustomButton entered");
				entered = true;
			}
			
			public void mouseExited(MouseEvent e) {
				//System.out.println("CustomButton exited");
				entered = false;
			}
			
			public void mousePressed(MouseEvent e) {
				actions.actionSelect(action);
			}
		});
		
		this.setBounds(x, y, width, height);
		this.setCursor(new Cursor(Cursor.HAND_CURSOR));
	}

	protected void paintComponent(Graphics g) {
		super.paintChildren(g);
		if(!entered && !hide) 
			g.drawImage(unselected, x, y, width, height, observer);
		else if(entered && !hide) 
			g.drawImage(selected, x, y, width, height, observer);
	}
	
	/**
	 * Changes the position of the label
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void customSetBounds(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.hide = false;
		this.setBounds(x, y, width, height);
	}
	
	/**
	 * Makes the button invisible and unselectable
	 */
	public void hide() {
		this.hide = true;
		this.setBounds(0, 0, 0, 0);
	}
 	

	
}
