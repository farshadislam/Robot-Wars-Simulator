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

    public static Battle loadBattle(File file) {

        ArrayList<String> bLines = new ArrayList<>();

        Battle battle = null;

        int rowCount = 0;

        int colCount = 0;

        HashSet<String> robotSymbols = new HashSet<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String battleLine;
            while ((battleLine = br.readLine()) != null) {
                bLines.add(battleLine);
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        for (int i = 0; i < 2; i++) {
            if (i == 0) {
                try {
                    rowCount = Integer.parseInt(bLines.get(i));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid value for map dimensions!");
                }
            }

            if (i == 1) {
                try {
                    colCount = Integer.parseInt(bLines.get(i));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid value for map dimensions!");
                }
            }

            if (rowCount > 0 && colCount > 0) {battle = new Battle(rowCount, colCount);}
        }

        for (int i = 2; i < bLines.size(); i++) {
            assert battle != null;
            String[] entity = bLines.get(i).split(",");

            if (entity.length > 2) {
                switch (entity[2]) {
                    case "WALL":
                        int wrow = Integer.parseInt(entity[0]);
                        int wcol = Integer.parseInt(entity[1]);

                        battle.addEntity(wrow, wcol, Wall.getWall());
                        break;

                    case "MAXIMAL":
                        if (entity.length < 8) {
                            throw new IllegalArgumentException("Invalid data format: Missing Maximal attributes.");
                        }

                        if (entity[3].length() != 1) {
                            throw new IllegalArgumentException("Invalid Maximal symbol length: " + entity[3]);
                        }

                        if (robotSymbols.contains(entity[3])) {
                            throw new IllegalArgumentException("Non-unique robot symbol: " + entity[3]);
                        }

                        int mrow = Integer.parseInt(entity[0]);
                        int mcol = Integer.parseInt(entity[1]);

                        if (mrow < 0 || mcol < 0 || mrow >= rowCount || mcol >= colCount) {
                            throw new ArrayIndexOutOfBoundsException("Given Maximal coordinates outside of battle arena.");
                        }

                        char mcREP = entity[3].charAt(0);
                        robotSymbols.add(entity[3]);

                        String maxName = entity[4];
                        int mHP = Integer.parseInt(entity[5]);
                        int mAttack = Integer.parseInt(entity[6]);
                        int mDefense = Integer.parseInt(entity[7]);

                        Maximal maximal = new Maximal(mcREP, maxName, mHP, mAttack, mDefense);
                        battle.addEntity(mrow, mcol, maximal);
                        break;

                    case "PREDACON":
                        if (entity.length < 7) {
                            throw new IllegalArgumentException("Invalid data format: Missing PredaCon attributes.");
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
