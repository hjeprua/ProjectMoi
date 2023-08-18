package com.nrolove.models.boss.cell;

import com.nrolove.consts.ConstPlayer;
import com.nrolove.consts.ConstRatio;
import com.nrolove.models.boss.Boss;
import com.nrolove.models.boss.BossData;
import com.nrolove.models.boss.BossFactory;
import com.nrolove.models.boss.BossManager;
import com.nrolove.models.map.ItemMap;
import com.nrolove.models.map.Zone;
import com.nrolove.models.player.Player;
import com.nrolove.models.skill.Skill;
import com.nrolove.services.PlayerService;
import com.nrolove.services.RewardService;
import com.nrolove.services.Service;
import com.nrolove.services.SkillService;
import com.nrolove.services.TaskService;
import com.nrolove.services.func.ChangeMapService;
import com.nrolove.utils.Logger;
import com.nrolove.utils.SkillUtil;
import com.nrolove.utils.Util;

/**
 *
 * @author 💖 PuPuBug 💖
 *
 */
public class XenBoHung extends Boss {

    public XenBoHung() {
        super(BossFactory.XEN_BO_HUNG, BossData.XEN_BO_HUNG);
        Zone currZone = getCurrZone();
        BossFactory.createBoss(BossFactory.XEN_CON).zone = currZone;
    }

    @Override
    protected boolean useSpecialSkill() {
        this.playerSkill.skillSelect = this.getSkillSpecial();
        if (SkillService.gI().canUseSkillWithCooldown(this)) {
            SkillService.gI().useSkill(this, null, null);
            return true;
        } else {
            return false;
        }
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
    public void attack() {
        // if (BossManager.gI().getBossById(BossFactory.XEN_CON) != null) {
        // PlayerService.gI().changeAndSendTypePK(this, ConstPlayer.NON_PK);
        // this.changeIdle();
        // return;
        // }
        if (this.isDie()) {
            tuSat();
            die();
            return;
        }
        try {
            Player pl = getPlayerAttack();
            if (pl != null) {
                if (!useSpecialSkill()) {
                    this.playerSkill.skillSelect = this.getSkillAttack();
                    if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                        if (Util.isTrue(15, ConstRatio.PER100) && SkillUtil.isUseSkillChuong(this)) {
                            goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 80)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50),
                                    false);
                        }
                        SkillService.gI().useSkill(this, pl, null);
                        checkPlayerDie(pl);
                    } else {
                        goToPlayer(pl, false);
                    }
                }
            }
        } catch (Exception ex) {
            Logger.logException(XenBoHung.class, ex);
        }
    }

    @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (this.isDie() || this.playerSkill.prepareTuSat) {
            return 0;
        } else {
            int dame = super.injuredNotCheckDie(plAtt, damage, piercing);
            if (this.isDie()) {
                rewards(plAtt);
            }
            return dame;
        }
    }

    private void tuSat() {
        try {
            this.nPoint.hpg = 1000000000;
            this.nPoint.hp = 1;
            ChangeMapService.gI().changeMap(this, this.zone, this.location.x, this.location.y);
            PlayerService.gI().changeAndSendTypePK(this, ConstPlayer.NON_PK);
            PlayerService.gI().changeTypePK(this, ConstPlayer.PK_ALL);
            this.playerSkill.skillSelect = this.getSkillById(Skill.TU_SAT);
            SkillService.gI().useSkill(this, null, null);
            Thread.sleep(3000);
            SkillService.gI().useSkill(this, null, null);
        } catch (Exception e) {
            Logger.logException(XenBoHung.class, e);
        }
    }

    @Override
    public void idle() {
        if (BossManager.gI().getBossById(BossFactory.XEN_CON) == null) {
            PlayerService.gI().changeAndSendTypePK(this, ConstPlayer.PK_ALL);
            changeAttack();
        }
    }

    @Override
    public void rewards(Player pl) {
        if (pl != null) {
            ItemMap itemMap = null;
            int x = this.location.x;
            int y = this.zone.map.yPhysicInTop(x, this.location.y - 24);
            if (Util.isTrue(1, 15)) {
                int[] set1 = { 562, 564, 566, 561 };
                itemMap = new ItemMap(this.zone, set1[Util.nextInt(0, set1.length - 1)], 1, x, y, pl.id);
                RewardService.gI().initBaseOptionClothes(itemMap.itemTemplate.id, itemMap.itemTemplate.type,
                        itemMap.options);
            } else if (Util.isTrue(1, 10)) {
                int[] set2 = { 555, 556, 563, 557, 558, 565, 559, 567, 560 };
                itemMap = new ItemMap(this.zone, set2[Util.nextInt(0, set2.length - 1)], 1, x, y, pl.id);
                RewardService.gI().initBaseOptionClothes(itemMap.itemTemplate.id, itemMap.itemTemplate.type,
                        itemMap.options);
            } else if (Util.isTrue(1, 5)) {
                itemMap = new ItemMap(this.zone, 15, 1, x, y, pl.id);
            } else if (Util.isTrue(1, 2)) {
                itemMap = new ItemMap(this.zone, 16, 1, x, y, pl.id);
            }
            if (itemMap != null) {
                Service.getInstance().dropItemMap(zone, itemMap);
            }
            TaskService.gI().checkDoneTaskKillBoss(pl, this);

            notifyPlayeKill(pl);
        }
    }

    @Override
    public void checkPlayerDie(Player pl) {
    }

    @Override
    public void initTalk() {
        this.textTalkBefore = new String[] { "Ta cho các ngươi 5 giây để chuẩn bị", "Cuộc chơi bắt đầu.." };
        this.textTalkMidle = new String[] { "Kame Kame Haaaaa!!", "Mi khá đấy nhưng so với ta chỉ là hạng tôm tép",
                "Tất cả nhào vô hết đi", "Cứ chưởng tiếp đi. haha", "Các ngươi yếu thế này sao hạ được ta đây. haha",
                "Khi công pháo!!", "Cho mi biết sự lợi hại của ta" };
        this.textTalkAfter = new String[] {};
    }

    @Override
    public void leaveMap() {
        Boss sieuBoHung = BossFactory.createBoss(BossFactory.SIEU_BO_HUNG);
        sieuBoHung.zone = this.zone;
        sieuBoHung.changeToAttack();
        sieuBoHung.joinMap();
        super.leaveMap();
        BossManager.gI().removeBoss(this);
        this.changeToIdle();
    }
}
