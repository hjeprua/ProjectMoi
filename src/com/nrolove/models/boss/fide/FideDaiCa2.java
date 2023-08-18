package com.nrolove.models.boss.fide;

import com.nrolove.models.boss.Boss;
import com.nrolove.models.boss.BossData;
import com.nrolove.models.boss.BossFactory;
import com.nrolove.models.boss.BossManager;
import com.nrolove.models.player.Player;
import com.nrolove.server.ServerNotify;
import com.nrolove.services.Service;
import com.nrolove.services.TaskService;
import com.nrolove.services.func.ChangeMapService;

/**
 *
 * @author 💖 PuPuBug 💖
 *
 */
public class FideDaiCa2 extends Boss {

    public FideDaiCa2() {
        super(BossFactory.FIDE_DAI_CA_2, BossData.FIDE_DAI_CA_2);
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
        this.textTalkMidle = new String[]{"Xem bản lĩnh của ngươi như nào đã", "Các ngươi tới số mới gặp phải ta"};
        this.textTalkAfter = new String[]{"Ác quỷ biến hình, hêy aaa......."};
    }

    @Override
    public void leaveMap() {
        Boss fd3 = BossFactory.createBoss(BossFactory.FIDE_DAI_CA_3);
        fd3.zone = this.zone;
        fd3.location.x = this.location.x;
        fd3.location.y = this.location.y;
        fd3.changeToAttack();
        fd3.joinMap();
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }

    @Override
    public void joinMap() {
        if (this.zone != null) {
            ChangeMapService.gI().changeMap(this, zone, this.location.x, this.location.y);
            ServerNotify.gI().notify("Boss " + this.name + " vừa xuất hiện tại " + this.zone.map.mapName);
        }
    }
}
