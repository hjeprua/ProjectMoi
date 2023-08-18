package com.nrolove.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.nrolove.jdbc.DBService;
import com.nrolove.models.item.Item;
import com.nrolove.models.player.Player;
import com.nrolove.server.Client;
import com.nrolove.utils.Util;

/**
 *
 * @Stole By Royal
 *
 */
public class GiftService {

    String code;
    int Luot;
    public HashMap<Integer, Integer> Item = new HashMap<>();
    public final ArrayList<GiftService> listGiftCode = new ArrayList<>();
    public ArrayList<Item.ItemOption> option = new ArrayList<>();

    private static GiftService instance;

    public static GiftService gI() {
        if (instance == null) {
            instance = new GiftService();
        }
        return instance;
    }

    public void init() {
        listGiftCode.clear();
        try (Connection con = DBService.gI().getConnection();) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Gift_Code WHERE Luot >= 1");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                GiftService giftcode = new GiftService();
                giftcode.code = rs.getString("Code");
                giftcode.Luot = rs.getInt("Luot");
                JSONArray jar = (JSONArray) JSONValue.parse(rs.getString("Item"));
                if (jar != null) {
                    for (int i = 0; i < jar.size(); ++i) {
                        JSONObject jsonObj = (JSONObject) jar.get(i);
                        giftcode.Item.put(Integer.parseInt(jsonObj.get("id").toString()),
                                Integer.parseInt(jsonObj.get("soluong").toString()));
                        jsonObj.clear();
                    }
                }
                JSONArray option = (JSONArray) JSONValue.parse(rs.getString("Option"));
                if (option != null) {
                    for (int u = 0; u < option.size(); u++) {
                        JSONObject jsonobject = (JSONObject) option.get(u);
                        giftcode.option.add(new Item.ItemOption(Integer.parseInt(jsonobject.get("id").toString()),
                                Integer.parseInt(jsonobject.get("param").toString())));
                        jsonobject.clear();
                    }
                }
                listGiftCode.add(giftcode);
            }
            con.close();
        } catch (Exception erorlog) {
            erorlog.printStackTrace();
        }
    }

    public GiftService checkUseGiftCode(int idPlayer, String code) throws Exception {
        Connection con = DBService.gI().getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM History_Gift_Code WHERE `player_id` = "
                    + idPlayer + " AND `code` = '" + code + "';");
            ResultSet rs = ps.executeQuery();
            if (rs != null && rs.first()) {
                Service.getInstance().sendThongBaoOK(Client.gI().getPlayer(idPlayer).getSession(),
                        "Bạn đã nhập code này vào: " + rs.getTimestamp("time"));
                return null;
            } else {
                for (GiftService giftCode : listGiftCode) {
                    if (giftCode.code.equals(code) && giftCode.Luot > 0) {
                        giftCode.Luot -= 1;
                        String sqlSET = "(" + idPlayer + ", '" + code + "', '"
                                + Util.toDateString(Date.from(Instant.now())) + "');";
                        ps.executeUpdate(
                                "INSERT INTO `History_Gift_Code` (`player_id`,`code`,`time`) VALUES " + sqlSET);
                        ps.executeUpdate("UPDATE `Gift_Code` SET `Luot` = '" + giftCode.Luot + "' WHERE `Code` = '"
                                + code + "' LIMIT 1;");
                        return giftCode;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally{
            if (con != null) {
                con.close();
                con = null;
            }
        }
        return null;
    }

    public void checkInfomationGiftCode(Player p) {
        StringBuilder sb = new StringBuilder();
        for (GiftService giftCode : listGiftCode) {
            sb.append("Code: ").append(giftCode.code).append(", Số lượng: ").append(giftCode.Luot).append("\b");
        }
        NpcService.gI().createTutorial(p, 5073, sb.toString());
    }

    public void giftCode(Player player, String code) throws Exception {
        GiftService giftcode = GiftService.gI().checkUseGiftCode((int) player.id, code);
        if (giftcode == null) {
            Service.getInstance().sendThongBao(player, "Code đã được sử dụng, hoặc không tồn tại!");
        } else {
            InventoryService.gI().addItemGiftCodeToPlayer(player, giftcode);
        }
    }
}
