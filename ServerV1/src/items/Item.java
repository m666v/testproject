package items;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Tesunami
 */
public abstract class Item {
    private long id;
    private String name;
    private ItemSlot itemSlot;
    private int level;
    private long bounded;
    private boolean useable;
}
