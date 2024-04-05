/**
 * Author: Farshad Islam
 * Tutorial: 07
 * Tuesday, April 2nd, 2024
 **/

package rw.app;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.HashSet;
import rw.battle.*;
import rw.enums.WeaponType;
import rw.util.Reader;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import rw.util.Writer;

public class MainController{

    // All instance variables required for proper code functionality
    private Battle battle;

    private GridPane grid = new GridPane();

    private Button[][] buttons;

    private File mainBattleFile = new File("./saved-battle.txt");

    FileChooser fileChooser = new FileChooser();

    HashSet<String> symbols = new HashSet<>();

    // All attributes created in SceneBuilder that are used directly from where they exist in Main.fxml
    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private RadioButton radioPredaCon, radioMaximal;

    @FXML
    private Alert popup = new Alert(Alert.AlertType.NONE);

    @FXML
    private TextField rowCounter, columnCounter, getPredaConSymbol, getPredaConName, getPredaConHealth, getMaximalName, getMaximalSymbol, getMaximalHealth, getMaximalAttack, getMaximalArmour;

    @FXML
    private TextArea displayButtonInfo;

    @FXML
    private Label errorStatus, fileStatus;

    @FXML
    private AnchorPane anchorGridPane = new AnchorPane();


    /**
     * Set up the window state
     */


    /**
     * Initializes code before user does any kind of input
     *
     * @returns N/A
     */
    @FXML
    public void initialize() {
        comboBox.setItems(FXCollections.observableArrayList("Claws (C)", "Teeth (T)", "Lasers (L)")); // Establishing comboBox
        anchorGridPane.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE); // AnchorPane parameters
        createGrid(3, 3); // Initial grid
        fileStatus.setText("Currently meant to save to 'saved-battle.txt..."); // Tells user where to save current battle
        fadeOutfileStatus();
        symbols = new HashSet<>(); // Prevents previous allotment of symbols from interfering with current iteration
    }

    /**
     * Builds an interactable visual representation of a Battle object
     *
     * @params numRows, numCols
     * @returns N/A
     */
    private void createGrid(int numRows, int numCols) {
        grid.getChildren().clear(); // Clears GridPane and AnchorPane every time a new grid is created
        anchorGridPane.getChildren().clear();

        buttons = new Button[numRows + 2][numCols + 2]; // Resets button array to create a perimeter around actual battle array
        battle = new Battle(numRows, numCols); // Resets battle object to be iterated over
        errorStatus.setText(null); // Remove any pre-existing error messages
        grid = new GridPane(); // new GridPane made to have modified by...

        for (int gridRow = 0; gridRow < numRows+2; gridRow++) {
            for (int gridCol = 0; gridCol < numCols+2; gridCol++) { // Iterating through every possible position in grid
                String buttonText = null; // Initializes button text
                if (gridRow == 0 || gridCol == 0 || gridRow == numRows+1 || gridCol == numCols+1) {
                    buttonText = "#"; // Establishes a wall where an interactable button would have been
                }

                Button button = new Button(buttonText); // Creates button object with either null text or "#"
                if ("#".equals(buttonText)) { button.setStyle("-fx-background-color: #D3D3D3;"); }
                button.setPrefSize(30,30); // Preferred size for buttons
                grid.add(button, gridCol, gridRow); // Add newly created button to space

                buttons[gridRow][gridCol] = button; // Places newly created button in a 2D array of buttons (all clickable inside perimeter)
                int gridRowFR = gridRow; // Bypassing need for finality with integer variables
                int gridColFR = gridCol;

                button.setOnAction(event -> handleButtonClick(gridRowFR, gridColFR)); // What happens when a button is clicked

                button.setOnMouseEntered(event -> handleMouseEntry(button, gridRowFR, gridColFR)); // What happens when you hover over a button (displayButtonInfo becomes blank)

                button.setOnMouseExited(event -> displayButtonInfo.setText(null)); // What happens when you leave a button after hovering over it
            }
        }

        // Remaining visual touch-ups
        grid.setHgap(5);
        grid.setVgap(5);

        anchorGridPane.getChildren().add(grid);

        AnchorPane.setTopAnchor(grid, 10.0);
        AnchorPane.setLeftAnchor(grid, 10.0);
    }

    /**
     * Loads a file from directory and draws a grid from it
     *
     * @returns N/A
     */
    @FXML
    public void loadNewBattle() {
        fileChooser = new FileChooser(); // Initialization

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt"); // Only loads .txt files
        fileChooser.getExtensionFilters().add(filter);

        File file = fileChooser.showOpenDialog(grid.getScene().getWindow()); // Allows users to open files from OS

        if (file != null) { // Makes sure that the file is empty
            try {
                mainBattleFile = file; // Sets current JavaFX file to file being loaded
                battle =  Reader.loadBattle(file); // Change battle
                createGrid(battle.getRows(), battle.getColumns()); // Create grid with same dimensions as battle
                battle =  Reader.loadBattle(file); // Remind program of what attributes exist in battle
                updateGridPostLoad(); // Draw over everything in the GridPane so that it accurately depicts what was written in battle

                fileStatus.setText("Battle successfully loaded!"); // Success message
                fadeOutfileStatus();
            } catch (Exception e) {
                fileStatus.setText("Could not successfully load file."); // Failure message
                fadeOutfileStatus();
            }

        }
    }

    /**
     * Causes status messages related to file saving/loading to vanish soon after being displayed
     *
     * @returns N/A
     */
    private void fadeOutfileStatus() {
        FadeTransition ft = new FadeTransition(Duration.seconds(3), fileStatus); // Initialize transition object

        ft.setFromValue(1.0); // Start at this time
        ft.setToValue(0.0); // End at this time
        ft.setCycleCount(1); // Play (only) once

        ft.play(); // Gawk at how beautifully crafted this is to look at
    }


    /**
     * Performs the act of drawing over the GridPane with all the available Battle entities
     *
     * @returns N/A
     */
    private void updateGridPostLoad() {
        for (int loadRow = 0; loadRow < battle.getRows(); loadRow++) {
            for (int loadCol = 0; loadCol < battle.getColumns(); loadCol++) { // Iterates through every battle index
                Entity entity = battle.getEntity(loadRow, loadCol); // Sets battle index to an object (possibly null)
                Button button = buttons[loadRow + 1][loadCol + 1]; // Offset by 1 to match button positions

                if (entity instanceof Wall) { // Protocol for non-changeable Wall object found in battle besides perimeter
                    button.setText("#");
                    button.setStyle("-fx-background-color: #D3D3D3;"); // Different colouring from Robot buttons
                } else if (entity instanceof PredaCon || entity instanceof Maximal) { // Protocol for interestingly interactable Robot entity in battle
                    button.setText(String.valueOf(entity.getSymbol())); // Robot symbol appears on the face of the button representing its location
                } else {
                    button.setText(""); // Clear the button if there is no entity
                }
            }
        }
    }


    /**
     * Saves a battle to be loaded from a separate file
     *
     * @returns N/A
     */
    @FXML
    public void saveAsNewBattle() {
        fileChooser = new FileChooser(); // Initialize

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt")); // Save as only .txt type
        File newBattleFile = fileChooser.showSaveDialog(grid.getScene().getWindow()); // Open OS

        if (newBattleFile != null) {
            try {
                Writer.saveBattle(battle, newBattleFile); // Try to save a file using Writer.java
                mainBattleFile = newBattleFile; // Set the current file to what is being saved
                fileStatus.setText("New battle saved!"); // Success message that vanishes over time
                fadeOutfileStatus();
            } catch (Exception e) { // In cases where the file cannot be saved
                fileStatus.setText("Unable to save current battle."); // Failure message that vanishes over time
                fadeOutfileStatus();
            }

        }
    }

    /**
     * Overwrites a pre-existing .txt file with the current battle state
     *
     * @returns N/A
     */
    @FXML
    public void saveNewBattle() {
        fileChooser = new FileChooser();

        File file = fileChooser.showSaveDialog(null); // Open window to overwrite saved-battle.txt

        if (file != null) {
            try { // Success state
                Writer.saveBattle(battle, file);
                fileStatus.setText("Battle saved successfully!");
                fadeOutfileStatus();
            } catch (Exception e) { // Fail state
                fileStatus.setText("Battle was not saved successfully.");
                fadeOutfileStatus();
            }
        }
    }

    /**
     * Quits out of the program both from JavaFX and the console
     *
     * @returns N/A
     */
    @FXML
    public void quitBattle() {
        Platform.exit(); // Exit JavaFX
        System.exit(0); // End process in console
    }

    /**
     * Displays information about the user and what the code's functionality is
     *
     * @returns N/A
     */
    @FXML
    public void aboutBattle() {
        popup.setAlertType(Alert.AlertType.INFORMATION); // Establish new window for alert to appear in
        popup.setContentText("Author: Farshad Islam\nEmail: farshad.islam@ucalgary.ca\nVersion: v1.0\nThis code is meant to simulate a Robot Wars game."); // Standard info
        popup.setHeaderText("Ermmm... methinks this program is giving uber amounts of awesomesauce!!1!1"); // Ramblings of a madman
        popup.setTitle("We stan with Jonathan Hudson ∠(´д｀)"); // Needless idolizing
        popup.show(); // Display embarrassing message
    }

    /**
     * Saves a battle to be loaded from a separate file
     *
     * @return WeaponType of the entity requested (should it be a PredaCon object)
     */
    @FXML
    WeaponType getWeaponTypeBattle() {
        char comboSelect = comboBox.getValue().charAt(comboBox.getValue().length()-2); // Checks selected value of comboBox
        return WeaponType.getWeaponType(comboSelect); // returns WeaponType associated with character representation
    }

    /**
     * Draws a brand-new grid from user-entered rows and columns
     *
     * @return N/A
     */
    @FXML
    public void drawBattleField() {
        int newRowCount;
        int newColCount;

        try {
            newRowCount = Integer.parseInt(rowCounter.getText()); // Set integer to row number
        } catch (NumberFormatException e) {
            errorStatus.setText("Cannot parse invalid row input: " + e.getMessage());
            return; // Exit method if parsing rowCounter fails
        }

        try {
            newColCount = Integer.parseInt(columnCounter.getText()); // Set integer to column number
        } catch (NumberFormatException e) {
            errorStatus.setText("Cannot parse invalid column input: " + e.getMessage());
            return; // Exit method if parsing columnCounter fails
        }

        if (newColCount <= 0 || newRowCount <= 0) {
            errorStatus.setText("Cannot create board with negative or zero row or column count");
            return; // Exit method if array indices are not valid
        }

        createGrid(newRowCount, newColCount); // Standard protocol when building a new grid (also drawing one)
    }

    /**
     * Behaviour for when the cursor hovers over a button
     *
     * @params buttonHovered, gridRowFR, gridColFR
     * @returns N/A
     */
    private void handleMouseEntry(Button buttonHovered, int gridRowFR, int gridColFR) {
        Entity battleSpace;

        if ("#".equals(buttonHovered.getText())) {
            displayButtonInfo.setText("Cannot place robots here."); // Prevents robots from being placed on top of walls
        }

        else {
            battleSpace = battle.getEntity(gridRowFR - 1, gridColFR - 1); // Adjusted for border

            if (battleSpace == null) {
                displayButtonInfo.setText("Space currently empty. \nInitialize a robot to fill the space!"); // Tells user when they can spice up the board
            } else {
                // Display more detailed information about the entity
                String[] buttonInfo = battleSpace.toString().split("\t"); // Creates array out of battleString that can be used for more consise info
                if (buttonInfo.length == 6) { // PredaCon
                    predaConInfo(buttonInfo); // Display symbol, name, health, and WeaponType
                } else if (buttonInfo.length == 7) { // Maximal
                    maximalInfo(buttonInfo); // Display symbol, name, health, attack and armour
                }
            }
        }

    }

    /**
     * Tells user about the PredaCon robot they're inspecting
     *
     * @param pDetails
     * @returns N/A
     */
    private void predaConInfo(String[] pDetails) {
        displayButtonInfo.setText("Type: Predator\nSymbol: " + pDetails[1] + "\nName: " + pDetails[2] + "\nHealth: " + pDetails[3] + "\nWeaponType: " + pDetails[5]);
    }

    /**
     * Tells user about the Maximal robot they're inspecting
     *
     * @param mDetails
     * @returns N/A
     */
    private void maximalInfo(String[] mDetails) {
        displayButtonInfo.setText("Type: Maximal\nSymbol: " + mDetails[1] + "\nName: " + mDetails[2] + "\nHealth: " + mDetails[3] + "\nAttack: " + mDetails[5] + "\nArmour: " + mDetails[6]);
    }

    /**
     * Behaviour for when a button gets clicked
     *
     * @param buttonRow, buttonCol
     * @returns N/A
     */
    public void handleButtonClick(int buttonRow, int buttonCol) {
        Button buttonClicked = buttons[buttonRow][buttonCol]; // Identifying clicked button
        String buttonText = buttonClicked.getText(); // Checking text on the button

        if ("#".equals(buttonText)) { // Wall
            errorStatus.setText("Cannot place robot on top of a wall!"); // error message
        } else {
            if (radioMaximal.isSelected()) { // PredaCon
                placeMaximal(buttonClicked, buttonRow, buttonCol); // Places PredaCon

            } else if (radioPredaCon.isSelected()) { // Maximal
                placePredaCon(buttonClicked, buttonRow, buttonCol); // Places Maximal
            }

            else { // Neither radio button being selected allows for the user to delete robots
                buttonClicked.setText(null); // Clears text
                battle.addEntity(buttonRow-1, buttonCol-1, null); // removes entity from the grid
            }
        }
    }

    /**
     * Places valid PredaCon in the GridPane and also to battle
     *
     * @param buttonClicked, row, column
     * @returns N/A
     */
    private void placePredaCon(Button buttonClicked, int row, int column) {
        char predaConSymbol = 0; // Initializing
        String predaConName;
        int predaConHealth;
        WeaponType predaConWeaponType;

        if (symbols.contains(getPredaConSymbol.getText())) {
            errorStatus.setText("Must have a unique symbol!");
            return;
        } else {
            symbols.add(getPredaConSymbol.getText());
        }

        if (getPredaConSymbol.getText().length() > 1 || getPredaConSymbol.getText().isEmpty()) { // Gets char for symbol
            errorStatus.setText("Invalid character input for PredaCon symbol!");
            return;
        } else {
            predaConSymbol = getPredaConSymbol.getText().charAt(0);
        }

        predaConName = getPredaConName.getText(); // Strings can be literally anything so no need for an error check

        try {
            predaConHealth = Integer.parseInt(getPredaConHealth.getText()); // Gets PredaCon health
        } catch (NumberFormatException e) {
            errorStatus.setText("Cannot parse health value for PredaCon: " + e.getMessage());
            return;
        }

        if (predaConHealth < 0) {
            errorStatus.setText("Cannot set negative health for PredaCon!"); // No such thing as negative HP
            return;
        }

        try {
            predaConWeaponType = getWeaponTypeBattle(); // Gets WeaponType
        } catch (NullPointerException e) {
            errorStatus.setText("Must select a WeaponType before placing PredaCon!");
            return;
        }

        PredaCon predaCon = new PredaCon(predaConSymbol, predaConName, predaConHealth, predaConWeaponType); // New PredaCon object
        battle.addEntity(row-1, column-1, predaCon); // Add to battle
        buttonClicked.setText(String.valueOf(predaConSymbol)); // Add to grid
        radioPredaCon.setSelected(false); // Deselect radio button
        errorStatus.setText(null); // Erase any previous error message
    }

    /**
     * Places Maximal robot in grid and battle
     *
     * @params buttonClicked, row, column
     * @returns N/A
     */
    private void placeMaximal(Button buttonClicked, int row, int column) {
        char maximalSymbol; // Initializing
        String maximalName;
        int maximalHealth;
        int maximalAttack;
        int maximalArmour;

        if (symbols.contains(getMaximalSymbol.getText())) {
            errorStatus.setText("Must have a unique symbol!");
            return;
        } else {
            symbols.add(getMaximalSymbol.getText());
        }

        if (getMaximalSymbol.getText().length() > 1 || getMaximalSymbol.getText().isEmpty()) { // Same as placePredaCon
            errorStatus.setText("Invalid character input for Maximal symbol!");
            return;
        } else {
            maximalSymbol = getMaximalSymbol.getText().charAt(0);
        }

        maximalName = getMaximalName.getText(); // Same as placePredaCon

        try {
            maximalHealth = Integer.parseInt(getMaximalHealth.getText()); // Try to kill three birds with one stone
            maximalAttack = Integer.parseInt(getMaximalAttack.getText());
            maximalArmour = Integer.parseInt(getMaximalArmour.getText());
        } catch (NumberFormatException e) {
            errorStatus.setText("One or more invalid integer inputs for Maximal parameters: " + e.getMessage());
            return;
        }

        Maximal maximal = new Maximal(maximalSymbol, maximalName, maximalHealth, maximalAttack, maximalArmour); // New Maximal object
        battle.addEntity(row-1, column-1, maximal); // Add that bad boy to battle (while adjusting for differences from grid)
        buttonClicked.setText(String.valueOf(maximalSymbol)); // Change look of buttonClicked
        radioMaximal.setSelected(false); // Deselected radio button
        errorStatus.setText(null); // Erase any previous error messages after everything successfully went through
    }
}