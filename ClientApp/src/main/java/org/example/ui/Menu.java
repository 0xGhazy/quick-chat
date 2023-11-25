package org.example.ui;

public class Menu {

    public static void printHelpMenu() {
        System.out.println("\n\tCommand             Description");
        System.out.println("\t=======             ===========");
        System.out.println("\t/help               print the help list of available commands");
        System.out.println("\t/signup             signup a new user to the server");
        System.out.println("\t/login              login to your account");
        System.out.println("\t/reset              reset your account password");
        System.out.println("\t/exit               exit the application :)\n");
    }

    public static void printActiveUserHelpMenu() {
        System.out.println("\n\t Main Menu help:");
        System.out.println("\t================");
        System.out.println("\tCommand             Description");
        System.out.println("\t=======             ===========");
//        System.out.println("\t/live               list all current active login users");
        System.out.println("\t/join               join the room chat");
        System.out.println("\t/leave              leave the chat room");
        System.out.println("\t/visual             display word usage statistics");
        System.out.println("\t/dump               dump your user profile data including converstaions and words.");
        System.out.println("\t/logout             logout from mini discord app\n");
    }

}
