# Evaluation

---

## Analysis of Criteria

The table below lists every success criterion from the final agreed list, a brief description of how the criterion was met in the finished product, and whether it was fully satisfied.

### Qualitative Criteria

| # | Criteria | Description | Met |
|---|----------|-------------|-----|
| 1 | The game must be easy to pick up and play without reading any documentation — a new player must be able to understand what to do from the starting dialogs alone. | The program opens with a clearly labelled difficulty-selection dialog ("GCSE" / "A-Level") followed by two name-entry dialogs. The header then displays whose turn it is, and the turn label updates after every action. A new player does not need to consult any external document to make their first move. | ✓ |
| 2 | The correct-answer and incorrect-answer visual feedback must be immediately clear to both players without any explanation. | A correct answer produces a bright green tile flash; an incorrect answer produces a red flash and displays the correct answer as text on the tile for 1 500 ms. The contrasting colours are universally understood as pass/fail signals and require no explanation. | ✓ |
| 3 | The bomb mechanic must feel tense and exciting; the countdown timer and explosion animation must create a genuine sense of urgency for the player trying to defuse it. | The bomb-diffuse dialog presents a 35-second countdown with a depleting progress bar. At 5 seconds the label turns red. On failure, an explosion particle animation fires before the area is cleared, providing a satisfying and impactful consequence. | ✓ |
| 4 | The interface must look visually consistent throughout — fonts, colours, and layout must form a coherent style at every stage of the game. | All UI components use Arial Bold as the primary font. The dark grey header and statistics panel, white tile background, and orange accent on the Reset button are applied uniformly across start-up dialogs, the main board, and the bomb-diffuse dialog. The dark red background of the bomb-diffuse dialog distinguishes it as a special event while remaining visually coherent. | ✓ |
| 5 | The difficulty levels must feel appropriately challenging — GCSE questions must be suitable for GCSE students and A-Level questions must be noticeably harder, appropriate for students studying A-Level Mathematics. | GCSE standard questions draw from topics such as linear equations, percentages, and speed/distance/time. A-Level standard questions draw from differentiation, integration, logarithms, and binomial coefficients — topics that require A-Level knowledge to solve reliably. The harder bomb-diffuse questions at each level are further stepped up, maintaining a clear and appropriate progression. | ✓ |
| 6 | The game must feel fair to both players; neither player must have a structural advantage due to their symbol, the layout of the grid, or the selection of questions. | Questions are generated independently for each player from the same pool with freshly randomised parameters. The starting player is chosen randomly at the start of every round. Bomb positions are placed randomly and the grid is symmetric — neither symbol (X or O) has a visual or mechanical advantage. | ✓ |
| 7 | The statistics panel must be readable at a glance without obscuring or distracting from the game board. | The statistics panel is positioned to the left of the game board in a dedicated region and occupies a fixed narrow column. Text sizes are large enough to be read without squinting but the panel does not compete with the board for visual attention. All five statistics for both players are labelled clearly. | ✓ |
| 8 | The bomb explosion animation must be visually impressive and must clearly communicate that tiles in the surrounding area have been cleared. | The explosion is rendered by `BombAnimationEngine`, which generates multiple particle bursts radiating from the bomb cell. Once the animation completes, the 3×3 area is visibly cleared — tiles revert to blank white — making the area-clear effect unmistakably obvious. | ✓ |
| 9 | The transition between rounds (after a win, draw, or reset) must feel immediate — there must be no noticeable lag or visual disruption when a new round begins. | Pressing Reset or starting a new round rebuilds the grid synchronously on the EDT with no file I/O or background work. The new grid appears instantaneously with no blank frames or flicker. | ✓ |
| 10 | The answer-feedback flash must be long enough for both players to read the correct answer comfortably but short enough that it does not disrupt the pace of play. | The green (correct) flash lasts 600 ms — just long enough to register visually. The red (incorrect) flash lasts 1 500 ms — sufficient for both players to read the correct answer on the tile. Neither duration is long enough to cause frustration between turns. | ✓ |

---

### Quantitative Criteria

#### Start-up and Configuration

| # | Criteria | Description | Met |
|---|----------|-------------|-----|
| 11 | On launch, the program must display a difficulty-selection dialog before the game board is shown. | `TicTacTotalUI` calls the difficulty dialog inside its constructor before making the `JFrame` visible, so the dialog always appears before the board is rendered. | ✓ |
| 12 | The difficulty-selection dialog must offer exactly two options: "GCSE" and "A-Level". | The dialog is built with two `JButton` components labelled "GCSE" and "A-Level" and no other selectable options. | ✓ |
| 13 | After difficulty selection, the program must present an input dialog asking for the name of Player 1, followed by a second dialog asking for the name of Player 2. | Two successive `JOptionPane.showInputDialog` calls collect the names before the game board becomes interactive. | ✓ |
| 14 | If a player leaves their name field blank or cancels the dialog, the program must automatically substitute the default name "Player 1" or "Player 2" respectively. | The returned string is checked with `== null` (cancel) and `trim().isEmpty()` (blank); if either condition is true, the default name is used. | ✓ |
| 15 | The application window title must read "Tic-Tac-Total". | `Runner.java` constructs the `JFrame` with `new JFrame("Tic-Tac-Total")`. | ✓ |

#### Layout

| # | Criteria | Description | Met |
|---|----------|-------------|-----|
| 16 | The main window must contain five distinct regions: a header banner at the top, a statistics panel on the left, the game board in the centre, a Reset button at the bottom, and a grid-size slider on the right. | `TicTacTotalUI` uses a `BorderLayout`: the header occupies `NORTH`, the stats panel `WEST`, the game board `CENTER`, the Reset button `SOUTH`, and the slider `EAST`. | ✓ |
| 17 | The header label must display centred text in bold white font on a dark background. | The header `JLabel` has its foreground set to `Color.WHITE`, its font to `new Font("Arial", Font.BOLD, 28)`, and its background to a dark grey `Color(45, 45, 45)` with `setOpaque(true)`. Text alignment is `CENTER`. | ✓ |
| 18 | The Reset button must have a dark background and be clearly labelled "Reset". | The Reset button is constructed as `new JButton("Reset")` and styled with a dark background (`Color(45, 45, 45)`) and contrasting orange foreground. | ✓ |
| 19 | The grid-size slider must be oriented vertically, positioned to the right of the game board. | The `JSlider` is created with `SwingConstants.VERTICAL` orientation and added to the `EAST` region of the layout. | ✓ |
| 20 | The statistics panel must be positioned to the left of the game board and must always be visible during play. | The `StatsPanel` is added to the `WEST` region. It is never hidden or replaced at any point during a session. | ✓ |

#### Game Board and Grid

| # | Criteria | Description | Met |
|---|----------|-------------|-----|
| 21 | The game board must display a square grid of tiles, where every cell contains exactly one tile. | `GamePanel.buildGrid()` iterates over every `(row, col)` pair from `0` to `gridSize - 1` and places exactly one `Tile` instance (either `StandardTile` or `BombTile`) per cell in a `GridLayout`. | ✓ |
| 22 | The grid must support all sizes from 3×3 to 10×10 inclusive. | The `JSlider` is set with `setMinimum(3)` and `setMaximum(10)`. `GameModel` and `BombManager` both accept any grid size from 3 to 10. | ✓ |
| 23 | The slider on the right of the window must allow the player to select any grid size between 3 and 10. | The slider has minor tick spacing of 1 and `setPaintTicks(true)` so every integer from 3 to 10 is reachable. A `ChangeListener` fires on every slider movement and calls `GameController.changeGridSize()`. | ✓ |
| 24 | When the grid-size slider is moved to a new value, the board must immediately reset and a new round must begin at the new grid size. | The `ChangeListener` calls `GameController.changeGridSize(newSize)` which calls `GamePanel.rebuildGrid(newSize)` synchronously, replacing all tiles and restarting the round immediately. | ✓ |
| 25 | The game must start with a default grid size of 3×3. | `TicTacTotalUI` initialises the slider with `setValue(3)` and creates the initial `GameModel` and `GamePanel` with `gridSize = 3`. | ✓ |
| 26 | Tile font sizes must scale dynamically with the current grid size so that symbols (X and O) remain clearly legible on all supported grid sizes. | `GamePanel.doLayout()` is overridden; after the standard Swing layout pass it calculates a font size proportional to the tile height and calls `tile.setFontSize(fontSize)` on every tile, ensuring symbols scale with the available cell size. | ✓ |

#### Standard Tiles

| # | Criteria | Description | Met |
|---|----------|-------------|-----|
| 27 | Empty standard tiles must appear as plain white buttons with no text. | `StandardTile.draw()` sets `setText("")` and `setBackground(Color.WHITE)` when the tile is empty, producing a plain blank appearance. | ✓ |
| 28 | A claimed standard tile must display the claiming player's symbol (X or O) centred inside the tile. | `StandardTile.setSymbol(sym)` stores the symbol and calls `draw()`, which calls `setText(symbol)`. The symbol is drawn centred manually in `Tile.paintComponent()` to prevent Swing from truncating it with "...". | ✓ |
| 29 | Clicking an already-occupied tile must have no effect on the game state. | In `GameController.handleTileClick()`, the first check is `if (!tile.isEmpty()) return;` — occupied tiles are ignored immediately. | ✓ |
| 30 | Clicking any tile while an animation is in progress (bomb explosion or answer flash) must have no effect. | `GameController` maintains an `animationInProgress` boolean flag. Every tile-click handler checks this flag at entry and returns without action if it is `true`. | ✓ |
| 31 | Clicking an empty standard tile must immediately present a maths question to the current player in a dialog. | `GameController.handleTileClick()` calls `MathQuestionDialog.show()` synchronously on the EDT, which blocks until the player submits an answer. | ✓ |
| 32 | If the player answers a standard-tile question correctly, their symbol must be placed in that tile and the tile must flash green for exactly 600 milliseconds. | On a correct answer the symbol is set via `tile.setSymbol(sym)` and a `javax.swing.Timer` with a delay of `600` ms fires once to restore the white background. | ✓ |
| 33 | If the player answers a standard-tile question incorrectly, the tile must flash red for exactly 1 500 milliseconds, and during that flash the tile must display the correct answer. | On an incorrect answer, `tile.setBackground(Color.RED)` and `tile.setText(...)` display the correct answer; a `Timer` with a delay of `1500` ms restores the tile's original state. | ✓ |
| 34 | After an incorrect answer on a standard tile, the turn must pass to the other player once the red flash completes. | The `Timer` callback for the 1 500 ms red flash calls `GameController.switchTurn()` and `StatsPanel.refresh()` only after the delay has elapsed. | ✓ |
| 35 | After a correct answer on a standard tile, a win/draw check must be performed before the turn switches. | Immediately after placing the symbol, `GameController` calls `GameModel.checkWin()` and `GameModel.checkDraw()` before any turn-switching logic executes. | ✓ |

#### Maths Questions

| # | Criteria | Description | Met |
|---|----------|-------------|-----|
| 36 | Standard questions at GCSE difficulty must be drawn randomly from at least the following topic types: linear equations, percentages, area of rectangles and triangles, mean of a dataset, powers and indices, algebraic substitution, and speed/distance/time. | `MathQuestionBank.getStandardQuestion(GCSE)` randomly selects from a list of seven generator methods, one per required topic. All seven are implemented with randomised parameters. | ✓ |
| 37 | Standard questions at A-Level difficulty must be drawn randomly from at least the following topic types: differentiation (power rule), second derivatives, stationary points, definite integration, logarithms, binomial coefficients, and inverse-trigonometric angles. | `MathQuestionBank.getStandardQuestion(A_LEVEL)` randomly selects from a list of seven generator methods covering all seven required A-Level topics. | ✓ |
| 38 | Each time a question is needed, a topic type must be selected at random, and the numerical parameters of that question must be freshly generated at random. | Every call to `getStandardQuestion()` or `getBombQuestion()` uses `new Random()` (or an instance-level `Random`) to pick a topic index and then independently randomises all numerical values (coefficients, bases, dimensions, etc.) before constructing the `MathQuestion` object. | ✓ |
| 39 | Answer checking must be case-insensitive and must strip leading and trailing whitespace from the player's input before comparing it against the correct answer. | `MathQuestion.isCorrect(input)` normalises the input with `input.trim().toLowerCase()` and compares it against each accepted answer (also lower-cased) before returning the result. | ✓ |
| 40 | Where a question has more than one valid way to express the correct answer, all accepted forms must be stored with the question and any one of them must be accepted as correct. | `MathQuestion` stores a `List<String> acceptedAnswers`. `isCorrect()` iterates through the entire list and returns `true` if any element matches the normalised input. | ✓ |

#### Bomb Tiles

| # | Criteria | Description | Met |
|---|----------|-------------|-----|
| 41 | Bomb tiles must be hidden — before being clicked, a bomb tile must appear visually identical to an empty standard tile. | `BombTile.draw()` renders a plain white background with no text when `isActivated` is `false`, exactly matching the appearance of an empty `StandardTile`. | ✓ |
| 42 | Clicking a bomb tile must trigger the bomb: the tile must reveal its bomb graphic and begin an animated fuse-burn sequence. | Clicking a `BombTile` calls `activate()`, which sets `isActivated = true` and starts the `javax.swing.Timer` inside `BombAnimationEngine` that drives the frame-by-frame fuse animation. | ✓ |
| 43 | Simultaneously with the bomb animation beginning, a dedicated bomb-diffuse dialog must be displayed. | `GameController.handleBombClick()` calls `tile.activate()` and then immediately constructs and shows `BombDiffuseDialog` — both happen in the same EDT call, so the dialog appears at the same time as the animation starts. | ✓ |
| 44 | The bomb-diffuse dialog must show the question, an answer input field, and a "DEFUSE!" submit button. | `BombDiffuseDialog` contains a `JLabel` for the question text, a `JTextField` for the answer, and a `JButton` labelled "DEFUSE!". | ✓ |
| 45 | The bomb-diffuse dialog must display a countdown timer starting at 35 seconds, with a visible progress bar that decreases as time passes. | A `javax.swing.Timer` fires every 1 000 ms, decrementing the counter from 35. A `JProgressBar` is initialised to `maximum = 35` and its value decrements in step with the counter label. | ✓ |
| 46 | When 5 or fewer seconds remain on the countdown, the countdown label must change colour to red to warn the player. | Inside the timer callback, `if (timeLeft <= 5) countdownLabel.setForeground(Color.RED);` is checked on every tick. | ✓ |
| 47 | Bomb-diffuse questions at GCSE difficulty must be drawn from harder GCSE topics, covering at least: quadratic equations, Pythagoras' theorem, simultaneous equations, and nth-term sequences. | `MathQuestionBank.getBombQuestion(GCSE)` randomly selects from a separate pool of four harder GCSE generator methods, covering all four required topics. | ✓ |
| 48 | Bomb-diffuse questions at A-Level difficulty must be drawn from harder A-Level topics, covering at least: geometric series sums, chain-rule differentiation, and harder definite integrals. | `MathQuestionBank.getBombQuestion(A_LEVEL)` randomly selects from a pool of three harder A-Level generator methods covering all three required topics. | ✓ |
| 49 | If the player answers the bomb-diffuse question correctly within the time limit, the bomb must be diffused: the bomb tile must convert to an empty standard tile and the player's symbol must be placed in that cell. | On a correct diffuse answer, `GameController` calls `GamePanel.convertBombToStandard(row, col)`, which replaces the `BombTile` in the grid with a new `StandardTile`, then calls `setSymbol(sym)` on it. | ✓ |
| 50 | After a successful diffuse, the game must perform a win/draw check. | Immediately after placing the symbol on the converted tile, `GameController` calls `checkWin()` and `checkDraw()` before any further action. | ✓ |
| 51 | If the player answers the bomb-diffuse question incorrectly, or if the countdown reaches zero, the bomb must detonate. | Both the incorrect-answer path and the timer-expiry path in `BombDiffuseDialog` call `GameController.handleBombDetonation(row, col)` — the same detonation sequence runs regardless of the failure reason. | ✓ |
| 52 | When a bomb detonates, it must trigger an explosion particle animation before clearing the board. | `BombTile.triggerExplosion()` starts the particle-burst phase of `BombAnimationEngine`; the area-clear only occurs inside the `AnimationCompleteListener` callback, which fires after the animation finishes. | ✓ |
| 53 | Once the explosion animation completes, all tiles in the 3×3 area surrounding the bomb (including the bomb cell itself) must be cleared — any symbols must be removed from both the view and the internal board state. | Inside the `AnimationCompleteListener`, `GameController` iterates over every `(r, c)` in `[row-1, row+1] × [col-1, col+1]`, calls `GameModel.clearCell(r, c)` to update the model and `GamePanel.resetTile(r, c)` to reset the view tile. | ✓ |
| 54 | If a neighbouring bomb tile falls within the 3×3 blast radius of an exploding bomb, that neighbouring bomb must also be converted to an empty standard tile (it does not itself explode). | The area-clear loop checks whether a neighbour is a `BombTile`; if so, it calls `GamePanel.convertBombToStandard(r, c)` to replace it with a blank standard tile, rather than triggering a second explosion. | ✓ |
| 55 | All tile clicks must be blocked for the entire duration of a bomb explosion animation. | `GameController` sets `animationInProgress = true` when a bomb detonation begins. It is only reset to `false` inside the `AnimationCompleteListener` callback that fires when the last animation frame completes. | ✓ |
| 56 | After the explosion animation and area-clear complete, the turn must pass to the other player. | The final action inside the `AnimationCompleteListener` callback (after clearing the board and resetting the flag) is a call to `GameController.switchTurn()`. | ✓ |

#### Bomb Placement

| # | Criteria | Description | Met |
|---|----------|-------------|-----|
| 57 | At the start of every round, bomb tiles must be placed at randomly chosen positions on the grid. | `BombManager.placeBombs()` uses `java.util.Random` to generate candidate `(row, col)` pairs and is called at the start of every new round (construction or reset). | ✓ |
| 58 | The number of bombs placed must match the specified table exactly (3→1, 4→2, 5→3, 6→5, 7→9, 8→12, 9→16, 10→20). | `BombManager.getBombCount(gridSize)` looks up the count in a hard-coded 8-entry `BOMB_COUNTS` array that matches the specification table exactly. | ✓ |
| 59 | No two bombs must be placed in the same cell. | `BombManager.placeBombs()` stores occupied positions in a `HashSet<String>` and rejects any randomly generated position that is already in the set, repeating until a unique cell is found. | ✓ |

#### Win and Draw Detection

| # | Criteria | Description | Met |
|---|----------|-------------|-----|
| 60 | The program must detect a win whenever a player places a symbol that completes a consecutive line of the required length in any row, any column, or either diagonal. | `GameModel.checkWin()` runs four separate scans — across rows, down columns, along the top-left-to-bottom-right diagonal, and along the top-right-to-bottom-left diagonal — returning the winning line if any reaches `winLength` consecutive matching symbols. | ✓ |
| 61 | The required win length must follow the specified table (grids 3–4: win 3; 5–6: win 4; 7–8: win 5; 9–10: win 6). | `GameModel.setGridSize()` calculates `winLength` from the grid size using a conditional that matches the specification table exactly. | ✓ |
| 62 | When a win is detected, the tiles that form the winning line must be highlighted with a gold flashing animation. | `GameController` receives the list of winning `Point` objects from `checkWin()` and passes them to `WinFlashAnimation`, which alternates tile backgrounds between gold and white on a repeating `javax.swing.Timer`. | ✓ |
| 63 | When a win is detected, the header must display a message naming the winner (e.g. "Alice wins!"). | `GameController.handleWin()` calls `TicTacTotalUI.setHeaderText(name + " wins!")` immediately after the win is detected. | ✓ |
| 64 | When a win is detected, all tile clicks must be ignored until a new round begins. | `GameController` sets `gameOver = true` on a win. Every tile-click handler checks this flag and returns without action while it is `true`. The flag is cleared only when `startNewRound()` is called. | ✓ |
| 65 | When all tiles are occupied and no winning line has been found, the game must record a draw. | `GameModel.checkDraw()` returns `true` when `movesMade == gridSize * gridSize` and `checkWin()` returns `null`. | ✓ |
| 66 | When a draw occurs, the header must display a draw message (e.g. "It's a draw!"). | `GameController.handleDraw()` calls `TicTacTotalUI.setHeaderText("It's a draw!")` when `checkDraw()` returns `true`. | ✓ |

#### Turn Management

| # | Criteria | Description | Met |
|---|----------|-------------|-----|
| 67 | At the start of each new round, the first player to take a turn must be selected at random. | `GameController.startNewRound()` calls `currentPlayerIndex = new Random().nextInt(2)` to pick 0 or 1 with equal probability, then sets the header accordingly. | ✓ |
| 68 | The header must always display whose turn it currently is during active play. | Every call to `GameController.switchTurn()` and `startNewRound()` ends with `TicTacTotalUI.setHeaderText(currentPlayer.getName() + "'s turn")`. | ✓ |
| 69 | After a correct answer on a standard tile, the active player's symbol is placed and a win/draw check is run; if the game continues, the turn must then switch to the other player. | After placing the symbol and running `checkWin()` / `checkDraw()`, if neither returns a terminal result, `GameController` calls `switchTurn()`. The win/draw path skips `switchTurn()` and calls `handleWin()` or `handleDraw()` instead. | ✓ |
| 70 | After an incorrect answer on a standard tile, the turn must switch to the other player immediately once the red flash has finished. | The 1 500 ms `Timer` callback calls `switchTurn()` when it fires, ensuring the turn change happens only after the flash has completed. | ✓ |
| 71 | After a bomb diffuse, if the game continues, the turn must switch to the other player. | After a successful diffuse and win/draw check (with no terminal result), `GameController` calls `switchTurn()`. | ✓ |
| 72 | After a bomb detonation, the turn must switch to the other player once the explosion animation is complete. | The `AnimationCompleteListener` callback calls `switchTurn()` as its final action, after all area-clear work has been done. | ✓ |

#### Scoring and Statistics

| # | Criteria | Description | Met |
|---|----------|-------------|-----|
| 73 | Each player must have a rounds-won score (initialised to 0) that increments by exactly 1 each time they win a round. | The `Player` class holds an `int wins` field initialised to `0`. `GameController.handleWin()` calls `currentPlayer.incrementWins()` which adds exactly 1. | ✓ |
| 74 | Each player must have a current win streak that increments by 1 after each consecutive round win and resets to 0 after a loss or a draw. | `Player` holds `int streak`. `handleWin()` calls `incrementStreak()` on the winner; `handleDraw()` and `handleWin()` both call `resetStreak()` on the losing/drawing player. | ✓ |
| 75 | Each player must have a best-streak record that stores the highest consecutive win streak the player has reached in the current session; it must never decrease. | `Player.incrementStreak()` calls `if (streak > bestStreak) bestStreak = streak;` after incrementing, so `bestStreak` only ever increases. | ✓ |
| 76 | Each player must have a correct-answers count that increments by 1 every time they answer any maths question correctly (standard or bomb-diffuse). | Both the standard-tile correct-answer handler and the bomb-diffuse correct-answer handler call `currentPlayer.incrementCorrect()` before any other action. | ✓ |
| 77 | Each player must have a wrong-answers count that increments by 1 every time they answer incorrectly or let the bomb-diffuse countdown expire. | The standard-tile incorrect path, the bomb-diffuse incorrect path, and the bomb-diffuse timer-expiry path all call `currentPlayer.incrementWrong()`. | ✓ |
| 78 | The statistics panel must show all five statistics for both players: rounds won, current win streak, best streak, correct answers, and wrong answers. | `StatsPanel.refresh()` reads all five fields from each `Player` object and updates the corresponding `JLabel` widgets for both players simultaneously. | ✓ |
| 79 | The statistics panel must update in real time — it must refresh after every answered question and at the end of every round. | Every code path that modifies a player statistic (correct/wrong answer, win, draw) is followed immediately by a call to `StatsPanel.refresh()` before returning to the event loop. | ✓ |

#### Reset Button

| # | Criteria | Description | Met |
|---|----------|-------------|-----|
| 80 | A "Reset" button must be visible at the bottom of the window throughout the entire game session. | The Reset button is placed in the `SOUTH` panel of the `BorderLayout` and is never hidden or removed. It remains enabled and visible in all game states. | ✓ |
| 81 | Pressing the Reset button must immediately start a new round with the same grid size, resetting the board and picking a new random starting player. | The Reset `ActionListener` calls `GameController.startNewRound()` which rebuilds the grid at the current size and calls `currentPlayerIndex = new Random().nextInt(2)`. | ✓ |
| 82 | Pressing the Reset button must not reset either player's score, win streaks, or answer statistics — those must persist across rounds within the session. | `startNewRound()` only resets the `GameModel` board state and rebuilds `GamePanel`. The `Player` objects (which hold all statistics) are never re-constructed or reset during a Reset. | ✓ |

---

## Further Development

The program is structured around the Model-View-Controller (MVC) design pattern and an object-oriented tile hierarchy, both of which make it straightforward to extend in several directions.

**Additional tile types.** The abstract `Tile` class defines a contract — `draw()` for visual rendering and `onClick()` for click behaviour — that any new tile type must fulfil. A natural extension would be to introduce a **freeze tile**: clicking it would display a maths question as normal, but a correct answer would freeze the opponent's next turn (they would be prompted to answer a question before being allowed to place a symbol). This could be implemented by creating a `FreezeTile` class that extends `Tile`, overriding `draw()` to show a distinctive ice-blue graphic (visible only after activation, as with `BombTile`) and overriding `onClick()` to set a `frozenPlayer` flag in `GameController`. Because `GameController` already checks player state before each turn, adding a single conditional to the existing turn-management code would be sufficient to honour the freeze. The tile count per grid size could follow the same lookup-table pattern already used by `BombManager`.

**Online multiplayer.** At present the game is strictly local (two players share one keyboard). Because `GameController` separates all game logic from the view, an online mode could be added by replacing the local `Player` objects with networked proxies. A `RemotePlayer` class could implement the same interface as the existing `Player` class but relay moves over a socket connection. The `GameController` would require only minimal changes — it would call `player.submitMove()` regardless of whether the player is local or remote, and the networking layer would handle the difference transparently. The `MathQuestionBank` could remain entirely on the server side, eliminating any risk of clients seeing answers in advance.

**Persistent leaderboard.** The session statistics tracked by `StatsPanel` are currently held in memory and lost when the application closes. A straightforward extension would be to serialise each `Player`'s statistics to a local JSON or CSV file at the end of every session, and to load the highest recorded values on launch. Because `Player` is already a self-contained data class with clearly defined getters, wrapping it in a simple `SessionSerializer` utility class would require no changes to the existing logic. A leaderboard panel could then be added to the start-up dialog sequence, allowing players to see their best-ever streaks across sessions before choosing a difficulty.
