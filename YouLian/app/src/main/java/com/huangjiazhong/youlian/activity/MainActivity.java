package com.huangjiazhong.youlian.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huangjiazhong.youlian.R;
import com.huangjiazhong.youlian.adapter.MyPagerAdapter;
import com.huangjiazhong.youlian.entity.XmppUser;
import com.huangjiazhong.youlian.fragment.ContactFragment;
import com.huangjiazhong.youlian.fragment.JokeFragment;
import com.huangjiazhong.youlian.fragment.SessionFragment;
import com.huangjiazhong.youlian.service.IMService;
import com.huangjiazhong.youlian.utils.FormatUtils;
import com.huangjiazhong.youlian.utils.ThreadUtils;
import com.huangjiazhong.youlian.utils.ToastUtils;
import com.huangjiazhong.youlian.utils.ToolBarUtils;
import com.huangjiazhong.youlian.view.CustomShapeImageView;
import com.huangjiazhong.youlian.xmpp.XmppAdmin;
import com.huangjiazhong.youlian.xmpp.XmppConnection;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.huangjiazhong.youlian.xmpp.XmppConnection.SERVICENAME;

public class MainActivity extends AppCompatActivity implements JokeFragment.OnFragmentInteractionListener{
    @InjectView(R.id.vp_content)
    ViewPager mVpContent;
    @InjectView(R.id.ll_mainbottom)
    LinearLayout mLlMainbottom;
    @InjectView(R.id.main_drawer)
    LinearLayout mMainDrawer;
    @InjectView(R.id.left_drawer)
    LinearLayout mLeftDrawer;

    @InjectView(R.id.ibtn_drawer)
    ImageButton mIbtnDrawer;
    @InjectView(R.id.et_search_friend)
    EditText mEtSearchFriend;
    @InjectView(R.id.btn_add_friend)
    Button mBtnAddFriend;
    @InjectView(R.id.ibtn_share)
    ImageButton mIbtnShare;
    @InjectView(R.id.my_avatar)
    CustomShapeImageView mMyAvatar;
    @InjectView(R.id.tv_nickname)
    TextView mTvNickname;
    @InjectView(R.id.tv_username)
    TextView mTvUsername;
    @InjectView(R.id.tv_online)
    TextView mTvOnline;
    @InjectView(R.id.iv_on)
    ImageView mIvOn;
    @InjectView(R.id.tv_offline)
    TextView mTvOffline;
    @InjectView(R.id.iv_off)
    ImageView mIvOff;
    @InjectView(R.id.tv_chage_password)
    TextView mTvChagePassword;
    @InjectView(R.id.tv_about)
    TextView mTvAbout;
    @InjectView(R.id.drawerLayout)
    DrawerLayout mDrawerLayout;
    @InjectView(R.id.rl_on)
    RelativeLayout mRlOn;
    @InjectView(R.id.rl_off)
    RelativeLayout mRlOff;
    @InjectView(R.id.btn_switch_account)
    Button mBtnSwitchAccount;

    private static int RESULT_LOAD_IMAGE = 1;
    private static int RESULT_SET_PWD = 2;
    @InjectView(R.id.tv_message_remind)
    TextView mTvMessageRemind;
    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private ToolBarUtils mToolBarUtils;
    private int[] mIconArr;
    private String[] mToolBarTitleArr;
    private SharedPreferences mSp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);//注解方式初始化view
        mSp = getSharedPreferences("YOULIAN", MODE_PRIVATE);
        initData();
        initListener();
        initDrawData();
        initDrawItemLiestener();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mFragments.add(new SessionFragment());
        mFragments.add(new ContactFragment());
        mFragments.add(new JokeFragment());
        mVpContent.setAdapter(new MyPagerAdapter(getSupportFragmentManager(),mFragments));// 设置适配器
        mVpContent.setOffscreenPageLimit(3);//默认三个页面   让数据不被销毁
        //底部按钮设置
        mToolBarUtils = new ToolBarUtils();
        mToolBarTitleArr = new String[]{"消息", "朋友仔", "发现"};
        mIconArr = new int[]{R.drawable.selector_message, R.drawable.selector_contact, R.drawable.selector_timeline};
        mToolBarUtils.createToolBar(mLlMainbottom, mToolBarTitleArr, mIconArr);

        // 设置默认选中会话
        mToolBarUtils.changeColor(0);
    }

    /**
     * =============== 初始化抽屉数据 ===============
     */


    private void initDrawData() {
        String username = IMService.mCurAccout.substring(0, IMService.mCurAccout.indexOf("@"));
        Drawable myAvatar = XmppAdmin.getUserImage(username + "@" + SERVICENAME);
        if (myAvatar != null) {
            mMyAvatar.setImageDrawable(myAvatar);
        }else {
            mMyAvatar.setImageResource(R.mipmap.login);
        }
        mTvUsername.setText(username);
        XmppUser xmppUser = XmppAdmin.searchUser(username);
        String nickName = xmppUser.getNickName();
        if(!TextUtils.isEmpty(nickName)){
            mTvNickname.setText(nickName);
        }else {
            mTvNickname.setText(username);
        }

    }

    /**
     * 初始化事件
     */
    private void initListener() {
        mVpContent.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //隐藏搜索框
                mEtSearchFriend.setVisibility(View.INVISIBLE);
                mBtnAddFriend.setText("添加好友");
                mEtSearchFriend.setText("");
            }

            @Override
            public void onPageSelected(int position) {
                //隐藏搜索框
                mEtSearchFriend.setVisibility(View.INVISIBLE);
                mBtnAddFriend.setText("添加好友");
                mEtSearchFriend.setText("");
                /**
                 * viewpager滑动时改变按钮颜色和文字
                 */
                mToolBarUtils.changeColor(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //隐藏搜索框
                mEtSearchFriend.setVisibility(View.INVISIBLE);
                mBtnAddFriend.setText("添加好友");
                mEtSearchFriend.setText("");

            }
        });
        /**
         * 底部按钮事件监听,viewPager跟随按钮的改变而改变
         */
        mToolBarUtils.setOnToolBarClickListener(new ToolBarUtils.OnToolBarClickListener() {
            @Override
            public void OnToolBarClick(int position) {
                mVpContent.setCurrentItem(position);
            }
        });
        /**=============== headMenu begin ===============*/
        mIbtnDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //隐藏搜索框
                mEtSearchFriend.setVisibility(View.INVISIBLE);
                mBtnAddFriend.setText("添加好友");
                mEtSearchFriend.setText("");
                mDrawerLayout.openDrawer(Gravity.LEFT);//  打开左面抽屉
            }
        });
        mBtnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEtSearchFriend.getVisibility() == View.INVISIBLE) {
                    mEtSearchFriend.setVisibility(View.VISIBLE);
                    mBtnAddFriend.setText("搜索");
                } else {
                    final String userName = mEtSearchFriend.getText().toString().trim();
                    if(IMService.mCurAccout.equals(userName + "@" + SERVICENAME)){
                        mEtSearchFriend.setText("");
                        Toast.makeText(MainActivity.this, "不能添加自己", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ThreadUtils.runInThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable image = XmppAdmin.getUserImage(userName + "@" + SERVICENAME);
                            byte[] imgs = null;
                            if(image!=null){
                                imgs = FormatUtils.getInstance().Drawable2Bytes(image);
                            }
                            XmppUser user = XmppAdmin.searchUser(userName);
                            if (user != null) {
                                Intent intent = new Intent(MainActivity.this, FriendDetailActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("flag", "add");
                                bundle.putString("userName", user.getUserName());
                                bundle.putString("nickName", user.getNickName());
                                bundle.putByteArray("avatar",imgs);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            } else {
                                ToastUtils.showToastSafe(MainActivity.this, "该用户不存在");
                            }
                        }
                    });
                    mEtSearchFriend.setVisibility(View.INVISIBLE);
                    mBtnAddFriend.setText("添加好友");
                    mEtSearchFriend.setFocusable(true);
                    mEtSearchFriend.setText("");
                }

            }
        });
        /**=============== 分享到其他社交平台 ===============*/
        mIbtnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //隐藏搜索框
                mEtSearchFriend.setVisibility(View.INVISIBLE);
                mBtnAddFriend.setText("添加好友");
                mEtSearchFriend.setText("");
                ToastUtils.showToastSafe(MainActivity.this,"此功能尚未完成");
            }
        });
        /**=============== headMenu end ===============*/
    }

    /**
     * =============== 抽屉组件实践监听 ===============
     */
    public void jinzhi(View view) {}//解解bug用的方法
    private void initDrawItemLiestener() {
        mMyAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_PICK,
//                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(intent, RESULT_LOAD_IMAGE);
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "选择图片"), RESULT_LOAD_IMAGE);
            }
        });

        mRlOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIvOff.setVisibility(View.GONE);
                mIvOn.setVisibility(View.VISIBLE);
                mSp.edit().putInt("state", 0).commit();
                int state = mSp.getInt("state", 0);
            }
        });

        mRlOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIvOff.setVisibility(View.VISIBLE);
                mIvOn.setVisibility(View.GONE);
                mSp.edit().putInt("state", 1).commit();
                int state = mSp.getInt("state", 0);
            }
        });
        /**=============== 消息提醒 ===============*/
        mTvMessageRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,MessageRemindActivity.class));
            }
        });
        /**=============== 修改密码 ===============*/
        mTvChagePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SetPwdActivity.class);
                startActivityForResult(intent, RESULT_SET_PWD);
            }
        });
        mTvAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,AboutActivity.class));
            }
        });
        //切换账号
        mBtnSwitchAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSp.edit().putBoolean("auto_ischeck", false).commit();//设置为手动登录
                XmppConnection.closeConnection();//关闭连接
                startActivity(new Intent(MainActivity.this, SplashActivity.class));
                finish();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_SET_PWD && resultCode == RESULT_OK) {
            XmppConnection.closeConnection();//关闭连接
            startActivity(new Intent(MainActivity.this, SplashActivity.class));
            finish();
        }
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri uri = data.getData();
            String picturePath = null;
            if(!TextUtils.isEmpty(uri.getAuthority())) {
            Cursor cursor = getContentResolver().query(uri,new String[] { MediaStore.Images.Media.DATA },null, null, null);
                if(cursor == null){
                    ToastUtils.showToastSafe(MainActivity.this,"没有获取到数据");
                    return;
                }
                cursor.moveToFirst();
                //int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                cursor.close();
            }else {
                picturePath = uri.getPath();
            }
            Bitmap bitmap = pictureTool(picturePath);
            String username = IMService.mCurAccout.substring(0, IMService.mCurAccout.indexOf("@"));
            boolean b = XmppAdmin.changeImage(bitmap, username + "@" + SERVICENAME);
            if (b) {
                mMyAvatar.setImageBitmap(bitmap);
            }
        }
    }
    /**=============== 解决大图加载导致内存溢出的方法 ===============*/
    public Bitmap pictureTool(String picPath){
        //解析图片时需要使用到的参数都封装在这个对象里了
        BitmapFactory.Options options = new BitmapFactory.Options();
        //不为像素申请内存，只获取图片宽高
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picPath, options);
        //拿到图片宽高
        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;

        Display dp = getWindowManager().getDefaultDisplay();
        //拿到屏幕宽高
        int screenWidth = dp.getWidth();
        int screenHeight = dp.getHeight();

        //计算缩放比例
        int scale = 4;
        int scaleWidth = imageWidth / screenWidth;
        int scaleHeight = imageHeight / screenHeight;
        if(scaleWidth >= scaleHeight && scaleWidth >= 1){
            scale = 4*scaleWidth;
        }
        else if(scaleWidth < scaleHeight && scaleHeight >= 1){
            scale = 4*scaleHeight;
        }
        //设置缩放比例
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeFile(picPath, options);
        return bm;
    }
    @Override
    public void onFragmentInteraction(Uri uri) {}
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
