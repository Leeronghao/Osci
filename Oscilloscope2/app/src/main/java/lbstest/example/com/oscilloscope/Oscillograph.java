package lbstest.example.com.oscilloscope;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

public class Oscillograph extends AppCompatActivity {
    private  int len;
    private int port;
    private String address;
    private ConnectServer connectServer;
    private String data;
    private static final int UPDATE = 1;
    private mSurfaceview surfaceview;
    private Boolean connceted = false;
    private int sentivity = 60;
    private Switch aSwitch;
    private boolean Ischeckchange = false;
    private InputStream inputStream;
    private String staue;
    private  static final int PROGRESS_ON = 1;
    private  static final int PROGRESSO_OFF  = 0;
    private  static final int LINK_OFF = 11;
    private  static final int LINK_ON = 10;
    private  static final int GRAW_ON = 21;
    private  static final int OFF = 20;
    private ProgressBar progressBar;
    private  Switch conncet;
    private  Button ON;
    private TextView link_staue;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case PROGRESS_ON:progressBar.setVisibility(View.VISIBLE);link_staue.setText("监听");break;
                case PROGRESSO_OFF:progressBar.setVisibility(View.INVISIBLE);break;
                case LINK_ON:link_staue.setText("已连接");link_staue.setTextColor(Color.GREEN);break;
                case LINK_OFF:link_staue.setText("断开");link_staue.setTextColor(Color.RED);break;
                case GRAW_ON:ON.setText("开始");break;
                case OFF:ON.setText("暂停");break;
                default:break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
       // supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_oscillograph);
        progressBar =findViewById(R.id.progress);
        conncet = findViewById(R.id.connectSocket);
        link_staue = findViewById(R.id.statu);
        final TextView sen_num = findViewById(R.id.sensitive_number);
        surfaceview = findViewById(R.id.surfaceview);
        conncet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (link_staue.getText().toString().equals("断开")){
                                handler.sendMessage(handler.obtainMessage(PROGRESS_ON));
                                connectServer = new ConnectServer(null,8080,null);
                                staue="begin";
                                handler.sendMessage(handler.obtainMessage(LINK_ON));
                                surfaceview.setConnectServer(connectServer);
                                handler.sendMessage(handler.obtainMessage(PROGRESSO_OFF));
                            }
                            else if (link_staue.getText().toString().equals("已连接")){
                                staue="pause";
                                handler.sendMessage(handler.obtainMessage(LINK_OFF));
                                connectServer.close();
                                surfaceview.Draw_begin(staue);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
         ON = findViewById(R.id.ON);
        ON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (ON.getText().toString().equals("开始")){
                        surfaceview.Draw_begin("begin");
                      handler.sendMessage(  handler.obtainMessage(OFF));
                    }
                    if (ON.getText().toString().equals("暂停")){
                        surfaceview.Draw_begin("pause");
                       handler.sendMessage(handler.obtainMessage(GRAW_ON)) ;
                    }


            }
        });
        ImageButton add = findViewById(R.id.add_sensitive);
        aSwitch = findViewById(R.id.switch_bar);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               Ischeckchange = !Ischeckchange;
               surfaceview.setShowdata(Ischeckchange);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                               //时基本增加
                if (sentivity<80&&sentivity>=20){
                    sen_num.setText(String.valueOf((sentivity+20)/10)+"ms/div");
                    sentivity+=20;
                    surfaceview.setTime_sensitivity(sentivity);
                }
                else if (sentivity==80){
                    sen_num.setText(String.valueOf(sentivity*2/10)+"ms/div");
                    sentivity = sentivity*2;
                    surfaceview.setTime_sensitivity(sentivity);
                }
                else if (sentivity==160)
                    Toast.makeText(Oscillograph.this,"不能再加了！",Toast.LENGTH_SHORT).show();
            }
        });
        ImageButton dele = findViewById(R.id.delete_sensitive);
        dele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                             //时基减少
                if (sentivity<=80&&sentivity>20){
                    sen_num.setText(String.valueOf((sentivity-20)/10)+"ms/div");
                    sentivity-=20;
                    surfaceview.setTime_sensitivity(sentivity);
                }
                else  if (sentivity==160){
                    sen_num.setText(String.valueOf(sentivity/2/10)+"ms/div");
                    sentivity=sentivity/2;
                    surfaceview.setTime_sensitivity(sentivity);
                }
                else if (sentivity==20)
                    Toast.makeText(Oscillograph.this,"不能再减了！",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
