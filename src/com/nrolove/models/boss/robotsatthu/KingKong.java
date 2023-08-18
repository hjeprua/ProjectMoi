package com.nrolove.models.boss.robotsatthu;

import com.nrolove.models.boss.Boss;
import com.nrolove.models.boss.BossData;
import com.nrolove.models.boss.BossFactory;
import com.nrolove.models.boss.BossManager;
import com.nrolove.models.map.Zone;
import com.nrolove.models.player.Player;
import com.nrolove.services.TaskService;

/**
 *
 * @author 💖 PuPuBug 💖
 *
 */
public class KingKong extends Boss {

    public KingKong() {
        super(BossFactory.KINGKONG, BossData.KINGKONG);
        Zone currZone = getCurrZone();
        BossFactory.createBoss(BossFactory.POC).zone = currZone;
        BossFactory.createBoss(BossFactory.PIC).zone = currZone;
    }

    @Override
    public void joinMap() {
        super.joinMap();
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void update() {
        super.update();
        if (!this.isDie() && this.status == ATTACK && this.timeJoinZone != 0
                && this.timeJoinZone + this.timeReset < System.currentTimeMillis()) {
            this.nPoint.hp = this.nPoint.hpMax;
            joinMap();
            return;
        }
    }

    @Override
    public void rewards(Player pl) {
        TaskService.gI().checkDoneTaskKillBoss(pl, this);
    }

    @Override
    public void idle() {
    }

    @Override
    public void checkPlayerDie(Player pl) {
    }

    @Override
    public void initTalk() {
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
        BossFactory.createBoss(BossFactory.KINGKONG);
    }
}
