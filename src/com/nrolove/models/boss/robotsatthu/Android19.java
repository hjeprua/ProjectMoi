package com.nrolove.models.boss.robotsatthu;

import com.nrolove.models.boss.Boss;
import com.nrolove.models.boss.BossData;
import com.nrolove.models.boss.BossFactory;
import com.nrolove.models.boss.BossManager;
import com.nrolove.models.player.Player;
import com.nrolove.models.skill.Skill;
import com.nrolove.services.PlayerService;
import com.nrolove.services.TaskService;

/**
 *
 * @author 💖 PuPuBug 💖
 *
 */
public class Android19 extends Boss {

    public Android19() {
        super(BossFactory.ANDROID_19, BossData.ANDROID_19);
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
            BossManager.gI().getBossById(BossFactory.ANDROID_20).joinMap();
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
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (plAtt != null) {
            switch (plAtt.playerSkill.skillSelect.template.id) {
                case Skill.KAMEJOKO:
                case Skill.MASENKO:
                case Skill.ANTOMIC:
                    int hpHoi = (int) (damage - ((long) damage * 80 / 100));
                    PlayerService.gI().hoiPhuc(this, hpHoi, 0);
                    return 0;
            }
        }
        return super.injured(plAtt, damage, piercing, isMobAttack);
    }
    
    @Override
    public void leaveMap() {
        BossManager.gI().getBossById(BossFactory.ANDROID_20).changeToAttack();
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }

}
