package com.nrolove.models.player;

import com.nrolove.models.skill.PlayerSkill;
import java.util.List;
import com.nrolove.models.clan.Clan;
import com.nrolove.models.intrinsic.IntrinsicPlayer;
import com.nrolove.models.item.Item;
import com.nrolove.models.item.ItemTime;
import com.nrolove.models.npc.specialnpc.MagicTree;
import com.nrolove.consts.ConstPlayer;
import com.nrolove.consts.ConstTask;
import com.nrolove.models.npc.specialnpc.MabuEgg;
import com.nrolove.models.mob.MobMe;
import com.nrolove.data.DataGame;
import com.nrolove.models.DaiHoiVoThuat.DaiHoiVoThuat;
import com.nrolove.models.clan.ClanMember;
import com.nrolove.models.map.MaBu12h.MapMaBu;
import com.nrolove.models.map.TrapMap;
import com.nrolove.models.map.Zone;
import com.nrolove.models.map.blackball.BlackBallWar;
import com.nrolove.models.pvp.PVP;
import com.nrolove.services.Service;
import com.nrolove.server.io.Message;
import com.nrolove.server.io.Session;
import com.nrolove.models.task.TaskPlayer;
import com.nrolove.server.Client;
import com.nrolove.server.Manager;
import com.nrolove.services.EffectSkillService;
import com.nrolove.services.FriendAndEnemyService;
import com.nrolove.services.MapService;
import com.nrolove.services.PetService;
import com.nrolove.services.TaskService;
import com.nrolove.services.func.ChangeMapService;
import com.nrolove.services.func.CombineNew;
import com.nrolove.services.func.PVPServcice;
import com.nrolove.utils.Logger;
import com.nrolove.utils.Util;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 *
 */
public class Player {

    public long lastTimeChangeZone;
    public long lastTimeChatGlobal;
    public long lastTimeChatPrivate;

    private Session session;

    public byte countSaveFail;
    public boolean beforeDispose;

    public long timeFixInventory;
    public boolean isPet;
    public boolean isNewMiniPet;
    public boolean isBoss;

    public int playerTradeId = -1;
    public Player playerTrade;

    public int mapIdBeforeLogout;
    public List<Zone> mapBlackBall;
    public List<Zone> mapMaBu;
    public Zone zone;
    public Zone mapBeforeCapsule;
    public List<Zone> mapCapsule;
    public Pet pet;
    public MiniPet minipet;
    public MobMe mobMe;
    public Location location;
    public SetClothes setClothes;
    public EffectSkill effectSkill;
    public MabuEgg mabuEgg;
    public TaskPlayer playerTask;
    public ItemTime itemTime;
    public Fusion fusion;
    public MagicTree magicTree;
    public IntrinsicPlayer playerIntrinsic;
    public Inventory inventory;
    public PlayerSkill playerSkill;
    public CombineNew combineNew;
    public IDMark iDMark;
     public SkillSpecial skillSpecial;
    public Charms charms;
    public EffectSkin effectSkin;
    public Gift gift;
    public NPoint nPoint;
    public RewardBlackBall rewardBlackBall;
    public EffectFlagBag effectFlagBag;
    public FightMabu fightMabu;

    public Clan clan;
    public ClanMember clanMember;

    public ListFriendEnemy<Friend> friends;
    public ListFriendEnemy<Enemy> enemies;

    protected boolean actived = false;
    public boolean loaded;

    public long id;
    public String name;
    public byte gender;
    public boolean isNewMember;
    public short head;

    public byte typePk;

    public long lastTimeNotifyTimeHoldBlackBall;
    public long lastTimeHoldBlackBall;
    public int tempIdBlackBallHold = -1;
    public int tempIdNamecBallHold = -1;
    public boolean isHoldBlackBall;
    public boolean isHoldNamecBall;

    public byte cFlag;
    public long lastTimeChangeFlag;
    public long lastTimeTrade;

    public boolean haveTennisSpaceShip;
    private byte useSpaceShip;

    public boolean isGoHome;

    public boolean justRevived;
    public long lastTimeRevived;
    public boolean immortal;

    public long lastTimeBan;
    public boolean isBan;

    public boolean isGotoFuture;
    public long lastTimeGoToFuture;

    public boolean isGoToBDKB;
    public long lastTimeGoToBDKB;
    public long lastTimeAnXienTrapBDKB;

    public long lastTimePickItem;
    public boolean isChuongX3X4 = false;
    public long timeX3X4 = 0;
    
    public DaiHoiVoThuat DaiHoiVoThuat = new DaiHoiVoThuat(this);

    public Player() {
        location = new Location();
        nPoint = new NPoint(this);
        inventory = new Inventory(this);
        playerSkill = new PlayerSkill(this);
        setClothes = new SetClothes(this);
        effectSkill = new EffectSkill(this);
        skillSpecial = new SkillSpecial(this);
        fusion = new Fusion(this);
        playerIntrinsic = new IntrinsicPlayer(this);
        rewardBlackBall = new RewardBlackBall(this);
        effectFlagBag = new EffectFlagBag(this);
        fightMabu = new FightMabu(this);
        //----------------------------------------------------------------------
        iDMark = new IDMark();
        combineNew = new CombineNew();
        playerTask = new TaskPlayer(this);
        friends = new ListFriendEnemy<>(this);
        enemies = new ListFriendEnemy<>(this);
        itemTime = new ItemTime(this);
        charms = new Charms(this);
        gift = new Gift(this);
        effectSkin = new EffectSkin(this);
    }

    //--------------------------------------------------------------------------
    public void setUseSpaceShip(byte useSpaceShip) {
        // 0 - không dùng
        // 1 - tàu vũ trụ theo hành tinh
        // 2 - dịch chuyển tức thời
        // 3 - tàu tenis
        this.useSpaceShip = useSpaceShip;
    }

    public byte getUseSpaceShip() {
        return this.useSpaceShip;
    }

    public boolean isDie() {
        if (this.nPoint != null) {
            return this.nPoint.hp <= 0;
        } else {
            return true;
        }
    }

    //--------------------------------------------------------------------------
    public void setSession(Session session) {
        this.session = session;
    }

    public void sendMessage(Message msg) {
        if (this.session != null) {
            session.sendMessage(msg);
        }
    }

    public Session getSession() {
        return this.session;
    }

    public void update() {
        if (!this.beforeDispose) {
            try {
                if (!isBan) {
                    if (nPoint != null) {
                        nPoint.update();
                    }
                    if (fusion != null) {
                        fusion.update();
                    }
                    if (effectSkin != null) {
                        effectSkill.update();
                    }
                    if (mobMe != null) {
                        mobMe.update();
                    }
                    if (effectSkin != null) {
                        effectSkin.update();
                    }
                    if (pet != null) {
                        pet.update();
                    }
                    if (minipet != null) {
                        minipet.update();
                    }
                    if (magicTree != null) {
                        magicTree.update();
                    }
                    if (itemTime != null) {
                        itemTime.update();
                    }
                    BlackBallWar.gI().update(this);
                    MapMaBu.gI().update(this);
                    if (isGotoFuture && Util.canDoWithTime(lastTimeGoToFuture, 6000)) {
                        ChangeMapService.gI().changeMapBySpaceShip(this, 102, -1, Util.nextInt(60, 200));
                        this.isGotoFuture = false;
                    }
                    if (isGoToBDKB && Util.canDoWithTime(lastTimeGoToBDKB, 6000)) {
                        ChangeMapService.gI().changeMapBySpaceShip(this, 135, -1, 35);
                        this.isGoToBDKB = false;
                    }
                    if (this.zone != null) {
                        TrapMap trap = this.zone.isInTrap(this);
                        if (trap != null) {
                            trap.doPlayer(this);
                        }
                    }
                if(isPl() && this.inventory != null){
                        if(this.inventory.itemsBody.get(5).template != null && (this.inventory.itemsBody.get(5).template.id == 710
                                || this.inventory.itemsBody.get(5).template.id == 711)
                                && !this.isChuongX3X4 && (System.currentTimeMillis() >= this.timeX3X4)) {
                            int perX3X4 = 0;
                            if(perX3X4 == 0) {
                                this.isChuongX3X4 = true;
                                this.timeX3X4 = System.currentTimeMillis() + 60000;
                                if(this.inventory.itemsBody.get(5).template.id == 710) {
                                    this.nPoint.dame = this.nPoint.dame*3;
                                } else if (this.inventory.itemsBody.get(5).template.id == 711){
                                    this.nPoint.dame = this.nPoint.dame*4;
                                }
                            }
                        }
                        if(this.minipet == null && this.inventory.itemsBody.get(9).isNotNullItem()){
                            Item item = this.inventory.itemsBody.get(9);
                            switch (item.template.id) {
                                case 892:
                                    PetService.Pet2(this, 882, 883, 884);
                                    Service.getInstance().point(this);
                                    break;
                                case 893:
                                    PetService.Pet2(this, 885, 886, 887);
                                    Service.getInstance().point(this);
                                    break;
                                case 908:
                                    PetService.Pet2(this, 891, 892, 893);
                                    Service.getInstance().point(this);
                                    break;
                                case 909:
                                    PetService.Pet2(this, 897, 898, 899);
                                    Service.getInstance().point(this);
                                    break;
                                case 910:
                                    PetService.Pet2(this, 894, 895, 896);
                                    Service.getInstance().point(this);
                                    break;
                                case 916:
                                    PetService.Pet2(this, 931, 932, 933);
                                    Service.getInstance().point(this);
                                    break;
                                case 917:
                                    PetService.Pet2(this, 928, 929, 930);
                                    Service.getInstance().point(this);
                                    break;
                                case 918:
                                    PetService.Pet2(this, 925, 926, 927);
                                    Service.getInstance().point(this);
                                    break;
                                case 919:
                                    PetService.Pet2(this, 934, 935, 936);
                                    Service.getInstance().point(this);
                                    break;
                                case 936:
                                    PetService.Pet2(this, 718, 719, 720);
                                    Service.getInstance().point(this);
                                    break;
                                case 942:
                                    PetService.Pet2(this, 966, 967, 968);
                                    Service.getInstance().point(this);
                                    break;
                                case 943:
                                    PetService.Pet2(this, 969, 970, 971);
                                    Service.getInstance().point(this);
                                    break;
                                case 944:
                                    PetService.Pet2(this, 972, 973, 974);
                                    Service.getInstance().point(this);
                                    break;
                                case 1039:
                                    PetService.Pet2(this, 1092, 1093, 1094);
                                    Service.getInstance().point(this);
                                    break;
                                case 1040:

                                    PetService.Pet2(this, 1089, 1090, 1091);
                                    Service.getInstance().point(this);
                                    break;
                                case 1046:
                                    PetService.Pet2(this, 95, 96, 97);
                                    Service.getInstance().point(this);
                                    break;
                            }
                        }
                        if(minipet != null && !this.inventory.itemsBody.get(9).isNotNullItem()){
                            MapService.gI().exitMap(minipet);
                            minipet.dispose();
                            minipet = null;
                        }
                    }
                } else {
                    if (Util.canDoWithTime(lastTimeBan, 5000)) {
                        Client.gI().kickSession(session);
                    }
                }
            } catch (Exception e) {
                Logger.logException(Player.class, e, "Lỗi tại player: " + this.name);
            }
        }
    }
    //--------------------------------------------------------------------------
    /*
     * {380, 381, 382}: ht lưỡng long nhất thể xayda trái đất 
     * {383, 384, 385}: ht porata xayda trái đất 
     * {391, 392, 393}: ht namếc
     * {870, 871, 872}: ht c2 trái đất
     * {873, 874, 875}: ht c2 namếc
     * {867, 878, 869}: ht c2 xayda
     */
    private static final short[][] idOutfitFusion = {
        {380, 381, 382}, {383, 384, 385}, {391, 392, 393},
        {870, 871, 872}, {873, 874, 875}, {867, 868, 869}
    };

    public byte getAura() {
        if (Manager.TOP_PLAYERS.contains(this.name)) {
            return 1;
        }
        return -1;
    }

    public short getHead() {
        if (effectSkill != null && effectSkill.isMonkey) {
            return (short) ConstPlayer.HEADMONKEY[effectSkill.levelMonkey - 1];
        } else if (effectSkill != null && effectSkill.isSocola) {
            return 412;
        } else if (fusion != null && fusion.typeFusion != ConstPlayer.NON_FUSION) {
            if (fusion.typeFusion == ConstPlayer.LUONG_LONG_NHAT_THE) {
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 0][0];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
                if (this.pet.isMabu) {
                    return idOutfitFusion[3 + this.gender][0];
                }
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 1][0];
                    } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
//                if (this.pet.typePet == 1) {
//                    return idOutfitFusion[3 + this.gender][0];
                    }
            return idOutfitFusion[3 + this.gender][0];
        } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem()) {
            int head = inventory.itemsBody.get(5).template.head;
            if (head != -1) {
                return (short) head;
            }
        }
        return this.head;
    }

    public short getBody() {
        if (effectSkill != null && effectSkill.isMonkey) {
            return 193;
        } else if (effectSkill != null && effectSkill.isSocola) {
            return 413;
        } else if (fusion != null && fusion.typeFusion != ConstPlayer.NON_FUSION) {
            if (fusion.typeFusion == ConstPlayer.LUONG_LONG_NHAT_THE) {
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 0][1];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
                if (this.pet.isMabu) {
                    return idOutfitFusion[3 + this.gender][1];
                }
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 1][1];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
//                if (this.pet.typePet == 1) {
//                    return idOutfitFusion[3 + this.gender][1];
//                }
                return idOutfitFusion[3 + this.gender][1];
            }
        } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem()) {
            int body = inventory.itemsBody.get(5).template.body;
            if (body != -1) {
                return (short) body;
            }
        }
        if (inventory != null && inventory.itemsBody.get(0).isNotNullItem()) {
            return inventory.itemsBody.get(0).template.part;
        }
        return (short) (gender == ConstPlayer.NAMEC ? 59 : 57);
    }

    public short getLeg() {
        if (effectSkill != null && effectSkill.isMonkey) {
            return 194;
        } else if (effectSkill != null && effectSkill.isSocola) {
            return 414;
        } else if (fusion != null && fusion.typeFusion != ConstPlayer.NON_FUSION) {
            if (fusion.typeFusion == ConstPlayer.LUONG_LONG_NHAT_THE) {
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 0][2];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
                if (this.pet.isMabu) {
                    return idOutfitFusion[3 + this.gender][2];
                }
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 1][2];
                } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
//                if (this.pet.typePet == 1) {
//                    return idOutfitFusion[3 + this.gender][2];
//                }
                return idOutfitFusion[3 + this.gender][2];
            }
        } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem()) {
            int leg = inventory.itemsBody.get(5).template.leg;
            if (leg != -1) {
                return (short) leg;
            }
        }
        if (inventory != null && inventory.itemsBody.get(1).isNotNullItem()) {
            return inventory.itemsBody.get(1).template.part;
        }
        return (short) (gender == 1 ? 60 : 58);
    }

    public short getFlagBag() {
        if (this.isHoldBlackBall) {
            return 31;
        } else if (this.isHoldNamecBall) {
            return 30;
        }
        if (this.inventory.itemsBody.size() == 11) {
            if (this.inventory.itemsBody.get(8).isNotNullItem()) {
                if (this.inventory.itemsBody.get(8).template.id == (1101)){
                    return 205;
               } else {
                    return this.inventory.itemsBody.get(8).template.part;
                }
            }
        }
        if (TaskService.gI().getIdTask(this) == ConstTask.TASK_3_2) {
            return 28;
        }
        if (this.clan != null) {
            return (short) this.clan.imgId;
        }
        return -1;
    }

    public short getMount() {
        for (Item item : inventory.itemsBag) {
            if (item.isNotNullItem()) {
                if (item.template.type == 24) {
                    if (item.template.gender == 3 || item.template.gender == this.gender) {
                        return item.template.id;
                    } else {
                        return -1;
                    }
                }
                if (item.template.type == 23) {
                    if (item.template.id < 500) {
                        return item.template.id;
                    } else {
                        return (short) DataGame.MAP_MOUNT_NUM.get(String.valueOf(item.template.id));
                    }
                }
            }
        }
        return -1;
    }

    //--------------------------------------------------------------------------
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 100)) {
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1;
            }
            if (isMobAttack && this.charms.tdBatTu > System.currentTimeMillis() && damage >= this.nPoint.hp) {
                damage = this.nPoint.hp - 1;
            }
            this.nPoint.subHP(damage);
            if (isDie()) {
                setDie(plAtt);
            }
            return damage;
        } else {
            return 0;
        }
    }

    private void setDie(Player plAtt) {
        //xóa phù
        if (this.effectSkin.xHPKI > 1) {
            this.effectSkin.xHPKI = 1;
            Service.getInstance().point(this);
        }
        //xóa tụ skill đặc biệt
        this.playerSkill.prepareQCKK = false;
        this.playerSkill.prepareLaze = false;
        this.playerSkill.prepareTuSat = false;
        //xóa hiệu ứng skill
        this.effectSkill.removeSkillEffectWhenDie();
        //
        nPoint.setHp(0);
        nPoint.setMp(0);
        //xóa trứng
        if (this.mobMe != null) {
            this.mobMe.mobMeDie();
        }
        Service.getInstance().charDie(this);
        //add kẻ thù
        if (!this.isPet && !this.isBoss && plAtt != null && !plAtt.isPet  && !plAtt.isBoss) {
            if (!plAtt.itemTime.isUseAnDanh) {
                FriendAndEnemyService.gI().addEnemy(this, plAtt);
            }
        }
        //kết thúc pk
        PVPServcice.gI().finishPVP(this, PVP.TYPE_DIE);
        BlackBallWar.gI().dropBlackBall(this);
    }
    public boolean isPl(){
        return !isBoss && !isPet && !isNewMiniPet;
    }

    //--------------------------------------------------------------------------
    public void setClanMember() {
        if (this.clanMember != null) {
            this.clanMember.powerPoint = this.nPoint.power;
            this.clanMember.head = this.getHead();
            this.clanMember.body = this.getBody();
            this.clanMember.leg = this.getLeg();
        }
    }

    public boolean isAdmin() {
        return this.session.isAdmin;
    }

    public void setJustRevivaled() {
        this.justRevived = true;
        this.lastTimeRevived = System.currentTimeMillis();
        this.immortal = true;
    }

    public void dispose() {
        if (pet != null) {
            pet.dispose();
            pet = null;
        }
        if (minipet != null) {
            minipet.dispose();
            minipet = null;
        }
        playerTrade = null;
        if (mapBlackBall != null) {
            mapBlackBall.clear();
            mapBlackBall = null;
        }
        zone = null;
        mapBeforeCapsule = null;
        if (mapMaBu != null) {
            mapMaBu.clear();
            mapMaBu = null;
        }
        zone = null;
        mapBeforeCapsule = null;
        if (mapCapsule != null) {
            mapCapsule.clear();
            mapCapsule = null;
        }
        if (mobMe != null) {
            mobMe.dispose();
            mobMe = null;
        }
        location = null;
        if (setClothes != null) {
            setClothes.dispose();
            setClothes = null;
        }
        if (effectSkill != null) {
            effectSkill.dispose();
            effectSkill = null;
        }
        if (mabuEgg != null) {
            mabuEgg.dispose();
            mabuEgg = null;
        }
        
        if (skillSpecial != null) {
            skillSpecial.dispose();
            skillSpecial = null;
        }
        if (playerTask != null) {
            playerTask.dispose();
            playerTask = null;
        }
        if (itemTime != null) {
            itemTime.dispose();
            itemTime = null;
        }
        if (fusion != null) {
            fusion.dispose();
            fusion = null;
        }
        if (magicTree != null) {
            magicTree.dispose();
            magicTree = null;
        }
        if (playerIntrinsic != null) {
            playerIntrinsic.dispose();
            playerIntrinsic = null;
        }
        if (inventory != null) {
            inventory.dispose();
            inventory = null;
        }
        if (playerSkill != null) {
            playerSkill.dispose();
            playerSkill = null;
        }
        if (combineNew != null) {
            combineNew.dispose();
            combineNew = null;
        }
        iDMark = null;
        if (charms != null) {
            charms.dispose();
            charms = null;
        }
        if (effectSkin != null) {
            effectSkin.dispose();
            effectSkin = null;
        }
        if (gift != null) {
            gift.dispose();
            gift = null;
        }
        if (nPoint != null) {
            nPoint.dispose();
            nPoint = null;
        }
        if (rewardBlackBall != null) {
            rewardBlackBall.dispose();

            rewardBlackBall = null;
        }
        if (effectFlagBag != null) {
            effectFlagBag.dispose();
            effectFlagBag = null;
        }
        effectFlagBag = null;
        clan = null;
        clanMember = null;
        friends = null;
        enemies = null;
        session = null;
        name = null;
    }

}
