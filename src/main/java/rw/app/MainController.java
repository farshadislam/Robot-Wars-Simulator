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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import rw.battle.*;
import rw.enums.WeaponType;
import rw.util.Reader;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import rw.util.Writer;

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
    private Label errorStatus, fileStatus;

    @FXML
    private AnchorPane anchorGridPane = new AnchorPane();

    private GridPane grid = new GridPane();

    private Button[][] buttons;

    private File mainBattleFile;

    /**
     * Set up the window state
     */

    FileChooser fileChooser = new FileChooser();

    @FXML
    public void initialize() {
        comboBox.setItems(FXCollections.observableArrayList("Claws (C)", "Teeth (T)", "Lasers (L)"));
        anchorGridPane.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        createGrid(3, 3);
    }

    private void createGrid(int numRows, int numCols) {
        grid.getChildren().clear();
        anchorGridPane.getChildren().clear();

        // Create a brand-new grid with the valid row and column values

        buttons = new Button[numRows + 2][numCols + 2]; // Reset array of buttons
        battle = new Battle(numRows, numCols); // Reset battle object
        errorStatus.setText(null); // Remove any pre-existing error messages
        grid = new GridPane(); // new GridPane

        for (int gridRow = 0; gridRow < numRows+2; gridRow++) {
            for (int gridCol = 0; gridCol < numCols+2; gridCol++) {
                String buttonText = null; // Initialize button text
                if (gridRow == 0 || gridCol == 0 || gridRow == numRows+1 || gridCol == numCols+1) {
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

                button.setOnMouseEntered(event -> handleMouseEntry(button, gridRowFR, gridColFR));
            }
        }

        grid.setHgap(5);
        grid.setVgap(5);

        anchorGridPane.getChildren().add(grid);

        AnchorPane.setTopAnchor(grid, 10.0);
        AnchorPane.setLeftAnchor(grid, 10.0);
    }

    @FXML
    public void loadNewBattle() {
        fileChooser = new FileChooser();

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(filter);

        File file = fileChooser.showOpenDialog(grid.getScene().getWindow());

        if (file != null) {
            mainBattleFile = file;

            try {
                battle =  Reader.loadBattle(file);
                createGrid(battle.getRows(), battle.getColumns());
                battle =  Reader.loadBattle(file);
                updateGridPostLoad();

                fileStatus.setText("Battle successfully loaded!");
                fadeOutfileStatus();
            } catch (Exception e) {
                fileStatus.setText("Could not successfully load file.");
                fadeOutfileStatus();
            }

        }
    }

    private void fadeOutfileStatus() {
        // Create a FadeTransition with specified duration
        FadeTransition ft = new FadeTransition(Duration.seconds(3), fileStatus);

        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setCycleCount(1); // Play once

        ft.play();
    }


    private void updateGridPostLoad() {
        for (int loadRow = 0; loadRow < battle.getRows(); loadRow++) {
            for (int loadCol = 0; loadCol < battle.getColumns(); loadCol++) {
                Entity entity = battle.getEntity(loadRow, loadCol);
                Button button = buttons[loadRow + 1][loadCol + 1]; // Offset by 1 to match button positions

                if (entity instanceof Wall) {
                    button.setText("#");
                    button.setStyle("-fx-background-color: #D3D3D3;");
                } else if (entity instanceof PredaCon || entity instanceof Maximal) {
                    button.setText(String.valueOf(entity.getSymbol()));
                } else {
                    button.setText(""); // Clear the button if there is no entity
                }
            }
        }
    }


    @FXML
    public void saveAsNewBattle() {
        fileChooser = new FileChooser();
        fileChooser.setTitle("Save Battle As...");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File newBattleFile = fileChooser.showSaveDialog(grid.getScene().getWindow());

        if (newBattleFile != null) {
            try {
                Writer.saveBattle(battle, newBattleFile);
                mainBattleFile = newBattleFile;
                fileStatus.setText("New battle saved!");
                fadeOutfileStatus();
            } catch (Exception e) {
                fileStatus.setText("Unable to save current battle.");
                fadeOutfileStatus();
            }

        }
    }

    @FXML
    public void saveNewBattle() {
        if (mainBattleFile != null) {
            try {
                Writer.saveBattle(battle, mainBattleFile);
                fileStatus.setText("Battle saved successfully!");
                fadeOutfileStatus();
            } catch (Exception e) {
                fileStatus.setText("Battle could not be saved successfully.");
            }
        } else {
            saveAsNewBattle();
        }
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
        createGrid(newRowCount, newColCount);
    }

    private void handleMouseEntry(Button buttonHovered, int gridRowFR, int gridColFR) {
        Entity battleSpace;

        if ("#".equals(buttonHovered.getText())) {
            displayButtonInfo.setText("Cannot place robots here.");
        }

        else {
            battleSpace = battle.getEntity(gridRowFR - 1, gridColFR - 1); // Adjusted for border

            if (battleSpace == null) {
                displayButtonInfo.setText("Space currently empty. \nInitialize a robot to fill the space!");
            } else {
                // Display more detailed information about the entity
                String[] buttonInfo = battleSpace.toString().split("\t");
                if (buttonInfo.length == 6) {
                    predaConInfo(buttonInfo);
                } else if (buttonInfo.length == 7) {
                    maximalInfo(buttonInfo);
                }
            }
        }

    }

    private void predaConInfo(String[] pDetails) {
        displayButtonInfo.setText("Type: Predator\nSymbol: " + pDetails[1] + "\nName: " + pDetails[2] + "\nHealth: " + pDetails[3] + "\nWeaponType: " + pDetails[5]);
    }

    private void maximalInfo(String[] mDetails) {
        displayButtonInfo.setText("Type: Maximal\nSymbol: " + mDetails[1] + "\nName: " + mDetails[2] + "\nHealth: " + mDetails[3] + "\nAttack: " + mDetails[5] + "\nArmour: " + mDetails[6]);
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

            else {
                buttonClicked.setText(null);
            }
        }
    }

    private void placePredaCon(Button buttonClicked, int row, int column) {
        char predaConSymbol = 0;
        String predaConName;
        int predaConHealth;
        WeaponType predaConWeaponType;

        if (getPredaConSymbol.getText().length() > 1) {
            errorStatus.setText("Invalid character input for PredaCon symbol!");
            return;
        } else {
            predaConSymbol = getPredaConSymbol.getText().charAt(0);
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
        errorStatus.setText(null);
    }

    private void placeMaximal(Button buttonClicked, int row, int column) {
        char maximalSymbol;
        String maximalName;
        int maximalHealth;
        int maximalAttack;
        int maximalArmour;

        if (getMaximalSymbol.getText().length() > 1) {
            errorStatus.setText("Invalid character input for Maximal symbol!");
            return;
        } else {
            maximalSymbol = getMaximalSymbol.getText().charAt(0);
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
        errorStatus.setText(null);
    }
}