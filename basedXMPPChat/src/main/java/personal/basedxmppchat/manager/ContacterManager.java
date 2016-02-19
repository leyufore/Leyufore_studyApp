package personal.basedxmppchat.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;

import personal.basedxmppchat.comm.Constant;
import personal.basedxmppchat.model.User;
import personal.basedxmppchat.util.StringUtil;

public class ContacterManager {
    /**
     * 保存着所有的联系人信息
     */
    public static Map<String, User> contacters = null;

    public static void init(AbstractXMPPConnection connection) {
        contacters = new HashMap<String, User>();
        for (RosterEntry entry : Roster.getInstanceFor(connection).getEntries()) {
            contacters.put(entry.getUser(),
                    transEntryToUser(entry, Roster.getInstanceFor(connection)));
        }
    }

    public static void destroy() {
        contacters = null;
    }

    /**
     * 获得所有的联系人列表
     *
     * @return
     */
    public static List<User> getContacterList() {
        if (contacters == null)
            throw new RuntimeException("contacters is null");

        List<User> userList = new ArrayList<User>();

        for (String key : contacters.keySet())
            userList.add(contacters.get(key));

        return userList;
    }

    /**
     * 获得所有未分组的联系人列表
     *
     * @return
     */
    public static List<User> getNoGroupUserList(Roster roster) {
        List<User> userList = new ArrayList<User>();

        // 服务器的用户信息改变后，不会通知到unfiledEntries
        //getUnfiledEntries()表示获取未分组的联系人.注意,contacters里面包含了已分组与未分组的联系人
        for (RosterEntry entry : roster.getUnfiledEntries()) {
            userList.add(contacters.get(entry.getUser()).clone());
        }

        return userList;
    }

    /**
     * 获得所有分组联系人
     *
     * @return
     */
    public static List<MRosterGroup> getGroups(Roster roster) {
        if (contacters == null)
            throw new RuntimeException("contacters is null");

        List<MRosterGroup> groups = new ArrayList<ContacterManager.MRosterGroup>();
        groups.add(new MRosterGroup(Constant.ALL_FRIEND, getContacterList()));
        for (RosterGroup group : roster.getGroups()) {
            List<User> groupUsers = new ArrayList<User>();
            for (RosterEntry entry : group.getEntries()) {
                groupUsers.add(contacters.get(entry.getUser()));
            }
            groups.add(new MRosterGroup(group.getName(), groupUsers));
        }
        groups.add(new MRosterGroup(Constant.NO_GROUP_FRIEND,
                getNoGroupUserList(roster)));
        return groups;
    }

    /**
     * 根据RosterEntry创建一个User
     *
     * @param entry
     * @return
     */
    public static User transEntryToUser(RosterEntry entry, Roster roster) {
        User user = new User();
        if (entry.getName() == null) {
            user.setName(StringUtil.getUserNameByJid(entry.getUser()));
        } else {
            user.setName(entry.getName());
        }
        user.setJID(entry.getUser());
        System.out.println(entry.getUser());
        Presence presence = roster.getPresence(entry.getUser());
        user.setFrom(presence.getFrom());
        user.setStatus(presence.getStatus());
        user.setSize(entry.getGroups().size());
        user.setAvailable(presence.isAvailable());
        user.setType(entry.getType());
        return user;
    }

    /**
     * 修改这个好友的昵称
     *
     * @param user
     * @param nickname
     */
    public static void setNickname(User user, String nickname,
                                   XMPPConnection connection) {
        /**
         * 从源码中查看,这里的entry.setName()会发送stanza包
         * setName()源码中的这句: connection().createPacketCollectorAndSend(packet).nextResultOrThrow();
         * 这一句是一个阻塞函数,只有服务端成功修改,且客户端获得响应后才会正常退出
         * 知识点 : packetCollector是阻塞的接收器.
         */
        RosterEntry entry = Roster.getInstanceFor(connection).getEntry(user.getJID());
        try {
            entry.setName(nickname);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        }
    }

    /**
     * 把一个好友添加到一个组中
     *
     * @param user
     * @param groupName
     */
    public static void addUserToGroup(final User user, final String groupName,
                                      final XMPPConnection connection) {
        if (groupName == null || user == null)
            return;
        // 将一个rosterEntry添加到group中是PacketCollector，会阻塞线程
        new Thread() {
            public void run() {
                RosterGroup group = Roster.getInstanceFor(connection).getGroup(groupName);
                // 这个组已经存在就添加到这个组，不存在创建一个组
                RosterEntry entry = Roster.getInstanceFor(connection).getEntry(
                        user.getJID());
                try {
                    if (group != null) {
                        if (entry != null)
                            group.addEntry(entry);
                    } else {
                        RosterGroup newGroup = Roster.getInstanceFor(connection)
                                .createGroup(groupName);
                        if (entry != null)
                            newGroup.addEntry(entry);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 把一个好友从组中删除
     *
     * @param user
     * @param groupName
     */
    public static void removeUserFromGroup(final User user,
                                           final String groupName, final XMPPConnection connection) {
        if (groupName == null || user == null)
            return;
        new Thread() {
            public void run() {
                RosterGroup group = Roster.getInstanceFor(connection).getGroup(groupName);
                if (group != null) {
                    try {
                        System.out.println(user.getJID() + "----------------");
                        RosterEntry entry = Roster.getInstanceFor(connection).getEntry(
                                user.getJID());
                        if (entry != null)
                            try {
                                group.removeEntry(entry);
                            } catch (SmackException.NoResponseException e) {
                                e.printStackTrace();
                            } catch (SmackException.NotConnectedException e) {
                                e.printStackTrace();
                            }
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public static class MRosterGroup {
        private String name;
        private List<User> users;

        public MRosterGroup(String name, List<User> users) {
            this.name = name;
            this.users = users;
        }

        public int getCount() {
            if (users != null)
                return users.size();
            return 0;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<User> getUsers() {
            return users;
        }

        public void setUsers(List<User> users) {
            this.users = users;
        }

    }

    /**
     * 根据jid获得用户昵称
     *
     * @param Jid
     * @param connection
     * @return
     * @author shimiso
     * @update 2012-6-28 上午10:49:14
     */
    public static User getNickname(String Jid, XMPPConnection connection) {
        Roster roster = Roster.getInstanceFor(connection);
        for (RosterEntry entry : roster.getEntries()) {
            String params = entry.getUser();
            if (params.split("/")[0].equals(Jid)) {
                return transEntryToUser(entry, roster);
            }
        }
        return null;

    }

    /**
     * 添加分组 .
     *
     * @param groupName
     * @param connection
     * @author shimiso
     * @update 2012-6-28 下午3:30:32
     */
    public static void addGroup(final String groupName,
                                final XMPPConnection connection) {
        if (StringUtil.empty(groupName)) {
            return;
        }

        // 将一个rosterEntry添加到group中是PacketCollector，会阻塞线程
        new Thread() {
            public void run() {

                try {
                    RosterGroup g = Roster.getInstanceFor(connection).getGroup(groupName);
                    if (g != null) {
                        return;
                    }
                    Roster.getInstanceFor(connection).createGroup(groupName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 获得所有组名
     *
     * @return
     */
    public static List<String> getGroupNames(Roster roster) {

        List<String> groupNames = new ArrayList<String>();
        for (RosterGroup group : roster.getGroups()) {
            groupNames.add(group.getName());
        }
        return groupNames;
    }

    /**
     * 从花名册中删除用户.
     *
     * @param userJid
     * @throws XMPPException
     * @author shimiso
     * @update 2012-7-3 下午2:42:54
     */
    public static void deleteUser(String userJid) throws XMPPException {

        Roster roster = Roster.getInstanceFor(XmppConnectionManager.getInstance().getConnection());
        RosterEntry entry = roster.getEntry(userJid);
        try {
            Roster.getInstanceFor(XmppConnectionManager.getInstance().getConnection()).removeEntry(entry);
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 根据用户jid得到用户
     *
     * @param userJId
     * @param connection
     */
    public static User getByUserJid(String userJId, XMPPConnection connection) {
        Roster roster = Roster.getInstanceFor(connection);
        RosterEntry entry = Roster.getInstanceFor(connection).getEntry(userJId);
        if (null == entry) {
            return null;
        }
        User user = new User();
        if (entry.getName() == null) {
            user.setName(StringUtil.getUserNameByJid(entry.getUser()));
        } else {
            user.setName(entry.getName());
        }
        user.setJID(entry.getUser());
        System.out.println(entry.getUser());
        Presence presence = roster.getPresence(entry.getUser());
        user.setFrom(presence.getFrom());
        user.setStatus(presence.getStatus());
        user.setSize(entry.getGroups().size());
        //available - online ,unavailable - offline
        user.setAvailable(presence.isAvailable());
        /**
         * ItemType api文档解释,ItemType有五个值
         */
        /**
         * The user does not have a subscription to the contact's presence, and the contact does not
         * have a subscription to the user's presence; this is the default value, so if the
         * subscription attribute is not included then the state is to be understood as "none".
         */
        //none,

        /**
         * The user has a subscription to the contact's presence, but the contact does not have a
         * subscription to the user's presence.
         */
        // to,

        /**
         * The contact has a subscription to the user's presence, but the user does not have a
         * subscription to the contact's presence.
         */
        // from,

        /**
         * The user and the contact have subscriptions to each other's presence (also called a
         * "mutual subscription").
         */
        //  both,

        /**
         * The user wishes to stop receiving presence updates from the subscriber.
         */
        // remove
        user.setType(entry.getType());
        return user;

    }

}
