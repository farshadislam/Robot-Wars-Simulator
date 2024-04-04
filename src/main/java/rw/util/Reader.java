/**
 * Author: Farshad Islam
 * Tutorial: 07
 * Tuesday, April 2nd, 2024
 **/

package rw.util;

import rw.battle.Battle;
import rw.battle.Maximal;
import rw.battle.PredaCon;
import rw.battle.Wall;
import rw.enums.WeaponType;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Class to assist reading in battle file
 *
 * @author Jonathan Hudson
 * @version 1.0
 */
public final class Reader {

    /**
     * Takes in a .txt file and creates a Battle object out of it
     *
     * @param file
     * @return battle
     */
    public static Battle loadBattle(File file) {

        ArrayList<String> bLines = new ArrayList<>(); // Initialize amalgamation of all lines

        Battle battle = null; // Initialize new battle object

        int rowCount = 0; // Arbitrary assignments

        int colCount = 0;

        HashSet<String> robotSymbols = new HashSet<>(); // Initialize tracker for all robotSymbols

        try {
            BufferedReader br = new BufferedReader(new FileReader(file)); // Initialize BufferedReader
            String battleLine; // String representation of lines
            while ((battleLine = br.readLine()) != null) { // While-loop iterating over every line in the file
                bLines.add(battleLine); // Strings get added to bLines
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e); // error handling
        }


        for (int i = 0; i < 2; i++) { // Only meant to iterate over the first two lines
            if (i == 0) { // Map row length
                try {
                    rowCount = Integer.parseInt(bLines.get(i)); // String to integer
                } catch (NumberFormatException e) {
                    System.err.println("Invalid value for map dimensions!"); // Error handling
                }
            }

            if (i == 1) { // Map column length
                try {
                    colCount = Integer.parseInt(bLines.get(i)); // String to integer
                } catch (NumberFormatException e) {
                    System.err.println("Invalid value for map dimensions!"); // Error handling
                }
            }

            if (rowCount > 0 && colCount > 0) { battle = new Battle(rowCount, colCount); } // Only creates a battle if map sizes contribute to 2D array
        }

        for (int i = 2; i < bLines.size(); i++) { // Remainder of lines in file all handled similarly
            assert battle != null;
            String[] entity = bLines.get(i).split(","); // Every line is turned into an array of index info

            if (entity.length > 2) { // Not just a coordinate
                switch (entity[2]) { // Analyzes the third index position in the array
                    case "WALL": // Identifies a wall
                        int wrow = Integer.parseInt(entity[0]); // Get coordinate info
                        int wcol = Integer.parseInt(entity[1]);

                        battle.addEntity(wrow, wcol, Wall.getWall()); // Place wall @ coordinate (wrow, wcol)
                        break;

                    case "MAXIMAL": // Identifies Maximal
                        if (entity.length < 8) { // Needs a total of 8 arguments
                            throw new IllegalArgumentException("Invalid data format: Missing Maximal attributes.");
                        }

                        if (entity[3].length() != 1) { // Needs a char variable for symbol
                            throw new IllegalArgumentException("Invalid Maximal symbol length: " + entity[3]);
                        }

                        if (robotSymbols.contains(entity[3])) { // Needs an original symbol
                            throw new IllegalArgumentException("Non-unique robot symbol: " + entity[3]);
                        }

                        int mrow = Integer.parseInt(entity[0]); // Get coordinate info
                        int mcol = Integer.parseInt(entity[1]);

                        if (mrow < 0 || mcol < 0 || mrow >= rowCount || mcol >= colCount) { // Must occur inside of array
                            throw new ArrayIndexOutOfBoundsException("Given Maximal coordinates outside of battle arena.");
                        }

                        char mcREP = entity[3].charAt(0); // Creating all Maximal attributes once error handling occurs and no exceptions are found
                        robotSymbols.add(entity[3]); // Will not let future symbols match this one

                        String maxName = entity[4];
                        int mHP = Integer.parseInt(entity[5]);
                        int mAttack = Integer.parseInt(entity[6]);
                        int mDefense = Integer.parseInt(entity[7]);

                        Maximal maximal = new Maximal(mcREP, maxName, mHP, mAttack, mDefense); // Creates new Maximal instance variable
                        battle.addEntity(mrow, mcol, maximal); // Adds it to the battle
                        break;

                    case "PREDACON":
                        if (entity.length < 7) { //
                            throw new IllegalArgumentException("Invalid data format: Missing PredaCon attributes."); // Needs 7 attributes
                        }

                        if (entity[3].length() != 1) {
                            throw new IllegalArgumentException("Invalid PredaCon symbol length: " + entity[3]);
                        }

                        if (robotSymbols.contains(entity[3])) {
                            throw new IllegalArgumentException("Non-unique robot symbol: " + entity[3]);
                        }

                        int prow = Integer.parseInt(entity[0]);
                        int pcol = Integer.parseInt(entity[1]);

                        if (prow < 0 || pcol < 0 || prow > rowCount || pcol > colCount) {
                            throw new IllegalArgumentException("Given PredaCon coordinates outside of battle arena.");
                        }

                        char pcREP = entity[3].charAt(0);
                        String predName = entity[4];
                        int pHP = Integer.parseInt(entity[5]);
                        WeaponType type = WeaponType.getWeaponType(entity[6].charAt(0));

                        PredaCon predaCon = new PredaCon(pcREP, predName, pHP, type);
                        battle.addEntity(prow, pcol, predaCon);
                        break;

                    default:
                        throw new IllegalStateException("Invalid entity type: " + entity[2]);
                }
            }

        }

        return battle;
    }
}
