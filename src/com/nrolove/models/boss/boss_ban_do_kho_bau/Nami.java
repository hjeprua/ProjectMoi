package com.nrolove.models.boss.boss_ban_do_kho_bau;

import com.nrolove.models.boss.BossData;
import com.nrolove.models.boss.BossFactory;
import com.nrolove.models.map.phoban.BanDoKhoBau;
import com.nrolove.services.func.ChangeMapService;
import com.nrolove.utils.Util;

/**
 *
 * @author 💖 PuPuBug 💖
 *
 */
public class Nami extends BossBanDoKhoBau {

    public Nami(BanDoKhoBau banDoKhoBau) {
        super(BossFactory.NAMI, BossData.NAMI, banDoKhoBau);
    }

    @Override
    public void leaveMap() {
        for (BossBanDoKhoBau boss : this.banDoKhoBau.bosses) {
            if (boss.id == BossFactory.CHOPPER) {
                boss.changeToAttack();
                break;
            }
        }
        super.leaveMap();
    }

    @Override
    public void joinMap() {
        try {
            this.zone = this.banDoKhoBau.getMapById(mapJoin[Util.nextInt(0, mapJoin.length - 1)]);
            ChangeMapService.gI().changeMap(this, this.zone, 520, 336);
        } catch (Exception e) {

        }
    }

}
