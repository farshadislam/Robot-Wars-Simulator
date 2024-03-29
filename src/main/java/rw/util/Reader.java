package rw.util;

import rw.battle.Battle;
import rw.battle.Maximal;
import rw.battle.PredaCon;
import rw.battle.Wall;
import rw.enums.WeaponType;

import java.io.*;
import java.util.ArrayList;

/**
 * Class to assist reading in battle file
 *
 * @author Jonathan Hudson
 * @version 1.0
 */
public final class Reader {

    public static Battle loadBattle(File file) {
        String fileName = file.getName();

        ArrayList<String> bLines = new ArrayList<>();

        Battle battle;

        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String battleLine;
            while ((battleLine = br.readLine()) != null) {
                bLines.add(battleLine);
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < bLines.size(); i++) {
            int rowCount = 0;
            int colCount = 0;

            if (i == 0 && (bLines.get(i).length() == 1)) {
                try {
                    rowCount = Integer.parseInt(bLines.get(i));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid value for map dimensions!");
                }
            }

            if (i == 1 && (bLines.get(i).length() == 1)) {
                try {
                    colCount = Integer.parseInt(bLines.get(i));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid value for map dimensions!");
                }
            }

            battle = new Battle(rowCount,colCount);

            if (i < 1 && (bLines.get(i).length() > 3)) {
                String[] entity = bLines.get(i).split(",");
                switch (entity[2]) {
                    case "WALL":
                        int wrow = Integer.parseInt(entity[0]);
                        int wcol = Integer.parseInt(entity[1]);

                        battle.addEntity(wrow, wcol, Wall.getWall());

                    case "MAXIMAL":
                        int mrow = Integer.parseInt(entity[0]);
                        int mcol = Integer.parseInt(entity[1]);

                        char mcREP = entity[3].charAt(0);
                        String maxName = entity[4];
                        int mHP = Integer.parseInt(entity[5]);
                        int mAttack = Integer.parseInt(entity[6]);
                        int mDefense = Integer.parseInt(entity[7]);

                        Maximal maximal = new Maximal(mcREP, maxName, mHP, mAttack, mDefense);
                        battle.addEntity(mrow, mcol, maximal);

                    case "PREDACON":

                        int prow = Integer.parseInt(entity[0]);
                        int pcol = Integer.parseInt(entity[1]);

                        char pcREP = entity[3].charAt(0);
                        String predName = entity[4];
                        int pHP = Integer.parseInt(entity[5]);
                        WeaponType type = WeaponType.getWeaponType(entity[6].charAt(0));

                        PredaCon predaCon = new PredaCon(pcREP, predName, pHP, type);
                        battle.addEntity(prow, pcol, predaCon);
                }
            }
        }


        return null;
    }

    private void addNewRobot(String[] battleInfo) {
        if (battleInfo[2].equals("PREDACON")) {

        }
    }
}
