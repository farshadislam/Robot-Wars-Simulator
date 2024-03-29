package rw.util;

import rw.battle.Battle;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Writer {
   public static void main(String[] args) {
       try {
           BufferedWriter bw = new BufferedWriter(new FileWriter("new_battle.txt"));
           bw.write("Writing to file...");
           bw.close();
       } catch (IOException ex) {
           throw new RuntimeException(ex);
       }


   }


}
