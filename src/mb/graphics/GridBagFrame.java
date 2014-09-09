package mb.graphics;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/**
 * GridBagFrame is an extension of JFrame that uses GridBagLayout to place components.
 * @author Mateusz Bysiek, mb@mbdev.pl, http://mbdev.pl/
 */
public abstract class GridBagFrame extends JFrame implements SwingConstants, ActionListener, KeyListener, MouseListener, WindowListener {
	/**
	 * ID.
	 */
	private static final long serialVersionUID = 6311434719088210631L;
	/**
	 * When any non-menu component is added to the frame, reference to it is added to this list.
	 */
	protected ArrayList<JComponent> components;
	/**
	 * Menu bar of the frame, may be null when you don't need any menu bar.
	 */
	private JMenuBar menuBar;
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
	protected Font font;
	
	/**
	 * Constructs a GridBagFrame. Constructors in all subclasses must call this.
	 * @param title title of the frame
	 * @param x position on the screen
	 * @param y position on the screen
	 * @param width width of the frame
	 * @param height height of the frame
	 */
	public GridBagFrame(String title, int x, int y, int width, int height) {
		super();
		this.frameInit();
		this.setTitle(title);
		this.setBounds(x, y, width, height);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.addWindowListener(this);
		components = new ArrayList<JComponent>();
		dim = new Dimension();
		//GridBag
		this.setLayout(new GridBagLayout());
		gb = new GridBagConstraints();
		cp = this.getContentPane();
		font = this.getFont();// = new Font("default", Font.PLAIN, this.getFont().getSize());
	}
	
	/**
	 * This method should be invoked instead the first setVisible(true).
	 * This method should be run not before the end of constructor.
	 */
	public void launch() {
		this.validate();
		this.setVisible(true);
	}
	/**
	 * Shows a dialog connected to this frame.
	 * @param message message to be displayed
	 */
	public void launchInfoDialog(String title, String message) {
		//JOptionPane.showMessageDialog(this, message);
		JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
	/**
	 * Shows a question connected to this frame.
	 * @param message message to be displayed
	 */
	public void launchQuestionDialog(String title, String message) {
		int answer = JOptionPane.showOptionDialog(this, message, title, JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, null, null);
		
		if(answer == JOptionPane.YES_OPTION)
			runCommand("dialogYes");
		else
			runCommand("dialogNo");
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
	 * Adds all components from the array to the frame.
	 * @param components
	 */
	protected void addAllComponents(JComponent[] components) {
		for(int i = 0; i < components.length; i++)
			addComponent(components[i]);
	}
	
	/**
	 * Adds menu bar to the frame, works only once.
	 */
	private void addMenuBar() {
		if(menuBar == null) {
			JMenuBar bar = new JMenuBar();
			menuBar = bar;
			this.setJMenuBar(bar);
		}
	}
	/**
	 * Adds menu to the frame. If there is no menu bar, it is created and then the menu is added to it.
	 * @param title
	 * @return reference to the menu
	 */
	protected JMenu addMenu(String title) {
		JMenu menu = new JMenu(title);
		//addComponent(menu);
		if(menuBar == null) addMenuBar();
		menuBar.add(menu);
		return menu;
	}
	/**
	 * Gets the last (chronologically) menu.
	 * @return the menu, or null if no menu bar or no menu exist
	 */
	protected JMenu getLastMenu() {
		if(menuBar == null || menuBar.getMenuCount() == 0) return null;
		return menuBar.getMenu(menuBar.getMenuCount() - 1);
	}
	/**
	 * Adds a menu item to a specified menu.
	 * @param menu item is added to this menu
	 * @param text
	 * @param al action listener
	 * @return reference to the item
	 */
	protected JMenuItem addMenuItem(JMenu menu, String text, ActionListener al) {
		JMenuItem item = new JMenuItem(text);
		item.addActionListener(this);
		if(al != null) item.addActionListener(al);
		menu.add(item);
		return item;
	}
	/**
	 * Adds menu item to the last (rightmost) menu.
	 * @param text
	 * @param al
	 * @return reference to the added item
	 */
	protected JMenuItem addMenuItem(String text, ActionListener al) {
		return addMenuItem(getLastMenu(), text, al);
	}
	/**
	 * Adds new menu item to the last (rightmost) menu. It does not connect any extra listener to the item.
	 * @param text text on the item
	 * @return reference to the created object
	 */
	protected JMenuItem addMenuItem(String text) {
		return addMenuItem(text, null);
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
		JTextArea area = new JTextArea(value);
		area.setName(getNewName(area) + value);
		addComponent(area);
		return area;
	}
	/**
	 * Gets a component that ends with given string, and casts it to JTextField before return.
	 * @param name suffix of the name
	 * @return that field
	 */
	protected JTextArea getAsTextArea(String name) {
		return (JTextArea)getByName(name);
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
	protected JLabel getAsLabel(String name) {
		return (JLabel)getByName(name);
	}
	protected JLabel getLastLabel() {
		return (JLabel)getLastByType("JLabel");
	}
	
	protected JButton addButton(String title, ActionListener a) {
		JButton button = new JButton(title);
		if(a != null) button.addActionListener(a);
		addComponent(button);
		return button;
	}
	protected JButton addButton(String title) {
		return addButton(title, this);
	}
	
	/**
	 * Member of ActionListener interface.
	 */
	@Override
	public void actionPerformed(ActionEvent a) {
		String cmd = a.getActionCommand();
		runCommand(cmd);
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		runCommand("keyPressed_" + e.getKeyChar());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		runCommand("keyReleased_" + e.getKeyChar());
	}

	@Override
	public void keyTyped(KeyEvent e) {
		runCommand(String.valueOf(e.getKeyChar()));
	}
	
	@Override
	public void mouseClicked(MouseEvent me) {
		//
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {
		//
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		//
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		//
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		//
	}
	
	private void windowActionPerformed(WindowEvent w) {
		String cmd = w.paramString();//getActionCommand();
		runCommand(cmd);
	}
	
	@Override
	public void windowActivated(WindowEvent w) {
		//windowActionPerformed(w);
		runCommand("frameActivated");
	}
	
	@Override
	public void windowClosed(WindowEvent w) {
		//windowActionPerformed(w);
		runCommand("frameClosed");
	}

	@Override
	public void windowClosing(WindowEvent w) {
		//windowActionPerformed(w);
		runCommand("frameClosing");
	}

	@Override
	public void windowDeactivated(WindowEvent w) {
		//windowActionPerformed(w);
		runCommand("frameDeactivated");
	}

	@Override
	public void windowDeiconified(WindowEvent w) {
		windowActionPerformed(w);
	}

	@Override
	public void windowIconified(WindowEvent w) {
		windowActionPerformed(w);
	}

	@Override
	public void windowOpened(WindowEvent w) {
		//windowActionPerformed(w);
		runCommand("frameOpened");
	}
	
	/**
	 * Runs actions associated with a given command.
	 * @param cmd command action from ActionEvent
	 */
	protected abstract void runCommand(String cmd);
	
	//old
	
	public JPanel addPanel(String title, JComponent[] components) {
		JPanel panel = new JPanel(null);
		panel.setName(title);
		for(JComponent c: components)
			panel.add(c);
		addComponent(panel);
		return panel;
	}

	public JTabbedPane addTabs() {
		JTabbedPane pane = new JTabbedPane(JTabbedPane.TOP);
		addComponent(pane);
		return pane;
	}
	
	public JTabbedPane addTabs(JComponent[] tabs) {
		JTabbedPane pane = addTabs();
		for(JComponent t: tabs)
			pane.addTab(t.getName(), t);
		return pane;
	}
	
	public JTabbedPane addTabs(int tabPlacement, int width, int height) {
		JTabbedPane pane = addTabs();
		pane.setTabPlacement(tabPlacement);
		pane.setSize(width, height);
		return pane;
	}
}
