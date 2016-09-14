
import items.Bag;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Tesunami
 */
public class Character {

    private long id;
    private String name;
    private CharClass charClass;
    private int level;
    private long head;
    private long chest;
    private long leg;
    private long experience;
    private double longtitude;
    private double latitude;
    private long bag0id;
    private Bag bag0;
    private boolean defaultChar;

    public Character(long id, String name, short charClass, long head, long chest, long leg, long bag0id, long experience, double longtitude, double latitude) {
        this.id = id;
        this.name = name;
        switch (charClass) {
            case 0:
                this.charClass = CharClass.WARRIOR;
                break;
            case 1:
                this.charClass = CharClass.Warlock;
                break;
            case 2:
                this.charClass = CharClass.Rogue;
                break;
            case 3:
                this.charClass = CharClass.Priest;
                break;
            case 4:
                this.charClass = CharClass.Mage;
                break;
        }

        this.head = head;
        this.chest = chest;
        this.leg = leg;
        this.bag0id = bag0id;
        this.experience = experience;
        this.longtitude = longtitude;
        this.latitude = latitude;
    }

    private String enterWorld(double longtitude, double latitude, Connection con) throws SQLException {
            bag0 = Bag.getBag(id, con);
            return null;
    }

    static Character getCharacter(long id, Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement("SELECT * FROM CHARACTERS WHERE ID = ?");
        ps.setLong(1, id);
        ResultSet rs = ps.executeQuery();
        return new Character(rs.getLong("ID"), rs.getString("NAME"), rs.getShort("CLASS"), rs.getLong("HEAD"), rs.getLong("CHEST"), rs.getLong("LEG"),
                rs.getLong("BAG0"), rs.getLong("EXPERIENCE"), rs.getDouble("LONGTITUDE"), rs.getDouble("LATITUDE"));

    }
}
