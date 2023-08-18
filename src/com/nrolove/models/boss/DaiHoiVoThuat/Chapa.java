package com.nrolove.models.boss.DaiHoiVoThuat;

import com.nrolove.models.boss.Boss;
import com.nrolove.models.boss.BossData;
import com.nrolove.models.boss.BossFactory;
import com.nrolove.models.boss.BossManager;
import com.nrolove.models.item.Item;
import com.nrolove.models.map.ItemMap;
import com.nrolove.models.player.Player;
import com.nrolove.services.RewardService;
import com.nrolove.services.Service;
import com.nrolove.utils.Util;

public class Chapa extends Boss {

    public Chapa(){
        super(BossFactory.CHAPA, BossData.CHA_PA);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void rewards(Player pl) {
        leaveMap();
    }

    @Override
    public void idle() {

    }

    @Override
    public void checkPlayerDie(Player pl) {

    }

    @Override
    public void initTalk() {
        textTalkMidle = new String[] { "Oắt con ta đánh ngươi" };
        textTalkAfter = new String[] { "haha chừa chưa nhóc" };
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }
}
