package com.mycompany.snakegame;

import com.mycompany.snakegame.db.Database;
import com.mycompany.snakegame.db.ScoreDao;
import com.mycompany.snakegame.exception.DatabaseException;
import com.mycompany.snakegame.service.ScoreService;
import com.mycompany.snakegame.ui.MainFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Entry point of the CS0070 final project: SNAKE GAME with Swing, threads and JDBC.
 *
 * =====================================================================================
 * MODULE 1 PROOF - Introduction to Java Technology
 *   - Program structure: package, imports, class, and the main() method below.
 *   - Source -> compiler -> bytecode -> JVM: NetBeans "Build" compiles the .java files to
 *     .class bytecode; "Run" starts the JVM, which loads SnakeGame and calls main().
 *   - IDE workflow used: Create (Maven project) -> Configure (pom.xml) -> Code -> Build ->
 *     Run -> Debug (breakpoint in GameEngine.tick()).
 *   - The About dialog and the console line below print the JDK/JVM details at run time.
 *
 * MODULE 2 - Java Programming Fundamentals
 *   Data types, constants (final), operators, casting, arrays, Scanner, formatted output,
 *   if/switch/for/while/do-while, methods with parameters and return values.
 *
 * MODULE 3 - Classes, objects, encapsulation, relationships
 *   Private fields + getters/setters, constructors, this, static members;
 *   HAS-A: GameEngine has a Snake and a Food;  IS-A: Snake is a GameObject.
 *
 *   UML (simplified):
 *      GameObject <|-- Snake         GameObject <|-- Food <|-- NormalFood, GoldenFood
 *      GameEngine o-- Snake, Food    MainFrame --> GamePanel, ScoreService
 *      ScoreService --> ScoreDao --> Database
 *
 * MODULE 4 - Inheritance, polymorphism, abstraction, interfaces
 *   Abstract GameObject/Food, overriding + super, Drawable/Resettable/GameListener/Dao
 *   interfaces, dynamic method dispatch in GamePanel.paintComponent().
 *
 * MODULE 5 - Exceptions + modern Java
 *   Custom checked exceptions, try/catch/finally, throw/throws, packages, enums, records,
 *   generics, wrapper classes, var, lambdas, method references, switch expressions,
 *   pattern-matching instanceof, text blocks.
 *
 * MODULE 6 - Collections framework
 *   ArrayList, LinkedList (snake body), ArrayDeque (queued turns), HashSet (occupied cells),
 *   HashMap, TreeSet, Iterator, Comparator, Collections.sort/max/binarySearch.
 *
 * MODULE 7 - Multithreading + Swing
 *   ClockThread (extends Thread), ScoreSaver (implements Runnable), synchronized
 *   SessionStats, start/sleep/interrupt/join; JFrame, JPanel, layouts, listeners, Timer.
 *
 * MODULE 8 - JDBC + application integration
 *   Layered design GUI -> Service -> DAO -> Database, PreparedStatement CRUD, ResultSet,
 *   try-with-resources, validation, safe error handling, JTable display.
 * =====================================================================================
 */
public class SnakeGame {

    public static void main(String[] args) {
        // MODULE 1 + 2 PROOF: formatted console output of the runtime environment.
        System.out.printf("Starting Snake Game on Java %s (%s)%n",
                Runtime.version(), System.getProperty("java.vm.name"));

        final String databaseWarning = prepareDatabase();

        // MODULE 7 PROOF: all Swing work is created on the Event Dispatch Thread.
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(new ScoreService(new ScoreDao()));
            frame.setVisible(true);
            if (databaseWarning != null) {
                JOptionPane.showMessageDialog(frame, databaseWarning, "Scores unavailable",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    /** MODULE 5 + 8 PROOF: a failed database must not stop the game from starting. */
    private static String prepareDatabase() {
        try {
            Database.init();
            return null;
        } catch (DatabaseException e) {
            return e.getMessage() + "\nYou can still play, but scores cannot be saved.";
        }
    }
}
