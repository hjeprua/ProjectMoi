package com.nrolove.models.boss.traidat;

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
public class BULMA extends Boss {

    public BULMA() {
        super(BossFactory.BULMA, BossData.BULMA); 
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
        // Cải trang thỏ
        int[] tempIds1 = new int[]{1041};
        int[] tempIds2 = new int[]{17};

        int tempId = -1;
        if (Util.isTrue(1, 10)) {
            tempId = tempIds1[Util.nextInt(0, tempIds1.length - 1)];
        } else {
            tempId = tempIds2[Util.nextInt(0, tempIds2.length - 1)];
        }
        
        if (tempId != -1) {
            ItemMap itemMap = new ItemMap(this.zone, tempId, 1,
                    pl.location.x, this.zone.map.yPhysicInTop(pl.location.x, pl.location.y - 24), pl.id);
            if (tempId == 1041) {
                itemMap.options.add(new Item.ItemOption(77,Util.nextInt(20,40)));
                itemMap.options.add(new Item.ItemOption(103,Util.nextInt(20,40)));
                itemMap.options.add(new Item.ItemOption(50,Util.nextInt(20,40)));
                itemMap.options.add(new Item.ItemOption(117,Util.nextInt(20,30)));
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
        this.textTalkMidle = new String[]{"Oải rồi hả?", "Ê cố lên nhóc",
            "Chán", "Ta có nhầm không nhỉ"};

    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
        BossFactory.createBoss(BossFactory.BULMA).setJustRest();
    }

}
