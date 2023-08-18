package com.nrolove.models.boss.iboss;

import com.nrolove.models.player.Player;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 *
 */
public interface IBossStatus extends IBossInit{

    void attack(); //attack

    void idle(); //trong lúc attack có thể đứng nghỉ

    void checkPlayerDie(Player pl); //attack player nào đó rồi kiểm tra
    
    void die();
    
    void respawn();
}
