package redblacktree;

import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {

        String fileName = "players_homeruns.csv";
        RedBlackTreeMap tree = new RedBlackTreeMap();
        int i = 0;
        try(Scanner reader = new Scanner(new FileReader(fileName))){
            while(reader.hasNextLine()){
                String line = reader.nextLine();
                String[] split = line.split(",");
                tree.insert(split[0], split[1]);
                i++;
                if(i == 5){
                    System.out.println("****FIVE ELEMENTS*****");
                    tree.printStructure();
                    //break;
                }
                if(i == 10){
                    System.out.println("\n\n*******TEN ELEMENTS*******");
                    tree.printStructure();
                    //break;
                }
            }
        }
        catch(FileNotFoundException ex){}
        catch(RuntimeException ex){
            System.err.println("The key does not exist!!");
        }
        System.out.println("\n\n*****FULL TREE*****");
        tree.printStructure();
        System.out.println("Find");
        System.out.println("Honus Wagner: " + tree.find("Honus Wagner"));
        System.out.println("Stan Musial: " + tree.find("Stan Musial"));
        System.out.println("Rogers Hornsby: " + tree.find("Rogers Hornsby"));
        System.out.println("Ted Williams: " + tree.find("Ted Williams"));
        System.out.println(tree.getCount());
    }
}
