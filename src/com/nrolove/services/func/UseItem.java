package com.nrolove.services.func;

import com.nrolove.consts.ConstMap;
import com.nrolove.consts.ConstNpc;
import com.nrolove.consts.ConstPlayer;
import com.nrolove.models.item.Item;
import com.nrolove.models.item.Item.ItemOption;
import com.nrolove.models.map.Zone;
import com.nrolove.models.player.Inventory;
import com.nrolove.models.player.Player;
import com.nrolove.models.skill.Skill;
import com.nrolove.server.io.Message;
import com.nrolove.server.io.Session;
import com.nrolove.services.InventoryService;
import com.nrolove.services.ItemService;
import com.nrolove.services.ItemTimeService;
import com.nrolove.services.MapService;
import com.nrolove.services.NpcService;
import com.nrolove.services.PetService;
import com.nrolove.services.PlayerService;
import com.nrolove.services.Service;
import com.nrolove.services.TaskService;
import com.nrolove.utils.Logger;
import com.nrolove.utils.SkillUtil;
import com.nrolove.utils.Util;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 *
 */
public class UseItem {

    private static final int ITEM_BOX_TO_BODY_OR_BAG = 0;
    private static final int ITEM_BAG_TO_BOX = 1;
    private static final int ITEM_BODY_TO_BOX = 3;
    private static final int ITEM_BAG_TO_BODY = 4;
    private static final int ITEM_BODY_TO_BAG = 5;
    private static final int ITEM_BAG_TO_PET_BODY = 6;
    private static final int ITEM_BODY_PET_TO_BAG = 7;

    private static final byte DO_USE_ITEM = 0;
    private static final byte DO_THROW_ITEM = 1;
    private static final byte ACCEPT_THROW_ITEM = 2;
    private static final byte ACCEPT_USE_ITEM = 3;

    private static UseItem instance;

    private UseItem() {

    }

    public static UseItem gI() {
        if (instance == null) {
            instance = new UseItem();
        }
        return instance;
    }

    public void getItem(Session session, Message msg) {
        Player player = session.player;
        TransactionService.gI().cancelTrade(player);
        try {
            int type = msg.reader().readByte();
            int index = msg.reader().readByte();
            switch (type) {
                case ITEM_BOX_TO_BODY_OR_BAG:
                    InventoryService.gI().itemBoxToBodyOrBag(player, index);
                    TaskService.gI().checkDoneTaskGetItemBox(player);
                    break;
                case ITEM_BAG_TO_BOX:
                    InventoryService.gI().itemBagToBox(player, index);
                    break;
                case ITEM_BODY_TO_BOX:
                    InventoryService.gI().itemBodyToBox(player, index);
                    break;
                case ITEM_BAG_TO_BODY:
                    InventoryService.gI().itemBagToBody(player, index);
                    break;
                case ITEM_BODY_TO_BAG:
                    InventoryService.gI().itemBodyToBag(player, index);
                    break;
                case ITEM_BAG_TO_PET_BODY:
                    InventoryService.gI().itemBagToPetBody(player, index);
                    break;
                case ITEM_BODY_PET_TO_BAG:
                    InventoryService.gI().itemPetBodyToBag(player, index);
                    break;
            }
            player.setClothes.setup();
            if (player.pet != null) {
                player.pet.setClothes.setup();
            }
            player.setClanMember();
            Service.getInstance().point(player);
        } catch (Exception e) {
            Logger.logException(UseItem.class, e);

        }
    }

    public void doItem(Player player, Message _msg) {
        TransactionService.gI().cancelTrade(player);
        Message msg;
        try {
            byte type = _msg.reader().readByte();
            int where = _msg.reader().readByte();
            int index = _msg.reader().readByte();
            // System.out.println(type + " " + where + " " + index);
            switch (type) {
                case DO_USE_ITEM:
                    if (player != null && player.inventory != null) {
                        if (index != -1) {
                            Item item = player.inventory.itemsBag.get(index);
                            if (item.isNotNullItem()) {
                                if (item.template.type == 7) {
                                    msg = new Message(-43);
                                    msg.writer().writeByte(type);
                                    msg.writer().writeByte(where);
                                    msg.writer().writeByte(index);
                                    msg.writer().writeUTF("Bạn chắc chắn học "
                                            + player.inventory.itemsBag.get(index).template.name + "?");
                                    player.sendMessage(msg);
                                } else {
                                    UseItem.gI().useItem(player, item, index);
                                }
                            }
                        } else {
                            InventoryService.gI().eatPea(player);
                        }
                    }
                    break;
                case DO_THROW_ITEM:
                    if (!(player.zone.map.mapId == 21 || player.zone.map.mapId == 22 || player.zone.map.mapId == 23)) {
                        Item item = null;
                        if (where == 0) {
                            item = player.inventory.itemsBody.get(index);
                        } else {
                            item = player.inventory.itemsBag.get(index);
                        }
                        msg = new Message(-43);
                        msg.writer().writeByte(type);
                        msg.writer().writeByte(where);
                        msg.writer().writeByte(index);
                        msg.writer().writeUTF("Bạn chắc chắn muốn vứt " + item.template.name + "?");
                        player.sendMessage(msg);
                    } else {
                        Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                    }
                    break;
                case ACCEPT_THROW_ITEM:
                    InventoryService.gI().throwItem(player, where, index);
                    Service.getInstance().point(player);
                    InventoryService.gI().sendItemBags(player);
                    break;
                case ACCEPT_USE_ITEM:
                    UseItem.gI().useItem(player, player.inventory.itemsBag.get(index), index);
                    break;
            }
        } catch (Exception e) {
     //       Logger.logException(UseItem.class, e);
        }
    }

    private void useItem(Player pl, Item item, int indexBag) {
        if (item != null && item.template != null) {
            if (item.template.strRequire <= pl.nPoint.power) {
                if (item.template.type == 6) {
                    InventoryService.gI().eatPea(pl);
                } else {
                    switch (item.template.id) {
                        case 570: {
                            if (item.itemOptions.size() < 2) {
                                Service.getInstance().sendThongBaoOK(pl,
                                        "Phát hiện hack đồ báo cáo admin để không bị khóa tài khoản");
                                break;
                            }
                            if (item.itemOptions.get(1).param > (int) System.currentTimeMillis()) {
                                Service.getInstance().sendThongBaoOK(pl, "Một này mới được mở nhé haha.");
                                break;
                            }
                            Service.getInstance().sendThongBaoOK(pl, "Mở Rương");
                            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                            pl.DaiHoiVoThuat.step = 0;
                            pl.DaiHoiVoThuat.die = 1;
                            pl.DaiHoiVoThuat.isDrop = false;

                            break;
                        }
                        // khoi dau minipet///////
                        case 892:
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 882, 883, 884);
                            Service.getInstance().point(pl);
                            break;
                        case 893:
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 885, 886, 887);
                            Service.getInstance().point(pl);
                            break;
                        case 908:
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 891, 892, 893);
                            Service.getInstance().point(pl);
                            break;
                        case 909:
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 897, 898, 899);
                            Service.getInstance().point(pl);
                            break;
                        case 910:
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 894, 895, 896);
                            Service.getInstance().point(pl);
                            break;
                        case 916:
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 931, 932, 933);
                            Service.getInstance().point(pl);
                            break;
                        case 917:
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 928, 929, 930);
                            Service.getInstance().point(pl);
                            break;
                        case 918:
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 925, 926, 927);
                            Service.getInstance().point(pl);
                            break;
                        case 919:
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 934, 935, 936);
                            Service.getInstance().point(pl);
                            break;
                        case 936:
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 718, 719, 720);
                            Service.getInstance().point(pl);
                            break;
                        case 942:
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 966, 967, 968);
                            Service.getInstance().point(pl);
                            break;
                        case 943:
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 969, 970, 971);
                            Service.getInstance().point(pl);
                            break;
                        case 944:
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 972, 973, 974);
                            Service.getInstance().point(pl);
                            break;
                        case 1992:
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 409, 410, 411);
                            Service.getInstance().point(pl);
                            break;
                        case 1039:
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 1092, 1093, 1094);
                            Service.getInstance().point(pl);
                            break;
                        case 1040:
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 1089, 1090, 1091);
                            Service.getInstance().point(pl);
                            break;
                        case 1046:
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 95, 96, 97);
                            Service.getInstance().point(pl);
                            break;
                        // ket thuc minipet/////////////////////////////////////
                        case 2006: // phiếu cải trang hải tặc
                        case 2007: // phiếu cải trang hải tặc
                        case 2008: // phiếu cải trang hải tặc
                            openPhieuCaiTrangHaiTac(pl, item);
                            break;
                        case 736:
                            openSPL(pl, item);
                            break;
                        case 397:
                            Service.getInstance().sendThongBaoOK(pl, "Đồ này đã bị thu hồi không thể sử dụng.");
                            break;
                        case 398:// hop qua tet
                            openboxkichhoat(pl, item);
                            break;
                        case 342:
                        case 343:
                        case 344:
                        case 345:
                            if (pl.zone.items.stream().filter(it -> it != null && it.itemTemplate.type == 22)
                                    .count() < 5) {
                                Service.getInstance().DropVeTinh(pl, item, pl.zone, pl.location.x, pl.location.y);
                                InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                            } else {
                                Service.getInstance().sendThongBao(pl, "Đặt ít thôi con");
                            }
                            break;
                        case 2020: // phiếu cải trang 20/10
                            openbox2010(pl, item);
                            break;
                        case 211: // nho tím
                        case 212: // nho xanh
                            eatGrapes(pl, item);
                            break;
                        case 380: // cskb
                            openCSKB(pl, item);
                            break;
                        case 381: // cuồng nộ
                        case 382: // bổ huyết
                        case 383: // bổ khí
                        case 384: // giáp xên
                        case 385: // ẩn danh
                        case 379: // máy dò
                        case 663: // bánh pudding
                        case 664: // xúc xíc
                        case 665: // kem dâu
                        case 666: // mì ly
                        case 667: // sushi
                            useItemTime(pl, item);
                            break;
                        case 521: // tdlt
                            useTDLT(pl, item);
                            break;
                        case 454: // bông tai
                            UseItem.gI().usePorata(pl);
                            break;
                        case 193: // gói 10 viên capsule
                            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                        case 194: // capsule đặc biệt
                            openCapsuleUI(pl);
                            break;
                        case 401: // đổi đệ tử
                            changePet(pl, item);
                            break;
                        case 402: // sách nâng chiêu 1 đệ tử
                        case 403: // sách nâng chiêu 2 đệ tử
                        case 404: // sách nâng chiêu 3 đệ tử
                        case 759: // sách nâng chiêu 4 đệ tử
                            upSkillPet(pl, item);
                            break;
                        default:
                            switch (item.template.type) {
                                case 7: // sách học, nâng skill
                                    learnSkill(pl, item);
                                    break;
                                case 12: // ngọc rồng các loại
                                    controllerCallRongThan(pl, item);
                                    break;
                                case 11: // item bag
                                    InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                                    Service.getInstance().sendFlagBag(pl);
                                    break;
                                case 23: // thú cưỡi mới
                                case 24: // thú cưỡi cũ
                                    InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                                    break;
                                case 72: {
                                    InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                                    Service.gI().sendPetFollow(pl, (short) (item.template.iconID - 1));
                                    break;
                                }
                            }
                    }
                }
                InventoryService.gI().sendItemBags(pl);

            } else {
                Service.getInstance().sendThongBaoOK(pl, "Sức mạnh không đủ yêu cầu");
            }
        }
    }

    private void useItemChangeFlagBag(Player player, Item item) {
        switch (item.template.id) {
            case 865: // kiem Z
                if (!player.effectFlagBag.useKiemz) {
                    player.effectFlagBag.reset();
                    player.effectFlagBag.useKiemz = !player.effectFlagBag.useKiemz;
                } else {
                    player.effectFlagBag.reset();
                }
                break;
            case 994: // vỏ ốc
                break;
            case 995: // cây kem
                break;
            case 996: // cá heo
                break;
            case 997: // con diều
                break;
            case 998: // diều rồng
                break;
            case 999: // mèo mun
                if (!player.effectFlagBag.useMeoMun) {
                    player.effectFlagBag.reset();
                    player.effectFlagBag.useMeoMun = !player.effectFlagBag.useMeoMun;
                } else {
                    player.effectFlagBag.reset();
                }
                break;
            case 1000: // xiên cá
                if (!player.effectFlagBag.useXienCa) {
                    player.effectFlagBag.reset();
                    player.effectFlagBag.useXienCa = !player.effectFlagBag.useXienCa;
                } else {
                    player.effectFlagBag.reset();
                }
                break;
            case 1001: // phóng heo
                if (!player.effectFlagBag.usePhongHeo) {
                    player.effectFlagBag.reset();
                    player.effectFlagBag.usePhongHeo = !player.effectFlagBag.usePhongHeo;
                } else {
                    player.effectFlagBag.reset();
                }
                break;
        }
        Service.getInstance().point(player);
        Service.getInstance().sendFlagBag(player);
    }

    private void changePet(Player player, Item item) {
        if (player.pet != null) {
            int gender = player.pet.gender + 1;
            if (gender > 2) {
                gender = 0;
            }
            PetService.gI().changeNormalPet(player, gender);
            InventoryService.gI().subQuantityItemsBag(player, item, 1);
        } else {
            Service.getInstance().sendThongBao(player, "Không thể thực hiện");
        }
    }

    private void openSPL(Player player, Item item) {
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            short[] possibleItems = {441, 442, 443, 444, 445, 446, 447};
            byte selectedIndex = (byte) Util.nextInt(0, possibleItems.length - 2);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            Item newItem = ItemService.gI().createNewItem(possibleItems[selectedIndex]);
            newItem.itemOptions.add(new ItemOption(73, 0));
            newItem.quantity = (short) Util.nextInt(1, 10);
            InventoryService.gI().addItemBag(player, newItem,false);
            icon[1] = newItem.template.iconID;

            InventoryService.gI().subQuantityItemsBag(player, item, 1);
            InventoryService.gI().sendItemBags(player);

            CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
        } else {
            Service.gI().sendThongBao(player, "Hàng trang đã đầy");
        }
    }

    private void openbox2010(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            short[] temp = { 17, 16, 15, 675, 676, 677, 678, 679, 680, 681, 580, 581, 582
            };
            int[][] gold = { { 5000, 20000 } };
            byte index = (byte) Util.nextInt(0, temp.length - 1);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;

            Item it = ItemService.gI().createNewItem(temp[index]);

            if (temp[index] >= 15 && temp[index] <= 17) {
                it.itemOptions.add(new ItemOption(73, 0));

            } else if (temp[index] >= 580 && temp[index] <= 582
                    || temp[index] >= 675 && temp[index] <= 681) { // cải trang

                it.itemOptions.add(new ItemOption(77, Util.nextInt(20, 30)));
                it.itemOptions.add(new ItemOption(103, Util.nextInt(20, 30)));
                it.itemOptions.add(new ItemOption(50, Util.nextInt(20, 30)));
                it.itemOptions.add(new ItemOption(95, Util.nextInt(5, 15)));
                it.itemOptions.add(new ItemOption(96, Util.nextInt(5, 15)));

                if (Util.isTrue(1, 200)) {
                    it.itemOptions.add(new ItemOption(74, 0));
                } else {
                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                }

            } else {
                it.itemOptions.add(new ItemOption(73, 0));
            }
            InventoryService.gI().addItemBag(pl, it, false);
            icon[1] = it.template.iconID;

            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBags(pl);

            CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        } else {
            Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    private void openboxsukien(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            short[] temp = { 16, 15, 865, 999, 1000, 1001, 739, 742, 743
            };
            int[][] gold = { { 5000, 20000 } };
            byte index = (byte) Util.nextInt(0, temp.length - 1);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;

            Item it = ItemService.gI().createNewItem(temp[index]);

            if (temp[index] >= 15 && temp[index] <= 16) {
                it.itemOptions.add(new ItemOption(73, 0));

            } else if (temp[index] == 865) {

                it.itemOptions.add(new ItemOption(30, 0));

                if (Util.isTrue(1, 30)) {
                    it.itemOptions.add(new ItemOption(93, 365));
                } else {
                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                }
            } else if (temp[index] == 999) { // mèo mun
                it.itemOptions.add(new ItemOption(77, 15));
                it.itemOptions.add(new ItemOption(30, 0));
                if (Util.isTrue(1, 50)) {
                    it.itemOptions.add(new ItemOption(74, 0));
                } else {
                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                }
            } else if (temp[index] == 1000) { // xiên cá
                it.itemOptions.add(new ItemOption(103, 15));
                it.itemOptions.add(new ItemOption(30, 0));
                if (Util.isTrue(1, 50)) {
                    it.itemOptions.add(new ItemOption(74, 0));
                } else {
                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                }
            } else if (temp[index] == 1001) { // Phóng heo
                it.itemOptions.add(new ItemOption(50, 15));
                it.itemOptions.add(new ItemOption(30, 0));
                if (Util.isTrue(1, 50)) {
                    it.itemOptions.add(new ItemOption(74, 0));
                } else {
                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                }

            } else if (temp[index] == 739) { // cải trang Billes

                it.itemOptions.add(new ItemOption(77, Util.nextInt(30, 40)));
                it.itemOptions.add(new ItemOption(103, Util.nextInt(30, 40)));
                it.itemOptions.add(new ItemOption(50, Util.nextInt(30, 45)));

                if (Util.isTrue(1, 100)) {
                    it.itemOptions.add(new ItemOption(74, 0));
                } else {
                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                }

            } else if (temp[index] == 742) { // cải trang Caufila

                it.itemOptions.add(new ItemOption(77, Util.nextInt(30, 40)));
                it.itemOptions.add(new ItemOption(103, Util.nextInt(30, 40)));
                it.itemOptions.add(new ItemOption(50, Util.nextInt(30, 45)));

                if (Util.isTrue(1, 100)) {
                    it.itemOptions.add(new ItemOption(74, 0));
                } else {
                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                }
            } else if (temp[index] == 743) { // chổi bay
                it.itemOptions.add(new ItemOption(30, 0));
                if (Util.isTrue(1, 50)) {
                    it.itemOptions.add(new ItemOption(74, 0));
                } else {
                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                }

            } else {
                it.itemOptions.add(new ItemOption(73, 0));
            }
            InventoryService.gI().addItemBag(pl, it, false);
            icon[1] = it.template.iconID;

            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBags(pl);

            CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        } else {
            Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    private void openboxkichhoat(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            short[] temp = { 76, 188, 189, 190, 441, 442, 447,
                    2010, 2009, 865, 938, 939, 940, 16, 17, 18, 19, 20,
                    946, 947, 948, 382, 383, 384, 385
            };
            int[][] gold = { { 5000, 20000 } };
            byte index = (byte) Util.nextInt(0, temp.length - 1);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            if (index <= 3 && index >= 0) {
                pl.inventory.gold += Util.nextInt(gold[0][0], gold[0][1]);
                if (pl.inventory.gold > Inventory.LIMIT_GOLD) {
                    pl.inventory.gold = Inventory.LIMIT_GOLD;
                }
                PlayerService.gI().sendInfoHpMpMoney(pl);
                icon[1] = 930;
            } else {

                Item it = ItemService.gI().createNewItem(temp[index]);
                if (temp[index] == 441) {
                    it.itemOptions.add(new ItemOption(95, 5));
                } else if (temp[index] == 442) {
                    it.itemOptions.add(new ItemOption(96, 5));
                } else if (temp[index] == 447) {
                    it.itemOptions.add(new ItemOption(101, 5));
                } else if (temp[index] >= 2009 && temp[index] <= 2010) {
                    it.itemOptions.add(new ItemOption(30, 0));
                } else if (temp[index] == 865) {
                    it.itemOptions.add(new ItemOption(30, 0));
                    if (Util.isTrue(1, 20)) {
                        it.itemOptions.add(new ItemOption(93, 365));
                    } else {
                        it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                    }
                } else if (temp[index] >= 938 && temp[index] <= 940) {
                    it.itemOptions.add(new ItemOption(77, 35));
                    it.itemOptions.add(new ItemOption(103, 35));
                    it.itemOptions.add(new ItemOption(50, 35));
                    if (Util.isTrue(1, 50)) {
                        it.itemOptions.add(new ItemOption(116, 0));
                    } else {
                        it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                    }
                } else if (temp[index] >= 946 && temp[index] <= 948) {
                    it.itemOptions.add(new ItemOption(77, 35));
                    it.itemOptions.add(new ItemOption(103, 35));
                    it.itemOptions.add(new ItemOption(50, 35));
                    if (Util.isTrue(1, 20)) {
                        it.itemOptions.add(new ItemOption(93, 365));
                    } else {
                        it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                    }
                } else {
                    it.itemOptions.add(new ItemOption(73, 0));
                }
                InventoryService.gI().addItemBag(pl, it, false);
                icon[1] = it.template.iconID;

            }
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBags(pl);

            CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        } else {
            Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    private void openPhieuCaiTrangHaiTac(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            Item ct = ItemService.gI().createNewItem((short) Util.nextInt(618, 626));
            ct.itemOptions.add(new ItemOption(147, 3));
            ct.itemOptions.add(new ItemOption(77, 3));
            ct.itemOptions.add(new ItemOption(103, 3));
            ct.itemOptions.add(new ItemOption(149, 0));
            if (item.template.id == 2006) {
                ct.itemOptions.add(new ItemOption(93, Util.nextInt(1, 7)));
            } else if (item.template.id == 2007) {
                ct.itemOptions.add(new ItemOption(93, Util.nextInt(7, 30)));
            }
            InventoryService.gI().addItemBag(pl, ct, false);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBags(pl);
            CombineServiceNew.gI().sendEffectOpenItem(pl, item.template.iconID, ct.template.iconID);
        } else {
            Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    private void eatGrapes(Player pl, Item item) {
        int percentCurrentStatima = pl.nPoint.stamina * 100 / pl.nPoint.maxStamina;
        if (percentCurrentStatima > 50) {
            Service.getInstance().sendThongBao(pl, "Thể lực vẫn còn trên 50%");
            return;
        } else if (item.template.id == 211) {
            pl.nPoint.stamina = pl.nPoint.maxStamina;
            Service.getInstance().sendThongBao(pl, "Thể lực của bạn đã được hồi phục 100%");
        } else if (item.template.id == 212) {
            pl.nPoint.stamina += (pl.nPoint.maxStamina * 20 / 100);
            Service.getInstance().sendThongBao(pl, "Thể lực của bạn đã được hồi phục 20%");
        }
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBags(pl);
        PlayerService.gI().sendCurrentStamina(pl);
    }

    private void openCSKB(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            short[] temp = { 76, 188, 189, 190, 381, 382, 383, 384, 385 };
            int[][] gold = { { 5000, 20000 } };
            byte index = (byte) Util.nextInt(0, temp.length - 1);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            if (index <= 3) {
                pl.inventory.gold += Util.nextInt(gold[0][0], gold[0][1]);
                if (pl.inventory.gold > Inventory.LIMIT_GOLD) {
                    pl.inventory.gold = Inventory.LIMIT_GOLD;
                }
                PlayerService.gI().sendInfoHpMpMoney(pl);
                icon[1] = 930;
            } else {
                Item it = ItemService.gI().createNewItem(temp[index]);
                it.itemOptions.add(new ItemOption(73, 0));
                InventoryService.gI().addItemBag(pl, it, false);
                icon[1] = it.template.iconID;
            }
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBags(pl);

            CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        } else {
            Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    private void useItemTime(Player pl, Item item) {
        switch (item.template.id) {
            case 382: // bổ huyết
                pl.itemTime.lastTimeBoHuyet = System.currentTimeMillis();
                pl.itemTime.isUseBoHuyet = true;
                break;
            case 383: // bổ khí
                pl.itemTime.lastTimeBoKhi = System.currentTimeMillis();
                pl.itemTime.isUseBoKhi = true;
                break;
            case 384: // giáp xên
                pl.itemTime.lastTimeGiapXen = System.currentTimeMillis();
                pl.itemTime.isUseGiapXen = true;
                break;
            case 381: // cuồng nộ
                pl.itemTime.lastTimeCuongNo = System.currentTimeMillis();
                pl.itemTime.isUseCuongNo = true;
                Service.getInstance().point(pl);
                break;
            case 385: // ẩn danh
                pl.itemTime.lastTimeAnDanh = System.currentTimeMillis();
                pl.itemTime.isUseAnDanh = true;
                break;
            case 379: // máy dò
                pl.itemTime.lastTimeUseMayDo = System.currentTimeMillis();
                pl.itemTime.isUseMayDo = true;
                break;
            case 663: // bánh pudding
            case 664: // xúc xíc
            case 665: // kem dâu
            case 666: // mì ly
            case 667: // sushi
                pl.itemTime.lastTimeEatMeal = System.currentTimeMillis();
                pl.itemTime.isEatMeal = true;
                ItemTimeService.gI().removeItemTime(pl, pl.itemTime.iconMeal);
                pl.itemTime.iconMeal = item.template.iconID;
                break;
        }
        Service.getInstance().point(pl);
        ItemTimeService.gI().sendAllItemTime(pl);
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBags(pl);
    }

    private void controllerCallRongThan(Player pl, Item item) {
        int tempId = item.template.id;
        if (tempId >= SummonDragon.NGOC_RONG_1_SAO && tempId <= SummonDragon.NGOC_RONG_7_SAO) {
            switch (tempId) {
                case SummonDragon.NGOC_RONG_1_SAO:
                case SummonDragon.NGOC_RONG_2_SAO:
                case SummonDragon.NGOC_RONG_3_SAO:
                    SummonDragon.gI().openMenuSummonShenron(pl, (byte) (tempId - 13));
                    break;
                default:
                    NpcService.gI().createMenuConMeo(pl, ConstNpc.TUTORIAL_SUMMON_DRAGON,
                            -1, "Bạn chỉ có thể gọi rồng từ ngọc 3 sao, 2 sao, 1 sao", "Hướng\ndẫn thêm\n(mới)", "OK");
                    break;
            }
        }
    }

    private void learnSkill(Player pl, Item item) {
        Message msg;
        try {
            if (item.template.gender == pl.gender || item.template.gender == 3) {
                String[] subName = item.template.name.split("");
                byte level = Byte.parseByte(subName[subName.length - 1]);
                Skill curSkill = SkillUtil.getSkillByItemID(pl, item.template.id);
                if (curSkill.point == 7) {
                    Service.getInstance().sendThongBao(pl, "Kỹ năng đã đạt tối đa!");
                } else {
                    if (curSkill.point == 0) {
                        if (level == 1) {
                            curSkill = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id),
                                    level);
                            SkillUtil.setSkill(pl, curSkill);
                            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                            msg = Service.getInstance().messageSubCommand((byte) 23);
                            msg.writer().writeShort(curSkill.skillId);
                            pl.sendMessage(msg);
                            msg.cleanup();
                        } else {
                            Skill skillNeed = SkillUtil
                                    .createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id), level);
                            Service.getInstance().sendThongBao(pl,
                                    "Vui lòng học " + skillNeed.template.name + " cấp " + skillNeed.point + " trước!");
                        }
                    } else {
                        if (curSkill.point + 1 == level) {
                            curSkill = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id),
                                    level);
                            // System.out.println(curSkill.template.name + " - " + curSkill.point);
                            SkillUtil.setSkill(pl, curSkill);
                            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                            msg = Service.getInstance().messageSubCommand((byte) 62);
                            msg.writer().writeShort(curSkill.skillId);
                            pl.sendMessage(msg);
                            msg.cleanup();
                        } else {
                            Service.getInstance().sendThongBao(pl, "Vui lòng học " + curSkill.template.name + " cấp "
                                    + (curSkill.point + 1) + " trước!");
                        }
                    }
                    InventoryService.gI().sendItemBags(pl);
                }
            } else {
                Service.getInstance().sendThongBao(pl, "Không thể thực hiện");

            }
        } catch (Exception e) {
            Logger.logException(UseItem.class,
                    e);
        }
    }

    private void useTDLT(Player pl, Item item) {
        if (pl.itemTime.isUseTDLT) {
            ItemTimeService.gI().turnOffTDLT(pl, item);
        } else {
            ItemTimeService.gI().turnOnTDLT(pl, item);
        }
    }

    private void usePorata(Player pl) {
        if (pl.pet == null || pl.fusion.typeFusion == 4) {
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
        } else {
            if (pl.fusion.typeFusion == ConstPlayer.NON_FUSION) {
                pl.pet.fusion(true);
            } else {
                pl.pet.unFusion();
            }
        }
    }

    private void openCapsuleUI(Player pl) {
        pl.iDMark.setTypeChangeMap(ConstMap.CHANGE_CAPSULE);
        ChangeMapService.gI().openChangeMapTab(pl);
    }

    public void choseMapCapsule(Player pl, int index) {
        if (index < 0) {
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
            return;
        }
        int zoneId = -1;
        Zone zoneChose = pl.mapCapsule.get(index);
        //Kiểm tra số lượng người trong khu

        if (zoneChose.getNumOfPlayers() > 25
                || MapService.gI().isMapDoanhTrai(zoneChose.map.mapId)
                || MapService.gI().isMapMaBu(zoneChose.map.mapId)) {
            Service.getInstance().sendThongBao(pl, "Hiện tại không thể vào được khu!");
            return;
        }
        if (index != 0 || zoneChose.map.mapId == 21
                || zoneChose.map.mapId == 22
                || zoneChose.map.mapId == 23) {
            pl.mapBeforeCapsule = pl.zone;
        } else {
            zoneId = pl.mapBeforeCapsule != null ? pl.mapBeforeCapsule.zoneId : -1;
            pl.mapBeforeCapsule = null;
        }
        ChangeMapService.gI().changeMapBySpaceShip(pl, pl.mapCapsule.get(index).map.mapId, zoneId, -1);
    }
    private void upSkillPet(Player pl, Item item) {
        if (pl.pet == null) {
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
            return;
        }
        try {
            switch (item.template.id) {
                case 402: // skill 1
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 0)) {
                        Service.getInstance().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ");
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 403: // skill 2
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 1)) {
                        Service.getInstance().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ");
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 404: // skill 3
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 2)) {
                        Service.getInstance().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ");
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 759: // skill 4
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 3)) {
                        Service.getInstance().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ");
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
            }
        } catch (Exception e) {
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
        }
    }
}
