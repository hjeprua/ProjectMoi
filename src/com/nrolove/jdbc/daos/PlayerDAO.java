package com.nrolove.jdbc.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.nrolove.consts.ConstPlayer;
import com.nrolove.jdbc.DBService;
import com.nrolove.models.clan.Clan;
import com.nrolove.models.clan.ClanMember;
import com.nrolove.models.item.Item;
import com.nrolove.models.item.ItemTime;
import com.nrolove.models.npc.specialnpc.MabuEgg;
import com.nrolove.models.npc.specialnpc.MagicTree;
import com.nrolove.models.player.Enemy;
import com.nrolove.models.player.Friend;
import com.nrolove.models.player.Fusion;
import com.nrolove.models.player.Inventory;
import com.nrolove.models.player.Pet;
import com.nrolove.models.player.Player;
import com.nrolove.models.skill.Skill;
import com.nrolove.models.task.TaskMain;
import com.nrolove.server.Manager;
import com.nrolove.server.io.Session;
import com.nrolove.services.ClanService;
import com.nrolove.services.IntrinsicService;
import com.nrolove.services.ItemService;
import com.nrolove.services.MapService;
import com.nrolove.services.TaskService;
import com.nrolove.utils.Logger;
import com.nrolove.utils.SkillUtil;
import com.nrolove.utils.TimeUtil;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 *
 */
public class PlayerDAO {

    public static void createNewPlayer(Connection con, int userId, String name, byte gender, int hair) {
        try {
            JSONObject dataObject = new JSONObject();
            dataObject.put("gold", 2000000000);
            dataObject.put("gem", 100000);
            dataObject.put("ruby", 2000000);
            String inventory = dataObject.toJSONString();
            dataObject.clear();

            dataObject.put("map", 21 + gender);
            dataObject.put("x", 100);
            dataObject.put("y", 384);
            String location = dataObject.toJSONString();
            dataObject.clear();

            dataObject.put("power", 2000);
            dataObject.put("tiem_nang", 2000);
            dataObject.put("stamina", 1000);
            dataObject.put("max_stamina", 1000);
            dataObject.put("hpg", gender == 0 ? 200 : 100);
            dataObject.put("mpg", gender == 1 ? 200 : 100);
            dataObject.put("damg", gender == 2 ? 15 : 10);
            dataObject.put("defg", 0);
            dataObject.put("critg", 0);
            dataObject.put("nang_dong", 0);
            dataObject.put("limit_power", 0);
            dataObject.put("hp", gender == 0 ? 200 : 100);
            dataObject.put("mp", gender == 1 ? 200 : 100);
            String point = dataObject.toJSONString();
            dataObject.clear();

            dataObject.put("level", 1);
            dataObject.put("curr_pea", 5);
            dataObject.put("is_upgrade", 0);
            dataObject.put("last_time_harvest", new Date().getTime());
            dataObject.put("last_time_upgrade", new Date().getTime());
            String magicTree = dataObject.toJSONString();
            dataObject.clear();
            /**
             *
             * [
             * {"temp_id":"1","option":[[5,7],[7,3]],"create_time":"49238749283748957""},
             * {"temp_id":"1","option":[[5,7],[7,3]],"create_time":"49238749283748957""},
             * {"temp_id":"-1","option":[],"create_time":"0""}, ... ]
             */

            int idAo = gender == 0 ? 0 : gender == 1 ? 1 : 2;
            int idQuan = gender == 0 ? 6 : gender == 1 ? 7 : 8;
            int def = gender == 2 ? 3 : 2;
            int hp = gender == 0 ? 30 : 20;

            JSONArray dataArray = new JSONArray();
            JSONObject item = new JSONObject();
            JSONArray options = new JSONArray();
            JSONArray opt = new JSONArray();
            for (int i = 0; i < 11; i++) {
                if (i == 0) {
                    opt.add(47);
                    opt.add(def);
                    item.put("temp_id", idAo);
                    item.put("create_time", System.currentTimeMillis());
                    item.put("quantity", 1);
                } else if (i == 1) {
                    opt.add(6);
                    opt.add(hp);
                    item.put("temp_id", idQuan);
                    item.put("create_time", System.currentTimeMillis());
                    item.put("quantity", 1);
                } else {
                    item.put("temp_id", -1);
                    item.put("create_time", 0);
                    item.put("quantity", 1);
                }
                options.add(opt.toJSONString());
                item.put("option", options.toJSONString());
                dataArray.add(item.toJSONString());
                item.clear();
                opt.clear();
                options.clear();
            }
            String itemsBody = dataArray.toJSONString();
            dataArray.clear();

            for (int i = 0; i < 20; i++) {
                if (i == 0) {
                    opt.add(73);
                    opt.add(1);
                    item.put("temp_id", 457);
                    item.put("create_time", System.currentTimeMillis());
                    item.put("quantity", 100000);
                } else {
                    item.put("temp_id", -1);
                    item.put("create_time", 0);
                    item.put("quantity", 1);
                }
                options.add(opt.toJSONString());
                item.put("option", options.toJSONString());
                dataArray.add(item.toJSONString());
                item.clear();
                opt.clear();
                options.clear();
            }
            String itemsBag = dataArray.toJSONString();
            dataArray.clear();

            for (int i = 0; i < 20; i++) {
                if (i == 0) {
                    item.put("temp_id", 12);
                    opt.add(14);
                    opt.add(1);
                    options.add(opt.toJSONString());
                    item.put("option", options.toJSONString());
                    item.put("create_time", System.currentTimeMillis());
                } else {
                    item.put("temp_id", -1);
                    options.add(opt.toJSONString());
                    item.put("option", options.toJSONString());
                    item.put("create_time", 0);
                }
                item.put("quantity", 1);
                dataArray.add(item.toJSONString());
                item.clear();
                opt.clear();
                options.clear();
            }
            String itemsBox = dataArray.toJSONString();
            dataArray.clear();

            for (int i = 0; i < 110; i++) {
                item.put("temp_id", -1);
                options.add(opt.toJSONString());
                item.put("option", options.toJSONString());
                item.put("create_time", 0);
                item.put("quantity", 1);
                dataArray.add(item.toJSONString());
                item.clear();
                opt.clear();
                options.clear();
            }
            String itemsBoxLuckyRound = dataArray.toJSONString();
            dataArray.clear();

            String friends = dataArray.toJSONString();
            String enemies = dataArray.toJSONString();

            dataObject.put("intrinsic_id", 0);
            dataObject.put("param_1", 0);
            dataObject.put("param_2", 0);
            dataObject.put("count_open", 0);
            String intrinsic = dataObject.toJSONString();
            dataObject.clear();

            dataObject.put("time_bo_huyet", 0);
            dataObject.put("time_bo_khi", 0);
            dataObject.put("time_giap_xen", 0);
            dataObject.put("time_cuong_no", 0);
            dataObject.put("time_an_danh", 0);
            dataObject.put("time_open_power", 0);
            dataObject.put("time_may_do", 0);
            String itemTime = dataObject.toJSONString();
            dataObject.clear();

            dataObject.put("task_id", 1);
            dataObject.put("task_index", 0);
            dataObject.put("count", 0);
            String task = dataObject.toJSONString();
            dataObject.clear();

            String mabuEgg = dataObject.toJSONString();

            dataObject.put("td_tri_tue", System.currentTimeMillis());
            dataObject.put("td_manh_me", System.currentTimeMillis());
            dataObject.put("td_da_trau", System.currentTimeMillis());
            dataObject.put("td_oai_hung", System.currentTimeMillis());
            dataObject.put("td_bat_tu", System.currentTimeMillis());
            dataObject.put("td_deo_dai", System.currentTimeMillis());
            dataObject.put("td_thu_hut", System.currentTimeMillis());
            dataObject.put("td_de_tu", System.currentTimeMillis());
            dataObject.put("td_tri_tue3", System.currentTimeMillis());
            dataObject.put("td_tri_tue4", System.currentTimeMillis());
            String charms = dataObject.toJSONString();
            dataObject.clear();

          
            int[] skillsArr = gender == 0 ? new int[]{0, 1, 6, 9, 10, 20, 22, 24, 19}
                    : gender == 1 ? new int[]{2, 3, 7, 11, 12, 17, 18,26, 19}
                    : new int[]{4, 5, 8, 13, 14, 21, 23, 25, 19};
            //[{"temp_id":"4","point":0,"last_time_use":0},]

            for (int i = 0; i < skillsArr.length; i++) {
                dataObject.put("temp_id", skillsArr[i]);
                if (i == 0) {
                    dataObject.put("point", 1);
                } else {
                    dataObject.put("point", 0);
                }
                dataObject.put("last_time_use", 0);
                dataArray.add(dataObject.toJSONString());
                dataObject.clear();
            }
            String skills = dataArray.toJSONString();
            dataArray.clear();

            dataArray.add(gender == 0 ? 0 : gender == 1 ? 2 : 4);
            dataArray.add(-1);
            dataArray.add(-1);
            dataArray.add(-1);
            dataArray.add(-1);
            String skillsShortcut = dataArray.toJSONString();
            dataArray.clear();

            String petInfo = dataObject.toJSONString();
            String petPoint = dataObject.toJSONString();
            String petBody = dataArray.toJSONString();
            String petSkill = dataArray.toJSONString();

            for (int i = 1; i <= 7; i++) {
                dataObject.put("time_out_of_date_" + i + "_star", 0);
                dataObject.put("last_time_get_" + i + "_star", 0);
                dataArray.add(dataObject.toJSONString());
                dataObject.clear();
            }
            String dataBlackBall = dataArray.toString();
            dataArray.clear();

            PreparedStatement ps = con.prepareStatement("insert into player"
                    + "(account_id, name, head, gender, have_tennis_space_ship, clan_id_sv" + Manager.SERVER + ", "
                    + "data_inventory, data_location, data_point, data_magic_tree, items_body, "
                    + "items_bag, items_box, items_box_lucky_round, friends, enemies, data_intrinsic, data_item_time,"
                    + "data_task, data_mabu_egg, data_charm, skills, skills_shortcut, pet_info, pet_point, pet_body, pet_skill,"
                    + "data_black_ball, thoi_vang, data_side_task) "
                    + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            ps.setInt(1, userId);
            ps.setString(2, name);
            ps.setInt(3, hair);
            ps.setByte(4, gender);
            ps.setBoolean(5, false);
            ps.setInt(6, -1);
            ps.setString(7, inventory);
            ps.setString(8, location);
            ps.setString(9, point);
            ps.setString(10, magicTree);
            ps.setString(11, itemsBody);
            ps.setString(12, itemsBag);
            ps.setString(13, itemsBox);
            ps.setString(14, itemsBoxLuckyRound);
            ps.setString(15, friends);
            ps.setString(16, enemies);
            ps.setString(17, intrinsic);
            ps.setString(18, itemTime);
            ps.setString(19, task);
            ps.setString(20, mabuEgg);
            ps.setString(21, charms);
            ps.setString(22, skills);
            ps.setString(23, skillsShortcut);
            ps.setString(24, petInfo);
            ps.setString(25, petPoint);
            ps.setString(26, petBody);
            ps.setString(27, petSkill);
            ps.setString(28, dataBlackBall);
            ps.setInt(29, 10); //gold bar
            ps.setString(30, "{}");
            ps.executeUpdate();
            ps.close();

            Logger.success("Tạo player mới thành công!");
        } catch (Exception e) {
            Logger.logException(PlayerDAO.class, e, "Lỗi tạo player mới");
        }
    }

    public static List<Player> getPlayers(String query) {
        List<Player> list = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Connection con = DBService.gI().getConnection();) {
            int plHp = 200000000;
            int plMp = 200000000;
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                JSONValue jv = new JSONValue();
                JSONArray dataArray = null;
                JSONObject dataObject = null;

                Player player = new Player();

                //base info
                player.id = rs.getInt("id");
                player.name = rs.getString("name");
                player.head = rs.getShort("head");
                player.gender = rs.getByte("gender");
                player.haveTennisSpaceShip = rs.getBoolean("have_tennis_space_ship");

                int clanId = rs.getInt("clan_id_sv" + Manager.SERVER);
                if (clanId != -1) {
                    Clan clan = ClanService.gI().getClanById(clanId);
                    for (ClanMember cm : clan.getMembers()) {
                        if (cm.id == player.id) {
                            clan.addMemberOnline(player);
                            player.clan = clan;
                            player.clanMember = cm;
                            break;
                        }
                    }
                }

                //data kim lượng
                dataObject = (JSONObject) jv.parse(rs.getString("data_inventory"));
                player.inventory.gold = Integer.parseInt(String.valueOf(dataObject.get("gold")));
                player.inventory.gem = Integer.parseInt(String.valueOf(dataObject.get("gem")));
                player.inventory.ruby = Integer.parseInt(String.valueOf(dataObject.get("ruby")));
                dataObject.clear();

                //data tọa độ
                dataObject = (JSONObject) jv.parse(rs.getString("data_location"));
                player.location.x = Integer.parseInt(String.valueOf(dataObject.get("x")));
                player.location.y = Integer.parseInt(String.valueOf(dataObject.get("y")));
                int mapId = Integer.parseInt(String.valueOf(dataObject.get("map")));
                if (MapService.gI().isMapDoanhTrai(mapId) || MapService.gI().isMapBlackBallWar(mapId)
                        || MapService.gI().isMapBanDoKhoBau(mapId)|| MapService.gI().isMapMaBu(mapId)) {
                    mapId = player.gender + 21;
                    player.location.x = 300;
                    player.location.y = 336;
                }
                player.zone = MapService.gI().getMapCanJoin(player, mapId);
                dataObject.clear();

                //data chỉ số
                dataObject = (JSONObject) jv.parse(rs.getString("data_point"));
                player.nPoint.power = Long.parseLong(String.valueOf(dataObject.get("power")));
                player.nPoint.tiemNang = Long.parseLong(String.valueOf(dataObject.get("tiem_nang")));
                player.nPoint.stamina = Short.parseShort(String.valueOf(dataObject.get("stamina")));
                player.nPoint.maxStamina = Short.parseShort(String.valueOf(dataObject.get("max_stamina")));
                player.nPoint.limitPower = Byte.parseByte(String.valueOf(dataObject.get("limit_power")));
                player.nPoint.hpg = Integer.parseInt(String.valueOf(dataObject.get("hpg")));
                player.nPoint.mpg = Integer.parseInt(String.valueOf(dataObject.get("mpg")));
                player.nPoint.dameg = Integer.parseInt(String.valueOf(dataObject.get("damg")));
                player.nPoint.defg = Integer.parseInt(String.valueOf(dataObject.get("defg")));
                player.nPoint.critg = Byte.parseByte(String.valueOf(dataObject.get("critg")));
                plHp = Integer.parseInt(String.valueOf(dataObject.get("hp")));
                plMp = Integer.parseInt(String.valueOf(dataObject.get("mp")));
                dataObject.clear();

                //data đậu thần
                dataObject = (JSONObject) jv.parse(rs.getString("data_magic_tree"));
                byte level = Byte.parseByte(String.valueOf(dataObject.get("level")));
                byte currPea = Byte.parseByte(String.valueOf(dataObject.get("curr_pea")));
                boolean isUpgrade = Byte.parseByte(String.valueOf(dataObject.get("is_upgrade"))) == 1;
                long lastTimeHarvest = Long.parseLong(String.valueOf(dataObject.get("last_time_harvest")));
                long lastTimeUpgrade = Long.parseLong(String.valueOf(dataObject.get("last_time_upgrade")));
                player.magicTree = new MagicTree(player, level, currPea, lastTimeHarvest, isUpgrade, lastTimeUpgrade);
                dataObject.clear();

                //data phần thưởng sao đen
                dataArray = (JSONArray) jv.parse(rs.getString("data_black_ball"));
                for (int i = 0; i < dataArray.size(); i++) {
                    dataObject = (JSONObject) jv.parse(String.valueOf(dataArray.get(i)));
                    player.rewardBlackBall.timeOutOfDateReward[i] = Long.parseLong(String.valueOf(dataObject.get("time_out_of_date_" + (i + 1) + "_star")));
                    player.rewardBlackBall.lastTimeGetReward[i] = Long.parseLong(String.valueOf(dataObject.get("last_time_get_" + (i + 1) + "_star")));
                    dataObject.clear();
                }
                dataArray.clear();

                //data body
                dataArray = (JSONArray) jv.parse(rs.getString("items_body"));
                for (int i = 0; i < dataArray.size(); i++) {
                    Item item = null;
                    dataObject = (JSONObject) jv.parse(dataArray.get(i).toString());
                    short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                        JSONArray options = (JSONArray) jv.parse(String.valueOf(dataObject.get("option")).replaceAll("\"", ""));
                        for (int j = 0; j < options.size(); j++) {
                            JSONArray opt = (JSONArray) jv.parse(String.valueOf(options.get(j)));
                            item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                    Integer.parseInt(String.valueOf(opt.get(1)))));
                        }
                        item.createTime = Long.parseLong(String.valueOf(dataObject.get("create_time")));
                        if (ItemService.gI().isOutOfDateTime(item)) {
                            item = ItemService.gI().createItemNull();
                        }
                    } else {
                        item = ItemService.gI().createItemNull();
                    }
                    player.inventory.itemsBody.add(item);
                }
                dataArray.clear();
                dataObject.clear();

                //data bag
                dataArray = (JSONArray) jv.parse(rs.getString("items_bag"));
                for (int i = 0; i < dataArray.size(); i++) {
                    Item item = null;
                    dataObject = (JSONObject) jv.parse(dataArray.get(i).toString());
                    short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                        JSONArray options = (JSONArray) jv.parse(String.valueOf(dataObject.get("option")).replaceAll("\"", ""));
                        for (int j = 0; j < options.size(); j++) {
                            JSONArray opt = (JSONArray) jv.parse(String.valueOf(options.get(j)));
                            item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                    Integer.parseInt(String.valueOf(opt.get(1)))));
                        }
                        item.createTime = Long.parseLong(String.valueOf(dataObject.get("create_time")));
                        if (ItemService.gI().isOutOfDateTime(item)) {
                            item = ItemService.gI().createItemNull();
                        }
                    } else {
                        item = ItemService.gI().createItemNull();
                    }
                    player.inventory.itemsBag.add(item);
                }
                dataArray.clear();
                dataObject.clear();

                //data box
                dataArray = (JSONArray) jv.parse(rs.getString("items_box"));
                for (int i = 0; i < dataArray.size(); i++) {
                    Item item = null;
                    dataObject = (JSONObject) jv.parse(dataArray.get(i).toString());
                    short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                        JSONArray options = (JSONArray) jv.parse(String.valueOf(dataObject.get("option")).replaceAll("\"", ""));
                        for (int j = 0; j < options.size(); j++) {
                            JSONArray opt = (JSONArray) jv.parse(String.valueOf(options.get(j)));
                            item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                    Integer.parseInt(String.valueOf(opt.get(1)))));
                        }
                        item.createTime = Long.parseLong(String.valueOf(dataObject.get("create_time")));
                        if (ItemService.gI().isOutOfDateTime(item)) {
                            item = ItemService.gI().createItemNull();
                        }
                    } else {
                        item = ItemService.gI().createItemNull();
                    }
                    player.inventory.itemsBox.add(item);
                }
                dataArray.clear();
                dataObject.clear();

                //data box lucky round
                dataArray = (JSONArray) jv.parse(rs.getString("items_box_lucky_round"));
                for (int i = 0; i < dataArray.size(); i++) {
                    Item item = null;
                    dataObject = (JSONObject) jv.parse(dataArray.get(i).toString());
                    short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                        JSONArray options = (JSONArray) jv.parse(String.valueOf(dataObject.get("option")).replaceAll("\"", ""));
                        for (int j = 0; j < options.size(); j++) {
                            JSONArray opt = (JSONArray) jv.parse(String.valueOf(options.get(j)));
                            item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                    Integer.parseInt(String.valueOf(opt.get(1)))));
                        }
                    } else {
                        item = ItemService.gI().createItemNull();
                    }
                    player.inventory.itemsBoxCrackBall.add(item);
                }
                dataArray.clear();
                dataObject.clear();

                //data friends
                dataArray = (JSONArray) jv.parse(rs.getString("friends"));
                for (int i = 0; i < dataArray.size(); i++) {
                    dataObject = (JSONObject) jv.parse(String.valueOf(dataArray.get(i)));
                    Friend friend = new Friend();
                    friend.id = Integer.parseInt(String.valueOf(dataObject.get("id")));
                    friend.name = String.valueOf(dataObject.get("name"));
                    friend.head = Short.parseShort(String.valueOf(dataObject.get("head")));
                    friend.body = Short.parseShort(String.valueOf(dataObject.get("body")));
                    friend.leg = Short.parseShort(String.valueOf(dataObject.get("leg")));
                    friend.bag = Byte.parseByte(String.valueOf(dataObject.get("bag")));
                    friend.power = Long.parseLong(String.valueOf(dataObject.get("power")));
                    player.friends.add(friend);
                    dataObject.clear();
                }
                dataArray.clear();

                //data enemies
                dataArray = (JSONArray) jv.parse(rs.getString("enemies"));
                for (int i = 0; i < dataArray.size(); i++) {
                    dataObject = (JSONObject) jv.parse(String.valueOf(dataArray.get(i)));
                    Enemy enemy = new Enemy();
                    enemy.id = Integer.parseInt(String.valueOf(dataObject.get("id")));
                    enemy.name = String.valueOf(dataObject.get("name"));
                    enemy.head = Short.parseShort(String.valueOf(dataObject.get("head")));
                    enemy.body = Short.parseShort(String.valueOf(dataObject.get("body")));
                    enemy.leg = Short.parseShort(String.valueOf(dataObject.get("leg")));
                    enemy.bag = Byte.parseByte(String.valueOf(dataObject.get("bag")));
                    enemy.power = Long.parseLong(String.valueOf(dataObject.get("power")));
                    player.enemies.add(enemy);
                    dataObject.clear();
                }
                dataArray.clear();

                //data nội tại
                dataObject = (JSONObject) jv.parse(rs.getString("data_intrinsic"));
                byte intrinsicId = Byte.parseByte(String.valueOf(dataObject.get("intrinsic_id")));
                player.playerIntrinsic.intrinsic = IntrinsicService.gI().getIntrinsicById(intrinsicId);
                player.playerIntrinsic.intrinsic.param1 = Short.parseShort(String.valueOf(dataObject.get("param_1")));
                player.playerIntrinsic.intrinsic.param2 = Short.parseShort(String.valueOf(dataObject.get("param_2")));
                player.playerIntrinsic.countOpen = Byte.parseByte(String.valueOf(dataObject.get("count_open")));
                dataObject.clear();

                //data item time
                dataObject = (JSONObject) jv.parse(rs.getString("data_item_time"));
                int timeBoHuyet = Integer.parseInt(String.valueOf(dataObject.get("time_bo_huyet")));
                int timeBoKhi = Integer.parseInt(String.valueOf(dataObject.get("time_bo_khi")));
                int timeGiapXen = Integer.parseInt(String.valueOf(dataObject.get("time_giap_xen")));
                int timeCuongNo = Integer.parseInt(String.valueOf(dataObject.get("time_cuong_no")));
                int timeAnDanh = Integer.parseInt(String.valueOf(dataObject.get("time_an_danh")));
                int timeOpenPower = Integer.parseInt(String.valueOf(dataObject.get("time_open_power")));
                int timeMayDo = 0;
                int timeMeal = 0;
                int iconMeal = 0;
                try {
                    timeMayDo = Integer.parseInt(String.valueOf(dataObject.get("time_may_do")));
                    timeMeal = Integer.parseInt(String.valueOf(dataObject.get("time_meal")));
                    iconMeal = Integer.parseInt(String.valueOf(dataObject.get("icon_meal")));
                } catch (Exception e) {
                }
                player.itemTime.lastTimeBoHuyet = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeBoHuyet);
                player.itemTime.lastTimeBoKhi = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeBoKhi);
                player.itemTime.lastTimeGiapXen = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeGiapXen);
                player.itemTime.lastTimeCuongNo = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeCuongNo);
                player.itemTime.lastTimeAnDanh = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeAnDanh);
                player.itemTime.lastTimeOpenPower = System.currentTimeMillis() - (ItemTime.TIME_OPEN_POWER - timeOpenPower);
                player.itemTime.lastTimeUseMayDo = System.currentTimeMillis() - (ItemTime.TIME_MAY_DO - timeMayDo);
                player.itemTime.lastTimeEatMeal = System.currentTimeMillis() - (ItemTime.TIME_EAT_MEAL - timeMeal);
                player.itemTime.iconMeal = iconMeal;
                player.itemTime.isUseBoHuyet = timeBoHuyet != 0;
                player.itemTime.isUseBoKhi = timeBoKhi != 0;
                player.itemTime.isUseGiapXen = timeGiapXen != 0;
                player.itemTime.isUseCuongNo = timeCuongNo != 0;
                player.itemTime.isUseAnDanh = timeAnDanh != 0;
                player.itemTime.isOpenPower = timeOpenPower != 0;
                player.itemTime.isUseMayDo = timeMayDo != 0;
                player.itemTime.isEatMeal = timeMeal != 0;
                dataObject.clear();

                //data nhiệm vụ
                dataObject = (JSONObject) jv.parse(rs.getString("data_task"));
                TaskMain taskMain = TaskService.gI().getTaskMainById(player, Byte.parseByte(String.valueOf(dataObject.get("task_id"))));
                taskMain.index = Byte.parseByte(String.valueOf(dataObject.get("task_index")));
                taskMain.subTasks.get(taskMain.index).count = Short.parseShort(String.valueOf(dataObject.get("count")));
                player.playerTask.taskMain = taskMain;
                dataObject.clear();

                //data nhiệm vụ hàng ngày
                try {
                    dataObject = (JSONObject) jv.parse(rs.getString("data_side_task"));
                    String format = "dd-MM-yyyy";
                    long receivedTime = Long.parseLong(String.valueOf(dataObject.get("received_time")));
                    Date date = new Date(receivedTime);
                    if (TimeUtil.formatTime(date, format).equals(TimeUtil.formatTime(new Date(), format))) {
                        player.playerTask.sideTask.template = TaskService.gI().getSideTaskTemplateById(Integer.parseInt(String.valueOf(dataObject.get("task_id"))));
                        player.playerTask.sideTask.count = Integer.parseInt(String.valueOf(dataObject.get("count")));
                        player.playerTask.sideTask.maxCount = Integer.parseInt(String.valueOf(dataObject.get("max_count")));
                        player.playerTask.sideTask.leftTask = Integer.parseInt(String.valueOf(dataObject.get("left_task")));
                        player.playerTask.sideTask.level = Integer.parseInt(String.valueOf(dataObject.get("level")));
                        player.playerTask.sideTask.receivedTime = receivedTime;
                    }
                } catch (Exception e) {

                }

                //data trứng bư
                dataObject = (JSONObject) jv.parse(rs.getString("data_mabu_egg"));
                Object createTime = dataObject.get("create_time");
                if (createTime != null) {
                    player.mabuEgg = new MabuEgg(player, Long.parseLong(String.valueOf(createTime)),
                            Long.parseLong(String.valueOf(dataObject.get("time_done"))));
                }
                dataObject.clear();

                //data bùa
                dataObject = (JSONObject) jv.parse(rs.getString("data_charm"));
                player.charms.tdTriTue = Long.parseLong(String.valueOf(dataObject.get("td_tri_tue")));
                player.charms.tdManhMe = Long.parseLong(String.valueOf(dataObject.get("td_manh_me")));
                player.charms.tdDaTrau = Long.parseLong(String.valueOf(dataObject.get("td_da_trau")));
                player.charms.tdOaiHung = Long.parseLong(String.valueOf(dataObject.get("td_oai_hung")));
                player.charms.tdBatTu = Long.parseLong(String.valueOf(dataObject.get("td_bat_tu")));
                player.charms.tdDeoDai = Long.parseLong(String.valueOf(dataObject.get("td_deo_dai")));
                player.charms.tdThuHut = Long.parseLong(String.valueOf(dataObject.get("td_thu_hut")));
                player.charms.tdDeTu = Long.parseLong(String.valueOf(dataObject.get("td_de_tu")));
                player.charms.tdTriTue3 = Long.parseLong(String.valueOf(dataObject.get("td_tri_tue3")));
                player.charms.tdTriTue4 = Long.parseLong(String.valueOf(dataObject.get("td_tri_tue4")));
                dataObject.clear();

                //data skill
                dataArray = (JSONArray) jv.parse(rs.getString("skills"));
                for (int i = 0; i < dataArray.size(); i++) {
                    dataObject = (JSONObject) jv.parse(String.valueOf(dataArray.get(i)));
                    int tempId = Integer.parseInt(String.valueOf(dataObject.get("temp_id")));
                    byte point = Byte.parseByte(String.valueOf(dataObject.get("point")));
                    Skill skill = null;
                    if (point != 0) {
                        skill = SkillUtil.createSkill(tempId, point);
                    } else {
                        skill = SkillUtil.createSkillLevel0(tempId);
                    }
                    skill.lastTimeUseThisSkill = Long.parseLong(String.valueOf(dataObject.get("last_time_use")));
                    player.playerSkill.skills.add(skill);
                }
                dataObject.clear();
                dataArray.clear();

                //data skill shortcut
                dataArray = (JSONArray) jv.parse(rs.getString("skills_shortcut"));
                for (int i = 0; i < dataArray.size(); i++) {
                    player.playerSkill.skillShortCut[i] = Byte.parseByte(String.valueOf(dataArray.get(i)));
                }
                for (int i : player.playerSkill.skillShortCut) {
                    if (player.playerSkill.getSkillbyId(i) != null && player.playerSkill.getSkillbyId(i).damage > 0) {
                        player.playerSkill.skillSelect = player.playerSkill.getSkillbyId(i);
                        break;
                    }
                }
                if (player.playerSkill.skillSelect == null) {
                    player.playerSkill.skillSelect = player.playerSkill.getSkillbyId(player.gender == ConstPlayer.TRAI_DAT
                            ? Skill.DRAGON : (player.gender == ConstPlayer.NAMEC ? Skill.DEMON : Skill.GALICK));
                }
                dataArray.clear();

                //data pet
                dataObject = (JSONObject) jv.parse(rs.getString("pet_info"));
                if (!String.valueOf(dataObject).equals("{}")) {
                    Pet pet = new Pet(player);
                    pet.id = -player.id;
                    pet.gender = Byte.parseByte(String.valueOf(dataObject.get("gender")));
                    pet.isMabu = Byte.parseByte(String.valueOf(dataObject.get("is_mabu"))) == 1;
                    pet.name = String.valueOf(dataObject.get("name"));
                    player.fusion.typeFusion = Byte.parseByte(String.valueOf(dataObject.get("type_fusion")));
                    player.fusion.lastTimeFusion = System.currentTimeMillis()
                            - (Fusion.TIME_FUSION - Integer.parseInt(String.valueOf(dataObject.get("left_fusion"))));
                    pet.status = Byte.parseByte(String.valueOf(dataObject.get("status")));

                    //data chỉ số
                    dataObject = (JSONObject) jv.parse(rs.getString("pet_point"));
                    pet.nPoint.stamina = Short.parseShort(String.valueOf(dataObject.get("stamina")));
                    pet.nPoint.maxStamina = Short.parseShort(String.valueOf(dataObject.get("max_stamina")));
                    pet.nPoint.hpg = Integer.parseInt(String.valueOf(dataObject.get("hpg")));
                    pet.nPoint.mpg = Integer.parseInt(String.valueOf(dataObject.get("mpg")));
                    pet.nPoint.dameg = Integer.parseInt(String.valueOf(dataObject.get("damg")));
                    pet.nPoint.defg = Integer.parseInt(String.valueOf(dataObject.get("defg")));
                    pet.nPoint.critg = Integer.parseInt(String.valueOf(dataObject.get("critg")));
                    pet.nPoint.power = Long.parseLong(String.valueOf(dataObject.get("power")));
                    pet.nPoint.tiemNang = Long.parseLong(String.valueOf(dataObject.get("tiem_nang")));
                    pet.nPoint.limitPower = Byte.parseByte(String.valueOf(dataObject.get("limit_power")));
                    int hp = Integer.parseInt(String.valueOf(dataObject.get("hp")));
                    int mp = Integer.parseInt(String.valueOf(dataObject.get("mp")));

                    //data body
                    dataArray = (JSONArray) jv.parse(rs.getString("pet_body"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        dataObject = (JSONObject) jv.parse(String.valueOf(dataArray.get(i)));
                        Item item = null;
                        short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                        if (tempId != -1) {
                            item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                            JSONArray options = (JSONArray) jv.parse(String.valueOf(dataObject.get("option")).replaceAll("\"", ""));
                            for (int j = 0; j < options.size(); j++) {
                                JSONArray opt = (JSONArray) jv.parse(String.valueOf(options.get(j)));
                                item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                        Integer.parseInt(String.valueOf(opt.get(1)))));
                            }
                            item.createTime = Long.parseLong(String.valueOf(dataObject.get("create_time")));
                            if (ItemService.gI().isOutOfDateTime(item)) {
                                item = ItemService.gI().createItemNull();
                            }
                        } else {
                            item = ItemService.gI().createItemNull();;
                        }
                        pet.inventory.itemsBody.add(item);
                    }

                    //data skills
                    dataArray = (JSONArray) jv.parse(rs.getString("pet_skill"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        JSONArray skillTemp = (JSONArray) jv.parse(String.valueOf(dataArray.get(i)));
                        int tempId = Integer.parseInt(String.valueOf(skillTemp.get(0)));
                        byte point = Byte.parseByte(String.valueOf(skillTemp.get(1)));
                        Skill skill = null;
                        if (point != 0) {
                            skill = SkillUtil.createSkill(tempId, point);
                        } else {
                            skill = SkillUtil.createSkillLevel0(tempId);
                        }
                        switch (skill.template.id) {
                            case Skill.KAMEJOKO:
                            case Skill.MASENKO:
                            case Skill.ANTOMIC:
                                skill.coolDown = 1000;
                                break;
                        }
                        pet.playerSkill.skills.add(skill);
                    }
                    pet.nPoint.hp = hp;
                    pet.nPoint.mp = mp;
//                    pet.nPoint.calPoint();
//                    pet.nPoint.setHp(hp);
//                    pet.nPoint.setMp(mp);
                    player.pet = pet;
                }
                dataObject = (JSONObject) jv.parse(rs.getString("Dhvt"));
                player.DaiHoiVoThuat.step = Integer.parseInt(String.valueOf(dataObject.get("step")));
                player.DaiHoiVoThuat.die = Integer.parseInt(String.valueOf(dataObject.get("die")));
                player.DaiHoiVoThuat.isDrop = Boolean.parseBoolean(String.valueOf(dataObject.get("drop")));
                dataObject.clear();

                player.nPoint.hp = plHp;
                player.nPoint.mp = plMp;

//                if (player != null) {
//                    player.nPoint.calPoint();
//                    player.nPoint.setHp(plHp);
//                    player.nPoint.setMp(plMp);
//                    player.zone.addPlayer(player);
//                    PlayerService.gI().addPlayer(player);
//                    player.loaded = true;
//                }
                list.add(player);
            }
            ps.close();
        } catch (Exception e) {
            Logger.logException(PlayerDAO.class, e, "Lỗi load list player");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return list;
    }

    public static Player getPlayer(Connection con, Session session) {
        String name = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            int plHp = 200000000;
            int plMp = 200000000;
            ps = con.prepareStatement("select * from player where account_id = ? limit 1");
            ps.setInt(1, session.userId);
            rs = ps.executeQuery();
            if (rs.first()) {
                JSONValue jv = new JSONValue();
                JSONArray dataArray = null;
                JSONObject dataObject = null;

                Player player = new Player();

                //base info
                player.id = rs.getInt("id");
                player.name = rs.getString("name");
                player.head = rs.getShort("head");
                player.gender = rs.getByte("gender");
                player.haveTennisSpaceShip = rs.getBoolean("have_tennis_space_ship");
                name = player.name;

                int clanId = rs.getInt("clan_id_sv" + Manager.SERVER);
                if (clanId != -1) {
                    Clan clan = ClanService.gI().getClanById(clanId);
                    for (ClanMember cm : clan.getMembers()) {
                        if (cm.id == player.id) {
                            clan.addMemberOnline(player);
                            player.clan = clan;
                            player.clanMember = cm;
                            break;
                        }
                    }
                }

                //data kim lượng
                dataObject = (JSONObject) jv.parse(rs.getString("data_inventory"));
                player.inventory.gold = Integer.parseInt(String.valueOf(dataObject.get("gold")));
                player.inventory.gem = Integer.parseInt(String.valueOf(dataObject.get("gem")));
                player.inventory.ruby = Integer.parseInt(String.valueOf(dataObject.get("ruby")));
                dataObject.clear();

                //data tọa độ
                dataObject = (JSONObject) jv.parse(rs.getString("data_location"));
                player.location.x = Integer.parseInt(String.valueOf(dataObject.get("x")));
                player.location.y = Integer.parseInt(String.valueOf(dataObject.get("y")));
                int mapId = Integer.parseInt(String.valueOf(dataObject.get("map")));
                if (MapService.gI().isMapDoanhTrai(mapId) || MapService.gI().isMapBlackBallWar(mapId)
                        || MapService.gI().isMapBanDoKhoBau(mapId)|| MapService.gI().isMapMaBu(mapId)) {
                    mapId = player.gender + 21;
                    player.location.x = 300;
                    player.location.y = 336;
                }
                player.zone = MapService.gI().getMapCanJoin(player, mapId);
                dataObject.clear();

                //data chỉ số
                dataObject = (JSONObject) jv.parse(rs.getString("data_point"));
                player.nPoint.power = Long.parseLong(String.valueOf(dataObject.get("power")));
                player.nPoint.tiemNang = Long.parseLong(String.valueOf(dataObject.get("tiem_nang")));
                player.nPoint.stamina = Short.parseShort(String.valueOf(dataObject.get("stamina")));
                player.nPoint.maxStamina = Short.parseShort(String.valueOf(dataObject.get("max_stamina")));
                player.nPoint.limitPower = Byte.parseByte(String.valueOf(dataObject.get("limit_power")));
                player.nPoint.hpg = Integer.parseInt(String.valueOf(dataObject.get("hpg")));
                player.nPoint.mpg = Integer.parseInt(String.valueOf(dataObject.get("mpg")));
                player.nPoint.dameg = Integer.parseInt(String.valueOf(dataObject.get("damg")));
                player.nPoint.defg = Integer.parseInt(String.valueOf(dataObject.get("defg")));
                player.nPoint.critg = Byte.parseByte(String.valueOf(dataObject.get("critg")));
                plHp = Integer.parseInt(String.valueOf(dataObject.get("hp")));
                plMp = Integer.parseInt(String.valueOf(dataObject.get("mp")));
                dataObject.clear();

                //data đậu thần
                dataObject = (JSONObject) jv.parse(rs.getString("data_magic_tree"));
                byte level = Byte.parseByte(String.valueOf(dataObject.get("level")));
                byte currPea = Byte.parseByte(String.valueOf(dataObject.get("curr_pea")));
                boolean isUpgrade = Byte.parseByte(String.valueOf(dataObject.get("is_upgrade"))) == 1;
                long lastTimeHarvest = Long.parseLong(String.valueOf(dataObject.get("last_time_harvest")));
                long lastTimeUpgrade = Long.parseLong(String.valueOf(dataObject.get("last_time_upgrade")));
                player.magicTree = new MagicTree(player, level, currPea, lastTimeHarvest, isUpgrade, lastTimeUpgrade);
                dataObject.clear();

                //data phần thưởng sao đen
                dataArray = (JSONArray) jv.parse(rs.getString("data_black_ball"));
                for (int i = 0; i < dataArray.size(); i++) {
                    dataObject = (JSONObject) jv.parse(String.valueOf(dataArray.get(i)));
                    player.rewardBlackBall.timeOutOfDateReward[i] = Long.parseLong(String.valueOf(dataObject.get("time_out_of_date_" + (i + 1) + "_star")));
                    player.rewardBlackBall.lastTimeGetReward[i] = Long.parseLong(String.valueOf(dataObject.get("last_time_get_" + (i + 1) + "_star")));
                    dataObject.clear();
                }
                dataArray.clear();

                //data body
                dataArray = (JSONArray) jv.parse(rs.getString("items_body"));
                for (int i = 0; i < dataArray.size(); i++) {
                    Item item = null;
                    dataObject = (JSONObject) jv.parse(dataArray.get(i).toString());
                    short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                        JSONArray options = (JSONArray) jv.parse(String.valueOf(dataObject.get("option")).replaceAll("\"", ""));
                        for (int j = 0; j < options.size(); j++) {
                            JSONArray opt = (JSONArray) jv.parse(String.valueOf(options.get(j)));
                            item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                    Integer.parseInt(String.valueOf(opt.get(1)))));
                        }
                        item.createTime = Long.parseLong(String.valueOf(dataObject.get("create_time")));
                        if (ItemService.gI().isOutOfDateTime(item)) {
                            item = ItemService.gI().createItemNull();
                        }
                    } else {
                        item = ItemService.gI().createItemNull();
                    }
                    player.inventory.itemsBody.add(item);
                }
                dataArray.clear();
                dataObject.clear();

                //data bag
                dataArray = (JSONArray) jv.parse(rs.getString("items_bag"));
                for (int i = 0; i < dataArray.size(); i++) {
                    Item item = null;
                    dataObject = (JSONObject) jv.parse(dataArray.get(i).toString());
                    short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                        JSONArray options = (JSONArray) jv.parse(String.valueOf(dataObject.get("option")).replaceAll("\"", ""));
                        for (int j = 0; j < options.size(); j++) {
                            JSONArray opt = (JSONArray) jv.parse(String.valueOf(options.get(j)));
                            item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                    Integer.parseInt(String.valueOf(opt.get(1)))));
                        }
                        item.createTime = Long.parseLong(String.valueOf(dataObject.get("create_time")));
                        if (ItemService.gI().isOutOfDateTime(item)) {
                            item = ItemService.gI().createItemNull();
                        }
                    } else {
                        item = ItemService.gI().createItemNull();
                    }
                    player.inventory.itemsBag.add(item);
                }
                dataArray.clear();
                dataObject.clear();

                //data box
                dataArray = (JSONArray) jv.parse(rs.getString("items_box"));
                for (int i = 0; i < dataArray.size(); i++) {
                    Item item = null;
                    dataObject = (JSONObject) jv.parse(dataArray.get(i).toString());
                    short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                        JSONArray options = (JSONArray) jv.parse(String.valueOf(dataObject.get("option")).replaceAll("\"", ""));
                        for (int j = 0; j < options.size(); j++) {
                            JSONArray opt = (JSONArray) jv.parse(String.valueOf(options.get(j)));
                            item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                    Integer.parseInt(String.valueOf(opt.get(1)))));
                        }
                        item.createTime = Long.parseLong(String.valueOf(dataObject.get("create_time")));
                        if (ItemService.gI().isOutOfDateTime(item)) {
                            item = ItemService.gI().createItemNull();
                        }
                    } else {
                        item = ItemService.gI().createItemNull();
                    }
                    player.inventory.itemsBox.add(item);
                }
                dataArray.clear();
                dataObject.clear();

                //data box lucky round
                dataArray = (JSONArray) jv.parse(rs.getString("items_box_lucky_round"));
                for (int i = 0; i < dataArray.size(); i++) {
                    Item item = null;
                    dataObject = (JSONObject) jv.parse(dataArray.get(i).toString());
                    short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                        JSONArray options = (JSONArray) jv.parse(String.valueOf(dataObject.get("option")).replaceAll("\"", ""));
                        for (int j = 0; j < options.size(); j++) {
                            JSONArray opt = (JSONArray) jv.parse(String.valueOf(options.get(j)));
                            item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                    Integer.parseInt(String.valueOf(opt.get(1)))));
                        }
                    } else {
                        item = ItemService.gI().createItemNull();
                    }
                    player.inventory.itemsBoxCrackBall.add(item);
                }
                dataArray.clear();
                dataObject.clear();

                //data friends
                dataArray = (JSONArray) jv.parse(rs.getString("friends"));
                for (int i = 0; i < dataArray.size(); i++) {
                    dataObject = (JSONObject) jv.parse(String.valueOf(dataArray.get(i)));
                    Friend friend = new Friend();
                    friend.id = Integer.parseInt(String.valueOf(dataObject.get("id")));
                    friend.name = String.valueOf(dataObject.get("name"));
                    friend.head = Short.parseShort(String.valueOf(dataObject.get("head")));
                    friend.body = Short.parseShort(String.valueOf(dataObject.get("body")));
                    friend.leg = Short.parseShort(String.valueOf(dataObject.get("leg")));
                    friend.bag = Byte.parseByte(String.valueOf(dataObject.get("bag")));
                    friend.power = Long.parseLong(String.valueOf(dataObject.get("power")));
                    player.friends.add(friend);
                    dataObject.clear();
                }
                dataArray.clear();

                //data enemies
                dataArray = (JSONArray) jv.parse(rs.getString("enemies"));
                for (int i = 0; i < dataArray.size(); i++) {
                    dataObject = (JSONObject) jv.parse(String.valueOf(dataArray.get(i)));
                    Enemy enemy = new Enemy();
                    enemy.id = Integer.parseInt(String.valueOf(dataObject.get("id")));
                    enemy.name = String.valueOf(dataObject.get("name"));
                    enemy.head = Short.parseShort(String.valueOf(dataObject.get("head")));
                    enemy.body = Short.parseShort(String.valueOf(dataObject.get("body")));
                    enemy.leg = Short.parseShort(String.valueOf(dataObject.get("leg")));
                    enemy.bag = Byte.parseByte(String.valueOf(dataObject.get("bag")));
                    enemy.power = Long.parseLong(String.valueOf(dataObject.get("power")));
                    player.enemies.add(enemy);
                    dataObject.clear();
                }
                dataArray.clear();

                //data nội tại
                dataObject = (JSONObject) jv.parse(rs.getString("data_intrinsic"));
                byte intrinsicId = Byte.parseByte(String.valueOf(dataObject.get("intrinsic_id")));
                player.playerIntrinsic.intrinsic = IntrinsicService.gI().getIntrinsicById(intrinsicId);
                player.playerIntrinsic.intrinsic.param1 = Short.parseShort(String.valueOf(dataObject.get("param_1")));
                player.playerIntrinsic.intrinsic.param2 = Short.parseShort(String.valueOf(dataObject.get("param_2")));
                player.playerIntrinsic.countOpen = Byte.parseByte(String.valueOf(dataObject.get("count_open")));
                dataObject.clear();

                //data item time
                dataObject = (JSONObject) jv.parse(rs.getString("data_item_time"));
                int timeBoHuyet = Integer.parseInt(String.valueOf(dataObject.get("time_bo_huyet")));
                int timeBoKhi = Integer.parseInt(String.valueOf(dataObject.get("time_bo_khi")));
                int timeGiapXen = Integer.parseInt(String.valueOf(dataObject.get("time_giap_xen")));
                int timeCuongNo = Integer.parseInt(String.valueOf(dataObject.get("time_cuong_no")));
                int timeAnDanh = Integer.parseInt(String.valueOf(dataObject.get("time_an_danh")));
                int timeOpenPower = Integer.parseInt(String.valueOf(dataObject.get("time_open_power")));
                int timeMayDo = 0;
                int timeMeal = 0;
                int iconMeal = 0;
                try {
                    timeMayDo = Integer.parseInt(String.valueOf(dataObject.get("time_may_do")));
                    timeMeal = Integer.parseInt(String.valueOf(dataObject.get("time_meal")));
                    iconMeal = Integer.parseInt(String.valueOf(dataObject.get("icon_meal")));
                } catch (Exception e) {
                }
                player.itemTime.lastTimeBoHuyet = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeBoHuyet);
                player.itemTime.lastTimeBoKhi = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeBoKhi);
                player.itemTime.lastTimeGiapXen = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeGiapXen);
                player.itemTime.lastTimeCuongNo = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeCuongNo);
                player.itemTime.lastTimeAnDanh = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeAnDanh);
                player.itemTime.lastTimeOpenPower = System.currentTimeMillis() - (ItemTime.TIME_OPEN_POWER - timeOpenPower);
                player.itemTime.lastTimeUseMayDo = System.currentTimeMillis() - (ItemTime.TIME_MAY_DO - timeMayDo);
                player.itemTime.lastTimeEatMeal = System.currentTimeMillis() - (ItemTime.TIME_EAT_MEAL - timeMeal);
                player.itemTime.iconMeal = iconMeal;
                player.itemTime.isUseBoHuyet = timeBoHuyet != 0;
                player.itemTime.isUseBoKhi = timeBoKhi != 0;
                player.itemTime.isUseGiapXen = timeGiapXen != 0;
                player.itemTime.isUseCuongNo = timeCuongNo != 0;
                player.itemTime.isUseAnDanh = timeAnDanh != 0;
                player.itemTime.isOpenPower = timeOpenPower != 0;
                player.itemTime.isUseMayDo = timeMayDo != 0;
                player.itemTime.isEatMeal = timeMeal != 0;
                dataObject.clear();

                //data nhiệm vụ
                dataObject = (JSONObject) jv.parse(rs.getString("data_task"));
                TaskMain taskMain = TaskService.gI().getTaskMainById(player, Byte.parseByte(String.valueOf(dataObject.get("task_id"))));
                taskMain.index = Byte.parseByte(String.valueOf(dataObject.get("task_index")));
                taskMain.subTasks.get(taskMain.index).count = Short.parseShort(String.valueOf(dataObject.get("count")));
                player.playerTask.taskMain = taskMain;
                dataObject.clear();

                //data nhiệm vụ hàng ngày
                try {
                    dataObject = (JSONObject) jv.parse(rs.getString("data_side_task"));
                    String format = "dd-MM-yyyy";
                    long receivedTime = Long.parseLong(String.valueOf(dataObject.get("received_time")));
                    Date date = new Date(receivedTime);
                    if (TimeUtil.formatTime(date, format).equals(TimeUtil.formatTime(new Date(), format))) {
                        player.playerTask.sideTask.template = TaskService.gI().getSideTaskTemplateById(Integer.parseInt(String.valueOf(dataObject.get("task_id"))));
                        player.playerTask.sideTask.count = Integer.parseInt(String.valueOf(dataObject.get("count")));
                        player.playerTask.sideTask.maxCount = Integer.parseInt(String.valueOf(dataObject.get("max_count")));
                        player.playerTask.sideTask.leftTask = Integer.parseInt(String.valueOf(dataObject.get("left_task")));
                        player.playerTask.sideTask.level = Integer.parseInt(String.valueOf(dataObject.get("level")));
                        player.playerTask.sideTask.receivedTime = receivedTime;
                    }
                } catch (Exception e) {

                }

                //data trứng bư
                dataObject = (JSONObject) jv.parse(rs.getString("data_mabu_egg"));
                Object createTime = dataObject.get("create_time");
                if (createTime != null) {
                    player.mabuEgg = new MabuEgg(player, Long.parseLong(String.valueOf(createTime)),
                            Long.parseLong(String.valueOf(dataObject.get("time_done"))));
                }
                dataObject.clear();

                //data bùa
                dataObject = (JSONObject) jv.parse(rs.getString("data_charm"));
                player.charms.tdTriTue = Long.parseLong(String.valueOf(dataObject.get("td_tri_tue")));
                player.charms.tdManhMe = Long.parseLong(String.valueOf(dataObject.get("td_manh_me")));
                player.charms.tdDaTrau = Long.parseLong(String.valueOf(dataObject.get("td_da_trau")));
                player.charms.tdOaiHung = Long.parseLong(String.valueOf(dataObject.get("td_oai_hung")));
                player.charms.tdBatTu = Long.parseLong(String.valueOf(dataObject.get("td_bat_tu")));
                player.charms.tdDeoDai = Long.parseLong(String.valueOf(dataObject.get("td_deo_dai")));
                player.charms.tdThuHut = Long.parseLong(String.valueOf(dataObject.get("td_thu_hut")));
                player.charms.tdDeTu = Long.parseLong(String.valueOf(dataObject.get("td_de_tu")));
                player.charms.tdTriTue3 = Long.parseLong(String.valueOf(dataObject.get("td_tri_tue3")));
                player.charms.tdTriTue4 = Long.parseLong(String.valueOf(dataObject.get("td_tri_tue4")));
                dataObject.clear();

                //data skill
                dataArray = (JSONArray) jv.parse(rs.getString("skills"));
                for (int i = 0; i < dataArray.size(); i++) {
                    dataObject = (JSONObject) jv.parse(String.valueOf(dataArray.get(i)));
                    int tempId = Integer.parseInt(String.valueOf(dataObject.get("temp_id")));
                    byte point = Byte.parseByte(String.valueOf(dataObject.get("point")));
                    Skill skill = null;
                    if (point != 0) {
                        skill = SkillUtil.createSkill(tempId, point);
                    } else {
                        skill = SkillUtil.createSkillLevel0(tempId);
                    }
                    skill.lastTimeUseThisSkill = Long.parseLong(String.valueOf(dataObject.get("last_time_use")));
                    player.playerSkill.skills.add(skill);
                }
                dataObject.clear();
                dataArray.clear();

                //data skill shortcut
                dataArray = (JSONArray) jv.parse(rs.getString("skills_shortcut"));
                for (int i = 0; i < dataArray.size(); i++) {
                    player.playerSkill.skillShortCut[i] = Byte.parseByte(String.valueOf(dataArray.get(i)));
                }
                for (int i : player.playerSkill.skillShortCut) {
                    if (player.playerSkill.getSkillbyId(i) != null && player.playerSkill.getSkillbyId(i).damage > 0) {
                        player.playerSkill.skillSelect = player.playerSkill.getSkillbyId(i);
                        break;
                    }
                }
                if (player.playerSkill.skillSelect == null) {
                    player.playerSkill.skillSelect = player.playerSkill.getSkillbyId(player.gender == ConstPlayer.TRAI_DAT
                            ? Skill.DRAGON : (player.gender == ConstPlayer.NAMEC ? Skill.DEMON : Skill.GALICK));
                }
                dataArray.clear();

                //data pet
                dataObject = (JSONObject) jv.parse(rs.getString("pet_info"));
                if (!String.valueOf(dataObject).equals("{}")) {
                    Pet pet = new Pet(player);
                    pet.id = -player.id;
                    pet.gender = Byte.parseByte(String.valueOf(dataObject.get("gender")));
                    pet.isMabu = Byte.parseByte(String.valueOf(dataObject.get("is_mabu"))) == 1;
                    pet.name = String.valueOf(dataObject.get("name"));
                    player.fusion.typeFusion = Byte.parseByte(String.valueOf(dataObject.get("type_fusion")));
                    player.fusion.lastTimeFusion = System.currentTimeMillis()
                            - (Fusion.TIME_FUSION - Integer.parseInt(String.valueOf(dataObject.get("left_fusion"))));
                    pet.status = Byte.parseByte(String.valueOf(dataObject.get("status")));

                    //data chỉ số
                    dataObject = (JSONObject) jv.parse(rs.getString("pet_point"));
                    pet.nPoint.stamina = Short.parseShort(String.valueOf(dataObject.get("stamina")));
                    pet.nPoint.maxStamina = Short.parseShort(String.valueOf(dataObject.get("max_stamina")));
                    pet.nPoint.hpg = Integer.parseInt(String.valueOf(dataObject.get("hpg")));
                    pet.nPoint.mpg = Integer.parseInt(String.valueOf(dataObject.get("mpg")));
                    pet.nPoint.dameg = Integer.parseInt(String.valueOf(dataObject.get("damg")));
                    pet.nPoint.defg = Integer.parseInt(String.valueOf(dataObject.get("defg")));
                    pet.nPoint.critg = Integer.parseInt(String.valueOf(dataObject.get("critg")));
                    pet.nPoint.power = Long.parseLong(String.valueOf(dataObject.get("power")));
                    pet.nPoint.tiemNang = Long.parseLong(String.valueOf(dataObject.get("tiem_nang")));
                    pet.nPoint.limitPower = Byte.parseByte(String.valueOf(dataObject.get("limit_power")));
                    int hp = Integer.parseInt(String.valueOf(dataObject.get("hp")));
                    int mp = Integer.parseInt(String.valueOf(dataObject.get("mp")));

                    //data body
                    dataArray = (JSONArray) jv.parse(rs.getString("pet_body"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        dataObject = (JSONObject) jv.parse(String.valueOf(dataArray.get(i)));
                        Item item = null;
                        short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                        if (tempId != -1) {
                            item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                            JSONArray options = (JSONArray) jv.parse(String.valueOf(dataObject.get("option")).replaceAll("\"", ""));
                            for (int j = 0; j < options.size(); j++) {
                                JSONArray opt = (JSONArray) jv.parse(String.valueOf(options.get(j)));
                                item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                        Integer.parseInt(String.valueOf(opt.get(1)))));
                            }
                            item.createTime = Long.parseLong(String.valueOf(dataObject.get("create_time")));
                            if (ItemService.gI().isOutOfDateTime(item)) {
                                item = ItemService.gI().createItemNull();
                            }
                        } else {
                            item = ItemService.gI().createItemNull();;
                        }
                        pet.inventory.itemsBody.add(item);
                    }

                    //data skills
                    dataArray = (JSONArray) jv.parse(rs.getString("pet_skill"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        JSONArray skillTemp = (JSONArray) jv.parse(String.valueOf(dataArray.get(i)));
                        int tempId = Integer.parseInt(String.valueOf(skillTemp.get(0)));
                        byte point = Byte.parseByte(String.valueOf(skillTemp.get(1)));
                        Skill skill = null;
                        if (point != 0) {
                            skill = SkillUtil.createSkill(tempId, point);
                        } else {
                            skill = SkillUtil.createSkillLevel0(tempId);
                        }
                        switch (skill.template.id) {
                            case Skill.KAMEJOKO:
                            case Skill.MASENKO:
                            case Skill.ANTOMIC:
                                skill.coolDown = 1000;
                                break;
                        }
                        pet.playerSkill.skills.add(skill);
                    }
                    pet.nPoint.hp = hp;
                    pet.nPoint.mp = mp;
//                    pet.nPoint.calPoint();
//                    pet.nPoint.setHp(hp);
//                    pet.nPoint.setMp(mp);
                    player.pet = pet;
                }
                

                dataObject = (JSONObject) jv.parse(rs.getString("Dhvt"));
                player.DaiHoiVoThuat.step = Integer.parseInt(String.valueOf(dataObject.get("step")));
                player.DaiHoiVoThuat.die = Integer.parseInt(String.valueOf(dataObject.get("die")));
                player.DaiHoiVoThuat.isDrop = Boolean.parseBoolean(String.valueOf(dataObject.get("drop")));
                dataObject.clear();

                player.nPoint.hp = plHp;
                player.nPoint.mp = plMp;
                rs.close();
                ps.close();

//                if (player != null) {
//                    player.nPoint.calPoint();
//                    player.nPoint.setHp(plHp);
//                    player.nPoint.setMp(plMp);
//                    player.zone.addPlayer(player);
//                    PlayerService.gI().addPlayer(player);
//                    player.loaded = true;
//                }
                return player;
            }
        } catch (Exception e) {
            Logger.logException(PlayerDAO.class, e, "Lỗi load player: " + name);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;

    }

    public static void updatePlayer(Player player) {
        int n1s = 0;
        int n2s = 0;
        int n3s = 0;
        int tv = 0;
        PreparedStatement ps = null;
        if (player.loaded) {
            long st = System.currentTimeMillis();
            try {
                JSONArray dataArray = new JSONArray();
                JSONObject dataObject = new JSONObject();

                //data kim lượng
                dataObject.put("gold", player.inventory.gold > Inventory.LIMIT_GOLD
                        ? Inventory.LIMIT_GOLD : player.inventory.gold);
                dataObject.put("gem", player.inventory.gem);
                dataObject.put("ruby", player.inventory.ruby);
                String inventory = dataObject.toJSONString();
                dataObject.clear();

                int mapId = -1;
                mapId = player.mapIdBeforeLogout;
                int x = player.location.x;
                int y = player.location.y;
                int hp = player.nPoint.hp;
                int mp = player.nPoint.mp;
                if (player.isDie()) {
                    mapId = player.gender + 21;
                    x = 300;
                    y = 336;
                    hp = 1;
                    mp = 1;
                } else {
                    if (MapService.gI().isMapDoanhTrai(mapId) || MapService.gI().isMapBlackBallWar(mapId)
                            || MapService.gI().isMapBanDoKhoBau(mapId)|| MapService.gI().isMapMaBu(mapId)) {
                        mapId = player.gender + 21;
                        x = 300;
                        y = 336;
                    }
                }

                //data vị trí
                dataObject.put("map", mapId);
                dataObject.put("x", x);
                dataObject.put("y", y);
                String location = dataObject.toJSONString();
                dataObject.clear();

                //data chỉ số
                dataObject.put("nang_dong", 0);
                dataObject.put("stamina", player.nPoint.stamina);
                dataObject.put("max_stamina", player.nPoint.maxStamina);
                dataObject.put("power", player.nPoint.power);
                dataObject.put("tiem_nang", player.nPoint.tiemNang);
                dataObject.put("limit_power", player.nPoint.limitPower);
                dataObject.put("hpg", player.nPoint.hpg);
                dataObject.put("mpg", player.nPoint.mpg);
                dataObject.put("damg", player.nPoint.dameg);
                dataObject.put("defg", player.nPoint.defg);
                dataObject.put("critg", player.nPoint.critg);
                dataObject.put("hp", hp);
                dataObject.put("mp", mp);
                String point = dataObject.toJSONString();
                dataObject.clear();

                //data đậu thần
                dataObject.put("is_upgrade", player.magicTree.isUpgrade ? 1 : 0);
                dataObject.put("level", player.magicTree.level);
                dataObject.put("last_time_harvest", player.magicTree.lastTimeHarvest);
                dataObject.put("last_time_upgrade", player.magicTree.lastTimeUpgrade);
                dataObject.put("curr_pea", player.magicTree.currPeas);
                String magicTree = dataObject.toJSONString();
                dataObject.clear();

                //data body
                for (Item item : player.inventory.itemsBody) {
                    JSONArray opt = new JSONArray();
                    if (item.isNotNullItem()) {
                        dataObject.put("temp_id", item.template.id);
                        dataObject.put("quantity", item.quantity);
                        dataObject.put("create_time", item.createTime);
                        JSONArray options = new JSONArray();
                        for (Item.ItemOption io : item.itemOptions) {
                            opt.add(io.optionTemplate.id);
                            opt.add(io.param);
                            options.add(opt.toJSONString());
                            opt.clear();
                        }
                        dataObject.put("option", options.toJSONString());
                    } else {
                        dataObject.put("temp_id", -1);
                        dataObject.put("quantity", 0);
                        dataObject.put("create_time", 0);
                        dataObject.put("option", opt.toJSONString());
                    }
                    dataArray.add(dataObject.toJSONString());
                }
                String itemsBody = dataArray.toJSONString();
                dataObject.clear();
                dataArray.clear();

                //data bag
                for (Item item : player.inventory.itemsBag) {
                    JSONArray opt = new JSONArray();
                    if (item.isNotNullItem()) {
                        switch (item.template.id) {
                            case 14:
                                n1s += item.quantity;
                                break;
                            case 15:
                                n2s += item.quantity;
                                break;
                            case 16:
                                n3s += item.quantity;
                                break;
                            case 457:
                                tv += item.quantity;
                                break;
                        }
                        dataObject.put("temp_id", item.template.id);
                        dataObject.put("quantity", item.quantity);
                        dataObject.put("create_time", item.createTime);
                        JSONArray options = new JSONArray();
                        for (Item.ItemOption io : item.itemOptions) {
                            opt.add(io.optionTemplate.id);
                            opt.add(io.param);
                            options.add(opt.toJSONString());
                            opt.clear();
                        }
                        dataObject.put("option", options.toJSONString());
                    } else {
                        dataObject.put("temp_id", -1);
                        dataObject.put("quantity", 0);
                        dataObject.put("create_time", 0);
                        dataObject.put("option", opt.toJSONString());
                    }
                    dataArray.add(dataObject.toJSONString());
                }
                String itemsBag = dataArray.toJSONString();
                dataObject.clear();
                dataArray.clear();

                //data box
                for (Item item : player.inventory.itemsBox) {
                    JSONArray opt = new JSONArray();
                    if (item.isNotNullItem()) {
                        switch (item.template.id) {
                            case 14:
                                n1s += item.quantity;
                                break;
                            case 15:
                                n2s += item.quantity;
                                break;
                            case 16:
                                n3s += item.quantity;
                                break;
                            case 457:
                                tv += item.quantity;
                                break;
                        }
                        dataObject.put("temp_id", item.template.id);
                        dataObject.put("quantity", item.quantity);
                        dataObject.put("create_time", item.createTime);
                        JSONArray options = new JSONArray();
                        for (Item.ItemOption io : item.itemOptions) {
                            opt.add(io.optionTemplate.id);
                            opt.add(io.param);
                            options.add(opt.toJSONString());
                            opt.clear();
                        }
                        dataObject.put("option", options.toJSONString());
                    } else {
                        dataObject.put("temp_id", -1);
                        dataObject.put("quantity", 0);
                        dataObject.put("create_time", 0);
                        dataObject.put("option", opt.toJSONString());
                    }
                    dataArray.add(dataObject.toJSONString());
                }
                String itemsBox = dataArray.toJSONString();
                dataObject.clear();
                dataArray.clear();

                //data box crack ball
                for (Item item : player.inventory.itemsBoxCrackBall) {
                    JSONArray opt = new JSONArray();
                    if (item.isNotNullItem()) {
                        dataObject.put("temp_id", item.template.id);
                        dataObject.put("quantity", item.quantity);
                        dataObject.put("create_time", item.createTime);
                        JSONArray options = new JSONArray();
                        for (Item.ItemOption io : item.itemOptions) {
                            opt.add(io.optionTemplate.id);
                            opt.add(io.param);
                            options.add(opt.toJSONString());
                            opt.clear();
                        }
                        dataObject.put("option", options.toJSONString());
                    } else {
                        dataObject.put("temp_id", -1);
                        dataObject.put("quantity", 0);
                        dataObject.put("create_time", 0);
                        dataObject.put("option", opt.toJSONString());
                    }
                    dataArray.add(dataObject.toJSONString());
                }
                String itemsBoxLuckyRound = dataArray.toJSONString();
                dataObject.clear();
                dataArray.clear();

                //data bạn bè
                for (Friend f : player.friends) {
                    dataObject.put("id", f.id);
                    dataObject.put("name", f.name);
                    dataObject.put("power", f.power);
                    dataObject.put("head", f.head);
                    dataObject.put("body", f.body);
                    dataObject.put("leg", f.leg);
                    dataObject.put("bag", f.bag);
                    dataArray.add(dataObject.toJSONString());
                    dataObject.clear();
                }
                String friend = dataArray.toJSONString();
                dataArray.clear();

                //data kẻ thù
                for (Friend e : player.enemies) {
                    dataObject.put("id", e.id);
                    dataObject.put("name", e.name);
                    dataObject.put("power", e.power);
                    dataObject.put("head", e.head);
                    dataObject.put("body", e.body);
                    dataObject.put("leg", e.leg);
                    dataObject.put("bag", e.bag);
                    dataArray.add(dataObject.toJSONString());
                    dataObject.clear();
                }
                String enemy = dataArray.toJSONString();
                dataArray.clear();

                //data nội tại
                dataObject.put("intrinsic_id", player.playerIntrinsic.intrinsic.id);
                dataObject.put("param_1", player.playerIntrinsic.intrinsic.param1);
                dataObject.put("param_2", player.playerIntrinsic.intrinsic.param2);
                dataObject.put("count_open", player.playerIntrinsic.countOpen);
                String intrinsic = dataObject.toJSONString();
                dataObject.clear();

                //data item time
                dataObject.put("time_bo_huyet", (player.itemTime.isUseBoHuyet ? (ItemTime.TIME_ITEM - (System.currentTimeMillis() - player.itemTime.lastTimeBoHuyet)) : 0));
                dataObject.put("time_bo_khi", (player.itemTime.isUseBoKhi ? (ItemTime.TIME_ITEM - (System.currentTimeMillis() - player.itemTime.lastTimeBoKhi)) : 0));
                dataObject.put("time_giap_xen", (player.itemTime.isUseGiapXen ? (ItemTime.TIME_ITEM - (System.currentTimeMillis() - player.itemTime.lastTimeGiapXen)) : 0));
                dataObject.put("time_cuong_no", (player.itemTime.isUseCuongNo ? (ItemTime.TIME_ITEM - (System.currentTimeMillis() - player.itemTime.lastTimeCuongNo)) : 0));
                dataObject.put("time_an_danh", (player.itemTime.isUseAnDanh ? (ItemTime.TIME_ITEM - (System.currentTimeMillis() - player.itemTime.lastTimeAnDanh)) : 0));
                dataObject.put("time_open_power", (player.itemTime.isOpenPower ? (ItemTime.TIME_OPEN_POWER - (System.currentTimeMillis() - player.itemTime.lastTimeOpenPower)) : 0));
                dataObject.put("time_may_do", (player.itemTime.isUseMayDo ? (ItemTime.TIME_MAY_DO - (System.currentTimeMillis() - player.itemTime.lastTimeUseMayDo)) : 0));
                dataObject.put("time_meal", (player.itemTime.isEatMeal ? (ItemTime.TIME_EAT_MEAL - (System.currentTimeMillis() - player.itemTime.lastTimeEatMeal)) : 0));
                dataObject.put("icon_meal", player.itemTime.iconMeal);
                String itemTime = dataObject.toJSONString();
                dataObject.clear();

                //data nhiệm vụ
                dataObject.put("task_id", player.playerTask.taskMain.id);
                dataObject.put("task_index", player.playerTask.taskMain.index);
                dataObject.put("count", player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count);
                String task = dataObject.toJSONString();
                dataObject.clear();

                //data nhiệm vụ hàng ngày
                dataObject.put("task_id", player.playerTask.sideTask.template != null ? player.playerTask.sideTask.template.id : -1);
                dataObject.put("received_time", player.playerTask.sideTask.receivedTime);
                dataObject.put("count", player.playerTask.sideTask.count);
                dataObject.put("max_count", player.playerTask.sideTask.maxCount);
                dataObject.put("left_task", player.playerTask.sideTask.leftTask);
                dataObject.put("level", player.playerTask.sideTask.level);
                String sideTask = dataObject.toJSONString();
                dataObject.clear();

                //data trứng bư
                if (player.mabuEgg != null) {
                    dataObject.put("create_time", player.mabuEgg.lastTimeCreate);
                    dataObject.put("time_done", player.mabuEgg.timeDone);
                }
                String mabuEgg = dataObject.toJSONString();
                dataObject.clear();

                //data bùa
                dataObject.put("td_tri_tue", player.charms.tdTriTue);
                dataObject.put("td_manh_me", player.charms.tdManhMe);
                dataObject.put("td_da_trau", player.charms.tdDaTrau);
                dataObject.put("td_oai_hung", player.charms.tdOaiHung);
                dataObject.put("td_bat_tu", player.charms.tdBatTu);
                dataObject.put("td_deo_dai", player.charms.tdDeoDai);
                dataObject.put("td_thu_hut", player.charms.tdThuHut);
                dataObject.put("td_de_tu", player.charms.tdDeTu);
                dataObject.put("td_tri_tue3", player.charms.tdTriTue3);
                dataObject.put("td_tri_tue4", player.charms.tdTriTue4);
                String charm = dataObject.toJSONString();
                dataObject.clear();

                //data skill
                for (Skill skill : player.playerSkill.skills) {
//                    if (skill.skillId != -1) {
                    dataObject.put("temp_id", skill.template.id);
                    dataObject.put("point", skill.point);
                    dataObject.put("last_time_use", skill.lastTimeUseThisSkill);
//                    } else {
//                        dataObject.put("temp_id", -1);
//                        dataObject.put("point", 0);
//                        dataObject.put("last_time_use", 0);
//                    }
                    dataArray.add(dataObject.toJSONString());
                }
                String skills = dataArray.toJSONString();
                dataObject.clear();
                dataArray.clear();

                //data skill shortcut
                for (int skillId : player.playerSkill.skillShortCut) {
                    dataArray.add(skillId);
                }
                String skillShortcut = dataArray.toJSONString();
                dataArray.clear();

                String petInfo = dataObject.toJSONString();
                String petPoint = dataObject.toJSONString();
                String petBody = dataArray.toJSONString();
                String petSkill = dataArray.toJSONString();

                //data pet
                if (player.pet != null) {
                    dataObject.put("name", player.pet.name);
                    dataObject.put("gender", player.pet.gender);
                    dataObject.put("is_mabu", player.pet.isMabu ? 1 : 0);
                    dataObject.put("status", player.pet.status);
                    dataObject.put("type_fusion", player.fusion.typeFusion);
                    int timeLeftFusion = (int) (Fusion.TIME_FUSION - (System.currentTimeMillis() - player.fusion.lastTimeFusion));
                    dataObject.put("left_fusion", timeLeftFusion < 0 ? 0 : timeLeftFusion);
                    petInfo = dataObject.toJSONString();
                    dataObject.clear();

                    dataObject.put("power", player.pet.nPoint.power);
                    dataObject.put("tiem_nang", player.pet.nPoint.tiemNang);
                    dataObject.put("stamina", player.pet.nPoint.stamina);
                    dataObject.put("max_stamina", player.pet.nPoint.maxStamina);
                    dataObject.put("hpg", player.pet.nPoint.hpg);
                    dataObject.put("mpg", player.pet.nPoint.mpg);
                    dataObject.put("damg", player.pet.nPoint.dameg);
                    dataObject.put("defg", player.pet.nPoint.defg);
                    dataObject.put("critg", player.pet.nPoint.critg);
                    dataObject.put("limit_power", player.pet.nPoint.limitPower);
                    dataObject.put("hp", player.pet.nPoint.hp);
                    dataObject.put("mp", player.pet.nPoint.mp);
                    petPoint = dataObject.toJSONString();
                    dataObject.clear();

                    JSONArray items = new JSONArray();
                    for (Item item : player.pet.inventory.itemsBody) {
                        JSONObject dataItem = new JSONObject();
                        JSONArray opt = new JSONArray();
                        if (item.isNotNullItem()) {
                            dataItem.put("temp_id", item.template.id);
                            dataItem.put("quantity", item.quantity);
                            dataItem.put("create_time", item.createTime);
                            JSONArray options = new JSONArray();
                            for (Item.ItemOption io : item.itemOptions) {
                                opt.add(io.optionTemplate.id);
                                opt.add(io.param);
                                options.add(opt.toJSONString());
                                opt.clear();
                            }
                            dataItem.put("option", options.toJSONString());
                        } else {
                            dataItem.put("temp_id", -1);
                            dataItem.put("quantity", 0);
                            dataItem.put("create_time", 0);
                            dataItem.put("option", opt.toJSONString());
                        }
                        items.add(dataItem.toJSONString());
                    }
                    petBody = items.toJSONString();

                    JSONArray petSkills = new JSONArray();
                    for (Skill s : player.pet.playerSkill.skills) {
                        JSONArray pskill = new JSONArray();
                        if (s.skillId != -1) {
                            pskill.add(s.template.id);
                            pskill.add(s.point);
                        } else {
                            pskill.add(-1);
                            pskill.add(0);
                        }
                        petSkills.add(pskill.toJSONString());
                    }
                    petSkill = petSkills.toJSONString();
                }
                dataArray.clear();
                dataObject.clear();

                //data thưởng ngọc rồng đen
                for (int i = 1; i <= 7; i++) {
                    dataObject.put("time_out_of_date_" + i + "_star", player.rewardBlackBall.timeOutOfDateReward[i - 1]);
                    dataObject.put("last_time_get_" + i + "_star", player.rewardBlackBall.lastTimeGetReward[i - 1]);
                    dataArray.add(dataObject.toJSONString());
                    dataObject.clear();
                }
                String dataBlackBall = dataArray.toJSONString();
                dataArray.clear();
                ps = null;
                try (Connection con = DBService.gI().getConnection();) {
                    ps = con.prepareStatement("update player set head = ?, have_tennis_space_ship = ?,"
                            + "clan_id_sv" + Manager.SERVER + " = ?, data_inventory = ?, data_location = ?, data_point = ?, data_magic_tree = ?,"
                            + "items_body = ?, items_bag = ?, items_box = ?, items_box_lucky_round = ?, friends = ?,"
                            + "enemies = ?, data_intrinsic = ?, data_item_time = ?, data_task = ?, data_mabu_egg = ?,"
                            + "pet_info = ?, pet_point = ?, pet_body = ?, pet_skill = ? , power = ?, pet_power = ?, "
                            + "data_black_ball = ?, data_side_task = ?, data_charm = ?, skills = ?, skills_shortcut = ?,"  
                            + "thoi_vang = ?, 1sao = ?, 2sao = ?, 3sao = ?, Dhvt= ? where id = ?");

                    ps.setShort(1, player.head);
                    ps.setBoolean(2, player.haveTennisSpaceShip);
                    ps.setShort(3, (short) (player.clan != null ? player.clan.id : -1));
                    ps.setString(4, inventory);
                    ps.setString(5, location);
                    ps.setString(6, point);
                    ps.setString(7, magicTree);
                    ps.setString(8, itemsBody);
                    ps.setString(9, itemsBag);
                    ps.setString(10, itemsBox);
                    ps.setString(11, itemsBoxLuckyRound);
                    ps.setString(12, friend);
                    ps.setString(13, enemy);
                    ps.setString(14, intrinsic);
                    ps.setString(15, itemTime);
                    ps.setString(16, task);
                    ps.setString(17, mabuEgg);
                    ps.setString(18, petInfo);
                    ps.setString(19, petPoint);
                    ps.setString(20, petBody);
                    ps.setString(21, petSkill);
                    ps.setLong(22, player.nPoint.power);
                    ps.setLong(23, player.pet != null ? player.pet.nPoint.power : 0);
                    ps.setString(24, dataBlackBall);
                    ps.setString(25, sideTask);
                    ps.setString(26, charm);
                    ps.setString(27, skills);
                    ps.setString(28, skillShortcut);
                    ps.setInt(29, tv);
                    ps.setInt(30, n1s);
                    ps.setInt(31, n2s);
                    ps.setInt(32, n3s);
                    ps.setString(33, player.DaiHoiVoThuat.toJson());
                    ps.setInt(34, (int) player.id);
//                    ServerLogSavePlayer.gI().add(ps.toString());
                    ps.executeUpdate();
                    Logger.success("Total time save player " + player.name + " thành công! " + (System.currentTimeMillis() - st));
                    Logger.warning("\nCurrent using: " + DBService.gI().currentActive() + " - Current available: " + DBService.gI().currentIdle() + "\n");
                    ps.close();
                } catch (Exception e) {
                } finally {
                    try {
                        ps.close();
                    } catch (Exception e) {

                    }
                }
            } catch (Exception e) {
                Logger.logException(PlayerDAO.class, e, "Lỗi save player " + player.name);
            } finally {

            }
        }
    }

    public static void saveName(Player player) {
        PreparedStatement ps = null;
        try (Connection con = DBService.gI().getConnection();) {
            ps = con.prepareStatement("update player set name = ? where id = ?");
            ps.setString(1, player.name);
            ps.setInt(2, (int) player.id);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
        } finally {
            try {
                ps.close();
            } catch (Exception e) {
            }
        }
    }

    public static boolean isExistName(String name) {
        boolean exist = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Connection con = DBService.gI().getConnection();) {
            ps = con.prepareStatement("select * from player where name = ?");
            ps.setString(1, name);
            rs = ps.executeQuery();
            if (rs.next()) {
                exist = true;
            }
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return exist;
    }

//    public static void getGoldBar(Player player) {
//        Connection con = null;
//        try {
//            con = DBService.gI().getConnection();
//            PreparedStatement ps = con.prepareStatement("select thoi_vang from player where id = ?");
//            ps.setInt(1, (int) player.id);
//            ResultSet rs = ps.executeQuery();
//            if (rs.first()) {
//                player.inventory.goldBar = rs.getInt("thoi_vang");
//            }
//            rs.close();
//            ps.close();
//        } catch (Exception e) {
//            Logger.logException(PlayerDAO.class, e, "Lỗi get thỏi vàng " + player.name);
//        } finally {
//            DBService.gI().release(con);
//        }
//    }
    public static void subGoldBar(Player player, int num) {
        PreparedStatement ps = null;
        try (Connection con = DBService.gI().getConnection();) {
            ps = con.prepareStatement("update account set thoi_vang = (thoi_vang - ?) where id = ?");
            ps.setInt(1, num);
            ps.setInt(2, player.getSession().userId);
            ps.executeUpdate();
        } catch (Exception e) {
            Logger.logException(PlayerDAO.class, e, "Lỗi update thỏi vàng " + player.name);
        } finally {
            try {
                ps.close();
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(PlayerDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void addHistoryReceiveGoldBar(Player player, int goldBefore, int goldAfter,
            int goldBagBefore, int goldBagAfter, int goldBoxBefore, int goldBoxAfter) {
        PreparedStatement ps = null;
        try (Connection con = DBService.gI().getConnection();) {
            ps = con.prepareStatement("insert into history_receive_goldbar(player_id,player_name,gold_before_receive,"
                    + "gold_after_receive,gold_bag_before,gold_bag_after,gold_box_before,gold_box_after) values (?,?,?,?,?,?,?,?)");
            ps.setInt(1, (int) player.id);
            ps.setString(2, player.name);
            ps.setInt(3, goldBefore);
            ps.setInt(4, goldAfter);
            ps.setInt(5, goldBagBefore);
            ps.setInt(6, goldBagAfter);
            ps.setInt(7, goldBoxBefore);
            ps.setInt(8, goldBoxAfter);
            ps.executeUpdate();
        } catch (Exception e) {
            Logger.logException(PlayerDAO.class, e, "Lỗi update thỏi vàng " + player.name);
        } finally {
            try {
                ps.close();
            } catch (Exception e) {
            }
        }
    }

//    public static void getItemReward(Player player) {
//        player.inventory.itemsReward.clear();
//        Connection con = null;
//        try {
//            con = DBService.gI().getConnection();
//            PreparedStatement ps = con.prepareStatement("select reward from account where id = ?");
//            ps.setInt(1, player.getSession().userId);
//            ResultSet rs = ps.executeQuery();
//            if (rs.next()) {
//                String[] itemsReward = rs.getString("reward").split(";");
//                for (String itemInfo : itemsReward) {
//                    if (itemInfo == null || itemInfo.equals("")) {
//                        continue;
//                    }
//                    String[] subItemInfo = itemInfo.replaceAll("[{}\\[\\]]", "").split("\\|");
//                    String[] baseInfo = subItemInfo[0].split(":");
//                    int itemId = Integer.parseInt(baseInfo[0]);
//                    int quantity = Integer.parseInt(baseInfo[1]);
//                    Item item = ItemService.gI().createNewItem((short) itemId, quantity);
//                    if (subItemInfo.length == 2) {
//                        String[] options = subItemInfo[1].split(",");
//                        for (String opt : options) {
//                            if (opt == null || opt.equals("")) {
//                                continue;
//                            }
//                            String[] optInfo = opt.split(":");
//                            int tempIdOption = Integer.parseInt(optInfo[0]);
//                            int param = Integer.parseInt(optInfo[1]);
//                            item.itemOptions.add(new Item.ItemOption(tempIdOption, param));
//                        }
//                    }
//                    player.inventory.itemsReward.add(item);
//                }
//            }
//            ps.close();
//        } catch (Exception e) {
////            Logger.logException(PlayerDAO.class, e, "Lỗi get phần thưởng " + player.name);
//        } finally {
//            DBService.gI().release(con);
//        }
//    }
    public static void updateItemReward(Player player) {
        String dataItemReward = "";
        for (Item item : player.getSession().itemsReward) {
            if (item.isNotNullItem()) {
                dataItemReward += "{" + item.template.id + ":" + item.quantity;
                if (!item.itemOptions.isEmpty()) {
                    dataItemReward += "|";
                    for (Item.ItemOption io : item.itemOptions) {
                        dataItemReward += "[" + io.optionTemplate.id + ":" + io.param + "],";
                    }
                    dataItemReward = dataItemReward.substring(0, dataItemReward.length() - 1) + "};";
                }
            }
        }
        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Connection con = DBService.gI().getConnection();) {
            ps = con.prepareStatement("update account set reward = ? where id = ?");
            ps.setString(1, dataItemReward);
            ps.setInt(2, player.getSession().userId);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            Logger.logException(PlayerDAO.class, e, "Lỗi update phần thưởng " + player.name);
        } finally {
            try {
                ps.close();
            } catch (Exception e) {
            }
        }
    }

    public static void saveBag(Connection con, Player player) {
        if (player.loaded) {
            PreparedStatement ps = null;
            try {
                JSONArray dataArray = new JSONArray();
                JSONObject dataObject = new JSONObject();

                for (Item item : player.inventory.itemsBag) {
                    JSONArray opt = new JSONArray();
                    if (item.isNotNullItem()) {
                        dataObject.put("temp_id", item.template.id);
                        dataObject.put("quantity", item.quantity);
                        dataObject.put("create_time", item.createTime);
                        JSONArray options = new JSONArray();
                        for (Item.ItemOption io : item.itemOptions) {
                            opt.add(io.optionTemplate.id);
                            opt.add(io.param);
                            options.add(opt.toJSONString());
                            opt.clear();
                        }
                        dataObject.put("option", options.toJSONString());
                    } else {
                        dataObject.put("temp_id", -1);
                        dataObject.put("quantity", 0);
                        dataObject.put("create_time", 0);
                        dataObject.put("option", opt.toJSONString());
                    }
                    dataArray.add(dataObject.toJSONString());
                }
                String itemsBag = dataArray.toJSONString();
                dataObject.clear();
                dataArray.clear();

                ps = con.prepareStatement("update player set items_bag = ? where id = ?");
                ps.setString(1, itemsBag);
                ps.setInt(2, (int) player.id);
                ps.executeUpdate();
                ps.close();
            } catch (Exception e) {
                Logger.logException(PlayerDAO.class, e, "Lỗi save bag player " + player.name);
            } finally {
                try {
                    ps.close();
                } catch (SQLException ex) {
                    java.util.logging.Logger.getLogger(PlayerDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }
}
