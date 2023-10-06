package com.nrolove.server.io;

import java.net.Socket;
import com.nrolove.models.player.Player;
import com.nrolove.server.Controller;
import com.nrolove.data.DataGame;
import com.nrolove.jdbc.DBService;
import com.nrolove.jdbc.daos.AccountDAO;
import com.nrolove.jdbc.daos.GodGK;
import com.nrolove.models.item.Item;
import com.nrolove.server.Client;
import com.nrolove.server.Maintenance;
import com.nrolove.server.Manager;
import static com.nrolove.server.ServerManager.CLIENTS;
import com.nrolove.server.model.AntiLogin;
import com.nrolove.services.ItemService;
import com.nrolove.services.PlayerService;
import com.nrolove.services.Service;
import com.nrolove.utils.Logger;
import com.nrolove.utils.Util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Session {

    private static final Map<String, AntiLogin> ANTILOGIN = new HashMap<>();

    private static final int TIME_WAIT_READ_MESSAGE = 180000;

    public boolean logCheck;

    private static int baseId = 0;
    public int id;
    public Player player;

    public byte timeWait = 50;

    public boolean connected;

    static final byte[] KEYS = {0};
    byte curR, curW;

    private Socket socket;
    Thread sendThread;
    Thread receiveThread;
    private MessageCollector collector;
    private MessageSender sender;

    Controller controller;

    public String ipAddress;
    public boolean isAdmin;
    public int userId;
    public String uu;
    public String pp;

    public int typeClient;
    public byte zoomLevel;

    public long lastTimeLogout;
    public boolean joinedGame;

    public long lastTimeReadMessage;

    public boolean actived;

    public int goldBar;
    public List<Item> itemsReward;
    public String dataReward;

    public int version;

    public void update() {
        if (Util.canDoWithTime(lastTimeReadMessage, TIME_WAIT_READ_MESSAGE)) {
//            Client.gI().kickSession(this);
        }
    }

    public void initItemsReward() {
        try {
            this.itemsReward = new ArrayList<>();
            String[] itemsReward = dataReward.split(";");
            for (String itemInfo : itemsReward) {
                if (itemInfo == null || itemInfo.equals("")) {
                    continue;
                }
                String[] subItemInfo = itemInfo.replaceAll("[{}\\[\\]]", "").split("\\|");
                String[] baseInfo = subItemInfo[0].split(":");
                int itemId = Integer.parseInt(baseInfo[0]);
                int quantity = Integer.parseInt(baseInfo[1]);
                Item item = ItemService.gI().createNewItem((short) itemId, quantity);
                if (subItemInfo.length == 2) {
                    String[] options = subItemInfo[1].split(",");
                    for (String opt : options) {
                        if (opt == null || opt.equals("")) {
                            continue;
                        }
                        String[] optInfo = opt.split(":");
                        int tempIdOption = Integer.parseInt(optInfo[0]);
                        int param = Integer.parseInt(optInfo[1]);
                        item.itemOptions.add(new Item.ItemOption(tempIdOption, param));
                    }
                }
                this.itemsReward.add(item);
            }
        } catch (Exception e) {

        }
    }

    public int getNumOfMessages() {
        return this.sender.getNumMessage();
    }

    public Session(Socket socket, Controller controller, String ip) {
        try {
            socket.setTcpNoDelay(true);
            this.id = baseId++;
            this.socket = socket;
            this.controller = controller;
            this.sendThread = new Thread((this.sender = new MessageSender(this, socket)), "Send " + ip);
            this.receiveThread = new Thread((this.collector = new MessageCollector(this, socket)), "Receive " + ip);
//            this.doControllerThread = new Thread((this.doController = new MessageDoController(this)), "Do controller " + ip);
//            this.doControllerThread.start();
            this.receiveThread.start();
            Client.gI().put(this);
        } catch (Exception e) {
            Logger.logException(Session.class, e);
        }
    }

    public void sendMessage(Message msg) {
        if (this.sender != null) {
            sender.addMessage(msg);
        }
    }

    public void doSendMessage(Message msg) {
        if (this.sender != null) {
            sender.doSendMessage(msg);
        }
    }

    public void disconnect() {
        if (connected) {
            connected = false;
            curR = 0;
            curW = 0;
            this.player = null;
            try {
                if (this.sender != null) {
                    this.sender.close();
                }
                if (this.collector != null) {
                    this.collector.close();
                }
            } catch (Exception e) {
                Logger.logException(Session.class, e);
            } finally {
                try {
                    if (socket != null) {
                        socket.close();
                    }
                } catch (Exception e) {
                    Logger.logException(Session.class, e);
                } finally {
                    this.socket = null;
                    this.sender = null;
                    this.collector = null;
                    this.sendThread = null;
                    this.receiveThread = null;
                    this.uu = null;
                    this.pp = null;
                    this.itemsReward = null;
                }
            }
        }
    }

    public void setClientType(Message msg) {
        try {
            this.typeClient = (msg.reader().readByte());//client_type
            this.zoomLevel = msg.reader().readByte();//zoom_level
            msg.reader().readBoolean();//is_gprs
            msg.reader().readInt();//width
            msg.reader().readInt();//height
            msg.reader().readBoolean();//is_qwerty
            msg.reader().readBoolean();//is_touch
            String platform = msg.reader().readUTF();
            String[] arrPlatform = platform.split("\\|");
            this.version = Integer.parseInt(arrPlatform[1].replaceAll("\\.", ""));
        } catch (Exception e) {
        } finally {
            msg.cleanup();
        }
        DataGame.sendLinkIP(this);
    }

    public String getName() {
        if (this.player != null) {
            return this.player.name;
        } else {
            return String.valueOf(this.socket.getPort());
        }
    }

    public void sendSessionKey() {
        this.sender.sendSessionKey();
    }

    public boolean canConnectWithIp() {
        Object o = CLIENTS.get(ipAddress);
        if (o == null) {
            CLIENTS.put(ipAddress, 1);
            return true;
        } else {
            int n = Integer.parseInt(String.valueOf(o));
            if (n < Manager.MAX_PER_IP) {
                n++;
                CLIENTS.put(ipAddress, n);
                return true;
            } else {
                return false;
            }
        }
    }

    public void login(String username, String password) {
        AntiLogin al = ANTILOGIN.get(this.ipAddress);
        if (al == null) {
            al = new AntiLogin();
            ANTILOGIN.put(this.ipAddress, al);
        }
        if (!al.canLogin()) {
            Service.getInstance().sendThongBaoOK(this, al.getNotifyCannotLogin());
            return;
        }

        if (Maintenance.isRuning) {
            Service.getInstance().sendThongBaoOK(this, "Server đang trong thời gian bảo trì, vui lòng quay lại sau");
            return;
        }
        if (!this.isAdmin && Client.gI().getPlayers().size() >= Manager.MAX_PLAYER) {
            Service.getInstance().sendThongBaoOK(this, "Máy chủ hiện đang quá tải, "
                    + "cư dân vui lòng di chuyển sang máy chủ khác.");
            return;
        }
        if (this.player != null) {
            return;
        } else {
            Player player = null;
            try {
                long st = System.currentTimeMillis();
                this.uu = username;
                this.pp = password;
                player = GodGK.login(this, al);
                if (player != null) {
                    // -77 max small
                    DataGame.sendMaxSmall(this);
                    // -93 bgitem version
                    Service.getInstance().sendMessage(this, -93, "1630679752231_-93_r");

                    this.timeWait = 0;
                    this.joinedGame = true;
                    player.nPoint.calPoint();
                    player.nPoint.setHp(player.nPoint.hp);
                    player.nPoint.setMp(player.nPoint.mp);
                    player.zone.addPlayer(player);
                    player.loaded = true;
                    if (player.pet != null) {
                        player.pet.nPoint.calPoint();
                        player.pet.nPoint.setHp(player.pet.nPoint.hp);
                        player.pet.nPoint.setMp(player.pet.nPoint.mp);
                    }

                    player.setSession(this);
                    Client.gI().put(player);
                    this.player = player;
                    //-28 -4 version data game
                    DataGame.sendVersionGame(this);
                    //-31 data item background
                    DataGame.sendDataItemBG(this);
                    controller.sendInfo(this);
                    Logger.login("Login usename " + this.userId  );
                }
            } catch (Exception e) {
                if (player != null) {
                    player.dispose();
                }
            }
        }
    }
}
