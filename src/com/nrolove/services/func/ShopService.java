package com.nrolove.services.func;

import com.nrolove.consts.ConstNpc;
import com.nrolove.jdbc.daos.PlayerDAO;
import com.nrolove.models.Template.CaiTrang;
import com.nrolove.models.item.Item;
import com.nrolove.models.npc.Npc;
import com.nrolove.models.player.Inventory;
import com.nrolove.models.player.Player;
import com.nrolove.models.shop.ItemShop;
import com.nrolove.models.shop.Shop;
import com.nrolove.models.shop.TabShop;
import com.nrolove.server.Manager;
import com.nrolove.server.io.Message;
import com.nrolove.services.InventoryService;
import com.nrolove.services.ItemService;
import com.nrolove.services.PlayerService;
import com.nrolove.services.Service;
import com.nrolove.utils.Logger;
import com.nrolove.utils.Util;

/**
 *
 * @author 💖 PuPuBug 💖
 *
 */
public class ShopService {

    private static final int COST_GOLD_BAR = 500000000;
    private static final int COST_LOCK_GOLD_BAR = 300000000;

    private static final byte NORMAL_SHOP = 0;
    private static final byte SPEC_SHOP = 3;

    private static ShopService i;

    public static ShopService gI() {
        if (i == null) {
            i = new ShopService();
        }
        return i;
    }

    // Lấy ra itemshop khi mua
    private ItemShop getItemShop(int shopId, int tempId) {
        ItemShop itemShop = null;
        Shop shop = null;
        switch (shopId) {
            case ConstNpc.SHOP_BUNMA_QK_0:
                shop = getShop(ConstNpc.BUNMA, 0, -1);
                break;
            case ConstNpc.SHOP_DENDE_0:
                shop = getShop(ConstNpc.DENDE, 0, -1);
                break;
            case ConstNpc.SHOP_APPULE_0:
                shop = getShop(ConstNpc.APPULE, 0, -1);
                break;
            case ConstNpc.SHOP_URON_0:
                shop = getShop(ConstNpc.URON, 0, -1);
                break;
            case ConstNpc.SHOP_SANTA_0:
                shop = getShop(ConstNpc.SANTA, 0, -1);
                break;
            case ConstNpc.SHOP_SANTA_1:
                shop = getShop(ConstNpc.SANTA, 1, -1);
                break;
            case ConstNpc.SHOP_BA_HAT_MIT_0:
                shop = getShop(ConstNpc.BA_HAT_MIT, 0, -1);
                break;
            case ConstNpc.SHOP_BA_HAT_MIT_1:
                shop = getShop(ConstNpc.BA_HAT_MIT, 1, -1);
                break;
            case ConstNpc.SHOP_BA_HAT_MIT_2:
                shop = getShop(ConstNpc.BA_HAT_MIT, 2, -1);
                break;
            case ConstNpc.SHOP_BUNMA_TL_0:
                shop = getShop(ConstNpc.BUNMA_TL, 0, -1);
                break;
            case ConstNpc.SHOP_BILL_HUY_DIET_0:
                shop = getShop(ConstNpc.BILL, 0, -1);
                break;
                case ConstNpc.SHOP_HONG_NGOC:
                shop = getShop(ConstNpc.QUY_LAO_KAME, 0, -1);
                break;
        }
        if (shop != null) {
            for (TabShop tab : shop.tabShops) {
                for (ItemShop is : tab.itemShops) {
                    if (is.temp.id == tempId) {
                        itemShop = is;
                        break;
                    }
                }
                if (itemShop != null) {
                    break;
                }
            }
        }
        return itemShop;
    }

    private Shop getShop(int npcId, int order, int gender) {
        for (Shop shop : Manager.SHOPS) {
            if (shop.npcId == npcId && shop.shopOrder == order) {
                if (gender != -1) {
                    return new Shop(shop, gender);
                } else {
                    return shop;
                }
            }
        }
        return null;
    }

    private Shop getShopHuyDiet(Player player, Shop s) {
        Shop shop = new Shop(s);
        for (TabShop tabShop : shop.tabShops) {
            for (ItemShop item : tabShop.itemShops) {
                item.iconSpec = 12006 + item.temp.type;
                item.costSpec = 1;
            }
        }
        return shop;
    }

    private Shop getShopHongNgoc(Player player, Shop s) {
        Shop shop = new Shop(s);
        for (TabShop tabShop : shop.tabShops) {
            for (ItemShop item : tabShop.itemShops) {
                item.iconSpec = 7743 + item.temp.type;
                item.costSpec = 1;
            }
        }
        return shop;
    }

    private Shop getShopThoiVang(Player player, Shop s) {
        Shop shop = new Shop(s);
        for (TabShop tabShop : shop.tabShops) {
            for (ItemShop item : tabShop.itemShops) {
                item.iconSpec = 4028 + item.temp.type;
                item.costSpec = 1;
            }
        }
        return shop;
    }
    

    private Shop getShopBua(Player player, Shop s) {
        Shop shop = new Shop(s);
        for (TabShop tabShop : shop.tabShops) {
            for (ItemShop item : tabShop.itemShops) {

                long min = 0;
                switch (item.temp.id) {
                    case 213:
                        long timeTriTue = player.charms.tdTriTue;
                        long current = System.currentTimeMillis();
                        min = (timeTriTue - current) / 60000;

                        break;
                    case 214:
                        min = (player.charms.tdManhMe - System.currentTimeMillis()) / 60000;
                        break;
                    case 215:
                        min = (player.charms.tdDaTrau - System.currentTimeMillis()) / 60000;
                        break;
                    case 216:
                        min = (player.charms.tdOaiHung - System.currentTimeMillis()) / 60000;
                        break;
                    case 217:
                        min = (player.charms.tdBatTu - System.currentTimeMillis()) / 60000;
                        break;
                    case 218:
                        min = (player.charms.tdDeoDai - System.currentTimeMillis()) / 60000;
                        break;
                    case 219:
                        min = (player.charms.tdThuHut - System.currentTimeMillis()) / 60000;
                        break;
                    case 522:
                        min = (player.charms.tdDeTu - System.currentTimeMillis()) / 60000;
                        break;
                    case 671:
                        min = (player.charms.tdTriTue3 - System.currentTimeMillis()) / 60000;
                        break;
                    case 672:
                        min = (player.charms.tdTriTue4 - System.currentTimeMillis()) / 60000;
                        break;
                }
                if (min > 0) {
                    item.options.clear();
                    if (min >= 1440) {
                        item.options.add(new Item.ItemOption(63, (int) min / 1440));
                    } else if (min >= 60) {
                        item.options.add(new Item.ItemOption(64, (int) min / 60));
                    } else {
                        item.options.add(new Item.ItemOption(65, (int) min));
                    }
                }
            }
        }
        return shop;
    }

    // shop đồ hủy diệt
    public void openShopBillHuyDiet(Player player, int shopId, int order) {
        Shop shop = getShopHuyDiet(player, getShop(ConstNpc.BILL, order, -1));
        openShopType4(player, shop, shopId);
    }
    // THOI
    public void openShopThoiVang(Player player, int shopId, int order) {
        Shop shop = getShopThoiVang(player, getShop(ConstNpc.MR_POPO, order, -1));
        openShopType4(player, shop, shopId);
    }
public void openShopHongNgoc(Player player, int shopId, int order) {
        Shop shop = getShopHongNgoc(player, getShop(ConstNpc.QUY_LAO_KAME, order, -1));
        openShopType4(player, shop, shopId);
    }

    // shop bùa
    public void openShopBua(Player player, int shopId, int order) {
        // player.iDMark.setShopId(shopId);
        Shop shop = getShopBua(player, getShop(ConstNpc.BA_HAT_MIT, order, -1));
        openShopType0(player, shop, shopId);
    }

    // shop normal
    public void openShopNormal(Player player, Npc npc, int shopId, int order, int gender) {
        Shop shop = getShop(npc.tempId, order, gender);
        openShopType0(player, shop, shopId);
    }

    private void openShopType0(Player player, Shop shop, int shopId) {
        player.iDMark.setShopId(shopId);
        if (shop != null) {
            Message msg;
            try {
                msg = new Message(-44);
                msg.writer().writeByte(NORMAL_SHOP);
                msg.writer().writeByte(shop.tabShops.size());
                for (TabShop tab : shop.tabShops) {
                    msg.writer().writeUTF(tab.name);
                    msg.writer().writeByte(tab.itemShops.size());
                    for (ItemShop itemShop : tab.itemShops) {
                        msg.writer().writeShort(itemShop.temp.id);
                        msg.writer().writeInt(itemShop.gold);
                        msg.writer().writeInt(itemShop.gem);
                        msg.writer().writeByte(itemShop.options.size());
                        for (Item.ItemOption option : itemShop.options) {
                            msg.writer().writeByte(option.optionTemplate.id);
                            msg.writer().writeShort(option.param);
                        }
                        msg.writer().writeByte(itemShop.isNew ? 1 : 0);
                        CaiTrang caiTrang = Manager.gI().getCaiTrangByItemId(itemShop.temp.id);
                        msg.writer().writeByte(caiTrang != null ? 1 : 0);
                        if (caiTrang != null) {
                            msg.writer().writeShort(caiTrang.getID()[0]);
                            msg.writer().writeShort(caiTrang.getID()[1]);
                            msg.writer().writeShort(caiTrang.getID()[2]);
                            msg.writer().writeShort(caiTrang.getID()[3]);
                        }
                    }
                }
                player.sendMessage(msg);
                msg.cleanup();
            } catch (Exception e) {
                Logger.logException(ShopService.class, e);
            }
        }
    }

    private void openShopType3(Player player, Shop shop, int shopId) {
        player.iDMark.setShopId(shopId);
        if (shop != null) {
            Message msg;
            try {
                msg = new Message(-44);
                msg.writer().writeByte(3);
                msg.writer().writeByte(shop.tabShops.size());
                for (TabShop tab : shop.tabShops) {
                    msg.writer().writeUTF(tab.name);
                    msg.writer().writeByte(tab.itemShops.size());
                    // System.out.println(tab.name);
                    for (ItemShop itemShop : tab.itemShops) {
                        msg.writer().writeShort(itemShop.temp.id);
                        msg.writer().writeShort(itemShop.iconSpec);
                        msg.writer().writeInt(itemShop.costSpec);
                        msg.writer().writeByte(itemShop.options.size());
                        for (Item.ItemOption option : itemShop.options) {
                            msg.writer().writeByte(option.optionTemplate.id);
                            msg.writer().writeShort(option.param);
                        }
                        msg.writer().writeByte(itemShop.isNew ? 1 : 0);
                        CaiTrang caiTrang = Manager.gI().getCaiTrangByItemId(itemShop.temp.id);
                        msg.writer().writeByte(caiTrang != null ? 1 : 0);
                        if (caiTrang != null) {
                            msg.writer().writeShort(caiTrang.getID()[0]);
                            msg.writer().writeShort(caiTrang.getID()[1]);
                            msg.writer().writeShort(caiTrang.getID()[2]);
                            msg.writer().writeShort(caiTrang.getID()[3]);
                        }
                    }
                }
                player.sendMessage(msg);
                msg.cleanup();
                // System.out.println("sent");
            } catch (Exception e) {
                Logger.logException(ShopService.class, e);
            }
        }
    }

    private void openShopType4(Player player, Shop shop, int shopId) {
        player.iDMark.setShopId(shopId);
        if (shop != null) {
            Message msg;
            try {
                msg = new Message(-44);
                msg.writer().writeByte(3);
                msg.writer().writeByte(shop.tabShops.size());
                for (TabShop tab : shop.tabShops) {
                    msg.writer().writeUTF(tab.name);
                    msg.writer().writeByte(tab.itemShops.size());
                    // System.out.println(tab.name);
                    for (ItemShop itemShop : tab.itemShops) {
                        msg.writer().writeShort(itemShop.temp.id);
                        msg.writer().writeShort(getIconItemShop(itemShop.type_sell));
                        msg.writer().writeInt(itemShop.gold);
                        msg.writer().writeByte(itemShop.options.size());
                        for (Item.ItemOption option : itemShop.options) {
                            msg.writer().writeByte(option.optionTemplate.id);
                            msg.writer().writeShort(option.param);
                        }
                        msg.writer().writeByte(itemShop.isNew ? 1 : 0);
                        CaiTrang caiTrang = Manager.gI().getCaiTrangByItemId(itemShop.temp.id);
                        msg.writer().writeByte(caiTrang != null ? 1 : 0);
                        if (caiTrang != null) {
                            msg.writer().writeShort(caiTrang.getID()[0]);
                            msg.writer().writeShort(caiTrang.getID()[1]);
                            msg.writer().writeShort(caiTrang.getID()[2]);
                            msg.writer().writeShort(caiTrang.getID()[3]);
                        }
                    }
                }
                player.sendMessage(msg);
                msg.cleanup();
                // System.out.println("sent");
            } catch (Exception e) {
                Logger.logException(ShopService.class, e);
            }
        }
    }

    private void buyItemShopNormal(Player player, ItemShop is) {
        if (is != null) {

            if (is.temp.id == 517 && player.inventory.itemsBag.size() >= 80) {
                Service.getInstance().sendThongBao(player, "Hành trang đã đạt tới số lượng tối đa");
                Service.getInstance().sendMoney(player);
                return;
            }
            if (is.temp.id == 518 && player.inventory.itemsBox.size() >= 80) {
                Service.getInstance().sendThongBao(player, "rương đồ đã đạt tới số lượng tối đa");
                Service.getInstance().sendMoney(player);
                return;
            }
            if (InventoryService.gI().getCountEmptyBag(player) > 0) {

                if (!checkCanPay(player, is)) {
                    return;
                }
            }
            switch (player.iDMark.getShopId()) {
                case ConstNpc.SHOP_SANTA_1:
                    player.head = is.temp.part;
                    Service.getInstance().Send_Caitrang(player);
                    Service.getInstance().sendThongBao(player, "Đổi kiểu tóc thành công");
                    break;
                case ConstNpc.SHOP_BA_HAT_MIT_0:
                    player.charms.addTimeCharms(is.temp.id, 60);
                    payment(player, is);
                    openShopBua(player, player.iDMark.getShopId(), 0);
                    break;
                case ConstNpc.SHOP_BA_HAT_MIT_1:
                    player.charms.addTimeCharms(is.temp.id, 60 * 8);
                    payment(player, is);
                    openShopBua(player, player.iDMark.getShopId(), 1);
                    break;
                case ConstNpc.SHOP_BA_HAT_MIT_2:
                    player.charms.addTimeCharms(is.temp.id, 60 * 24 * 30);
                    payment(player, is);
                    openShopBua(player, player.iDMark.getShopId(), 2);
                    break;
                case ConstNpc.SHOP_BILL_HUY_DIET_0:
                    if (player.setClothes.godClothes) {
                        Item meal = InventoryService.gI().findMealChangeDestroyClothes(player);
                        Item ticket = InventoryService.gI().findTicketChangeDestroyClothes(player, is.temp.type);
                        if (meal != null) {
                            Item item = ItemService.gI().createItemFromItemShop(is);
                            int param = 0;
                            if (Util.isTrue(2, 10)) {
                                param = Util.nextInt(10, 15);
                            } else if (Util.isTrue(3, 10)) {
                                param = Util.nextInt(0, 10);
                            }

                            for (Item.ItemOption io : item.itemOptions) {
                                int optId = io.optionTemplate.id;
                                switch (optId) {
                                    case 47: // giáp
                                    case 6: // hp
                                    case 26: // hp/30s
                                    case 22: // hp k
                                    case 0: // sức đánh
                                    case 7: // ki
                                    case 28: // ki/30s
                                    case 23: // ki k
                                    case 14: // crit
                                        io.param += ((long) io.param * param / 100);
                                        break;
                                }
                            }
                            payment(player, is);
                            InventoryService.gI().subQuantityItemsBag(player, meal, 99);
                            InventoryService.gI().addItemBag(player, item, true);
                            InventoryService.gI().sendItemBags(player);
                            Service.getInstance().sendThongBao(player, "Đổi thành công " + is.temp.name);
                        } else {
                            Service.getInstance().sendThongBao(player,
                                    "Yêu cầu có 99 thức ăn và 1 vé đổi đồ hủy diệt tương ứng");
                        }
                    } else {
                        Service.getInstance().sendThongBao(player, "Yêu cầu có đủ trang bị thần linh");
                    }
                    break;
                default:
                    payment(player, is);
                    InventoryService.gI().addItemBag(player, ItemService.gI().createItemFromItemShop(is), true);
                    InventoryService.gI().sendItemBags(player);
                    Service.getInstance().sendThongBao(player, "Mua thành công " + is.temp.name);
                    break;
            }
        } else {
            Service.getInstance().sendThongBao(player, "Hành trang đã đầy");
        }
        Service.getInstance().sendMoney(player);

    }

    // item reward lucky round---------------------------------------------------
    public void openBoxItemLuckyRound(Player player) {
        player.iDMark.setShopId(ConstNpc.SIDE_BOX_LUCKY_ROUND);
        InventoryService.gI().arrangeItems(player.inventory.itemsBoxCrackBall);
        Message msg;
        try {
            msg = new Message(-44);
            msg.writer().writeByte(4);
            msg.writer().writeByte(1);
            msg.writer().writeUTF("Rương đồ");
            int n = player.inventory.itemsBoxCrackBall.size()
                    - InventoryService.gI().getCountEmptyListItem(player.inventory.itemsBoxCrackBall);
            msg.writer().writeByte(n);
            for (int i = 0; i < n; i++) {
                Item item = player.inventory.itemsBoxCrackBall.get(i);
                msg.writer().writeShort(item.template.id);
                msg.writer().writeUTF("\n|7|NROLORD LUCKY");
                msg.writer().writeByte(item.itemOptions.size());
                for (Item.ItemOption io : item.itemOptions) {
                    msg.writer().writeByte(io.optionTemplate.id);
                    msg.writer().writeShort(io.param);
                }
                msg.writer().writeByte(1);
                CaiTrang ct = Manager.gI().getCaiTrangByItemId(item.template.id);
                msg.writer().writeByte(ct != null ? 1 : 0);
                if (ct != null) {
                    msg.writer().writeShort(ct.getID()[0]);
                    msg.writer().writeShort(ct.getID()[1]);
                    msg.writer().writeShort(ct.getID()[2]);
                    msg.writer().writeShort(ct.getID()[3]);
                }
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    private void getItemSideBoxLuckyRound(Player player, byte type, int index) {
        Item item = player.inventory.itemsBoxCrackBall.get(index);
        switch (type) {
            case 0: // nhận
                if (item.isNotNullItem()) {
                    if (InventoryService.gI().getCountEmptyBag(player) != 0) {
                        InventoryService.gI().addItemBag(player, item, false);
                        Service.getInstance().sendThongBao(player,
                                "Bạn nhận được " + (item.template.id == 189
                                        ? Util.numberToMoney(item.quantity) + " vàng"
                                        : item.template.name));
                        InventoryService.gI().sendItemBags(player);
                        InventoryService.gI().removeItem(player.inventory.itemsBoxCrackBall, index);
                        openBoxItemLuckyRound(player);
                    } else {
                        Service.getInstance().sendThongBao(player, "Hành trang đã đầy");
                    }
                } else {
                    Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                }
                break;
            case 1: // xóa
                InventoryService.gI().subQuantityItem(player.inventory.itemsBoxCrackBall, item, item.quantity);
                openBoxItemLuckyRound(player);
                Service.getInstance().sendThongBao(player, "Xóa vật phẩm thành công");
                break;
            case 2: // nhận hết
                for (int i = 0; i < player.inventory.itemsBoxCrackBall.size(); i++) {
                    item = player.inventory.itemsBoxCrackBall.get(i);
                    if (item.isNotNullItem()) {
                        if (InventoryService.gI().addItemBag(player, item, false)) {
                            player.inventory.itemsBoxCrackBall.set(i, ItemService.gI().createItemNull());
                            Service.getInstance().sendThongBao(player,
                                    "Bạn nhận được " + (item.template.id == 189
                                            ? Util.numberToMoney(item.quantity) + " vàng"
                                            : item.template.name));
                        }
                    } else {
                        break;
                    }
                }
                InventoryService.gI().sendItemBags(player);
                openBoxItemLuckyRound(player);
                break;
        }
    }
    // item reward---------------------------------------------------------------

    public void openBoxItemReward(Player player) {
        if (player.getSession().itemsReward == null) {
            player.getSession().initItemsReward();
        }
        player.iDMark.setShopId(ConstNpc.SIDE_BOX_ITEM_REWARD);
        Message msg;
        try {
            msg = new Message(-44);
            msg.writer().writeByte(4);
            msg.writer().writeByte(1);
            msg.writer().writeUTF("Phần\nthưởng");
            msg.writer().writeByte(player.getSession().itemsReward.size());
            for (Item item : player.getSession().itemsReward) {
                msg.writer().writeShort(item.template.id);
                msg.writer().writeUTF("\n|7|LUCKY ITEM");
                msg.writer().writeByte(item.itemOptions.size() + 1);
                for (Item.ItemOption io : item.itemOptions) {
                    msg.writer().writeByte(io.optionTemplate.id);
                    msg.writer().writeShort(io.param);
                }
                // số lượng
                msg.writer().writeByte(31);
                msg.writer().writeShort(item.quantity);
                //
                msg.writer().writeByte(1);
                CaiTrang ct = Manager.gI().getCaiTrangByItemId(item.template.id);
                msg.writer().writeByte(ct != null ? 1 : 0);
                if (ct != null) {
                    msg.writer().writeShort(ct.getID()[0]);
                    msg.writer().writeShort(ct.getID()[1]);
                    msg.writer().writeShort(ct.getID()[2]);
                    msg.writer().writeShort(ct.getID()[3]);
                }
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    private void getItemSideBoxReward(Player player, byte type, int index) {
        Item item = player.getSession().itemsReward.get(index);
        switch (type) {
            case 0: // nhận
                if (item.isNotNullItem()) {
                    if (InventoryService.gI().getCountEmptyBag(player) != 0) {
                        InventoryService.gI().addItemBag(player, item, false);
                        Service.getInstance().sendThongBao(player,
                                "Bạn nhận được " + (item.template.id == 189
                                        ? Util.numberToMoney(item.quantity) + " vàng"
                                        : item.template.name));
                        InventoryService.gI().sendItemBags(player);
                        player.getSession().itemsReward.remove(index);
                        openBoxItemReward(player);
                    } else {
                        Service.getInstance().sendThongBao(player, "Hành trang đã đầy");
                    }
                } else {
                    Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                }
                break;
            case 1: // xóa
                player.getSession().itemsReward.remove(index);
                openBoxItemReward(player);
                Service.getInstance().sendThongBao(player, "Xóa vật phẩm thành công");
                break;
            case 2: // nhận hết
                for (int i = player.getSession().itemsReward.size() - 1; i >= 0; i--) {
                    item = player.getSession().itemsReward.get(i);
                    if (item.isNotNullItem()) {
                        if (InventoryService.gI().addItemBag(player, item, false)) {
                            player.getSession().itemsReward.remove(i);
                            Service.getInstance().sendThongBao(player,
                                    "Bạn nhận được " + (item.template.id == 189
                                            ? Util.numberToMoney(item.quantity) + " vàng"
                                            : item.template.name));
                        }
                    } else {
                        break;
                    }
                }
                InventoryService.gI().sendItemBags(player);
                openBoxItemReward(player);
                break;
        }
        PlayerDAO.updateItemReward(player);

    }

    // --------------------------------------------------------------------------
    // điều hướng mua
    public void buyItem(Player player, byte type, int tempId) {
        switch (player.iDMark.getShopId()) {
            case ConstNpc.SIDE_BOX_LUCKY_ROUND:
                getItemSideBoxLuckyRound(player, type, tempId);
                break;
            case ConstNpc.SIDE_BOX_ITEM_REWARD:
                getItemSideBoxReward(player, type, tempId);
                break;
            default:
                buyItemShopNormal(player, getItemShop(player.iDMark.getShopId(), tempId));
                break;
        }
    }

    public void showConfirmSellItem(Player pl, int where, int index) {
        Item item = null;
        if (where == 0) {
            item = pl.inventory.itemsBody.get(index);
        } else {
            item = pl.inventory.itemsBag.get(index);
        }
        if (item != null) {
            if (item.template.id == 570) {
                Service.getInstance().sendThongBaoOK(pl, "Không bán được bạn ơi.");
                return;
            }
            int goldReceive = 0;
            if (item.template.id == 457) {
                goldReceive = COST_GOLD_BAR;
            } else if (item.template.id == 2011) {
                goldReceive = COST_LOCK_GOLD_BAR;
            } else {
                goldReceive = item.quantity;
            }
            Message msg = new Message(7);
            try {
                msg.writer().writeByte(where);
                msg.writer().writeShort(index);
                msg.writer()
                        .writeUTF("Bạn có muốn bán\n x"
                                + (item.template.id == 457 || item.template.id == 2011 ? 1 : item.quantity) + " "
                                + item.template.name
                                + "\nvới giá là " + Util.numberToMoney(goldReceive) + " vàng?");
                pl.sendMessage(msg);
                msg.cleanup();
            } catch (Exception e) {
            }
        }
    }

    public void sellItem(Player pl, int where, int index) {
        Item item = null;
        if (where == 0) {
            item = pl.inventory.itemsBody.get(index);
        } else {
            item = pl.inventory.itemsBag.get(index);
        }
        if (item != null) {
            int goldReceive = 0;
            if (item.template.id == 457) {
                goldReceive = COST_GOLD_BAR;
            } else if (item.template.id == 2011) {
                goldReceive = COST_LOCK_GOLD_BAR;
            } else {
                goldReceive = item.quantity;
            }
            if (pl.inventory.gold + goldReceive <= Inventory.LIMIT_GOLD) {
                if (where == 0) {
                    InventoryService.gI().subQuantityItemsBody(pl, item, item.quantity);
                    InventoryService.gI().sendItemBody(pl);
                    Service.getInstance().Send_Caitrang(pl);
                } else {
                    if (item.template.id == 457 || item.template.id == 2011) {
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        InventoryService.gI().subQuantityItemsBag(pl, item, item.quantity);
                    }
                    InventoryService.gI().sendItemBags(pl);
                }
                pl.inventory.gold += goldReceive;
                PlayerService.gI().sendInfoHpMpMoney(pl);
                Service.getInstance().sendThongBao(pl, "Đã bán " + item.template.name
                        + " thu được " + Util.numberToMoney(goldReceive) + " vàng");
            } else {
                Service.getInstance().sendThongBao(pl, "Vàng sau khi bán vượt quá giới hạn");
            }
        } else {
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
        }
    }

    private int getIconItemShop(int type) {
        int icon;
        switch (type) {
            // Vàng
            case 0:
                icon = 930;
                break;
            // Thỏi
            case 1:
                icon = 4028;
                break;
            // Ngọc
            case 2:
                icon = 932;
                break;
            // Hồng ngọc
            case 3:
                icon = 7743;
                break;
            // Vé hông ngọc
            case 4:
                icon = 18177;
                break;
            // vé hủy diệt
            case 5:
                icon = 12006;// áo
                break;
            case 6:
                icon = 12007;// quần
                break;
            case 7:
                icon = 12008;// găng
                break;
            case 8:
                icon = 12009;// giay
                break;
            case 9:
                icon = 12010;// rada
                break;
            // Vàng
            default:
                icon = 930;
                break;
        }
        return icon;
    }

    private void payment(Player player, ItemShop item) {
        switch (item.type_sell) {
            // Vàng
            case 0:
                player.inventory.gold -= item.gold;
                break;
            // Thỏi
            case 1:
                Item itemGold = InventoryService.gI().findItemBagByTemp(player, 457);
                InventoryService.gI().subQuantityItemsBag(player, itemGold, item.gold);
                break;
            // Ngọc
            case 2:
                player.inventory.gem -= item.gem;
                break;
            // Hồng ngọc
            case 3:
                player.inventory.ruby -= item.gold;
                break;
            // Vé hông ngọc
            case 4:
                break;
            case 5:
                Item itemPay = InventoryService.gI().findItemBagByTemp(player, 2001);
                InventoryService.gI().subQuantityItemsBag(player, itemPay, item.gold);
                break;
            case 6:
                Item itemPay1 = InventoryService.gI().findItemBagByTemp(player, 2002);
                InventoryService.gI().subQuantityItemsBag(player, itemPay1, item.gold);
                break;
            case 7:
                Item itemPay2 = InventoryService.gI().findItemBagByTemp(player, 2003);
                InventoryService.gI().subQuantityItemsBag(player, itemPay2, item.gold);
                break;
            case 8:
                Item itemPay3 = InventoryService.gI().findItemBagByTemp(player, 2004);
                InventoryService.gI().subQuantityItemsBag(player, itemPay3, item.gold);
                break;
            case 9:
                Item itemPay4 = InventoryService.gI().findItemBagByTemp(player, 2005);
                InventoryService.gI().subQuantityItemsBag(player, itemPay4, item.gold);
                break;
            // Vàng
            default:
                player.inventory.gold -= item.gold;
                break;
        }
    }

    private boolean checkCanPay(Player player, ItemShop item) {
        boolean isCan = true;
        switch (item.type_sell) {
            // Vàng
            case 0:
                if (player.inventory.gold < item.gold) {
                    Service.getInstance().sendThongBao(player, "Bạn không đủ vàng, còn thiếu "
                            + (Util.numberToMoney(item.gold - player.inventory.gold) + " vàng"));
                    Service.getInstance().sendMoney(player);
                    isCan = false;
                    break;
                }
                break;
            // Thỏi
            case 1:
                Item itemGold = InventoryService.gI().findItemBagByTemp(player, 457);
                if (itemGold == null || itemGold.quantity < item.gold) {
                    Service.getInstance().sendThongBao(player, "Bạn không đủ thỏi vàng, còn thiếu "
                            + (Util.numberToMoney(item.gold - itemGold.quantity) + " thỏi vàng"));
                    Service.getInstance().sendMoney(player);
                    isCan = false;
                    break;
                }
                break;
            // Ngọc
            case 2:
                if (player.inventory.gem < item.gem) {
                    Service.getInstance().sendThongBao(player, "Bạn không đủ ngọc, còn thiếu "
                            + (Util.numberToMoney(item.gem - player.inventory.gem) + " ngọc"));
                    Service.getInstance().sendMoney(player);
                    isCan = false;
                    break;
                }
                break;
            // Hồng ngọc
            case 3:
                if (player.inventory.ruby < item.gold) {
                    Service.getInstance().sendThongBao(player, "Bạn không đủ hồng ngọc, còn thiếu "
                            + (Util.numberToMoney(item.gold - player.inventory.ruby) + " hồng ngọc nữa"));
                    Service.getInstance().sendMoney(player);
                    isCan = false;
                    break;
                }
                break;
            // Vé hông ngọc
            case 4:
                break;
            // vé hủy diệt
            case 5:
                Item itemPay = InventoryService.gI().findItemBagByTemp(player, 2001);
                if (itemPay == null || itemPay.quantity < item.gold) {
                    Service.getInstance().sendThongBao(player, "Bạn không đủ vé mà đòi mua. lêu lêu ");
                    Service.getInstance().sendMoney(player);
                    isCan = false;
                    break;
                }
                break;
            case 6:
                Item itemPay1 = InventoryService.gI().findItemBagByTemp(player, 2002);
                if (itemPay1 == null || itemPay1.quantity < item.gold) {
                    Service.getInstance().sendThongBao(player, "Bạn không đủ vé mà đòi mua. lêu lêu ");
                    Service.getInstance().sendMoney(player);
                    isCan = false;
                    break;
                }
                break;
            case 7:
                Item itemPay2 = InventoryService.gI().findItemBagByTemp(player, 2003);
                if (itemPay2 == null || itemPay2.quantity < item.gold) {
                    Service.getInstance().sendThongBao(player, "Bạn không đủ vé mà đòi mua. lêu lêu ");
                    Service.getInstance().sendMoney(player);
                    isCan = false;
                    break;
                }
                break;
            case 8:
                Item itemPay3 = InventoryService.gI().findItemBagByTemp(player, 2004);
                if (itemPay3 == null || itemPay3.quantity < item.gold) {
                    Service.getInstance().sendThongBao(player, "Bạn không đủ vé mà đòi mua. lêu lêu ");
                    Service.getInstance().sendMoney(player);
                    isCan = false;
                    break;
                }
                break;
            case 9:
                Item itemPay4 = InventoryService.gI().findItemBagByTemp(player, 2005);
                if (itemPay4 == null || itemPay4.quantity < item.gold) {
                    Service.getInstance().sendThongBao(player, "Bạn không đủ vé mà đòi mua. lêu lêu ");
                    Service.getInstance().sendMoney(player);
                    isCan = false;
                    break;
                }
                break;
            // Vàng
            default:
                if (player.inventory.gold < item.gold) {
                    Service.getInstance().sendThongBao(player, "Bạn không đủ vàng, còn thiếu "
                            + (Util.numberToMoney(item.gold - player.inventory.gold) + " vàng"));
                    Service.getInstance().sendMoney(player);
                    isCan = false;
                    break;
                }
                break;
        }
        return isCan;
    }
}
