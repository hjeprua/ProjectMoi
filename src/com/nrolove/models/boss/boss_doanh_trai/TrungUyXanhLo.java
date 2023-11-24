package com.nrolove.models.boss.boss_doanh_trai;

import com.nrolove.consts.ConstRatio;
import com.nrolove.models.boss.BossData;
import com.nrolove.models.boss.BossFactory;
import com.nrolove.services.func.ChangeMapService;
import com.nrolove.models.map.phoban.DoanhTrai;
import com.nrolove.models.player.Player;
import com.nrolove.services.SkillService;
import com.nrolove.utils.Util;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author 💖 PuPuBug 💖
 *
 */
public class TrungUyXanhLo extends BossDoanhTrai {

    private boolean activeAttack;

    public TrungUyXanhLo(DoanhTrai doanhTrai) {
        super(BossFactory.TRUNG_UY_XANH_LO, BossData.TRUNG_UY_XANH_LO, doanhTrai);
    }

    @Override
    public void attack() {
        try {
            if (activeAttack) {
                if (!useSpecialSkill()) {
                    Player pl = getPlayerAttack();
                    this.playerSkill.skillSelect = this.getSkillAttack();
                    if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                        if (Util.isTrue(10, ConstRatio.PER100)) {
                            goToXY(pl.location.x + Util.nextInt(-20, 20),
                                    Util.nextInt(pl.location.y - 80, this.zone.map.yPhysicInTop(pl.location.x, 0)), false);
                        }
                        SkillService.gI().useSkill(this, pl, null ,null );
                        checkPlayerDie(pl);
                    } else {
                        goToPlayer(pl, false);
                    }
                }
            } else {
                List<Player> notBosses = this.zone.getNotBosses();
                for (Player pl : notBosses) {

                    if (pl.location.x >= 820 && !pl.effectSkin.isVoHinh) {
                        this.activeAttack = true;
                        break;
                    }
                }
            }
        } catch (Exception ex) {
//            ex.printStackTrace();
        }
    }

    @Override
    protected boolean useSpecialSkill() {
        //boss này chỉ có chiêu thái dương hạ san
        this.playerSkill.skillSelect = this.getSkillSpecial();
        if (SkillService.gI().canUseSkillWithCooldown(this)) {
            SkillService.gI().useSkill(this, null, null ,null );
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void joinMap() {
        try {
            this.zone = this.doanhTrai.getMapById(mapJoin[Util.nextInt(0, mapJoin.length - 1)]);
            ChangeMapService.gI().changeMap(this, this.zone, 1065, this.zone.map.yPhysicInTop(1065, 0));
        } catch (Exception e) {

        }
    }

}
