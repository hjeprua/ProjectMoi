package com.nrolove.server;

import java.net.ServerSocket;
import java.net.Socket;
import com.nrolove.jdbc.daos.AccountDAO;
import com.nrolove.jdbc.daos.HistoryTransactionDAO;
import com.nrolove.models.boss.BossFactory;
import com.nrolove.models.boss.BossManager;
import com.nrolove.models.map.phoban.BanDoKhoBau;
import com.nrolove.models.map.phoban.DoanhTrai;
import com.nrolove.server.io.Session;
import com.nrolove.services.ClanService;
import com.nrolove.services.PlayerService;
import com.nrolove.utils.Logger;
import com.nrolove.utils.TimeUtil;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ServerManager {

    public static String timeStart;

    public static final Map CLIENTS = new HashMap();

    public static String NAME = "NroLove";
    public static int PORT = 14445;

    private Controller controller;

    private static ServerManager instance;

    public static ServerSocket listenSocket;
    public static boolean isRunning;

    public void init() {
        Manager.gI();
        AccountDAO.updateLastTimeLoginAllAccount();
        HistoryTransactionDAO.deleteHistory();
        BossFactory.initBoss();
        this.controller = new Controller();
    }

    public static ServerManager gI() {
        if (instance == null) {
            instance = new ServerManager();
            instance.init();
        }
        return instance;
    }

    public static void main(String[] args) {
        timeStart = TimeUtil.getTimeNow("dd/MM/yyyy HH:mm:ss");
        ServerManager.gI().run();
    }

    public void run() {
        isRunning = true;
        activeCommandLine();
        activeGame();
        activeServerSocket();
    }

    private void activeServerSocket() {
        try {
            Logger.log(Logger.PURPLE, "Start server......... Current thread: " + Thread.activeCount() + "\n");
            listenSocket = new ServerSocket(PORT);
            while (isRunning) {
                try {
                    Socket sc = listenSocket.accept();
                    String ip = (((InetSocketAddress) sc.getRemoteSocketAddress()).getAddress()).toString().replace("/", "");
                    if (canConnectWithIp(ip)) {
                        Session session = new Session(sc, controller, ip);
                        session.ipAddress = ip;
                    } else {
                        sc.close();
                    }
                } catch (Exception e) {
                        Logger.logException(ServerManager.class, e);
                }
            }
            listenSocket.close();
        } catch (Exception e) {
            Logger.logException(ServerManager.class, e, "Lỗi mở port");
            System.exit(0);
        }
    }

    private boolean canConnectWithIp(String ipAddress) {
        Object o = CLIENTS.get(ipAddress);
        if (o == null) {
            CLIENTS.put(ipAddress, 1);
            return true;
        } else {
            int n = Integer.parseInt(String.valueOf(o));
            n++;
            CLIENTS.put(ipAddress, n);
            return true;
        }
    }

    public void disconnect(Session session) {
        Object o = CLIENTS.get(session.ipAddress);
        if (o != null) {
            int n = Integer.parseInt(String.valueOf(o));
            n--;
            if (n < 0) {
                n = 0;
            }
            CLIENTS.put(session.ipAddress, n);
        }
    }

    private void activeCommandLine() {
        new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            while (true) {
                String line = sc.nextLine();
                if (line.equals("baotri")) {
                    Maintenance.gI().start(30);
                } else if (line.equals("athread")) {
                    ServerNotify.gI().notify("NroLove debug server: " + Thread.activeCount());
                } else if (line.equals("nplayer")) {
                    Logger.error("Player in game: " + Client.gI().getPlayers().size() + "\n");
                } else if (line.equals("nrolove")) {
                    new Thread(() -> {
                        Client.gI().close();
                    }).start();
                }
            }
        }, "Active line").start();
    }

    private void activeGame() {
        new Thread(() -> {
            while (isRunning) {
                long start = System.currentTimeMillis();
                BossManager.gI().updateAllBoss();
                long timeUpdate = System.currentTimeMillis() - start;
                if (timeUpdate < 100) {
                    try {
                        Thread.sleep(200 - timeUpdate);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }, "Update boss").start();
        new Thread(() -> {
            while (isRunning) {
                long start = System.currentTimeMillis();
                for (DoanhTrai dt : DoanhTrai.DOANH_TRAIS) {
                    dt.update();
                }
                for (BanDoKhoBau bdkb : BanDoKhoBau.BAN_DO_KHO_BAUS) {
                    bdkb.update();
                }
                long timeUpdate = System.currentTimeMillis() - start;
                if (timeUpdate < 100) {
                    try {
                        Thread.sleep(300 - timeUpdate);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }, "Update pho ban").start();
    }

    public void close(long delay) {
        isRunning = false;
        Client.gI().close();
        try {
            ClanService.gI().close();
        } catch (Exception e) {
            Logger.error("Lỗi save clan!...................................\n");
        }
        Logger.success("SUCCESSFULLY MAINTENANCE!...................................\n");
        System.exit(0);
        }
    }
