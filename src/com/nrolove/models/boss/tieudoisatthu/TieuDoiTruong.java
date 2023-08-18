package com.nrolove.models.boss.tieudoisatthu;

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
public class TieuDoiTruong extends Boss {

    public TieuDoiTruong() {
        super(BossFactory.TIEU_DOI_TRUONG, BossData.TIEU_DOI_TRUONG);
        Zone currZone = getCurrZone();
        BossFactory.createBoss(BossFactory.SO1).zone = currZone;
        BossFactory.createBoss(BossFactory.SO2).zone = currZone;
        BossFactory.createBoss(BossFactory.SO3).zone = currZone;
        BossFactory.createBoss(BossFactory.SO4).zone = currZone;
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
        this.textTalkMidle = new String[] { "Oải rồi hả?", "Ê cố lên nhóc",
                "Chán", "Đại ca Fide có nhầm không nhỉ" };
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
        BossFactory.createBoss(BossFactory.TIEU_DOI_TRUONG);
    }
}
