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
import rw.util.Reader;

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

                button.setOnMouseEntered(event -> handleMouseEntry(gridRowFR, gridColFR));
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
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(filter);

        File file = fileChooser.showOpenDialog(grid.getScene().getWindow());

        if (file != null) {
            try {
                battle = Reader.loadBattle(file);
                if (battle != null) {
                    updateGridPostLoad();
                    fileStatus.setText("Battle loaded successfully!");
                } else {
                    fileStatus.setText("Battle could not be loaded.");
                }
            } catch (Exception e) {
                fileStatus.setText("Battle could not be loaded.");
            }
        }
    }

    private void updateGridPostLoad() {
        createGrid(battle.getRows(), battle.getColumns());
        for (int battleRow = 0; battleRow < battle.getRows(); battleRow++) {
            for (int battleCol = 0; battleCol < battle.getColumns(); battleCol++) {
                Entity entity = battle.getEntity(battleRow, battleCol);
                if (entity != null) {
                    if (entity instanceof Wall) {
                        buttons[battleRow+1][battleCol+1].setText("#");
                        buttons[battleRow+1][battleCol+1].setStyle("-fx-background-color: #D3D3D3;");
                    }
                    else if (entity instanceof PredaCon || entity instanceof Maximal) {
                        buttons[battleRow+1][battleCol+1].setText(String.valueOf(entity.getSymbol()));
                    }
                }
            }
        }
    }

    @FXML
    public void saveAsNewBattle() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Battle As...");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File battleFile = fileChooser.showSaveDialog(grid.getScene().getWindow());

        if (battleFile != null) {

        }
    }

    @FXML
    public void saveNewBattle() {
        FileChooser fileChooser = new FileChooser();
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
        createGrid(newRowCount, newColCount);
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