package persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import model.Exercise;
import model.Muscles;

// Referenced from JsonSerialization Demo
public class JsonTest {
    /*
     * Helper to check if an Exercise object has the correct fields.
     */
    protected void checkExercise(Exercise exercise, String exerciseName, Muscles muscleType, 
                                        int weightLifted, int numSets, int numReps) {
        
        assertEquals(exerciseName, exercise.getExerciseName());
        assertEquals(muscleType, exercise.getMuscleType());
        assertEquals(weightLifted, exercise.getWeightLifted());
        assertEquals(numSets, exercise.getNumSets());
        assertEquals(numReps, exercise.getNumReps());
    }
}
