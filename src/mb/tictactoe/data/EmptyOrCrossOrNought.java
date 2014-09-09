package mb.tictactoe.data;

import mb.web.Xmlable;

/**
 * Abstract superclass for objects that can be 'x', 'o' or ' ', like fields in game board and such.
 * @author mb
 */
public abstract class EmptyOrCrossOrNought extends Xmlable {
	/**
	 * 0, 1 or 2.
	 */
	protected int state = 0;
	
	/**
	 * Constructs new object from a char.
	 * @param symbol ' ' (space), 'x' or 'o' (lower-case or upper-case)
	 * @throws IllegalArgumentException when argument is out of the scope
	 */
	public EmptyOrCrossOrNought(char symbol) {
		if(symbol == ' ') state = 0;
		else if(symbol == 'x' || symbol =='X') state = 1;
		else if(symbol == 'o' || symbol =='O') state = 2;
		else throw new IllegalArgumentException("symbol '" + symbol + "' is outside of the scope that is ' ', 'X' or 'O'");
	}
	
	/**
	 * Constructs new object from an int.
	 * @param number 0, 1 or 2
	 * @throws IllegalArgumentException when argument is out of the scope
	 */
	public EmptyOrCrossOrNought(int number) {
		if(number != 0 && number != 1 && number != 2)
			throw new IllegalArgumentException("number " + String.valueOf(number) + " is outside of the scope, which is 0, 1 or 2");
		state = number;
	}
	
	/**
	 * Copy constructor.
	 * @param ecn source of data
	 * @throws IllegalArgumentException when argument's state is out of the scope
	 */
	public EmptyOrCrossOrNought(EmptyOrCrossOrNought ecn) {
		if(ecn.getState() != 0 && ecn.getState() != 1 && ecn.getState() != 2)
			throw new IllegalArgumentException("number is outside of the scope, which is 0, 1 or 2");
		state = ecn.state;
	}

	/**
	 * 
	 * @return true if state is equal 0
	 */
	public boolean isEmpty() {
		return (state == 0);
	}

	/**
	 * 
	 * @return true if state is equal 1
	 */
	public boolean isCross() {
		return (state == 1);
	}

	/**
	 * 
	 * @return true if state is equal 2
	 */
	public boolean isNought() {
		return (state == 2);
	}

	/**
	 * Returns state of the object.
	 * @return 0, 1 or 2
	 */
	public int getState() {
		return state;
	}
	
	/**
	 * Gets the symbol that represents the state of this object.
	 * @return 'x', 'o' or ' ' (space)
	 */
	public char getSymbol() {
		if(state == 0) return ' ';
		if(state == 1) return 'x';
		if(state == 2) return 'o';
		throw new IllegalStateException("unexpected state = " + String.valueOf(state) + ", neither 0, 1 nor 2");
	}
	
	@Override
	public String toString() {
		if(state == 0) return " ";
		if(state == 1) return "x";
		if(state == 2) return "o";
		throw new IllegalStateException("unexpected state, neither 0, 1 nor 2");
	}
}
