package com.nrolove.models.boss.broly;

import com.nrolove.models.boss.BossData;
import com.nrolove.models.boss.BossFactory;
import com.nrolove.models.player.Player;

/**
 *
 * @author 💖 PuPuBug 💖
 *
 */
public class SuperBrolyRed extends SuperBroly {

    public SuperBrolyRed() {
        super(BossFactory.SUPER_BROLY_RED, BossData.SUPER_BROLY_RED);
    }

    @Override
    public void rewards(Player pl) {
        this.dropItemReward(568, (int) pl.id);
    }
}
