
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Tesunami
 */
public class Player {
    final String USERNAME;
    private String password;
    private ArrayList<Character> listOfChar;
    public Player(String username, String pass){
        this.USERNAME = username;
        this.password = pass;
    }
    
    public void addChar(Character c){
        this.listOfChar.add(c);
    }
}
