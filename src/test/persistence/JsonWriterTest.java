package persistence;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import model.Exercise;
import model.Logbook;
import model.Muscles;
import model.WorkoutSession;

// Referenced from JsonSerialization Demo
// https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
public class JsonWriterTest extends JsonTest {
    @Test
    public void testWriterInvalidFile() {
        
        try {
            Logbook lb = new Logbook("./data/my\0illegal:fileName.json");
            lb.saveLogBook();
            fail("IOException was expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    public void testWriterEmptyLog() {
        try {
            Logbook lb = new Logbook("./data/testReaderEmptyLog.json");
            lb.saveLogBook(); // this saves an empty logbook: []

            // now read it back to confirm
            JsonReader reader = new JsonReader("./data/testReaderEmptyLog.json");
            List<WorkoutSession> sessions = reader.read();
            assertTrue(sessions.isEmpty());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }
    
    @Test
    public void testWriterGenralLogbook() {
        try {
            Logbook lb = new Logbook("./data/testWriterGeneralLog.json");

            // create and add session 1
            WorkoutSession s1 = new WorkoutSession("2025/11/01");
            s1.addExercise(new Exercise("Bench press", Muscles.CHEST, 135, 3, 5));
            s1.addExercise(new Exercise("Pull down", Muscles.BACK, 120, 4, 8));
            lb.addSession(s1);

            // create and add session 2
            WorkoutSession s2 = new WorkoutSession("2025/11/03");
            s2.addExercise(new Exercise("Squat", Muscles.LEGS, 225, 3, 5));
            lb.addSession(s2);

            // save the logbook - this is the method to test
            lb.saveLogBook();

            // now read it back and verify
            JsonReader reader = new JsonReader("./data/testWriterGeneralLog.json");
            List<WorkoutSession> sessions = reader.read();
            assertEquals(2, sessions.size());

            // check session 1
            WorkoutSession session1 = sessions.get(0);
            assertEquals("2025/11/01", session1.getDate());
            assertEquals(2, session1.getExercises().size());
            checkExercise(session1.getExercises().get(0), "Bench press", Muscles.CHEST, 135, 3, 5);
            checkExercise(session1.getExercises().get(1), "Pull down", Muscles.BACK, 120, 4, 8);

            // check session 2
            WorkoutSession session2 = sessions.get(1);
            assertEquals("2025/11/03", session2.getDate());
            assertEquals(1, session2.getExercises().size());
            checkExercise(session2.getExercises().get(0), "Squat", Muscles.LEGS, 225, 3, 5);

        } catch (IOException e) {
            fail("Exception should not have been thrown: " + e.getMessage());
        }
    }
}
