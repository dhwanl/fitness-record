package persistence;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import model.Muscles;
import model.WorkoutSession;


// Referenced from JsonSerialization Demo
// https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
public class JsonReaderTest extends JsonTest {

    @Test
    public void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("./data/noSuchFile.json");

        try {
            reader.read();
            fail("IOExcetion expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    public void testReaderEmptyfile() {
        // testReaderEmptyLog.json file must contain: []
        JsonReader reader = new JsonReader("./data/testReaderEmptyLog.json");
        try {
            List<WorkoutSession> sessions = reader.read();
            System.out.println(sessions.isEmpty());
            assertTrue(sessions.isEmpty());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    @Test
    public void testReaderGeneral() {
        JsonReader reader = new JsonReader("./data/testReaderGeneral.json");
        try {
            // This test needs testReaderGeneral.json to be in new format
            List<WorkoutSession> sessions = reader.read();
            assertEquals(2, sessions.size());
            
            // check session 1
            WorkoutSession session1 = sessions.get(0);
            assertEquals("2025/10/01", session1.getDate());
            assertEquals(1, session1.getExercises().size());
            // use the capitalization from the Exercise class
            checkExercise(session1.getExercises().get(0), "Bench press", Muscles.CHEST, 100, 3, 12);

            // check session 2
            WorkoutSession session2 = sessions.get(1);
            assertEquals("2025/10/03", session2.getDate());
            assertEquals(2, session2.getExercises().size());
            checkExercise(session2.getExercises().get(0), "Squat", Muscles.LEGS, 200, 3, 5);
            checkExercise(session2.getExercises().get(1), "Leg press", Muscles.LEGS, 300, 4, 10);
            
        } catch (IOException e) {
            fail("Couldn't read from file: " + e.getMessage());
        }
    }
}
