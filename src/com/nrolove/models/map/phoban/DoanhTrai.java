package com.nrolove.models.map.phoban;

import com.nrolove.models.boss.BossFactory;
import com.nrolove.models.boss.boss_doanh_trai.BossDoanhTrai;
import com.nrolove.models.boss.boss_doanh_trai.NinjaAoTim;
import com.nrolove.models.boss.boss_doanh_trai.RobotVeSi;
import com.nrolove.models.boss.boss_doanh_trai.TrungUyThep;
import com.nrolove.models.boss.boss_doanh_trai.TrungUyTrang;
import com.nrolove.models.boss.boss_doanh_trai.TrungUyXanhLo;
import com.nrolove.models.clan.Clan;
import com.nrolove.models.map.Zone;
import com.nrolove.models.mob.Mob;
import com.nrolove.models.player.Player;
import com.nrolove.services.ClanService;
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
public class DoanhTrai {

    public static final int DATE_WAIT_FROM_JOIN_CLAN = 1;

    public static final List<DoanhTrai> DOANH_TRAIS;
    public static final int MAX_AVAILABLE = 50;
    public static final int TIME_DOANH_TRAI = 1800000;

    static {
        DOANH_TRAIS = new ArrayList<>();
        for (int i = 0; i < MAX_AVAILABLE; i++) {
            DOANH_TRAIS.add(new DoanhTrai(i));
        }
    }

    public final int id;
    public final List<Zone> zones;
    public final List<BossDoanhTrai> bosses;

    public Clan clan;
    public boolean isOpened;
    private long lastTimeOpen;

    public DoanhTrai(int id) {
        this.id = id;
        this.zones = new ArrayList<>();
        this.bosses = new ArrayList<>();
    }

    public void update() {
        if (this.isOpened) {
            if (Util.canDoWithTime(lastTimeOpen, TIME_DOANH_TRAI)) {
                this.finish();
            }
        }
    }

    public void openDoanhTrai(Player plOpen, Clan clan) {
        if(false){
            Service.getInstance().sendThongBao(plOpen, "Chuc nang tam dong");
            return;
        }
        this.lastTimeOpen = System.currentTimeMillis();
        this.isOpened = true;
        this.clan = clan;
        this.clan.timeOpenDoanhTrai = this.lastTimeOpen;
        this.clan.playerOpenDoanhTrai = plOpen;
        this.clan.doanhTrai = this;
        resetDoanhTrai();

        List<Player> plJoinDT = new ArrayList();

        List<Player> players = plOpen.zone.getPlayers();
            for (Player pl : players) {
                if (pl.clan != null && pl.clan.id == plOpen.clan.id
                        && pl.location.x >= 1285 && pl.location.x <= 1645) {
                    plJoinDT.add(pl);
                }
            
        }

        for (Player pl : plJoinDT) {
            if (pl.isAdmin() || pl.clanMember.getNumDateFromJoinTimeToToday() >= DATE_WAIT_FROM_JOIN_CLAN) {
                ChangeMapService.gI().changeMap(pl, 53, -1, 35, 432);
            }
        }
        sendTextDoanhTraiAllClan();
    }

    //kết thúc doanh trại
    private void finish() {
        List<Player> plOutDT = new ArrayList();
        for (Zone zone : zones) {
            List<Player> players = zone.getPlayers();
                for (Player pl : players) {
                    plOutDT.add(pl);
                }
            
        }
        for (Player pl : plOutDT) {
            ChangeMapService.gI().changeMapBySpaceShip(pl, pl.gender + 21, -1, -1);
        }
        this.clan.haveGoneDoanhTrai = true;
        this.clan.doanhTrai = null;
        this.clan = null;
        this.isOpened = false;
    }

    private void resetDoanhTrai() {
        for (Zone zone : zones) {
            for (Mob m : zone.mobs) {
                MobService.gI().initMobDoanhTrai(m, this.clan);
                MobService.gI().hoiSinhMob(m);
            }
        }
        for (BossDoanhTrai boss : bosses) {
            boss.leaveMap();
        }
        this.bosses.clear();
        initBoss();
    }

    public Zone getMapById(int mapId) {
        for (Zone zone : zones) {
            if (zone.map.mapId == mapId) {
                return zone;
            }
        }
        return null;
    }

    private void initBoss() {
        this.bosses.add(new TrungUyTrang(this));
        this.bosses.add(new TrungUyXanhLo(this));
        this.bosses.add(new TrungUyThep(this));
        this.bosses.add(new NinjaAoTim(this));
        this.bosses.add(new RobotVeSi(BossFactory.ROBOT_VE_SI_1, this));
        this.bosses.add(new RobotVeSi(BossFactory.ROBOT_VE_SI_2, this));
        this.bosses.add(new RobotVeSi(BossFactory.ROBOT_VE_SI_3, this));
        this.bosses.add(new RobotVeSi(BossFactory.ROBOT_VE_SI_4, this));
    }

    private void sendTextDoanhTraiAllClan() {
        for (Player pl : this.clan.membersInGame) {
            ItemTimeService.gI().sendTextDoanhTrai(pl);
        }
    }

    public static void addZone(int idDoanhTrai, Zone zone) {
        DOANH_TRAIS.get(idDoanhTrai).zones.add(zone);
    }
}
