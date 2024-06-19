package jls.elem;

import jls.*;
import jls.sim.*;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;
import java.util.BitSet;
import javax.swing.*;

/**
 * Multiplexor.
 * 
 * @author David A. Poplawski
 */
public class ShiftRegister extends LogicElement {

	// default values
	private static final int defaultBits = 8;
	private static final int defaultPropDelay = 25; 
	
	// saved properties
	private int bits = defaultBits;
	private int propDelay = defaultPropDelay;
	private JLSInfo.Orientation outputOrientation = JLSInfo.Orientation.RIGHT;
	private JLSInfo.Orientation selectorOrientation = JLSInfo.Orientation.DOWN;
	
	
	public enum Type { 
		LogicalLeft, 
		LogicalRight, 
		ArithmeticRight
	}
	private Type type = Type.LogicalLeft;
		
	// running properties
	private boolean cancelled;

	/**
	 * Create a new multiplexor element.
	 * 
	 * @param circuit The circuit this element is part of.
	 */
	public ShiftRegister(Circuit circuit) {
		
		super(circuit);
	} // end of constructor

	/**
	 * Display dialog to get characteristics.
	 * 
	 * @param g The Graphics object to use to initialize sizes
	 * @param editWindow The editor window this constant will be displayed in.
	 * @param x The x-coordinate of the last known mouse position.
	 * @param y The y-coordinate of the last known mouse position.
	 * 
	 * @return false if cancelled, true otherwise.
	 */
	public boolean setup(Graphics g, JPanel editWindow, int x, int y) {
		
		// show creation dialog
		Point pos = editWindow.getMousePosition();
		Point win = editWindow.getLocationOnScreen();
		if (pos == null) {
			new SRCreate(x+win.x,y+win.y);
		}
		else {
			new SRCreate(pos.x+win.x,pos.y+win.y);
		}
		
		// don't do anything if user cancelled element
		if (cancelled)
			return false;
		
		// complete initialization
		init(g);
		
		// save position
		Point p = MouseInfo.getPointerInfo().getLocation();
		p.x -= win.x;
		p.y -= win.y;
		if (p != null) {
			super.setXY(p.x-width/2,p.y-height/2);
		}
		
		return true;
	} // end of setup method
	
	/**
	 * Initialize internal info for this element.
	 * 
	 * @param g The Graphics object to use.
	 */
	public void init(Graphics g) {
		
		// set up size
		int s = JLSInfo.spacing;
		
		if(outputOrientation == JLSInfo.Orientation.LEFT || outputOrientation == JLSInfo.Orientation.RIGHT)
		{
			height = 2*s;
			width = 2*s;
		}
		else
		{
			width = 2*s;
			height = 2*s;
		}
		
		int shiftAmountBits = (int)Math.ceil(Math.log(bits)/Math.log(2));
				
		// create select input
		if(selectorOrientation == JLSInfo.Orientation.DOWN)
		{
			inputs.add(new Input("amount",this,s,height,shiftAmountBits));
		}
		else if(selectorOrientation == JLSInfo.Orientation.UP)
		{
			inputs.add(new Input("amount",this,s,0,shiftAmountBits));
		}
		else if(selectorOrientation == JLSInfo.Orientation.LEFT)
		{
			inputs.add(new Input("amount",this,0,s,shiftAmountBits));
		}
		else if(selectorOrientation == JLSInfo.Orientation.RIGHT)
		{
			inputs.add(new Input("amount",this,width,s,shiftAmountBits));
		}
		
		if(outputOrientation == JLSInfo.Orientation.RIGHT)
		{
			// create inputs
			inputs.add(new Input("input",this,0,s,bits));
			// create output
			outputs.add(new Output("output",this,width,s,bits));
		}
		else if(outputOrientation == JLSInfo.Orientation.LEFT)
		{
			inputs.add(new Input("input",this,width,s,bits));
			// create output
			outputs.add(new Output("output",this,0,s,bits));
		}
		else if(outputOrientation == JLSInfo.Orientation.DOWN)
		{
			// create inputs
			inputs.add(new Input("input",this,s,0,bits));
			// create output
			outputs.add(new Output("output",this,s,height,bits));
		}
		else if(outputOrientation == JLSInfo.Orientation.UP)
		{
			inputs.add(new Input("input",this,s,height,bits));
			// create output
			outputs.add(new Output("output",this,s,0,bits));
		}
		
	} // end of init method
	
	/**
	 * Draw this sr.
	 * 
	 * @param g The graphics object to draw with.
	 */
	public void draw(Graphics g) {
		
		// draw context
		super.draw(g);
		
		// draw shape
//		int s = JLSInfo.spacing;
		g.setColor(Color.black);
		
		// Square 
		g.drawLine(x,y,x+width,y);             // Top
		g.drawLine(x,y+height,x+width,y+height);         // Bottom
		g.drawLine(x,y,x,y+height);              // Left
		g.drawLine(x+width,y,x+width,y+height); // Right	

		// draw inputs and labels
		FontMetrics fm = g.getFontMetrics();
		int ascent = fm.getAscent();
		int hi = ascent + fm.getDescent();
		int d2 = JLSInfo.pointDiameter/2;
		int inum = -1; // first input is selector, so start one too small
		if(outputOrientation == JLSInfo.Orientation.LEFT || outputOrientation == JLSInfo.Orientation.RIGHT)
		{
			for (Input input : inputs) {
				input.draw(g);
				if (inum >= 0) {
					g.setColor(Color.BLACK);
					if(outputOrientation == JLSInfo.Orientation.RIGHT)
					{
						g.drawString("in",x+d2,input.getY()-hi/2+ascent);			
					}
					else if(outputOrientation == JLSInfo.Orientation.LEFT)
					{
						g.drawString("in",x+width-5*d2,input.getY()-hi/2+ascent);
					}
					else if(outputOrientation == JLSInfo.Orientation.UP || outputOrientation == JLSInfo.Orientation.DOWN)
					{
						g.drawString("in",input.getX()-4,y+5*d2);
					}
				} 
				inum += 1;
			}
		}
		if(outputOrientation == JLSInfo.Orientation.UP || outputOrientation == JLSInfo.Orientation.DOWN)
		{
			inputs.get(0).draw(g);
			inputs.get(1).draw(g);
			g.setColor(Color.BLACK);
			g.drawString("in",inputs.get(1).getX()-4,y+5*d2);
		}
		
		// draw output
		outputs.get(0).draw(g);
		
	} // end of draw method

	/**
	 * Set an int instance variable value (during a load).
	 * 
	 * @param name The instance variable name.
	 * @param value The instance variable value.
	 */
	public void setValue(String name, int value) {
		
		if (name.equals("bits")) {
			bits = value;
		} else if (name.equals("delay")) {
			propDelay = value;
		} else {
			super.setValue(name,value);
		}
	} // end of setValue method
	
	/**
	 * Set an int instance variable value (during a load).
	 * 
	 * @param name The instance variable name.
	 * @param value The instance variable value.
	 */
	public void setValue(String name, String value) {
		if (name.equals("type")) {
			type = Type.valueOf(value);
		} else if (name.equals("iOrient")) {
			if(value.equals("LEFT"))
			{
				outputOrientation = JLSInfo.Orientation.LEFT;
			}
			else if(value.equals("RIGHT"))
			{
				outputOrientation = JLSInfo.Orientation.RIGHT;
			}
			else if(value.equals("UP"))
			{
				outputOrientation = JLSInfo.Orientation.UP;
			}
			else if(value.equals("DOWN"))
			{
				outputOrientation = JLSInfo.Orientation.DOWN;
			}
		} 
		else if(name.equals("sOrient"))
		{
			if(value.equals("LEFT"))
			{
				selectorOrientation = JLSInfo.Orientation.LEFT;
			}
			else if(value.equals("RIGHT"))
			{
				selectorOrientation = JLSInfo.Orientation.RIGHT;
			}
			else if(value.equals("UP"))
			{
				selectorOrientation = JLSInfo.Orientation.UP;
			}
			else if(value.equals("DOWN"))
			{
				selectorOrientation = JLSInfo.Orientation.DOWN;
			}
		}
		else 
		{
			super.setValue(name,value);
		}
	} // end of setValue method
	
	/**
	 * Save this element.
	 * 
	 * @param output The output writer.
	 */
	public void save(PrintWriter output) {
		
		output.println("ELEMENT ShiftRegister");
		super.save(output);
		output.println(" String type \"" + type.name() + "\"");
		output.println(" int bits " + bits);
		output.println(" int delay " + propDelay);
		output.println(" String iOrient \"" + outputOrientation.toString() + "\"");
		output.println(" String sOrient \"" + selectorOrientation.toString() + "\"");
		output.println("END");
	} // end of save method

	/**
	 * Copy this element.
	 * 
	 * @return a copy of this element.
	 */
	public Element copy() {
		
		ShiftRegister it = new ShiftRegister(circuit);
		it.bits = bits;
		it.propDelay = propDelay;
		it.outputOrientation = outputOrientation;
		it.selectorOrientation = selectorOrientation;
		it.type = type;
		for (Input input : inputs) {
			it.inputs.add(input.copy(it));
		}
		for (Output output : outputs) {
			it.outputs.add(output.copy(it));
		}
		super.copy(it);
		return it;
	} // end of copy method

	/**
	 * Display info about this element.
	 * 
	 * @param info The JLabel to display with.
	 */
	public void showInfo(JLabel info) {
		
		String kind = null; 
		switch(type) {
		case LogicalLeft:
			kind = "Logical left";
			break;
		case LogicalRight:
			kind = "Logical right";
			break;
		case ArithmeticRight:
			kind = "Arithmetic right";
			break;
				
		default:
			kind = "ERROR";

		}
		info.setText(kind + " shift register (" +  bits + ")");
	} // end of showInfo method

	/**
	 * Multiplexors have timing info (propagation delay).
	 * 
	 * @return true.
	 */
	public boolean hasTiming() {
		
		return true;
	} // end of hasTiming method

	/**
	 * Reset propagation delay to default value.
	 */
	public void resetPropDelay() {
		
		propDelay = defaultPropDelay;
	} // end of resetPropDelay method

	/**
	 * Get the propagation delay in this element.
	 * 
	 * @return the current delay.
	 */
	public int getDelay() {
		
		return propDelay;
	} // end of getDelay method
	
	/**
	 * Set the propagation delay in this element.
	 * 
	 * @param amount The new delay amount.
	 */
	public void setDelay(int temp) {
		
		propDelay = temp;
	} // end of setDelay method
	
	/**
	 * Tells if a SR is capable of flipping, can only flip when inputs or outputs have no attachments.
	 * @return False if any input or output has a wire attached, True otherwise
	 */
	public boolean canFlip()
	{
		boolean success = true;
		for(Input i : inputs)
		{
			if(i.isAttached())
			{
				success = false;
				break;
			}
		}
		for(Output o : outputs)
		{
			if(o.isAttached())
			{
				success = false;
				break;
			}
		}
		return success;
	}
	
	/**
	 * This method will flip a SR's selector
	 * @param g The current graphics context to facilitate recalculation of size when flipping
	 */
	public void flip(Graphics g)
	{
		if(selectorOrientation == JLSInfo.Orientation.LEFT)
		{
			selectorOrientation = JLSInfo.Orientation.RIGHT;
		}
		else if(selectorOrientation == JLSInfo.Orientation.RIGHT)
		{
			selectorOrientation = JLSInfo.Orientation.LEFT;
		}
		else if(selectorOrientation == JLSInfo.Orientation.UP)
		{
			selectorOrientation = JLSInfo.Orientation.DOWN;
		}
		else if(selectorOrientation == JLSInfo.Orientation.DOWN)
		{
			selectorOrientation = JLSInfo.Orientation.UP;
		}
		inputs.clear();
		outputs.clear();
		width = 0;
		height = 0;
		init(g);
	}
	
	/**
	 *  This method will rotate the SR if it is rotateable.
	 * @param direction The direction to rotate
	 * @param g The current graphics context for use in recalculating size
	 */
	public void rotate(JLSInfo.Orientation direction, Graphics g)
	{
		if(direction == JLSInfo.Orientation.LEFT)
		{
			if(selectorOrientation == JLSInfo.Orientation.LEFT)
			{
				selectorOrientation = JLSInfo.Orientation.DOWN;
			}
			else if(selectorOrientation == JLSInfo.Orientation.DOWN)
			{
				selectorOrientation = JLSInfo.Orientation.RIGHT;
			}
			else if(selectorOrientation == JLSInfo.Orientation.RIGHT)
			{
				selectorOrientation = JLSInfo.Orientation.UP;
			}
			else if(selectorOrientation == JLSInfo.Orientation.UP)
			{
				selectorOrientation = JLSInfo.Orientation.LEFT;
			}
			
			if(outputOrientation == JLSInfo.Orientation.LEFT)
			{
				outputOrientation = JLSInfo.Orientation.DOWN;
			}
			else if(outputOrientation == JLSInfo.Orientation.DOWN)
			{
				outputOrientation = JLSInfo.Orientation.RIGHT;
			}
			else if(outputOrientation == JLSInfo.Orientation.RIGHT)
			{
				outputOrientation = JLSInfo.Orientation.UP;
			}
			else if(outputOrientation == JLSInfo.Orientation.UP)
			{
				outputOrientation = JLSInfo.Orientation.LEFT;
			}
			
		}
		else if(direction == JLSInfo.Orientation.RIGHT)
		{
			if(selectorOrientation == JLSInfo.Orientation.LEFT)
			{
				selectorOrientation = JLSInfo.Orientation.UP;
			}
			else if(selectorOrientation == JLSInfo.Orientation.DOWN)
			{
				selectorOrientation = JLSInfo.Orientation.LEFT;
			}
			else if(selectorOrientation == JLSInfo.Orientation.RIGHT)
			{
				selectorOrientation = JLSInfo.Orientation.DOWN;
			}
			else if(selectorOrientation == JLSInfo.Orientation.UP)
			{
				selectorOrientation = JLSInfo.Orientation.RIGHT;
			}
			
			if(outputOrientation == JLSInfo.Orientation.LEFT)
			{
				outputOrientation = JLSInfo.Orientation.UP;
			}
			else if(outputOrientation == JLSInfo.Orientation.DOWN)
			{
				outputOrientation = JLSInfo.Orientation.LEFT;
			}
			else if(outputOrientation == JLSInfo.Orientation.RIGHT)
			{
				outputOrientation = JLSInfo.Orientation.DOWN;
			}
			else if(outputOrientation == JLSInfo.Orientation.UP)
			{
				outputOrientation = JLSInfo.Orientation.RIGHT;
			}
		}
		inputs.clear();
		outputs.clear();
		width = 0;
		height = 0;
		init(g);
	}
	
	/**
	 * Tells if a SR is capable of rotatating, can only rotate when inputs or outputs have no attachments.
	 * @return False if any input or output has a wire attached, True otherwise
	 */
	public boolean canRotate()
	{
		boolean success = true;
		for(Input i : inputs)
		{
			if(i.isAttached())
			{
				success = false;
				break;
			}
		}
		for(Output o : outputs)
		{
			if(o.isAttached())
			{
				success = false;
				break;
			}
		}
		return success;
	}

	/**
	 * Dialog box to set inputs and bits.
	 */
	protected class SRCreate extends JDialog implements ActionListener {
		
		private static final long serialVersionUID = 5283714377285864030L;
		// properties
		private JButton ok = new JButton("OK");
		private JButton cancel = new JButton("Cancel");
		private JTextField bitsField = new JTextField(defaultBits+"",5);
		private KeyPad bitsPad = new KeyPad(bitsField,10,defaultBits,this);
		
		
		private JRadioButton oLeft = new JRadioButton("Left");
		private JRadioButton oRight = new JRadioButton("Right", true);
		private JRadioButton oUp = new JRadioButton("Up");
		private JRadioButton oDown = new JRadioButton("Down");
		private JRadioButton sLeft = new JRadioButton("Left");
		private JRadioButton sRight = new JRadioButton("Right");
		private JRadioButton sUp = new JRadioButton("Up");
		private JRadioButton sDown = new JRadioButton("Down",true);
		
		private JRadioButton shiftLeft = new JRadioButton("Shift Left",true);
		private JRadioButton shiftRight = new JRadioButton("Shift Right",true);
		private JRadioButton shiftRightArithmetic = new JRadioButton("Shift Right Arithmetic",true);
		
		
		private JLabel olbl2 = new JLabel("Amount Orientation");
		
		/**
		 * Set up dialog window.
		 * 
		 * @param x The x-coordinate of the position of the dialog.
		 * @param y The y-coordinate of the position of the dialog.
		 */
		protected SRCreate(int x, int y) {
			
			// set up window title
			super(JLSInfo.frame,"Create Shift Register",true);
			
			// set not cancelled
			cancelled = false;
			
			// set up window
			Container window = getContentPane();
			window.setLayout(new BoxLayout(window,BoxLayout.Y_AXIS));
			
			// set up input panel
			JPanel info = null;
			info = new JPanel(new GridLayout(1,2));
			
			JLabel gates;
			gates = new JLabel("Bits: ",SwingConstants.RIGHT);
			info.add(gates);
			JPanel ga = new JPanel(new FlowLayout());
			ga.add(bitsField);
			ga.add(bitsPad);
			info.add(ga);
			window.add(info);

			JPanel typePanel = new JPanel(new GridLayout(3,1));
			ButtonGroup typeButtons = new ButtonGroup();
			typeButtons.add(this.shiftLeft);
			typeButtons.add(this.shiftRight);
			typeButtons.add(this.shiftRightArithmetic);
			JLabel tlbl = new JLabel("Shift Type");
			tlbl.setAlignmentX(Component.CENTER_ALIGNMENT);
			window.add(tlbl);
			typePanel.add(this.shiftLeft);
			typePanel.add(this.shiftRight);
			typePanel.add(this.shiftRightArithmetic);

			typePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
			JPanel containingPanel = new JPanel();
			containingPanel.add(typePanel);
			containingPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
			window.add(containingPanel);

			JPanel orient = new JPanel(new GridLayout(3,3));
			JPanel orient2 = new JPanel(new GridLayout(3,3));
			ButtonGroup gr = new ButtonGroup();
			ButtonGroup gr2 = new ButtonGroup();
			gr.add(this.oLeft);
			gr.add(this.oRight);
			gr.add(this.oDown);
			gr.add(this.oUp);
			gr2.add(this.sDown);
			gr2.add(this.sUp);
			gr2.add(this.sLeft);
			gr2.add(this.sRight);
			orient.add(new JLabel(""));
			orient.add(this.oUp);
			orient.add(new JLabel(""));
			orient.add(this.oLeft);
			orient.add(new JLabel(""));
			orient.add(this.oRight);
			orient.add(new JLabel(""));
			orient.add(this.oDown);
			orient.add(new JLabel(""));
			
			orient2.add(new JLabel(""));
			orient2.add(this.sUp);
			orient2.add(new JLabel(""));
			orient2.add(this.sLeft);
			orient2.add(new JLabel(""));
			orient2.add(this.sRight);
			orient2.add(new JLabel(""));
			orient2.add(this.sDown);
			orient2.add(new JLabel(""));
			
			JLabel olbl = new JLabel("Output Orientation");
			olbl.setAlignmentX(Component.CENTER_ALIGNMENT);
			window.add(olbl);
			window.add(orient);

			olbl2.setAlignmentX(Component.CENTER_ALIGNMENT);
			window.add(olbl2);
			window.add(orient2);
			
			sLeft.setVisible(false);
			sRight.setVisible(false);
			
			// set up ok and cancel buttons
			window.add(new JLabel(" "));
			JPanel okCancel = new JPanel(new GridLayout(1,2));
			ok.setBackground(Color.green);
			okCancel.add(ok);
			cancel.setBackground(Color.pink);
			okCancel.add(cancel);
			JButton help = new JButton("Help");
			if (JLSInfo.hb == null)
				Util.noHelp(help);
			else
				// TODO: Add help stuff
				JLSInfo.hb.enableHelpOnButton(help,"shiftregister",null);
			okCancel.add(help);
			window.add(okCancel);
			getRootPane().setDefaultButton(ok);
			
			ok.addActionListener(this);
			bitsField.addActionListener(this);
			cancel.addActionListener(this);
			oLeft.addActionListener(this);
			oRight.addActionListener(this);
			oUp.addActionListener(this);
			oDown.addActionListener(this);
			
			// set up window close listener to cancel SR
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			addWindowListener (
					new WindowAdapter() {
						public void windowClosing(WindowEvent e) {
							cancel();
						}
					}
			);
			
			// finish up GUI
			pack();
			Dimension d = getSize();
			setLocation(x-d.width/2,y-d.height/2);
			setVisible(true);
		} // end of constructor
		
		/**
		 * React to ok and cancel buttons.
		 * 
		 * @param event The event object for this action.
		 */
		public void actionPerformed(ActionEvent event) {
			
			if(event.getSource() == oLeft || event.getSource() == oRight)
			{
				olbl2.setVisible(true);
				sUp.setVisible(true);
				sDown.setVisible(true);
				sDown.setSelected(true);
				sLeft.setVisible(false);
				sRight.setVisible(false);
				return;
			}
			else if(event.getSource() == oUp || event.getSource() == oDown)
			{
				olbl2.setVisible(true);
				sLeft.setVisible(true);
				sLeft.setSelected(true);
				sRight.setVisible(true);
				sUp.setVisible(false);
				sDown.setVisible(false);
				return;
			}
			
			// if ok button, check values for validity
			if (event.getSource() == ok ||  event.getSource() == bitsField) {
				try {
					bits = Integer.parseInt(bitsField.getText());
				}
				catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(this,
							"Value not numeric, try again", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (bits < 2) {
					JOptionPane.showMessageDialog(this,
							"Must be at least two bits", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(this.shiftRight.isSelected()) {
					type = ShiftRegister.Type.LogicalRight;
				} else if(this.shiftRightArithmetic.isSelected()){
					type = ShiftRegister.Type.ArithmeticRight;
				} else {
					type = ShiftRegister.Type.LogicalLeft;					
				}
				if(this.oLeft.isSelected())
				{
					outputOrientation = JLSInfo.Orientation.LEFT;
					if(this.sUp.isSelected())
					{
						selectorOrientation = JLSInfo.Orientation.UP;
					}
					else if(this.sDown.isSelected())
					{
						selectorOrientation = JLSInfo.Orientation.DOWN;
					}
				}
				else if(this.oRight.isSelected())
				{
					outputOrientation = JLSInfo.Orientation.RIGHT;
					if(this.sUp.isSelected())
					{
						selectorOrientation = JLSInfo.Orientation.UP;
					}
					else if(this.sDown.isSelected())
					{
						selectorOrientation = JLSInfo.Orientation.DOWN;
					}
				}
				else if(this.oDown.isSelected())
				{
					outputOrientation = JLSInfo.Orientation.DOWN;
					if(this.sLeft.isSelected())
					{
						selectorOrientation = JLSInfo.Orientation.LEFT;
					}
					else if(this.sRight.isSelected())
					{
						selectorOrientation = JLSInfo.Orientation.RIGHT;
					}
				}
				else if(this.oUp.isSelected())
				{
					outputOrientation = JLSInfo.Orientation.UP;
					if(this.sLeft.isSelected())
					{
						selectorOrientation = JLSInfo.Orientation.LEFT;
					}
					else if(this.sRight.isSelected())
					{
						selectorOrientation = JLSInfo.Orientation.RIGHT;
					}
				}
				bitsPad.close();
				dispose();
			}
			
			// if cancel button, cancel sr creation
			else if (event.getSource() == cancel) {
				cancel();
			}
			
		} // end of actionPerformed method
		
		/**
		 * Cancel this sr.
		 */
		private void cancel() {
			
			cancelled = true;
			bitsPad.close();
			dispose();
		} // end of cancel method
		
	} // end of ShiftRegister Create class
	

//	-------------------------------------------------------------------------------
//	Simulation
//	-------------------------------------------------------------------------------
	
	private BitSet toBeValue;
	
	/**
	 * Initialize this element by setting its output and to-be value to 0.
	 * 
	 * @param sim Unused.
	 */
	public void initSim(Simulator sim) {
		
		// set outputs to 0
		BitSet zero = new BitSet(1);
		outputs.get(0).setValue(zero);
		
		// set to-be value
		toBeValue = (BitSet)zero.clone();
	} // end of initSim method
	
	/**
	 * React to an event.
	 * 
	 * @param now The current simulation time.
	 * @param sim The simulator to post events to.
	 * @param todo Null if an input change, the new output value otherwise.
	 */
	public void react(long now, Simulator sim, Object todo) {
		
		// if an input has changed ...
		if (todo == null) {
			
			// get the amount to shift
			BitSet bw = inputs.get(0).getValue();
			if (bw == null)
				bw = new BitSet();
			int amount = BitSetUtils.ToInt(bw);
			
			// Get the input value too
			BitSet input = inputs.get(1).getValue();
			BitSet newValue = new BitSet(bits);

			switch(type) {
			case LogicalLeft:
				for(int i=bits-1; i>=0;i--) {
					if(i-amount>=0) {
						newValue.set(i,input.get(i-amount));
					} 
				}
				break;
			case LogicalRight:
				for(int i=0; i<bits;i++) {
					if(i+amount<bits) {
						newValue.set(i,input.get(i+amount));
					} 
				}
				break;
			case ArithmeticRight:
				for(int i=0; i<bits;i++) {
					if(i+amount<bits) {
						newValue.set(i,input.get(i+amount));
					} else {
						// Copy sign bit
						newValue.set(i,input.get(bits-1));						
					}
				}
			}
			
			
			// if new value is different from the value propagating through
			// the SR, then post an event
			if (!newValue.equals(toBeValue)) {
				toBeValue = (BitSet)newValue.clone();
				sim.post(new SimEvent(now+propDelay,this,newValue));
			}
		}
		else {
			
			// get the new output value
			BitSet value = (BitSet)todo;
			
			// send to output
			Output srOut = outputs.get(0);
			srOut.propagate(value,now,sim);
		}
		
	} // end of react method

} // end of SR class
