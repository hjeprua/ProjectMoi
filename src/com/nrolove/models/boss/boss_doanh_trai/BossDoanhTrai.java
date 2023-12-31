package com.nrolove.models.boss.boss_doanh_trai;

import com.nrolove.models.boss.Boss;
import com.nrolove.models.boss.BossData;
import com.nrolove.models.boss.BossManager;
import com.nrolove.models.clan.Clan;
import com.nrolove.models.clan.ClanMember;
import com.nrolove.models.map.ItemMap;
import com.nrolove.models.map.phoban.DoanhTrai;
import com.nrolove.models.player.Player;
import com.nrolove.services.Service;
import com.nrolove.services.PlayerService;
import com.nrolove.utils.Util;

/**
 *
 * @author 💖 PuPuBug 💖
 *
 */
public abstract class BossDoanhTrai extends Boss {

    private int highestDame; //dame lớn nhất trong clan
    private int highestHp; //hp lớn nhất trong clan

    private int xHpForDame = 50; //dame gốc = highesHp / xHpForDame;
    private int xDameForHp = 50; //hp gốc = xDameForHp * highestDame;

    protected DoanhTrai doanhTrai;

    public BossDoanhTrai(byte id, BossData data, DoanhTrai doanhTrai) {
        super(id, data);
        this.xHpForDame = data.dame;

        int[] arrHp = data.hp[Util.nextInt(0, data.hp.length - 1)];
        this.xDameForHp = arrHp[Util.nextInt(0, arrHp.length - 1)];
        this.doanhTrai = doanhTrai;

        this.spawn(doanhTrai.clan);
    }

    private void spawn(Clan clan) {
        switch (this.typeDame) {
            case DAME_TIME_PLAYER_WITH_HIGHEST_HP_IN_CLAN:
                for (ClanMember cm : clan.getMembers()) {
                    for (Player pl : clan.membersInGame) {
                        if (pl.id == cm.id && pl.nPoint.hpMax >= highestHp) {
                            this.highestHp = pl.nPoint.hpMax;
                        }
                    }
                }
                this.nPoint.dameg = this.highestHp / this.xHpForDame;
                break;
        }
        switch (this.typeHp) {
            case HP_TIME_PLAYER_WITH_HIGHEST_DAME_IN_CLAN:
                for (ClanMember cm : clan.getMembers()) {
                    for (Player pl : clan.membersInGame) {
                        if (pl.id == cm.id && pl.nPoint.dame >= highestDame) {
                            this.highestDame = pl.nPoint.dame;
                        }
                    }
                }
                this.nPoint.hpg = this.highestDame * this.xDameForHp;
                this.nPoint.calPoint();
                this.nPoint.setFullHpMp();
                break;
        }
    }

    @Override
    public void idle() {

    }

    @Override
    public void checkPlayerDie(Player pl) {
        if (pl.isDie()) {
            Service.getInstance().chat(this, "Chừa chưa ranh con, nên nhớ ta là " + this.name);
        }
    }

    @Override
    public void initTalk() {

    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }

    @Override
    public void rewards(Player pl) {
        if (Util.isTrue(1, 5)) {
            ItemMap itemMap = new ItemMap(this.zone, 611, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x, 100), -1);
            Service.getInstance().dropItemMap(this.zone, itemMap);
        }
        int[] nro = {17, 18, 19, 20};
        ItemMap itemMap = new ItemMap(this.zone, nro[Util.nextInt(0, nro.length - 1)], 1,
                this.location.x, this.zone.map.yPhysicInTop(this.location.x, 100), -1);
        Service.getInstance().dropItemMap(this.zone, itemMap);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    protected void notifyPlayeKill(Player player) {
    }
}
