package com.nrolove.models.boss.cell;

import com.nrolove.consts.ConstRatio;
import com.nrolove.models.boss.Boss;
import com.nrolove.models.boss.BossData;
import com.nrolove.models.boss.BossFactory;
import com.nrolove.models.boss.BossManager;
import com.nrolove.models.map.ItemMap;
import com.nrolove.models.player.Player;
import com.nrolove.services.Service;
import com.nrolove.services.SkillService;
import com.nrolove.services.TaskService;
import com.nrolove.utils.Logger;
import com.nrolove.utils.SkillUtil;
import com.nrolove.utils.Util;

/**
 *
 * @author 💖 PuPuBug 💖
 *
 */
public class XenBoHung2 extends Boss {

    public XenBoHung2() {
        super(BossFactory.XEN_BO_HUNG_2, BossData.XEN_BO_HUNG_2);
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
        if (Util.isTrue(1, 20)) {
            ItemMap itemMap = null;
            int x = this.location.x;
            int y = this.zone.map.yPhysicInTop(x, this.location.y - 24);
            itemMap = new ItemMap(pl.zone, 16, 1, x, y, pl.id);
            Service.getInstance().dropItemMap(zone, itemMap);
        }
        TaskService.gI().checkDoneTaskKillBoss(pl, this);
    }

    @Override
    public void attack() {
        try {
            Player pl = getPlayerAttack();
            if (pl != null) {
                this.playerSkill.skillSelect = this.getSkillAttack();
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(15, ConstRatio.PER100) && SkillUtil.isUseSkillChuong(this)) {
                        goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 80)),
                                Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50), false);
                    }
                    SkillService.gI().useSkill(this, pl, null ,null );
                    checkPlayerDie(pl);
                } else {
                    goToPlayer(pl, false);
                }
            }
        } catch (Exception ex) {
            Logger.logException(XenBoHung2.class, ex);
        }
    }

    @Override
    public void idle() {

    }

    @Override
    public void checkPlayerDie(Player pl) {

    }

    @Override
    public void initTalk() {
        this.textTalkBefore = new String[]{};
        this.textTalkMidle = new String[]{"Tất cả nhào vô", "Mình ta cũng đủ để hủy diệt các ngươi"};
        this.textTalkAfter = new String[]{};
    }

    @Override
    public void leaveMap() {
        Boss xht = BossFactory.createBoss(BossFactory.XEN_BO_HUNG_HOAN_THIEN);
        xht.zone = this.zone;
        xht.changeToAttack();
        xht.joinMap();
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }
    
    

}
