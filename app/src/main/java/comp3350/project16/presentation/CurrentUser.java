package comp3350.project16.presentation;

public class CurrentUser {
    
    private static String currentUser = "admin";

    public static void setUser(String newUser){
        currentUser = newUser;
        System.out.println("Current User Is: " + currentUser);
    }
    public static String getCurrentUser(){return currentUser;}
}
