package mb.tictactoe.data;

import mb.web.XmlTree;

/**
 * Non-abstract class used to store side that the player is on.
 * @author mb
 */
public class PlayerSide extends CrossOrNought {
	public PlayerSide() {
		super(1);
	}
	
	public PlayerSide(char symbol) {
		super(symbol);
	}
	
	public PlayerSide(int number) {
		super(number);
	}
	
	public PlayerSide(PlayerSide side) {
		super(side);
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof PlayerSide)
			return ( ( (PlayerSide)o ).getState() == getState() );
		else return false;
	}
	
	@Override
	public XmlTree toXmlTree() {
		throw new NullPointerException("Player side does not implement Xmlable yet.");
		//return null;
	}
}
