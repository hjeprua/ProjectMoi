package com.nrolove.models.intrinsic;

import com.nrolove.models.player.Player;
import com.nrolove.services.IntrinsicService;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 *
 */
public class IntrinsicPlayer {

    private Player player;

    public byte countOpen;

    public Intrinsic intrinsic;

    public IntrinsicPlayer(Player player) {
        this.player = player;
        this.intrinsic = IntrinsicService.gI().getIntrinsicById(0);
    }

    public void dispose(){
        this.player = null;
        this.intrinsic = null;
    }
}
