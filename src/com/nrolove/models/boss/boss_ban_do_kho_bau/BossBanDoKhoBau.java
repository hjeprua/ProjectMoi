package com.nrolove.models.boss.boss_ban_do_kho_bau;

import com.nrolove.models.boss.Boss;
import com.nrolove.models.boss.BossData;
import com.nrolove.models.boss.BossManager;
import com.nrolove.models.item.Item;
import com.nrolove.models.map.ItemMap;
import com.nrolove.models.map.phoban.BanDoKhoBau;
import com.nrolove.models.mob.Mob;
import com.nrolove.models.player.Player;
import com.nrolove.services.Service;
import com.nrolove.services.func.ChangeMapService;
import com.nrolove.utils.Util;

/**
 *
 * @author 💖 PuPuBug 💖
 *
 */
public abstract class BossBanDoKhoBau extends Boss {

    protected BanDoKhoBau banDoKhoBau;

    public BossBanDoKhoBau(byte id, BossData data, BanDoKhoBau banDoKhoBau) {
        super(id, data);
        this.banDoKhoBau = banDoKhoBau;
        this.spawn(banDoKhoBau.level);
    }

    private void spawn(byte level) {
        this.nPoint.hpg = level * this.data.hp[0][0];
        switch (this.data.typeDame) {
            case DAME_PERCENT_HP_THOU:
                this.nPoint.dameg = this.nPoint.hpg / 1000 * this.data.dame;
                break;
            case DAME_PERCENT_HP_HUND:
                this.nPoint.dameg = this.nPoint.hpg / 100 * this.data.dame;
                break;
        }
        this.nPoint.calPoint();
        this.nPoint.setFullHpMp();
    }

    @Override
    public void attack() {
        super.attack();
    }

    @Override
    public void idle() {
        boolean allMobDie = true;
        for (Mob mob : this.zone.mobs) {
            if (!mob.isDie()) {
                allMobDie = false;
                break;
            }
        }
        if (allMobDie) {
            this.changeToAttack();
        }
    }

    @Override
    public void checkPlayerDie(Player pl) {
        if (pl.isDie()) {
            Service.getInstance().chat(this, "Chừa chưa ranh con, nên nhớ ta là " + this.name);
        }
    }

    @Override
    public void initTalk() {

    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }

    @Override
    public void rewards(Player pl) {
        ItemMap itemMap = null;
        if (this.banDoKhoBau.level < 50) {
            if (Util.isTrue(1, 30)) {
                itemMap = new ItemMap(this.zone, 2006, 1, this.location.x,
                        this.zone.map.yPhysicInTop(this.location.x, 100), -1);
            }
        } else if (this.banDoKhoBau.level < 100) {
            if (Util.isTrue(1, 30)) {
                itemMap = new ItemMap(this.zone, 2007, 1, this.location.x,
                        this.zone.map.yPhysicInTop(this.location.x, 100), -1);
            }
        } else {
            if (Util.isTrue(1, 30)) {
                itemMap = new ItemMap(this.zone, 2008, 1, this.location.x,
                        this.zone.map.yPhysicInTop(this.location.x, 100), -1);
            }
        }
        if (itemMap != null) {
            // không thể giao dịch
            itemMap.options.add(new Item.ItemOption(30, 0));
            Service.getInstance().dropItemMap(this.zone, itemMap);
        }
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    protected void notifyPlayeKill(Player player) {
    }

    @Override
    public void update() {
        super.update(); // To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void joinMap() {
        try {
            this.zone = this.banDoKhoBau.getMapById(mapJoin[Util.nextInt(0, mapJoin.length - 1)]);
            ChangeMapService.gI().changeMap(this, this.zone, 1065, this.zone.map.yPhysicInTop(1065, 0));
        } catch (Exception e) {

        }
    }

}
