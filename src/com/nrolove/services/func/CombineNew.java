package com.nrolove.services.func;

import com.nrolove.models.item.Item;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN  💖
 *
 */
public class CombineNew {

    public long lastTimeCombine;
    
    public List<Item> itemsCombine;
    public int typeCombine;
    
    public int goldCombine;
    public int gemCombine;
    public float ratioCombine;
    public int countDaNangCap;
    public short quantities = 1;

    public CombineNew() {
        this.itemsCombine = new ArrayList<>();
    }
    
    public void setTypeCombine(int type){
        this.typeCombine = type;
    }
    
    public void clearItemCombine(){
        this.itemsCombine.clear();
    }
    
    public void clearParamCombine(){
        this.goldCombine = 0;
        this.gemCombine = 0;
        this.ratioCombine = 0;
        this.countDaNangCap = 0;
    }
    
    public void dispose(){
        this.itemsCombine = null;
    }
}
