# Acceptable Limitations

The following limitations have been consciously accepted within the scope of this project. Each one has been identified as either technically out of scope for an A-level project, not essential to the core educational purpose of the system, or disproportionately complex relative to the benefit it would provide.

---

## 1. Two local players only — no AI opponent

The game is designed for two human players sharing the same machine. Implementing a computer opponent would require a decision-making algorithm such as Minimax (with alpha-beta pruning for larger grids), which is a substantial stand-alone project in its own right. Because the primary purpose of the software is to practise mathematics through competitive play between two people, the absence of an AI does not reduce the usefulness of the system for its intended audience.

---

## 2. No network or online multiplayer

Players must be physically present at the same computer. Supporting remote play would require a client-server architecture, socket programming, and synchronisation logic — all well beyond the scope of this project. Local two-player gameplay fully satisfies the agreed requirements.

---

## 3. No persistent save/load — session-only statistics

Player scores, win streaks, and answer counts are held in memory only and are lost when the application closes. Implementing a save system (file I/O or a database) was considered but excluded because the project's focus is on the gameplay and maths challenge mechanics rather than long-term record-keeping. Players can note scores manually between sessions if required.

---

## 4. Fixed set of question templates (with randomised parameters)

The question generator uses a fixed set of question *types* — seven standard types and four harder bomb-diffuse types at GCSE level, and seven standard types and three harder types at A-Level. When a question is needed, one of these types is picked at random and its numerical parameters (coefficients, dimensions, percentages, etc.) are generated randomly at that moment, so the specific numbers change every time. However, the underlying *topic and structure* of the question always falls into one of those pre-defined categories.

This means the game cannot, for example, surprise an experienced player with a topic they have never seen before; it will always cycle through the same set of question styles (solve a linear equation, find a percentage, differentiate a power, and so on). A system that could dynamically introduce entirely new question formats or topics would require a much more sophisticated question-generation engine or an external curriculum data source. For the purposes of this project, the fixed template set provides a wide enough variety of question styles to support meaningful maths revision at both levels.

There is also a possibility of encountering similar-looking questions within the same session — for instance, two percentage questions in a row — since each question is selected independently at random from the available types. Tracking recently used types and actively avoiding repetition would add complexity for limited benefit, so this has not been implemented.

---

## 5. Answers must be typed as integers or short plain strings

The answer-checking logic works by taking whatever the player types into the text box, trimming any surrounding whitespace, converting it to lower case, and then comparing it character-for-character against a list of pre-approved answer strings stored in the question. This is deliberately simple: it avoids any need for mathematical expression parsing.

This means the system cannot accept answers in alternative but equivalent forms unless those forms are explicitly listed when the question is created. For example, if the correct answer to a question is `6`, the player must type `6`; typing `6.0` or `+6` would be marked wrong. For the few questions where a fraction or decimal might be a natural way to express the answer (for instance, "one half"), both `0.5` and `1/2` can be listed as valid answers — but only if the question author remembered to add them. The system has no ability to recognise mathematical equivalence on its own.

All questions in the bank are deliberately designed so the correct answer is always a clean integer or a short unambiguous string (such as a topic keyword or a simple numerical result). This design choice makes the answer-checking reliable and removes any risk of a correct answer being marked wrong due to floating-point rounding or different but equivalent algebraic expressions. The trade-off is that question types requiring non-integer answers — such as surds, exact fractions in unsimplified form, or algebraic expressions like `2x + 3` — cannot be used without extending the validation logic.

---

## 6. Only two difficulty levels (GCSE and A-Level)

The system offers two pre-set difficulty modes. There is no adaptive difficulty that adjusts in real time based on player performance, and teachers cannot add their own custom questions. Adding a question editor or adaptive engine would be a significant feature in its own right; providing two well-defined, curriculum-aligned levels satisfies the requirements of the intended audience.

---

## 7. Grid size limited to between 3×3 and 10×10

The board can be resized between 3 and 10 rows/columns. Grids larger than 10×10 would require substantial UI rework — including smaller tiles, scrolling, and revised win-length logic — for little additional gameplay benefit, so this upper bound is an acceptable constraint.

---

## 8. Fixed window size of 800×700 pixels — not fully resizable

The application window has a fixed size. Supporting arbitrary window resizing would require fully responsive layout calculations throughout the codebase. The tile font does scale with the grid size, but the overall window dimensions are fixed for simplicity and visual consistency.

---

## 9. No sound effects or audio feedback

The game relies entirely on visual feedback (flash animations, bomb animations, and score updates). Adding audio would require integrating Java's `javax.sound` API and managing audio resources. Since audio was not a stated requirement and its absence does not affect gameplay, it has been excluded.

---

## 10. No user accounts or password protection

Players enter their names at the start of each session with no authentication. Because the software runs locally as a single-machine desktop application and stores no sensitive data, user accounts are not required.

---

## 11. No hint system for maths questions

If a player does not know the answer, no help is available within the application. Providing worked hints or step-by-step guidance would substantially increase the complexity of the question model and was outside the agreed requirements. The correct answer is shown after an incorrect attempt, which provides a basic learning feedback loop.

---

## 12. Bomb density is fixed — players cannot adjust the number of bombs

The number of bomb tiles is determined by a fixed lookup table based on grid size (for example, three bombs on a 5×5 grid, twenty on a 10×10 grid). There is no option for players to adjust how many bombs appear. This is an acceptable simplification; the scaling table provides a reasonable default experience across all supported grid sizes.
