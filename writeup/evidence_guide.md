# Evidence Guide

For each test in `testing.md`, the table below states whether a **screenshot** or a short **video clip** is the most appropriate form of evidence, and exactly what should be captured.

> **How to take evidence**
> - **Screenshot**: use your operating system's screenshot tool (e.g. Win + Shift + S on Windows, Cmd + Shift + 4 on macOS) and crop to the relevant part of the screen.
> - **Video**: use a screen-recording tool (e.g. OBS Studio, Xbox Game Bar on Windows, or QuickTime on macOS). Record only the duration relevant to the test — short clips (5–30 seconds) are better than long recordings.

---

| Test No. | Evidence Type | What to Capture |
|----------|--------------|----------------|
| T01 | Screenshot | The difficulty-selection dialog visible in front of the (not-yet-rendered) game board. Both "GCSE" and "A-Level" buttons must be clearly visible. |
| T02 | Screenshot (×2) | (1) The first name-entry dialog for Player 1. (2) The game board after both dialogs are dismissed without input, showing "Player 1" and "Player 2" in the statistics panel and turn label. |
| T03 | Screenshot | The full application window with the title bar visible, showing it reads "Tic-Tac-Total". |
| T04 | Screenshot | A screenshot of the full game window clearly showing all five regions: header at top, stats panel on the left, game board in the centre, Reset button at the bottom, and grid-size slider on the right. |
| T05 | Screenshot | The 3×3 game board immediately after the start dialogs are completed, showing all 9 blank white tiles. |
| T06 | Screenshot (×2) | (1) The board at grid size 3 (3×3). (2) The board at grid size 10 (10×10), showing the much larger grid. The slider position should be visible in both screenshots. |
| T07 | Screenshot | A 10×10 board with several tiles claimed, showing that X and O symbols are clearly legible at the small tile size. |
| T08 | Screenshot | The board showing a claimed tile (with a visible symbol) and the turn label in the header — demonstrating the symbol was placed correctly. |
| T09 | Screenshot (×2) | (1) The maths question dialog open after clicking a tile. (2) The green-flashing tile immediately after entering a correct answer (capture quickly — flash lasts 600 ms). |
| T10 | Screenshot (×2) | (1) The tile flashing red with the correct answer displayed on it. (2) The turn label after the flash showing the other player's name. |
| T11 | Screenshot | The game board while the green flash is visible, alongside a separate attempt to click another tile that has no effect (the flash should still be visible). |
| T12 | Screenshot (×7) | One screenshot per distinct GCSE standard question type. Each screenshot should show the question dialog with the question text visible. |
| T13 | Screenshot (×7) | One screenshot per distinct A-Level standard question type, showing the question dialog with the question text visible. |
| T14 | Screenshot (×2) | Two screenshots of the same topic (e.g. two linear-equation questions) showing different numerical values — proving randomised parameters. |
| T15 | Screenshot | The maths question dialog with a correct answer typed that includes leading/trailing spaces or mixed case. Show the "Correct!" outcome (green flash or next question appearing). |
| T16 | Screenshot (×2) | (1) A question accepted with one answer form (e.g. "0.5"). (2) The same question type accepted with the alternative form (e.g. "1/2"). Both should show a correct-answer outcome. |
| T17 | Screenshot (×2) | (1) The board before any bomb tile has been clicked, showing all tiles look identical. (2) A bomb tile mid-animation (fuse burning) after it has been clicked and revealed. |
| T18 | Screenshot | The bomb-diffuse dialog open, clearly showing the countdown label (with seconds), progress bar, question text, answer input field, and "DEFUSE!" button. |
| T19 | Screenshot | The bomb-diffuse dialog at 5 or fewer seconds remaining, showing the countdown label in red. |
| T20 | Screenshot (×4) | One screenshot per distinct harder GCSE bomb-diffuse question type (quadratic, Pythagoras, simultaneous equations, nth-term). Each must show the question dialog open. |
| T21 | Screenshot (×3) | One screenshot per distinct harder A-Level bomb-diffuse question type (geometric series, chain rule, harder integral). Each must show the question dialog open. |
| T22 | Screenshot (×2) | (1) The bomb-diffuse dialog open with a correct answer entered. (2) The board immediately after the diffuse, showing the former bomb cell now containing the player's symbol. |
| T23 | Video (~5–8 s) | Record from the moment of entering a wrong answer in the bomb-diffuse dialog through the complete explosion animation and the resulting cleared 3×3 area. |
| T24 | Video (~5–8 s) | Record clicking a tile during a bomb explosion animation to demonstrate nothing happens, then a click succeeding once the animation has fully completed. |
| T25 | Screenshot | The turn label immediately after a bomb explosion animation completes, showing the other player's name. |
| T26 | Screenshot (×3) | Three screenshots of the same grid size (e.g. 5×5) after separate resets, showing that bomb positions differ between rounds. |
| T27 | Screenshot (×4) | One screenshot per grid size tested (3×3, 5×5, 7×7, 9×9) showing the exact moment a win is detected — the winning line highlighted in gold and the header showing the win message. |
| T28 | Screenshot (×4) | One screenshot each of a win via: (1) a row, (2) a column, (3) a top-left to bottom-right diagonal, (4) a top-right to bottom-left diagonal. Each should show the gold flash on the winning line. |
| T29 | Screenshot | The game board showing the gold flashing winning tiles and the header label reading "[Player] wins!". Also demonstrate that clicking a tile after the win has no effect. |
| T30 | Screenshot | A fully filled 3×3 board with no winning line, and the header displaying the draw message. |
| T31 | Screenshot (×2) | (1) A round starting with Player X's turn. (2) A round starting with Player O's turn. Both should show the turn label in the header. |
| T32 | Screenshot | The turn label updating to the other player's name after a correct answer that does not produce a win. |
| T33 | Screenshot | The turn label showing the other player's name after the red flash completes following a wrong answer. |
| T34 | Screenshot (×2) | (1) The turn label after a successful bomb diffuse (with no win), showing the other player's name. (2) The turn label after a bomb explosion, showing the other player's name. |
| T35 | Screenshot (×2) | (1) The statistics panel after two consecutive wins by the same player, showing Score = 2, Streak = 2, Best Streak = 2. (2) The panel after the opposing player wins a round, showing the first player's Streak reset to 0 but Best remaining at 2. |
| T36 | Screenshot | The statistics panel mid-game showing non-zero correct and wrong answer counts for both players, with all five statistics (wins, streak, best streak, correct, wrong) visible. |
| T37 | Screenshot (×2) | (1) The statistics panel mid-game with non-zero stats. (2) The same statistics panel immediately after pressing Reset, showing the board has cleared but all player stats are unchanged. |
