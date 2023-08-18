package com.nrolove.models.boss.boss_ban_do_kho_bau;

import com.nrolove.consts.ConstRatio;
import com.nrolove.models.boss.BossData;
import com.nrolove.models.boss.BossFactory;
import com.nrolove.models.boss.boss_doanh_trai.BossDoanhTrai;
import com.nrolove.models.map.phoban.BanDoKhoBau;
import com.nrolove.models.player.Player;
import com.nrolove.services.SkillService;
import com.nrolove.services.func.ChangeMapService;
import com.nrolove.utils.Logger;
import com.nrolove.utils.SkillUtil;
import com.nrolove.utils.Util;

/**
 *
 * @author 💖 PuPuBug 💖
 *
 */
public class Usopp extends BossBanDoKhoBau {

    public Usopp(BanDoKhoBau banDoKhoBau) {
        super(BossFactory.USOPP, BossData.USOPP, banDoKhoBau);
    }

    @Override
    public void attack() {
        try {
            Player pl = getPlayerAttack();
            if (pl != null && !pl.isDie()) {
                this.playerSkill.skillSelect = this.getSkillAttack();
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect() * 2) {
                    if (Util.isTrue(15, ConstRatio.PER100)) {
                        if (SkillUtil.isUseSkillChuong(this)) {
                            goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 80)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50), false);
                        } else {
                            goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 30)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50), false);
                        }
                    }
                    SkillService.gI().useSkill(this, pl, null);
                    checkPlayerDie(pl);
                } else {
                    goToPlayer(pl, false);
                }
            }
        } catch (Exception ex) {
            Logger.logException(Usopp.class, ex);
        }
    }

    @Override
    public void initTalk() {
        this.textTalkMidle = new String[]{
            "Tuyệt kỹ, ngôi sao xanh.."
        };
    }

    @Override
    public void joinMap() {
        try {
            this.zone = this.banDoKhoBau.getMapById(mapJoin[Util.nextInt(0, mapJoin.length - 1)]);
            ChangeMapService.gI().changeMap(this, this.zone, 1227, 552);
        } catch (Exception e) {

        }
    }

    @Override
    public void leaveMap() {
        for(BossBanDoKhoBau boss : this.banDoKhoBau.bosses){
            if(boss.id == BossFactory.FRANKY){
                boss.changeToAttack();
                break;
            }
        }
        super.leaveMap(); 
    }

}
