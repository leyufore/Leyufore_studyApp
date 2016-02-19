package personal.basedxmppchat.activity.im;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;

import java.util.ArrayList;
import java.util.List;

import personal.basedxmppchat.R;
import personal.basedxmppchat.activity.LoginActivity;
import personal.basedxmppchat.comm.Constant;
import personal.basedxmppchat.manager.ContacterManager;
import personal.basedxmppchat.manager.MessageManager;
import personal.basedxmppchat.manager.NoticeManager;
import personal.basedxmppchat.manager.XmppConnectionManager;
import personal.basedxmppchat.model.ChartHisBean;
import personal.basedxmppchat.model.Notice;
import personal.basedxmppchat.model.User;
import personal.basedxmppchat.util.StringUtil;
import personal.basedxmppchat.view.ContacterExpandAdapter;
import personal.basedxmppchat.view.LayoutChangeListener;
import personal.basedxmppchat.view.RecentChartAdapter;
import personal.basedxmppchat.view.ScrollLayout;
import personal.basedxmppchat.manager.ContacterManager.MRosterGroup;

/**
 * ContacterMainActivity 设计思路:
 * 关键布局 : ScrollLayout
 * 布局思路 : 上方三个Tab ,三个Tab布局下方有一个绿色选择条,用于显示当前处于哪一个View 下方一个ScrollLayout 装载三个View
 * View1 : contacterTab1(没实现具体作用)
 * View2 (分组联系人) : 关键组件ExpandableListView -- 关联的适配器 expandAdapter   -- 关联数据 rGroups
 * View3 (历史聊天记录) : 关键组件ListView -- 关联的适配器 noticeAdapter    -- 关联数据 inviteNotices
 *
 * View2中的ExpandableListView中的每个用户View都会利用setTag()把其View与其User对象相关联.(该设计思路与网站类似)
 * 该关联数据用于点击用户View时,跳转ChatActivity时,Intent携带所跳转用户的基本信息数据.
 * 绿色选择条ImageView的移动是利用设置View的TranlationAnimation来实现动画移动.
 *
 * Activity中有三个监听器 :
 * 1.联系人View的setOnCreateContextMenuListener
 * 2.联系人View的setOnChildClickListener
 * 3.聊天记录View的setOnClickListener
 *
 * 该界面实现的Receiver :接收7中action的广播    对应IMContactService中监听的情况.当服务端数据被修改后,会向客户端回复相应信息.这是smack提供的功能.
 * 1.ROSTER_ADDED:
 *  IMContacterService中,当服务端数据被修改后,IMContactService中的rosterListener(smack中的监听器)中的entriesAdded函数将会被触发,定义一个广播发送出去
 *  该Activity接收到广播时,调用refreshList()函数刷新视图
 * 2.ROSTER_DELETED:类似ROSTER_ADD
 * 3.ROSTER_PRESENCE_CHANGED
 * 4.ROSTER_UPDATED
 * 5.ROSTER_SUBSCRIPTION
 * 6.NEW_MESSAGE_ACTION
 * 7.ACTION_RECONNECT_STATE
 *
 * refreshList()函数:
 * 重新获取数据,A.刷新分组联系人视图 B.刷新聊天记录视图 C.刷新泡泡视图
 *
 * onResume()中调用refreshList函数刷新视图,由于在读取与某用户聊天消息时(e.g. 进入ChatActivity)后退回到该界面时,由于信息被读取了,所以得刷新视图.其中,信息的状态由数据库决定.
 *
 * 功能思路:
 * 1.添加分组   该处并没有在服务器创建分组.在向分组中添加用户时,才会真正调用API添加分组
 * addNewGroup();
 * -- addGroupNamesUi UI级添加分组 groupNames,newNames,rGroup集合均添加新分组
 * 2.更改组名
 * updateGroupNameA(groupName);
 * -- updateGroupNameUI(groupName, gNewName); UI级修改操作 newNames,rGroup集合中修改相应组
 * -- updateGroupName(groupName, gNewName);  UIAPI 对服务器数据进行修改
 * 3.设置昵称           ???????未完善 如何刷新页面视图信息
 * setNickname(clickUser);  API
 * 4.添加好友           ???????未完善 如何刷新页面视图信息
 * addSubscriber();
 * -- createSubscriber API
 * 5.删除好友
 * showDeleteDialog(clickUser);
 * -- deleteUserUI(clickUser);   UI删除   删除rGroup中的用户,刷新视图
 * -- removeSubscriber(clickUser.getJID());  API删除
 * -- NoticeManager.getInstance(context).delNoticeHisWithSb(clickUser.getJID());   删除数据库中好友的系统消息
 * -- MessageManager.getInstance(context).delChatHisWithSb(clickUser.getJID());    删除数据库中好友的聊天消息
 * 6.移动到分组 （1.先移除本组，2移入某组）
 * removeUserFromGroupUI(clickUser);    UI移除old组    删除rGroup中该组的用户
 * removeUserFromGroup(clickUser,clickUser.getGroupName());     API
 * addToGroup(clickUser);
 * -- 若newNames集合包含要移至的分组,则删除分组     if (newNames.contains(groupName)) {newNames.remove(groupName);}
 * -- addUserGroupUI(user, groupName);  UI级把用户移到某组
 * -- addUserToGroup(user, groupName);   API移入组
 * 7.移出组
 * removeUserFromGroupUI(clickUser);  UI移除old组  删除rGroup中该组的用户
 * removeUserFromGroup(clickUser,clickUser.getGroupName());    API级出某组
 *
 * 补充理解:
 * smack中的一个问题:添加人的时候是不会考虑服务器中是否存在这个用户
 * UI级别刷新 : 更新关联数据,调用适配器的notifyDataSetChanged方法刷新视图.并没有使用smack去修改服务端数据.
 * API : 修改服务端数据
 *
 *
 * bug :
 * 1.添加分组功能,由于只在移动用户到分组时才会真正在服务器创建分组.导致添加分组如果没有添加用户时候,退出重新登录是没有该新分组的.
 * 2.修改分组名字功能,当要修改的组名是新添加的但是没有添加到服务器端的，只是ui级添加的.应用会出错退出
 *
 * 对手机客户端的思考:
 * 可能有时候可以在客户端先做缓存,在实际有必须时或程序休闲时,才对所作修改的缓存统一向服务端请求.可以更有效利用资源.
 * 可是,对这种即时通信的应用,貌似不实际.
 *
 * learner : leyufore
 */
public class ContacterMainActivity extends AContacterActivity implements
        LayoutChangeListener, OnClickListener {
    private final static String TAG = "ContacterMainActivity";
    private LayoutInflater inflater;
    private ScrollLayout layout;    //自定义的ScollLayout (ViewGroup) 装载三个View(Tab页)
    private ImageView imageView;    //三个Tab下方的绿色选择条
    private ImageView tab1;
    private ImageView tab2;
    private ImageView tab3;
    private ExpandableListView contacterList = null;    //分组联系人页面
    private ContacterExpandAdapter expandAdapter = null;
    private ListView inviteList = null;
    private RecentChartAdapter noticeAdapter = null;
    private List<ChartHisBean> inviteNotices = new ArrayList<ChartHisBean>();
    private ImageView headIcon;
    private TextView noticePaopao;  //通知泡泡 (e.g. 该界面中聊天记录Tab的消息泡泡)(有未读消息时显示未读数量的泡泡,没有未读消息时不显示泡泡)
    private List<String> groupNames;    //初始化时为服务器中的所有分组名称集合
    private List<String> newNames = new ArrayList<String>();    //用于只在UI级别更新时的分组名称集合 (e.g.当前还不会立刻同步到服务器中,只是先缓存起来,先迅速更新UI)
    private List<MRosterGroup> rGroups;     //保存着所有的分组信息(新旧信息都包含)
    private ImageView iv_status;    //用户状态(上线,下线)
    private User clickUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacter_main);
        init();
    }

    private void init() {
        try {
            groupNames = ContacterManager.getGroupNames(Roster.getInstanceFor(XmppConnectionManager.getInstance().getConnection()));
            rGroups = ContacterManager.getGroups(Roster.getInstanceFor(XmppConnectionManager.getInstance().getConnection()));
        } catch (Exception e) {
            groupNames = new ArrayList<String>();
            rGroups = new ArrayList<MRosterGroup>();
        }
        iv_status = (ImageView) findViewById(R.id.imageView1);
        getEimApplication().addActivity(this);
        inflater = LayoutInflater.from(context);
        layout = (ScrollLayout) findViewById(R.id.scrolllayout);
        /**
         * ScrollLayout里面有属性 Vector<LayoutChangeListener> --存储一些自定义的监听器
         * 监听器作用的时间:在进行snapToDestination()函数里面会同时调用监听器的响应函数
         * (e.g. 在手指离开时候,ScrollLayout进行动画跳转Tab时,调用监听器的函数,实际上就是实现上方
         *  菜单栏的绿色选择条的移动)
         */
        layout.addChangeListener(this);
        tab1 = (ImageView) findViewById(R.id.tab1);
        tab2 = (ImageView) findViewById(R.id.tab2);
        tab3 = (ImageView) findViewById(R.id.tab3);
        noticePaopao = (TextView) findViewById(R.id.notice_paopao);

        imageView = (ImageView) findViewById(R.id.top_bar_select);

        View contacterTab1 = inflater.inflate(R.layout.contacter_tab1, null);
        View contacterTab2 = inflater.inflate(R.layout.contacter_tab2, null);
        View contacterTab3 = inflater.inflate(R.layout.contacter_tab3, null);
        layout.addView(contacterTab1);
        layout.addView(contacterTab2);
        layout.addView(contacterTab3);
        /**
         * 界面中有三个Tab,使其最初显示时,显示中间的Tab
         */
        layout.setToScreen(1);

        contacterList = (ExpandableListView) findViewById(R.id.main_expand_list);
        ImageView titleBack = (ImageView) findViewById(R.id.title_back);
        headIcon = (ImageView) findViewById(R.id.head_icon);
        headIcon.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, UserInfoActivity.class);
                startActivity(intent);
            }
        });
        titleBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // 联系人
        expandAdapter = new ContacterExpandAdapter(context, rGroups);
        contacterList.setAdapter(expandAdapter);
        /**
         * setOnCreateContextMenuListener : 长按某项弹出上下文菜单
         * learner : leyufore
         */
        contacterList.setOnCreateContextMenuListener(onCreateContextMenuListener);
        /**
         * 点击ChildView时触发 : 跳转去ChatActivity,Intent携带user.getJID()
         */
        contacterList.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                createChat((User) v.findViewById(R.id.username).getTag());
                /**
                 * True if the click was handled
                 * 设置为true时,事件不再传递
                 * learner : leyufore
                 */
                return false;
            }
        });
        // 未读信息
        inviteList = (ListView) findViewById(R.id.main_invite_list);
        /**
         * inviteNotices : 存储最近聊天人聊天最后一条消息和未读消息总数
         * 功能 : 类似qq,微信的聊天记录页面,显示聊天人聊天最后一条消息和未读消息总数
         * learner : leyufore
         */
        inviteNotices = MessageManager.getInstance(context)
                .getRecentContactsWithLastMsg();
        noticeAdapter = new RecentChartAdapter(context, inviteNotices);
        inviteList.setAdapter(noticeAdapter);
        noticeAdapter.setOnClickListener(contacterOnClickJ);

    }

    /**
     * 有新消息进来
     */
    @Override
    protected void msgReceive(Notice notice) {
        for (ChartHisBean ch : inviteNotices) {
            if (ch.getFrom().equals(notice.getFrom())) {
                ch.setContent(notice.getContent());
                ch.setNoticeTime(notice.getNoticeTime());
                Integer x = ch.getNoticeSum() == null ? 0 : ch.getNoticeSum();
                ch.setNoticeSum(x + 1);
            }
        }
        noticeAdapter.setNoticeList(inviteNotices);
        noticeAdapter.notifyDataSetChanged();
        setPaoPao();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
        if (getUserOnlineState()) {
            iv_status.setImageDrawable(getResources().getDrawable(
                    R.drawable.status_online));
        } else {
            iv_status.setImageDrawable(getResources().getDrawable(
                    R.drawable.status_offline));
        }

    }

    /**
     * 通知点击
     */
    private OnClickListener contacterOnClickJ = new OnClickListener() {

        @Override
        public void onClick(View v) {
            createChat((User) v.findViewById(R.id.new_content).getTag());

        }
    };

    /**
     * 设置昵称
     *
     * @param user
     */
    private void setNickname(final User user) {
        final EditText name_input = new EditText(context);
        name_input.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
        name_input.setHint("输入昵称");
        new AlertDialog.Builder(context).setTitle("修改昵称").setView(name_input)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = name_input.getText().toString();
                        if (!"".equals(name))
                            setNickname(user, name);
                    }
                }).setNegativeButton("取消", null).show();
    }

    /**
     * 添加好友
     */
    private void addSubscriber() {
        final EditText name_input = new EditText(context);
        name_input.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
        name_input.setHint(getResources().getString(R.string.input_username));
        final EditText nickname = new EditText(context);
        nickname.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
        nickname.setHint(getResources().getString(R.string.set_nickname));
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
        layout.addView(name_input);
        layout.addView(nickname);
        new AlertDialog.Builder(context).setTitle("添加好友").setView(layout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String userName = name_input.getText().toString();
                        String nickname_in = nickname.getText().toString();
                        if (StringUtil.empty(userName)) {
                            showToast(getResources().getString(
                                    R.string.username_not_null));
                            return;
                        }
                        userName = StringUtil.doEmpty(userName);
                        if (StringUtil.empty(nickname_in)) {
                            nickname_in = null;
                        }

                        if (isExitJid(StringUtil.getJidByName(userName),
                                rGroups)) {
                            showToast(getResources().getString(
                                    R.string.username_exist));
                            return;
                        }
                        try {
                            createSubscriber(StringUtil.getJidByName(userName),
                                    nickname_in, null);
                        } catch (XMPPException e) {
                        }
                    }
                }).setNegativeButton("取消", null).show();
    }

    /**
     * 加入组
     *
     * @param user
     */
    private void addToGroup(final User user) {
        LayoutInflater inflaterx = LayoutInflater.from(context);
        View dialogView = inflaterx.inflate(R.layout.yd_group_dialog, null);
        final Spinner spinner = (Spinner) dialogView.findViewById(R.id.list);
        ArrayAdapter<String> ada = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_dropdown_item, groupNames);
        spinner.setAdapter(ada);

        new AlertDialog.Builder(context).setTitle("移动" + "至分组")
                .setView(dialogView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String groupName = (spinner.getSelectedItem())
                                .toString();
                        if (StringUtil.notEmpty(groupName)) {
                            groupName = StringUtil.doEmpty(groupName);
                            if (newNames.contains(groupName)) {
                                newNames.remove(groupName);
                            }
                            // UI级把用户移到某组
                            addUserGroupUI(user, groupName);

                            // api移入组
                            addUserToGroup(user, groupName);
                        }
                    }
                }).setNegativeButton("取消", null).show();
    }

    /**
     * 修改组名
     *
     * @param user
     */
    private void updateGroupNameA(final String groupName) {
        final EditText name_input = new EditText(context);
        name_input.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
        name_input.setHint("输入组名");
        new AlertDialog.Builder(context).setTitle("修改组名").setView(name_input)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String gNewName = name_input.getText().toString();
                        if (newNames.contains(gNewName)
                                || groupNames.contains(gNewName)) {
                            showToast("组名已存在");
                            return;
                        }
                        // UI级修改操作
                        updateGroupNameUI(groupName, gNewName);
                        // UIAPI
                        updateGroupName(groupName, gNewName);

                        /**
                         * 该更新组名的方法有bug:当要修改的组名是新添加的但是没有添加到服务器端的，只是ui级添加的.应用会出错退出.
                         * 原因 : 新添加的组名由于未在服务器中实际添加,而导致之后调用的updateGroupName()中无法找到相应组进行设置,导致出错.
                         * learner : leyufore
                         */

                    }
                }).setNegativeButton("取消", null).show();
    }

    private OnItemClickListener inviteListClick = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int arg2,
                                long arg3) {
            final Notice notice = (Notice) view.findViewById(R.id.new_content)
                    .getTag();
            if (notice.getNoticeType() == Notice.CHAT_MSG) {
                User user = new User();
                user.setJID("admin@zkost.com");
                createChat(user);
            } else {
                final String subFrom = notice.getFrom();
                new AlertDialog.Builder(context)
                        .setMessage(subFrom + "请求添加您为好友")
                        .setTitle("提示")
                        .setPositiveButton("添加",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // 接受请求
                                        sendSubscribe(Presence.Type.subscribed,
                                                subFrom);
                                        sendSubscribe(Presence.Type.subscribe,
                                                subFrom);
                                        refreshList();

                                    }
                                })
                        .setNegativeButton("拒绝",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        sendSubscribe(
                                                Presence.Type.unsubscribe,
                                                subFrom);
                                    }
                                }).show();
            }

        }
    };

    /**
     * 刷新当前的列表
     */
    private void refreshList() {
        rGroups = ContacterManager.getGroups(Roster.getInstanceFor(XmppConnectionManager.getInstance().getConnection()));
        for (String newGroupName : newNames) {
            MRosterGroup mg = new MRosterGroup(newGroupName,
                    new ArrayList<User>());
            rGroups.add(rGroups.size() - 1, mg);
        }
        expandAdapter.setContacter(rGroups);
        expandAdapter.notifyDataSetChanged();

        // 刷新notice信息
        inviteNotices = MessageManager.getInstance(context)
                .getRecentContactsWithLastMsg();
        noticeAdapter.setNoticeList(inviteNotices);
        noticeAdapter.notifyDataSetChanged();
        /**
         * 有新消息进来的气泡设置
         */
        setPaoPao();

    }

    @Override
    protected void addUserReceive(User user) {
        refreshList();
    }

    @Override
    protected void deleteUserReceive(User user) {
        if (user == null)
            return;
        Toast.makeText(
                context,
                (user.getName() == null) ? user.getJID() : user.getName()
                        + "被删除了", Toast.LENGTH_SHORT).show();
        refreshList();
    }

    @Override
    protected void changePresenceReceive(User user) {
        if (user == null)
            return;
        if (ContacterManager.contacters.get(user.getJID()) == null)
            return;
        // 下线
        if (!user.isAvailable())
            if (ContacterManager.contacters.get(user.getJID()).isAvailable())
                Toast.makeText(
                        context,
                        (user.getName() == null) ? user.getJID() : user
                                .getName() + "上线了", Toast.LENGTH_SHORT).show();
        // 上线
        if (user.isAvailable())
            if (!ContacterManager.contacters.get(user.getJID()).isAvailable())
                Toast.makeText(
                        context,
                        (user.getName() == null) ? user.getJID() : user
                                .getName() + "下线了", Toast.LENGTH_SHORT).show();
        refreshList();
    }

    @Override
    protected void updateUserReceive(User user) {
        refreshList();
    }

    @Override
    protected void subscripUserReceive(final String subFrom) {
        Notice notice = new Notice();
        notice.setFrom(subFrom);
        notice.setNoticeType(Notice.CHAT_MSG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contacter_menu, menu);
        return true;
    }

    /**
     * 修改状态
     */
    private void modifyState() {
        String[] states = new String[]{"在线", "隐身", "吃饭", "睡觉"};
        new AlertDialog.Builder(this)
                .setItems(states, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Presence presence = new Presence(
                                Presence.Type.available);
                        switch (which) {
                            case 0:
                                break;
                            case 1:
                                presence.setType(Presence.Type.unavailable);
                                break;
                            case 2:
                                presence.setStatus("吃饭");
                                break;
                            case 3:
                                presence.setStatus("睡觉");
                                break;
                        }
                        try {
                            XmppConnectionManager.getInstance().getConnection()
                                    .sendPacket(presence);
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }
                    }
                }).setPositiveButton("取消", null).setTitle("修改状态").show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.menu_add_subscriber:
                addSubscriber();
                break;
            case R.id.menu_modify_state:
                modifyState();
                break;
            case R.id.menu_relogin:
                intent.setClass(context, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.menu_exit:
                isExit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void doChange(int lastIndex, int currentIndex) {
        if (lastIndex != currentIndex) {
            /**
             * TranslateAnimation类比较常用，比如QQ，网易新闻菜单条的动画(移动)
             * 该Animation用于实现水平平移效果,绿色选择条移动的位置数据可以参考tab1,ta2的位置数据
             *
             * learner : leyufore
             */
            TranslateAnimation animation = null;
            LinearLayout layout = null;
            switch (currentIndex) {
                case 0:
                    if (lastIndex == 1) {
                        layout = (LinearLayout) tab1.getParent();
                        animation = new TranslateAnimation(0, -layout.getWidth(),
                                0, 0);
                    } else if (lastIndex == 2) {
                        layout = (LinearLayout) tab2.getParent();
                        animation = new TranslateAnimation(layout.getLeft(),
                                -((LinearLayout) tab1.getParent()).getWidth(), 0, 0);
                    }
                    break;
                case 1:
                    if (lastIndex < 1) {
                        // 左到中
                        layout = (LinearLayout) tab1.getParent();
                        animation = new TranslateAnimation(-layout.getWidth(), 0,
                                0, 0);
                    } else if (lastIndex > 1) {
                        // 右到中
                        layout = (LinearLayout) tab2.getParent();
                        animation = new TranslateAnimation(layout.getLeft(), 0, 0,
                                0);
                    }
                    break;
                case 2:
                    if (lastIndex == 1) {
                        layout = (LinearLayout) tab2.getParent();
                        animation = new TranslateAnimation(0, layout.getLeft(), 0,
                                0);
                    } else if (lastIndex == 0) {
                        layout = (LinearLayout) tab2.getParent();
                        animation = new TranslateAnimation(
                                -((LinearLayout) tab1.getParent()).getWidth(),
                                layout.getLeft(), 0, 0);
                    }
                    break;
            }
            animation.setDuration(300);
            /**
             * setFillAfter(true)是动画结束后,是View固定在结束后的位置.不然View会在动画结束后回到原始的位置.
             * learner : leyufore
             */
            animation.setFillAfter(true);
            /**
             * 开启动画
             * learner : leyufore
             */
            imageView.startAnimation(animation);
        }
    }

    @Override
    public void onClick(View v) {

        if (v == tab1) {
            layout.snapToScreen(0);

        } else if (v == tab2) {
            layout.snapToScreen(1);

        } else if (v == tab3) {
            layout.snapToScreen(2);

        }
    }

    /**
     * 上面滚动条上的气泡设置 有新消息来的通知气泡，数量设置,
     */
    private void setPaoPao() {
        if (null != inviteNotices && inviteNotices.size() > 0) {
            int paoCount = 0;
            for (ChartHisBean c : inviteNotices) {
                Integer countx = c.getNoticeSum();
                paoCount += (countx == null ? 0 : countx);
            }
            if (paoCount == 0) {
                noticePaopao.setVisibility(View.GONE);
                return;
            }
            noticePaopao.setText(paoCount + "");
            noticePaopao.setVisibility(View.VISIBLE);
        } else {
            noticePaopao.setVisibility(View.GONE);
        }
    }

    /**
     * 加入组
     *
     * @param user
     */
    private void addNewGroup() {
        final EditText name_input = new EditText(context);
        name_input.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
        name_input.setHint("输入组名");
        new AlertDialog.Builder(context).setTitle("加入组").setView(name_input)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String groupName = name_input.getText().toString();
                        if (StringUtil.empty(groupName)) {
                            // showToast("组名不能为空");
                            return;
                        }
                        // ui上增加数据
                        if (groupNames.contains(groupName)) {
                            // showToast("组名已经存在");
                            return;
                        }
                        addGroupNamesUi(groupName);

                    }
                }).setNegativeButton("取消", null).show();
    }

    OnCreateContextMenuListener onCreateContextMenuListener = new OnCreateContextMenuListener() {
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenuInfo menuInfo) {

            ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;

            /**
             * getPackedPositionType():Gets the type of a packed position
             * 译文:获取expandableListView的填充位置(自我理解:获取点击处的位置类型)
             * 有三种位置信息:1.PACKED_POSITION_TYPE_CHILD 2.PACKED_POSITION_TYPE_GROUP 3.PACKED_POSITION_TYPE_NULL
             */
            // 类型，0代表是group类，1代表是child类
            int type = ExpandableListView
                    .getPackedPositionType(info.packedPosition);

            if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                /**
                 * getPackedPotisionGroup():获取所点击位置的分组postion.
                 * 个人理解:大可以将list中potion看成是list Id来看.因为postion只有自增的一个值,与Id规律相同
                 */
                int gId = ExpandableListView
                        .getPackedPositionGroup(info.packedPosition);
                String[] longClickItems = null;
                final String groupName = rGroups.get(gId).getName();
                if (StringUtil.notEmpty(groupName)
                        && !Constant.ALL_FRIEND.equals(groupName)
                        && !Constant.NO_GROUP_FRIEND.equals(groupName)) {
                    longClickItems = new String[]{"添加分组", "更改组名",};
                } else {
                    longClickItems = new String[]{"添加分组"};

                }
                new AlertDialog.Builder(context)
                        .setItems(longClickItems,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        switch (which) {
                                            case 0:// 添加分组
                                                addNewGroup();
                                                break;

                                            case 1:// 更改组名
                                                updateGroupNameA(groupName);
                                                break;
                                        }
                                    }
                                }).setTitle("选项").show();
            } else if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {

                String[] longClickItems = null;

                View vx = info.targetView;
                clickUser = (User) vx.findViewById(R.id.username).getTag();
                showToast(clickUser.getJID() + "---");

                if (StringUtil.notEmpty(clickUser.getGroupName())
                        && !Constant.ALL_FRIEND
                        .equals(clickUser.getGroupName())
                        && !Constant.NO_GROUP_FRIEND.equals(clickUser
                        .getGroupName())) {
                    longClickItems = new String[]{"设置昵称", "添加好友", "删除好友",
                            "移动到分组", "退出该组"};
                } else {
                    longClickItems = new String[]{"设置昵称", "添加好友", "删除好友",
                            "移动到分组"};
                }
                new AlertDialog.Builder(context)
                        .setItems(longClickItems,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        switch (which) {
                                            case 0:// 设置昵称
                                                setNickname(clickUser);
                                                break;
                                            case 1:// 添加好友
                                                addSubscriber();
                                                break;
                                            case 2:// 删除好友

                                                showDeleteDialog(clickUser);

                                                break;
                                            case 3:// 移动到分组 （1.先移除本组，2移入某组）
                                                /**
                                                 * ui移除old组
                                                 */
                                                removeUserFromGroupUI(clickUser);

                                                removeUserFromGroup(clickUser,
                                                        clickUser.getGroupName());
                                                addToGroup(clickUser);

                                                break;

                                            case 4:// 移出组
                                                /**
                                                 * ui移除old组
                                                 */
                                                removeUserFromGroupUI(clickUser);
                                                /**
                                                 * api级出某组
                                                 */
                                                removeUserFromGroup(clickUser,
                                                        clickUser.getGroupName());
                                                break;
                                        }
                                    }

                                }).setTitle("选项").show();
            }
        }

    };

    public void delayRefresh() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                // TODO Auto-generated method stub
                refreshList();

            }
        });
    }

    /**
     * UI级添加分组 这里用一句话描述这个方法的作用.
     *
     * @author shimiso
     * @update 2012-7-2 下午1:04:09
     */
    public void addGroupNamesUi(String newGroupName) {
        groupNames.add(newGroupName);
        newNames.add(newGroupName);
        MRosterGroup mg = new MRosterGroup(newGroupName, new ArrayList<User>());
        rGroups.add(rGroups.size() - 1, mg);
        // 刷新用户信息
        expandAdapter.setContacter(rGroups);
        expandAdapter.notifyDataSetChanged();
    }

    /**
     * UI级删除用户
     */
    private void deleteUserUI(User user) {
        for (MRosterGroup g : rGroups) {
            List<User> us = g.getUsers();
            if (us != null && us.size() > 0) {
                if (us.contains(user)) {
                    us.remove(user);
                    g.setUsers(us);
                }
            }
        }
        expandAdapter.setContacter(rGroups);
        expandAdapter.notifyDataSetChanged();

    }

    /**
     * UI级移动用户，把用户移除某组
     */

    private void removeUserFromGroupUI(User user) {

        for (MRosterGroup g : rGroups) {
            if (g.getUsers().contains(user)) {
                if (StringUtil.notEmpty(g.getName())
                        && !Constant.ALL_FRIEND.equals(g.getName())) {
                    List<User> users = g.getUsers();
                    users.remove(user);
                    g.setUsers(users);

                }
            }
        }
        expandAdapter.setContacter(rGroups);
        expandAdapter.notifyDataSetChanged();
    }

    /**
     * UI级移动用户，把用户加入某组
     */

    private void addUserGroupUI(User user, String groupName) {
        for (MRosterGroup g : rGroups) {
            if (groupName.equals(g.getName())) {
                List<User> users = g.getUsers();
                users.add(user);
                g.setUsers(users);
            }
        }
        expandAdapter.setContacter(rGroups);
        expandAdapter.notifyDataSetChanged();

    }

    /**
     * UI更改组名
     */

    private void updateGroupNameUI(String old, String newGroupName) {
        /**
         * updateGroupNameUI实现了这样一个过程:
         * 修改newNames,rGroups,刷新视图
         * 为了逻辑性更好,感觉属性groupNames也进行更新会更好.
         * 不过这对功能上并没有影响,由于实际上联系expandAdapter的属性是rGroups,这里面改变了就没问题了
         * learner : leyufore
         */
        if (StringUtil.empty(old) || Constant.ALL_FRIEND.equals(old)
                || Constant.NO_GROUP_FRIEND.equals(old)) {
            return;
        }
        // 虽然没必要，但是如果输入忘记限制
        if (StringUtil.empty(newGroupName)
                || Constant.ALL_FRIEND.equals(newGroupName)
                || Constant.NO_GROUP_FRIEND.equals(newGroupName)) {
            return;
        }

        // 要修改的组名是新添加的但是没有添加到服务器端的，只是ui级添加的，如下操作
        if (newNames.contains(old)) {
            newNames.remove(old);
            newNames.add(newGroupName);
            /**
             * 这里有个bug:
             * 当要修改的组名是新添加的但是没有添加到服务器端的，只是ui级添加的.应用会出错退出.
             * founder : leyufore
             */
            return;
        }
        // 列表修改;
        for (MRosterGroup g : rGroups) {
            if (g.getName().equals(old)) {
                g.setName(newGroupName);
            }
        }
        /**
         * 此处应该可以多加一行expandAdapter.setContacter(rGroups);
         * 这里不添加程序也会正常运行原因 : expandAdapter中的rGroups与该Activity中的rGroups是同一个对象,
         * 所以,改变这里的rGroups,同时也就是改变了expandAdapter中的rGroups,无需再设置.
         * 不过,为了视觉上逻辑性更好,添加这个
         * adviser : leyufore
         */
        expandAdapter.notifyDataSetChanged();

    }

    /**
     * 删除用户
     *
     * @param clickUser
     */
    private void showDeleteDialog(final User clickUser) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(
                /**
                 * 设置信息内容
                 */
                getResources().getString(R.string.delete_user_confim))
                /**
                 * setCancelable(false)的作用 : 使点击界面无法取消对话框
                 * leaner : leyufore
                 */
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                // ui删除
                                deleteUserUI(clickUser);
                                // api删除
                                try {
                                    removeSubscriber(clickUser.getJID());
                                } catch (XMPPException e) {
                                    Log.e(TAG, "", e);
                                }
                                // 删除数据库
                                NoticeManager.getInstance(context)
                                        .delNoticeHisWithSb(clickUser.getJID());
                                MessageManager.getInstance(context)
                                        .delChatHisWithSb(clickUser.getJID());
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    @Override
    protected void handReConnect(boolean isSuccess) {
        // 成功了连接
        if (Constant.RECONNECT_STATE_SUCCESS == isSuccess) {
            iv_status.setImageDrawable(getResources().getDrawable(
                    R.drawable.status_online));

        } else if (Constant.RECONNECT_STATE_FAIL == isSuccess) {// 失败
            iv_status.setImageDrawable(getResources().getDrawable(
                    R.drawable.status_offline));
        }
    }

}