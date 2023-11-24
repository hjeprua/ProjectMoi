package com.nrolove.services.func;

import java.util.List;

import com.nrolove.models.item.Item;
import com.nrolove.models.player.Pet;
import com.nrolove.models.player.Player;
import com.nrolove.server.io.Message;
import com.nrolove.services.Service;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 *
 */
public class InventoryServiceNew {

    private static InventoryServiceNew i;

    public static InventoryServiceNew gI() {
        if (i == null) {
            i = new InventoryServiceNew();
        }
        return i;
    }

    private Item putItemBody(Player player, Item item) {
        Item sItem = item;
        if (!item.isNotNullItem()) {
            return sItem;
        }
        if (player.isPl() && !player.isPet) {
            switch (item.template.type) {
                case 0: // áo
                case 1: // quần
                case 2: // găng
                case 3: // giầy
                case 4: // rada
                case 5: // Cải trang
                case 32: //giap luyen tap
                case 23:
                case 24:
                case 27:
                case 11: // vật phẩm đeo lưng
                case 72: // Pet
                    break;
                default:
                    Service.gI().sendThongBaoOK(player.isPet ? ((Pet) player).master : player, "Trang bị không phù hợp!");
                    return sItem;
            }
        } else if (!player.isPl() && player.isPet) {
            switch (item.template.type) {
                case 0: // áo
                case 1: // quần
                case 2: // găng
                case 3: // giầy
                case 4: // rada
                case 5: // Cải trang

                case 32: //giap luyen tap
                    break;
                default:
                    Service.gI().sendThongBaoOK(player.isPet ? ((Pet) player).master : player, "Trang bị không phù hợp!");
                    return sItem;
            }
        }
        if (item.template.gender < 3 && item.template.gender != player.gender) {
            Service.gI().sendThongBaoOK(player.isPet ? ((Pet) player).master : player, "Trang bị không phù hợp!");
            return sItem;
        }
        long powerRequire = item.template.strRequire;
        for (Item.ItemOption io : item.itemOptions) {
            if (io.optionTemplate.id == 21) {
                powerRequire = io.param * 1000000000L;
                break;
            }
        }
        if (player.nPoint.power < powerRequire) {
            Service.gI().sendThongBaoOK(player.isPet ? ((Pet) player).master : player, "Sức mạnh không đủ yêu cầu!");
            return sItem;
        }
        int index = -1;
        if (player.isPl() && !player.isPet) {
            switch (item.template.type) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    index = item.template.type;
                    break;
                case 32:
                    index = 6;
                    break;
                case 50:
                case 23:
                case 24:
                    index = 7;
                    break;
                case 11:
                    index = 8;
                    break;
                case 27:
                    index = 9;
                    break;
                case 72:
                    index = 10;
            }
        } else if (!player.isPl() && player.isPet) {
            switch (item.template.type) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    index = item.template.type;
                    break;
                case 32:
                    index = 6;
                    break;
            }
        }
        sItem = player.inventory.itemsBody.get(index);
        player.inventory.itemsBody.set(index, item);
        return sItem;
    }

    public void sendItemBags(Player player) {
        sortItems(player.inventory.itemsBag);
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
                msg.writer().writeByte(item.itemOptions.size()); // options
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

    public void sortItems(List<Item> list) {
        int first = -1;
        int last = -1;
        Item tempFirst = null;
        Item tempLast = null;
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).isNotNullItem()) {
                first = i;
                tempFirst = list.get(i);
                break;
            }
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).isNotNullItem()) {
                last = i;
                tempLast = list.get(i);
                break;
            }
        }
        if (first != -1 && last != -1 && first < last) {
            list.set(first, tempLast);
            list.set(last, tempFirst);
            sortItems(list);
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

    public void itemBagToBody(Player player, int index) {
        Item item = player.inventory.itemsBag.get(index);
        if (item.isNotNullItem()) {
            player.inventory.itemsBag.set(index, putItemBody(player, item));
            sendItemBags(player);
            sendItemBody(player);
            Service.getInstance().Send_Caitrang(player);
            Service.getInstance().point(player);
            Service.getInstance().sendFlagBag(player);
        }
    }

}
