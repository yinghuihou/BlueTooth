package com.iswitch.iswitch;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.iswitch.ui.SwitchButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends Activity implements OnClickListener {
    private Button kaiDianJi, guanDianJi, safeOn;
    private Button start, stop, reset, resetAll, jiTing;
    private Button heightAdd, heightSub, buChangAdd, buChangSub, speedAdd, speedSub, yuShengSong, yuShengJin;
    private Button zhiNengXueXi, ziDongTiChui, fangShangTianLun, zhiNengPaiSheng, jinChiJiaSu;

    private Button duanKai;
    //+++++++++++++++++++++
    private ImageView shache_jia;
    private ImageView shache_jian, lihe_jia, lihe_jian;
    //+++++++++++++++++++++
    private Switch aSwitch;
    // private Switch duanshuangda;
    public final static int SENDPOS = 100;
    public final static String FNAME = "FNAME";
    private final static int REQUEST_CONNECT_DEVICE = 1; // 宏定义查询设备句柄
    private final static int REQUEST_DATA = 2;
    private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB"; // SPP服务UUID号
    protected static final int REQUEST_ENABLE = 0;

    private InputStream is; // 输入流，用来接收蓝牙数据

    private TextView dis; // 接收数据显示句柄
    private ScrollView sv; // 翻页句柄
    private String smsg = ""; // 显示用数据缓存
    int MAX = 4096;
    byte[] all = new byte[MAX];
    int allPos = 0;


    BluetoothDevice _device = null; // 蓝牙设备
    BluetoothSocket _socket = null; // 蓝牙通信socket
    boolean _discoveryFinished = false;
    boolean bRun = true;
    boolean bThread = false;
    boolean hex = false;

    private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter(); // 获取本地蓝牙适配器，即蓝牙设备
    private ImageView btnadd;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    //+++++++++++++++++++++
    private TextView height_value;
    private TextView rope_value;
    private TextView speed_value;//速度
    private TextView shache_qiya;//刹车气压
    private TextView lihe_qiya;//离合气压
    private TextView tv_A;//大电流
    private TextView tv_V;//总气压
    private ImageView imageView;//刹车加
    private ImageView imageView2;
    private ImageView imageView3;
    private ImageView imageView4;
    private SwitchButton suo;
    private ImageView icon_suo;


    private boolean dianjikai;

    //+++++++++++++++++++++++
    public void initView() {
        duanKai = (Button) findViewById(R.id.duan_kai);
        duanKai.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                disconnect();
            }
        });
        suo = findViewById(R.id.suo);
        icon_suo = findViewById(R.id.suo_gaun);
        suo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    icon_suo.setImageResource(R.drawable.suokai);
                }else {
                    icon_suo.setImageResource(R.drawable.suoguan);
                }
            }
        });

        // kaiDianJi = (Button) findViewById(R.id.kai);
        // kaiDianJi.setOnClickListener(this);

        // kaiDianJi = (Button) findViewById(R.id.kai);
        // kaiDianJi.setOnClickListener(this);
        //++++++++++++++
        height_value = (TextView) findViewById(R.id.height_value);
        rope_value = (TextView) findViewById(R.id.rope_value);
        speed_value = (TextView) findViewById(R.id.speed_value);
        shache_qiya = (TextView) findViewById(R.id.shache_qiya);
        lihe_qiya = (TextView) findViewById(R.id.lihe_qiya);
        tv_A = (TextView) findViewById(R.id.tv_A);
        tv_V = (TextView) findViewById(R.id.tv_V);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        imageView3 = (ImageView) findViewById(R.id.imageView3);
        imageView4 = (ImageView) findViewById(R.id.imageView4);
        //+++++++++++++
        // guanDianJi = (Button) findViewById(R.id.guan);
        // guanDianJi.setOnClickListener(this);

        // safeOn =  (Button) findViewById(R.id.jingyan_qidong);
        // safeOn.setOnClickListener(this);

        start = (Button) findViewById(R.id.qidong);
        start.setOnClickListener(this);

        stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(this);
        //reset = (Button) findViewById(R.id.reset);
        //reset.setOnClickListener(this);

        // resetAll = (Button) findViewById(R.id.reset_all);
        // resetAll.setOnClickListener(this);

        jiTing = (Button) findViewById(R.id.emergency_stop);
        jiTing.setOnClickListener(this);

        heightAdd = (Button) findViewById(R.id.height_add);
        heightAdd.setOnClickListener(this);

        heightSub = (Button) findViewById(R.id.height_sub);
        heightSub.setOnClickListener(this);

        //  buChangAdd = (Button) findViewById(R.id.compensate_add);
        // buChangAdd.setOnClickListener(this);

        //  buChangSub = (Button) findViewById(R.id.compensate_sub);
        //  buChangSub.setOnClickListener(this);

        speedAdd = (Button) findViewById(R.id.speed_add);
        speedAdd.setOnClickListener(this);

        speedSub = (Button) findViewById(R.id.speed_sub);
        speedSub.setOnClickListener(this);

        yuShengSong = (Button) findViewById(R.id.rope_add);
        yuShengSong.setOnClickListener(this);

        yuShengJin = (Button) findViewById(R.id.rope_sub);
        yuShengJin.setOnClickListener(this);

        zhiNengXueXi = (Button) findViewById(R.id.zhixue);
        zhiNengXueXi.setOnClickListener(this);

        ziDongTiChui = (Button) findViewById(R.id.zidong);
        ziDongTiChui.setOnClickListener(this);

        // fangShangTianLun = (Button) findViewById(R.id.fang_shang_tian_lun);
        // fangShangTianLun.setOnClickListener(this);

        // zhiNengPaiSheng = (Button) findViewById(R.id.zhi_neng_pai_sheng);
        // zhiNengPaiSheng.setOnClickListener(this);

        //jinChiJiaSu = (Button) findViewById(R.id.jin_chi_jia_su);
        //jinChiJiaSu.setOnClickListener(this);
        //++++++++++++++++++
        shache_jia = (ImageView) findViewById(R.id.imageView);
        shache_jia.setOnClickListener(this);
        shache_jian = (ImageView) findViewById(R.id.imageView2);
        shache_jian.setOnClickListener(this);
        lihe_jia = (ImageView) findViewById(R.id.imageView3);
        lihe_jia.setOnClickListener(this);
        lihe_jian = (ImageView) findViewById(R.id.imageView4);
        lihe_jian.setOnClickListener(this);

        // aSwitch = (Switch) findViewById(R.id.switch1);
        //  aSwitch.setOnClickListener(this);
        //++++++++++++++++++


        //关于选项按钮触发事件
//        Button usetButton = (Button) findViewById(R.id.use);
//        usetButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(MainActivity.this,
//                        UseActivity.class);
//                //startActivity(intent);
//                startActivityForResult(intent, REQUEST_DATA);
//            }
//
//        });


//        Button exitButton = (Button) findViewById(R.id.exit);
//        exitButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                finish();
//            }
//        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //case R.id.kai:
            //开电机

            //  sendString("10");

            //  break;
            // case R.id.guan:
            //关电机
            //   sendString("11");
            //   break;

            case R.id.qidong:
                if (dianjikai) {
                    //停止
                    sendString("08");
                    dianjikai = false;
                } else {
                    //启动
                    sendString("09");
                    dianjikai = true;
                }
                break;
            case R.id.stop:
                //停止
                sendString("12");
                break;
            // case R.id.reset:
            //复位

            //   break;

            case R.id.emergency_stop:

                //急停按钮
                break;
            case R.id.height_add:
                //高度加

                sendString("01");
                break;
            case R.id.height_sub:
                //高度减
                sendString("04");
                break;

            case R.id.speed_add:
                //速度加
                sendString("06");
                break;
            case R.id.speed_sub:
                //速度减
                sendString("07");
                break;
            case R.id.rope_add:
                //余绳松
                sendString("05");
                break;
            case R.id.rope_sub:
                //余绳紧
                sendString("02");
                break;
            case R.id.zhixue:
                //智能学习
                break;
            case R.id.zidong:
                //自动提锤
                break;

            case R.id.imageView:
                //刹车气压加
                sendString("41");
                break;
            case R.id.imageView2:
                //刹车气压减
                sendString("42");
                break;
            case R.id.imageView3:
                sendString("43");
                break;
            case R.id.imageView4:
                sendString("44");
                break;
            //   case R.id.switch2 :
            //单双打切换
            //      sendString("32");
            //      break;

            default:
                break;
        }
    }


    public void disconnect() {
        if (_socket != null) {
            //取消注册异常断开接收器
            this.unregisterReceiver(mReceiver);

            SharedPreferences.Editor sharedata = getSharedPreferences("Add", 0).edit();
            sharedata.clear();
            sharedata.commit();

            mPairedDevicesArrayAdapter.clear();
            Toast.makeText(this, "线路已断开，请重新连接！", Toast.LENGTH_SHORT).show();
            // 关闭连接socket
            try {
                bRun = false; // 一定要放在前面
                is.close();
                _socket.close();
                _socket = null;
                bRun = false;

                //btnadd.setText(getResources().getString(R.string.add));
            } catch (IOException e) {
            }
        }
    }

    public void connect() {
        if (_bluetooth.isEnabled() == false) { // 如果蓝牙服务不可用则提示
            //询问打开蓝牙
            Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enabler, REQUEST_ENABLE);
            return;
        }

        // 如未连接设备则打开DeviceListActivity进行设备搜索
        if (_socket == null) {
            mPairedDevicesArrayAdapter.clear();
            Intent serverIntent = new Intent(MainActivity.this,
                    DeviceListActivity.class); // 跳转程序设置
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE); // 设置返回宏定义
        }
        return;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this,
                R.layout.device_name);
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);


        dis = (TextView) findViewById(R.id.in); // 得到数据显示句柄
        sv = (ScrollView) findViewById(R.id.scroll_view);

        btnadd = (ImageView) findViewById(R.id.add);
        btnadd.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                connect();
            }
        });

        Button btnrec = (Button) findViewById(R.id.rec);
        btnrec.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                smsg = "";
                dis.setText(smsg); // 显示数据
            }
        });


        // 如果打开本地蓝牙设备不成功，提示信息，结束程序
        if (_bluetooth == null) {
            Toast.makeText(this, "本机没有找到蓝牙硬件或驱动！", Toast.LENGTH_LONG)
                    .show();
            finish();
            return;
        }
        if (_bluetooth.isEnabled() == false) { // 如果蓝牙服务不可用则提示
            Toast.makeText(MainActivity.this, " 打开蓝牙中...",
                    Toast.LENGTH_SHORT).show();

            new Thread() {
                public void run() {
                    if (_bluetooth.isEnabled() == false) {
                        _bluetooth.enable();
                    }
                }
            }.start();
        }
        if (_bluetooth.isEnabled() == false) {
            Toast.makeText(MainActivity.this, "等待蓝牙打开，5秒后，尝试连接！", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {   //延迟执行
                @Override
                public void run() {
                    if (_bluetooth.isEnabled() == false) {
                        Toast.makeText(MainActivity.this, "自动打开蓝牙失败，请手动打开蓝牙！", Toast.LENGTH_SHORT).show();
                        //询问打开蓝牙
                        Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enabler, REQUEST_ENABLE);
                    } else
                        connect(); //自动进入连接

                }
            }, 5000);

        } else {
            connect(); //自动进入连接
        }
    }


    public static byte[] hexStringToBytes(String hexString) {
        hexString = hexString.replaceAll(" ", ""); // 去空格
        if ((hexString == null) || (hexString.equals(""))) {
            return null;
        }
        hexString = hexString.toUpperCase(); // 字符串中的所有字母都被转化为大写字母
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; ++i) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[(pos + 1)]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    // 发送响应
    // 调用这个方法发送数据到单片机。
    public boolean sendString(String str) {
        if (_socket == null) {
            Toast.makeText(this, "未连接蓝牙", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (str == null) {
            Toast.makeText(this, "发送内容为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {

            OutputStream os = _socket.getOutputStream(); // 蓝牙连接输出流
            if (hex) {

                byte[] bos_hex = hexStringToBytes(str); // 十六进制
                os.write(bos_hex);
            } else {
                byte[] bos = str.getBytes("GB2312");    //native的Socket发送字节流默认是GB2312的，所以在Java方面需要指定GB2312
                os.write(bos);
            }

        } catch (IOException e) {
        }
        return true;
    }

    public boolean sendBytes(byte[] buffer) {
        if (_socket == null) {
            Toast.makeText(this, "未连接蓝牙", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (buffer == null) {
            Toast.makeText(this, "发送内容为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            OutputStream os = _socket.getOutputStream(); // 蓝牙连接输出流
            byte[] bos_hex = buffer;
            os.write(bos_hex);
        } catch (IOException e) {
        }
        return true;
    }


    // 关闭程序掉用处理部分
    public void onDestroy() {
        super.onDestroy();
        if (_socket != null) // 关闭连接socket
            try {
                _socket.close();
            } catch (IOException e) {
            }

        _bluetooth.disable(); //关闭蓝牙服务

        //  android.os.Process.killProcess(android.os.Process.myPid()); // 终止线程
    }

    // 接收活动结果，响应startActivityForResult()
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE: // 连接结果，由DeviceListActivity设置返回
                // 响应返回结果
                if (resultCode == Activity.RESULT_OK) { // 连接成功，由DeviceListActivity设置返回
                    // MAC地址，由DeviceListActivity设置返回
                    String address = data.getExtras().getString(
                            DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // 得到蓝牙设备句柄
                    _device = _bluetooth.getRemoteDevice(address);
                    // 用服务号得到socket
                    try {
                        _socket = _device.createRfcommSocketToServiceRecord(UUID
                                .fromString(MY_UUID));
                    } catch (IOException e) {

                        Toast.makeText(this, "连接失败,无法得到Socket！" + e, Toast.LENGTH_SHORT).show();

                    }


                    // 连接socket
                    try {
                        _socket.connect();

                        Toast.makeText(this, "连接" + _device.getName() + "成功！",
                                Toast.LENGTH_SHORT).show();
                        mPairedDevicesArrayAdapter.add(_device.getName() + "\n"
                                + _device.getAddress());
                        SharedPreferences.Editor sharedata = getSharedPreferences("Add", 0).edit();
                        sharedata.putString(String.valueOf(0), _device.getName());
                        sharedata.putString(String.valueOf(1), _device.getAddress());
                        sharedata.commit();


                        //注册异常断开接收器  等连接成功后注册
                        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
                        this.registerReceiver(mReceiver, filter);

                    } catch (IOException e) {

                        try {
                            Toast.makeText(this, "连接失败！" + e, Toast.LENGTH_SHORT)
                                    .show();
                            _socket.close();
                            _socket = null;
                        } catch (IOException ee) {
                        }
                        return;
                    }

                    // 打开接收线程
                    try {
                        is = _socket.getInputStream(); // 得到蓝牙数据输入流
                    } catch (IOException e) {
                        Toast.makeText(this, "异常：打开接收线程！" + e, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (bThread == false) {// 如果没有启动线程，就启动线程;否则开始接收
                        {
                            ReadThread.start();
                            bThread = true;

                        }
                    } else {
                        bRun = true;
                    }
                }
                break;
            case REQUEST_DATA:
                //Log.d("TAG", "REQUEST_DATA");
                //flushdata();

                //setbtn();
                break;
            default:
                break;
        }
    }

    // 接收数据线程
    Thread ReadThread = new Thread() {

        public void run() {
            int num = 0;
            byte[] buffer = new byte[1024];
            byte[] buffer_new = new byte[1024];


            bRun = true;
            // 接收线程
            while (true) {
                try {
                    while (is.available() == 0) {        //无接收数据
                        while (bRun == false) {   //线程阻塞
                        }
                    }
                    while (true) {
                        if (1 == is.read(buffer, 0, 1)) // 一个一个地接收，把需要的数据放在buffer_new中
                        {
                            //????<<
                            hex = true;
                            //????>>
                            if (hex) {
                                smsg += String.format("%02X ", buffer[0]);
                                //转为十六进制格式 所有接收
                                //????<<
                                all[allPos++] = buffer[0];
                                //????>>
                            } else {
                                if (127 < (buffer[0] & 0xff)) //解决汉字被截断
                                {
                                    buffer_new[num++] = buffer[0];
                                    if (num == 2) {
                                        smsg += new String(buffer_new, 0, 2, "GB2312");  //+String.format("(%02X %02X )",buffer_new[0],buffer_new[1]);
                                        // GB2312   GBK  UTF-8

                                        num = 0;
                                    }
                                } else
                                    smsg += new String(buffer, 0, 1, "GB2312");  //GB2312   GBK  UTF-8
                            }

                        }
                        /*
                        num = is.read(buffer); // 读入数据
                  smsg += new String(buffer, 0, num, "GB2312");  //GB2312   GBK  UTF-8
                  */
                        if (is.available() == 0)
                            break; // 短时间没有数据才跳出进行显示
                    }
                    // 发送显示消息，进行显示刷新
                    handler.sendMessage(handler.obtainMessage());
                } catch (IOException e) {
                }
            }
        }
    };

    // 消息处理队列
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            updateView();

            // parse data;
            parseData(all, allPos);
        }
    };

    private void updateView() {
        dis.setText(smsg); // 显示数据
        sv.scrollTo(0, dis.getMeasuredHeight()); // 跳至数据最后一页

        //判断收到来自单片机的数据时10或者11，来更改启动按钮的图片
        if (smsg.equals("10")){
            start.setBackgroundResource(R.drawable.dianjiqidong);
        }else if (smsg.equals("11")){
            start.setBackgroundResource(R.drawable.dianjiting);
        }

    }

    public void parseData(byte[] all, int allPos) {
        //

        byte[] src = fromAll(all, allPos);
        //disp2Mobile(src);
        this.all = new byte[MAX];
        allPos = 0;

        //-----------------------------------
        byte[] frame = from(src, src.length);
        if (frame == null) {
            height_value.setText("," + (UN_I++) % 10);
        }

        while (frame != null) {
            //output frame,display to mobile screen
            disp2Mobile(frame);
            break;
            //
            //frame = from(src,frame.length);
        }
        //
    }

    int UN_I = 0;

    public void disp2Mobile(byte[] src) {
        if (src == null) {
            //disp2Mobile(src);
            return;
        }
        // gaodu
        //byte a1 = 33;//src[0];
        float sheche = 0;
        float dadianliu = 0;
        float lihe = 0;
        float zongqiya = 0;
        int a1 = src[0];
        int a2 = src[1];
        int a3 = src[2];
        int a4 = src[3];
        int a5 = src[4];
        int a6 = src[5];
        int a7 = src[6];
        int a8 = src[7];
        int a9 = src[8];
        int a10 = src[9];
        int a11 = src[10];
        int a12 = src[11];
        if (a1 < 0) {
            a1 = a1 + 256;
        }

        if (a3 < 0) {
            a3 = a3 + 256;
        }
        if (a5 < 0) {
            a5 = a5 + 256;
        }
        if (a6 < 0) {
            a6 = a6 + 256;
        }
        if (a7 < 0) {
            a7 = a7 + 256;
        }
        if (a8 < 0) {
            a8 = a8 + 256;
        }
        if (a9 < 0) {
            a9 = a9 + 256;
        }
        if (a10 < 0) {
            a10 = a10 + 256;
        }
        if (a11 < 0) {
            a11 = a11 + 256;
        }
        if (a12 < 0) {
            a12 = a12 + 256;
        }
        dadianliu = a5 << 8 | a6;
        zongqiya = a7 << 8 | a8;
        sheche = a9 << 8 | a10;
        lihe = a11 << 8 | a12;
        sheche = sheche / 100;
        lihe = lihe / 100;
        zongqiya = zongqiya / 100;
        height_value.setText((int) a1 + "," + (UN_I++) % 10);
        rope_value.setText((int) a2 + "  ");
        speed_value.setText((int) a3 + "  ");
        tv_A.setText((float) dadianliu + "  ");
        tv_V.setText((float) zongqiya + "  ");
        shache_qiya.setText((float) sheche + "  ");
        lihe_qiya.setText((float) lihe + "  ");
        //
        //


    }

    // public  byte  pinjie(byte gao,byte di,int he){
    //     he = gao<<8 | di;
    //return he;
    //  }
    public byte[] fromAll(byte[] src, int end) {
        byte[] buffer = new byte[end];
        for (int i = 0; i < end; i++) {
            buffer[i] = src[i];
        }
        return buffer;
    }

    public byte[] from(byte[] src, int end) {
        byte[] head = new byte[2];
        head[0] = 0x1d;
        head[1] = 0x2d;

        //start
        int index1 = -1;
        int index2 = -1;
        for (int i = 0; i < end; i++) {
            if (src[i] == head[0]) {
                index1 = i;
            }
            if (index1 != -1 && src[i] == head[1] && i == index1 + 1) {
                index2 = i;
                break;
            }
        }
        //
        if (!(index1 != -1 && index2 != -1)) {
            return null;
        }

        //
        //end
        byte[] tail = new byte[2];
        tail[0] = 0x2e;
        tail[1] = 0x3e;
        int t1 = -1;
        int t2 = -1;
        for (int i = index2 + 1; i < end; i++) {
            if (src[i] == tail[0]) {
                t1 = i;
            }
            if (t1 != -1 && src[i] == tail[1] && i == t1 + 1) {
                t2 = i;
                break;
            }
        }
        //
        if (!(t1 != -1 && t2 != -1)) {
            return null;
        }

        //read data
        byte[] data = copyBuffer(src, index2, t1);

        //validatte

        //
        return data;
    }


    public byte[] copyBuffer(byte[] src, int start, int end) {
        byte[] buffer = new byte[end - start - 1];
        int j = 0;
        for (int i = start + 1; i < end; i++) {
            buffer[j++] = src[i];
        }
        return buffer;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {

                disconnect();
            }
        }
    };

}

