package com.nrolove.models.player;
import com.nrolove.models.player.Player;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 *
 */
public class FriendEnemy extends Player {

    public FriendEnemy() {
        this.inventory = new Inventory(this);
    }
}
