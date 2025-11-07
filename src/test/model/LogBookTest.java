package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

public class LogBookTest {
    private Logbook logbook;
    private WorkoutSession session1;
    private WorkoutSession session2;
    private Exercise chestEx;
    private Exercise backEx;
    private Exercise legEx;

    @BeforeEach
    void runBefore() {
        logbook = new Logbook("./data/test_logbook.json"); // Test file

        // Session 1: "2025/10/01" (Chest/Back)
        session1 = new WorkoutSession("2025/10/01");
        chestEx = new Exercise("Bench Press", Muscles.CHEST, 150, 3, 5);
        backEx = new Exercise("Pull Up", Muscles.BACK, 0, 5, 8);
        session1.addExercise(chestEx);
        session1.addExercise(backEx);

        // Session 2: sessions to logbook
        session2 = new WorkoutSession("2025/10/03");
        legEx = new Exercise("Squat", Muscles.LEGS, 250, 3, 5);
        session2.addExercise(legEx);

        // adds sessions to logbook
        logbook.addSession(session1);
        logbook.addSession(session2);
    }

    @Test
    void testConstructor() {
        Logbook emptyLogbook = new Logbook("./data/file.json");
        assertTrue(emptyLogbook.getAllSessions().isEmpty());
    }

    @Test
    void testGetSessionByDate() {
        assertEquals(session1, logbook.getSessionByDate("2025/10/01"));
        assertEquals(session2, logbook.getSessionByDate("2025/10/03"));
        assertNull(logbook.getSessionByDate("2025/11/11"));
    }

    @Test
    void testFilterSessionsByDate() {
        List<WorkoutSession> filtered = logbook.filterSessionsByDate("2025/10/01");
        assertEquals(1, filtered.size());
        assertEquals(session1, filtered.get(0));

        List<WorkoutSession> notFound = logbook.filterSessionsByDate("2025/11/11");
        assertTrue(notFound.isEmpty());
    }

    @Test
    void testFilterSessionsByMuscle() {
        List<WorkoutSession> chestSessions = logbook.filterSessionsByMuscle(Muscles.CHEST);
        assertEquals(1, chestSessions.size());
        assertEquals(session1, chestSessions.get(0));

        List<WorkoutSession> legSessions = logbook.filterSessionsByMuscle(Muscles.LEGS);
        assertEquals(1, legSessions.size());
        assertEquals(session2, legSessions.get(0));

        List<WorkoutSession> backSessions = logbook.filterSessionsByMuscle(Muscles.BACK);
        assertEquals(1, backSessions.size());
        assertEquals(session1, backSessions.get(0)); // Session 1 had both

        List<WorkoutSession> bicepSessions = logbook.filterSessionsByMuscle(Muscles.BICEPS);
        assertTrue(bicepSessions.isEmpty());
    }

    @Test
    void testGetAllExercisesByMuscle() {
        List<Exercise> chestExercises = logbook.getAllExercisesByMuscle(Muscles.CHEST);
        assertEquals(1, chestExercises.size());
        assertEquals(chestEx, chestExercises.get(0));
    }

    @Test
    void testClearLogbook() {
        assertFalse(logbook.getAllSessions().isEmpty());
        logbook.clearLogbook();
        assertTrue(logbook.getAllSessions().isEmpty());
    }
}
