package rw.util;

import rw.battle.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Writer {
   public static void saveBattle(Battle battle) {
       ArrayList<String> battleDetails = new ArrayList<>();
       battleDetails.add(String.valueOf(battle.getRows()));
       battleDetails.add(String.valueOf(battle.getColumns()));

       for (int i = 0; i < battle.getRows(); i++) {
           for (int j = 0; j < battle.getColumns(); j++) {
               Entity entity = battle.getEntity(i,j);
               String battleLine = i + "," + j;

               if (entity instanceof PredaCon) {
                   battleLine = battleLine + ",PREDACON," + entity.getSymbol() + "," + ((PredaCon) entity).getName() + "," + ((PredaCon) entity).getHealth() + "," + ((PredaCon) entity).getWeaponType();
               }

               if (entity instanceof Maximal) {
                   battleLine = battleLine + ",MAXIMAL," + entity.getSymbol() + "," + ((Maximal) entity).getName() + "," + ((Maximal) entity).getHealth() + "," + ((Maximal) entity).weaponStrength() + "," + ((Maximal) entity).armorStrength();
               }

               if (entity instanceof Wall) {
                   battleLine = battleLine + ",WALL";
               }

               battleDetails.add(battleLine);
           }
       }

       try {
           BufferedWriter bw = new BufferedWriter(new FileWriter("new_battle.txt"));
           for (String coordinateInfo : battleDetails) {
               bw.write(coordinateInfo);
           }
           bw.close();
       } catch (IOException ex) {
           throw new RuntimeException(ex);
       }

   }


}
