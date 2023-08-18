package com.nrolove.models.boss.boss_ban_do_kho_bau;

import com.nrolove.models.boss.BossData;
import com.nrolove.models.boss.BossFactory;
import com.nrolove.models.map.phoban.BanDoKhoBau;
import com.nrolove.services.func.ChangeMapService;
import com.nrolove.utils.Util;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN  💖
 *
 */
public class Chopper extends BossBanDoKhoBau{

    public Chopper(BanDoKhoBau banDoKhoBau) {
        super(BossFactory.CHOPPER, BossData.CHOPPER, banDoKhoBau);
    }

    @Override
    public void idle() {
    }

    @Override
    public void joinMap() {
        try {
            this.zone = this.banDoKhoBau.getMapById(mapJoin[Util.nextInt(0, mapJoin.length - 1)]);
            ChangeMapService.gI().changeMap(this, this.zone, 570, 336);
        } catch (Exception e) {

        }
    }

    @Override
    public void leaveMap() {
        for (BossBanDoKhoBau boss : this.banDoKhoBau.bosses) {
            if (boss.id == BossFactory.ROBIN) {
                boss.changeToAttack();
                break;
            }
        }
        super.leaveMap();
    }

}
