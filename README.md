# Snake Game - CS0070 Final Project (NetBeans / Maven)

## How to open and run in NetBeans
1. File > Open Project > select this `SnakeGame` folder (it has the `pom.xml`).
2. Right-click the project > **Clean and Build** (Maven downloads the SQLite JDBC driver once, so you need internet the first time).
3. Right-click the project > **Run**. Main class: `com.mycompany.snakegame.SnakeGame`.
4. Scores are stored in `snake_scores.db`, created in the project folder on first run.

Controls: arrow keys or W/A/S/D = steer, P or SPACE = pause, ENTER = start / play again.

## Where each module is proven
Search the code for `MODULE n PROOF` (Ctrl+Shift+F in NetBeans). The big map is in the header comment of `SnakeGame.java`.

| Module | Main files |
|---|---|
| 1 Java technology, IDE, debugging | `SnakeGame` (header + main), `MainFrame.showAbout()`, `GameEngine.tick()` (breakpoint spot) |
| 2 Fundamentals | `GameConfig`, `Snake.drawFace`, `MainFrame.promptAndSave` (do-while), `onScoreChanged` (cast) |
| 3 Classes / encapsulation / relationships | `GameObject`, `Snake`, `Food` (static), `Direction` (this) |
| 4 Inheritance / polymorphism / abstraction / interfaces | `GameObject`, `Food`, `NormalFood`, `GoldenFood`, `Drawable`, `Resettable`, `GameListener`, `GamePanel.paintComponent` |
| 5 Exceptions + modern Java | `exception/*`, `ScoreService`, `Score` (record), `Direction`/`Difficulty` (enums), `Dao<T,K>`, `Lists`, `GameConfig` (text block) |
| 6 Collections | `Snake` (LinkedList + Iterator), `GameEngine` (ArrayDeque, HashSet, ArrayList), `ScoreService` (Comparator, sort, binarySearch, HashMap, TreeSet) |
| 7 Threads + Swing | `ClockThread`, `ScoreSaver`, `SessionStats`, `MainFrame`, `GamePanel`, `LeaderboardDialog` |
| 8 JDBC + integration | `Database`, `ScoreDao`, `ScoreService`, `LeaderboardDialog`, layered GUI > Service > DAO > DB |

## Bugs fixed from the original single-class version
- Array overflow: `snakeX[bodyParts]` could go out of bounds once the snake filled the board.
- Apples could spawn on top of the snake.
- Pressing two keys quickly (e.g. UP then LEFT while moving right) could reverse the snake into itself; turns are now queued, one per tick.
- Keys were handled twice (key bindings and a `KeyListener` that was never registered); only key bindings remain.
- The snake started with all body parts on the same cell; it now starts in a line.
- Added pause, win state, difficulty levels, and a saved leaderboard.

## Using MySQL instead of SQLite
Change `URL` in `Database.java`, add the MySQL Connector/J dependency to `pom.xml`, and use `DriverManager.getConnection(URL, user, password)`.
