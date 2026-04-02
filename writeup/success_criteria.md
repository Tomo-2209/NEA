# Success Criteria

The following criteria define what the finished product must achieve to be considered a success. They are written before development and will be used at the end of the project to evaluate the product against each point. A criterion is only satisfied if it is fully and reliably met — partial implementations do not count.

---

## Qualitative Criteria

Criteria that are subjective and will be judged by observation and user feedback.

1. The game must be easy to pick up and play without reading any documentation — a new player must be able to understand what to do from the starting dialogs alone.

2. The correct-answer and incorrect-answer visual feedback must be immediately clear to both players without any explanation.

3. The bomb mechanic must feel tense and exciting; the countdown timer and explosion animation must create a genuine sense of urgency for the player trying to defuse it.

4. The interface must look visually consistent throughout — fonts, colours, and layout must form a coherent style at every stage of the game.

5. The difficulty levels must feel appropriately challenging — GCSE questions must be suitable for GCSE students and A-Level questions must be noticeably harder, appropriate for students studying A-Level Mathematics.

6. The game must feel fair to both players; neither player must have a structural advantage due to their symbol, the layout of the grid, or the selection of questions.

7. The statistics panel must be readable at a glance without obscuring or distracting from the game board.

8. The bomb explosion animation must be visually impressive and must clearly communicate that tiles in the surrounding area have been cleared.

9. The transition between rounds (after a win, draw, or reset) must feel immediate — there must be no noticeable lag or visual disruption when a new round begins.

10. The answer-feedback flash must be long enough for both players to read the correct answer comfortably but short enough that it does not disrupt the pace of play.

---

## Quantitative Criteria

Criteria that are objective and can be verified by inspection or testing.

### Start-up and Configuration

11. On launch, the program must display a difficulty-selection dialog before the game board is shown.

12. The difficulty-selection dialog must offer exactly two options: "GCSE" and "A-Level".

13. After difficulty selection, the program must present an input dialog asking for the name of Player 1, followed by a second dialog asking for the name of Player 2.

14. If a player leaves their name field blank or cancels the dialog, the program must automatically substitute the default name "Player 1" or "Player 2" respectively.

15. The main game window must be exactly 800 pixels wide and 700 pixels tall.

16. The game window must not be resizable by the user.

17. The application window title must read "Tic-Tac-Total".

---

### Layout

18. The main window must contain five distinct regions: a header banner at the top, a statistics panel on the left, the game board in the centre, a Reset button at the bottom, and a grid-size slider on the right.

19. The header label must display centred text in bold white font on a dark background.

20. The Reset button must have a dark background with orange text.

21. The grid-size slider must be oriented vertically, positioned to the right of the game board.

22. The statistics panel must be positioned to the left of the game board and must always be visible during play.

---

### Game Board and Grid

23. The game board must display a square grid of tiles, where every cell contains exactly one tile.

24. The grid must support all sizes from 3×3 to 10×10 inclusive — no other sizes are required.

25. The slider on the right of the window must allow the player to select any grid size between 3 and 10.

26. When the grid-size slider is moved to a new value, the board must immediately reset and a new round must begin at the new grid size.

27. The game must start with a default grid size of 3×3.

28. Tile font sizes must scale dynamically with the current grid size so that symbols (X and O) remain clearly legible on all supported grid sizes.

---

### Standard Tiles

29. Empty standard tiles must appear as plain white buttons with no text.

30. A claimed standard tile must display the claiming player's symbol (X or O) centred inside the tile.

31. Clicking an already-occupied tile must have no effect on the game state.

32. Clicking any tile while an animation is in progress (bomb explosion or answer flash) must have no effect.

33. Clicking an empty standard tile must immediately present a maths question to the current player in a dialog.

34. If the player answers a standard-tile question correctly, their symbol must be placed in that tile and the tile must flash green for exactly 600 milliseconds.

35. If the player answers a standard-tile question incorrectly, the tile must flash red for exactly 1 500 milliseconds, and during that flash the tile must display the correct answer.

36. After an incorrect answer on a standard tile, the turn must pass to the other player once the red flash completes.

37. After a correct answer on a standard tile, a win/draw check must be performed before the turn switches.

---

### Maths Questions

38. Standard questions at GCSE difficulty must be drawn randomly from at least the following topic types: linear equations, percentages, area of rectangles and triangles, mean of a dataset, powers and indices, algebraic substitution, and speed/distance/time.

39. Standard questions at A-Level difficulty must be drawn randomly from at least the following topic types: differentiation (power rule), second derivatives, stationary points, definite integration, logarithms, binomial coefficients, and inverse-trigonometric angles.

40. Each time a question is needed, a topic type must be selected at random, and the numerical parameters of that question (coefficients, dimensions, values, etc.) must be freshly generated at random — so that the specific numbers differ between questions even when the topic type repeats.

41. Answer checking must be case-insensitive and must strip leading and trailing whitespace from the player's input before comparing it against the correct answer.

42. Where a question has more than one valid way to express the correct answer (for example "0.5" and "1/2"), all accepted forms must be stored with the question and any one of them must be accepted as correct.

---

### Bomb Tiles

43. Bomb tiles must be hidden — before being clicked, a bomb tile must appear visually identical to an empty standard tile.

44. Clicking a bomb tile must trigger the bomb: the tile must reveal its bomb graphic and begin an animated fuse-burn sequence.

45. Simultaneously with the bomb animation beginning, a dedicated bomb-diffuse dialog must be displayed, presenting the current player with a harder maths question.

46. The bomb-diffuse dialog must show the question, an answer input field, and a "DEFUSE!" submit button.

47. The bomb-diffuse dialog must display a countdown timer starting at 35 seconds, with a visible progress bar that decreases as time passes.

48. When 5 or fewer seconds remain on the countdown, the countdown label must change colour to red to warn the player.

49. Bomb-diffuse questions at GCSE difficulty must be drawn from harder GCSE topics, covering at least: quadratic equations, Pythagoras' theorem, simultaneous equations, and nth-term sequences.

50. Bomb-diffuse questions at A-Level difficulty must be drawn from harder A-Level topics, covering at least: geometric series sums, chain-rule differentiation, and harder definite integrals.

51. If the player answers the bomb-diffuse question correctly within the time limit, the bomb must be diffused: the bomb tile must convert to an empty standard tile and the player's symbol must be placed in that cell.

52. After a successful diffuse, the game must perform a win/draw check.

53. If the player answers the bomb-diffuse question incorrectly, or if the countdown reaches zero, the bomb must detonate.

54. When a bomb detonates, it must trigger an explosion particle animation before clearing the board.

55. Once the explosion animation completes, all tiles in the 3×3 area surrounding the bomb (including the bomb cell itself) must be cleared — any symbols present in those cells must be removed from both the view and the internal board state.

56. If a neighbouring bomb tile falls within the 3×3 blast radius of an exploding bomb, that neighbouring bomb must also be converted to an empty standard tile (it does not itself explode).

57. All tile clicks must be blocked for the entire duration of a bomb explosion animation; no player action must be accepted until the animation fully completes.

58. After the explosion animation and area-clear complete, the turn must pass to the other player.

---

### Bomb Placement

59. At the start of every round, bomb tiles must be placed at randomly chosen positions on the grid.

60. The number of bombs placed must match the following table exactly:

    | Grid size | Bombs |
    |-----------|-------|
    | 3×3       | 1     |
    | 4×4       | 2     |
    | 5×5       | 3     |
    | 6×6       | 5     |
    | 7×7       | 9     |
    | 8×8       | 12    |
    | 9×9       | 16    |
    | 10×10     | 20    |

61. No two bombs must be placed in the same cell.

---

### Win and Draw Detection

62. The program must detect a win whenever a player places a symbol that completes a consecutive line of the required length in any row, any column, or either diagonal.

63. The required line length (win length) must follow this table:

    | Grid size | Win length |
    |-----------|-----------|
    | 3–4       | 3         |
    | 5–6       | 4         |
    | 7–8       | 5         |
    | 9–10      | 6         |

64. When a win is detected, the tiles that form the winning line must be highlighted with a gold flashing animation.

65. When a win is detected, the header must display a message naming the winner (e.g. "Alice wins!").

66. When a win is detected, all tile clicks must be ignored until a new round begins.

67. When all tiles on the board are occupied and no winning line has been found, the game must record a draw.

68. When a draw occurs, the header must display a draw message (e.g. "It's a draw!").

---

### Turn Management

69. At the start of each new round, the first player to take a turn must be selected at random.

70. The header must always display whose turn it currently is during active play.

71. After a correct answer on a standard tile, the active player's symbol is placed and a win/draw check is run; if the game continues, the turn must then switch to the other player.

72. After an incorrect answer on a standard tile, the turn must switch to the other player immediately once the red flash has finished.

73. After a bomb diffuse, if the game continues, the turn must switch to the other player.

74. After a bomb detonation, the turn must switch to the other player once the explosion animation is complete.

---

### Scoring and Statistics

75. Each player must have a rounds-won score (initialised to 0) that increments by exactly 1 each time they win a round.

76. Each player must have a current win streak that increments by 1 after each consecutive round win and resets to 0 after a loss or a draw.

77. Each player must have a best-streak record that stores the highest consecutive win streak the player has reached in the current session; it must never decrease.

78. Each player must have a correct-answers count that increments by 1 every time they answer any maths question correctly (standard or bomb-diffuse).

79. Each player must have a wrong-answers count that increments by 1 every time they answer a standard question incorrectly, answer a bomb-diffuse question incorrectly, or let the bomb-diffuse countdown expire.

80. The statistics panel must show all five statistics for both players: rounds won, current win streak, best streak, correct answers, and wrong answers.

81. The statistics panel must update in real time — it must refresh after every answered question and at the end of every round.

82. The header must display the current round-win score for both players (e.g. "Alice : 2  |  Bob : 1") between rounds.

---

### Reset Button

83. A "Reset" button must be visible at the bottom of the window throughout the entire game session.

84. Pressing the Reset button must immediately start a new round with the same grid size, resetting the board and picking a new random starting player.

85. Pressing the Reset button must not reset either player's score, win streaks, or answer statistics — those must persist across rounds within the session.

---

### Architecture and Code Quality

86. The program must follow the Model-View-Controller (MVC) design pattern, with a clear separation between the board model (`GameModel`), the view components (`GamePanel`, `TicTacTotalUI`, `StatsPanel`), and the controller (`GameController`).

87. The game model (`GameModel`) must contain no Swing or UI code; it must represent only the board state, move validation, and win/draw detection.

88. The program must run correctly on the Java Swing Event Dispatch Thread (EDT) — all UI construction and updates must be performed on the EDT.
