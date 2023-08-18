package com.nrolove.services;

import com.nrolove.jdbc.daos.AccountDAO;
import com.nrolove.jdbc.daos.PlayerDAO;
import com.nrolove.models.player.Player;
import com.nrolove.server.Client;
import com.nrolove.server.io.Message;
import com.nrolove.server.io.Session;
import com.nrolove.services.func.ChangeMapService;
import com.nrolove.utils.Logger;
import com.nrolove.utils.Util;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 *
 */
public class PlayerService {

    private static PlayerService i;

    public PlayerService() {
    }

    public static PlayerService gI() {
        if (i == null) {
            i = new PlayerService();
        }
        return i;
    }

    public void sendTNSM(Player player, byte type, long param) {
        if (param > 0) {
            Message msg;
            try {
                msg = new Message(-3);
                msg.writer().writeByte(type);// 0 là cộng sm, 1 cộng tn, 2 là cộng cả 2
                msg.writer().writeInt((int) param);// số tn cần cộng
                player.sendMessage(msg);
                msg.cleanup();
            } catch (Exception e) {
            }
        }
    }

    public void sendMessageAllPlayer(Message msg) {
        for (Player pl : Client.gI().getPlayers()){
            if (pl != null) {
                pl.sendMessage(msg);
            }
        }
        msg.cleanup();

    }

    public void sendMessageIgnore(Player plIgnore, Message msg) {
        for (Player pl : Client.gI().getPlayers()) {
            if (pl != null && !pl.equals(plIgnore)) {
                pl.sendMessage(msg);
            }
        }
        msg.cleanup();

    }

    public void sendInfoHp(Player player) {
        Message msg;
        try {
            msg = Service.getInstance().messageSubCommand((byte) 5);
            msg.writer().writeInt(player.nPoint.hp);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(PlayerService.class, e);
        }
    }

    public void sendInfoMp(Player player) {
        Message msg;
        try {
            msg = Service.getInstance().messageSubCommand((byte) 6);
            msg.writer().writeInt(player.nPoint.mp);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(PlayerService.class, e);
        }
    }

    public void sendInfoHpMp(Player player) {
        sendInfoHp(player);
        sendInfoMp(player);
    }

    public void hoiPhuc(Player player, int hp, int mp) {
        if (!player.isDie()) {
            player.nPoint.addHp(hp);
            player.nPoint.addMp(mp);
            Service.getInstance().Send_Info_NV(player);
            if (!player.isPet && !player.isNewMiniPet) {
                PlayerService.gI().sendInfoHpMp(player);
            }
        }
    }

    public void sendInfoHpMpMoney(Player player) {
        Message msg;
        try {
            msg = Service.getInstance().messageSubCommand((byte) 4);
            msg.writer().writeLong(player.inventory.gold);//xu
            msg.writer().writeInt(player.inventory.gem);//luong
            msg.writer().writeInt(player.nPoint.hp);//chp
            msg.writer().writeInt(player.nPoint.mp);//cmp
            msg.writer().writeInt(player.inventory.ruby);//ruby
            player.sendMessage(msg);
        } catch (Exception e) {
            Logger.logException(PlayerService.class, e);
        }
    }

    public void playerMove(Player player, int x, int y) {
        if(player.zone == null){
            return;
        }
        if (!player.isDie()) {
            if (player.effectSkill.isCharging) {
                EffectSkillService.gI().stopCharge(player);
            }
            if (player.effectSkill.useTroi) {
                EffectSkillService.gI().removeUseTroi(player);
            }
            player.location.x = x;
            player.location.y = y;
            switch (player.zone.map.mapId) {
                case 85:
                case 86:
                case 87:
                case 88:
                case 89:
                case 90:
                case 91:
                    if (x < 24 || x > player.zone.map.mapWidth - 24 || y < 0 || y > player.zone.map.mapHeight - 24) {
                        if (MapService.gI().getWaypointPlayerIn(player) == null) {
                            ChangeMapService.gI().changeMap(player, 21 + player.gender, 0, 200, 336);
                            return;
                        }
                    }
                    if (!player.isBoss && !player.isPet && !player.isNewMiniPet) {
                        int yTop = player.zone.map.yPhysicInTop(player.location.x, player.location.y);
                        if (yTop >= player.zone.map.mapHeight - 24) {
                            ChangeMapService.gI().changeMap(player, 21 + player.gender, 0, 200, 336);
                            return;
                        }
                    }
                    break;
            }
            if (player.pet != null) {
                player.pet.followMaster();
            }
            if (player.minipet != null) {
                player.minipet.followMaster();
            }
            MapService.gI().sendPlayerMove(player);
            TaskService.gI().checkDoneTaskGoToMap(player, player.zone);
        }
    }

    public void sendCurrentStamina(Player player) {
        Message msg;
        try {
            msg = new Message(-68);
            msg.writer().writeShort(player.nPoint.stamina);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(PlayerService.class, e);
        }
    }

    public void sendMaxStamina(Player player) {
        Message msg;
        try {
            msg = new Message(-69);
            msg.writer().writeShort(player.nPoint.maxStamina);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(PlayerService.class, e);
        }
    }

    public void changeAndSendTypePK(Player player, int type) {
        changeTypePK(player, type);
        sendTypePk(player);
    }

    public void changeTypePK(Player player, int type) {
        player.typePk = (byte) type;
    }

    public void sendTypePk(Player player) {
        Message msg;
        try {
            msg = Service.getInstance().messageSubCommand((byte) 35);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeByte(player.typePk);
            Service.getInstance().sendMessAllPlayerInMap(player.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void banPlayer(Player playerBaned) {
        AccountDAO.banAccount(playerBaned.getSession());
        Service.getInstance().sendThongBao(playerBaned,
                "Tài khoản của bạn đã bị khóa\nGame sẽ mất kết nối sau 5 giây...");
        playerBaned.lastTimeBan = System.currentTimeMillis();
        playerBaned.isBan = true;
    }

    private static final int COST_GOLD_HOI_SINH = 10000000;
    private static final int COST_GEM_HOI_SINH = 1;
    private static final int COST_GOLD_HOI_SINH_NRSD = 20000000;

    public void hoiSinh(Player player) {
        if (player.isDie()) {
            boolean canHs = false;
            if (MapService.gI().isMapBlackBallWar(player.zone.map.mapId)) {
                if (player.inventory.gold >= COST_GOLD_HOI_SINH) {
                    player.inventory.gold -= COST_GOLD_HOI_SINH;
                    canHs = true;
                } else {
                    Service.getInstance().sendThongBao(player, "Không đủ vàng để thực hiện, còn thiếu " + Util.numberToMoney(COST_GOLD_HOI_SINH
                            - player.inventory.gold) + " vàng");
                    return;
                }
            }
            if (!canHs) {
                if (player.inventory.gem > 1) {
                    player.inventory.gem -= 1;
                    canHs = true;
                }
            } else {
                Service.getInstance().sendThongBao(player, "Bạn không đủ ngọc xanh để hồi sinh");
            }
            if (canHs) {
                Service.getInstance().sendMoney(player);
                Service.getInstance().hsChar(player, player.nPoint.hpMax, player.nPoint.mpMax);
            }
        }
    }
    
    public void hoiSinhMaBu(Player player) {
        if (player.isDie()) {
            boolean canHs = false;
            if (MapService.gI().isMapMaBu(player.zone.map.mapId)) {
                if (player.inventory.gold >= COST_GOLD_HOI_SINH_NRSD) {
                    player.inventory.gold -= COST_GOLD_HOI_SINH_NRSD;
                    canHs = true;
                } else {
                    Service.getInstance().sendThongBao(player, "Không đủ vàng để thực hiện, còn thiếu " + Util.numberToMoney(COST_GOLD_HOI_SINH_NRSD
                            - player.inventory.gold) + " vàng");
                    return;
                }
            } else {
                if (player.inventory.gold >= COST_GOLD_HOI_SINH) {
                    player.inventory.gold -= COST_GOLD_HOI_SINH;
                    canHs = true;
                } else {
                    Service.getInstance().sendThongBao(player, "Không đủ vàng để thực hiện, còn thiếu " + Util.numberToMoney(COST_GOLD_HOI_SINH
                            - player.inventory.gold) + " vàng");
                    return;
                }
            }
            if (canHs) {
                Service.getInstance().sendMoney(player);
                Service.getInstance().hsChar(player, player.nPoint.hpMax, player.nPoint.mpMax);
            }
        }
    }


    //dao***********************************************************************
    public boolean createPlayer(Connection con, int userId, String name, int gender, int hair) {
        PlayerDAO.createNewPlayer(con, userId, name, (byte) gender, hair);
        return true;
    }

    public Player loadPlayer(Connection con, Session session) {
        return PlayerDAO.getPlayer(con, session);
    }

    public boolean savePlayer(Player player) {
        PlayerDAO.updatePlayer(player);
        return true;
    }
    //**************************************************************************

}
