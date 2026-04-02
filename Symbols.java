/**
 * Utility class holding the constant symbols used on the game board.
 *
 * <p>Rather than scattering magic strings such as {@code ""}, {@code "X"} and
 * {@code "O"} across the codebase, all tile-symbol literals are declared here
 * as named public constants.  This makes intent explicit and ensures that any
 * future change to a symbol only needs to be made in one place.</p>
 *
 * <p>The class cannot be instantiated; it exists solely as a namespace for its
 * constants.</p>
 *
 * @author  Tomo
 * @version 1.0
 */
public final class Symbols
{
/** Represents an empty, unoccupied cell on the board. */
public static final String EMPTY = "";

/** The symbol placed on the board by Player 1. */
public static final String X = "X";

/** The symbol placed on the board by Player 2. */
public static final String O = "O";

/**
 * Private constructor – prevents instantiation of this utility class.
 */
private Symbols() {}
}
