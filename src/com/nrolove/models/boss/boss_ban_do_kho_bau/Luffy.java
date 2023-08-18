package com.nrolove.models.boss.boss_ban_do_kho_bau;

import com.nrolove.models.boss.BossData;
import com.nrolove.models.boss.BossFactory;
import com.nrolove.models.map.phoban.BanDoKhoBau;
import com.nrolove.models.mob.Mob;
import com.nrolove.services.func.ChangeMapService;
import com.nrolove.utils.Util;

/**
 *
 * @author 💖 PuPuBug 💖
 *
 */
public class Luffy extends BossBanDoKhoBau {

    public Luffy(BanDoKhoBau banDoKhoBau) {
        super(BossFactory.LUFFY, BossData.LUFFY, banDoKhoBau);
    }

    @Override
    public void initTalk() {
        this.textTalkMidle = new String[]{
            "Gomu gomu no... pistal",
            "Gomu gomu no... ",
            "Gomu Gomu no Gatling",
            "Gomu Gomu no Bazooka",
            "Ta sẽ trở thành vua hải tặc",
            "Chỉ cần tay chân ta còn cử động được thì ta vô địch!",
            "Bạn bè của ta… dù ta chết… cũng đừng hòng cướp đi bất cứ người nào!!!",
            "Thế giới này chỉ cần có một vua hải tặc thôi!"
        };
    }

    @Override
    public void idle() {
    }

    @Override
    public void joinMap() {
        try {
            this.zone = this.banDoKhoBau.getMapById(mapJoin[Util.nextInt(0, mapJoin.length - 1)]);
            ChangeMapService.gI().changeMap(this, this.zone, 165, 456);
        } catch (Exception e) {

        }
    }

}
