package rw.app;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import java.io.File;
import javafx.stage.Stage;
import rw.battle.*;
import rw.enums.WeaponType;
import rw.util.Writer;

public class MainController{

    //Store the data of editor
    private Battle battle;

    @FXML
    private MenuItem battleSaver, battleLoader, battleHelper;

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private RadioButton radioPredaCon, radioMaximal;

    @FXML
    private Alert popup = new Alert(Alert.AlertType.NONE);

    @FXML
    private TextField rowCounter, columnCounter, getPredaConSymbol, getPredaConName, getPredaConHealth;

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
                }
                grid.add(button, gridCol, gridRow); // Swap the parameters to the correct order

                buttons[gridRow][gridCol] = button;
                int gridRowFR = gridRow;
                int gridColFR = gridCol;

                button.setOnAction(event -> handleButtonClick(gridRowFR, gridColFR));
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

    }

    @FXML
    public void aboutBattle() {
        popup.setAlertType(Alert.AlertType.INFORMATION);
        popup.setContentText("Author: Farshad Islam\nEmail: farshad.islam@ucalgary.ca\nVersion: v1.0\nThis code is meant to simulate a Robot Wars game.");
        popup.setHeaderText("Erm... methinks this program is giving uber amounts of awesomesauce!!1!1");
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
            return; // Exit the method if parsing fails
        }

        try {
            newColCount = Integer.parseInt(columnCounter.getText());
        } catch (NumberFormatException e) {
            errorStatus.setText("Cannot parse invalid column input: " + e.getMessage());
            return;
        }

        if (newRowCount < 3 || newColCount < 3) {
            errorStatus.setText("Battle arena size too small. Please re-enter.");
            return; // Exit the method if size is too small
        }

        if (newRowCount > 8 || newColCount > 8) {
            errorStatus.setText("Battle arena size too large. Please re-enter.");
            return; // Exit the method if size is too large
        }

        // Clear existing content
        grid.getChildren().clear();
        anchorGridPane.getChildren().clear();

        // Proceed with creating the new grid and buttons
        grid = new GridPane();
        buttons = new Button[newRowCount + 2][newColCount + 2];
        battle = new Battle(newRowCount, newColCount);
            errorStatus.setText(null);
            grid.getChildren().clear();
            anchorGridPane.getChildren().clear();
            grid = new GridPane();

            buttons = new Button[newRowCount+2][newColCount+2];

            battle = new Battle(newRowCount, newColCount);

            for (int gridRow = 0; gridRow < newRowCount+2; gridRow++) {
                for (int gridCol = 0; gridCol < newColCount+2; gridCol++) {
                    String buttonText = "";
                    if (gridRow == 0 || gridCol == 0 || gridRow == newRowCount+1 || gridCol == newColCount+1) {
                        buttonText = "#";
                    }
                    else {
                        buttonText = null;
                    }

                    Button button = new Button(buttonText);
                    button.setPrefSize(30,30);
                    grid.add(button, gridCol, gridRow); // Swap the parameters to the correct order

                    buttons[gridRow][gridCol] = button;
                    int gridRowFR = gridRow;
                    int gridColFR = gridCol;

                    button.setOnAction(event -> handleButtonClick(gridRowFR, gridColFR));
                }
            }

            grid.setHgap(5);
            grid.setVgap(5);

            anchorGridPane.getChildren().add(grid);

            AnchorPane.setTopAnchor(grid, 10.0);
            AnchorPane.setLeftAnchor(grid, 10.0);
    }

    public void handleButtonClick(int buttonRow, int buttonCol) {
        Button buttonClicked = buttons[buttonRow][buttonCol];

        if (radioMaximal.isSelected()) {
            char predaConSymbol;

            if (buttonClicked.getText().equals("#")) {
                errorStatus.setText("Cannot place robot on top of a wall!");
            }

            try {
                predaConSymbol = getPredaConSymbol.getText().charAt(0);
            } catch (IllegalArgumentException e) {
                errorStatus.setText("Invalid symbol input for PredaCon: " + getPredaConSymbol.getText());
            }
        }

        if (radioPredaCon.isSelected()) {
            if (buttonClicked.getText().equals("#")) {
                errorStatus.setText("Cannot place robot on top of a wall!");
            }
        }
    }
}