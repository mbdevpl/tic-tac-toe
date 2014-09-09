package mb.graphics;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Superclass for any pane used in TicTacToe.
 * @author mb
 */
public abstract class GridBagPane extends JLayeredPane {
	/**
	 * ID.
	 */
	private static final long serialVersionUID = 4090084967273453871L;
	/**
	 * When any non-menu component is added to the pane, reference to it is added to this list.
	 */
	protected ArrayList<JComponent> components;
	/**
	 * Short-named variable to use in the code. 
	 */
	protected GridBagConstraints gb;
	/**
	 * Short-name variable to use in the code.
	 */
	protected Dimension dim;
	/**
	 * Reference to the content pane of this frame.
	 */
	protected Container cp;
	
	protected ActionListener al;
	
	protected Font font;// = new Font("default", Font.PLAIN, this.getFont().getSize());
	
	/**
	 * Constructs a GridBagPane. Constructors in all subclasses must call this.
	 * @param name name of the pane
	 */
	public GridBagPane(String name) {
		super();
		this.setName(name);
		//this.setBounds(x, y, width, height);
		components = new ArrayList<JComponent>();
		dim = new Dimension();
		font = this.getFont();//  = new Font("default", Font.PLAIN, this.getFont().getSize());
		//GridBag
		this.setLayout(new GridBagLayout());
		gb = new GridBagConstraints();
		cp = this;
	}
	
	public GridBagPane(String name, ActionListener al) {
		this(name);
		this.al = al;
	}
	
	/**
	 * Finds the first component in this frame whose name ends with the given string. 
	 * @param name suffix of the component's name
	 * @return null if no such component was found
	 */
	protected JComponent getByName(String name) {
		JComponent comp = null;
		for(JComponent c: components)
			if(c.getName().endsWith(name)) {
				comp = c;
				break;
			}
		return comp;
	}
	/**
	 * Finds the last (chronologically) component of a given type.
	 * @param type name of the class of the component
	 * @return the component, or null if there was no component of the given type
	 */
	protected JComponent getLastByType(String type) {
		JComponent comp = null;
		for(int i = components.size() - 1; i >= 0; i--) {
			JComponent c = components.get(i);
			if(c.getClass().getSimpleName().equals(type)) {
				comp = c;
				break;
			}
		}
		return comp;
	}
	
	/**
	 * Constructs a name for new component that is just to be added to the frame.
	 * Good for frames with contents that are static i.e. not deleted after creation. 
	 * @param c reference to that component
	 * @return a default name: [simple name of class] + " " + [number of components already in the list] + " " 
	 */
	private String getNewName(JComponent c) {
		return c.getClass().getSimpleName() + " " + components.size() + " ";
	}
	
	/**
	 * Adds the given component to the components list as well as to the content pane of the frame.
	 * @param c the component to be added
	 */
	protected void addComponent(JComponent c) {
		components.add(c);
		add(c, gb);
		//System.out.println("Added: " + c.getName());
	}

	/**
	 * Adds a JTextField to the frame.
	 * @param value initial value of the field
	 * @return created field
	 */
	protected JTextField addTextField(String value) {
		JTextField field = new JTextField(value);
		field.setName(getNewName(field) + value);
		addComponent(field);
		return field;
	}
	/**
	 * Gets a component that ends with given string, and casts it to JTextField before return.
	 * @param name suffix of the name
	 * @return that field
	 */
	protected JTextField getAsTextField(String name) {
		return (JTextField)getByName(name);
	}
	/**
	 * Gets the last (chronologically) JTextField in this frame.
	 * @return this field 
	 */
	protected JTextField getLastTextField() {
		return (JTextField)getLastByType("JTextField");
	}
	
	/**
	 * Adds a JTextArea to the frame.
	 * @param value initial value of the field
	 * @return created field
	 */
	protected JTextArea addTextArea(String value) {
		JTextArea field = new JTextArea(value);
		field.setName(getNewName(field) + value);
		addComponent(field);
		return field;
	}
	/**
	 * Gets the last (chronologically) JTextField in this frame.
	 * @return this field 
	 */
	protected JTextArea getLastTextArea() {
		return (JTextArea)getLastByType("JTextArea");
	}
	
	protected JLabel addLabel(String text) {
		JLabel l = new JLabel(text);
		l.setName(getNewName(l) + text);
		addComponent(l);
		return l;
	}
//	protected JLabel getAsLabel(String name) {
//		return (JLabel)getByName(name);
//	}
	protected JLabel getLastLabel() {
		return (JLabel)getLastByType("JLabel");
	}
	
	protected JButton addButton(String title) {
		return this.addButton(title, al);
	}
	
	protected JButton addButton(String title, ActionListener a) {
		JButton button = new JButton(title);
		if(a != null) button.addActionListener(a);
		addComponent(button);
		return button;
	}
}
