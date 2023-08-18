package com.nrolove.models.boss;

import com.nrolove.models.boss.bill.Bill;
import com.nrolove.models.boss.bill.Whis;
import com.nrolove.models.boss.bosstuonglai.Blackgoku;
import com.nrolove.models.boss.bosstuonglai.Superblackgoku;
import com.nrolove.models.boss.broly.Broly;
import com.nrolove.models.boss.broly.SuperBroly;
import com.nrolove.models.boss.broly.SuperBrolyRed;
import com.nrolove.models.boss.cell.SieuBoHung;
import com.nrolove.models.boss.cell.XenBoHung;
import com.nrolove.models.boss.cell.XenBoHung1;
import com.nrolove.models.boss.cell.XenBoHung2;
import com.nrolove.models.boss.cell.XenBoHungHoanThien;
import com.nrolove.models.boss.cell.XenCon;
import com.nrolove.models.boss.chill.Chill;
import com.nrolove.models.boss.chill.Chill2;
import com.nrolove.models.boss.cold.Cooler;
import com.nrolove.models.boss.cold.Cooler2;
import com.nrolove.models.boss.fide.FideDaiCa1;
import com.nrolove.models.boss.fide.FideDaiCa2;
import com.nrolove.models.boss.fide.FideDaiCa3;
import com.nrolove.models.boss.nappa.Kuku;
import com.nrolove.models.boss.nappa.MapDauDinh;
import com.nrolove.models.boss.nappa.Rambo;
import com.nrolove.models.boss.robotsatthu.Android19;
import com.nrolove.models.boss.robotsatthu.Android20;
import com.nrolove.models.boss.robotsatthu.KingKong;
import com.nrolove.models.boss.robotsatthu.Pic;
import com.nrolove.models.boss.robotsatthu.Poc;
import com.nrolove.models.boss.tieudoisatthu.So1;
import com.nrolove.models.boss.tieudoisatthu.So2;
import com.nrolove.models.boss.tieudoisatthu.So3;
import com.nrolove.models.boss.tieudoisatthu.So4;
import com.nrolove.models.boss.tieudoisatthu.TieuDoiTruong;
import com.nrolove.models.boss.traidat.BULMA;
import com.nrolove.models.boss.traidat.CHICHITHO;
import com.nrolove.models.boss.traidat.POCTHO;
import com.nrolove.models.boss.Mabu12h.BuiBui;
import com.nrolove.models.boss.Mabu12h.BuiBui2;
import com.nrolove.models.boss.Mabu12h.Drabura;
import com.nrolove.models.boss.Mabu12h.Drabura2;
import com.nrolove.models.boss.Mabu12h.MaBu12h;
import com.nrolove.models.boss.Mabu12h.Yacon;

/**
 *
 * @author 💖 PuPuBug 💖
 *
 */
public class BossFactory {

    // id boss
    public static final byte BROLY = -1;
    public static final byte SUPER_BROLY = -2;
    public static final byte TRUNG_UY_TRANG = -3;
    public static final byte TRUNG_UY_XANH_LO = -4;
    public static final byte TRUNG_UD_THEP = -5;
    public static final byte NINJA_AO_TIM = -6;
    public static final byte NINJA_AO_TIM_FAKE_1 = -7;
    public static final byte NINJA_AO_TIM_FAKE_2 = -8;
    public static final byte NINJA_AO_TIM_FAKE_3 = -9;
    public static final byte NINJA_AO_TIM_FAKE_4 = -10;
    public static final byte NINJA_AO_TIM_FAKE_5 = -11;
    public static final byte NINJA_AO_TIM_FAKE_6 = -12;
    public static final byte ROBOT_VE_SI_1 = -13;
    public static final byte ROBOT_VE_SI_2 = -14;
    public static final byte ROBOT_VE_SI_3 = -15;
    public static final byte ROBOT_VE_SI_4 = -16;
    public static final byte XEN_BO_HUNG_1 = -17;
    public static final byte XEN_BO_HUNG_2 = -18;
    public static final byte XEN_BO_HUNG_HOAN_THIEN = -19;
    public static final byte XEN_BO_HUNG = -20;
    public static final byte XEN_CON = -21;
    public static final byte SIEU_BO_HUNG = -22;
    public static final byte KUKU = -23;
    public static final byte MAP_DAU_DINH = -24;
    public static final byte RAMBO = -25;
    public static final byte COOLER = -26;
    public static final byte COOLER2 = -27;
    public static final byte SO4 = -28;
    public static final byte SO3 = -29;
    public static final byte SO2 = -30;
    public static final byte SO1 = -31;
    public static final byte TIEU_DOI_TRUONG = -32;
    public static final byte FIDE_DAI_CA_1 = -33;
    public static final byte FIDE_DAI_CA_2 = -34;
    public static final byte FIDE_DAI_CA_3 = -35;
    public static final byte ANDROID_19 = -36;
    public static final byte ANDROID_20 = -37;
    public static final byte ANDROID_13 = -38;
    public static final byte ANDROID_14 = -39;
    public static final byte ANDROID_15 = -40;
    public static final byte PIC = -41;
    public static final byte POC = -42;
    public static final byte KINGKONG = -43;
    public static final byte SUPER_BROLY_RED = -44;
    public static final byte LUFFY = -45;
    public static final byte ZORO = -46;
    public static final byte SANJI = -47;
    public static final byte USOPP = -48;
    public static final byte FRANKY = -49;
    public static final byte BROOK = -50;
    public static final byte NAMI = -51;
    public static final byte CHOPPER = -52;
    public static final byte ROBIN = -53;
    public static final byte WHIS = -54;
    public static final byte BILL = -55;
    public static final byte CHILL = -56;
    public static final byte CHILL2 = -57;
    public static final byte BULMA = -58;
    public static final byte POCTHO = -59;
    public static final byte CHICHITHO = -60;
    public static final byte BLACKGOKU = -61;
    public static final byte SUPERBLACKGOKU = -62;
    public static final byte HecQuyn = -63;
    public static final byte ODO = -64;
    public static final byte XINBATO = -65;
    public static final byte CHAPA = -66;
    public static final byte PONPUT = -67;
    public static final byte CHANXU = -68;
    public static final byte TAU_PAY_PAY = -69;
    public static final byte YAMCHA = -70;
    public static final byte JACKY_CHUN = -71;
    public static final byte THIEN_PHAN = -72;
    public static final byte LIU_LIU = -73;
    //mabu 12h
    public static final byte DRABURA = -74;
    public static final byte BUI_BUI = -75;
    public static final byte YA_CON = -76;
    public static final byte MABU_12H = -77;
    public static final byte DRABURA_2 = -78;
    public static final byte BUI_BUI_2 = -79;

    // public static final byte BROLYDEN = -61;
    // public static final byte BROLYXANH = -62;
    // public static final byte BROLYVANG = -63;
    private BossFactory() {

    }

    public static void initBoss() {
        new Thread(() -> {
            try {
                createBoss(BULMA);
                createBoss(CHICHITHO);
                createBoss(POCTHO);
                createBoss(BLACKGOKU);
                createBoss(BILL);
                createBoss(CHILL);
                createBoss(XEN_BO_HUNG);
                createBoss(XEN_BO_HUNG_1);
                createBoss(TIEU_DOI_TRUONG);
                createBoss(FIDE_DAI_CA_1);
                createBoss(COOLER);
                createBoss(ANDROID_20);
                createBoss(KINGKONG);
                createBoss(KUKU);
                createBoss(RAMBO);
                createBoss(MAP_DAU_DINH);
                createBoss(BUI_BUI);
                createBoss(BUI_BUI_2);
                createBoss(DRABURA);
                createBoss(DRABURA_2);
                createBoss(MABU_12H);
                createBoss(YA_CON);
                for (int i = 0; i < 5; i++) {
                    createBoss(SUPER_BROLY);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static Boss createBoss(short bossId) {
        Boss boss = null;
        switch (bossId) {
            case BROLY:
                boss = new Broly();
                break;
            case SUPER_BROLY:
                boss = new SuperBroly();
                break;
            case XEN_BO_HUNG_1:
                boss = new XenBoHung1();
                break;
            case XEN_BO_HUNG_2:
                boss = new XenBoHung2();
                break;
            case XEN_BO_HUNG_HOAN_THIEN:
                boss = new XenBoHungHoanThien();
                break;
            case XEN_BO_HUNG:
                boss = new XenBoHung();
                break;
            case XEN_CON:
                boss = new XenCon();
                break;
            case SIEU_BO_HUNG:
                boss = new SieuBoHung();
                break;
            case KUKU:
                boss = new Kuku();
                break;
            case MAP_DAU_DINH:
                boss = new MapDauDinh();
                break;
            case RAMBO:
                boss = new Rambo();
                break;
            case COOLER:
                boss = new Cooler();
                break;
            case COOLER2:
                boss = new Cooler2();
                break;
            case SO4:
                boss = new So4();
                break;
            case SO3:
                boss = new So3();
                break;
            case SO2:
                boss = new So2();
                break;
            case SO1:
                boss = new So1();
                break;
            case TIEU_DOI_TRUONG:
                boss = new TieuDoiTruong();
                break;
            case FIDE_DAI_CA_1:
                boss = new FideDaiCa1();
                break;
            case FIDE_DAI_CA_2:
                boss = new FideDaiCa2();
                break;
            case FIDE_DAI_CA_3:
                boss = new FideDaiCa3();
                break;
            case ANDROID_19:
                boss = new Android19();
                break;
            case ANDROID_20:
                boss = new Android20();
                break;
            case SUPER_BROLY_RED:
                boss = new SuperBrolyRed();
                break;
            case POC:
                boss = new Poc();
                break;
            case PIC:
                boss = new Pic();
                break;
            case KINGKONG:
                boss = new KingKong();
                break;
            case WHIS:
                boss = new Whis();
                break;
            case BILL:
                boss = new Bill();
                break;
            case CHILL:
                boss = new Chill();
                break;
            case CHILL2:
                boss = new Chill2();
                break;
            case BULMA:
                boss = new BULMA();
                break;
            case POCTHO:
                boss = new POCTHO();
                break;
            case CHICHITHO:
                boss = new CHICHITHO();
                break;
            case BLACKGOKU:
                boss = new Blackgoku();
                break;
            case SUPERBLACKGOKU:
                boss = new Superblackgoku();
                break;
            case BUI_BUI:
                boss = new BuiBui();
                break;
            case BUI_BUI_2:
                boss = new BuiBui2();
                break;
            case DRABURA:
                boss = new Drabura();
                break;
            case DRABURA_2:
                boss = new Drabura2();
                break;
            case MABU_12H:
                boss = new MaBu12h();
                break;
            case YA_CON:
                boss = new Yacon();
                break;
        }
        return boss;
    }

}
