package com.nrolove.models.pvp;

import com.nrolove.consts.ConstPlayer;
import com.nrolove.models.player.Player;
import com.nrolove.services.PlayerService;
import com.nrolove.services.Service;
import com.nrolove.services.func.PVPServcice;
import com.nrolove.utils.Util;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 *
 */
public class RevengePVP extends PVP {

    private static final int TIME_WAIT = 3000;

    private boolean changeTypePk;
    public long lastTimeGoToMapEnemy;

    public RevengePVP(Player player, Player enemy) {
        this.player1 = player;
        this.player2 = enemy;
        this.typePVP = TYPE_PVP_REVENGE;
    }

    @Override
    public void update() {
        if (!changeTypePk && Util.canDoWithTime(lastTimeGoToMapEnemy, TIME_WAIT)) {
            changeTypePk = true;
            if (player1.zone.equals(player2.zone)) {
                Service.getInstance().chat(player1, "Mau đền tội");
                Service.getInstance().sendThongBao(player2, "Có người tìm bạn trả thù");
                super.start();
                PlayerService.gI().changeAndSendTypePK(this.player1, ConstPlayer.PK_PVP);
                PlayerService.gI().changeAndSendTypePK(this.player2, ConstPlayer.PK_PVP);
            } else {
                PVPServcice.gI().removePVP(this);
                return;
            }
        }
        super.update();
    }

    @Override
    public void sendResultMatch(Player winer, Player loser, byte typeWin) {
        switch (typeWin) {
            case PVP.TYPE_DIE:
                Service.getInstance().chat(winer, "Chừa nha " + loser.name);
                Service.getInstance().chat(loser, "Cay quá");
                break;
            case PVP.TYPE_LEAVE_MAP:
                Service.getInstance().chat(winer, loser.name + " suy cho cùng cũng chỉ là con thỏ đế");
                break;
        }
    }

    @Override
    public void reward(Player plWin) {
    }

}
