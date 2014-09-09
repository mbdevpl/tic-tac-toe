package mb.tictactoe;

import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mb.graphics.GridBagPane;
import mb.tictactoe.data.Participant;

/**
 * Simple pane used to do some per-participant operations, or just for displaying the list of them. 
 * @author mb
 */
@SuppressWarnings("serial")
public class GameParticipants extends GridBagPane implements ListSelectionListener {
	private JList partList;
	private DefaultListModel partListModel;
	private volatile ArrayList<Participant> participants;
	
	public GameParticipants(ArrayList<Participant> participants, ActionListener alist, String actionDescr) {
		super("participantsList", alist);
		this.participants = participants;
		this.partListModel = new DefaultListModel();
		for(Participant p: participants)
			partListModel.addElement(p.toNiceString());
		this.partList = new JList(partListModel);
		
		partList.setFont(font);
		
		gb.gridwidth = GridBagConstraints.REMAINDER;
		gb.fill = GridBagConstraints.BOTH;
		gb.weightx = 1;
		gb.weighty = 1;
		this.addComponent(partList);

		gb.weightx = 0;
		gb.weighty = 0;
		gb.gridwidth = GridBagConstraints.RELATIVE;
		gb.fill = GridBagConstraints.HORIZONTAL;
		this.addButton(actionDescr);
		if(!actionDescr.equals("Ok")) {
			gb.gridwidth = GridBagConstraints.REMAINDER;
			this.addButton("Cancel");
		}
	}
	
	public ArrayList<Participant> getSelectedParticipants() {
		if(partList.getSelectedIndex() == -1)
			return null;
		
		ArrayList<Participant> selected = new ArrayList<Participant>();
		for(int i: partList.getSelectedIndices())
			selected.add(participants.get(i));
		
		return selected;
	}

	@Override
	public void valueChanged(ListSelectionEvent s) {
		//int i = s.getFirstIndex();
	}
}
