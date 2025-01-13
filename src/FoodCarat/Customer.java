/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FoodCarat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author ASUS
 */
public class Customer extends User{
    private int points;
    
    public Customer(String email){ //for set points
        super(email);
        //paolawan@mail.com,,0.0,13
        try{
            BufferedReader br = new BufferedReader(new FileReader("resources/user.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] record = line.split(",");
                if (email.equals(record[0])) {
                    this.points = Integer.parseInt(record[3]);
                }
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    
    //point system related
    public void addPoints(int add){
        this.points += add;
        try{
            //Writing to file
            BufferedReader br = new BufferedReader(new FileReader("resources/user.txt"));
            StringBuffer buffer = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null){
                String record[] = line.split(",");
                String checkUsername = record[0];
                if (checkUsername.equals(email)){
                    line = record[0] + "," + record[1] + ","+ record[2] + "," + points;
                }
                String bufferLine = line + "\n";
                buffer.append(bufferLine);
            }
            br.close();
            BufferedWriter bw = new BufferedWriter(new FileWriter("resources/user.txt"));
            bw.write(buffer.toString());
            bw.close();   
    }
        catch(IOException e){
            e.printStackTrace();
        }

    }
    
    public void deductPoints(int sub){
        this.points -= sub;
        try{
            //Writing to file
            BufferedReader br = new BufferedReader(new FileReader("resources/user.txt"));
            StringBuffer buffer = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null){
                String record[] = line.split(",");
                String checkUsername = record[0];
                if (checkUsername.equals(email)){
                    line = record[0] + "," + record[1] + ","+ record[2] + "," + points;
                }
                String bufferLine = line + "\n";
                buffer.append(bufferLine);
            }
            br.close();
            BufferedWriter bw = new BufferedWriter(new FileWriter("resources/user.txt"));
            bw.write(buffer.toString());
            bw.close();   
        }
        catch(IOException e){
            e.printStackTrace();
        }

    }
    
    public int getPoints(){
        return points;
    }
    
    public int calculateEarnablePoints(double payment){
        int earnablePoints = (int) Math.round(payment*0.05);
        return earnablePoints;
    }
}
