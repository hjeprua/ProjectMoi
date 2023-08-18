package com.nrolove.models.shop;

import java.util.ArrayList;
import java.util.List;

import com.nrolove.models.Template;
import com.nrolove.models.item.Item;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN  💖
 *
 */
public class ItemShop {
    
    public TabShop tabShop;

    public int id;
    
    public Template.ItemTemplate temp;
    public int type_sell;
    public int gold;
    
    public int gem;
    
    public boolean isNew;
    
    public List<Item.ItemOption> options;
    
    public int iconSpec;
    
    public int costSpec;

    public ItemShop() {
        this.options = new ArrayList<>();
    }
    
    public ItemShop(ItemShop itemShop){
        this.tabShop = itemShop.tabShop;
        this.id = itemShop.id;
        this.temp = itemShop.temp;
        this.type_sell = itemShop.type_sell;
        this.gold = itemShop.gold;
        this.gem = itemShop.gem;
        this.isNew = itemShop.isNew;
        this.options = new ArrayList<>();
        for(Item.ItemOption io : itemShop.options){
            this.options.add(new Item.ItemOption(io));
        }
    }
    
    
}
