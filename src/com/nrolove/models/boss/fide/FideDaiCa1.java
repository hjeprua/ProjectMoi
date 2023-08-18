package com.nrolove.models.boss.fide;

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
public class FideDaiCa1 extends Boss {

    public FideDaiCa1() {
        super(BossFactory.FIDE_DAI_CA_1, BossData.FIDE_DAI_CA_1);
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
        this.textTalkMidle = new String[] { "Xem bản lĩnh của ngươi như nào đã", "Các ngươi tới số mới gặp phải ta" };
        this.textTalkAfter = new String[] { "Ác quỷ biến hình, hêy aaa......." };
    }

    @Override
    public void leaveMap() {
        Boss fd2 = BossFactory.createBoss(BossFactory.FIDE_DAI_CA_2);
        fd2.zone = this.zone;
        fd2.location.x = this.location.x;
        fd2.location.y = this.location.y;
        fd2.changeToAttack();
        fd2.joinMap();
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }

}
