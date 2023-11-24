package com.nrolove.models.boss.boss_doanh_trai;

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
public class TrungUyThep extends BossDoanhTrai {

    private boolean activeAttack;

    public TrungUyThep(DoanhTrai doanhTrai) {
        super(BossFactory.TRUNG_UD_THEP, BossData.TRUNG_UY_THEP, doanhTrai);
    }

    @Override
    public void attack() {
        try {
            if (activeAttack) {
                Player pl = getPlayerAttack();
                this.playerSkill.skillSelect = this.getSkillAttack();
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    SkillService.gI().useSkill(this, pl, null ,null );
                    checkPlayerDie(pl);
                } else {
                    goToPlayer(pl, false);
                }
            } else {
                List<Player> notBosses = this.zone.getNotBosses();
                for (Player pl : notBosses) {
                    if (pl.location.x >= 650 && !pl.effectSkin.isVoHinh) {
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
    public void joinMap() {
        try {
            this.zone = this.doanhTrai.getMapById(mapJoin[Util.nextInt(0, mapJoin.length - 1)]);
            ChangeMapService.gI().changeMap(this, this.zone, 900, this.zone.map.yPhysicInTop(900, 100));
        } catch (Exception e) {

        }
    }

}
