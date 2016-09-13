
import java.util.ArrayList;
import javax.tools.DocumentationTool.Location;

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
    final long id;
    final String USERNAME;
    private Character char1;
    private Character char2;
    private ClientStatus cs;
    private Location home;

    public Player(String username, ClientStatus cs, long id){
        this.USERNAME = username;
        this.cs = cs;
        this.id = id;
        this.char1 = char1;
        this.char2 = char2;
    }
    
    public Character getChar1() {
        return char1;
    }

    public void setChar1(Character char1) {
        this.char1 = char1;
    }

    public Character getChar2() {
        return char2;
    }

    public void setChar2(Character char2) {
        this.char2 = char2;
    }
    
    
}
