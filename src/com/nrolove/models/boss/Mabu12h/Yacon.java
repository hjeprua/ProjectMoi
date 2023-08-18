/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nrolove.models.boss.Mabu12h;

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
 * @author PC
 */
public class Yacon extends Boss {

    public Yacon() {
        super(BossFactory.YA_CON, BossData.Yacon);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void rewards(Player pl) {
        // do than 1/20
        int[] tempIds1 = new int[]{555, 556, 563, 557, 558, 565, 559, 567, 560};
        // Nhan, gang than 1/30
        int[] tempIds2 = new int[]{562, 564, 566, 561};
        
        int[] tempIds3 = new int[]{16, 17};
        
        
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
            RewardService.gI().initBaseOptionClothes(itemMap.itemTemplate.id, itemMap.itemTemplate.type, itemMap.options);
            RewardService.gI().initStarOption(itemMap, new RewardService.RatioStar[]{
                new RewardService.RatioStar((byte) 1, 1, 2),
                new RewardService.RatioStar((byte) 2, 1, 3),
                new RewardService.RatioStar((byte) 3, 1, 4),
                new RewardService.RatioStar((byte) 4, 1, 5),
                new RewardService.RatioStar((byte) 5, 1, 6),
                new RewardService.RatioStar((byte) 6, 1, 7),
                new RewardService.RatioStar((byte) 7, 1, 8)
            });
            Service.getInstance().dropItemMap(this.zone, itemMap);
        }
        pl.fightMabu.changePoint((byte) 40);
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
        BossFactory.createBoss(BossFactory.YA_CON).setJustRest();
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }

}
