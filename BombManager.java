import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Manages the random placement and tracking of bomb tiles on the game board.
 *
 * <p>When the game starts or the grid size changes, {@code BombManager}
 * calculates how many bombs should appear and places them at random
 * co-ordinates.  The number of bombs scales with the grid size according to a
 * fixed lookup table so that larger grids remain interesting without becoming
 * unplayable.</p>
 *
 * <p>The bomb-count table is:</p>
 * <pre>
 *   Grid size  |  3   4   5   6   7    8    9   10
 *   Bomb count |  1   2   3   5   9   12   16   20
 * </pre>
 *
 * @author  Tomo
 * @version 1.0
 * @see     BombTile
 * @see     GamePanel
 */
public class BombManager
{
// ── Constants ─────────────────────────────────────────────────────────

/**
 * Lookup table mapping each supported grid size to its bomb count.
 * Row format: {@code {gridSize, bombCount}}.
 */
private static final int[][] BOMB_COUNTS = {
{3,  1},
{4,  2},
{5,  3},
{6,  5},
{7,  9},
{8, 12},
{9, 16},
{10, 20}
};

// ── Fields ────────────────────────────────────────────────────────────

/**
 * Set of occupied bomb positions encoded as {@code "row,col"} strings for
 * fast O(1) look-up.
 */
private final Set<String> bombPositions;

/** The current grid dimension (number of rows = number of columns). */
private int gridSize;

/** Random number generator used when placing bombs. */
private final Random random;

// ── Constructor ───────────────────────────────────────────────────────

/**
 * Constructs a {@code BombManager} for the given grid size and immediately
 * places bombs at random positions.
 *
 * @param gridSize the initial grid dimension (number of rows / columns)
 */
public BombManager(int gridSize)
{
this.gridSize      = gridSize;
this.bombPositions = new HashSet<>();
this.random        = new Random();
placeBombs();
}

// ── Public methods ────────────────────────────────────────────────────

/**
 * Returns {@code true} if the cell at ({@code row}, {@code col}) should be
 * a {@link BombTile}.
 *
 * @param row the zero-based row index
 * @param col the zero-based column index
 * @return {@code true} if a bomb occupies that position
 */
public boolean isBombPosition(int row, int col)
{
return bombPositions.contains(row + "," + col);
}

/**
 * Resets bomb positions for a new game, optionally with a new grid size.
 *
 * <p>All previous positions are cleared before new ones are generated.</p>
 *
 * @param newGridSize the grid dimension to use for the new layout
 */
public void reset(int newGridSize)
{
this.gridSize = newGridSize;
bombPositions.clear();
placeBombs();
}

/**
 * Returns a defensive copy of the current bomb-position set.
 *
 * <p>Positions are encoded as {@code "row,col"} strings.  This method is
 * provided for testing and debugging purposes.</p>
 *
 * @return a new {@link Set} containing all current bomb-position strings
 */
public Set<String> getBombPositions()
{
return new HashSet<>(bombPositions);
}

// ── Private helpers ───────────────────────────────────────────────────

/**
 * Returns the number of bombs for the given grid size by consulting the
 * {@link #BOMB_COUNTS} lookup table.  Falls back to {@code 1} if the size
 * is not found.
 *
 * @param size the grid dimension
 * @return the corresponding bomb count
 */
private int getBombCount(int size)
{
for (int[] pair : BOMB_COUNTS)
{
if (pair[0] == size)
{
return pair[1];
}
}
return 1; // fallback for unexpected sizes
}

/**
 * Randomly places the required number of bombs on the board by generating
 * random (row, col) pairs until the target count is reached.
 *
 * <p>Using a {@link Set} ensures no two bombs share the same cell.</p>
 */
private void placeBombs()
{
bombPositions.clear();
int bombCount = getBombCount(gridSize);

while (bombPositions.size() < bombCount)
{
int row = random.nextInt(gridSize);
int col = random.nextInt(gridSize);
bombPositions.add(row + "," + col);
}
}
}
