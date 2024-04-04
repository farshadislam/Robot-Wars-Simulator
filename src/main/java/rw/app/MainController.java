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
import javafx.stage.Stage;
import rw.battle.*;
import rw.enums.WeaponType;

public class MainController{

    //Store the data of editor
    private Battle battle;

    @FXML
    private MenuItem battleSaver, battleLoader;

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
    private Label errorStatus;

    @FXML
    private AnchorPane anchorGridPane = new AnchorPane();

    private GridPane grid;

    private Button[][] buttons;

    /**
     * Set up the window state
     */

    FileChooser fileChooser = new FileChooser();

    @FXML
    public void initialize() {
        comboBox.setItems(FXCollections.observableArrayList("Claws (C)", "Teeth (T)", "Lasers (L)"));
        anchorGridPane.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        grid = new GridPane();

        // Decide on a specific number of rows and columns for your grid.
        int numRows = 5; // Example number of rows
        int numCols = 5; // Example number of columns

        buttons = new Button[numRows][numCols];

        battle = new Battle(numRows, numCols);

        for (int gridRow = 0; gridRow < numRows; gridRow++) {
            for (int gridCol = 0; gridCol < numCols; gridCol++) {
                Button button = new Button();
                button.setPrefSize(30,30);
                if (gridRow == 0 || gridCol == 0 || gridRow == numRows-1 || gridCol == numCols-1) {
                    button.setText("#");
                    button.setStyle("-fx-background-color: #D3D3D3;");
                }
                grid.add(button, gridCol, gridRow); // Swap the parameters to the correct order

                buttons[gridRow][gridCol] = button;
                int gridRowFR = gridRow;
                int gridColFR = gridCol;

                button.setOnAction(event -> handleButtonClick(gridRowFR, gridColFR));

                button.setOnMouseExited(event -> displayButtonInfo.setText(null));

                button.setOnMouseEntered(event -> handleMouseEntry(gridRowFR, gridColFR));
            }
        }

        grid.setHgap(5);
        grid.setVgap(5);

        anchorGridPane.getChildren().add(grid);

        // Optionally, set the GridPane to anchor at all four sides to fill the AnchorPane.
        AnchorPane.setTopAnchor(grid, 10.0);
        AnchorPane.setLeftAnchor(grid, 10.0);
    }


    @FXML
    public void loadNewBattle() {

    }

    @FXML
    public void saveNewBattle() {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("."));
        fileChooser.setInitialFileName("saved-battle.txt");
        File fileSave = fileChooser.showSaveDialog(new Stage());
        System.out.println(fileSave);
    }

    @FXML
    public void quitBattle() {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    public void aboutBattle() {
        popup.setAlertType(Alert.AlertType.INFORMATION);
        popup.setContentText("Author: Farshad Islam\nEmail: farshad.islam@ucalgary.ca\nVersion: v1.0\nThis code is meant to simulate a Robot Wars game.");
        popup.setHeaderText("Ermmm... methinks this program is giving uber amounts of awesomesauce!!1!1");
        popup.setTitle("We stan with Jonathan Hudson ∠(´д｀)");
        popup.show();
    }

    @FXML
    public void weaponTypeBattle() {

    }

    @FXML
    WeaponType getWeaponTypeBattle() {
        char comboSelect = comboBox.getValue().charAt(comboBox.getValue().length()-2);
        return WeaponType.getWeaponType(comboSelect);
    }

    @FXML
    public void drawBattleField() {
        int newRowCount = 0;
        int newColCount = 0;

        try {
            newRowCount = Integer.parseInt(rowCounter.getText());
        } catch (NumberFormatException e) {
            errorStatus.setText("Cannot parse invalid row input: " + e.getMessage());
            return; // Exit method if parsing rowCounter fails
        }

        try {
            newColCount = Integer.parseInt(columnCounter.getText());
        } catch (NumberFormatException e) {
            errorStatus.setText("Cannot parse invalid column input: " + e.getMessage());
            return; // Exit method if parsing columnCounter fails
        }

        if (newColCount <= 0 || newRowCount <= 0) {
            errorStatus.setText("Cannot create board with negative or zero row or column count");
            return; // Exit method if parsing columnCounter fails
        }

        // Clear whatever was drawn before to make room for new grid
        grid.getChildren().clear();
        anchorGridPane.getChildren().clear();

        // Create a brand-new grid with the valid row and column values

        buttons = new Button[newRowCount + 2][newColCount + 2]; // Reset array of buttons
        battle = new Battle(newRowCount, newColCount); // Reset battle object
        errorStatus.setText(null); // Remove any pre-existing error messages

        grid = new GridPane(); // new GridPane

        for (int gridRow = 0; gridRow < newRowCount+2; gridRow++) {
            for (int gridCol = 0; gridCol < newColCount+2; gridCol++) {
                String buttonText = null; // Initialize button text
                if (gridRow == 0 || gridCol == 0 || gridRow == newRowCount+1 || gridCol == newColCount+1) {
                    buttonText = "#"; // Establishes a wall on top of the button
                }

                Button button = new Button(buttonText); // Creates button object with either null text or "#"
                if ("#".equals(buttonText)) { button.setStyle("-fx-background-color: #D3D3D3;"); }
                button.setPrefSize(30,30); // Preferred size for buttons
                grid.add(button, gridCol, gridRow); // Add newly created button to space

                buttons[gridRow][gridCol] = button;
                int gridRowFR = gridRow;
                int gridColFR = gridCol;

                button.setOnAction(event -> handleButtonClick(gridRowFR, gridColFR));

                button.setOnMouseExited(event -> displayButtonInfo.setText(null));

                button.setOnMouseEntered(event -> handleMouseEntry(gridRowFR, gridColFR));
            }
        }

        grid.setHgap(5);
        grid.setVgap(5);

        anchorGridPane.getChildren().add(grid);

        AnchorPane.setTopAnchor(grid, 10.0);
        AnchorPane.setLeftAnchor(grid, 10.0);
    }

    private void handleMouseEntry(int gridRowFR, int gridColFR) {
        Entity battleSpace;

        if (gridRowFR == 0 || gridColFR == 0 || gridColFR == battle.getColumns()+1 || gridRowFR == battle.getRows()+1) {
            displayButtonInfo.setText("Cannot place robots here.");
        }

        else {
            battleSpace = battle.getEntity(gridRowFR - 1, gridColFR - 1); // Adjusted for border

            if (battleSpace == null) {
                displayButtonInfo.setText("Space currently empty. \nInitialize a robot to fill the space!");
            } else {
                // Display more detailed information about the entity
                displayButtonInfo.setText(battleSpace.toString()); // Assuming toString() method provides meaningful information
            }
        }

    }

    public void handleButtonClick(int buttonRow, int buttonCol) {
        Button buttonClicked = buttons[buttonRow][buttonCol];
        String buttonText = buttonClicked.getText();

        if ("#".equals(buttonText)) {
            errorStatus.setText("Cannot place robot on top of a wall!");
        } else {
            if (radioMaximal.isSelected()) {
                placeMaximal(buttonClicked, buttonRow, buttonCol);

            } else if (radioPredaCon.isSelected()) {
                placePredaCon(buttonClicked, buttonRow, buttonCol);
            }
        }
    }

    private void placePredaCon(Button buttonClicked, int row, int column) {
        char predaConSymbol;
        String predaConName;
        int predaConHealth;
        WeaponType predaConWeaponType;

        try {
            predaConSymbol = getPredaConSymbol.getText().charAt(0);
        } catch (IllegalArgumentException e) {
            errorStatus.setText("Invalid symbol for PredaCon: " + e.getMessage());
            return;
        }

        predaConName = getPredaConName.getText();

        try {
            predaConHealth = Integer.parseInt(getPredaConHealth.getText());
        } catch (NumberFormatException e) {
            errorStatus.setText("Cannot parse health value for PredaCon: " + e.getMessage());
            return;
        }

        if (predaConHealth < 0) {
            errorStatus.setText("Cannot set negative health for PredaCon!");
            return;
        }

        predaConWeaponType = getWeaponTypeBattle();

        PredaCon predaCon = new PredaCon(predaConSymbol, predaConName, predaConHealth, predaConWeaponType);
        battle.addEntity(row-1, column-1, predaCon);
        buttonClicked.setText(String.valueOf(predaConSymbol));
        radioPredaCon.setSelected(false);
    }

    private void placeMaximal(Button buttonClicked, int row, int column) {
        char maximalSymbol;
        String maximalName;
        int maximalHealth;
        int maximalAttack;
        int maximalArmour;

        try {
            maximalSymbol = getMaximalSymbol.getText().charAt(0);
        } catch (IllegalArgumentException e) {
            errorStatus.setText("Invalid symbol for Maximal: " + e.getMessage());
            return;
        }

        maximalName = getMaximalName.getText();

        try {
            maximalHealth = Integer.parseInt(getMaximalHealth.getText());
            maximalAttack = Integer.parseInt(getMaximalAttack.getText());
            maximalArmour = Integer.parseInt(getMaximalArmour.getText());
        } catch (NumberFormatException e) {
            errorStatus.setText("One or more invalid integer inputs for Maximal parameters: " + e.getMessage());
            return;
        }

        Maximal maximal = new Maximal(maximalSymbol, maximalName, maximalHealth, maximalAttack, maximalArmour);
        battle.addEntity(row-1, column-1, maximal);
        buttonClicked.setText(String.valueOf(maximalSymbol));
        radioMaximal.setSelected(false);
    }
}