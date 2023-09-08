package com.nrolove.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.nrolove.models.item.Item;
import com.nrolove.models.map.blackball.BlackBallWar;
import com.nrolove.models.npc.specialnpc.MabuEgg;
import com.nrolove.models.npc.specialnpc.MagicTree;
import com.nrolove.models.player.Inventory;
import com.nrolove.models.player.Pet;
import com.nrolove.models.player.Player;
import com.nrolove.server.io.Message;
import com.nrolove.utils.Logger;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 *
 */
public class InventoryService {

    private static InventoryService i;

    public static InventoryService gI() {
        if (i == null) {
            i = new InventoryService();
        }
        return i;
    }
    
    public void addItemGiftCodeToPlayer(Player p, GiftService giftcode) {
        Set<Integer> keySet = giftcode.Item.keySet();
        String textGift = "Bạn vừa nhận được:\b";
        for (Integer key : keySet) {
            int idItem = key;
            int quantity = giftcode.Item.get(key);
            if (idItem == -1) {
                p.inventory.gold = Math.min(p.inventory.gold + (long) quantity, 2000000000L);
                textGift += quantity + " vàng\b";
            } else if (idItem == -2) {
                p.inventory.gem = Math.min(p.inventory.gem + quantity, 200000000);
                textGift += quantity + " ngọc\b";
            } else if (idItem == -3) {
                p.inventory.ruby = Math.min(p.inventory.ruby + quantity, 200000000);
                textGift += quantity + " ngọc khóa\b";
            } else {
                Item itemGiftTemplate = ItemService.gI().createNewItem((short) idItem);
                if (itemGiftTemplate != null) {
                    Item itemGift = new Item((short) idItem);
                    if (itemGift.template.type == 0 || itemGift.template.type == 1 || itemGift.template.type == 2
                            || itemGift.template.type == 3
                            || itemGift.template.type == 4 || itemGift.template.type == 5
                            || itemGift.template.type == 29 || itemGift.template.type == 27) {
                        if (itemGift.template.id == 457) {
                            itemGift.itemOptions.add(new Item.ItemOption(30, 0));
                        } else {
                            itemGift.itemOptions = giftcode.option;
                            itemGift.quantity = quantity;
                            addItemBag(p, itemGift, false);
                        }
                    } else {
                        itemGift.quantity = quantity;
                        addItemBag(p, itemGift, false);
                    }
                    textGift += "x" + quantity + " " + itemGift.template.name + "\b";
                }
            }
        }
        sendItemBags(p);
        Service.getInstance().sendThongBaoOK(p, textGift);
    }

    public List<Item> copyItemsBag(Player player) {
        return copyList(player.inventory.itemsBag);
    }

    private List<Item> copyList(List<Item> items) {
        List<Item> list = new ArrayList<>();
        for (Item item : items) {
            list.add(ItemService.gI().copyItem(item));
        }
        return list;
    }

    public boolean existItemBag(Player player, int tempId) {
        return existItemInList(player.inventory.itemsBag, tempId);
    }

    private boolean existItemInList(List<Item> list, int tempId) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isNotNullItem() && list.get(i).template.id == tempId) {
                return true;
            }
        }
        return false;
    }

    public boolean addItemBag(Player player, Item item, boolean upQuantityTo99) {
        if (ItemMapService.gI().isBlackBall(item.template.id)) {
            return BlackBallWar.gI().pickBlackBall(player, item);
        }

        //quả trứng
        if (item.template.id == 568) {
            if (player.mabuEgg == null) {
                MabuEgg.createMabuEgg(player);
            }
            return true;
        }

        //tennis spaceship
        if (item.template.id == 453) {
            player.haveTennisSpaceShip = true;
            return true;
        }
        //đùi gà nướng
        if (item.template.id == 74) {
            player.nPoint.setFullHpMp();
            PlayerService.gI().sendInfoHpMp(player);
            return true;
        }

        //gold, gem, ruby
        switch (item.template.type) {
            case 9:
                if (player.inventory.gold + item.quantity <= Inventory.LIMIT_GOLD) {
                    player.inventory.gold += item.quantity;
                    Service.getInstance().sendMoney(player);
                    return true;
                } else {
                    Service.getInstance().sendThongBao(player, "Vàng sau khi nhặt quá giới hạn cho phép");
                    return false;
                }
            case 10:
                player.inventory.gem += item.quantity;
                Service.getInstance().sendMoney(player);
                return true;
            case 34:
                player.inventory.ruby += item.quantity;
                Service.getInstance().sendMoney(player);
                return true;
        }

        //mở rộng hành trang - rương đồ
        if (item.template.id == 517) {
            if (player.inventory.itemsBag.size() < 80) {
                player.inventory.itemsBag.add(ItemService.gI().createItemNull());
                Service.getInstance().sendThongBaoOK(player, "Hành trang của bạn đã được mở rộng thêm 1 ô");
                return true;
            } else {
                Service.getInstance().sendThongBaoOK(player, "Hành trang của bạn đã đạt tối đa");
                return false;
            }
        } else if (item.template.id == 518) {
            if (player.inventory.itemsBox.size() < 80) {
                player.inventory.itemsBox.add(ItemService.gI().createItemNull());
                Service.getInstance().sendThongBaoOK(player, "Rương đồ của bạn đã được mở rộng thêm 1 ô");
                return true;
            } else {
                Service.getInstance().sendThongBaoOK(player, "Rương đồ của bạn đã đạt tối đa");
                return false;
            }
        }

        return addItemList(player.inventory.itemsBag, item, upQuantityTo99);
    }

    public boolean addItemBox(Player player, Item item, boolean upQuantityTo99) {
        return addItemList(player.inventory.itemsBox, item, upQuantityTo99);
    }

    public boolean addItemBody(Player player, Item item, boolean upQuantityTo99) {
        return addItemList(player.inventory.itemsBody, item, upQuantityTo99);
    }

    public boolean addItemNotUpToUpQuantity(List<Item> items, Item item) {
        for (int i = 0; i < items.size(); i++) {
            if (!items.get(i).isNotNullItem()) {
                items.set(i, item);
                return true;
            }
        }
        return false;
    }

    public boolean addItemList(List<Item> items, Item item, boolean upQuantityTo99) {
        //nếu item ko có option, add option rỗng vào
        if (item.itemOptions.isEmpty()) {
            item.itemOptions.add(new Item.ItemOption(73, 0));
        }

        //item cộng thêm chỉ số param: tự động luyện tập
        int optionId = isItemIncrementalOption(item);
        if (optionId != -1) {
            int param = 0;
            for (Item.ItemOption io : item.itemOptions) {
                if (io.optionTemplate.id == optionId) {
                    param = io.param;
                }
            }
            for (Item it : items) {
                if (it.isNotNullItem() && it.template.id == item.template.id) {
                    for (Item.ItemOption io : it.itemOptions) {
                        if (io.optionTemplate.id == optionId) {
                            io.param += param;
                        }
                    }
                    return true;
                }
            }
        }

        //item tăng số lượng
        if (isItemIncremental(item)) {
            for (Item it : items) {
                if (it.isNotNullItem() && it.template.id == item.template.id) {
                    if (item.template.id != 457 && item.template.id != 590
                            && item.template.id != 2013 && item.template.id != 2014 && item.template.id != 2015
                            && item.template.type != 27
                            && it.quantity + item.quantity > 99) {
                        if (upQuantityTo99) {
                            int add = 99 - it.quantity;
                            it.quantity = 99;
                            item.quantity -= add;
//                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        it.quantity += item.quantity;
                        if (upQuantityTo99) {
                            item.quantity = 0;
                        }
                        return true;
                    }
                }
            }
        }

        //add item vào ô mới
        for (int i = 0; i < items.size(); i++) {
            if (!items.get(i).isNotNullItem()) {
                items.set(i, ItemService.gI().copyItem(item));
                item.quantity = 0;
                return true;
            }
        }
        return false;
    }

    private boolean isItemIncremental(Item item) { //item cộng dồn số lượng
        switch (item.template.type) {
            case 8: // vật phẩm nhiệm vụ
            case 12: // ngọc rồng
            case 30: // sao pha lê
            case 6: // đậu thần
            case 29: // item time, đồ ăn
            case 25: // rađa dò ngọc namếc
            case 27: // đồ tạp
            case 14: // đá nâng cấp
            case 50: // vé đổi đồ hủy diệt
                return true;
            default:
                return false;
        }
    }

    private byte isItemIncrementalOption(Item item) { //trả về id option template
        int temp = item.template.id;
        byte opp = -1;
        switch (temp) {
            case 521:
                opp = 1;
                break;
            default:
                break;

        }
        return opp;
    }

    public void throwItem(Player player, int where, int index) {
        Item itemThrow = null;
        if (where == 0) {
            itemThrow = player.inventory.itemsBody.get(index);
            removeItemBody(player, index);
            sortItemBag(player);
            sendItemBody(player);
        } else if (where == 1) {
            itemThrow = player.inventory.itemsBag.get(index);
            if (itemThrow.template != null && itemThrow.template.id != 457) {
                removeItemBag(player, index);
                sortItemBag(player);
                sendItemBags(player);
            } else {
                Service.getInstance().sendThongBao(player, "Thưa ngài bình tĩnh");
            }
        }
        if (itemThrow == null) {
            return;
        }

//        ItemMap itemMap = new ItemMap(player.map, itemThrow.template.id,
//                itemThrow.quantity, player.location.x, player.location.y, player.id);
//        itemMap.options = itemThrow.itemOptions;
        //Service.getInstance().dropItemMap(player.map, itemMap);
        Service.getInstance().Send_Caitrang(player);
    }

    public void arrangeItems(List<Item> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (!list.get(i).isNotNullItem()) {
                int indexSwap = -1;
                for (int j = i + 1; j < list.size(); j++) {
                    if (list.get(j).isNotNullItem()) {
                        indexSwap = j;
                        break;
                    }
                }
                if (indexSwap != -1) {
                    Item sItem = ItemService.gI().createItemNull();
                    list.set(i, list.get(indexSwap));
                    list.set(indexSwap, sItem);
                } else {
                    break;
                }
            }
        }
    }

    private Item putItemBag(Player player, Item item) {
        for (int i = 0; i < player.inventory.itemsBag.size(); i++) {
            if (!player.inventory.itemsBag.get(i).isNotNullItem()) {
                player.inventory.itemsBag.set(i, item);
                Item sItem = ItemService.gI().createItemNull();
                return sItem;
            }
        }
        return item;
    }

    private Item putItemBox(Player player, Item item) {
        for (int i = 0; i < player.inventory.itemsBox.size(); i++) {
            if (!player.inventory.itemsBox.get(i).isNotNullItem()) {
                player.inventory.itemsBox.set(i, item);
                Item sItem = ItemService.gI().createItemNull();
                return sItem;
            }
        }
        return item;
    }

    private Item putItemBody(Player player, Item item) {
        Item sItem = item;
        if (item.isNotNullItem()) {
            if (item.template.type >= 0 && item.template.type <= 5
                    || item.template.type == 32) {
                if (item.template.gender == player.gender || item.template.gender == 3) {
                    long powerRequire = item.template.strRequire;
                    for (Item.ItemOption io : item.itemOptions) {
                        if (io.optionTemplate.id == 21) {
                            powerRequire = io.param * 1000000000L;
                            break;
                        }
                    }
                    if (powerRequire <= player.nPoint.power) {
                        sItem = player.inventory.itemsBody.get(item.template.type == 32 ? 6 : item.template.type);
                        player.inventory.itemsBody.set(item.template.type == 32 ? 6 : item.template.type, item);
                    } else {
                        Service.getInstance().sendThongBaoOK(player.isPet ? ((Pet) player).master : player, "Sức mạnh không đủ yêu cầu!");
                    }
                } else {
                    Service.getInstance().sendThongBaoOK(player.isPet ? ((Pet) player).master : player, "Trang bị không phù hợp!");
                }
            }
            if (item.template.type == 11) {
                long powerRequire = item.template.strRequire;
                if (powerRequire <= player.nPoint.power) {
                    sItem = player.inventory.itemsBody.get(item.template.type == 11 ? 8 : item.template.type);
                    player.inventory.itemsBody.set(item.template.type == 11 ? 8 : item.template.type, item);
                } else {
                    Service.getInstance().sendThongBaoOK(player.isPet ? ((Pet) player).master : player, "Sức mạnh không đủ yêu cầu!");
                }
            }else if (item.template.type == 23) {
                long powerRequire = item.template.strRequire;
                if (powerRequire <= player.nPoint.power) {
                    sItem = player.inventory.itemsBody.get(item.template.type == 23 ? 9 : item.template.type);
                    player.inventory.itemsBody.set(item.template.type == 23 ? 9 : item.template.type, item);
                } else {
                    Service.getInstance().sendThongBaoOK(player.isPet ? ((Pet) player).master : player, "Sức mạnh không đủ yêu cầu!");
                }
            }else if (item.template.type == 24) {
                long powerRequire = item.template.strRequire;
                if (powerRequire <= player.nPoint.power) {
                    sItem = player.inventory.itemsBody.get(item.template.type == 24 ? 9 : item.template.type);
                    player.inventory.itemsBody.set(item.template.type == 24 ? 9 : item.template.type, item);
                } else {
                    Service.getInstance().sendThongBaoOK(player.isPet ? ((Pet) player).master : player, "Sức mạnh không đủ yêu cầu!");
                }
            }
        }
        return sItem;
    }

    //==========================================================================
    public void itemBagToBody(Player player, int index) {
        if (index == -1) {
            Logger.log("Player error use item: "+ player.name);
            Service.getInstance().sendThongBaoOK(player, "Lỗi sửa item. liên hệ admin để được hỗ trợ");
            return;
        }
        Item item = player.inventory.itemsBag.get(index);
        if (item.isNotNullItem()) {
            player.inventory.itemsBag.set(index, putItemBody(player, item));
            sendItemBags(player);
            sendItemBody(player);
            Service.gI().sendFlagBag(player);
            Service.getInstance().Send_Caitrang(player);
            Service.getInstance().point(player);
        }
    }

    public void itemBodyToBag(Player player, int index) {
        Item item = player.inventory.itemsBody.get(index);
        if (item.isNotNullItem()) {
                if (index == 10) {
                    Service.getInstance().sendPetFollow(player, (short) 0);
                }
            player.inventory.itemsBody.set(index, putItemBag(player, item));
            sendItemBags(player);
            sendItemBody(player);
            Service.getInstance().Send_Caitrang(player);
            Service.getInstance().point(player);
        }
    }

    public void itemBagToPetBody(Player player, int index) {
        if (player.pet != null && player.pet.nPoint.power >= 1500000) {
            Item item = player.inventory.itemsBag.get(index);
            if (item.isNotNullItem()) {
                Item itemSwap = putItemBody(player.pet, item);
                player.inventory.itemsBag.set(index, itemSwap);
                sendItemBags(player);
                sendItemBody(player);
                Service.getInstance().Send_Caitrang(player.pet);
                Service.getInstance().Send_Caitrang(player);
                if (!itemSwap.equals(item)) {
                    Service.getInstance().point(player);
                    Service.getInstance().showInfoPet(player);
                }
            }
        } else {
            Service.getInstance().sendThongBaoOK(player, "Đệ tử phải đạt 1tr5 sức mạnh mới có thể mặc");
        }
    }

    public void itemPetBodyToBag(Player player, int index) {
        Item item = player.pet.inventory.itemsBody.get(index);
        if (item.isNotNullItem()) {
            player.pet.inventory.itemsBody.set(index, putItemBag(player, item));
            sendItemBags(player);
            sendItemBody(player);
            Service.getInstance().Send_Caitrang(player.pet);
            Service.getInstance().Send_Caitrang(player);
            Service.getInstance().point(player);
            Service.getInstance().showInfoPet(player);
        }
    }

    //--------------------------------------------------------------------------
    public void itemBoxToBodyOrBag(Player player, int index) {
        Item item = player.inventory.itemsBox.get(index);
        if (item.isNotNullItem()) {
            boolean done = false;
            if (item.template.type >= 0 && item.template.type <= 5 || item.template.type == 32) {
                Item itemBody = player.inventory.itemsBody.get(item.template.type == 32 ? 6 : item.template.type);
                if (!itemBody.isNotNullItem()) {
                    if (item.template.gender == player.gender || item.template.gender == 3) {
                        long powerRequire = item.template.strRequire;
                        for (Item.ItemOption io : item.itemOptions) {
                            if (io.optionTemplate.id == 21) {
                                powerRequire = io.param * 1000000000L;
                                break;
                            }
                        }
                        if (powerRequire <= player.nPoint.power) {
                            player.inventory.itemsBody.set(item.template.type == 32 ? 6 : item.template.type, item);
                            player.inventory.itemsBox.set(index, itemBody);
                            done = true;

                            sendItemBody(player);
                            Service.getInstance().Send_Caitrang(player);
                            Service.getInstance().point(player);
                        }
                    }
                }
            }
            if (!done) {
                if (addItemBag(player, item, true)) {
                    if (item.quantity == 0) {
                        Item sItem = ItemService.gI().createItemNull();
                        player.inventory.itemsBox.set(index, sItem);
                    }
                    sendItemBags(player);
                }
            }
            sendItemBox(player);
        }
    }

    public void itemBagToBox(Player player, int index) {
        Item item = player.inventory.itemsBag.get(index);
        if (item != null && item.template != null) {
            if (item.template.id == 457) {
                Service.getInstance().sendThongBao(player, "Bug ?");
                return;
            }
            if (addItemBox(player, item, true)) {
                if (item.quantity == 0) {
                    Item sItem = ItemService.gI().createItemNull();
                    player.inventory.itemsBag.set(index, sItem);
                }
                arrangeItems(player.inventory.itemsBag);
                sendItemBags(player);
                sendItemBox(player);
            }
        }
    }

    public void itemBodyToBox(Player player, int index) {
        Item item = player.inventory.itemsBody.get(index);
        if (item.isNotNullItem()) {
            player.inventory.itemsBody.set(index, putItemBox(player, item));
            arrangeItems(player.inventory.itemsBag);
            sendItemBody(player);
            sendItemBox(player);
            Service.getInstance().Send_Caitrang(player);
            sendItemBody(player);
            Service.getInstance().point(player);
        }
    }

    //--------------------------------------------------------------------------
    public void subQuantityItemsBag(Player player, Item item, int quantity) {
        subQuantityItem(player.inventory.itemsBag, item, quantity);
    }

    public void subQuantityItemsBody(Player player, Item item, int quantity) {
        subQuantityItem(player.inventory.itemsBody, item, quantity);
    }

    public void subQuantityItem(List<Item> items, Item item, int quantity) {
        if (item != null) {
            for (Item it : items) {
//                if (it.isNotNullItem() && it.template.id == item.template.id) {
                if (item.equals(it)) {
                    it.quantity -= quantity;
                    if (it.quantity <= 0) {
                        removeItem(items, item);
                    }
                    break;
                }
            }
        }
    }

    public void sortItemBag(Player player) {
        sortItem(player.inventory.itemsBag);
    }

    public void sortItem(List<Item> items) {
        int index = 0;
        for (Item item : items) {
            if (item.isNotNullItem()) {
                items.set(index, item);
                index++;
            }
        }
        for (int i = index; i < items.size(); i++) {
            Item item = ItemService.gI().createItemNull();
            items.set(i, item);
        }
    }

    //--------------------------------------------------------------------------
    public void removeItem(List<Item> items, int index) {
        Item item = ItemService.gI().createItemNull();
        items.set(index, item);
    }

    public void removeItem(List<Item> items, Item item) {
        Item it = ItemService.gI().createItemNull();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).equals(item)) {
                items.set(i, it);
                break;
            }
        }
    }

    public void removeItemBag(Player player, int index) {
        removeItem(player.inventory.itemsBag, index);
    }

    public void removeItemBag(Player player, Item item) {
        removeItem(player.inventory.itemsBag, item);
    }

    public void removeItemBody(Player player, int index) {
        removeItem(player.inventory.itemsBody, index);
    }

    public void removeItemPetBody(Player player, int index) {
        removeItemBody(player.pet, index);
    }

    public void removeItemBox(Player player, int index) {
        removeItem(player.inventory.itemsBox, index);
    }

    public Item findItem(List<Item> list, int tempId) {
        for (Item item : list) {
            if (item.isNotNullItem() && item.template.id == tempId) {
                return item;
            }
        }
        return null;
    }
    
    /// item sự kiện 
    public Item finditemnguyenlieuKeo(Player player) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && (item.template.id == 2013) && item.quantity >= 10) {
                return item;
            }
        }
        return null;
    }

    public Item finditemnguyenlieucapsutet(Player player) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && (item.template.id >= 1032 && item.template.id <= 1036) && item.quantity >= 10) {
                return item;
            }
        }
        return null;
    }
    public Item finditemnguyenlieucapsutet1(Player player) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && (item.template.id == 1032)) {
                return item;
            }
        }
        return null;
    }
    public Item finditemnguyenlieucapsutet2(Player player) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && (item.template.id == 1033)) {
                return item;
            }
        }
        return null;
    }
    public Item finditemnguyenlieucapsutet3(Player player) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && (item.template.id == 1034)) {
                return item;
            }
        }
        return null;
    }
    public Item finditemnguyenlieucapsutet4(Player player) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && (item.template.id == 1035)) {
                return item;
            }
        }
        return null;
    }
    public Item finditemnguyenlieucapsutet5(Player player) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && (item.template.id == 1036)) {
                return item;
            }
        }
        return null;
    }

    public Item finditemnguyenlieuBanh(Player player) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && (item.template.id == 2014) && item.quantity >= 10) {
                return item;
            }
        }
        return null;
    }
     
    public Item finditemnguyenlieuBingo(Player player) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && (item.template.id == 2015) && item.quantity >= 10) {
                return item;
            }
        }
        return null;
    }
    
    
     public Item finditemnguyenlieuGiokeo(Player player) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && (item.template.id == 2016) && item.quantity >= 3) {
                return item;
            }
        }
        return null;
    }
    public Item findthiepchuctet(Player player) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && (item.template.id == 399) && item.quantity >= 1) {
                return item;
            }
        }
        return null;
    }
    
     public Item finditemnguyenlieuVe(Player player) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && (item.template.id == 2018) && item.quantity >= 3) {
                return item;
            }
        }
        return null;
    }

    public Item finditemnguyenlieubanhchung(Player player) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && (item.template.id >= 748 && item.template.id <=751) && item.quantity >= 3) {
                return item;
            }
        }
        return null;
    }
    public Item finditemnguyenlieubanhchung1(Player player) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && (item.template.id == 748)) {
                return item;
            }
        }
        return null;
    }
    public Item finditemnguyenlieubanhchung2(Player player) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && (item.template.id == 749)) {
                return item;
            }
        }
        return null;
    }
    public Item finditemnguyenlieubanhchung3(Player player) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && (item.template.id == 750)) {
                return item;
            }
        }
        return null;
    }
    public Item finditemnguyenlieubanhchung4(Player player) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && (item.template.id == 751)) {
                return item;
            }
        }
        return null;
    }

    public Item finditemnguyenlieubanhtet(Player player) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && (item.template.id >= 749 && item.template.id <=751) && item.quantity >= 3) {
                return item;
            }
        }
        return null;
    }

    public Item finditemnguyenlieuHopmaquy(Player player) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && (item.template.id == 2017) && item.quantity >= 3) {
                return item;
            }
        }
        return null;
    }          
    
    

    public Item findItemBagByTemp(Player player, int tempId) {
        return findItem(player.inventory.itemsBag, tempId);
    }

    public Item findMealChangeDestroyClothes(Player player) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && (item.template.id >= 663 && item.template.id <= 667)
                    && item.quantity == 99) {
                return item;
            }
        }
        return null;
    }

    public Item findTicketChangeDestroyClothes(Player player, byte typeClothe) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.type == 50 && item.template.id == (2001 + typeClothe)) {
                return item;
            }
        }
        return null;
    }

    public Item findGodClothesByType(Player player, int type) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.type == type && (item.template.id >= 555 && item.template.id <= 567)) {
                return item;
            }
        }
        return null;
    }

    //--------------------------------------------------------------------------
    public void sendItemBags(Player player) {
        if (player.inventory != null) {
            arrangeItems(player.inventory.itemsBag);
            Message msg;
            try {
                msg = new Message(-36);
                msg.writer().writeByte(0);
                msg.writer().writeByte(player.inventory.itemsBag.size());
                for (int i = 0; i < player.inventory.itemsBag.size(); i++) {
                    Item item = player.inventory.itemsBag.get(i);
                    if (!item.isNotNullItem()) {
                        continue;
                    }
                    msg.writer().writeShort(item.template.id);
                    msg.writer().writeInt(item.quantity);
                    msg.writer().writeUTF(item.getInfo());
                    msg.writer().writeUTF(item.getContent());
                    msg.writer().writeByte(item.itemOptions.size()); //options
                    for (int j = 0; j < item.itemOptions.size(); j++) {
                        msg.writer().writeByte(item.itemOptions.get(j).optionTemplate.id);
                        msg.writer().writeShort(item.itemOptions.get(j).param);
                    }
                }

                player.sendMessage(msg);
                msg.cleanup();
            } catch (Exception e) {
            }
        }
    }

    public void sendItemBody(Player player) {
        Message msg;
        try {
            msg = new Message(-37);
            msg.writer().writeByte(0);
            msg.writer().writeShort(player.getHead());
            msg.writer().writeByte(player.inventory.itemsBody.size());
            for (Item item : player.inventory.itemsBody) {
                if (!item.isNotNullItem()) {
                    msg.writer().writeShort(-1);
                } else {
                    msg.writer().writeShort(item.template.id);
                    msg.writer().writeInt(item.quantity);
                    msg.writer().writeUTF(item.getInfo());
                    msg.writer().writeUTF(item.getContent());
                    List<Item.ItemOption> itemOptions = item.itemOptions;
                    msg.writer().writeByte(itemOptions.size());
                    for (Item.ItemOption itemOption : itemOptions) {
                        msg.writer().writeByte(itemOption.optionTemplate.id);
                        msg.writer().writeShort(itemOption.param);
                    }
                }
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
        Service.getInstance().Send_Caitrang(player);
    }

    public void sendItemBox(Player player) {
        Message msg;
        try {
            msg = new Message(-35);
            msg.writer().writeByte(0);
            msg.writer().writeByte(player.inventory.itemsBox.size());
            for (Item it : player.inventory.itemsBox) {
                msg.writer().writeShort(it.isNotNullItem() ? it.template.id : -1);
                if (it.isNotNullItem()) {
                    msg.writer().writeInt(it.quantity);
                    msg.writer().writeUTF(it.getInfo());
                    msg.writer().writeUTF(it.getContent());
                    msg.writer().writeByte(it.itemOptions.size());
                    for (Item.ItemOption io : it.itemOptions) {
                        msg.writer().writeByte(io.optionTemplate.id);
                        msg.writer().writeShort(io.param);
                    }
                }
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void openBox(Player player) {
        Message msg;
        try {
            msg = new Message(-35);
            msg.writer().writeByte(1);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void eatPea(Player player) {
        Item pea = null;
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.type == 6) {
                pea = item;
                break;
            }
        }
        if (pea != null) {
            int hpKiHoiPhuc = 0;
            int lvPea = Integer.parseInt(pea.template.name.substring(13));
            for (Item.ItemOption io : pea.itemOptions) {
                if (io.optionTemplate.id == 2) {
                    hpKiHoiPhuc = io.param * 1000;
                    break;
                }
                if (io.optionTemplate.id == 48) {
                    hpKiHoiPhuc = io.param;
                    break;
                }
            }
            player.nPoint.setHp(player.nPoint.hp + hpKiHoiPhuc);
            player.nPoint.setMp(player.nPoint.mp + hpKiHoiPhuc);
            PlayerService.gI().sendInfoHpMp(player);
            Service.getInstance().sendInfoPlayerEatPea(player);
            if (player.pet != null && player.zone.equals(player.pet.zone) && !player.pet.isDie()) {
                int statima = 100 * lvPea;
                player.pet.nPoint.stamina += statima;
                if (player.pet.nPoint.stamina > player.pet.nPoint.maxStamina) {
                    player.pet.nPoint.stamina = player.pet.nPoint.maxStamina;
                }
                player.pet.nPoint.setHp(player.pet.nPoint.hp + hpKiHoiPhuc);
                player.pet.nPoint.setMp(player.pet.nPoint.mp + hpKiHoiPhuc);
                Service.getInstance().sendInfoPlayerEatPea(player.pet);
                Service.getInstance().chatJustForMe(player, player.pet, "Cảm ơn sư phụ đã cho con đậu thần");
            }

            subQuantityItemsBag(player, pea, 1);
            sendItemBags(player);
        }
    }

    public int addPeaHarvest(Player player, byte level, int quantity) {
        Item pea = ItemService.gI().createNewItem(MagicTree.PEA_TEMP[level - 1], quantity);
        pea.itemOptions.add(new Item.ItemOption(level - 1 > 1 ? 2 : 48, MagicTree.PEA_PARAM[level - 1]));
        addItemBag(player, pea, true);
        if (pea.quantity > 0) {
            addItemBox(player, pea, true);
        }
        if (pea.quantity < quantity) {
            Service.getInstance().sendThongBao(player, "Bạn vừa thu hoạch được " + (quantity - pea.quantity) + " hạt " + pea.template.name);
        }
        return pea.quantity;
    }

    public Item getPeaBox(Player player) {
        for (Item item : player.inventory.itemsBox) {
            if (item.isNotNullItem() && item.template.type == 6) {
                return item;
            }
        }
        return null;
    }

    private byte getNumPeaBag(Player player) {
        return getNumPea(player.inventory.itemsBag);
    }

    private byte getNumPeaBox(Player player) {
        return getNumPea(player.inventory.itemsBox);
    }

    private byte getNumPea(List<Item> items) {
        byte num = 0;
        for (Item item : items) {
            if (item.isNotNullItem() && item.template.type == 6) {
                num += item.quantity;
            }
        }
        return num;
    }

    public byte getCountEmptyBag(Player player) {
        return getCountEmptyListItem(player.inventory.itemsBag);
    }

    public byte getCountEmptyListItem(List<Item> list) {
        byte count = 0;
        for (Item item : list) {
            if (!item.isNotNullItem()) {
                count++;
            }
        }
        return count;
    }
}
