package com.nrolove.data;

import com.nrolove.models.Template.HeadAvatar;
import com.nrolove.models.Template.MapTemplate;
import java.io.DataInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import com.nrolove.utils.FileIO;
import com.nrolove.services.Service;
import com.nrolove.utils.Util;
import com.nrolove.models.skill.NClass;
import com.nrolove.models.skill.Skill;
import com.nrolove.models.Template.MobTemplate;
import com.nrolove.models.Template.NpcTemplate;
import com.nrolove.models.Template.SkillTemplate;
import com.nrolove.server.Manager;
import com.nrolove.server.io.Message;
import com.nrolove.server.io.Session;
import com.nrolove.utils.Logger;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import javax.imageio.ImageIO;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 *
 */
public class DataGame {

//    public static byte vsData = 47;
//    public static byte vsMap = 25;
//    public static byte vsSkill = 5;
//    public static byte vsItem = 90;
    public static byte vsData = 5;
    public static byte vsMap = 5;
    public static byte vsSkill = 5;
    public static byte vsItem = 5;

    public static String LINK_IP_PORT = "Girlkun75-1:localhost:14445:0";
    private static final String MOUNT_NUM = "733:1,734:2,735:3,743:4,744:5,746:6,795:7,849:8,897:9,920:10";
    public static final Map MAP_MOUNT_NUM = new HashMap();

    private static final byte[] dart = FileIO.readFile("data/nrolove/update_data/dart");
    private static final byte[] arrow = FileIO.readFile("data/nrolove/update_data/arrow");
    private static final byte[] effect = FileIO.readFile("data/nrolove/update_data/effect");
    private static final byte[] image = FileIO.readFile("data/nrolove/update_data/image");
    private static final byte[] part = FileIO.readFile("data/nrolove/update_data/part");
    private static final byte[] skill = FileIO.readFile("data/nrolove/update_data/skill");

    static {
        String[] array = MOUNT_NUM.split(",");
        for (String str : array) {
            String[] data = str.split(":");
            short num = (short) (Short.parseShort(data[1]) + 30000);
            MAP_MOUNT_NUM.put(data[0], num);
        }
    }

    private DataGame() {

    }

    public static void sendVersionGame(Session session) {
        Message msg;
        try {
            msg = Service.getInstance().messageNotMap((byte) 4);
            msg.writer().writeByte(vsData);
            msg.writer().writeByte(vsMap);
            msg.writer().writeByte(vsSkill);
            msg.writer().writeByte(vsItem);
            msg.writer().writeByte(0);

            long[] smtieuchuan = {1000L, 3000L, 15000L, 40000L, 90000L, 170000L, 340000L, 700000L,
                1500000L, 15000000L, 150000000L, 1500000000L, 5000000000L, 10000000000L, 40000000000L,
                50010000000L, 60010000000L, 70010000000L, 80010000000L, 100010000000L};
            msg.writer().writeByte(smtieuchuan.length);
            for (int i = 0; i < smtieuchuan.length; i++) {
                msg.writer().writeLong(smtieuchuan[i]);
            }
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    //vcData
    public static void updateData(Session session) {
        Message msg;
        try {
            msg = new Message(-87);
            msg.writer().writeByte(vsData);
            msg.writer().writeInt(dart.length);
            msg.writer().write(dart);
            msg.writer().writeInt(arrow.length);
            msg.writer().write(arrow);
            msg.writer().writeInt(effect.length);
            msg.writer().write(effect);
            msg.writer().writeInt(image.length);
            msg.writer().write(image);
            msg.writer().writeInt(part.length);
            msg.writer().write(part);
            msg.writer().writeInt(skill.length);
            msg.writer().write(skill);

            session.doSendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    //vcMap
    public static void createMap(Session session) {
        Message msg;
        try {
            msg = Service.getInstance().messageNotMap((byte) 6);
            msg.writer().writeByte(vsMap);
            msg.writer().writeByte(Manager.MAP_TEMPLATES.length);
            for (MapTemplate temp : Manager.MAP_TEMPLATES) {
                msg.writer().writeUTF(temp.name);
            }
            msg.writer().writeByte(Manager.NPC_TEMPLATES.size());
            for (NpcTemplate temp : Manager.NPC_TEMPLATES) {
                msg.writer().writeUTF(temp.name);
                msg.writer().writeShort(temp.head);
                msg.writer().writeShort(temp.body);
                msg.writer().writeShort(temp.leg);
                msg.writer().writeByte(0);
            }
            msg.writer().writeByte(Manager.MOB_TEMPLATES.size());
            for (MobTemplate temp : Manager.MOB_TEMPLATES) {
                msg.writer().writeByte(temp.type);
                msg.writer().writeUTF(temp.name);
                msg.writer().writeInt(temp.hp);
                msg.writer().writeByte(temp.rangeMove);
                msg.writer().writeByte(temp.speed);
                msg.writer().writeByte(temp.dartType);
            }
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(DataGame.class, e);
        }
    }

    //vcSkill
    public static void updateSkill(Session session) {
        Message msg;
        try {
            msg = new Message(-28);
//            msg.writer().write(FileIO.readFile("data/1632811838545_-28_7_r"));

            msg.writer().writeByte(7);
            msg.writer().writeByte(vsSkill);
            msg.writer().writeByte(0); //count skill option

            msg.writer().writeByte(Manager.NCLASS.size());
            for (NClass nClass : Manager.NCLASS) {
                msg.writer().writeUTF(nClass.name);

                msg.writer().writeByte(nClass.skillTemplatess.size());
                for (SkillTemplate skillTemp : nClass.skillTemplatess) {
                    msg.writer().writeByte(skillTemp.id);
                    msg.writer().writeUTF(skillTemp.name);
                    msg.writer().writeByte(skillTemp.maxPoint);
                    msg.writer().writeByte(skillTemp.manaUseType);
                    msg.writer().writeByte(skillTemp.type);
                    msg.writer().writeShort(skillTemp.iconId);
                    msg.writer().writeUTF(skillTemp.damInfo);
                    msg.writer().writeUTF("OverLord Skills");
                    if (skillTemp.id != 0) {
                        msg.writer().writeByte(skillTemp.skillss.size());
                        for (Skill skill : skillTemp.skillss) {
                            msg.writer().writeShort(skill.skillId);
                            msg.writer().writeByte(skill.point);
                            msg.writer().writeLong(skill.powRequire);
                            msg.writer().writeShort(skill.manaUse);
                            if(skill.skillId == 1) {
                                msg.writer().writeInt(5000);
                            } else {
                                msg.writer().writeInt(skill.coolDown);
                            }
                            msg.writer().writeShort(skill.dx);
                            msg.writer().writeShort(skill.dy);
                            msg.writer().writeByte(skill.maxFight);
                            msg.writer().writeShort(skill.damage);
                            msg.writer().writeShort(skill.price);
                            msg.writer().writeUTF(skill.moreInfo);
                        }
                    } else {
                        //Thêm 2 skill trống 105, 106
                        msg.writer().writeByte(skillTemp.skillss.size() + 2);
                        for (Skill skill : skillTemp.skillss) {
                            msg.writer().writeShort(skill.skillId);
                            msg.writer().writeByte(skill.point);
                            msg.writer().writeLong(skill.powRequire);
                            msg.writer().writeShort(skill.manaUse);
                            if(skill.skillId == 1) {
                                msg.writer().writeInt(5000);
                            } else {
                                msg.writer().writeInt(skill.coolDown);
                            }
                            msg.writer().writeShort(skill.dx);
                            msg.writer().writeShort(skill.dy);
                            msg.writer().writeByte(skill.maxFight);
                            msg.writer().writeShort(skill.damage);
                            msg.writer().writeShort(skill.price);
                            msg.writer().writeUTF(skill.moreInfo);
                        }
                        for (int i = 105; i <= 106; i++) {
                            msg.writer().writeShort(i);
                            msg.writer().writeByte(0);
                            msg.writer().writeLong(0);
                            msg.writer().writeShort(0);
                            msg.writer().writeInt(0);
                            msg.writer().writeShort(0);
                            msg.writer().writeShort(0);
                            msg.writer().writeByte(0);
                            msg.writer().writeShort(0);
                            msg.writer().writeShort(0);
                            msg.writer().writeUTF("");
                        }
                    }
                }
            }
            session.doSendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(DataGame.class, e);
        }
    }

    public static void sendDataImageVersion(Session session) {
        Message msg;
        try {
            msg = new Message(-111);
            msg.writer().write(FileIO.readFile("data/nrolove/data_img_version/x" + session.zoomLevel + "/img_version"));
            session.doSendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(DataGame.class, e);
        }
    }

    //--------------------------------------------------------------------------
    public static void sendEffectTemplate(Session session, int id, int... idtemp) {
        int idT = id;
        if(idtemp.length > 0 && idtemp[0] != 0){
            idT = idtemp[0];
        }
        Message msg;
        try {
            byte[] effData = FileIO.readFile("data/nrolove/effect_data/x" + session.zoomLevel + "/data/DataEffect_" + idT);
            byte[] effImg = FileIO.readFile("data/nrolove/effect_data/x" + session.zoomLevel + "/img/ImgEffect_" + idT+".png");
            msg = new Message(-66);
            msg.writer().writeShort(id);
            msg.writer().writeInt(effData.length);
            msg.writer().write(effData);
            msg.writer().writeByte(0);
            msg.writer().writeInt(effImg.length);
            msg.writer().write(effImg);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public static void sendItemBGTemplate(Session session, int id) {
        Message msg;
        try {
            byte[] bg_temp = FileIO.readFile("data/nrolove/item_bg_temp/x" + session.zoomLevel + "/" + id + ".png");
            msg = new Message(-32);
            msg.writer().writeShort(id);
            msg.writer().writeInt(bg_temp.length);
            msg.writer().write(bg_temp);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(DataGame.class, e);
        }
    }

    public static void sendDataItemBG(Session session) {
        Message msg;
        try {
            byte[] item_bg = FileIO.readFile("data/nrolove/item_bg_temp/item_bg_data");
            msg = new Message(-31);
            msg.writer().write(item_bg);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public static void sendIcon(Session session, int id) {
        Message msg;
        try {
            byte[] icon = FileIO.readFile("data/nrolove/icon/x" + session.zoomLevel + "/" + id + ".png");
            msg = new Message(-67);
            msg.writer().writeInt(id);
            msg.writer().writeInt(icon.length);
            msg.writer().write(icon);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public static void sendMaxSmall(Session session) {
        Message msg;
        try {
            msg = new Message(-77);
            byte[] data = FileIO.readFile("data/nrolove/data_img_version/x" + session.zoomLevel + "/img_version");
            if (data != null) {
                msg.writer().write(data);
            } else {
                msg.writer().writeShort(30000);
                for (int i = 0; i < 30000; i++) {
                    msg.writer().writeByte(0);
                }
            }
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public static void requestMobTemplate(Session session, int id) {
        Message msg;
        try {
            byte[] mob = FileIO.readFile("data/nrolove/mob/x" + session.zoomLevel + "/" + id);
            msg = new Message(11);
            msg.writer().writeByte(id);
            msg.writer().write(mob);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public static void mainx(String[] args) {
        try {
            int mobId = 1;
            BufferedImage img = ImageIO.read(new File("C:\\Users\\adm\\Desktop\\girlkun75\\mobimg\\" + mobId + ".png"));
            Graphics2D g = (Graphics2D) img.getGraphics();
            g.setColor(Color.RED);

            DataInputStream diss = new DataInputStream(new FileInputStream("data/nrolove/mob/x1/" + mobId));
            byte type = diss.readByte();
            byte[] data = new byte[diss.readInt()];
            diss.read(data);

            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
            int x = dis.readByte();

            for (int i = 0; i < x; i++) {
                int id = dis.readByte();
                int x0 = dis.readByte();
                int y0 = dis.readByte();
                int w = dis.readByte();
                int h = dis.readByte();
                try {
                    g.drawRect(x0, y0, w, h);

                } catch (Exception e) {
                }
//                System.out.println("id: " + id + " - " + x0 + " - " + y0 + " - " + w + " - " + h);
            }
            ImageIO.write(img, "png", new File("C:\\Users\\adm\\Desktop\\imgmob\\" + mobId + ".png"));

            x = dis.readShort();
            for (int i = 0; i < x; i++) {
                int n = dis.readByte();
                for (int j = 0; j < n; j++) {

                }
            }

//                } catch (Exception e) {
//                }
//            }
        } catch (Exception e) {
        }
    }

    public static void sendTileSetInfo(Session session) {
        Message msg;
        try {
            msg = new Message(-82);
            msg.writer().write(FileIO.readFile("data/nrolove/map/tile_set_info"));
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(DataGame.class, e);
        }
    }
    //data vẽ map
    public static void sendMapTemp(Session session, int id) {
        Message msg;
        try {
            msg = new Message(-28);
            msg.writer().write(FileIO.readFile("data/nrolove/map/tile_map_data/" + id));
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(DataGame.class, e);
        }
    }

    //head-avatar
    public static void sendHeadAvatar(Message msg) {
        try {
            msg.writer().writeShort(Manager.HEAD_AVATARS.size());
            for (HeadAvatar ha : Manager.HEAD_AVATARS) {
                msg.writer().writeShort(ha.headId);
                msg.writer().writeShort(ha.avatarId);
            }
        } catch (Exception e) {
        }
    }

    public static void sendImageByName(Session session, String imgName) {
        Message msg;
        try {
            msg = new Message(66);
            msg.writer().writeUTF(imgName);
            msg.writer().writeByte(Manager.getNFrameImageByName(imgName));
            byte[] data = FileIO.readFile("data/nrolove/img_by_name/x" + session.zoomLevel + "/" + imgName + ".png");
            msg.writer().writeInt(data.length);
            msg.writer().write(data);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    //download data res --------------------------------------------------------
    public static void sendVersionRes(Session session) {
        Message msg;
        try {
            msg = new Message(-74);
            msg.writer().writeByte(0);
            msg.writer().writeInt(5714013);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public static void sendSizeRes(Session session) {
        Message msg;
        try {
            msg = new Message(-74);
            msg.writer().writeByte(1);
            msg.writer().writeShort(new File("data/nrolove/res/x" + session.zoomLevel).listFiles().length);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public static void sendRes(Session session) {
        Message msg;
        try {
            for (final File fileEntry : new File("data/nrolove/res/x" + session.zoomLevel).listFiles()) {
                String original = fileEntry.getName();
                byte[] res = FileIO.readFile(fileEntry.getAbsolutePath());
                msg = new Message(-74);
                msg.writer().writeByte(2);
                msg.writer().writeUTF(original);
                msg.writer().writeInt(res.length);
                msg.writer().write(res);

                session.sendMessage(msg);
                msg.cleanup();
                Thread.sleep(1);
            }

            msg = new Message(-74);
            msg.writer().writeByte(3);
            msg.writer().writeInt(5714013);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(DataGame.class, e);
        }
    }

    public static void sendLinkIP(Session session) {
        Message msg;
        try {
            msg = new Message(-29);
            msg.writer().writeByte(2);
            msg.writer().writeUTF(LINK_IP_PORT + ",0,0");
            msg.writer().writeByte(1);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }
}
