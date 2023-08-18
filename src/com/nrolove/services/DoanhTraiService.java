package com.nrolove.services;

import com.nrolove.models.boss.Boss;
import com.nrolove.models.map.Map;
import com.nrolove.models.map.Zone;
import com.nrolove.models.map.phoban.DoanhTrai;
import com.nrolove.models.mob.Mob;
import com.nrolove.models.player.Player;
import java.util.List;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 *
 */
public class DoanhTraiService {

    private static DoanhTraiService i;

    private DoanhTraiService() {

    }

    public static DoanhTraiService gI() {
        if (i == null) {
            i = new DoanhTraiService();
        }
        return i;
    }

    public Zone getMapDoanhTrai(Player player, int mapId) {
        if(MapService.gI().isMapDoanhTrai(player.zone.map.mapId) && !player.isAdmin()){
            boolean canJoin = true;
            for(Mob mob : player.zone.mobs){
                if(!mob.isDie()){
                    canJoin = false;
                    break;
                }
            }
            if(canJoin){
                for(Player boss : player.zone.getBosses()){
                    if(!boss.isDie()){
                        canJoin = false;
                        break;
                    }
                }
            }
            if(!canJoin){
                return null;
            }
        }
        Zone zone = null;
        if (player.clan != null && player.clan.doanhTrai != null) {
            for (Zone z : player.clan.doanhTrai.zones) {
                if (z.map.mapId == mapId) {
                    zone = z;
                    break;
                }
            }
        }
        return zone;
    }

    public void openDoanhTrai(Player player) {
        if (player.clan != null && player.clan.doanhTrai == null 
                && !player.clan.haveGoneDoanhTrai) {
            DoanhTrai doanhTrai = null;
            for (DoanhTrai dt : DoanhTrai.DOANH_TRAIS) {
                if (!dt.isOpened) {
                    doanhTrai = dt;
                    break;
                }
            }
            if (doanhTrai != null) {
                doanhTrai.openDoanhTrai(player, player.clan);
            } else {
                Service.getInstance().sendThongBao(player, "Doanh trại đã đầy, vui lòng quay lại sau");
            }
        } else {
            Service.getInstance().sendThongBao(player, "Không thể thực hiện");
        }
    }

}
