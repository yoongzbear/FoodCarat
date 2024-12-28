/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FoodCarat;

/**
 *
 * @author Yuna
 */
public class userSession {
    private static String loggedInUser;
    
    public static String getLoggedInUser(){
        return loggedInUser;
    }
    
    public static void setLoggedInUser(String User){
        loggedInUser = User;
    }
    
    public static void clearSession(){
        loggedInUser = null;
    }
}
