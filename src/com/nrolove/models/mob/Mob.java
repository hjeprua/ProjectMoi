package com.nrolove.models.mob;

import com.nrolove.consts.ConstMap;
import java.util.LinkedList;
import java.util.List;
import com.nrolove.models.map.Zone;
import com.nrolove.models.player.Location;
import com.nrolove.models.player.Player;
import com.nrolove.server.io.Message;
import com.nrolove.services.MapService;
import com.nrolove.services.Service;
import com.nrolove.utils.Util;
import com.nrolove.services.MobService;
import com.nrolove.services.TaskService;

public class Mob {

    public int id;
    public Zone zone;
    public int tempId;
    public String name;
    public byte level;

    public MobPoint point;
    public MobEffectSkill effectSkill;
    public Location location;

    public byte pDame;
    public int pTiemNang;
    private long maxTiemNang;

    public long lastTimeDie;
    public int sieuquai = 0;

    public boolean actived;

    public Mob(Mob mob) {
        this.point = new MobPoint(this);
        this.effectSkill = new MobEffectSkill(this);
        this.location = new Location();
        this.id = mob.id;
        this.tempId = mob.tempId;
        this.level = mob.level;
        this.point.setHpFull(mob.point.getHpFull());
        this.point.sethp(this.point.getHpFull());
        this.location.x = mob.location.x;
        this.location.y = mob.location.y;
        this.pDame = mob.pDame;
        this.pTiemNang = mob.pTiemNang;
        this.setTiemNang();
        this.status = 5;
    }

    public Mob() {
        this.point = new MobPoint(this);
        this.effectSkill = new MobEffectSkill(this);
        this.location = new Location();
    }

    public void setTiemNang() {
        this.maxTiemNang = (long) this.point.getHpFull() * (this.pTiemNang + Util.nextInt(-2, 2)) / 100;
    }

    public byte status;

//    private List<Player> playerAttack = new LinkedList<>();
    private long lastTimeAttackPlayer;

    public boolean isDie() {
        return this.point.gethp() <= 0;
    }

    public synchronized void injured(Player plAtt, int damage, boolean dieWhenHpFull) {
        if (!this.isDie()) {
            this.addPlayerAttack(plAtt);

            if (damage >= this.point.hp) {
                damage = this.point.hp;
            }
            if (!dieWhenHpFull) {
                if (this.point.hp == this.point.maxHp && damage >= this.point.hp) {
                    damage = this.point.hp - 1;
                }
                if (this.tempId == 0 && damage > 10) {
                    damage = 10;
                }
            }
            this.point.hp -= damage;
            if (this.isDie()) {
                MobService.gI().sendMobDieAffterAttacked(this, plAtt, damage);
                MobService.gI().dropItemTask(plAtt, this);
                TaskService.gI().checkDoneTaskKillMob(plAtt, this);
                TaskService.gI().checkDoneSideTaskKillMob(plAtt, this);
                setDie();
            } else {
                MobService.gI().sendMobStillAliveAffterAttacked(this, damage, plAtt != null ? plAtt.nPoint.isCrit : false);
            }
            if (plAtt != null) {
                Service.getInstance().addSMTN(plAtt, (byte) 2, getTiemNangForPlayer(plAtt, damage), true);
            }

//            if (this.isDie() && plAtt != null) {
//                if (!plAtt.isPet) {
//                    if (plAtt.charms.tdThuHut > System.currentTimeMillis()) {
//                        for (int i = this.zone.items.size() - 1; i >= 0; i--) {
//                            ItemMap itemMap = this.zone.items.get(i);
//                            if (itemMap.playerId == plAtt.id) {
//                                ItemMapService.gI().pickItem(plAtt, itemMap.itemMapId);
//                            }
//                        }
//                    }
//                } else {
//                    if (((Pet) plAtt).master.charms.tdThuHut > System.currentTimeMillis()) {
//                        for (int i = this.zone.items.size() - 1; i >= 0; i--) {
//                            ItemMap itemMap = this.zone.items.get(i);
//                            if (itemMap.playerId == ((Pet) plAtt).master.id) {
//                                ItemMapService.gI().pickItem(((Pet) plAtt).master, itemMap.itemMapId);
//                            }
//                        }
//                    }
//                }
//            }
        }
    }

    public long getTiemNangForPlayer(Player pl, long dame) {
        int levelPlayer = Service.getInstance().getCurrLevel(pl);
        int n = levelPlayer - this.level;
        long pDameHit = dame * 100 / point.getHpFull();
        long tiemNang = pDameHit * maxTiemNang / 100;
        if (tiemNang <= 0) {
            tiemNang = 1;
        }
        if (n >= 0) {
            for (int i = 0; i < n; i++) {
                long sub = tiemNang * 10 / 100;
                if (sub <= 0) {
                    sub = 1;
                }
                tiemNang -= sub;
            }
        } else {
            for (int i = 0; i < -n; i++) {
                long add = tiemNang * 10 / 100;
                if (add <= 0) {
                    add = 1;
                }
                tiemNang += add;
            }
        }
        if (tiemNang <= 0) {
            tiemNang = 1;
        }
        tiemNang = (int) pl.nPoint.calSucManhTiemNang(tiemNang);
        return tiemNang;
    }

    public void update() {
        if (this.tempId == 71) {
            try {
                Message msg = new Message(102);
                msg.writer().writeByte(5);
                msg.writer().writeShort(this.zone.getPlayers().get(0).location.x);
                Service.gI().sendMessAllPlayerInMap(zone, msg);
                msg.cleanup();
            } catch (Exception e) {
            }
        }

        if (this.isDie()) {
            if ((zone.map.type == ConstMap.MAP_NORMAL
                    || zone.map.type == ConstMap.MAP_OFFLINE
                    || zone.map.type == ConstMap.MAP_BLACK_BALL_WAR) && Util.canDoWithTime(lastTimeDie, 2000)) {
                MobService.gI().hoiSinhMob(this);
            } else if (this.zone.map.type == ConstMap.MAP_DOANH_TRAI && Util.canDoWithTime(lastTimeDie, 10000)) {
                MobService.gI().hoiSinhMobDoanhTrai(this);
            }
        }
        effectSkill.update();
        attackPlayer();
    }

    private void attackPlayer() {
        if (!isDie() && !effectSkill.isHaveEffectSkill() && !(tempId == 0) && Util.canDoWithTime(lastTimeAttackPlayer, 2000)) {
            Player pl = getPlayerCanAttack();
            if (pl != null) {
                MobService.gI().mobAttackPlayer(this, pl);
//                this.mobAttackPlayer(pl);
            }
            this.lastTimeAttackPlayer = System.currentTimeMillis();
        }
    }

    public Player getPlayerCanAttack() {
        int distance = 150;
        Player plAttack = null;
        try {
            List<Player> players = this.zone.getNotBosses();
            for (Player pl : players) {
                if (!pl.isDie() && !pl.isBoss && !pl.isNewMiniPet && !pl.effectSkin.isVoHinh) {
                    int dis = Util.getDistance(pl, this);
                    if (dis <= distance) {
                        plAttack = pl;
                        distance = dis;
                    }
                }
            }
        } catch (Exception e) {

        }
        return plAttack;
    }

    private void addPlayerAttack(Player pl) {
    }

    public void setDie() {
        this.lastTimeDie = System.currentTimeMillis();
    }
}
