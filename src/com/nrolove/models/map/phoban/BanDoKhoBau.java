package com.nrolove.models.map.phoban;

import com.nrolove.models.boss.boss_ban_do_kho_bau.BossBanDoKhoBau;
import com.nrolove.models.boss.boss_ban_do_kho_bau.Brook;
import com.nrolove.models.boss.boss_ban_do_kho_bau.Chopper;
import com.nrolove.models.boss.boss_ban_do_kho_bau.Franky;
import com.nrolove.models.boss.boss_ban_do_kho_bau.Luffy;
import com.nrolove.models.boss.boss_ban_do_kho_bau.Nami;
import com.nrolove.models.boss.boss_ban_do_kho_bau.Robin;
import com.nrolove.models.boss.boss_ban_do_kho_bau.Sanji;
import com.nrolove.models.boss.boss_ban_do_kho_bau.Usopp;
import com.nrolove.models.boss.boss_ban_do_kho_bau.Zoro;
import com.nrolove.models.clan.Clan;
import com.nrolove.models.map.TrapMap;
import com.nrolove.models.map.Zone;
import com.nrolove.models.mob.Mob;
import com.nrolove.models.player.Player;
import com.nrolove.services.ItemTimeService;
import com.nrolove.services.MobService;
import com.nrolove.services.Service;
import com.nrolove.services.func.ChangeMapService;
import com.nrolove.utils.Util;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 *
 */
public class BanDoKhoBau {

    public static final long POWER_CAN_GO_TO_DBKB = 2000000000;

    public static final List<BanDoKhoBau> BAN_DO_KHO_BAUS;
    public static final int MAX_AVAILABLE = 50;
    public static final int TIME_BAN_DO_KHO_BAU = 1800000;

    static {
        BAN_DO_KHO_BAUS = new ArrayList<>();
        for (int i = 0; i < MAX_AVAILABLE; i++) {
            BAN_DO_KHO_BAUS.add(new BanDoKhoBau(i));
        }
    }

    public int id;
    public byte level;
    public final List<Zone> zones;
    public final List<BossBanDoKhoBau> bosses;

    public Clan clan;
    public boolean isOpened;
    private long lastTimeOpen;

    public BanDoKhoBau(int id) {
        this.id = id;
        this.zones = new ArrayList<>();
        this.bosses = new ArrayList<>();
    }

    public void update() {
        if (this.isOpened) {
            if (Util.canDoWithTime(lastTimeOpen, TIME_BAN_DO_KHO_BAU)) {
                this.finish();
            }
        }
    }

    public void openBanDoKhoBau(Player plOpen, Clan clan, byte level) {
        if(false){
            Service.getInstance().sendThongBao(plOpen, "Chuc nang tam dong");
            return;
        }
        this.level = level;
        this.lastTimeOpen = System.currentTimeMillis();
        this.isOpened = true;
        this.clan = clan;
        this.clan.timeOpenBanDoKhoBau = this.lastTimeOpen;
        this.clan.playerOpenBanDoKhoBau = plOpen;
        this.clan.banDoKhoBau = this;
        resetBanDo();

        ChangeMapService.gI().goToDBKB(plOpen);

        sendTextBanDoKhoBau();
    }

    private void resetBanDo() {
        for (Zone zone : zones) {
            for (TrapMap trap : zone.trapMaps) {
                trap.dame = this.level * 10000;
            }
        }
        for (Zone zone : zones) {
            for (Mob m : zone.mobs) {
                MobService.gI().initMobBanDoKhoBau(m, this.level);
                MobService.gI().hoiSinhMob(m);
            }
        }
        for (BossBanDoKhoBau boss : bosses) {
            boss.leaveMap();
        }
        this.bosses.clear();
        initBoss();
    }

    private void initBoss() {
        this.bosses.add(new Luffy(this));
        this.bosses.add(new Zoro(this));
        this.bosses.add(new Sanji(this));
        this.bosses.add(new Usopp(this));
        this.bosses.add(new Franky(this));
        this.bosses.add(new Brook(this));
        this.bosses.add(new Nami(this));
        this.bosses.add(new Chopper(this));
        this.bosses.add(new Robin(this));
    }

    //kết thúc bản đồ kho báu
    private void finish() {
        List<Player> plOutDT = new ArrayList();
        for (Zone zone : zones) {
            List<Player> players = zone.getPlayers();
            for (Player pl : players) {
                plOutDT.add(pl);
            }

        }
        for (Player pl : plOutDT) {
            ChangeMapService.gI().changeMapBySpaceShip(pl, 5, -1, 64);
        }
        this.clan.banDoKhoBau = null;
        this.clan = null;
        this.isOpened = false;
    }

    public Zone getMapById(int mapId) {
        for (Zone zone : zones) {
            if (zone.map.mapId == mapId) {
                return zone;
            }
        }
        return null;
    }

    public static void addZone(int idBanDo, Zone zone) {
        BAN_DO_KHO_BAUS.get(idBanDo).zones.add(zone);
    }

    private void sendTextBanDoKhoBau() {
        for (Player pl : this.clan.membersInGame) {
            ItemTimeService.gI().sendTextBanDoKhoBau(pl);
        }
    }
}
