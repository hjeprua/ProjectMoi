package com.nrolove.models.boss;

import java.util.ArrayList;
import java.util.List;

import com.nrolove.consts.ConstPlayer;
import com.nrolove.consts.ConstRatio;
import com.nrolove.models.boss.iboss.BossInterface;
import com.nrolove.models.map.ItemMap;
import com.nrolove.models.map.Zone;
import com.nrolove.models.player.Player;
import com.nrolove.models.skill.Skill;
import com.nrolove.server.ServerNotify;
import com.nrolove.services.MapService;
import com.nrolove.services.PlayerService;
import com.nrolove.services.Service;
import com.nrolove.services.SkillService;
import com.nrolove.services.func.ChangeMapService;
import com.nrolove.utils.Logger;
import com.nrolove.utils.SkillUtil;
import com.nrolove.utils.Util;

/**
 *
 * @author 💖 PuPuBug 💖
 *
 */
public abstract class Boss extends Player implements BossInterface {

    // type dame
    public static final byte DAME_NORMAL = 0;
    public static final byte DAME_PERCENT_HP_HUND = 1;
    public static final byte DAME_PERCENT_MP_HUND = 2;
    public static final byte DAME_PERCENT_HP_THOU = 3;
    public static final byte DAME_PERCENT_MP_THOU = 4;
    public static final byte DAME_TIME_PLAYER_WITH_HIGHEST_HP_IN_CLAN = 5;

    // type hp
    public static final byte HP_NORMAL = 0;
    public static final byte HP_TIME_PLAYER_WITH_HIGHEST_DAME_IN_CLAN = 1;

    protected static final byte DO_NOTHING = 71;
    protected static final byte RESPAWN = 77;
    protected static final byte JUST_RESPAWN = 75; // khởi tạo lại, rồi chuyển sang nghỉ
    protected static final byte REST = 0; // boss chưa xuất hiện
    protected static final byte JUST_JOIN_MAP = 1; // vào map rồi chuyển sang nói chuyện lúc đầu
    protected static final byte TALK_BEFORE = 2; // chào hỏi chuyển sang trạng thái khác
    protected static final byte ATTACK = 3;
    protected static final byte IDLE = 4;
    protected static final byte DIE = 5;
    protected static final byte TALK_AFTER = 6;
    protected static final byte LEAVE_MAP = 7;

    // --------------------------------------------------------------------------
    protected BossData data;
    protected byte status;
    protected short[] outfit;
    protected byte typeDame;
    protected byte typeHp;
    protected int percentDame;
    protected short[] mapJoin;

    protected byte indexTalkBefore;
    protected String[] textTalkBefore;
    protected byte indexTalkAfter;
    protected String[] textTalkAfter;
    protected String[] textTalkMidle;

    protected long lastTimeTalk;
    protected int timeTalk;
    protected byte indexTalk;
    protected boolean doneTalkBefore;
    protected boolean doneTalkAffter;

    private long lastTimeRest;
    // thời gian nghỉ chuẩn bị đợt xuất hiện sau
    protected int secondTimeRestToNextTimeAppear = 1800;

    protected int maxIdle;
    protected int countIdle;

    private final List<Skill> skillsAttack;
    private final List<Skill> skillsSpecial;

    protected Player plAttack;
    protected int targetCountChangePlayerAttack;
    protected int countChangePlayerAttack;

    private long lastTimeStartLeaveMap;
    private int timeDelayLeaveMap = 2000;

    protected boolean joinMapIdle;
    protected long timeJoinZone;
    protected long timeReset;
    
    public Zone zoneFinal = null;

    protected void changeStatus(byte status) {
        this.status = status;
    }

    public Boss(byte id, BossData data) {
        super();
        this.id = id;
        this.timeReset = data.secondsRest * 1000;
        this.skillsAttack = new ArrayList<>();
        this.skillsSpecial = new ArrayList<>();
        this.data = data;
        this.isBoss = true;
        this.initTalk();
        this.respawn();
        setJustRest();
        BossManager.gI().addBoss(this);
    }

    @Override
    public void init() {
        this.name = data.name.replaceAll("%1", String.valueOf(Util.nextInt(0, 100)));
        this.gender = data.gender;
        this.typeDame = data.typeDame;
        this.typeHp = data.typeHp;
        this.nPoint.power = 1;
        this.nPoint.mpg = 752002;
        int dame = data.dame;
        int hp = 1;
        if (data.secondsRest != -1) {
            this.secondTimeRestToNextTimeAppear = data.secondsRest;
        }

        int[] arrHp = data.hp[Util.nextInt(0, data.hp.length - 1)];
        if (arrHp.length == 1) {
            hp = arrHp[0];
        } else {
            hp = Util.nextInt(arrHp[0], arrHp[1]);
        }
        switch (this.typeHp) {
            case HP_NORMAL:
                this.nPoint.hpg = hp;
                break;
            case HP_TIME_PLAYER_WITH_HIGHEST_DAME_IN_CLAN:

                break;
        }

        switch (this.typeDame) {
            case DAME_NORMAL:
                this.nPoint.dameg = dame;
                break;
            case DAME_PERCENT_HP_HUND:
                this.percentDame = dame;
                this.nPoint.dameg = this.nPoint.hpg * dame / 100;
                break;
            case DAME_PERCENT_MP_HUND:
                this.percentDame = dame;
                this.nPoint.dameg = this.nPoint.mpg * dame / 100;
                break;
            case DAME_PERCENT_HP_THOU:
                this.percentDame = dame;
                this.nPoint.dameg = this.nPoint.hp * dame / 1000;
                break;
            case DAME_PERCENT_MP_THOU:
                this.percentDame = dame;
                this.nPoint.dameg = this.nPoint.mpg * dame / 1000;
                break;
            case DAME_TIME_PLAYER_WITH_HIGHEST_HP_IN_CLAN:

                break;
        }
        this.nPoint.calPoint();
        this.outfit = data.outfit;
        this.mapJoin = data.mapJoin;
        if (data.timeDelayLeaveMap != -1) {
            this.timeDelayLeaveMap = data.timeDelayLeaveMap;
        }
        this.joinMapIdle = data.joinMapIdle;
        initSkill();
    }

    protected void initSkill() {
        this.playerSkill.skills.clear();
        this.skillsAttack.clear();
        this.skillsSpecial.clear();
        int[][] skillTemp = data.skillTemp;
        for (int i = 0; i < skillTemp.length; i++) {
            Skill skill = SkillUtil.createSkill(skillTemp[i][0], skillTemp[i][1]);
            skill.coolDown = skillTemp[i][2];
            this.playerSkill.skills.add(skill);
            switch (skillTemp[i][0]) {
                case Skill.DRAGON:
                case Skill.DEMON:
                case Skill.GALICK:
                case Skill.KAMEJOKO:
                case Skill.MASENKO:
                case Skill.ANTOMIC:
                    this.skillsAttack.add(skill);
                    break;
                case Skill.TAI_TAO_NANG_LUONG:
                case Skill.THAI_DUONG_HA_SAN:
                case Skill.BIEN_KHI:
                case Skill.THOI_MIEN:
                case Skill.TROI:
                case Skill.KHIEN_NANG_LUONG:
                case Skill.SOCOLA:
                case Skill.DE_TRUNG:
                    this.skillsSpecial.add(skill);
                    break;
            }
        }
    }

    @Override
    public void update() {
        super.update();
        try {
            if (!this.effectSkill.isHaveEffectSkill()
                    && !this.effectSkill.isCharging) {
                this.immortalMp();
                switch (this.status) {

                    case RESPAWN:
                        respawn();
                        break;
                    case JUST_RESPAWN:
                        this.changeStatus(REST);
                        break;
                    case REST:
                        if (Util.canDoWithTime(lastTimeRest, secondTimeRestToNextTimeAppear * 1000)) {
                            this.changeStatus(JUST_JOIN_MAP);
                        }
                        break;
                    case JUST_JOIN_MAP:
                        joinMap();
                        if (this.zone != null) {
                            changeStatus(TALK_BEFORE);
                        }
                        break;
                    case TALK_BEFORE:
                        if (talk()) {
                            if (!this.joinMapIdle) {
                                changeToAttack();
                            } else {
                                this.changeStatus(IDLE);
                            }
                        }
                        break;
                    case ATTACK:
                        this.talk();
                        if (this.playerSkill.prepareTuSat || this.playerSkill.prepareLaze
                                || this.playerSkill.prepareQCKK) {
                            break;
                        } else {
                            this.attack();
                        }
                        break;
                    case IDLE:
                        this.idle();
                        break;
                    case DIE:
                        if (this.joinMapIdle) {
                            this.changeToIdle();
                        }
                        changeStatus(TALK_AFTER);
                        break;
                    case TALK_AFTER:
                        if (talk()) {
                            changeStatus(LEAVE_MAP);
                            this.lastTimeStartLeaveMap = System.currentTimeMillis();
                        }
                        break;
                    case LEAVE_MAP:
                        if (Util.canDoWithTime(lastTimeStartLeaveMap, timeDelayLeaveMap)) {
                            this.leaveMap();
                            this.changeStatus(RESPAWN);
                        }
                        break;
                    case DO_NOTHING:
                        Logger.log(this.name + " UPDATE\n");
                        break;
                }
            }
            if (this.zone == null) {
                return;
            }
            if (this.zone.map.mapId == 129 && this.status != 3) {
                changeStatus((byte) 3);
                changeToAttack();
                return;
            }
        } catch (Exception e) {
            Logger.logException(Boss.class, e);
        }
    }

    @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        int dame = 0;
        if (this.isDie()) {
            return dame;
        } else {
            if (Util.isTrue(15, 100) && plAtt != null) {
                switch (plAtt.playerSkill.skillSelect.template.id) {
                    case Skill.TU_SAT:
                    case Skill.QUA_CAU_KENH_KHI:
                    case Skill.MAKANKOSAPPO:
                        break;
                    default:
                        return 0;
                }
            }
            dame = super.injured(plAtt, damage, piercing, isMobAttack);
            if (this.isDie()) {
                rewards(plAtt);
                notifyPlayeKill(plAtt);
                die();
            }
            return dame;
        }
    }

    protected void notifyPlayeKill(Player player) {
        if (player != null) {
            ServerNotify.gI().notify(player.name + " vừa tiêu diệt được " + this.name + " mọi người đều ngưỡng mộ");
        }
    }

    public int injuredNotCheckDie(Player plAtt, int damage, boolean piercing) {
        if (this.isDie()) {
            return 0;
        } else {
            int dame = super.injured(plAtt, damage, piercing, false);
            return dame;
        }
    }

    protected Skill getSkillAttack() {
        return skillsAttack.get(Util.nextInt(0, skillsAttack.size() - 1));
    }

    protected Skill getSkillSpecial() {
        return skillsSpecial.get(Util.nextInt(0, skillsSpecial.size() - 1));
    }

    protected Skill getSkillById(int skillId) {
        return SkillUtil.getSkillbyId(this, skillId);
    }

    @Override
    public void die() {
        setJustRest();
        changeStatus(DIE);
    }
    
    public void joinMapByZone(Player player) {
        if (player.zone != null) {
            this.zone = player.zone;
            ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
        }

    }

    @Override
    public void joinMap() {
        this.timeJoinZone = System.currentTimeMillis();
        if (this.zone == null) {
            this.zone = getMapCanJoin(mapJoin[Util.nextInt(0, mapJoin.length - 1)]);
        }
        if (this.zone != null && mapJoin.length > 1) {
            ChangeMapService.gI().changeMapBySpaceShip(this, this.zone, ChangeMapService.TENNIS_SPACE_SHIP);
            ServerNotify.gI().notify("BOSS " + this.name + " vừa xuất hiện tại " + this.zone.map.mapName);
    //        System.out.println("BOSS " + this.name + " : " + this.zone.map.mapName + " khu vực " + this.zone.zoneId + "(" + this.zone.map.mapId + ")");
        }
    }

    public Zone getMapCanJoin(int mapId) {
        Zone map = MapService.gI().getMapWithRandZone(mapId);
        if (map.isBossCanJoin(this)) {
            return map;
        } else {
            return getMapCanJoin(mapJoin[Util.nextInt(0, mapJoin.length - 1)]);
        }
    }

    @Override
    public void leaveMap() {
        MapService.gI().exitMap(this);
    }

    @Override
    public boolean talk() {
        switch (status) {
            case TALK_BEFORE:
                if (this.textTalkBefore == null || this.textTalkBefore.length == 0) {
                    return true;
                }
                if (Util.canDoWithTime(lastTimeTalk, 5000)) {
                    if (indexTalkBefore < textTalkBefore.length) {
                        this.chat(textTalkBefore[indexTalkBefore++]);
                        if (indexTalkBefore >= textTalkBefore.length) {
                            return true;
                        }
                        lastTimeTalk = System.currentTimeMillis();
                    } else {
                        return true;
                    }
                }
                break;
            case IDLE:
            case ATTACK:
                if (this.textTalkMidle == null || this.textTalkMidle.length == 0 || !Util.isTrue(1, 30)) {
                    return true;
                }
                if (Util.canDoWithTime(lastTimeTalk, Util.nextInt(15000, 20000))) {
                    this.chat(textTalkMidle[Util.nextInt(0, textTalkMidle.length - 1)]);
                }
                break;
            case TALK_AFTER:
                if (this.textTalkAfter == null || this.textTalkAfter.length == 0) {
                    return true;
                }
                if (Util.canDoWithTime(lastTimeTalk, 5000)) {
                    this.chat(textTalkAfter[indexTalkAfter++]);
                    if (indexTalkAfter >= textTalkAfter.length) {
                        return true;
                    }
                    lastTimeTalk = System.currentTimeMillis();
                }
                break;
        }
        return false;
    }

    @Override
    public void respawn() {
        this.init();
        this.indexTalkBefore = 0;
        this.indexTalkAfter = 0;
        this.nPoint.setFullHpMp();
        this.changeStatus(JUST_RESPAWN);
    }

    protected void goToPlayer(Player pl, boolean isTeleport) {
        goToXY(pl.location.x, pl.location.y, isTeleport);
    }

    protected void goToXY(int x, int y, boolean isTeleport) {
        if (!isTeleport) {
            byte dir = (byte) (this.location.x - x < 0 ? 1 : -1);
            byte move = (byte) Util.nextInt(50, 100);
            PlayerService.gI().playerMove(this, this.location.x + (dir == 1 ? move : -move), y);
        } else {
            ChangeMapService.gI().changeMapYardrat(this, this.zone, x, y);
        }
    }

    public void gotoMove(int _toX, int _toY) {
        if (_toX != this.location.x) {
            this.location.x = _toX;
        }
        if (_toY != this.location.y) {
            this.location.y = _toY;
        }
        MapService.gI().sendPlayerMove(this);
    }

    protected int getRangeCanAttackWithSkillSelect() {
        int skillId = this.playerSkill.skillSelect.template.id;
        if (skillId == Skill.KAMEJOKO || skillId == Skill.MASENKO || skillId == Skill.ANTOMIC) {
            return Skill.RANGE_ATTACK_CHIEU_CHUONG;
        } else {
            return Skill.RANGE_ATTACK_CHIEU_DAM;
        }
    }

    @Override
    public Player getPlayerAttack() throws Exception {
        if (this.zone == null) {
            return null;
        }
        if (countChangePlayerAttack < targetCountChangePlayerAttack
                && plAttack != null && plAttack.zone != null
                && plAttack.zone.equals(this.zone)) {
            if (!plAttack.isDie() && !plAttack.effectSkin.isVoHinh) {
                this.countChangePlayerAttack++;
                return plAttack;
            } else {
                plAttack = null;
            }
        } else {
            this.targetCountChangePlayerAttack = Util.nextInt(10, 20);
            this.countChangePlayerAttack = 0;
            plAttack = this.zone.getRandomPlayerInMap();
            if (plAttack != null && plAttack.effectSkin.isVoHinh) {
                plAttack = null;
            }
        }
        return plAttack;
    }

    @Override
    public void attack() {
        try {
            Player pl = getPlayerAttack();
            if (pl != null && !pl.isDie()) {
                this.playerSkill.skillSelect = this.getSkillAttack();
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(15, ConstRatio.PER100)) {
                        if (SkillUtil.isUseSkillChuong(this)) {
                            goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 80)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50),
                                    false);
                        } else {
                            goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 30)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50),
                                    false);
                        }
                    }
                    SkillService.gI().useSkill(this, pl, null);
                    checkPlayerDie(pl);
                } else {
                    goToPlayer(pl, false);
                }
            }
        } catch (Exception ex) {
            Logger.logException(Boss.class, ex);
        }
    }

    public void attack(Player pl) {
        try {
            if (pl != null && !pl.isDie()) {
                this.playerSkill.skillSelect = this.getSkillAttack();
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(15, ConstRatio.PER100)) {
                        if (SkillUtil.isUseSkillChuong(this)) {
                            goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 80)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50),
                                    false);
                        } else {
                            goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 30)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50),
                                    false);
                        }
                    }
                    SkillService.gI().useSkill(this, pl, null);
                    checkPlayerDie(pl);
                } else {
                    goToPlayer(pl, false);
                }
            }
        } catch (Exception ex) {
            Logger.logException(Boss.class, ex);
        }
    }

    private void immortalMp() {
        this.nPoint.mp = this.nPoint.mpg;
    }

    protected abstract boolean useSpecialSkill();

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public short getHead() {
        return this.outfit[0];
    }

    @Override
    public short getBody() {
        return this.outfit[1];
    }

    @Override
    public short getLeg() {
        return this.outfit[2];
    }

    // status
    protected void changeIdle() {
        this.changeStatus(IDLE);
    }

    /**
     * Đổi sang trạng thái tấn công
     */
    protected void changeAttack() {
        this.changeStatus(ATTACK);
    }

    public void setJustRest() {
        this.lastTimeRest = System.currentTimeMillis();
    }

    public void setJustRestToFuture() {
        this.lastTimeRest = System.currentTimeMillis() + 8640000000L;
    }

    @Override
    public void dropItemReward(int tempId, int playerId, int... quantity) {
        ItemMap itemMap = new ItemMap(this.zone, tempId,
                (quantity != null && quantity.length == 1) ? quantity[0] : 1,
                this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                        this.location.y - 24),
                playerId);
        Service.getInstance().dropItemMap(itemMap.zone, itemMap);
    }

    /**
     * Đổi trạng thái máu trắng -> đỏ, chuyển trạng thái tấn công
     */
    public void changeToAttack() {
        PlayerService.gI().changeAndSendTypePK(this, ConstPlayer.PK_ALL);
        changeStatus(ATTACK);
    }

    /**
     * Đổi trạng thái máu đỏ -> trắng, chuyển trạng thái đứng
     */
    public void changeToIdle() {
        PlayerService.gI().changeAndSendTypePK(this, ConstPlayer.NON_PK);
        changeStatus(IDLE);
    }

    protected void chat(String text) {
        Service.getInstance().chat(this, text);
    }

    protected Zone getCurrZone() {
        if (this.zone == null) {
            this.zone = getMapCanJoin(mapJoin[Util.nextInt(0, mapJoin.length - 1)]);
        }
        return this.zone;
    }
}
