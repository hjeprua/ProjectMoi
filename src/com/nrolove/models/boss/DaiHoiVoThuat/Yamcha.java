package com.nrolove.models.boss.DaiHoiVoThuat;

import com.nrolove.models.boss.Boss;
import com.nrolove.models.boss.BossData;
import com.nrolove.models.boss.BossFactory;
import com.nrolove.models.boss.BossManager;
import com.nrolove.models.player.Player;
public class Yamcha extends Boss {

    public Yamcha(){
        super(BossFactory.YAMCHA, BossData.YAMCHA);
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
