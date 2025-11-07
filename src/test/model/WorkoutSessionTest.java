package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

public class WorkoutSessionTest {
    private WorkoutSession session;
    private Exercise ex1;
    private Exercise ex2;

    @BeforeEach
    void runBefore() {
        session = new WorkoutSession("2025/11/06");
        ex1 = new Exercise("Bench Press", Muscles.CHEST, 135, 3 ,5);
        ex2 = new Exercise("Squat", Muscles.LEGS, 225, 3 ,5);
    }

    @Test
    void testConstructor() {
        assertEquals("2025/11/06", session.getDate());
        assertTrue(session.getExercises().isEmpty());
    }

    @Test
    void testAddExercise() {
        session.addExercise(ex1);
        List<Exercise> exercises = session.getExercises();
        assertEquals(1, exercises.size());
        assertEquals(ex1, exercises.get(0));

        session.addExercise(ex2);
        exercises = session.getExercises();
        assertEquals(2, exercises.size());
        assertEquals(ex1, exercises.get(0));
        assertEquals(ex2, exercises.get(1));
    }

    @Test
    void testRemoveExercise() {
        session.addExercise(ex1);
        session.addExercise(ex2);
        assertEquals(2, session.getExercises().size());

        // Test remove non-existent
        assertFalse(session.removeExercise("Overhead Press"));
        assertEquals(2, session.getExercises().size());

        // Test remove (case-insensitive)
        assertTrue(session.removeExercise("bench press"));
        List<Exercise> exercises = session.getExercises();
        assertEquals(1, exercises.size());
        assertEquals(ex2, exercises.get(0)); // only squat remains

        // Test remove last item
        assertTrue(session.removeExercise("Squat"));
        exercises = session.getExercises();
        assertTrue(exercises.isEmpty());
    }

    @Test
    void testSetDate() {
        session.setDate("2025/12/25");
        assertEquals("2025/12/25", session.getDate());
    }

    @Test
    void testToJson() {
        session.addExercise(ex1);
        session.addExercise(ex2);
        var json = session.toJson();

        assertEquals("2025/11/06", json.getString("date"));
        var jsonExercises = json.getJSONArray("exercises");
        assertEquals(2, jsonExercises.length());

        // Check first exercise
        var jsonEx1 = jsonExercises.getJSONObject(0);
        assertEquals("Bench press", jsonEx1.getString("exercise name"));
        assertEquals(135, jsonEx1.getInt("weight"));

        // Check second exercise
        var jsonEx2 = jsonExercises.getJSONObject(1);
        assertEquals("Squat", jsonEx2.getString("exercise name"));
        assertEquals(225, jsonEx2.getInt("weight"));
    }
}
