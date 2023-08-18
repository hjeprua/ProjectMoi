package com.nrolove.models.boss.iboss;

import com.nrolove.models.player.Player;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 *
 */
public interface BossInterface extends IBossStatus{
    
    void update();

    void rewards(Player pl); //phần thưởng sau khi bị chết
    
    Player getPlayerAttack() throws Exception; //lấy ra 1 player để đánh
    
    void joinMap();
    
    void leaveMap();
    
    boolean talk();
}
