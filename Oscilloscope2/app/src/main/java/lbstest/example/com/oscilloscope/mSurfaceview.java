package lbstest.example.com.oscilloscope;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

/**
 * Created by SUER on 2018/5/12.
 */

public class mSurfaceview extends SurfaceView implements SurfaceHolder.Callback,Runnable {
    private Paint paintCoor;
    private Paint paintScope;
    private int freq_1=0;                          //用于频率计算
    private int freq_2=0;                          //用于频率计算
    private Boolean  square_wave = false;          //方形波，用于计算有效值
    private int square_num=0;                      //用于方波统计，连续5个电压值相等切大于0即判断为方波
    int frequency = 0;
    private Boolean mIsDrawing;
    private Canvas canvas;
    private SurfaceHolder mHolder;
    private Paint mpaint;
    private Path mpath;
    private Paint paintX;
    private Paint paintY;
    private static  final int FRAME = 50;
    private int oldx = 0;
    private int oldy;
    private ConnectServer connectServer;
    private Boolean connected = false;
    public Boolean connect = false;
    private int len;
    private int time=0;
    private int[] data = new int[1024];               //最大接收波形数据点数
    int x=3,y;
    int width;
    int heigth;
    private Boolean Pause = false;
    private int buff_length;
    private int time_sensitivity;                    //时间灵敏度
    private float Vpp_max;                            //电压最大值
    private float Vpp_min;                            //电压最小值
    private boolean Showdata = false;
    public volatile int pots_x;                            //时间轴，根据波形数，波形数越小，一个波形点所占的像素越大
    private  boolean select_sentivity = false;        //控制位，不能再while循环中修改数组的值
    public Paint paintdata;
    public void setShowdata(boolean showdata) {
        Showdata = showdata;
    }
    public int getGraph_num() {
        return graph_num;
    }
    public volatile int graph_num;                       //根据时间灵敏度指定波形个数

    public void setPots_x(int pots_x) {
        this.pots_x = pots_x;
    }
    public int getPots_x() {
        return pots_x;
    }
    public void setConnectServer(ConnectServer connectServer) {
        this.connectServer = connectServer;
    }
    public void setGraph_num(int graph_num) {
        this.graph_num = graph_num;
        switch (graph_num){
            case 1:setPots_x(8);break;
            case 2:setPots_x(5);break;
            case 3:setPots_x(3);break;
            case 4:setPots_x(2);break;
            case 8:setPots_x(1);break;
            default:break;
        }
    }
    public void setTime_sensitivity(int time_sensitivity) {
        select_sentivity = true;
        this.time_sensitivity = time_sensitivity;
        switch (time_sensitivity){
            case 20:setGraph_num(1);break;            //波形时基2ms/div下，绘制128个点，即一个波形
            case 40:setGraph_num(2);break;
            case 60:setGraph_num(3);break;
            case 80:setGraph_num(4);break;
            case 160:setGraph_num(8);break;
            default:break;
        }
        select_sentivity = false;
    }
    public void setPause(Boolean pause) {       //暂停波形绘制
        Pause = pause;
        try {
            connectServer.sendOrder("L");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private InputStream inputStream;
    public mSurfaceview(Context context) {
        super(context);
        initPaint();
    }

    public mSurfaceview(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mIsDrawing = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsDrawing  = false;
    }
    public void draw_coord(){                               //坐标函数
        canvas = mHolder.lockCanvas();
        canvas.drawColor(Color.BLACK);        //清屏操作，每次绘制指定波形，然后下一次绘制前将上一次绘制的波形清除
        canvas.drawLine(0,0,width-8,0,paintX);              //
        canvas.drawLine(width-8,0,width-8,heigth-8,paintX);//
        canvas.drawLine(0,heigth-8,width-8,heigth-8,paintX);//      图形边框
        canvas.drawLine(0,0,0,heigth-8,paintX);            //


        for (int i=0;i<10;i++){                         //横轴
            if (i==5){                                 //网格中心线
                paintX.setARGB(180,255,106,106);
                paintX.setStrokeWidth(6);
                paintX.setPathEffect(null);
                canvas.drawLine(0,100*i,width,100*i,paintX);
                paintX.setARGB(80,211,211,211);
                paintX.setStrokeWidth(3);
                paintX.setPathEffect(new DashPathEffect(new float[]{10,4},0));
            }
            else
                canvas.drawLine(0,99*i,width,99*i,paintX);       //网格横线
        }
        for (int j=0;j<11;j++) {
            canvas.drawLine(j * 96, 0, j * 96, heigth, paintX);   //网格竖线
        }
    }
    public void draw(){
        try {
           draw_coord();                                  //坐标绘制
          canvas.drawPath(mpath,mpaint);
//            if (freq_2!=0){
//                     double freq = 1/((float)(freq_1-freq_2)*0.156)*1000;
//                      frequency =  (int)freq;
//            }



            if (Showdata){

                paintX.setARGB(180,255,106,106);
                paintX.setStrokeWidth(6);
                paintX.setPathEffect(null);
                canvas.drawText("1.65V:",500,50,paintdata);
                canvas.drawLine(620,35,750,35,paintX);        //1.65V基准提示
                paintX.setARGB(80,211,211,211);
                paintX.setStrokeWidth(3);
                paintX.setPathEffect(new DashPathEffect(new float[]{10,4},0));
                paintdata.setTextSize(35);
                canvas.drawText("Amplitude:mV/div",780,50,paintdata);    //每一格表示电压数
                paintdata.setTextSize(40);

                Vpp_max = (float) Math.round(Vpp_max/256*3.3*100)/100;
                Vpp_min = (float) Math.round(Vpp_min/256*3.3*100)/100;
                canvas.drawText("Vmax: "+String.valueOf(Vpp_max)+" V",10,heigth-40,paintdata); //电压最大值
                canvas.drawText("Vmin: "+String.valueOf(Vpp_min)+" V",300,heigth-40,paintdata); //电压最小值
                canvas.drawText("CH:WiFi",10,50,paintdata);
                if (Vpp_max-Vpp_min<0.2){                                                   //直流电
                    BigDecimal b = new BigDecimal(Vpp_max-Vpp_min);
                    canvas.drawText("U:"+String.valueOf(Vpp_max)+"V",850,heigth-40,paintdata);//直流电有效值
                    canvas.drawText("Frequency:"+String.valueOf(0)+"Hz",200,50,paintdata);
                    canvas.drawText("Vp-p: "+"-/-",580,heigth-40,paintdata);//峰峰值
                }

                else if (Vpp_max-Vpp_min>=0.2){                                                            //交流电
                    canvas.drawText("U:"+String.valueOf((float)Math.round(Vpp_max*0.707*100)/100)+"V",850,heigth-40,paintdata); //交流电有效值
                    canvas.drawText("Frequency:"+String.valueOf(50)+"Hz",200,50,paintdata);     //交流电频率
                    canvas.drawText("Vp-p: "+String.valueOf((Vpp_max-Vpp_min)*100/100)+" V",580,heigth-40,paintdata);//峰峰值
                }
                else if (square_wave){
                    canvas.drawText("U:"+String.valueOf((float)Math.round(Vpp_max*0.5*100)/100)+"V",850,heigth-40,paintdata); //方波有效值
                    canvas.drawText("Frequency:"+String.valueOf(50)+"Hz",200,50,paintdata);     //方波频率
                    canvas.drawText("Vp-p: "+String.valueOf((Vpp_max-Vpp_min)*100/100)+" V",580,heigth-40,paintdata);//峰峰值
                }
            }
          //  Log.d("mSurfaceview.class", "draw: ");
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (canvas!=null){
                mHolder.unlockCanvasAndPost(canvas);
                mpath.reset();                            //重置绘图路径，
            }
        }
    }
    @Override
    public void run() {
        while (true) {
              if (connectServer!=null&&connect){
                  try {
                      inputStream = connectServer.getSocket().getInputStream();
                      connect = false;
                      connected = true;
                  } catch (IOException e) {
                      e.printStackTrace();
                  }
              }
                while (mIsDrawing && connected && Pause && select_sentivity == false) {
                    try {
                        buff_length = getGraph_num();
                        byte b[] = new byte[buff_length * 128];
                        len = inputStream.read(b);
                        while (len < this.buff_length * 128) {             //循环读取数据，直到读出128位为止，便于波形绘制
                            len += inputStream.read(b, len, buff_length* 128 - len);
                        }
                        for (int i = 0; i < buff_length * 128; i++) {       //将接收到的数据处理为整形
                            if (b[i] < 0)
                                data[i] = b[i] + 255;
                            else
                                data[i] = b[i];
                        }
                        oldy = 891 - data[0] * 3;            //波形的第一个数据点
                        Vpp_max = data[0];                    //暂取数据的第一个点为最大值和最小值
                        Vpp_min = data[0];                    //
                        mpath.moveTo(oldx, oldy);
                        for (int j = 0; j < buff_length* 128; j++) {     //处理波形127个点的数据，用path将数据点装入路径，便于在surfaceview中绘制出来
                            y = 891 - data[j] * 3;
                            x = x + pots_x;                      //绘图框宽为1070px
                            // Log.d("mSurfaceview.class", String.valueOf(y));
                            mpath.lineTo(x, y);
                            if (data[j]>Vpp_max){
                                Vpp_max = data[j];          //寻找峰值
                                if(j!=0&&data[j-1]==0)        //从零跳变至最大值，判断为方波
                                    square_wave = true;
                            }
                            else if (data[j]<Vpp_min)            //寻找最低值
                                Vpp_min  = data[j];
                        }
                        draw();
                        x = 0;
                        square_wave =false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                while (!Pause){
                        draw_coord();
                    mHolder.unlockCanvasAndPost(canvas);
                }
        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
         width = MeasureSpec.getSize(widthMeasureSpec);
         heigth = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(width,heigth);
    }
    public void Draw_begin(String staue) {
        if (staue.equals("begin")){
            mIsDrawing = true;
            Pause  = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        connectServer.sendOrder("ADCV");
                        connect = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        if (staue.equals("pause")){

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            connectServer.sendOrder("L");
                            mIsDrawing =false;
                            connected =false;
                            Pause = false;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
        }
    }
    public void initPaint(){
        mHolder = getHolder();
        mHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);
        mpath = new Path();
        mpaint = new Paint();
        mpaint.setStyle(Paint.Style.STROKE);
        mpaint.setARGB(255,160,32,240);
        mpaint.setAntiAlias(true);
        mpaint.setStrokeWidth(10);
        paintX = new Paint();                         //坐标轴画笔
        paintX.setARGB(80,211,211,211);
        paintX.setStrokeWidth(3);
        paintX.setStyle(Paint.Style.FILL);
        PathEffect pathEffect = new DashPathEffect(new float[]{10,4},0);       //坐标轴画虚线
        paintX.setPathEffect(pathEffect);
        paintdata = new Paint();
        paintdata.setARGB(255,218,165,32);
        paintdata.setTextSize(40);
        setGraph_num(3);
    }
}
