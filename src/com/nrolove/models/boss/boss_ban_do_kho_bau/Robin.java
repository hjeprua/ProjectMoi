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
public class Robin extends BossBanDoKhoBau{

    public Robin(BanDoKhoBau banDoKhoBau) {
        super(BossFactory.ROBIN, BossData.ROBIN, banDoKhoBau);
    }

    @Override
    public void idle() {
    }

    @Override
    public void joinMap() {
        try {
            this.zone = this.banDoKhoBau.getMapById(mapJoin[Util.nextInt(0, mapJoin.length - 1)]);
            ChangeMapService.gI().changeMap(this, this.zone, 620, 336);
        } catch (Exception e) {

        }
    }

    
}
