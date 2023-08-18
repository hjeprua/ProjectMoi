package com.nrolove.models.task;

import com.nrolove.models.player.Player;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 *
 */
public class TaskPlayer {

    public TaskMain taskMain;
    
    public SideTask sideTask;

    private Player player;

    public TaskPlayer(Player player) {
        this.player = player;
        this.sideTask = new SideTask();
    }
    
    public void dispose(){
        this.taskMain = null;
        this.sideTask = null;
        this.player = null;
    }

}
