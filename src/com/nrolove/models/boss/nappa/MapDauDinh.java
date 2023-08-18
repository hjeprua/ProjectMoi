package com.nrolove.models.boss.nappa;

import com.nrolove.models.boss.Boss;
import com.nrolove.models.boss.BossData;
import com.nrolove.models.boss.BossFactory;
import com.nrolove.models.boss.BossManager;
import com.nrolove.models.map.ItemMap;
import com.nrolove.models.player.Player;
import com.nrolove.services.RewardService;
import com.nrolove.services.Service;
import com.nrolove.services.TaskService;
import com.nrolove.utils.Util;

/**
 *
 * @author 💖 PuPuBug 💖
 *
 */
public class MapDauDinh extends Boss {

    public MapDauDinh() {
        super(BossFactory.MAP_DAU_DINH, BossData.MAP_DAU_DINH);
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
        if (Util.isTrue(1, 10)) {
            int[] tempId = new int[]{138, 142, 146, 150, 154, 158, 162, 166, 170, 174, 178, 182, 186};
            ItemMap itemMap = new ItemMap(this.zone, tempId[Util.nextInt(0, tempId.length - 1)],
                    1, pl.location.x, this.zone.map.yPhysicInTop(pl.location.x, pl.location.y - 24), pl.id);
            RewardService.gI().initBaseOptionClothes(itemMap.itemTemplate.id, itemMap.itemTemplate.type, itemMap.options);
            RewardService.gI().initStarOption(itemMap, new RewardService.RatioStar[]{
                new RewardService.RatioStar((byte) 1, 1, 2),
                new RewardService.RatioStar((byte) 2, 1, 3),
                new RewardService.RatioStar((byte) 3, 1, 4),
                new RewardService.RatioStar((byte) 4, 1, 5),
                new RewardService.RatioStar((byte) 5, 1, 50),
                new RewardService.RatioStar((byte) 6, 1, 100)
            });
            Service.getInstance().dropItemMap(this.zone, itemMap);
        }
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
    public void leaveMap() {
        BossFactory.createBoss(BossFactory.MAP_DAU_DINH);
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }

}
