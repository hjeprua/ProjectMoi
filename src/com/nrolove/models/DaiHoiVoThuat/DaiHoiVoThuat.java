package com.nrolove.models.DaiHoiVoThuat;

import java.util.Timer;
import java.util.TimerTask;

import com.nrolove.models.boss.Boss;
import com.nrolove.models.boss.BossManager;
import com.nrolove.models.boss.DaiHoiVoThuat.Chanxu;
import com.nrolove.models.boss.DaiHoiVoThuat.Chapa;
import com.nrolove.models.boss.DaiHoiVoThuat.HecQuyn;
import com.nrolove.models.boss.DaiHoiVoThuat.JackyChun;
import com.nrolove.models.boss.DaiHoiVoThuat.LiuLiu;
import com.nrolove.models.boss.DaiHoiVoThuat.Odo;
import com.nrolove.models.boss.DaiHoiVoThuat.Ponput;
import com.nrolove.models.boss.DaiHoiVoThuat.TauPayPay;
import com.nrolove.models.boss.DaiHoiVoThuat.TenXinHang;
import com.nrolove.models.boss.DaiHoiVoThuat.Xinpato;
import com.nrolove.models.boss.DaiHoiVoThuat.Yamcha;
import com.nrolove.models.map.Map;
import com.nrolove.models.map.Zone;
import com.nrolove.models.player.Player;
import com.nrolove.models.skill.Skill;
import com.nrolove.services.EffectSkillService;
import com.nrolove.services.PlayerService;
import com.nrolove.services.Service;
import com.nrolove.services.func.ChangeMapService;
import com.nrolove.utils.Util;

public class DaiHoiVoThuat {
    private static DaiHoiVoThuat instance;
    public int next = 0;

    public int step = 0;
    public int die = 1;
    public boolean isDrop = false;
    public boolean isWin = false;

    public boolean isStart = false;

    public long curr;
    public long currATTACK;
    public long currTHDS;

    public Timer timer;
    public TimerTask task;
    protected boolean actived = false;

    public Player player;
    public Boss boss;

    public static DaiHoiVoThuat gI() {
        if (instance == null) {
            instance = new DaiHoiVoThuat(null);
        }
        return instance;
    }

    public DaiHoiVoThuat(Player player) {
        this.player = player;
    }

    // Set player to arena
    public void bossToArena() {
        ChangeMapService.gI().changeMap(boss, 129, player.zone.zoneId, 470, 264);
    }

    public void active(int delay) {
        if (!actived) {
            actived = true;
            this.timer = new Timer();
            task = new TimerTask() {
                @Override
                public void run() {
                    DaiHoiVoThuat.this.update();
                }
            };
            this.timer.schedule(task, delay, delay);
        }
    }

    public void Start() {
        Zone zone = null;
        if (this.player.zone.isZoneDaiHoiVoThuat) {
            Map map = this.player.zone.map;
            for (Zone z : map.zones) {
                if (!z.isZoneDaiHoiVoThuat) {
                    z.isZoneDaiHoiVoThuat = true;
                    zone = z;
                    break;
                }
            }
            ChangeMapService.gI().changeMap(player, 129, zone.zoneId, 360, 360);
        } else {
            this.player.zone.isZoneDaiHoiVoThuat = true;
            zone = this.player.zone;
        }
        if (zone != null) {
            Service.getInstance().sendThongBao(player, "Bạn chuẩn bị đầu tiên");
            curr = System.currentTimeMillis();
            next = 0;
            active(5);
            isStart = true;
        }
    }

    public void update() {
        try {
            if (next == 0) {
                if (System.currentTimeMillis() - curr >= 3000 || !isStart) {
                    curr = System.currentTimeMillis();
                    player.location.x = 300;
                    player.location.y = 264;
                    Service.getInstance().setPos(player, 300, 264);
                    createBoss();
                    next++;
                    return;
                }
            } else if (next == 1) {
                if (System.currentTimeMillis() - curr >= 1000 || isStart) {
                    isStart = false;
                    curr = System.currentTimeMillis();
                    Service.getInstance().setPos(player, 300, 264);
                    EffectSkillService.gI().startStun(player, System.currentTimeMillis(), 10000);
                    EffectSkillService.gI().startStun(boss, System.currentTimeMillis(), 10000);
                    player.nPoint.setFullHpMp();
                    PlayerService.gI().sendInfoHpMp(player);
                    Service.getInstance().Send_Info_NV(player);
                    Service.getInstance().Send_Info_NV(boss);
                    next++;
                    return;
                }
            } else if (next == 2) {
                if (System.currentTimeMillis() - curr >= 11000) {
                    curr = System.currentTimeMillis();
                    Service.getInstance().sendPlayerDaiHoiVoThuat(player, boss, (byte) 3);
                    boss.typePk = 3;
                    Service.getInstance().Send_Info_NV(boss);
                    for (int i = 0; i < player.playerSkill.skills.size(); i++) {
                        Skill skill = player.playerSkill.skills.get(i);
                        if (skill != null) {
                            skill.lastTimeUseThisSkill = 0;
                            Service.getInstance().sendResetSkill(player, skill.skillId, 0);
                        }
                    }
                    next++;
                    return;
                }
            } else if (next == 3) {
                if (Util.canDoWithTime(curr, 180000)) {
                    next++;
                    return;
                }
                if (boss != null) {
                    if (boss.isDie() && step < 10) {
                        Service.getInstance().sendPlayerDaiHoiVoThuat(player, boss, (byte) 0);
                        step++;
                        next = 0;
                        return;
                    } else if (boss.isDie() && step >= 10) {
                        Service.getInstance().sendPlayerDaiHoiVoThuat(player, boss, (byte) 0);
                        isWin = true;
                        next++;
                        step++;
                        return;
                    }
                    BossAttack();
                }
                if (player != null && player.isDie() || player.location.y == 288) {
                    next++;
                }
            } else if (next == 4) {
                if (isWin) {
                    ChangeMapService.gI().changeMap(player, 129, -1, 360, 360);
                    Service.getInstance().sendThongBao(player, "Chúc mừng bạn đã vô dịch");
                } else {
                    Service.getInstance().setPos(player, 360, 360);
                    Service.getInstance().hsChar(player, 1, 1);
                    Service.getInstance().sendPlayerDaiHoiVoThuat(player, boss, (byte) 0);
                    Service.getInstance().sendThongBao(player, "Rất tiếc bạn đã thua cuộc");
                }
                close();
            }
        } catch (Exception e) {
            Util.debug(e.toString());
            e.printStackTrace();
        }
    }

    private void createBoss() {
        if (step >= 11) {
            return;
        }
        if (step == 0) {
            boss = new HecQuyn();
        }
        if (step == 1) {
            boss = new Odo();
        }
        if (step == 2) {
            boss = new Xinpato();
        }
        if (step == 3) {
            boss = new Chapa();
        }
        if (step == 4) {
            boss = new Ponput();
        }
        if (step == 5) {
            boss = new Chanxu();
        }
        if (step == 6) {
            boss = new TauPayPay();
        }
        if (step == 7) {
            boss = new Yamcha();
        }
        if (step == 8) {
            boss = new JackyChun();
        }
        if (step == 9) {
            boss = new TenXinHang();
        }
        if (step == 10) {
            boss = new LiuLiu();
        }
        boss.zone = player.zone;
        bossToArena();
    }

    public void BossAttack() {
        if (boss.effectSkill.isStun) {
            return;
        }
        if (step != 5 && Util.getDistance(boss, player) >= 60) {
            boss.gotoMove(player.location.x + 40, player.location.y);
        }
        if (step == 5 && Util.getDistance(boss, player) >= 60) {
            boss.playerSkill.skillSelect = boss.playerSkill.skills.get(1);
        } else if (step == 9 && Util.canDoWithTime(currTHDS, 60000)) {
            currTHDS = System.currentTimeMillis();
            boss.playerSkill.skillSelect = boss.playerSkill.skills.get(2);
        } else {
            boss.playerSkill.skillSelect = boss.playerSkill.skills.get(0);
        }
        if (Util.canDoWithTime(currATTACK, 500)) {
            currATTACK = System.currentTimeMillis();
            boss.attack(player);
        }
    }

    public void close() {
        try {
            if (player.zone != null) {
                player.zone.isZoneDaiHoiVoThuat = false;
            }
            if (next != 0 && !isWin) {
                die++;
                Service.getInstance().sendPlayerDaiHoiVoThuat(player, boss, (byte) 0);
            }
            next = 0;
            actived = false;
            if (task != null) {
                task.cancel();
                task = null;
            }
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            if (boss != null) {
                boss.leaveMap();
                BossManager.gI().removeBoss(boss);
                boss = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (player.zone != null) {
                player.zone.isZoneDaiHoiVoThuat = false;
            }
            task = null;
            timer = null;
            if (boss != null) {
                boss.zone.removePlayer(boss);
                boss.leaveMap();
                BossManager.gI().removeBoss(boss);
                boss = null;
            }
        }
    }

    public String toJson() {
        return "{\"step\":" + step + ",\"die\":" + die + ",\"isdrop\":" + isDrop + ",\"time\":"
                + (System.currentTimeMillis() + 86400000) + "}";
    }
}
