package personal.basedxmppchat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import personal.basedxmppchat.R;
import personal.basedxmppchat.activity.im.ContacterMainActivity;
import personal.basedxmppchat.activity.im.UserInfoActivity;
import personal.basedxmppchat.activity.notice.MyNoticeActivity;
import personal.basedxmppchat.comm.Constant;
import personal.basedxmppchat.manager.UserManager;
import personal.basedxmppchat.model.LoginConfig;
import personal.basedxmppchat.model.MainPageItem;
import personal.basedxmppchat.util.StringUtil;
import personal.basedxmppchat.view.MainPageAdapter;

public class MainActivity extends ActivitySupport {

    private GridView gridview;
    private List<MainPageItem> list;
    private MainPageAdapter adapter;
    private ImageView iv_status;
    private ContacterReceiver receiver;
    private TextView usernameView;
    private UserManager userManager;
    private LoginConfig loginConfig;
    private ImageView userimageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        init();
    }

    @Override
    protected void onPause() {
        // 卸载广播接收器
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        //  注册广播接收器
        IntentFilter filter = new IntentFilter();
        //  好友请求
        filter.addAction(Constant.ROSTER_SUBSCRIPTION);
        filter.addAction(Constant.NEW_MESSAGE_ACTION);
        filter.addAction(Constant.ACTION_SYS_MSG);

        filter.addAction(Constant.ACTION_RECONNECT_STATE);
        registerReceiver(receiver,filter);

        if(getUserOnlineState()){
            iv_status.setImageDrawable(getResources().getDrawable(R.drawable.status_online));
        }else{
            iv_status.setImageDrawable(getResources().getDrawable(R.drawable.status_offline));
        }
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){    // resultCode为回传的标记
            case 1:
                setUserView();
                break;
            default:
                break;
        }
    }

    private void setUserView(){
        String jid = StringUtil.getJidByName(loginConfig.getUsername(),loginConfig.getXmppServiceName());
        VCard vCard = userManager.getUserVCard(jid);
        InputStream is = userManager.getUserImage(jid);
        if (is != null) {
            Bitmap bm = BitmapFactory.decodeStream(is);
            userimageView.setImageBitmap(bm);
        }
        if (vCard.getFirstName() != null) {
            usernameView.setText(vCard.getFirstName()
                    + (StringUtil.notEmpty(vCard.getOrganization()) ? " - "
                    + vCard.getOrganization() : ""));
        } else {
            usernameView.setText(loginConfig.getUsername()
                    + (StringUtil.notEmpty(vCard.getOrganization()) ? " - "
                    + vCard.getOrganization() : ""));
        }
    }


    private void init() {
        userManager = UserManager.getInstance(this);
        loginConfig = getLoginConfig();
        gridview = (GridView) findViewById(R.id.gridview);
        iv_status = (ImageView) findViewById(R.id.iv_status);
        userimageView = (ImageView) findViewById(R.id.userimage);
        usernameView = (TextView) findViewById(R.id.username);
        setUserView();
        userimageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, UserInfoActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        // 初始化广播
        receiver = new ContacterReceiver();

        loadMenuList();
        adapter = new MainPageAdapter(this);
        adapter.setList(list);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final Intent intent = new Intent();
                switch (position) {
                    case 0:// 我的联系人
                        intent.setClass(context, ContacterMainActivity.class);
                        startActivity(intent);
                        break;
                    case 1:// 我的消息
                        intent.setClass(context, MyNoticeActivity.class);
                        startActivity(intent);
                        break;
                    case 2:// 企业通讯录
                        break;
                    case 3:// 个人通讯录
                        break;
                    case 4:// 我的邮件
                        break;
                    case 5:// 网络收藏夹
                        break;
                    case 6:// 个人文件夹
                        break;
                }
            }
        });
    }

    /**
     *
     * 加载菜单.
     *
     * @author shimiso
     * @update 2012-5-16 下午7:15:21
     */
    protected void loadMenuList() {
        list = new ArrayList<MainPageItem>();
        list.add(new MainPageItem("我的联系人", R.drawable.mycontacts));
        list.add(new MainPageItem("我的消息", R.drawable.mynotice));
        list.add(new MainPageItem("企业通讯录", R.drawable.e_contact));
        list.add(new MainPageItem("个人通讯录", R.drawable.p_contact));
        list.add(new MainPageItem("邮件", R.drawable.email));
        list.add(new MainPageItem("单点登录", R.drawable.sso));
        list.add(new MainPageItem("个人文件夹", R.drawable.p_folder));
        list.add(new MainPageItem("我的笔记", R.drawable.mynote));
        list.add(new MainPageItem("我的签到", R.drawable.signin));
        list.add(new MainPageItem("我的工作日报", R.drawable.mydaily));
        list.add(new MainPageItem("我的日程", R.drawable.mymemo));
        list.add(new MainPageItem("设置", R.drawable.set));
    }

    /**
     * 在进入了联系人,系统消息的界面后,可能会阅读了相关信息,导致退回到该界面时没有未读信息的提示
     * 因此,在onRestart()阶段重新刷新视图
     */
    @Override
    protected void onRestart() {
        adapter.notifyDataSetChanged();
        super.onRestart();
    }

    /**
     * 设计思路:
     * A.每个activity实现一个Receiver,用于当出现相关事件时,接收相应的广播信息,以便做出相应反应.
     * B.该应用所需的所有服务 -- 联系人服务,聊天服务,系统消息服务,重连接服务,都是对connection进行监听
     * ,在有消息到来时,会发出一个相应广播.
     *
     * 例如:该主界面中,注册了一个Receiver.
     * 功能:
     * 1.有系统消息到来时.(来自系统消息服务)
     * 2.有聊天信息到来时.(来自聊天服务)
     * 3.有好友添加消息到来时.(来自联系人服务)
     * 当这三种广播出现时,该Receiver会在界面图标上显示信息的数量.利用adapter的notifyDataSetChanged()方法,
     * 刷新gridView中的视图
     */
    private class ContacterReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constant.ROSTER_SUBSCRIPTION.equals(action)) {
                //数据改变时通知相应View刷新视图
                //调用该方法后,adapter会重新调用其中的getView,然后重新获取数据,实现数据库数据更新(在Service中更新)同时更新界面显示
                adapter.notifyDataSetChanged();
            } else if (Constant.NEW_MESSAGE_ACTION.equals(action)) {
                // 添加小气泡
                adapter.notifyDataSetChanged();
            } else if (Constant.ACTION_RECONNECT_STATE.equals(action)) {
                boolean isSuccess = intent.getBooleanExtra(
                        Constant.RECONNECT_STATE, false);
                handReConnect(isSuccess);
            } else if (Constant.ACTION_SYS_MSG.equals(action)) {
                adapter.notifyDataSetChanged();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_page_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.menu_relogin:
                intent.setClass(context, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.menu_exit:
                isExit();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        isExit();
    }

    /**
     * 处理冲连接返回状态，连接成功 改变头像 ，失败
     *
     * @param isSuccess
     */
    private void handReConnect(boolean isSuccess) {
        // 成功了连接
        if (Constant.RECONNECT_STATE_SUCCESS == isSuccess) {
            iv_status.setImageDrawable(getResources().getDrawable(
                    R.drawable.status_online));
            // Toast.makeText(context, "网络恢复,用户已上线!", Toast.LENGTH_LONG).show();
        } else if (Constant.RECONNECT_STATE_FAIL == isSuccess) {// 失败
            iv_status.setImageDrawable(getResources().getDrawable(
                    R.drawable.status_offline));
            // Toast.makeText(context, "网络断开,用户已离线!", Toast.LENGTH_LONG).show();
        }

    }
}
