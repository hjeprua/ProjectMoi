package com.nrolove.jdbc.daos;

import com.nrolove.consts.ConstPlayer;
import com.nrolove.data.DataGame;
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
import com.nrolove.models.player.Pet;
import com.nrolove.models.player.Player;
import com.nrolove.models.skill.Skill;
import com.nrolove.models.task.TaskMain;
import com.nrolove.server.Client;
import com.nrolove.server.Controller;
import com.nrolove.server.Manager;
import com.nrolove.server.io.Session;
import com.nrolove.server.model.AntiLogin;
import com.nrolove.services.ClanService;
import com.nrolove.services.IntrinsicService;
import com.nrolove.services.ItemService;
import com.nrolove.services.MapService;
import com.nrolove.services.PlayerService;
import com.nrolove.services.Service;
import com.nrolove.services.TaskService;
import com.nrolove.utils.Logger;
import com.nrolove.utils.SkillUtil;
import com.nrolove.utils.TimeUtil;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author ❤Girlkun75❤
 * @copyright ❤Trần Lại❤
 */
public class GodGK {

    public static Player login(Session session, AntiLogin al) {
        Player player = null;
        Statement stm = null;
        ResultSet rs = null;
        try (Connection con = DBService.gI().getConnection();) {
            String query = "select * from account where username = '" + session.uu + "' and password = '" + session.pp + "'";
            stm = con.createStatement();
            rs = stm.executeQuery(query);
            if (rs.first()) {
                session.userId = rs.getInt("account.id");
                session.isAdmin = rs.getBoolean("is_admin");
                session.lastTimeLogout = rs.getTimestamp("last_time_logout").getTime();
                session.actived = rs.getBoolean("active");
                session.goldBar = rs.getInt("account.thoi_vang");
                session.dataReward = rs.getString("reward");

//                if (!session.isAdmin) {
//                    Service.getInstance().sendThongBaoOK(session, "Chi danh cho admin");
//                } else 
                    
                if (rs.getBoolean("ban")) {
                    Service.getInstance().sendThongBaoOK(session, "Tài khoản đã bị khóa do vi phạm điều khoản!");
                } else if (rs.getTimestamp("last_time_login").getTime() > session.lastTimeLogout) {
                    Player plInGame = Client.gI().getPlayerByUser(session.userId);
                    if (plInGame != null) {
                        Client.gI().kickSession(plInGame.getSession());
                        Service.getInstance().sendThongBaoOK(session, "Máy chủ tắt hoặc mất sóng");
                    } else {
                        Service.getInstance().sendThongBaoOK(session, "Tài khoản đang được đăng nhập tại máy chủ khác");
                    }
                } else {
                    long lastTimeLogout = rs.getTimestamp("last_time_logout").getTime();
                    int secondsPass = (int) ((System.currentTimeMillis() - lastTimeLogout) / 1000);
                    if (secondsPass < Manager.SECOND_WAIT_LOGIN) {
                        Service.getInstance().sendThongBaoOK(session, "Vui lòng chờ " + (Manager.SECOND_WAIT_LOGIN - secondsPass) + " giây để đăng nhập lại.");
                    } else {
                        rs = stm.executeQuery("select * from player where account_id = " + session.userId + " limit 1");
                        if (!rs.first()) {
                            //-28 -4 version data game
                            DataGame.sendVersionGame(session);
                            //-31 data item background
                            DataGame.sendDataItemBG(session);
                            Service.getInstance().switchToCreateChar(session);
                        } else {
                            int plHp = 200000000;
                            int plMp = 200000000;
                            JSONValue jv = new JSONValue();
                            JSONArray dataArray = null;
                            JSONObject dataObject = null;

                            player = new Player();

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
                            try {
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
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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
                            
                            dataObject = (JSONObject) jv.parse(rs.getString("Dhvt"));
                            if (System.currentTimeMillis() < Long.parseLong(String.valueOf(dataObject.get("time")))) {
                                player.DaiHoiVoThuat.step = Integer
                                        .parseInt(String.valueOf(dataObject.get("step")));
                                player.DaiHoiVoThuat.die = Integer
                                        .parseInt(String.valueOf(dataObject.get("die")));
                                player.DaiHoiVoThuat.isDrop = Boolean
                                        .parseBoolean(String.valueOf(dataObject.get("isdrop")));
                            }
                            dataObject.clear();

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
                                // pet.nPoint.calPoint();
                                player.pet = pet;
                            }

                            player.nPoint.hp = plHp;
                            player.nPoint.mp = plMp;
                            stm.executeUpdate("update account set last_time_login = '"
                                    + new Timestamp(System.currentTimeMillis()) + "', ip_address = '"
                                    + session.ipAddress + "' where id = " + session.userId);
                        }
                    }
                }
                al.reset();
            } else {
                Service.getInstance().sendThongBaoOK(session, "Thông tin tài khoản hoặc mật khẩu không chính xác");
                //al.wrong();
                // Anti login
            }
        } catch (Exception e) {
            player.dispose();
            player = null;
            Logger.logException(GodGK.class, e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                }
            }
            if (stm != null) {
                try {
                    stm.close();
                } catch (SQLException ex) {
                }
            }
        }
        return player;
    }

}
