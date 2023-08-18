package com.nrolove.models.boss.tieudoisatthu;

import com.nrolove.models.boss.Boss;
import com.nrolove.models.boss.BossData;
import com.nrolove.models.boss.BossFactory;
import com.nrolove.models.boss.BossManager;
import com.nrolove.models.player.Player;
import com.nrolove.services.TaskService;

/**
 *
 * @author 💖 PuPuBug 💖
 *
 */
public class So4 extends Boss {

    public So4() {
        super(BossFactory.SO4, BossData.SO4);
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
            BossManager.gI().getBossById(BossFactory.SO3).joinMap();
            BossManager.gI().getBossById(BossFactory.SO2).joinMap();
            BossManager.gI().getBossById(BossFactory.SO1).joinMap();
            BossManager.gI().getBossById(BossFactory.TIEU_DOI_TRUONG).joinMap();
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
        this.textTalkBefore = new String[] { "" };
        this.textTalkMidle = new String[] { "Oải rồi hả?", "Ê cố lên nhóc",
                "Chán", "Đại ca Fide có nhầm không nhỉ" };
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
        BossManager.gI().getBossById(BossFactory.SO3).changeToAttack();
    }
}
