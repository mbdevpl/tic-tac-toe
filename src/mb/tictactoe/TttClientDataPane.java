package mb.tictactoe;

import java.awt.GridBagConstraints;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;

import mb.graphics.GridBagPane;
import mb.tictactoe.data.Participant;
import mb.web.XmlTree;

/**
 * Pane with places for login and password as well as other settings that user enters before connection.
 * @author mb
 */
@SuppressWarnings("serial")
public class TttClientDataPane extends GridBagPane {
	private JRadioButton plP, plO;
	private JCheckBox create;
	
	public TttClientDataPane(String filePath) {
		super("clientdata");
		
		XmlTree tree = null;
		try {
			tree = XmlTree.createFromFile(filePath);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		

		gb.gridwidth = GridBagConstraints.REMAINDER;
		gb.fill = GridBagConstraints.HORIZONTAL;
		addLabel("Participant data");
		
		gb.fill = GridBagConstraints.HORIZONTAL;
		gb.gridwidth = 1;
		gb.weightx = 0;
		addLabel("Nickname: ").setFont(font);
		gb.gridwidth = GridBagConstraints.REMAINDER;
		gb.weightx = 1;
		addTextField("nickname").setText("");
		if(tree!=null) getLastTextField().setText(tree.getValue());
		
		gb.fill = GridBagConstraints.HORIZONTAL;
		gb.gridwidth = 1;
		gb.weightx = 0;
		addLabel("Password: ").setFont(font);
		gb.gridwidth = GridBagConstraints.REMAINDER;
		//gb.weightx = 1;
		JPasswordField pass = new JPasswordField();
		pass.setName("password");
		pass.setText("");
		this.addComponent(pass);
		if(tree!=null) pass.setText(tree.getAttr("password"));
		
		gb.gridwidth = 1;
		addLabel("Create account: ").setFont(font);
		create = new JCheckBox("");
		this.addComponent(create);
		/*if(tree!=null)
			if(tree.getAttr("createnew").equals("true"))
				create.setSelected(true);*/
		gb.gridwidth = GridBagConstraints.REMAINDER;
		addLabel(" (if it does not exist)").setFont(font);
		
		gb.gridwidth = 1;
		gb.weightx = 0;
		addLabel("Join as: ").setFont(font);
		gb.gridwidth = GridBagConstraints.REMAINDER;
		plP = new JRadioButton("player", true);
		plP.setFont(font);
		this.addComponent(plP);
		gb.gridx = 1;
		plO = new JRadioButton("observer", false);
		plO.setFont(font);
		this.addComponent(plO);
		if(tree!=null)
			if(Integer.parseInt(tree.getAttr("kind")) == Participant.PLAYER)
				plP.setSelected(true);
			else 
				plO.setSelected(true);
		
		ButtonGroup buttonGr = new ButtonGroup();
		buttonGr.add(plP);
		buttonGr.add(plO);
		//addTextField("radio will be here");//.setText("");
		
		if(tree == null) {
			this.saveTo(filePath);
		}
	}

	public String getNickname() {
		return getAsTextField("nickname").getText();
	}

	public String getPassword() {
		return getAsTextField("password").getText();
	}

	public int getKind() {
		return (plP.isSelected() ? Participant.PLAYER : Participant.OBSERVER);
	}

	public boolean createNew() {
		return create.isSelected();
	}

	public void saveTo(String filePath) {
		XmlTree tree = new XmlTree();
		tree.setDtdURL("clientdata");
		tree.setName("clientdata");
		tree.setValue(getNickname());
		tree.addAttribute("password", getPassword());
		//tree.addAttribute("createnew", String.valueOf(createNew()));
		tree.addAttribute("kind", String.valueOf(getKind()));
		tree.saveToFile(filePath);
	}

}
