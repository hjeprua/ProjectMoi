package com.nrolove.jdbc.daos;

import com.nrolove.jdbc.DBService;
import com.nrolove.models.player.Player;
import com.nrolove.server.Client;
import com.nrolove.server.Manager;
import com.nrolove.server.ServerManager;
import com.nrolove.server.io.Message;
import com.nrolove.server.io.Session;
import com.nrolove.services.Service;
import com.nrolove.utils.Logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.logging.Level;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 *
 */
public class AccountDAO {

    public static void updateAccount(Session session) {
        PreparedStatement ps = null;
        try (Connection con = DBService.gI().getConnection();) {
            ps = con.prepareStatement("update account set password = ? where id = ? and username = ?");
            ps.setString(1, session.pp);
            ps.setInt(2, session.userId);
            ps.setString(3, session.uu);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            Logger.logException(AccountDAO.class, e);
        } finally {
            try {
                ps.close();
            } catch (SQLException ex) {
            }
        }
    }

    public static boolean login(Connection con, Message msg, Session session) {
        boolean loginSuccess = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            session.uu = msg.reader().readUTF();
            session.pp = msg.reader().readUTF();
//            System.out.println("version: " + msg.reader().readUTF());
//            System.out.println("type: " + msg.reader().readByte());
            msg.reader().readUTF();
            msg.reader().readByte();
            msg.cleanup();
            //------------------------------------------------------------------
            ps = con.prepareStatement("select * from account where username = ? and password = ? limit 1");
            ps.setString(1, session.uu);
            ps.setString(2, session.pp);
            rs = ps.executeQuery();
            if (rs.first()) {
                session.userId = rs.getInt("id");
                session.isAdmin = rs.getBoolean("is_admin");
                session.lastTimeLogout = rs.getTimestamp("last_time_logout").getTime();
                session.actived = rs.getBoolean("active");
                session.goldBar = rs.getInt("thoi_vang");
                session.dataReward = rs.getString("reward");
//                if(!session.isAdmin){
//                    Service.getInstance().sendThongBaoOK(session, "Đăng nhập chỉ dành cho admin");
//                    return false;
//                }
                if (rs.getBoolean("ban")) {
                    Service.getInstance().sendThongBaoOK(session, "Tài khoản đã bị khóa do vi phạm điều khoản tại NROLOVE!");
                } else if (rs.getTimestamp("last_time_login").getTime() > session.lastTimeLogout) {
//                    Player plInGame = PlayerService.gI().getPlayerByUserId(session.userId);
                    Player plInGame = Client.gI().getPlayerByUser(session.userId);
                    if (plInGame != null) {
                        Client.gI().kickSession(plInGame.getSession());
                        Service.getInstance().sendThongBaoOK(session, "Máy chủ tắt hoặc mất sóng");
                    } else {
                        Service.getInstance().sendThongBaoOK(session, "Tài khoản đang được đăng nhập tại máy chủ khác");
                    }
                } else {
                    long lastTimeLogout = rs.getTimestamp("last_time_logout").getTime();
                    int secondsPass = (int) ((System.currentTimeMillis() - lastTimeLogout) / 1000);
                    if (secondsPass < Manager.SECOND_WAIT_LOGIN) {
                        Service.getInstance().sendThongBaoOK(session, "Vui lòng chờ " + (Manager.SECOND_WAIT_LOGIN - secondsPass) + "s");
                    } else {
                        session.joinedGame = true;
                        loginSuccess = true;
                    }
                }
            } else {
                Service.getInstance().sendThongBaoOK(session, "Thông tin tài khoản hoặc mật khẩu không chính xác");
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            loginSuccess = false;
            Logger.logException(AccountDAO.class, e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return loginSuccess;
    }

    public static void updateLastTimeLoginAllAccount() {
        PreparedStatement ps = null;
        try (Connection con = DBService.gI().getConnection();) {
            ps = con.prepareStatement("update account set last_time_login = '2000-01-01', "
                    + "last_time_logout = '2001-01-01'");
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            Logger.logException(AccountDAO.class, e);
        } finally {
            try {
                ps.close();
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void updateLastTimeLogin(Connection con, Session session) {

        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement("update account set last_time_login = ?, ip_address = ? where id = ?");
            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.setString(2, session.ipAddress);
            ps.setInt(3, session.userId);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            Logger.logException(AccountDAO.class, e);
        } finally {
            try {
                ps.close();
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void updateAccoutLogout(Session session) {
        if (session.uu != null && session.pp != null) {
            PreparedStatement ps = null;
            try (Connection con = DBService.gI().getConnection();) {
                ps = con.prepareStatement("update account set last_time_logout = ? where id = ?");
                ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                ps.setInt(2, session.userId);
                ps.executeUpdate();
                ps.close();
            } catch (Exception e) {
                Logger.logException(AccountDAO.class, e);
            } finally {
                try {
                    ps.close();
                } catch (SQLException ex) {
                }
            }
        }
    }

    public static void banAccount(Session session) {
        PreparedStatement ps = null;
        try (Connection con = DBService.gI().getConnection();) {
            ps = con.prepareStatement("update account set ban = 1 where id = ? and username = ?");
            ps.setInt(1, session.userId);
            ps.setString(2, session.uu);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            Logger.logException(AccountDAO.class, e);
        } finally {
            try {
                ps.close();
            } catch (SQLException ex) {
            }
        }
    }

    public static int createAccount(String user, String password) {
        int key = -1;
        PreparedStatement ps = null;
        try (Connection con = DBService.gI().getConnection();) {
            ps = con.prepareStatement("select * from account where username = ?");
            ps.setString(1, user);
            if (ps.executeQuery().next()) {
                System.out.println("Tạo thất bại do tài khoản đã tồn tại");
            } else {
                ps = con.prepareStatement("insert into account(username,password) values (?,?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user);
                ps.setString(2, password);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                rs.first();
                key = rs.getInt(1);
                System.out.println("Tạo tài khoản thành công!");
            }
            ps.close();
        } catch (Exception e) {
            Logger.logException(AccountDAO.class, e);
        } finally {
            try {
                ps.close();
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return key;
    }

}
