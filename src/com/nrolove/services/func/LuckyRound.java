package com.nrolove.services.func;

import com.nrolove.consts.ConstNpc;
import com.nrolove.models.Template.CaiTrang;
import com.nrolove.models.item.Item;
import com.nrolove.models.player.Player;
import com.nrolove.server.Manager;
import com.nrolove.server.io.Message;
import com.nrolove.services.InventoryService;
import com.nrolove.services.RewardService;
import com.nrolove.services.Service;
import java.util.List;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 *
 */
public class LuckyRound {

    //1 gem and ruby
    public static final byte USING_GEM = 2;
    public static final byte USING_GOLD = 0;

    private static final byte PRICE_GEM = 4;
    private static final int PRICE_GOLD = 20000000;

    private static LuckyRound i;

    private LuckyRound() {

    }

    public static LuckyRound gI() {
        if (i == null) {
            i = new LuckyRound();
        }
        return i;
    }

    public void openCrackBallUI(Player pl, byte type) {
        pl.iDMark.setTypeLuckyRound(type);
        Message msg;
        try {
            msg = new Message(-127);
            msg.writer().writeByte(0);
            msg.writer().writeByte(7);
            for (int i = 0; i < 7; i++) {
                msg.writer().writeShort(419 + i);
            }
            msg.writer().writeByte(type); //type price
            msg.writer().writeInt(type == USING_GEM ? PRICE_GEM : PRICE_GOLD); //price
            msg.writer().writeShort(14); //id ticket
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void readOpenBall(Player player, Message msg) {
        try {
            byte type = msg.reader().readByte();
            byte count = msg.reader().readByte();
            switch (player.iDMark.getTypeLuckyRound()) {
                case USING_GEM:
                    openBallByGem(player, count);
                    break;
                case USING_GOLD:
                    openBallByGold(player, count);
                    break;
            }
        } catch (Exception e) {
            openCrackBallUI(player, player.iDMark.getTypeLuckyRound());
        }
    }

    private void openBallByGem(Player player, byte count) {
        int gemNeed = (count * PRICE_GEM);
        if (player.inventory.getGemAndRuby() < gemNeed) {
            Service.getInstance().sendThongBao(player, "Bạn không đủ ngọc để mở");
            return;
        } else {
            if (count < InventoryService.gI().getCountEmptyListItem(player.inventory.itemsBoxCrackBall)) {
                player.inventory.subGemAndRuby(gemNeed);
                List<Item> list = RewardService.gI().getListItemLuckyRound(player, count);
                addItemToBox(player, list);
                sendReward(player, list);
                Service.getInstance().sendMoney(player);
            } else {
                Service.getInstance().sendThongBao(player, "Rương phụ đã đầy");
            }
        }
    }

    private void openBallByGold(Player player, byte count) {
        int goldNeed = (count * PRICE_GOLD);
        if (player.inventory.gold < goldNeed) {
            Service.getInstance().sendThongBao(player, "Bạn không đủ vàng để mở");
            return;
        } else {
            if (count < InventoryService.gI().getCountEmptyListItem(player.inventory.itemsBoxCrackBall)) {
                player.inventory.gold -= goldNeed;
                List<Item> list = RewardService.gI().getListItemLuckyRound(player, count);
                addItemToBox(player, list);
                sendReward(player, list);
                Service.getInstance().sendMoney(player);
            } else {
                Service.getInstance().sendThongBao(player, "Rương phụ đã đầy");
            }
        }
    }

    private void sendReward(Player player, List<Item> items) {
        Message msg;
        try {
            msg = new Message(-127);
            msg.writer().writeByte(1);
            msg.writer().writeByte(items.size());
            for (Item item : items) {
                msg.writer().writeShort(item.template.iconID);
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    private void addItemToBox(Player player, List<Item> items) {
        for (Item item : items) {
            InventoryService.gI().addItemNotUpToUpQuantity(player.inventory.itemsBoxCrackBall, item);
        }
    }

//    public void openBoxItemLuckyRound(Player player) {
//        player.iDMark.setShopId(ConstNpc.SIDE_BOX_LUCKY_ROUND);
//        InventoryService.gI().arrangeItems(player.inventory.itemsBoxCrackBall);
//        Message msg;
//        try {
//            msg = new Message(-44);
//            msg.writer().writeByte(4);
//            msg.writer().writeByte(1);
//            msg.writer().writeUTF("Rương đồ");
//            int n = player.inventory.itemsBoxCrackBall.size() - 
//                    InventoryService.gI().getCountEmptyListItem(player.inventory.itemsBoxCrackBall);
//            msg.writer().writeByte(n);
//            for (int i = 0; i < n; i++) {
//                Item item = player.inventory.itemsBoxCrackBall.get(i);
//                msg.writer().writeShort(item.template.id);
//                msg.writer().writeUTF("NROLOVE LUCKY");
//                msg.writer().writeByte(item.itemOptions.size());
//                for (Item.ItemOption io : item.itemOptions) {
//                    msg.writer().writeByte(io.optionTemplate.id);
//                    msg.writer().writeShort(io.param);
//                }
//                msg.writer().writeByte(1);
//                CaiTrang ct = Manager.gI().getCaiTrangByItemId(item.template.id);
//                msg.writer().writeByte(ct != null ? 1 : 0);
//                if (ct != null) {
//                    msg.writer().writeShort(ct.getID()[0]);
//                    msg.writer().writeShort(ct.getID()[1]);
//                    msg.writer().writeShort(ct.getID()[2]);
//                    msg.writer().writeShort(ct.getID()[3]);
//                }
//            }
//            player.sendMessage(msg);
//            msg.cleanup();
//        } catch (Exception e) {
//        }
//    }

}
