package com.nrolove.models.boss.chill;

import com.nrolove.models.boss.Boss;
import com.nrolove.models.boss.BossData;
import com.nrolove.models.boss.BossFactory;
import com.nrolove.models.boss.BossManager;
import com.nrolove.models.item.Item;
import com.nrolove.models.map.ItemMap;
import com.nrolove.models.player.Player;
import com.nrolove.services.RewardService;
import com.nrolove.services.Service;
import com.nrolove.utils.Util;

/**
 *
 * @author 💖 PuPuBug 💖
 *
 */
public class Chill extends Boss {

    public Chill() {
        super(BossFactory.CHILL, BossData.CHILL);
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
        // do than 1/20
        int[] tempIds1 = new int[]{555, 556, 563, 557, 558, 565, 559, 567, 560};
        // Nhan, gang than 1/30
        int[] tempIds2 = new int[]{562, 564, 566, 561};
        
        int[] tempIds3 = new int[]{985};
        
        
        int tempId = -1;

        if (Util.isTrue(1, 5)) {
            tempId = tempIds3[Util.nextInt(0, tempIds3.length - 1)];
        } else if (Util.isTrue(1, 20)) {
            tempId = tempIds1[Util.nextInt(0, tempIds1.length - 1)];
        } else if (Util.isTrue(1, 30)) {
            tempId = tempIds2[Util.nextInt(0, tempIds2.length - 1)];
        }
        
        if (tempId != -1) {
            ItemMap itemMap = new ItemMap(this.zone, tempId, 1,
                    pl.location.x, this.zone.map.yPhysicInTop(pl.location.x, pl.location.y - 24), pl.id);
            if (tempId == 985) {
                itemMap.options.add(new Item.ItemOption(77,40));
                itemMap.options.add(new Item.ItemOption(103,40));
                itemMap.options.add(new Item.ItemOption(50,35));
                itemMap.options.add(new Item.ItemOption(93,Util.nextInt(1,30)));
            }
            RewardService.gI().initBaseOptionClothes(itemMap.itemTemplate.id, itemMap.itemTemplate.type, itemMap.options);
            Service.getInstance().dropItemMap(this.zone, itemMap);

        }
    }


    @Override
    public void idle() {

    }

    @Override
    public void checkPlayerDie(Player pl) {

    }

    @Override
    public void initTalk() {
        textTalkMidle = new String[]{"Ta chính là đệ nhất vũ trụ cao thủ"};
        textTalkAfter = new String[]{"Ác quỷ biến hình aaa..."};
    }

    @Override
    public void leaveMap() {
        Boss chill2 = BossFactory.createBoss(BossFactory.CHILL2);
        chill2.zone = this.zone;
        chill2.changeToAttack();
        chill2.joinMap();
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }

}
