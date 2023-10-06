package com.nrolove.models.npc;

import static com.nrolove.services.func.SummonDragon.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.nrolove.consts.ConstMap;
import com.nrolove.consts.ConstNpc;
import com.nrolove.consts.ConstPlayer;
import com.nrolove.consts.ConstTask;
import com.nrolove.jdbc.daos.PlayerDAO;
import com.nrolove.models.boss.Boss;
import com.nrolove.models.boss.BossFactory;
import com.nrolove.models.boss.BossManager;
import com.nrolove.models.item.Item;
import com.nrolove.models.map.Map;
import com.nrolove.models.map.MaBu12h.MapMaBu;
import com.nrolove.models.map.blackball.BlackBallWar;
import com.nrolove.models.map.phoban.BanDoKhoBau;
import com.nrolove.models.map.phoban.DoanhTrai;
import com.nrolove.models.player.Inventory;
import com.nrolove.models.player.NPoint;
import com.nrolove.models.player.Player;
import com.nrolove.server.Maintenance;
import com.nrolove.services.BanDoKhoBauService;
import com.nrolove.services.ClanService;
import com.nrolove.services.DoanhTraiService;
import com.nrolove.services.FriendAndEnemyService;
import com.nrolove.services.IntrinsicService;
import com.nrolove.services.InventoryService;
import com.nrolove.services.ItemService;
import com.nrolove.services.MapService;
import com.nrolove.services.NpcService;
import com.nrolove.services.OpenPowerService;
import com.nrolove.services.PetService;
import com.nrolove.services.PlayerService;
import com.nrolove.services.Service;
import com.nrolove.services.TaskService;
import com.nrolove.services.func.ChangeMapService;
import com.nrolove.services.func.CombineServiceNew;
import com.nrolove.services.func.Input;
import com.nrolove.services.func.LuckyRound;
import com.nrolove.services.func.PVPServcice;
import com.nrolove.services.func.ShopService;
import com.nrolove.services.func.SummonDragon;
import com.nrolove.services.func.TopService;
import com.nrolove.utils.Logger;
import com.nrolove.utils.TimeUtil;
import com.nrolove.utils.Util;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 *
 */
public class NpcFactory {

    private static boolean nhanVang = true;
    private static boolean nhanDeTu = true;

    //playerid - object
    public static final java.util.Map<Long, Object> PLAYERID_OBJECT = new HashMap<Long, Object>();

    private NpcFactory() {

    }

    public static Npc createNPC(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        Npc npc = null;
        try {
            switch (tempId) {
                case ConstNpc.QUY_LAO_KAME:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Chào con, con muốn ta giúp gì nào?",
                                            "Bản đồ\nkho báu", "Chưa Có\nSự Kiện", "Shop\nHồng Ngọc",
                                            "Giải tán\nbang hội", "Lãnh Địa\nbang hội", "Từ chối");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:
                                            if (player.clan != null) {
                                                if (player.clan.banDoKhoBau != null) {
                                                    this.createOtherMenu(player, ConstNpc.MENU_OPENED_DBKB,
                                                            "Bang hội của con đang đi tìm kho báu dưới biển cấp độ "
                                                            + player.clan.banDoKhoBau.level
                                                            + "\nCon có muốn đi theo không?",
                                                            "Đồng ý", "Từ chối");
                                                } else {

                                                    this.createOtherMenu(player, ConstNpc.MENU_OPEN_DBKB,
                                                            "Đây là bản đồ kho báu hải tặc tí hon\nCác con cứ yên tâm lên đường\n"
                                                            + "Ở đây có ta lo\nNhớ chọn cấp độ vừa sức mình nhé",
                                                            "Chọn\ncấp độ", "Từ chối");
                                                }
                                            } else {
                                                this.npcChat(player, "Con phải có bang hội ta mới có thể cho con đi");
                                            }
                                            break;
                                        case 1:
                                            this.createOtherMenu(player, ConstNpc.MENU_OPEN_SUKIEN,
                                                    "Sự kiện Halloween chính thức tại Ngọc Rồng Lord\n"
                                                    + "Chuẩn bị x10 nguyên liệu Kẹo, Bánh Quy, Bí ngô để đổi Giỏ Kẹo cho ta nhé\n"
                                                    + "Nguyên Liệu thu thập bằng cách đánh quái tại các hành tinh được chỉ định\n"
                                                    + "Tích lũy 3 Giỏ Kẹo +  3 Vé mang qua đây ta sẽ cho con 1 Hộp Ma Quỷ\n"
                                                    + "Tích lũy 3 Giỏ Kẹo, 3 Hộp Ma Quỷ + 3 Vé \nmang qua đây ta sẽ cho con 1 hộp quà thú vị.",
                                                    "Đổi\nGiỏ Kẹo", "Đổi Hộp\nMa Quỷ", "Đổi Hộp\nQuà Halloween",
                                                    "Từ chối");

                                            break;
                                        case 2:
                                            ShopService.gI().openShopHongNgoc(player, ConstNpc.SHOP_HONG_NGOC, 0);
                                            break;
                                        case 3:
                                            if (player.clan != null) {
                                                ClanService.gI().RemoveClanAll(player);
                                            } else {
                                                Service.getInstance().sendThongBao(player,
                                                        "Bạn không có bang hội nào để giải tán.");
                                            }
                                            break;
                                        case 4:
                                            if (player.clan != null) {
                                                Service.getInstance().sendThongBao(player,
                                                        "Cần Đạt 80 tỷ.");
                                            }
                                            if (player.getSession().player.nPoint.power >= 80000000000L) {

                                                ChangeMapService.gI().changeMapBySpaceShip(player, 156, -1, 432);
                                                break; // qua lanh dia
                                            }
                                    }

                                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPEN_SUKIEN) {
                                    switch (select) {
                                        case 0:
                                            if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                                                Item keo = InventoryService.gI().finditemnguyenlieuKeo(player);
                                                Item banh = InventoryService.gI().finditemnguyenlieuBanh(player);
                                                Item bingo = InventoryService.gI().finditemnguyenlieuBingo(player);

                                                if (keo != null && banh != null && bingo != null) {
                                                    Item GioBingo = ItemService.gI().createNewItem((short) 2016, 1);

                                                    // - Số item sự kiện có trong rương
                                                    InventoryService.gI().subQuantityItemsBag(player, keo, 10);
                                                    InventoryService.gI().subQuantityItemsBag(player, banh, 10);
                                                    InventoryService.gI().subQuantityItemsBag(player, bingo, 10);

                                                    GioBingo.itemOptions.add(new Item.ItemOption(74, 0));
                                                    InventoryService.gI().addItemBag(player, GioBingo, false);
                                                    InventoryService.gI().sendItemBags(player);
                                                    Service.getInstance().sendThongBao(player,
                                                            "Đổi quà sự kiện thành công");
                                                } else {
                                                    Service.getInstance().sendThongBao(player,
                                                            "Vui lòng chuẩn bị x10 Nguyên Liệu Kẹo, Bánh Quy, Bí Ngô để đổi vật phẩm sự kiện");
                                                }
                                            } else {
                                                Service.getInstance().sendThongBao(player, "Hành trang đầy.");
                                            }
                                            break;
                                        case 1:
                                            if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                                                Item ve = InventoryService.gI().finditemnguyenlieuVe(player);
                                                Item giokeo = InventoryService.gI().finditemnguyenlieuGiokeo(player);

                                                if (ve != null && giokeo != null) {
                                                    Item Hopmaquy = ItemService.gI().createNewItem((short) 2017, 1);
                                                    // - Số item sự kiện có trong rương
                                                    InventoryService.gI().subQuantityItemsBag(player, ve, 3);
                                                    InventoryService.gI().subQuantityItemsBag(player, giokeo, 3);

                                                    Hopmaquy.itemOptions.add(new Item.ItemOption(74, 0));
                                                    InventoryService.gI().addItemBag(player, Hopmaquy, false);
                                                    InventoryService.gI().sendItemBags(player);
                                                    Service.getInstance().sendThongBao(player,
                                                            "Đổi quà sự kiện thành công");
                                                } else {
                                                    Service.getInstance().sendThongBao(player,
                                                            "Vui lòng chuẩn bị x3 Vé đổi Kẹo và x3 Giỏ kẹo để đổi vật phẩm sự kiện");
                                                }
                                            } else {
                                                Service.getInstance().sendThongBao(player, "Hành trang đầy.");
                                            }
                                            break;
                                        case 2:
                                            if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                                                Item ve = InventoryService.gI().finditemnguyenlieuVe(player);
                                                Item giokeo = InventoryService.gI().finditemnguyenlieuGiokeo(player);
                                                Item hopmaquy = InventoryService.gI()
                                                        .finditemnguyenlieuHopmaquy(player);

                                                if (ve != null && giokeo != null && hopmaquy != null) {
                                                    Item HopQuaHLW = ItemService.gI().createNewItem((short) 2012, 1);
                                                    // - Số item sự kiện có trong rương
                                                    InventoryService.gI().subQuantityItemsBag(player, ve, 3);
                                                    InventoryService.gI().subQuantityItemsBag(player, giokeo, 3);
                                                    InventoryService.gI().subQuantityItemsBag(player, hopmaquy, 3);

                                                    HopQuaHLW.itemOptions.add(new Item.ItemOption(74, 0));
                                                    HopQuaHLW.itemOptions.add(new Item.ItemOption(30, 0));
                                                    InventoryService.gI().addItemBag(player, HopQuaHLW, false);
                                                    InventoryService.gI().sendItemBags(player);
                                                    Service.getInstance().sendThongBao(player,
                                                            "Đổi quà hộp quà sự kiện Halloween thành công");
                                                } else {
                                                    Service.getInstance().sendThongBao(player,
                                                            "Vui lòng chuẩn bị x3 Hộp Ma Quỷ, x3 Vé đổi Kẹo và x3 Giỏ kẹo để đổi vật phẩm sự kiện");
                                                }
                                            } else {
                                                Service.getInstance().sendThongBao(player, "Hành trang đầy.");
                                            }
                                            break;
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPENED_DBKB) {
                                    switch (select) {
                                        case 0:
                                            if (player.isAdmin()
                                                    || player.nPoint.power >= BanDoKhoBau.POWER_CAN_GO_TO_DBKB) {
                                                ChangeMapService.gI().goToDBKB(player);
                                            } else {
                                                this.npcChat(player, "Sức mạnh của con phải ít nhất phải đạt "
                                                        + Util.numberToMoney(BanDoKhoBau.POWER_CAN_GO_TO_DBKB));
                                            }
                                            break;

                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPEN_DBKB) {
                                    switch (select) {
                                        case 0:
                                            if (player.isAdmin()
                                                    || player.nPoint.power >= BanDoKhoBau.POWER_CAN_GO_TO_DBKB) {
                                                Input.gI().createFormChooseLevelBDKB(player);
                                            } else {
                                                this.npcChat(player, "Sức mạnh của con phải ít nhất phải đạt "
                                                        + Util.numberToMoney(BanDoKhoBau.POWER_CAN_GO_TO_DBKB));
                                            }
                                            break;
                                    }

                                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_ACCEPT_GO_TO_BDKB) {
                                    switch (select) {
                                        case 0:
                                            BanDoKhoBauService.gI().openBanDoKhoBau(player,
                                                    Byte.parseByte(String.valueOf(PLAYERID_OBJECT.get(player.id))));
                                            break;
                                    }

                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.TRUONG_LAO_GURU:
                case ConstNpc.VUA_VEGETA:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                    super.openBaseMenu(player);
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {

                            }
                        }
                    };
                    break;
                case ConstNpc.ONG_GOHAN:
                case ConstNpc.ONG_MOORI:
                case ConstNpc.ONG_PARAGUS:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Con cố gắng theo %1 học thành tài, đừng lo lắng cho ta."
                                                    .replaceAll("%1", player.gender == ConstPlayer.TRAI_DAT ? "Quy lão Kamê"
                                                            : player.gender == ConstPlayer.NAMEC ? "Trưởng lão Guru" : "Vua Vegeta"),
                                            "Nhận quà\ntân thủ", "Quy Đổi");
                                    // , "Đổi\nMật Khẩu"
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:
                                            this.createOtherMenu(player, ConstNpc.QUA_TAN_THU, "Ông có quà cho con đây này",
                                                    "Nhận 100k\nNgọc xanh", "Nhận\n2 Tỉ Vàng", "Nhận\nĐệ tử", "Mã Quà Tặng");
                                            break;
                                        case 1:
                                            if (player.getSession().goldBar > 0) {
                                                this.createOtherMenu(player, ConstNpc.MENU_PHAN_THUONG, "Ta đang giữ cho con "
                                                        + player.getSession().goldBar + " thỏi vàng, con có nhận luôn không?",
                                                        "Nhận " + player.getSession().goldBar + "\nthỏi vàng", "Từ chối");
                                                //"Rương Quà",
                                            } else {
                                                this.createOtherMenu(player, ConstNpc.MENU_PHAN_THUONG, "Hiện tại ông không giữ của con thỏi vàng nào cả!",
                                                        "Từ chối");
                                                //"Mở rương\nquà",
                                            }
                                            break;
                                        case 2:
//                                            Input.gI().createFormChangePassword(player);
                                            break;
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.QUA_TAN_THU) {
                                    switch (select) {
                                        case 0:
//                                        if (!player.gift.gemTanThu) {
                                            if (true) {
                                                player.inventory.gem = 100000;
                                                Service.getInstance().sendMoney(player);
                                                Service.getInstance().sendThongBao(player, "Bạn vừa nhận được 100K ngọc xanh");
                                                player.gift.gemTanThu = true;
                                            } else {
                                                this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Con đã nhận phần quà này rồi mà",
                                                        "Đóng");
                                            }
                                            break;
                                        case 1:
                                            if (nhanVang) {
                                                player.inventory.gold = Inventory.LIMIT_GOLD;
                                                Service.getInstance().sendMoney(player);
                                                Service.getInstance().sendThongBao(player, "Bạn vừa nhận được 2 tỉ vàng");
                                            } else {
                                                this.npcChat("Tính năng Nhận vàng đã đóng.");
                                            }
                                            break;
                                        case 2:
                                            if (nhanDeTu) {
                                                if (player.pet == null) {
                                                    PetService.gI().createNormalPet(player);
                                                    Service.getInstance().sendThongBao(player, "Bạn vừa nhận được đệ tử");
                                                } else {
                                                    this.npcChat("Con đã nhận đệ tử rồi");
                                                }
                                            } else {
                                                this.npcChat("Tính năng Nhận đệ tử đã đóng.");
                                            }
                                            break;
                                        case 3:
                                            Input.gI().createFormGiftCode(player);
                                            break;

                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_PHAN_THUONG) {
                                    switch (select) {
//                                        case 0:
//                                            ShopService.gI().openBoxItemReward(player);
//                                            break;
                                        case 0:
                                            if (player.getSession().goldBar > 0) {
                                                if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                                                    int quantity = player.getSession().goldBar;
                                                    Item goldBar = ItemService.gI().createNewItem((short) 457, quantity);
                                                    InventoryService.gI().addItemBag(player, goldBar, false);
                                                    InventoryService.gI().sendItemBags(player);
                                                    this.npcChat(player, "Ông đã để " + quantity + " thỏi vàng vào hành trang con rồi đấy");
                                                    PlayerDAO.subGoldBar(player, quantity);
                                                    player.getSession().goldBar = 0;
                                                } else {
                                                    this.npcChat(player, "Con phải có ít nhất 1 ô trống trong hành trang ông mới đưa cho con được");
                                                }
                                            }
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;

                case ConstNpc.BUNMA:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Cậu cần trang bị gì cứ đến chỗ tôi nhé", "Cửa\nhàng");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0://Shop
                                            if (player.gender == ConstPlayer.TRAI_DAT) {
                                                this.openShopWithGender(player, ConstNpc.SHOP_BUNMA_QK_0, 0);
                                            } else {
                                                this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Xin lỗi cưng, chị chỉ bán đồ cho người Trái Đất", "Đóng");
                                            }
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.DENDE:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Anh cần trang bị gì cứ đến chỗ em nhé", "Cửa\nhàng");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0://Shop
                                            if (player.gender == ConstPlayer.NAMEC) {
                                                this.openShopWithGender(player, ConstNpc.SHOP_DENDE_0, 0);
                                            } else {
                                                this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Xin lỗi anh, em chỉ bán đồ cho dân tộc Namếc", "Đóng");
                                            }
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.APPULE:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Ngươi cần trang bị gì cứ đến chỗ ta nhé", "Cửa\nhàng");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0://Shop
                                            if (player.gender == ConstPlayer.XAYDA) {
                                                this.openShopWithGender(player, ConstNpc.SHOP_APPULE_0, 0);
                                            } else {
                                                this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Về hành tinh hạ đẳng của ngươi mà mua đồ cùi nhé. Tại đây ta chỉ bán đồ cho người Xayda thôi", "Đóng");
                                            }
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.DR_DRIEF:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player pl) {
                            if (canOpenNpc(pl)) {
                                if (this.mapId == 84) {
                                    this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                            "Tàu Vũ Trụ của ta có thể đưa cậu đến hành tinh khác chỉ trong 3 giây. Cậu muốn đi đâu?",
                                            pl.gender == ConstPlayer.TRAI_DAT ? "Đến\nTrái Đất" : pl.gender == ConstPlayer.NAMEC ? "Đến\nNamếc" : "Đến\nXayda");
                                } else if (!TaskService.gI().checkDoneTaskTalkNpc(pl, this)) {
                                    if (pl.playerTask.taskMain.id == 7) {
                                        NpcService.gI().createTutorial(pl, this.avartar, "Hãy lên đường cứu đứa bé nhà tôi\n"
                                                + "Chắc bây giờ nó đang sợ hãi lắm rồi");
                                    } else {
                                        this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                                "Tàu Vũ Trụ của ta có thể đưa cậu đến hành tinh khác chỉ trong 3 giây. Cậu muốn đi đâu?",
                                                "Đến\nNamếc", "Đến\nXayda", "Siêu thị");
                                    }
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 84) {
                                    ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 24, -1, -1);
                                } else if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:
                                            ChangeMapService.gI().changeMapBySpaceShip(player, 25, -1, -1);
                                            break;
                                        case 1:
                                            ChangeMapService.gI().changeMapBySpaceShip(player, 26, -1, -1);
                                            break;
                                        case 2:
                                            ChangeMapService.gI().changeMapBySpaceShip(player, 84, -1, -1);
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.CARGO:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player pl) {
                            if (canOpenNpc(pl)) {
                                if (!TaskService.gI().checkDoneTaskTalkNpc(pl, this)) {
                                    if (pl.playerTask.taskMain.id == 7) {
                                        NpcService.gI().createTutorial(pl, this.avartar, "Hãy lên đường cứu đứa bé nhà tôi\n"
                                                + "Chắc bây giờ nó đang sợ hãi lắm rồi");
                                    } else {
                                        this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                                "Tàu Vũ Trụ của ta có thể đưa cậu đến hành tinh khác chỉ trong 3 giây. Cậu muốn đi đâu?",
                                                "Đến\nTrái Đất", "Đến\nXayda", "Siêu thị");
                                    }
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:
                                            ChangeMapService.gI().changeMapBySpaceShip(player, 24, -1, -1);
                                            break;
                                        case 1:
                                            ChangeMapService.gI().changeMapBySpaceShip(player, 26, -1, -1);
                                            break;
                                        case 2:
                                            ChangeMapService.gI().changeMapBySpaceShip(player, 84, -1, -1);
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.CUI:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {

                        private final int COST_FIND_BOSS = 20000000;

                        @Override
                        public void openBaseMenu(Player pl) {
                            if (canOpenNpc(pl)) {
                                if (!TaskService.gI().checkDoneTaskTalkNpc(pl, this)) {
                                    if (pl.playerTask.taskMain.id == 7) {
                                        NpcService.gI().createTutorial(pl, this.avartar, "Hãy lên đường cứu đứa bé nhà tôi\n"
                                                + "Chắc bây giờ nó đang sợ hãi lắm rồi");
                                    } else {
                                        if (this.mapId == 19) {

                                            int taskId = TaskService.gI().getIdTask(pl);
                                            switch (taskId) {
                                                case ConstTask.TASK_19_0:
                                                    this.createOtherMenu(pl, ConstNpc.MENU_FIND_KUKU,
                                                            "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                                            "Đến chỗ\nKuku\n(" + Util.numberToMoney(COST_FIND_BOSS) + " vàng)", "Đến Cold", "Đến\nNappa", "Từ chối");
                                                    break;
                                                case ConstTask.TASK_19_1:
                                                    this.createOtherMenu(pl, ConstNpc.MENU_FIND_MAP_DAU_DINH,
                                                            "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                                            "Đến chỗ\nMập đầu đinh\n(" + Util.numberToMoney(COST_FIND_BOSS) + " vàng)", "Đến Cold", "Đến\nNappa", "Từ chối");
                                                    break;
                                                case ConstTask.TASK_19_2:
                                                    this.createOtherMenu(pl, ConstNpc.MENU_FIND_RAMBO,
                                                            "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                                            "Đến chỗ\nRambo\n(" + Util.numberToMoney(COST_FIND_BOSS) + " vàng)", "Đến Cold", "Đến\nNappa", "Từ chối");
                                                    break;
                                                default:
                                                    this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                                            "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                                            "Đến Cold", "Đến\nNappa", "Từ chối");

                                                    break;
                                            }
                                        } else if (this.mapId == 68) {
                                            this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                                    "Ngươi muốn về Thành Phố Vegeta", "Đồng ý", "Từ chối");
                                        } else {
                                            this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                                    "Tàu vũ trụ Xayda sử dụng công nghệ mới nhất, "
                                                    + "có thể đưa ngươi đi bất kỳ đâu, chỉ cần trả tiền là được.",
                                                    "Đến\nTrái Đất", "Đến\nNamếc", "Siêu thị");
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 26) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 24, -1, -1);
                                                break;
                                            case 1:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 25, -1, -1);
                                                break;
                                            case 2:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 84, -1, -1);
                                                break;
                                        }
                                    }
                                }
                                if (this.mapId == 19) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                                                break;
                                            case 1:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_FIND_KUKU) {
                                        switch (select) {
                                            case 0:
                                                Boss boss = BossManager.gI().getBossById(BossFactory.KUKU);
                                                if (boss != null && !boss.isDie()) {
                                                    if (player.inventory.gold >= COST_FIND_BOSS) {
                                                        player.inventory.gold -= COST_FIND_BOSS;
                                                        ChangeMapService.gI().changeMap(player, boss.zone, boss.location.x, boss.location.y);
                                                        Service.getInstance().sendMoney(player);
                                                    } else {
                                                        Service.getInstance().sendThongBao(player, "Không đủ vàng, còn thiếu "
                                                                + Util.numberToMoney(COST_FIND_BOSS - player.inventory.gold) + " vàng");
                                                    }
                                                }
                                                break;
                                            case 1:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                                                break;
                                            case 2:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_FIND_MAP_DAU_DINH) {
                                        switch (select) {
                                            case 0:
                                                Boss boss = BossManager.gI().getBossById(BossFactory.MAP_DAU_DINH);
                                                if (boss != null && !boss.isDie()) {
                                                    if (player.inventory.gold >= COST_FIND_BOSS) {
                                                        player.inventory.gold -= COST_FIND_BOSS;
                                                        ChangeMapService.gI().changeMap(player, boss.zone, boss.location.x, boss.location.y);
                                                        Service.getInstance().sendMoney(player);
                                                    } else {
                                                        Service.getInstance().sendThongBao(player, "Không đủ vàng, còn thiếu "
                                                                + Util.numberToMoney(COST_FIND_BOSS - player.inventory.gold) + " vàng");
                                                    }
                                                }
                                                break;
                                            case 1:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                                                break;
                                            case 2:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_FIND_RAMBO) {
                                        switch (select) {
                                            case 0:
                                                Boss boss = BossManager.gI().getBossById(BossFactory.RAMBO);
                                                if (boss != null && !boss.isDie()) {
                                                    if (player.inventory.gold >= COST_FIND_BOSS) {
                                                        player.inventory.gold -= COST_FIND_BOSS;
                                                        ChangeMapService.gI().changeMap(player, boss.zone, boss.location.x, boss.location.y);
                                                        Service.getInstance().sendMoney(player);
                                                    } else {
                                                        Service.getInstance().sendThongBao(player, "Không đủ vàng, còn thiếu "
                                                                + Util.numberToMoney(COST_FIND_BOSS - player.inventory.gold) + " vàng");
                                                    }
                                                }
                                                break;
                                            case 1:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                                                break;
                                            case 2:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                                                break;
                                        }
                                    }
                                }
                                if (this.mapId == 68) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 19, -1, 1100);
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.SANTA:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "Xin chào, ta có một số vật phẩm đặt biệt cậu có muốn xem không?",
                                        "Cửa hàng");
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 5 || this.mapId == 13 || this.mapId == 20) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0: //shop
                                                this.openShopWithGender(player, ConstNpc.SHOP_SANTA_0, 0);
                                                break;
                                            case 1: //tiệm hớt tóc
                                                this.openShopWithGender(player, ConstNpc.SHOP_SANTA_1, 1);
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.URON:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player pl) {
                            if (canOpenNpc(pl)) {
                                this.openShopWithGender(pl, ConstNpc.SHOP_URON_0, 0);
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {

                            }
                        }
                    };
                    break;
                case ConstNpc.BA_HAT_MIT:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 5) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Ngươi tìm ta có việc gì?",
                                            "Ép sao\ntrang bị", "Pha lê\nhóa\ntrang bị",
                                            "Đổi Vé\nHủy Diệt", "Đổi Đồ\nKích Hoạt");
                                } else if (this.mapId == 121) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Ngươi tìm ta có việc gì?",
                                            "Về đảo\nrùa");

                                } else {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Ngươi tìm ta có việc gì?",
                                            "Cửa hàng\nBùa", "Nâng cấp\nVật phẩm",
                                            "Nâng cấp\nBông tai\nPorata", "Làm phép\nNhập đá",
                                            "Nhập\nNgọc Rồng", "Nâng cấp\nChỉ Số\nPorata");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 5) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
//                                                CombineService.gI().openTabCombine(player, CombineService.EP_SAO_TRANG_BI);
                                                CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.EP_SAO_TRANG_BI);
                                                break;
                                            case 1:
                                                this.createOtherMenu(player, ConstNpc.MENU_PHA_LE_HOA_TRANG_BI,
                                                        "Ngươi muốn pha lê hóa trang bị bằng cách nào?", "Bằng ngọc", "Từ chối");
                                                break;
//                                            case 2:
//                                                this.createOtherMenu(player, ConstNpc.MENU_PHA_LE_HOA_TRANG_BI,
//                                                        "Ta sẽ biến trang bị mới cao cấp hơn của ngươi\nthành trang"
//                                                        + " bị có cấp độ và sao pha lê của trang bị cũ", "Chuyển\nhóa\nDùng vàng",
//                                                        "Chuyển\nhóa\nDùng ngọc");
//                                                break;
                                            case 2:
                                                CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.DOI_VE_HUY_DIET);
                                                break;
                                            case 3:
                                                CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.DAP_SET_KICH_HOAT);
                                                break;
//                                            case 5:
//                                                CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.DOI_MANH_KICH_HOAT);
//                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_PHA_LE_HOA_TRANG_BI) {
                                        switch (select) {
                                            case 0:
//                                                CombineService.gI().openTabCombine(player, CombineService.PHA_LE_HOA_TRANG_BI);
                                                CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.PHA_LE_HOA_TRANG_BI);
                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_CHUYEN_HOA_TRANG_BI) {
                                        switch (select) {
//                                            case 0:
//                                                CombineService.gI().openTabCombine(player, CombineService.CHUYEN_HOA_TRANG_BI); //by gold
//                                                break;
//                                            case 1:
//                                                CombineService.gI().openTabCombine(player, CombineService.CHUYEN_HOA_TRANG_BI); //by gold
//                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                                        switch (player.combineNew.typeCombine) {
                                            case CombineServiceNew.EP_SAO_TRANG_BI:
                                            case CombineServiceNew.PHA_LE_HOA_TRANG_BI:
//                                            case CombineServiceNew.CHUYEN_HOA_TRANG_BI:
                                            case CombineServiceNew.DOI_VE_HUY_DIET:
                                            case CombineServiceNew.DAP_SET_KICH_HOAT:
//                                            case CombineServiceNew.DOI_MANH_KICH_HOAT:
                                                if (select == 0) {
                                                    CombineServiceNew.gI().startCombine(player);
                                                }
                                                break;
                                        }
                                    }
                                } else if (this.mapId == 112) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                                                break;
                                        }
                                    }
                                } else if (this.mapId == 42 || this.mapId == 43 || this.mapId == 44) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0: //shop bùa
                                                createOtherMenu(player, ConstNpc.MENU_OPTION_SHOP_BUA,
                                                        "Bùa của ta rất lợi hại, nhìn ngươi yếu đuối thế này, chắc muốn mua bùa để "
                                                        + "mạnh mẽ à, mua không ta bán cho, xài rồi lại thích cho mà xem.",
                                                        "Bùa\n1 giờ", "Bùa\n8 giờ", "Bùa\n1 tháng", "Đóng");
                                                break;
                                            case 1:
//                                                CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_TRANG_BI);
                                                CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_VAT_PHAM);
                                                break;
                                            case 2: //nâng cấp bông tai
                                                CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_BONG_TAI);
                                                break;
                                            case 3: //làm phép nhập đá
                                                break;
                                            case 4:
//                                                CombineService.gI().openTabCombine(player, CombineService.NHAP_NGOC_RONG);
                                                CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NHAP_NGOC_RONG);
                                                break;
                                            case 5:
                                                CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.MO_CHI_SO_BONG_TAI);
                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_SHOP_BUA) {
                                        switch (select) {
                                            case 0:
                                                ShopService.gI().openShopBua(player, ConstNpc.SHOP_BA_HAT_MIT_0, 0);
                                                break;
                                            case 1:
                                                ShopService.gI().openShopBua(player, ConstNpc.SHOP_BA_HAT_MIT_1, 1);
                                                break;
                                            case 2:
                                                ShopService.gI().openShopBua(player, ConstNpc.SHOP_BA_HAT_MIT_2, 2);
                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                                        switch (player.combineNew.typeCombine) {
                                            case CombineServiceNew.NANG_CAP_VAT_PHAM:
                                            case CombineServiceNew.NANG_CAP_BONG_TAI:
                                            case CombineServiceNew.LAM_PHEP_NHAP_DA:
                                            case CombineServiceNew.NHAP_NGOC_RONG:
                                                if (select == 0) {
                                                    CombineServiceNew.gI().startCombine(player);
                                                }
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.RUONG_DO:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {

                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                InventoryService.gI().sendItemBox(player);
                                InventoryService.gI().openBox(player);
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {

                            }
                        }
                    };
                    break;
                case ConstNpc.DAU_THAN:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                player.magicTree.openMenuTree();
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                TaskService.gI().checkDoneTaskConfirmMenuNpc(player, this, (byte) select);
                                switch (player.iDMark.getIndexMenu()) {
                                    case ConstNpc.MAGIC_TREE_NON_UPGRADE_LEFT_PEA:
                                        if (select == 0) {
                                            player.magicTree.harvestPea();
                                        } else if (select == 1) {
                                            if (player.magicTree.level == 10) {
                                                player.magicTree.fastRespawnPea();
                                            } else {
                                                player.magicTree.showConfirmUpgradeMagicTree();
                                            }
                                        } else if (select == 2) {
                                            player.magicTree.fastRespawnPea();
                                        }
                                        break;
                                    case ConstNpc.MAGIC_TREE_NON_UPGRADE_FULL_PEA:
                                        if (select == 0) {
                                            player.magicTree.harvestPea();
                                        } else if (select == 1) {
                                            player.magicTree.showConfirmUpgradeMagicTree();
                                        }
                                        break;
                                    case ConstNpc.MAGIC_TREE_CONFIRM_UPGRADE:
                                        if (select == 0) {
                                            player.magicTree.upgradeMagicTree();
                                        }
                                        break;
                                    case ConstNpc.MAGIC_TREE_UPGRADE:
                                        if (select == 0) {
                                            player.magicTree.fastUpgradeMagicTree();
                                        } else if (select == 1) {
                                            player.magicTree.showConfirmUnuppgradeMagicTree();
                                        }
                                        break;
                                    case ConstNpc.MAGIC_TREE_CONFIRM_UNUPGRADE:
                                        if (select == 0) {
                                            player.magicTree.unupgradeMagicTree();
                                        }
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.CALICK:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        private final byte COUNT_CHANGE = 50;
                        private int count;

                        private void changeMap() {
                            if (this.mapId != 102) {
                                count++;
                                if (this.count >= COUNT_CHANGE) {
                                    count = 0;
                                    this.map.npcs.remove(this);
                                    Map map = MapService.gI().getMapForCalich();
                                    this.mapId = map.mapId;
                                    this.cx = Util.nextInt(100, map.mapWidth - 100);
                                    this.cy = map.yPhysicInTop(this.cx, 0);
                                    this.map = map;
                                    this.map.npcs.add(this);
                                }
                            }
                        }

                        @Override
                        public void openBaseMenu(Player player) {
                            player.iDMark.setIndexMenu(ConstNpc.BASE_MENU);
                            if (TaskService.gI().getIdTask(player) < ConstTask.TASK_20_0) {
                                Service.getInstance().hideWaitDialog(player);
                                Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                                return;
                            }
                            if (this.mapId != player.zone.map.mapId) {
                                Service.getInstance().sendThongBao(player, "Calích đã rời khỏi map!");
                                Service.getInstance().hideWaitDialog(player);
                                return;
                            }

                            if (this.mapId == 102) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "Chào chú, cháu có thể giúp gì?",
                                        "Kể\nChuyện", "Quay về\nQuá khứ");
                            } else {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "Chào chú, cháu có thể giúp gì?", "Kể\nChuyện", "Đi đến\nTương lai", "Từ chối");
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (this.mapId == 102) {
                                if (player.iDMark.isBaseMenu()) {
                                    if (select == 0) {
                                        //kể chuyện
                                        NpcService.gI().createTutorial(player, this.avartar, ConstNpc.CALICK_KE_CHUYEN);
                                    } else if (select == 1) {
                                        //về quá khứ
                                        ChangeMapService.gI().goToQuaKhu(player);
                                    }
                                }
                            } else if (player.iDMark.isBaseMenu()) {
                                if (select == 0) {
                                    //kể chuyện
                                    NpcService.gI().createTutorial(player, this.avartar, ConstNpc.CALICK_KE_CHUYEN);
                                } else if (select == 1) {
                                    //đến tương lai
//                                    changeMap();
                                    if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_20_0) {
                                        ChangeMapService.gI().goToTuongLai(player);
                                    }
                                } else {
                                    Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.JACO:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 24) {
                                    if (player.iDMark.isBaseMenu()) {
                                        if (select == 0) {
                                            //đến potaufeu
                                            ChangeMapService.gI().goToPotaufeu(player);
                                        }
                                    }
                                } else if (this.mapId == 139) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 24, -1, -1);
                                                break;
                                            case 1:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 25, -1, -1);
                                                break;
                                            case 2:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 26, -1, -1);
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.THUONG_DE:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {

                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 45) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Con muốn làm gì nào", "Đến Kaio", "Quay số\nmay mắn");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 45) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 48, -1, 354);
                                                break;
                                            case 1:
                                                this.createOtherMenu(player, ConstNpc.MENU_CHOOSE_LUCKY_ROUND,
                                                        "Con muốn làm gì nào?", "Quay bằng\nvàng",
                                                        "Rương phụ\n("
                                                        + (player.inventory.itemsBoxCrackBall.size()
                                                        - InventoryService.gI().getCountEmptyListItem(player.inventory.itemsBoxCrackBall))
                                                        + " món)",
                                                        "Xóa hết\ntrong rương", "Đóng");
                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_CHOOSE_LUCKY_ROUND) {
                                        switch (select) {
                                            case 0:
                                                LuckyRound.gI().openCrackBallUI(player, LuckyRound.USING_GOLD);
                                                break;
                                            case 1:
                                                ShopService.gI().openBoxItemLuckyRound(player);
                                                break;
                                            case 2:
                                                NpcService.gI().createMenuConMeo(player,
                                                        ConstNpc.CONFIRM_REMOVE_ALL_ITEM_LUCKY_ROUND, this.avartar,
                                                        "Con có chắc muốn xóa hết vật phẩm trong rương phụ? Sau khi xóa "
                                                        + "sẽ không thể khôi phục!",
                                                        "Đồng ý", "Hủy bỏ");
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    };

                    break;
                case ConstNpc.THAN_VU_TRU:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 48) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Con muốn làm gì nào", "Di chuyển");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 48) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                this.createOtherMenu(player, ConstNpc.MENU_DI_CHUYEN,
                                                        "Con muốn đi đâu?", "Về\nthần điện", "Thánh địa\nKaio", "Con\nđường\nrắn độc", "Từ chối");
                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_DI_CHUYEN) {
                                        switch (select) {
                                            case 0:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 45, -1, 354);
                                                break;
                                            case 1:
                                                ChangeMapService.gI().changeMap(player, 50, -1, 318, 336);
                                                break;
                                            case 2:
                                                //con đường rắn độc
                                                break;
                                        }
                                    }
                                }
                            }
                        }

                    };
                    break;
                case ConstNpc.KIBIT:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 50) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                            "Đến\nKaio", "Từ chối");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 50) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                ChangeMapService.gI().changeMap(player, 48, -1, 354, 240);
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.OSIN:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 50) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                            "Đến\nKaio", "Đến\nhành tinh\nBill", "Từ chối");
                                } else if (this.mapId == 154) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                            "Về thánh địa", "Đến\nhành tinh\nngục tù", "Từ chối");
                                } else if (this.mapId == 155) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                            "Quay về", "Từ chối");
                                } else if (this.mapId == 52) {
                                    try {
                                        MapMaBu.gI().setTimeJoinMapMaBu();
                                        if (this.mapId == 52) {
                                            long now = System.currentTimeMillis();
                                            if (now > MapMaBu.TIME_OPEN_MABU && now < MapMaBu.TIME_CLOSE_MABU) {
                                                this.createOtherMenu(player, ConstNpc.MENU_OPEN_MMB, "Đại chiến Ma Bư đã mở, "
                                                        + "ngươi có muốn tham gia không?",
                                                        "Hướng dẫn\nthêm", "Tham gia", "Từ chối");
                                            } else {
                                                this.createOtherMenu(player, ConstNpc.MENU_NOT_OPEN_MMB,
                                                        "Ta có thể giúp gì cho ngươi?", "Hướng dẫn", "Từ chối");
                                            }

                                        }
                                    } catch (Exception ex) {
                                        Logger.error("Lỗi mở menu osin");
                                    }
                                } else if (this.mapId >= 114 && this.mapId < 120 && this.mapId != 116) {
                                    if (player.fightMabu.pointMabu >= player.fightMabu.POINT_MAX) {
                                        this.createOtherMenu(player, ConstNpc.GO_UPSTAIRS_MENU, "Ta có thể giúp gì cho ngươi ?",
                                                "Lên Tầng!", "Quay về", "Từ chối");
                                    } else {
                                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                                "Quay về", "Từ chối");
                                    }
                                } else if (this.mapId == 120) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                            "Quay về", "Từ chối");
                                } else {
                                    super.openBaseMenu(player);
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 50) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                ChangeMapService.gI().changeMap(player, 48, -1, 354, 240);
                                                break;
                                            case 1:
                                                ChangeMapService.gI().changeMap(player, 154, -1, 200, 312);
                                                break;
                                        }
                                    }
                                } else if (this.mapId == 154) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                ChangeMapService.gI().changeMap(player, 50, -1, 318, 336);
                                                break;
                                            case 1:
                                                ChangeMapService.gI().changeMap(player, 155, -1, 111, 792);
                                                break;
                                        }
                                    }
                                } else if (this.mapId == 155) {
                                    if (player.iDMark.isBaseMenu()) {
                                        if (select == 0) {
                                            ChangeMapService.gI().changeMap(player, 154, -1, 200, 312);
                                        }
                                    }
                                } else if (this.mapId == 52) {
                                    switch (player.iDMark.getIndexMenu()) {
                                        case ConstNpc.MENU_REWARD_MMB:
                                            break;
                                        case ConstNpc.MENU_OPEN_MMB:
                                            if (select == 0) {
                                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_MAP_MA_BU);
                                            } else if (select == 1) {
//                                    if (!player.getSession().actived) {
//                                        Service.getInstance().sendThongBao(player, "Vui lòng kích hoạt tài khoản để sử dụng chức năng này");
//                                    } else
                                                ChangeMapService.gI().changeMap(player, 114, -1, 318, 336);
                                            }
                                            break;
                                        case ConstNpc.MENU_NOT_OPEN_BDW:
                                            if (select == 0) {
                                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_MAP_MA_BU);
                                            }
                                            break;
                                    }
                                } else if (this.mapId >= 114 && this.mapId < 120 && this.mapId != 116) {
                                    if (player.iDMark.getIndexMenu() == ConstNpc.GO_UPSTAIRS_MENU) {
                                        if (select == 0) {
                                            player.fightMabu.clear();
                                            ChangeMapService.gI().changeMap(player, this.map.mapIdNextMabu((short) this.mapId), -1, this.cx, this.cy);
                                        } else if (select == 1) {
                                            ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, 0, -1);
                                        }
                                    } else {
                                        if (select == 0) {
                                            ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, 0, -1);
                                        }
                                    }
                                } else if (this.mapId == 120) {
                                    if (player.iDMark.getIndexMenu() == ConstNpc.BASE_MENU) {
                                        if (select == 0) {
                                            ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, 0, -1);
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.LINH_CANH:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (player.clan == null) {
                                    this.createOtherMenu(player, ConstNpc.MENU_KHONG_CHO_VAO_DT,
                                            "Chỉ tiếp các bang hội, miễn tiếp khách vãng lai", "Đóng");
                                } else if (player.clan.getMembers().size() < 0) {
//                                } else if (player.clan.getMembers().size() < 1) {
                                    this.createOtherMenu(player, ConstNpc.MENU_KHONG_CHO_VAO_DT,
                                            "Bang hội phải có ít nhất 5 thành viên mới có thể mở", "Đóng");
                                } else {
                                    if (!player.clan.haveGoneDoanhTrai && player.clan.timeOpenDoanhTrai != 0) {
                                        createOtherMenu(player, ConstNpc.MENU_VAO_DT,
                                                "Bang hội của ngươi đang đánh trại độc nhãn\n"
                                                + "Thời gian còn lại là "
                                                + TimeUtil.getSecondLeft(player.clan.timeOpenDoanhTrai, DoanhTrai.TIME_DOANH_TRAI / 1000)
                                                + ". Ngươi có muốn tham gia không?",
                                                "Tham gia", "Không", "Hướng\ndẫn\nthêm");
                                    } else {
                                        List<Player> plSameClans = new ArrayList<>();
                                        List<Player> playersMap = player.zone.getPlayers();
                                        for (Player pl : playersMap) {
                                            if (!pl.equals(player) && pl.clan != null
                                                    && pl.clan.id == player.clan.id && pl.location.x >= 1285
                                                    && pl.location.x <= 1645) {
                                                plSameClans.add(pl);
                                            }

                                        }
//                                        if (plSameClans.size() >= 0) {
                                        if (plSameClans.size() >= 0) {
                                            if (!player.isAdmin()
                                                    && player.clanMember.getNumDateFromJoinTimeToToday() < DoanhTrai.DATE_WAIT_FROM_JOIN_CLAN) {
                                                createOtherMenu(player, ConstNpc.MENU_KHONG_CHO_VAO_DT,
                                                        "Bang hội chỉ cho phép những người ở trong bang trên 1 ngày. Hẹn ngươi quay lại vào lúc khác",
                                                        "OK", "Hướng\ndẫn\nthêm");
                                            } else if (player.clan.haveGoneDoanhTrai) {
                                                createOtherMenu(player, ConstNpc.MENU_KHONG_CHO_VAO_DT,
                                                        "Bang hội của ngươi đã đi trại lúc " + Util.formatTime(player.clan.timeOpenDoanhTrai) + " hôm nay. Người mở\n"
                                                        + "(" + player.clan.playerOpenDoanhTrai.name + "). Hẹn ngươi quay lại vào ngày mai", "OK", "Hướng\ndẫn\nthêm");

                                            } else {
                                                createOtherMenu(player, ConstNpc.MENU_CHO_VAO_DT,
                                                        "Hôm nay bang hội của ngươi chưa vào trại lần nào. Ngươi có muốn vào\n"
                                                        + "không?\nĐể vào, ta khuyên ngươi nên có 3-4 người cùng bang đi cùng",
                                                        "Vào\n(miễn phí)", "Không", "Hướng\ndẫn\nthêm");
                                            }
                                        } else {
                                            createOtherMenu(player, ConstNpc.MENU_KHONG_CHO_VAO_DT,
                                                    "Ngươi phải có ít nhất 2 đồng đội cùng bang đứng gần mới có thể\nvào\n"
                                                    + "tuy nhiên ta khuyên ngươi nên đi cùng với 3-4 người để khỏi chết.\n"
                                                    + "Hahaha.", "OK", "Hướng\ndẫn\nthêm");
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 27) {
                                    switch (player.iDMark.getIndexMenu()) {
                                        case ConstNpc.MENU_KHONG_CHO_VAO_DT:
                                            if (select == 1) {
                                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_DOANH_TRAI);
                                            }
                                            break;
                                        case ConstNpc.MENU_CHO_VAO_DT:
                                            switch (select) {
                                                case 0:
                                                    DoanhTraiService.gI().openDoanhTrai(player);
                                                    break;
                                                case 2:
                                                    NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_DOANH_TRAI);
                                                    break;
                                            }
                                            break;
                                        case ConstNpc.MENU_VAO_DT:
                                            switch (select) {
                                                case 0:
                                                    ChangeMapService.gI().changeMap(player, 53, 0, 35, 432);
                                                    break;
                                                case 2:
                                                    NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_DOANH_TRAI);
                                                    break;
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.QUA_TRUNG:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {

                        private final int COST_AP_TRUNG_NHANH = 1000000000;

                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                player.mabuEgg.sendMabuEgg();
                                if (player.mabuEgg.getSecondDone() != 0) {
                                    this.createOtherMenu(player, ConstNpc.CAN_NOT_OPEN_EGG, "Bư bư bư...",
                                            "Hủy bỏ\ntrứng", "Ấp nhanh\n" + Util.numberToMoney(COST_AP_TRUNG_NHANH) + " vàng", "Đóng");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.CAN_OPEN_EGG, "Bư bư bư...", "Nở", "Hủy bỏ\ntrứng", "Đóng");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                switch (player.iDMark.getIndexMenu()) {
                                    case ConstNpc.CAN_NOT_OPEN_EGG:
                                        if (select == 0) {
                                            this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                                                    "Bạn có chắc chắn muốn hủy bỏ trứng Mabư?", "Đồng ý", "Từ chối");
                                        } else if (select == 1) {
                                            if (player.inventory.gold >= COST_AP_TRUNG_NHANH) {
                                                player.inventory.gold -= COST_AP_TRUNG_NHANH;
                                                player.mabuEgg.timeDone = 0;
                                                Service.getInstance().sendMoney(player);
                                                player.mabuEgg.sendMabuEgg();
                                            } else {
                                                Service.getInstance().sendThongBao(player,
                                                        "Bạn không đủ vàng để thực hiện, còn thiếu "
                                                        + Util.numberToMoney((COST_AP_TRUNG_NHANH - player.inventory.gold)) + " vàng");
                                            }
                                        }
                                        break;
                                    case ConstNpc.CAN_OPEN_EGG:
                                        switch (select) {
                                            case 0:
                                                this.createOtherMenu(player, ConstNpc.CONFIRM_OPEN_EGG,
                                                        "Bạn có chắc chắn cho trứng nở?\n"
                                                        + "Đệ tử của bạn sẽ được thay thế bằng đệ Mabư",
                                                        "Đệ mabư\nTrái Đất", "Đệ mabư\nNamếc", "Đệ mabư\nXayda", "Từ chối");
                                                break;
                                            case 1:
                                                this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                                                        "Bạn có chắc chắn muốn hủy bỏ trứng Mabư?", "Đồng ý", "Từ chối");
                                                break;
                                        }
                                        break;
                                    case ConstNpc.CONFIRM_OPEN_EGG:
                                        switch (select) {
                                            case 0:
                                                player.mabuEgg.openEgg(ConstPlayer.TRAI_DAT);
                                                break;
                                            case 1:
                                                player.mabuEgg.openEgg(ConstPlayer.NAMEC);
                                                break;
                                            case 2:
                                                player.mabuEgg.openEgg(ConstPlayer.XAYDA);
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                    case ConstNpc.CONFIRM_DESTROY_EGG:
                                        if (select == 0) {
                                            player.mabuEgg.destroyEgg();
                                        }
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.QUOC_VUONG:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {

                        @Override
                        public void openBaseMenu(Player player) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "Con muốn nâng giới hạn sức mạnh cho bản thân hay đệ tử?",
                                    "Bản thân", "Đệ tử", "Từ chối");
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:
                                            if (player.nPoint.limitPower < NPoint.MAX_LIMIT) {
                                                this.createOtherMenu(player, ConstNpc.OPEN_POWER_MYSEFT,
                                                        "Ta sẽ truền năng lượng giúp con mở giới hạn sức mạnh của bản thân lên "
                                                        + Util.numberToMoney(player.nPoint.getPowerNextLimit()),
                                                        "Nâng\ngiới hạn\nsức mạnh",
                                                        "Nâng ngay\n" + Util.numberToMoney(OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) + " vàng", "Đóng");
                                            } else {
                                                this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                                        "Sức mạnh của con đã đạt tới giới hạn",
                                                        "Đóng");
                                            }
                                            break;
                                        case 1:
                                            if (player.pet != null) {
                                                if (player.pet.nPoint.limitPower < NPoint.MAX_LIMIT) {
                                                    this.createOtherMenu(player, ConstNpc.OPEN_POWER_PET,
                                                            "Ta sẽ truền năng lượng giúp con mở giới hạn sức mạnh của đệ tử lên "
                                                            + Util.numberToMoney(player.pet.nPoint.getPowerNextLimit()),
                                                            "Nâng ngay\n" + Util.numberToMoney(OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) + " vàng", "Đóng");
                                                } else {
                                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                                            "Sức mạnh của đệ con đã đạt tới giới hạn",
                                                            "Đóng");
                                                }
                                            } else {
                                                Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                                            }
                                            //giới hạn đệ tử
                                            break;
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.OPEN_POWER_MYSEFT) {
                                    switch (select) {
                                        case 0:
                                            OpenPowerService.gI().openPowerBasic(player);
                                            break;
                                        case 1:
                                            if (player.inventory.gold >= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) {
                                                if (OpenPowerService.gI().openPowerSpeed(player)) {
                                                    player.inventory.gold -= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER;
                                                    Service.getInstance().sendMoney(player);
                                                }
                                            } else {
                                                Service.getInstance().sendThongBao(player,
                                                        "Bạn không đủ vàng để mở, còn thiếu "
                                                        + Util.numberToMoney((OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER - player.inventory.gold)) + " vàng");
                                            }
                                            break;
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.OPEN_POWER_PET) {
                                    if (select == 0) {
                                        if (player.inventory.gold >= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) {
                                            if (OpenPowerService.gI().openPowerSpeed(player.pet)) {
                                                player.inventory.gold -= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER;
                                                Service.getInstance().sendMoney(player);
                                            }
                                        } else {
                                            Service.getInstance().sendThongBao(player,
                                                    "Bạn không đủ vàng để mở, còn thiếu "
                                                    + Util.numberToMoney((OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER - player.inventory.gold)) + " vàng");
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.BUNMA_TL:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Cậu bé muốn mua gì nào?", "Cửa hàng", "Đóng");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    if (select == 0) {
                                        ShopService.gI().openShopNormal(player, this, ConstNpc.SHOP_BUNMA_TL_0, 0, player.gender);
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.RONG_OMEGA:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                BlackBallWar.gI().setTime();
                                if (this.mapId == 24 || this.mapId == 25 || this.mapId == 26) {
                                    try {
                                        long now = System.currentTimeMillis();
                                        if (now > BlackBallWar.TIME_OPEN && now < BlackBallWar.TIME_CLOSE) {
                                            this.createOtherMenu(player, ConstNpc.MENU_OPEN_BDW, "Đường đến với ngọc rồng sao đen đã mở, "
                                                    + "ngươi có muốn tham gia không?",
                                                    "Hướng dẫn\nthêm", "Tham gia", "Từ chối");
                                        } else {
                                            String[] optionRewards = new String[7];
                                            int index = 0;
                                            for (int i = 0; i < 7; i++) {
                                                if (player.rewardBlackBall.timeOutOfDateReward[i] > System.currentTimeMillis()) {
                                                    optionRewards[index] = "Nhận thưởng\n" + (i + 1) + " sao";
                                                    index++;
                                                }
                                            }
                                            if (index != 0) {
                                                String[] options = new String[index + 1];
                                                for (int i = 0; i < index; i++) {
                                                    options[i] = optionRewards[i];
                                                }
                                                options[options.length - 1] = "Từ chối";
                                                this.createOtherMenu(player, ConstNpc.MENU_REWARD_BDW, "Ngươi có một vài phần thưởng ngọc "
                                                        + "rồng sao đen đây!",
                                                        options);
                                            } else {
                                                this.createOtherMenu(player, ConstNpc.MENU_NOT_OPEN_BDW,
                                                        "Ta có thể giúp gì cho ngươi?", "Hướng dẫn", "Từ chối");
                                            }
                                        }
                                    } catch (Exception ex) {
                                        Logger.error("Lỗi mở menu rồng Omega");
                                    }
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                switch (player.iDMark.getIndexMenu()) {
                                    case ConstNpc.MENU_REWARD_BDW:
                                        player.rewardBlackBall.getRewardSelect((byte) select);
                                        break;
                                    case ConstNpc.MENU_OPEN_BDW:
                                        if (select == 0) {
                                            NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_BLACK_BALL_WAR);
                                        } else if (select == 1) {
                                            player.iDMark.setTypeChangeMap(ConstMap.CHANGE_BLACK_BALL);
                                            ChangeMapService.gI().openChangeMapTab(player);
                                        }
                                        break;
                                    case ConstNpc.MENU_NOT_OPEN_BDW:
                                        if (select == 0) {
                                            NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_BLACK_BALL_WAR);
                                        }
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.RONG_1S:
                case ConstNpc.RONG_2S:
                case ConstNpc.RONG_3S:
                case ConstNpc.RONG_4S:
                case ConstNpc.RONG_5S:
                case ConstNpc.RONG_6S:
                case ConstNpc.RONG_7S:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (player.isHoldBlackBall) {
                                    this.createOtherMenu(player, ConstNpc.MENU_PHU_HP, "Ta có thể giúp gì cho ngươi?", "Phù hộ", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.MENU_OPTION_GO_HOME, "Ta có thể giúp gì cho ngươi?", "Về nhà", "Từ chối");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.getIndexMenu() == ConstNpc.MENU_PHU_HP) {
                                    if (select == 0) {
                                        this.createOtherMenu(player, ConstNpc.MENU_OPTION_PHU_HP,
                                                "Ta sẽ giúp ngươi tăng HP lên mức kinh hoàng, ngươi chọn đi",
                                                "x3 HP\n" + Util.numberToMoney(BlackBallWar.COST_X3) + " vàng",
                                                "x5 HP\n" + Util.numberToMoney(BlackBallWar.COST_X5) + " vàng",
                                                "x7 HP\n" + Util.numberToMoney(BlackBallWar.COST_X7) + " vàng",
                                                "Từ chối"
                                        );
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_GO_HOME) {
                                    if (select == 0) {
                                        ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_PHU_HP) {
                                    switch (select) {
                                        case 0:
                                            BlackBallWar.gI().xHPKI(player, BlackBallWar.X3);
                                            break;
                                        case 1:
                                            BlackBallWar.gI().xHPKI(player, BlackBallWar.X5);
                                            break;
                                        case 2:
                                            BlackBallWar.gI().xHPKI(player, BlackBallWar.X7);
                                            break;
                                        case 3:
                                            this.npcChat(player, "Để ta xem ngươi trụ được bao lâu");
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.NPC_64:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi muốn xem thông tin gì?",
                                        "Top\nsức mạnh", "Đóng");
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    if (select == 0) {
                                        TopService.gI().showTopPower(player);
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.WHIS:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (this.mapId == 154) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "Thử đánh với ta xem nào.\nNgươi còn 1 lượt cơ mà.!",
                                        "Chế Tạo", "Học \nTuyệt kỹ", "Hướng dẫn");
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu() && this.mapId == 154) {
                                    switch (select) {
                                        case 0:
                                            this.createOtherMenu(player, 5,
                                                    "Ta sẽ giúp ngươi chế tạo trang bị thiên sứ", "Chế tạo", "Đóng");
                                            break;
                                        case 1:
                                            Item BiKiepTuyetKy = InventoryService.gI()
                                                    .findItem(player.inventory.itemsBag, 1125);
                                            if (BiKiepTuyetKy != null) {
                                                this.createOtherMenu(player, 6,
                                                        "Ta sẽ giúp ngươi học tuyệt kỹ: %skill \nBí kiếp tuyệt kỹ: "
                                                        + BiKiepTuyetKy.quantity
                                                        + "/999\nGiá vàng: 1.500.000.000\nGiá ngọc: 99999",
                                                        "Đồng ý\nHọc", "Từ chối");
                                            }
                                            break;
                                    }
                                } else if (player.iDMark.getIndexMenu() == 5) {
                                    switch (select) {
                                        case 0:
                                            CombineServiceNew.gI().openTabCombine(player,
                                                    CombineServiceNew.DAP_DO_THIEN_SU);
                                            break;
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_DAP_DO) {
                                    if (select == 0) {
                                        CombineServiceNew.gI().startCombine(player);
                                    } else if (player.iDMark.getIndexMenu() == 6) {
                                        switch (select) {
                                            case 0:
                                                Item BiKiepTuyetKy = InventoryService.gI()
                                                        .findItem(player.inventory.itemsBag, 1125);
                                                if (BiKiepTuyetKy.quantity >= 9999) {
                                                    switch (player.gender) {
                                                        //
                                                        // SkillService.gI().learSkillSpecial(player, Skill.SUPER_KAME);
                                                        // SkillService.gI().learSkillSpecial(player,
                                                        // Skill.MA_PHONG_BA);
                                                        // SkillService.gI().learSkillSpecial(player,
                                                        // Skill.LIEN_HOAN_CHUONG);
                                                        // InventoryService.gI().subQuantityItem(player.inventory.itemsBag,
                                                        // BiKipTuyetKi, 999);
                                                        // InventoryServiceNew.gI().sendItemBags(player);
                                                        // }
                                                        // Service.gI().sendThongBao(player, "Chưa có đủ bí kíp tuyệt
                                                        // kĩ");
                                                        // return;
                                                        case 0:
                                                            Service.gI().sendThongBao(player, "Trái đất lè");
                                                            break;
                                                        case 1:
                                                            Service.gI().sendThongBao(player, "Namek lè");
                                                            break;
                                                        case 2:
                                                            Service.gI().sendThongBao(player, "Xaday lè");
                                                            break;

                                                    }
                                                } else {
                                                    Service.gI().sendThongBao(player,
                                                            "Con không đủ bí kíp tuyệt kỹ , hãy luyện tập để mạnh hơn");
                                                    break;
                                                }
                                                break;

                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.BILL:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 48) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi muốn gì nào?", "Đổi đồ\nhủy diệt", "Đóng");
                                } else {
                                    super.openBaseMenu(player);
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                switch (this.mapId) {
                                    case 48:
                                        switch (player.iDMark.getIndexMenu()) {
                                            case ConstNpc.BASE_MENU:
                                                if (select == 0) {
                                                    if (player.setClothes.godClothes || true) {
                                                        ShopService.gI().openShopBillHuyDiet(player,
                                                                ConstNpc.SHOP_BILL_HUY_DIET_0, 0);
                                                    } else {
                                                        Service.getInstance().sendThongBao(player,
                                                                "Yêu cầu có đủ trang bị thần linh");
                                                    }
                                                }
                                                break;
                                        }
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.BO_MONG:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 47) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Xin chào, cậu muốn tôi giúp gì?", "Nhiệm vụ\nhàng ngày", "Từ chối");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 47) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                if (player.playerTask.sideTask.template != null) {
                                                    String npcSay = "Nhiệm vụ hiện tại: " + player.playerTask.sideTask.getName() + " ("
                                                            + player.playerTask.sideTask.getLevel() + ")"
                                                            + "\nHiện tại đã hoàn thành: " + player.playerTask.sideTask.count + "/"
                                                            + player.playerTask.sideTask.maxCount + " ("
                                                            + player.playerTask.sideTask.getPercentProcess() + "%)\nSố nhiệm vụ còn lại trong ngày: "
                                                            + player.playerTask.sideTask.leftTask + "/" + ConstTask.MAX_SIDE_TASK;
                                                    this.createOtherMenu(player, ConstNpc.MENU_OPTION_PAY_SIDE_TASK,
                                                            npcSay, "Trả nhiệm\nvụ", "Hủy nhiệm\nvụ");
                                                } else {
                                                    this.createOtherMenu(player, ConstNpc.MENU_OPTION_LEVEL_SIDE_TASK,
                                                            "Tôi có vài nhiệm vụ theo cấp bậc, "
                                                            + "sức cậu có thể làm được cái nào?",
                                                            "Dễ", "Bình thường", "Khó", "Siêu khó", "Từ chối");
                                                }
                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_LEVEL_SIDE_TASK) {
                                        switch (select) {
                                            case 0:
                                            case 1:
                                            case 2:
                                            case 3:
                                                TaskService.gI().changeSideTask(player, (byte) select);
                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_PAY_SIDE_TASK) {
                                        switch (select) {
                                            case 0:
                                                TaskService.gI().paySideTask(player);
                                                break;
                                            case 1:
                                                TaskService.gI().removeSideTask(player);
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.GOKU_SSJ:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 80) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Xin chào, tôi có thể giúp gì cho cậu?",
                                            "Tới hành tinh\nYardart", "Từ chối");
                                } else if (this.mapId == 131) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Xin chào, tôi có thể giúp gì cho cậu?",
                                            "Quay về", "Từ chối");
                                } else {
                                    super.openBaseMenu(player);
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                switch (player.iDMark.getIndexMenu()) {
                                    case ConstNpc.BASE_MENU:
                                        if (this.mapId == 80) {
//                                            if (select == 0) {
//                                                if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_24_0) {
//                                                    ChangeMapService.gI().changeMapBySpaceShip(player, 160, -1, 168);
//                                                } else {
//                                                    this.npcChat(player, "Xin lỗi, tôi chưa thể đưa cậu tới nơi đó lúc này...");
//                                                }
//                                            } else 
                                            if (select == 0) {
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 131, -1, 940);
                                            }
                                        } else if (this.mapId == 131) {
                                            if (select == 0) {
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 80, -1, 870);
                                            }
                                        }
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.GOKU_SSJ_:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 133) {
                                    Item biKiep = InventoryService.gI().findItem(player.inventory.itemsBag, 590);
                                    int soLuong = 0;
                                    if (biKiep != null) {
                                        soLuong = biKiep.quantity;
                                    }
                                    if (soLuong >= 10000) {
                                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Bạn đang có " + soLuong + " bí kiếp.\n"
                                                + "Hãy kiếm đủ 10000 bí kiếp tôi sẽ dạy bạn cách dịch chuyển tức thời của người Yardart", "Học dịch\nchuyển", "Đóng");
                                    } else {
                                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Bạn đang có " + soLuong + " bí kiếp.\n"
                                                + "Hãy kiếm đủ 10000 bí kiếp tôi sẽ dạy bạn cách dịch chuyển tức thời của người Yardart", "Đóng");
                                    }
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 133) {
                                    Item biKiep = InventoryService.gI().findItem(player.inventory.itemsBag, 590);
                                    int soLuong = 0;
                                    if (biKiep != null) {
                                        soLuong = biKiep.quantity;
                                    }
                                    if (soLuong >= 10000 && InventoryService.gI().getCountEmptyBag(player) > 0) {
                                        Item yardart = ItemService.gI().createNewItem((short) (player.gender + 592));
                                        yardart.itemOptions.add(new Item.ItemOption(47, 400));
                                        yardart.itemOptions.add(new Item.ItemOption(108, 10));
                                        InventoryService.gI().addItemBag(player, yardart, false);
                                        InventoryService.gI().subQuantityItemsBag(player, biKiep, 10000);
                                        InventoryService.gI().sendItemBags(player);
                                        Service.getInstance().sendThongBao(player, "Bạn vừa nhận được trang phục tộc Yardart");
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.GHI_DANH:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player pl) {

                            if (mapId == 52) {
                                createOtherMenu(pl, 0, "Chiến đến giọt máu cuối cùng", "OK",
                                        "Đại Hội\nVõ Thuật\nLần thứ\n23");
                            } else if (mapId == 129) {
                                String[] menus;
                                if (pl.DaiHoiVoThuat.step == 11) {
                                    menus = new String[]{
                                        "Hướng\ndẫn\nthêm",
                                        "Nhận\nthưởng\nRương cấp " + pl.DaiHoiVoThuat.step,
                                        "Về\nĐại Hội\nVõ Thuật"
                                    };
                                } else if (pl.DaiHoiVoThuat.step < 11
                                        && pl.DaiHoiVoThuat.step > 0
                                        && !pl.DaiHoiVoThuat.isDrop) {
                                    menus = new String[]{
                                        "Hướng\ndẫn\nthêm",
                                        pl.DaiHoiVoThuat.isStart ? "Hủy\nđăng ký"
                                        : "Thi đấu\n"
                                        + Util.powerToString(pl.DaiHoiVoThuat.die * 50000)
                                        + "\nvàng",
                                        "Nhận\nthưởng\nRương cấp " + pl.DaiHoiVoThuat.step,
                                        "Về\nĐại Hội\nVõ Thuật"
                                    };
                                } else if (pl.DaiHoiVoThuat.step < 11
                                        && pl.DaiHoiVoThuat.step >= 0
                                        && !pl.DaiHoiVoThuat.isDrop) {
                                    menus = new String[]{
                                        "Hướng\ndẫn\nthêm",
                                        pl.DaiHoiVoThuat.isStart ? "Hủy\nđăng ký"
                                        : "Thi đấu\n"
                                        + Util.powerToString(pl.DaiHoiVoThuat.die * 50000)
                                        + "\nvàng",
                                        "Về\nĐại Hội\nVõ Thuật"
                                    };
                                } else {
                                    menus = new String[]{"Hướng\ndẫn\nthêm", "Về\nĐại Hội\nVõ Thuật"};
                                }
                                createOtherMenu(pl, 0, ""
                                        + "Đại hội võ thuật lần thứ 23\n"
                                        + "Diễn ra bất kể ngày đêm, ngày nghỉ, ngày lễ\n"
                                        + "Phần thưởng vô cùng quý giá\n"
                                        + "Nhanh chóng tham gia nào", menus);
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (mapId == 52) {
                                switch (select) {
                                    case 1:
                                        ChangeMapService.gI().changeMap(player, 129, -1, 300, 360);
                                        break;
                                }
                            } else if (mapId == 129) {
                                switch (select) {
                                    // Hướng dẫn
                                    case 0:
                                        Service.getInstance().sendThongBao(player,
                                                "Chiến thắng cuối cùng và nhận rương gõ\nchứa nhiều phần quà hấp hẫn");
                                        break;
                                    case 1:
                                        // chưa mở rương
                                        if (player.DaiHoiVoThuat.isDrop) {
                                            ChangeMapService.gI().changeMap(player, 52, -1, 300, 336);
                                            break;
                                        }
                                        // Đã đăng ký thì hủy đăng ký
                                        if (player.DaiHoiVoThuat.isStart) {
                                            player.DaiHoiVoThuat.close();
                                            break;
                                        }
                                        if (player.DaiHoiVoThuat.step == 11) {
                                            Item ruonggo = ItemService.gI().createNewItem((short) 570);
                                            ruonggo.itemOptions
                                                    .add(new Item.ItemOption(72, player.DaiHoiVoThuat.step));
                                            ruonggo.itemOptions
                                                    .add(new Item.ItemOption(73,
                                                            (int) (System.currentTimeMillis() + 86400000)));
                                            InventoryService.gI().addItemBag(player, ruonggo, false);
                                            InventoryService.gI().sendItemBags(player);
                                            Service.getInstance().sendThongBao(player, "Bạn đã nhận được rương gỗ cấp "
                                                    + player.DaiHoiVoThuat.step + ".");
                                            player.DaiHoiVoThuat.isDrop = true;
                                            break;
                                        }

                                        // Thi đấu
                                        int Gold = player.DaiHoiVoThuat.die * 50000;
                                        if (player.inventory.gold >= Gold) {
                                            player.inventory.gold -= Gold;
                                            Service.getInstance().sendMoney(player);
                                            if (player.DaiHoiVoThuat.next == 0) {
                                                player.DaiHoiVoThuat.Start();
                                            }
                                        } else {
                                            Service.getInstance().sendThongBao(player,
                                                    "Bạn còn thiếu " + (Gold - player.inventory.gold)
                                                    + "vàng để thi đấu.");
                                        }
                                        break;
                                    case 2:
                                        // Nhận rương < 11z
                                        if (player.DaiHoiVoThuat.step == 11
                                                || player.DaiHoiVoThuat.step == 0
                                                || player.DaiHoiVoThuat.isDrop) {
                                            ChangeMapService.gI().changeMap(player, 52, -1, 300, 336);
                                        } else {
                                            Item ruonggo = ItemService.gI().createNewItem((short) 570);
                                            ruonggo.itemOptions
                                                    .add(new Item.ItemOption(72, player.DaiHoiVoThuat.step));
                                            ruonggo.itemOptions
                                                    .add(new Item.ItemOption(73,
                                                            (int) (System.currentTimeMillis() + 86400000)));
                                            InventoryService.gI().addItemBag(player, ruonggo, false);
                                            InventoryService.gI().sendItemBags(player);
                                            Service.getInstance().sendThongBao(player, "Bạn đã nhận được rương gỗ cấp "
                                                    + player.DaiHoiVoThuat.step + ".");
                                            player.DaiHoiVoThuat.isDrop = true;
                                        }
                                        break;
                                    case 3:
                                        ChangeMapService.gI().changeMap(player, 52, -1, 300, 336);
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.DUONG_TANG:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 0) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Ai mi phò phò,thí chủ hay giúp ta giải cứu đồ đệ của bần tăng đang bị phong ấn tại ngũ hành sơn ?",
                                            "Đồng Ý", "Từ chối", "Nhận thưởng");
                                } else if (this.mapId == 123) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                            "Về Làng Aru", "Từ chối");
                                } else {
                                    super.openBaseMenu(player);
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 0) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                ChangeMapService.gI().changeMap(player, 123, -1, 35, 384);
                                                break;
                                        }
                                    }
                                } else if (this.mapId == 123) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                ChangeMapService.gI().changeMap(player, 0, -1, 584, 432);
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.LY_TIEU_NUONG:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Cậu bé muốn mua gì nào?",
                                            "Cửa hàng", "Đóng");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    if (select == 0) {
                                        ShopService.gI().openShopThoiVang(player,
                                                ConstNpc.SHOP_THOI_VANG, 0);
                                    }
                                }
                            }
                        }
                    };
                    break;
                default:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                super.openBaseMenu(player);
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
//                                ShopService.gI().openShopNormal(player, this, ConstNpc.SHOP_BUNMA_TL_0, 0, player.gender);
                            }
                        }
                    };
            }
        } catch (Exception e) {
            Logger.logException(NpcFactory.class, e, "Lỗi load npc");
        }
        return npc;
    }

    //girlkun75-mark
    public static void createNpcRongThieng() {
        Npc npc = new Npc(-1, -1, -1, -1, ConstNpc.RONG_THIENG, -1) {
            @Override
            public void confirmMenu(Player player, int select) {
                switch (player.iDMark.getIndexMenu()) {
                    case ConstNpc.IGNORE_MENU:

                        break;
                    case ConstNpc.SHENRON_CONFIRM:
                        if (select == 0) {
                            SummonDragon.gI().confirmWish();
                        } else if (select == 1) {
                            SummonDragon.gI().reOpenShenronWishes(player);
                        }
                        break;
                    case ConstNpc.SHENRON_1_1:
                        if (player.iDMark.getIndexMenu() == ConstNpc.SHENRON_1_1 && select == SHENRON_1_STAR_WISHES_1.length - 1) {
                            NpcService.gI().createMenuRongThieng(player, ConstNpc.SHENRON_1_2, SHENRON_SAY, SHENRON_1_STAR_WISHES_2);
                            break;
                        }
                    case ConstNpc.SHENRON_1_2:
                        if (player.iDMark.getIndexMenu() == ConstNpc.SHENRON_1_2 && select == SHENRON_1_STAR_WISHES_2.length - 1) {
                            NpcService.gI().createMenuRongThieng(player, ConstNpc.SHENRON_1_1, SHENRON_SAY, SHENRON_1_STAR_WISHES_1);
                            break;
                        }
                    default:
                        SummonDragon.gI().showConfirmShenron(player, player.iDMark.getIndexMenu(), (byte) select);
                        break;
                }
            }
        };
    }

    public static void createNpcConMeo() {
        Npc npc = new Npc(-1, -1, -1, -1, ConstNpc.CON_MEO, 351) {
            @Override
            public void confirmMenu(Player player, int select) {
                switch (player.iDMark.getIndexMenu()) {
                    case ConstNpc.IGNORE_MENU:

                        break;
                    case ConstNpc.MAKE_MATCH_PVP:
//                        PVP_old.gI().sendInvitePVP(player, (byte) select);
                        PVPServcice.gI().sendInvitePVP(player, (byte) select);
                        break;
                    case ConstNpc.MAKE_FRIEND:
                        if (select == 0) {
                            Object playerId = PLAYERID_OBJECT.get(player.id);
                            if (playerId != null) {
                                FriendAndEnemyService.gI().acceptMakeFriend(player,
                                        Integer.parseInt(String.valueOf(playerId)));
                            }
                        }
                        break;
                    case ConstNpc.REVENGE:
                        if (select == 0) {
                            PVPServcice.gI().acceptRevenge(player);
                        }
                        break;
                    case ConstNpc.TUTORIAL_SUMMON_DRAGON:
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, -1, SummonDragon.SUMMON_SHENRON_TUTORIAL);
                        }
                        break;
                    case ConstNpc.SUMMON_SHENRON:
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, -1, SummonDragon.SUMMON_SHENRON_TUTORIAL);
                        } else if (select == 1) {
                            SummonDragon.gI().summonShenron(player);
                        }
                        break;

                    case ConstNpc.INTRINSIC:
                        if (select == 0) {
                            IntrinsicService.gI().showAllIntrinsic(player);
                        } else if (select == 1) {
                            IntrinsicService.gI().showConfirmOpen(player);
                        } else if (select == 2) {
                            IntrinsicService.gI().showConfirmOpenVip(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_OPEN_INTRINSIC:
                        if (select == 0) {
                            IntrinsicService.gI().open(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_OPEN_INTRINSIC_VIP:
                        if (select == 0) {
                            IntrinsicService.gI().openVip(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_LEAVE_CLAN:
                        if (select == 0) {
                            ClanService.gI().leaveClan(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_NHUONG_PC:
                        if (select == 0) {
                            ClanService.gI().phongPc(player, (int) PLAYERID_OBJECT.get(player.id));
                        }
                        break;
                    case ConstNpc.BAN_PLAYER:
                        if (select == 0) {
                            PlayerService.gI().banPlayer((Player) PLAYERID_OBJECT.get(player.id));
                            Service.getInstance().sendThongBao(player, "Ban người chơi " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " thành công");
                        }
                        break;
                    case ConstNpc.BUFF_PET:
                        if (select == 0) {
                            Player pl = (Player) PLAYERID_OBJECT.get(player.id);
                            if (pl.pet == null) {
                                PetService.gI().createNormalPet(pl);
                                Service.getInstance().sendThongBao(player, "Phát đệ tử cho " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " thành công");
                            }
                        }
                        break;
                    case ConstNpc.MENU_ADMIN:
                        switch (select) {
                            case 0:
                                for (int i = 14; i <= 20; i++) {
                                    Item item = ItemService.gI().createNewItem((short) i);
                                    InventoryService.gI().addItemBag(player, item, false);
                                }
                                InventoryService.gI().sendItemBags(player);
                                break;
                            case 1:
                                if (player.pet == null) {
                                    PetService.gI().createNormalPet(player);
                                } else {
                                    if (player.pet.isMabu) {
                                        PetService.gI().changeNormalPet(player);
                                    } else {
                                        PetService.gI().changeMabuPet(player);
                                    }
                                }
                                break;
                            case 2:
//                                PlayerService.gI().baoTri();
                                Maintenance.gI().start(60);
                                break;
                            case 3:
                                Input.gI().createFormFindPlayer(player);
                                break;
                        }
                        break;
                    case ConstNpc.CONFIRM_REMOVE_ALL_ITEM_LUCKY_ROUND:
                        if (select == 0) {
                            for (int i = 0; i < player.inventory.itemsBoxCrackBall.size(); i++) {
                                player.inventory.itemsBoxCrackBall.set(i, ItemService.gI().createItemNull());
                            }
                            Service.getInstance().sendThongBao(player, "Đã xóa hết vật phẩm trong rương");
                        }
                        break;
                    case ConstNpc.MENU_FIND_PLAYER:
                        Player p = (Player) PLAYERID_OBJECT.get(player.id);
                        if (p != null) {
                            switch (select) {
                                case 0:
                                    if (p.zone != null) {
                                        ChangeMapService.gI().changeMapYardrat(player, p.zone, p.location.x, p.location.y);
                                    }
                                    break;
                                case 1:
                                    if (p.zone != null) {
                                        ChangeMapService.gI().changeMap(p, player.zone, player.location.x, player.location.y);
                                    }
                                    break;
                                case 2:
                                    if (p != null) {
                                        Input.gI().createFormChangeName(player, p);
                                    }
                                    break;
                                case 3:
                                    if (p != null) {
                                        String[] selects = new String[]{"Đồng ý", "Hủy"};
                                        NpcService.gI().createMenuConMeo(player, ConstNpc.BAN_PLAYER, -1,
                                                "Bạn có chắc chắn muốn ban " + p.name, selects, p);
                                    }
                                    break;
                            }
                        }
                        break;
                }
            }
        };
    }

}
