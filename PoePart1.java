/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package poepart3;

import javax.swing.JOptionPane;

/**
 *
 * @author User
 */
public class PoePart1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Login register = new Login();
       
        Message msg = new Message();
        
        JOptionPane.showMessageDialog(null,
                "===== Register =====",
                "Registration",
                JOptionPane.INFORMATION_MESSAGE
        );

        String name = JOptionPane.showInputDialog(null, "Please eneter the username");
        String password = JOptionPane.showInputDialog(null, "Please eneter the Password");
        String cellphone = JOptionPane.showInputDialog(null, "Please eneter the Cellphone Number");

        JOptionPane.showMessageDialog(null, register.registerUser(name,
                password, cellphone));

        if (!register.registerStatus(name,
                password, cellphone)) {
            return;
        } // this doesn't really do anything. It is just a condition

        JOptionPane.showMessageDialog(null,
                "\n===== Login =====",
                "Login",
                JOptionPane.INFORMATION_MESSAGE
        );
        
        String loginUsername = JOptionPane.showInputDialog(null, "Please eneter the username");
        String loginPassword = JOptionPane.showInputDialog(null, "Please eneter the Password");

        boolean status = register.loginUser(loginUsername, loginPassword);
        JOptionPane.showMessageDialog(null, register.returnLoginStatus(status) );

        if (status == true) {

            int option = 0;

            do { // change to JoptionPane
                
                String menu = "===== Select any of the options below =====" 
                        + "1. Send message"
                        + "2. Coming soon"
                        + "3. Exit"
                        + "Select an option" ;
                
                String input = JOptionPane.showInputDialog(null,
                        menu,
                        "Main menu",
                        JOptionPane.QUESTION_MESSAGE);
                
                if (input == null){
                    option = 3;
                    
                }else{
                    
                    try{ 
                        option = Integer.parseInt(input);
                        switch (option) {
                    case 1 ->
                        msg.sendingMessages();
                    case 2 ->
                        msg.messageManagementMenu();
                    case 3 ->
                        JOptionPane.showMessageDialog(null, 
                                "Good bye!!");
                    default ->
                        JOptionPane.showMessageDialog(null,
                                "Invalid option!!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
                        
                    } catch (NumberFormatException e) {
    System.out.println("Please enter a number from 1 to 4.");
}
                }

                

            } while (option != 3);
        }

    }

}
