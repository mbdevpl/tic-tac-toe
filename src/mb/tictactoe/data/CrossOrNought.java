package mb.tictactoe.data;

/**
 * Restricts the values only to 'x' and 'o'.
 * @author mb
 */
abstract class CrossOrNought extends EmptyOrCrossOrNought {
	/**
	 * Constructs new object from a char.
	 * @param symbol 'x' or 'o' (lower-case or upper-case)
	 * @throws IllegalArgumentException when argument is out of the scope
	 */
	public CrossOrNought(char symbol) throws IllegalArgumentException {
		super(symbol);
		if(isEmpty()) throw new IllegalArgumentException("CrossOrNought cannot be empty");
	}
	
	/**
	 * Constructs new object from an int.
	 * @param number 1 or 2
	 * @throws IllegalArgumentException when argument is out of the scope
	 */
	public CrossOrNought(int number) throws IllegalArgumentException {
		super(number);
		if(isEmpty()) throw new IllegalArgumentException("CrossOrNought cannot be empty");
	}
	
	/**
	 * Copy constructor.
	 * @param cn source of data
	 * @throws IllegalArgumentException when argument's state is out of the scope
	 */
	public CrossOrNought(CrossOrNought cn) throws IllegalArgumentException {
		super(cn);
		if(isEmpty()) throw new IllegalArgumentException("CrossOrNought cannot be empty");
	}
	
	@Override
	public String toString() {
		if(state == 1) return "x";
		if(state == 2) return "o";
		throw new IllegalStateException("unexpected state, neither 1 nor 2");
	}
}
