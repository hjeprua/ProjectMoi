package com.nrolove.models.boss.bill;

import com.nrolove.models.boss.Boss;
import com.nrolove.models.boss.BossData;
import com.nrolove.models.boss.BossFactory;
import com.nrolove.models.boss.BossManager;
import com.nrolove.models.map.ItemMap;
import com.nrolove.models.player.Player;
import com.nrolove.services.RewardService;
import com.nrolove.services.Service;
import com.nrolove.utils.Util;

/**
 *
 *
 * @author 💖 PuPuBug 💖
 *
 */
public class Whis extends Boss {

    public Whis() {
        super(BossFactory.WHIS, BossData.WHIS);
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
            BossManager.gI().getBossById(BossFactory.BILL).joinMap();
            return;
        }
    }

    @Override
    public void rewards(Player pl) {

        ItemMap itemMap = null;
        int x = this.location.x;
        int y = this.zone.map.yPhysicInTop(x, this.location.y - 24);
        if (Util.isTrue(1, 40)) {
            int[] set1 = { 562, 564, 566, 561 };
            itemMap = new ItemMap(this.zone, set1[Util.nextInt(0, set1.length - 1)], 1, x, y, pl.id);
            RewardService.gI().initBaseOptionClothes(itemMap.itemTemplate.id, itemMap.itemTemplate.type,
                    itemMap.options);
            RewardService.gI().initStarOption(itemMap, new RewardService.RatioStar[] {
                    new RewardService.RatioStar((byte) 1, 1, 2),
                    new RewardService.RatioStar((byte) 2, 1, 3),
                    new RewardService.RatioStar((byte) 3, 1, 4),
                    new RewardService.RatioStar((byte) 4, 1, 5),
                    new RewardService.RatioStar((byte) 5, 1, 6),
                    new RewardService.RatioStar((byte) 6, 1, 7),
                    new RewardService.RatioStar((byte) 7, 1, 8)
            });
        } else if (Util.isTrue(1, 30)) {
            int[] set2 = { 555, 556, 563, 557, 558, 565, 559, 567, 560 };
            itemMap = new ItemMap(this.zone, set2[Util.nextInt(0, set2.length - 1)], 1, x, y, pl.id);
            RewardService.gI().initBaseOptionClothes(itemMap.itemTemplate.id, itemMap.itemTemplate.type,
                    itemMap.options);
            RewardService.gI().initStarOption(itemMap, new RewardService.RatioStar[] {
                    new RewardService.RatioStar((byte) 1, 1, 2),
                    new RewardService.RatioStar((byte) 2, 1, 3),
                    new RewardService.RatioStar((byte) 3, 1, 4),
                    new RewardService.RatioStar((byte) 4, 1, 5),
                    new RewardService.RatioStar((byte) 5, 1, 6),
                    new RewardService.RatioStar((byte) 6, 1, 7),
                    new RewardService.RatioStar((byte) 7, 1, 8)
            });

        } else if (Util.isTrue(1, 10)) {
            itemMap = new ItemMap(this.zone, 15, 1, x, y, pl.id);
        } else if (Util.isTrue(1, 5)) {
            itemMap = new ItemMap(this.zone, 16, 1, x, y, pl.id);
        }
        if (itemMap != null) {
            Service.getInstance().dropItemMap(zone, itemMap);
        }

    }

    @Override
    public void idle() {

    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
        BossManager.gI().getBossById(BossFactory.BILL).changeToAttack();
    }

    @Override
    public void checkPlayerDie(Player pl) {

    }

    @Override
    public void initTalk() {
    }

}
