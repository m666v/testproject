/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package items;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Tesunami
 */
public class Bag extends Item{
    private int slotnumber;
    private long a[];
    
    public Bag(int slotNumber){
        this.slotnumber = slotNumber;
        this.a = new long[slotNumber];
    }
    
    
    public static Bag getBag(long id, Connection con) throws SQLException{
        PreparedStatement ps = con.prepareStatement("SELECT * FROM BAGS WHERE ID = ?");
        ps.setLong(1, id);
        ResultSet rs = ps.executeQuery();
        int slotNumber = rs.getInt("SLOTNUMBER");
        Bag b = new Bag(slotNumber);
        for(int i = 1;i<=slotNumber;i++){
            b.a[i] = rs.getLong(i);
        }
        return null;
    }
}
