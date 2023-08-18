package com.nrolove.models.player;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 *
 */
public class EffectFlagBag {

    private Player player;

    public EffectFlagBag(Player player) {
        this.player = player;
    }

    public boolean useVoOc;
    public boolean useCayKem;
    public boolean useCaHeo;
    public boolean useConDieu;
    public boolean useDieuRong;
    public boolean useMeoMun;
    public boolean useXienCa;
    public boolean usePhongHeo;
    public boolean useKiemz;

    public void reset() {
        this.useVoOc = false;
        this.useVoOc = false;
        this.useCayKem = false;
        this.useCaHeo = false;
        this.useConDieu = false;
        this.useDieuRong = false;
        this.useMeoMun = false;
        this.useXienCa = false;
        this.usePhongHeo = false;
        this.useKiemz = false;
    }
    
    public void dispose(){
        this.player = null;
    }
}
