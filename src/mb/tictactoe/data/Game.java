package mb.tictactoe.data;

import java.util.ArrayList;

import mb.tictactoe.Ttt;
import mb.web.WebThreadServer;
import mb.web.WebThreadServerClient;
import mb.web.XmlTree;
import mb.web.Xmlable;

/**
 * Contains all data about one game.
 * @author mb
 */
public class Game extends Xmlable {
	String servername;
	
	private Board board;
	private volatile ArrayList<Participant> participants;
	private ArrayList<ChatMsg> chat;
	
	private Participant currPlayer = null;
	private Participant nextPlayer = null;
	
	private Participant p1 = null;
	private Participant p2 = null;
	
	public Game(String servername, Board board) {
		this.servername = servername;
		this.setBoard(board);
		participants = new ArrayList<Participant>();
		chat = new ArrayList<ChatMsg>();
		chat.add(new ChatMsg(123, "CLIENT", "test", "hello world"));
	}
	
	public Game(XmlTree tree) {
		//System.out.print("GameTree received: ");
		//System.out.println(tree.toXmlString());
		//System.out.println(tree.toFullString());
		//tree.saveToFile("Ttt_receivedGame.xml");
		
		servername = tree.getAttr("servername");
		setBoard(new Board(tree.getSubNode("board", 0)));

		participants = new ArrayList<Participant>();
		int i = 0;
		XmlTree subTree = null;
		do {
			try {
				subTree = tree.getSubNode("participant", i);
			} catch (Exception e) {
				break;
			}
			participants.add(new Participant(subTree));
			i++;
		} while(true);

		chat = new ArrayList<ChatMsg>();
		i = 0;
		subTree = null;
		do {
			try {
				subTree = tree.getSubNode("chatmsg", i);
			} catch (Exception e) {
				break;
			}
			chat.add(new ChatMsg(subTree));
			i++;
		} while(true);
		
	}

	public boolean addParticipant(Participant p) {
		for(Participant participant: participants)
			if(participant.getName().equals(p.getName()))
				return false;
		if(participants.add(p)) {
			if(p1 == null && p.getKind() == Participant.PLAYER) {
				p1 = p;
				p.setSide(new PlayerSide('x'));
				
				if(board.isEmpty())
					currPlayer = p;
				else if(board.getCurrPlayer() == 1)
					currPlayer = p;
				else
					nextPlayer = p;
				
			} else if(p2 == null && p.getKind() == Participant.PLAYER) {
				p2 = p;
				p.setSide(new PlayerSide('o'));
				
				if(board.isEmpty())
					nextPlayer = p;
				else if(board.getCurrPlayer() == 2)
					currPlayer = p;
				else
					nextPlayer = p;
			}
			return true;
		}
		return false;
	}
	
	public void removeParticipant(int webId) {
		//System.out.println("I'm trying, man!!!");
		for(Participant p: participants)
			if(p.getTempId() == webId) {
				if(p1 != null && p1.equals(p)) p1 = null;
				if(p2 != null && p2.equals(p)) p2 = null;
				if(currPlayer != null && currPlayer.equals(p)) currPlayer = null;
				if(nextPlayer != null && nextPlayer.equals(p)) nextPlayer = null;
				participants.remove(p);
				//System.out.println("participant was removed from the game");
				break;
			}
	}
	
	public boolean syncParticipantList(ArrayList<WebThreadServerClient> clients) {
		boolean actionsWere = false;
		//removing participants that are disconnected
		for(Participant p: participants) {
			String name = p.getName();
			boolean isOnline = false;
			for(WebThreadServerClient c: clients) {
				if(c.participant.getName().equals(name))
					isOnline = true;
			}
			if(!isOnline) {
				participants.remove(p);
				actionsWere = true;
			}
		}
		
		return actionsWere;	
	}
	
	public boolean isPlayable() {
		return (currPlayer != null && nextPlayer != null);
	}
	
	public Participant getCurrPlayer() {
		return currPlayer;
	}
	
	@Override
	public XmlTree toXmlTree() {
		XmlTree tree = new XmlTree();
		tree.setDtdURL("http://mbdev.pl/files/tictactoe/game.dtd");
		tree.setName("game");
		tree.addAttribute("servername", servername);
		
		XmlTree bTree = getBoard().toXmlTree();
		bTree.setDtdURL("");
		tree.addSubNode(bTree);
		
		for(Participant p: participants) {
			XmlTree pTree = p.toXmlTree();
			pTree.removeAttr("password");
			pTree.setDtdURL("");
			tree.addSubNode(pTree);
		}
		for(ChatMsg cm: chat) {
			XmlTree cTree = cm.toXmlTree();
			cTree.setDtdURL("");
			tree.addSubNode(cTree);
		}
		
		//System.out.println(tree);
		//System.out.println("GameTree created: " + tree.toXmlString());
		//tree.saveToFile("Ttt_createdGame.xml");
		
		return tree;
	}

	@Override
	public String toString() {
		return "Game=[" + this.servername + ", " + this.getParticipants() + " ]";
	}

	public ArrayList<Participant> getParticipants() {
		return participants;
	}

	public Participant getPlayer(int number) {
		if(number == 1) return p1;
		else if(number == 2) return p2;
		else return null;
	}
	
	public boolean addMove(CellData cd) {
		int x = cd.getX();
		int y = cd.getY();
		if(getBoard().elementAt(x, y).getState() == 0) {
			getBoard().set(x, y, new CellState(cd.getState()));
			this.nextTurn();
			return true;
		}
		
		return false;
	}

	private void nextTurn() {
		Participant temp = currPlayer;
		currPlayer = nextPlayer;
		nextPlayer = temp;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public Board getBoard() {
		return board;
	}
	
	/**
	 * Initializes the game once more, as if the game was to be played again from the beginning, 
	 * in the same conditions. 
	 */
	public void reInit() {
		board.fillWith(new CellState(' '));
		currPlayer = p1;
		nextPlayer = p2;
	}

	public boolean addChatEntry(ChatMsg chatMsg) {
		return chat.add(chatMsg);
	}
	
	public boolean isOver() {
		return Ttt.gameOver(board);
	}

	
	public void refreshRemoteData(WebThreadServer server) {
		if(p1 == null || p2 == null) {
			server.sendToAll(new SystemMsg("turnend"));
			server.sendToAll(new SystemMsg("notplayable"));

			server.sendToAll(new SystemMsg("status=A player left, game is suspended."));
		}
	}

}
