package ui;

import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import model.Exercise;
import model.Logbook;
import model.Muscles;
import model.WorkoutSession;
import model.PrintEventLog;

import java.util.List;

/*
* Represent application's main window frame
* This class now acts as the VIEW and CONTROLLER
* It sends all user actions to the LOGBOOK (the model)
*/
public class FitnessRecordUI extends JFrame {
    private static final int WIDTH = 350;
    private static final int HEIGHT = 700;
    private static final String IMAGE_STORE = "./image/background.png";
    
    // MODEL
    private Logbook logbook;

    // View components
    private JFrame parentFrame;
    private JComboBox<Muscles> muscleComboBox;
    private JTextArea logDisplay;
    private JScrollPane scrollPane;
    private JTextField yearField;
    private JTextField monthField;
    private JTextField dayField;
    private JTextField nameField;
    private JTextField weightField;
    private JTextField setsField;
    private JTextField repsField;
    private String[] labels = {
        "Exercise Name", 
        "Muscle Type", 
        "Weight (kg)", 
        "Number of Reps", 
        "Number of Sets", 
        "Date yyyy/mm/dd"
    };
    

    /*
     * MODIFIES: this
     * EFFECTS: creates the main application window and initialize components
     */
    public FitnessRecordUI() {
        // Initialize the LogBook. This is the Model
        // It automatically knows where to save/load from.
        logbook = new Logbook("./data/fitness_log.json");

        parentFrame = new JFrame("Fitness Record");
        parentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        parentFrame.setSize(WIDTH, HEIGHT);
        parentFrame.setLayout(new BorderLayout());
        
        // Confirm Exit code
        JRootPane rootPane = parentFrame.getRootPane();
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap actionMap = rootPane.getActionMap();
        KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        
        inputMap.put(escapeKey, "CONFIRM_EXIT");
        actionMap.put("CONFIRM_EXIT", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // call a new confirmation method
                confirmExit();
            }
        });

        // Attempt to load existing logs on startup
        try {
            logbook.loadLogBook();
        } catch (IOException e) {
            System.out.println("No existing log file found. Starting fresh.");
        }

        parentFrame.add(new ImagePanel(IMAGE_STORE), BorderLayout.CENTER);
        addButtonPanel();

        centreOnScreen();
        parentFrame.setVisible(true);
    }

    /*
     * Displays a confirmation dialog before exiting this application.
     * Handles user's "Yes" (Enter), "No", or ESC key presses.
     */
    private void confirmExit() {
        String message = "Just double check! would you like to close this application?";
        String title = "Confirm Exit";

        // define our custom button text
        Object[] options = {"Yes", "No"};

        // This creates the "Yes/No" pop-uo
        int result = JOptionPane.showOptionDialog(
            parentFrame,                            // center it on the main window
            message,                                // the custom message above
            title,                                  // the window title
            JOptionPane.YES_NO_CANCEL_OPTION,       // the "Yes" and "No" buttons
            JOptionPane.QUESTION_MESSAGE,           // shows a question mark icon
            null,                              // icon (null for default)
            options,                                // use our custom button array ("Yes", "No")
            options[0]                              // The default button to be highlighted ("Yes")
        );

        // check which button was pressed
        // YES_OPTION == 0, NO_OPTION == 1
        if (result == JOptionPane.YES_OPTION) {
            // user clicked "Yes" or pressed ENTER
            PrintEventLog.printEventLog();
            System.exit(0);
        }

        // If user clicks "No" (result == 1)
        // or presses ESC/ clicks the [x] button (result == -1)
        // the dialog simply closes and nothing happens
    }
    /*
     * MODIFIES: this
     * EFFECTS: creates and adds the option button panel to the main frame
     */
    private void addButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(8, 1));
        buttonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        buttonPanel.add(createButton("Add an exercise", e -> addExercise()));
        buttonPanel.add(createButton("Remove an exercise", e -> removeExercise()));
        buttonPanel.add(createButton("Update the log", e -> updateLog()));
        buttonPanel.add(createButton("Filter workout log", e -> filteredLog()));
        buttonPanel.add(createButton("View all exercises you added", 
                                            e -> displayAllLogs()));
        buttonPanel.add(createButton("Save logs to file", e -> saveLogsToFile()));
        buttonPanel.add(createButton("Load logs from file", e -> loadLogsFromFile()));
        buttonPanel.add(createButton("Exit", e -> {
            PrintEventLog.printEventLog();
            System.exit(0);
        }));
    
        parentFrame.add(buttonPanel, BorderLayout.SOUTH);
    }

    /*
     * REQUIRES: text != null, action != null
     * EFFECTS: creates and returns a button with the text and action listener
     */
    private JButton createButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        return button;
    }

    /*
     * MODIFIES: logDisplay, scrollPane, parentFrame
     * EFFECTS: if logDisplay is null, initializes a new JTextArea and wraps it in a JScrollPane.
     */
    private void createDisplayLog() {
        if (logDisplay == null) {
            logDisplay = new JTextArea();
            logDisplay.setEditable(false);
            scrollPane = new JScrollPane(logDisplay);
            scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
            parentFrame.add(scrollPane, BorderLayout.CENTER);
            centreOnScreen();
            parentFrame.setVisible(true);
        }
    }

    /*
     * MODIFIES: this
     * EFFECTS: open a window to add a new exercise log
     */
    private void addExercise() {
        createDisplayLog();
        JDialog dialog = createDialog("Add Exercise", 400, 400);
        
        JPanel addExercisePanel = new JPanel();
        addExercisePanel.setLayout(new GridLayout(labels.length, 2));
        addExercisePanel.setBorder(new EmptyBorder(10, 5, 10, 5));
        
        addExerciseFormat(addExercisePanel);
        
        dialog.add(addExercisePanel, BorderLayout.CENTER);
        dialog.add(exerciseButtonPanel(dialog, "addEx"), BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    /*
     * REQUIRES: title != null, w > 0, h > 0
     * EFFECTS: creates a new window with the specified title, width, and height
     */
    private JDialog createDialog(String title, int w, int h) {
        JDialog dialog = new JDialog(parentFrame, title, true);
        dialog.setSize(w, h);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setLayout(new BorderLayout());
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // 1. Get the dialog's root pane (its main content area)
        JRootPane rootPane = dialog.getRootPane();
        
        // 2. Use the more robust "WHEN_ANCESTOR" binding
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap actionMap = rootPane.getActionMap();

        // 3. Define the ESC key
        KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

        // 4. Map the ESC key to an "action name"
        inputMap.put(escapeKey, "CLOSE_DIALOG");

        // 5. Map the "action name" to an actual action
        actionMap.put("CLOSE_DIALOG", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // This just closes the pop-up, not the whole app
                dialog.dispose();
            }
        });
        
        return dialog;
    }

    /*
     * REQUIRES: purpose is one of "addEx", "removeEx", "updateEx"
     * EFFECTS: creates a panel with buttons for saving and canceling
     */
    private JPanel exerciseButtonPanel(JDialog dialog, String purpose) {
        JPanel buttonPanel = new JPanel();
        JButton button = new JButton();
        JButton cancelButton = createCancelButton(dialog, "cancel");


        if (purpose.equals("addEx")) {
            button = createSaveButton(dialog);
        } else if (purpose.equals("removeEx")) {
            button = createSaveButtonForRemove(dialog);
        } else if (purpose.equals("updateEx")) {
            button = createSaveButtonForUpdate(dialog);
        }

        buttonPanel.add(button);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    /*
     * REQUIRES: dialog != null
     * MODIFIES: this, Log.exercises
     * EFFECTS: creates and adds a new exercise to the logs with the success message
     */
    private JButton createSaveButton(JDialog dialog) {
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                String exerciseName = nameField.getText();
                Muscles muscleType =  (Muscles) muscleComboBox.getSelectedItem();
                int weight = Integer.parseInt(weightField.getText());
                int reps = Integer.parseInt(repsField.getText());
                int sets = Integer.parseInt(setsField.getText());
                String date = String.format("%s/%s/%s", yearField.getText(), monthField.getText(), dayField.getText());
                
                // creates the exercise object
                Exercise exercise = new Exercise(exerciseName, muscleType, weight, reps, sets);
                // finds the session for that date
                WorkoutSession session = logbook.getSessionByDate(date);
                
                if (session == null) {
                    // if no session exists for that date, create one
                    session = new WorkoutSession(date);
                    logbook.addSession(session);
                }

                // adds the exercise to that day's session
                session.addExercise(exercise);
                
                // updates display and close
                displayLog(exercise, date, "Exercise Added");
                JOptionPane.showMessageDialog(dialog, "Exercise added successfully");
                dialog.dispose();

                // refreshes the main view to show the new exercise
                displayAllLogs();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter vaild numbers for weight, reps, and sets:)");
            }
        });

        return saveButton;
    }

    /*
     * REQUIRES: dialog != null
     * MODIFIES: this
     * EFFECTS: creates and returns a cancel button that closes the window
     */
    private JButton createCancelButton(JDialog dialog, String title) {
        JButton cancelButton = new JButton(title);
        cancelButton.addActionListener(e -> dialog.dispose());
        return cancelButton;
    }

    /*
     * REQUIRES: e != null, date != null, title != null
     * MODIFIES: this
     * EFFECTS: appends exercise to the log display area
     */
    private void displayLog(Exercise e, String date, String title) {
        logDisplay.append(String.format(
                "\n" + title + ": %s\n Muscle: %s\n Weight: %d kg\n Reps: %d\n Sets: %d\n Date: %s\n", 
                e.getExerciseName(), 
                e.getMuscleType(), 
                e.getWeightLifted(), 
                e.getNumReps(), 
                e.getNumSets(), 
                date
            )
        );
    }

    /*
     * REQUIRES: addExercisePanel != null
     * MODIFIES: this
     * EFFECTS: adds input field to the specified panel for adding a new exercise
     */
    private void addExerciseFormat(JPanel addExercisePanel) {

        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i]);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            addExercisePanel.add(label);
            
            if (i == 0) {
                nameField = new JTextField();
                addExercisePanel.add(nameField);
            } else if (i == 1) {
                muscleComboBox = createMuscleCombo();
                addExercisePanel.add(muscleComboBox);
            } else if (i == 2) {
                weightField = new JTextField();
                addExercisePanel.add(weightField);
            } else if (i == 3) {
                repsField = new JTextField();
                addExercisePanel.add(repsField);
            } else if (i == 4) {
                setsField = new JTextField();
                addExercisePanel.add(setsField);
            } else if (i == 5) {
                getDate(addExercisePanel);
            }
        }
    }

    /*
     * REQUIRES: addExercisePanel != null
     * MODIFIES: this
     * EFFECTS: initializes and adds input field for date to the panel
     */
    private void getDate(JPanel addExercisePanel) {
        yearField = new JTextField("YYYY");
        monthField = new JTextField("MM");
        dayField = new JTextField("DD");

        JPanel datePanel = new JPanel();
        datePanel.setLayout(new GridLayout(1, 3));
        datePanel.add(yearField);
        datePanel.add(monthField);
        datePanel.add(dayField);
        addExercisePanel.add(datePanel);
    }

    /*
     * MODIFIES: this
     * EFFECTS: creates a new window for user to remove a specific exercise log
     */
    private void removeExercise() {
        createDisplayLog();
        JDialog dialog = createDialog("Remove Exercise", 300, 150);

        JPanel removeExercisePanel = new JPanel();
        removeExercisePanel.setLayout(new GridLayout(2, 2));
        removeExercisePanel.setBorder(new EmptyBorder(10, 5, 10, 5));

        removeExericseFormat(removeExercisePanel);

        dialog.add(removeExercisePanel, BorderLayout.CENTER);

        dialog.add(exerciseButtonPanel(dialog, "removeEx"), BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    /*
     * REQUIRES: dialog != null
     * MODIFIES: this, logbook
     * EFFECTS: creates a button that removes an exercise from the correct workout sessionin the logbook
     */
    private JButton createSaveButtonForRemove(JDialog dialog) {
        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(e -> {
            try {
                // gets exerciseName and date from fields
                String exerciseName = nameField.getText();
                String date = String.format("%s/%s/%s", yearField.getText(), monthField.getText(), dayField.getText());

                // finds the session from logbook
                WorkoutSession session = logbook.getSessionByDate(date);

                // checks if the session for that date even exists
                if (session == null) {
                    JOptionPane.showMessageDialog(dialog, "No workout session found for date: " + date);
                } else {
                    // the session exists, so tell it to remove the exercise
                    boolean removed = session.removeExercise(exerciseName);

                    // checks if the exercise was successfully found and removed
                    if (removed) {
                        JOptionPane.showMessageDialog(dialog, "Exercise '" + exerciseName + "' removed successfully.");

                        // refreshes the main display to show the change
                        displayAllLogs();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Exercise '" + exerciseName + "' not found on this date.");
                    }
                }
                
                // closes the pop-up dialog
                dialog.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "An error occured. Please check your inputs.");
            }
        });

        return removeButton;
    }

    /*
     * REQUIRES: removeExercisePanel != null
     * MODIFIES: this
     * EFFECTS: adds input fields for removing an exercise (exercise name, date)
     */
    private void removeExericseFormat(JPanel removeExercisePanel) {
        removeExercisePanel.add(new JLabel(labels[0]));
        nameField = new JTextField();
        removeExercisePanel.add(nameField);

        removeExercisePanel.add(new JLabel(labels[5]));
        getDate(removeExercisePanel);
    }

    /*
     * MODIFIES: this
     * EFFECTS: find exercise log for updating. This just opens the "find" dialog.
     */
    private void updateLog() {
        createDisplayLog();
        JDialog dialog = createDialog("Update Exercise: Step 1 (Find)", 300, 150);
        
        JPanel updateExercisePanel = new JPanel();
        updateExercisePanel.setLayout(new GridLayout(2, 2));
        updateExercisePanel.setBorder(new EmptyBorder(10, 5, 10, 5));

        updateExericseFormat(updateExercisePanel);

        dialog.add(updateExercisePanel, BorderLayout.CENTER);
        dialog.add(exerciseButtonPanel(dialog, "updateEx"), BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    /*
     * REQUIRES: updateExercisePanel != null
     * MODIFIES: this
     * EFFECTS: adds input field for finding an exercise log to update
     */
    private void updateExericseFormat(JPanel updateExercisePanel) {
        updateExercisePanel.add(new JLabel(labels[0]));
        nameField = new JTextField();
        updateExercisePanel.add(nameField);

        updateExercisePanel.add(new JLabel(labels[5]));
        getDate(updateExercisePanel);
    }

    /*
     * REQURIES: dialog != null
     * MODIFIES: this
     * EFFECTS: finds a matching exercise log based on the input name and date, and open a new window.
     */
    private JButton createSaveButtonForUpdate(JDialog dialog) {
        JButton saveButton = new JButton("Find");
        saveButton.addActionListener(e -> {
            try {
                // gets user input
                String exerciseName = nameField.getText();
                String date = String.format("%s/%s/%s", yearField.getText(), monthField.getText(), dayField.getText());

                // finds the session
                WorkoutSession session = logbook.getSessionByDate(date);
                if (session == null) {
                    JOptionPane.showMessageDialog(dialog, "No workout session found for date: " + date);
                    return;
                }

                // finds the exercise within the session
                Exercise exerciseToUpdate = null;
                for (Exercise ex : session.getExercises()) {
                    if (ex.getExerciseName().equalsIgnoreCase(exerciseName)) {
                        exerciseToUpdate = ex;
                        break;
                    }
                }

                // opens the update dialog if found, otherwise show error
                if (exerciseToUpdate != null) {
                    // We found it! Pass both the session and the exercise to the next step
                    updateOptions(session, exerciseToUpdate);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Exercise '" + exerciseName + "' not found on this date.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter vaild numbers for date:)");
            }
        });

        return saveButton;
    }
    
    /*
     * REQUIRES: session != null, exercise != null
     * MODIFIES: session, exercise
     * EFFECTS: open a new window for updating the found exercise's info
     */
    private void updateOptions(WorkoutSession session, Exercise exercise) {
        JDialog dialog = createDialog("Update Exercise: Step 2 (Edit)", 400, 400);

        JPanel updateExercisePanel = new JPanel();
        updateExercisePanel.setLayout(new GridLayout(0, 1));
        updateExercisePanel.setBorder(new EmptyBorder(10, 5, 10, 5));

        JComboBox<String> updateFieldComboBox = new JComboBox<>(labels);
        
        updateExercisePanelWithFields(updateExercisePanel, updateFieldComboBox, session, exercise);

        JPanel datePanel = new JPanel(new GridLayout(1, 3));
        datePanel.add(yearField);
        datePanel.add(monthField);
        datePanel.add(dayField);
    
        updateExercisePanel.add(datePanel);

        updateFieldComboBoxEventHandler(updateFieldComboBox, dialog);

        // sets up the buttons
        JPanel buttonPanel = new JPanel();
        JButton updateButton = new JButton("Update");
        JButton cancelButton = createCancelButton(dialog, "Cancel");

        // This helper adds the final update logic to the button
        updateEventHandler(updateButton, updateFieldComboBox, session, exercise, dialog);

        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);

        dialog.add(updateExercisePanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    /*
     * REQUIRES: updateFieldComboBox != null, dialog != null
     * MODIFIES: this
     * EFFECTS: adds event handler to the combo box to visualize the input field by the option users choose
     */
    private void updateFieldComboBoxEventHandler(JComboBox<String> updateFieldComboBox, JDialog dialog) {
        updateFieldComboBox.addActionListener(e -> {
            String selected = (String) updateFieldComboBox.getSelectedItem();
            nameField.setVisible("Exercise Name".equals(selected));
            muscleComboBox.setVisible("Muscle Type".equals(selected));
            weightField.setVisible("Weight (kg)".equals(selected));
            repsField.setVisible("Number of Reps".equals(selected));
            setsField.setVisible("Number of Sets".equals(selected));
            boolean isDate = "Date yyyy/mm/dd".equals(selected);
            yearField.setVisible(isDate);
            monthField.setVisible(isDate);
            dayField.setVisible(isDate);
            dialog.pack();
        });
    }

    /*
     * REQUIRES: all params != null
     * MODIFIES: session, exercise
     * EFFECTS: updates the selected field in the exercise or session with the new input value
     */
    private void updateEventHandler(JButton updateButton, JComboBox<String> updateFieldComboBox, 
                                            WorkoutSession session, Exercise exercise, JDialog dialog) {
        updateButton.addActionListener(e -> {
            try {
                String selected = (String) updateFieldComboBox.getSelectedItem();
                if ("Exercise Name".equals(selected)) {
                    exercise.setExerciseName(nameField.getText());
                } else if ("Muscle Type".equals(selected)) {
                    exercise.setMuscleType((Muscles) muscleComboBox.getSelectedItem());
                } else if ("Weight (kg)".equals(selected)) {
                    exercise.setWeightLifted(Integer.parseInt(weightField.getText()));
                } else if ("Number of Reps".equals(selected)) {
                    exercise.setNumReps(Integer.parseInt(repsField.getText()));
                } else if ("Number of Sets".equals(selected)) {
                    exercise.setNumSets(Integer.parseInt(setsField.getText()));
                } else if ("Date yyyy/mm/dd".equals(selected)) {
                    String updatedDate = String.format("%s/%s/%s", 
                                            yearField.getText(), monthField.getText(), dayField.getText());
                    session.setDate(updatedDate);
                }
                JOptionPane.showMessageDialog(dialog, "Exercise updated successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input. Please check your values!");
            }
        });
    }

    /*
     * REQUIRES: all params != null
     * MODIFIES: this
     * EFFECTS: adds fields to update an exercise and hide all input fields initially
     */
    private void updateExercisePanelWithFields(JPanel updateExercisePanel, JComboBox<String> updateFieldComboBox, 
                                                    WorkoutSession session, Exercise exercise) {
        
        // creates components and populate with existing data
        nameField = new JTextField(exercise.getExerciseName());
        muscleComboBox = createMuscleCombo();
        muscleComboBox.setSelectedItem(exercise.getMuscleType());
        weightField = new JTextField(String.valueOf(exercise.getWeightLifted()));
        repsField = new JTextField(String.valueOf(exercise.getNumReps()));
        setsField = new JTextField(String.valueOf(exercise.getNumSets()));
        
        String[] dateParts = session.getDate().split("/");
        yearField = new JTextField(dateParts[0]);
        monthField = new JTextField(dateParts[1]);
        dayField = new JTextField(dateParts[2]);

        // adds components to the panel
        updateExercisePanel.add(new Label("Choose Fields to Update:"));
        updateExercisePanel.add(updateFieldComboBox);
        updateExercisePanel.add(muscleComboBox);
        updateExercisePanel.add(nameField);
        updateExercisePanel.add(weightField);
        updateExercisePanel.add(repsField);
        updateExercisePanel.add(setsField);

        nameField.setVisible(false);
        muscleComboBox.setVisible(false);
        weightField.setVisible(false);
        repsField.setVisible(false);
        setsField.setVisible(false);
        yearField.setVisible(false);
        monthField.setVisible(false);
        dayField.setVisible(false);
    }

    /*
     * MODIFIES: this
     * EFFECTS: iterates through all logs and display the details in the main panel
     */
    private void displayAllLogs() {
        createDisplayLog();
        logDisplay.setText(""); // clear the display

        List<WorkoutSession> sessions = logbook.getAllSessions();

        if (sessions.isEmpty()) {
            logDisplay.setText("No exercises have been logged yet.");
        } else {
            for (WorkoutSession session : sessions) {
                // adds a header for the date
                logDisplay.append("\n===============================\n");
                logDisplay.append("    DATE: " + session.getDate() + "\n");
                logDisplay.append("===============================\n");

                List<Exercise> exercises = session.getExercises();
                if (exercises.isEmpty()) {
                    logDisplay.append("  (Rest Day / No exercises logged)\n");
                } else {
                    for (Exercise ex : exercises) {
                        displayLog(ex, session.getDate(), "Exercise");
                    }
                }
            }
        }
    }

    /*
     * REQUIRES: sessions != null, title != null
     * MODIFIES: this
     * EFFECTS: Display a *filtered* list of sessions in the main display
     */
    private void displayAllLogs(List<WorkoutSession> sessions, String title) {
        createDisplayLog();
        logDisplay.setText("");
        logDisplay.append("--- " + title + " ---\n");
        if (sessions.isEmpty()) {
            logDisplay.append("No workout found matching this filter.");
            return;
        }

        for (WorkoutSession session : sessions) {
            logDisplay.append("\n--- " + session.getDate() + " --- \n");
            if (session.getExercises().isEmpty()) {
                logDisplay.append("  (No exercises for this session)\n");
            } else {
                for (Exercise ex : session.getExercises()) {
                    displayLog(ex, session.getDate(), "Exercise");
                }
            }
        }
    }
    /*
     * MODIFIES: this
     * EFFECTS: gives options to users for exercise to be filtered by date or muscle type
     *          and sets up event handlers for filtering
     */
    private void filteredLog() {
        createDisplayLog();
        JDialog dialog = createDialog("Filter log", 400, 75);

        JPanel buttonPanel = new JPanel();
        JButton dateButton = new JButton("Filtered by Date");
        JButton exerciseMuscleTypeButton = new JButton("Filtered by Muscle Type");

        buttonPanel.add(dateButton);
        buttonPanel.add(exerciseMuscleTypeButton);
        
        filteredByDateEventHandler(dateButton);
        filteredByMuscleTypeEventHandler(exerciseMuscleTypeButton);

        dialog.add(buttonPanel);

        centreOnScreen();
        dialog.setVisible(true);
        dialog.pack();
    }

    /*
     * REUQIRES: dataButton != null
     * MODIFIES: this
     * EFFECTS: pops up a window to filter exercises by date
     */
    private void filteredByDateEventHandler(JButton dateButton) {
        dateButton.addActionListener(e -> {
            JDialog subDialog = createDialog("Filtered By Date", 400, 150);
        
            JPanel datePanel = new JPanel();
            getDate(datePanel);

            JButton filterButton = new JButton("filter");
            JButton cancelButton = createCancelButton(subDialog, "cancel");

            filteredByDateEventHandlerHelper(filterButton, subDialog);

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(filterButton);
            buttonPanel.add(cancelButton);

            subDialog.add(datePanel, BorderLayout.CENTER);
            subDialog.add(buttonPanel, BorderLayout.SOUTH);

            subDialog.pack();
            subDialog.setVisible(true);
        });

    }

    /*
     * REQURIES: filterButton and subDialog != null
     * MODIFIES: this
     * EFFECTS: filters and displays exercises by the inputted date using logbook
     */
    private void filteredByDateEventHandlerHelper(JButton filterButton, JDialog subDialog) {
        filterButton.addActionListener(event -> {
            try {
                String date = String.format("%s/%s/%s", 
                                        yearField.getText(), monthField.getText(), dayField.getText());

                List<WorkoutSession> filteredSessions = logbook.filterSessionsByDate(date);

                displayAllLogs(filteredSessions, "Workouts on " + date);
                subDialog.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(subDialog, "Invalid data format. Please try again.");
            }
        });
    }

    /*
     * REUQIRES: muscleTypeButton != null
     * MODIFIES: this
     * EFFECTS: pops up a window to filter exercises by muscle type
     */
    private void filteredByMuscleTypeEventHandler(JButton muscleTypeButton) {
        muscleTypeButton.addActionListener(e -> {
            JDialog subDialog = createDialog("Filtered By Muscle Type", 400, 150);

            JPanel muscleTypePanel = new JPanel();
            muscleTypePanel.setLayout(new GridLayout(2, 1));
            JLabel muscleTypeLabel = new JLabel("Select Muscle Type: ");
            muscleComboBox = createMuscleCombo();

            muscleTypePanel.add(muscleTypeLabel);
            muscleTypePanel.add(muscleComboBox);

            JButton filterButton = new JButton("filter");
            JButton cancelButton = createCancelButton(subDialog, "cancel");

            filteredByMuscleTypeEventHandlerHelper(filterButton, subDialog);
            
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(filterButton);
            buttonPanel.add(cancelButton);

            subDialog.add(muscleTypePanel, BorderLayout.CENTER);
            subDialog.add(buttonPanel, BorderLayout.SOUTH);

            subDialog.setVisible(true);
        });
    }

    /*
     * REQURIES: filterButton and subDialog != null
     * MODIFIES: this
     * EFFECTS: filteres and displays exercises by the inputted muscle type using logbook
     */
    private void filteredByMuscleTypeEventHandlerHelper(JButton filterButton, JDialog subDialog) {
        filterButton.addActionListener(event -> {
            Muscles selectedMuscleType = (Muscles) muscleComboBox.getSelectedItem();
            List<WorkoutSession> filteredSessions = logbook.filterSessionsByMuscle(selectedMuscleType);

            displayAllLogs(filteredSessions, "Workouts for " + selectedMuscleType.toString());
            subDialog.dispose();
        });
    }

    /*
     * MODIFIES: a file
     * EFFECTS: saves all logs from logbook to its desginated file
     */
    private void saveLogsToFile() {
        try {
            logbook.saveLogBook();
            JOptionPane.showMessageDialog(this, "Logs saved successfully!");
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Unable to write logs to the file: " + e.getMessage());
        }
    }
    
    /*
     * EFFECTS: loads logs from a file
     */
    private void loadLogsFromFile() {
        try {
            logbook.loadLogBook();
            displayAllLogs(); // refreshes the view after loading
            JOptionPane.showMessageDialog(this, "Logs successfully loaded!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Unable to read from file: " + e.getMessage());
        }
    }

    /*
     * MODIFIES: muscleComboBox
     * EFFECTS: creates and returns a combo box with muscle types
     */
    private JComboBox<Muscles> createMuscleCombo() {
        muscleComboBox = new JComboBox<>();

        for (Muscles muscle : Muscles.values()) {
            muscleComboBox.addItem(muscle);
        }

        return muscleComboBox;
    }

    /*
     * MODIFIES: this
     * EFFECTS: centers the parent frame on the screen
     */
    private void centreOnScreen() {
        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        parentFrame.setLocation((width - parentFrame.getWidth()) / 2, (height - parentFrame.getHeight()) / 2);
    }

}