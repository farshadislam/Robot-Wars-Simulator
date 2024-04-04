/**
 * Author: Farshad Islam
 * Tutorial: 07
 * Tuesday, April 2nd, 2024
 **/

package rw.util;

import rw.battle.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Writer {
    /**
     *
     * @params battle, file
     * @returns N/A
     */
    public static void saveBattle(Battle battle, File file) {
       ArrayList<String> battleDetails = new ArrayList<>(); // Takes in all lines as readable strings
       battleDetails.add(String.valueOf(battle.getRows())); // Gets map row size
       battleDetails.add(String.valueOf(battle.getColumns())); // Gets map column size

       for (int i = 0; i < battle.getRows(); i++) {
           for (int j = 0; j < battle.getColumns(); j++) {
               Entity entity = battle.getEntity(i,j); // Identify the Entity object in the battle coordinate
               String battleLine = i + "," + j; // Always write out the coordinate first

               if (entity instanceof PredaCon) { // Retrieves all relevant info for a PredaCon object
                   battleLine = battleLine + ",PREDACON," + entity.getSymbol() + "," + ((PredaCon) entity).getName() + "," + ((PredaCon) entity).getHealth() + "," + ((PredaCon) entity).getWeaponType();
               }

               if (entity instanceof Maximal) { // Retrieves all relevant info for a Maximal object
                   battleLine = battleLine + ",MAXIMAL," + entity.getSymbol() + "," + ((Maximal) entity).getName() + "," + ((Maximal) entity).getHealth() + "," + ((Maximal) entity).weaponStrength() + "," + ((Maximal) entity).armorStrength();
               }

               if (entity instanceof Wall) { // Wall has no special attributes; it is just a wall
                   battleLine = battleLine + ",WALL";
               }

               battleDetails.add(battleLine); // Completed line gets added to battleDetails
           }
       }

       try {
           BufferedWriter bw = new BufferedWriter(new FileWriter(file.getName())); // Initialize Writer.java
           for (String coordinateInfo : battleDetails) {
               bw.write(coordinateInfo); // Write every string that is in battleDetails
               bw.write("\n"); // New line so that everything doesn't output to the same line
           }
           bw.close();
       } catch (IOException ex) {
           throw new RuntimeException(ex); // Take in exception if this process fails
       }

   }


}
