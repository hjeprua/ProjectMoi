package com.nrolove.models.player;

import com.nrolove.services.MapService;
import com.nrolove.services.PlayerService;
import com.nrolove.utils.Util;

public class MiniPet extends Player{
    public Player master;
    public short body;
    public short leg;
    public static int idb = -400000;
    public MiniPet(Player master , short h,short b,short l) {
        this.master = master;
        this.isNewMiniPet = true;
        this.isBoss = true;
        this.id = idb;
        idb--;
        this.head = h;
        this.body = b;
        this.leg = l;
    }

    @Override
    public short getHead() {
        return head;
    }

    @Override
    public short getBody() {
        return body;
    }

    @Override
    public void dispose() {
        this.master = null;
        super.dispose();
    }

    @Override
    public short getLeg() {
        return leg;
    }

    @Override
    public void update() {
        super.update();
        if (master != null && (this.zone == null || this.zone != master.zone)) {
            joinMapMaster();
        }
        if (master != null && master.isDie()) {
            return;
        }
        moveIdle();
    }

    public void joinMapMaster() {
        if (!isDie()) {
            this.location.x = master.location.x + Util.nextInt(-10, 10);
            this.location.y = master.location.y;
            MapService.gI().goToMap(this, master.zone);
            this.zone.load_Me_To_Another(this);
        }
    }

    public long lastTimeMoveIdle;

    private int timeMoveIdle;
    public boolean idle = true;

    private void moveIdle() {
        if (idle && Util.canDoWithTime(lastTimeMoveIdle, timeMoveIdle)) {
            int dir = this.location.x - master.location.x <= 0 ? -1 : 1;
            PlayerService.gI().playerMove(this, master.location.x
                    + Util.nextInt(dir == -1 ? 30 : -50, dir == -1 ? 50 : 30), master.location.y);
            lastTimeMoveIdle = System.currentTimeMillis();
            timeMoveIdle = Util.nextInt(5000, 8000);
        }
    }

    public void followMaster() {
        if (this.isDie()) {
            return;
        }
        followMaster(50);
    }

    private void followMaster(int dis) {
        int mX = master.location.x;
        int mY = master.location.y;
        int disX = this.location.x - mX;
        if (this.zone == null) {
            this.zone = master.zone;
            joinMapMaster();
        }
        if (Math.sqrt(Math.pow(mX - this.location.x, 2) + Math.pow(mY - this.location.y, 2)) >= dis) {
            if (disX < 0) {
                this.location.x = mX - Util.nextInt(0, dis);
            } else {
                this.location.x = mX + Util.nextInt(0, dis);
            }
            this.location.y = mY;
            PlayerService.gI().playerMove(this, this.location.x, this.location.y);
        }
    }
}
