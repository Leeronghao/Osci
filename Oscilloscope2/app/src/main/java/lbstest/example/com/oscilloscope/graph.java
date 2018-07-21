package lbstest.example.com.oscilloscope;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * Created by SUER on 2018/5/7.
 */

public class graph extends View {
    private Paint paintCoor;
    private Paint paintScope;
    private int width;
    private int height;
    private String data;
    private int data_Vlotage[] = new int[128];
    private Boolean initialized = false;
    private Boolean drawScope = false;
    private int data_len;
    public int getData_len() {
        return data_len;
    }
    public void setData_len(int data_len) {
        this.data_len = data_len;
    }
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        drawScope = true;
    }

    public graph(Context context) {
        super(context);
    }

    public graph(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialized = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (initialized) {
            init(canvas);
        }if (drawScope){
            DrawScope(canvas);
        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
         width = MeasureSpec.getSize(widthMeasureSpec);
         height= MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width,height);
    }
    public void handdata(){
        char vlotage_char[] = data.toCharArray();
        for (int i=0;i<vlotage_char.length;i++){
            data_Vlotage [i] = Integer.valueOf(vlotage_char[i]);
            Log.d("graph.class", String.valueOf(data_Vlotage[i]));
        }drawScope = true;
    }
    public void handleData(){
        char vlotage_char[] = data.toCharArray();
        char[][] vlotage = new char[128][3];
        int index = 0;
        for (int i=0;i<128;i++){    //转换为字符串：从字符串中分隔电压值
            for (int j=0;j<3;j++){
                vlotage[i][j] = vlotage_char[index++];   //将电压值字符串转换为整形
            }
            index += 3;                    //跳过 \r\n
        }
        for (int k=0;k<128;k++){
            String temp = String.valueOf(vlotage[k]);
            data_Vlotage[k] = Integer.valueOf(temp);
            drawScope = true;
        }
    }
    public void DrawScope(Canvas canvas){
        int startx=50;
        int stopy = 0;
        int starty =50;
        int stopx  =0;
        CornerPathEffect cornerPathEffect = new CornerPathEffect(60);
        paintScope.setPathEffect(cornerPathEffect);
        Path path = new Path();
        path.moveTo(0,data_Vlotage[1]*3);
        for (int i=1;i<128;i++){
            stopx = stopx+4;
            stopy = data_Vlotage[i]*3;
            path.lineTo(stopx,stopy);
        }
        Log.d("graph.class", "DrawScope:++++++++++++++++++++++++++++++++++++++++ ");
        canvas.drawPath(path,paintScope);
      //  drawScope = false;
    }
    public void init(Canvas canvas){
        paintScope = new Paint();
        paintScope.setStyle(Paint.Style.STROKE);
        paintScope.setStrokeWidth(8);
        paintScope.setAntiAlias(true);
        paintScope.setColor(Color.YELLOW);
        paintCoor = new Paint();
        paintCoor.setStyle(Paint.Style.FILL);
        paintCoor.setStrokeWidth(3);
        paintCoor.setAntiAlias(true);
        paintCoor.setColor(Color.GRAY);
        canvas.drawLine(0,0,width,0,paintCoor);
        canvas.drawLine(0,0,0,height,paintCoor);
        for (int i=0;i<21;i++){
            canvas.drawLine(i*100,0,100*i,height,paintCoor);
        }
        for (int j=0;j<=9;j++){
            if (j==4){
                Paint paintRed = new Paint();
                paintRed.setStrokeWidth(3);
                paintRed.setStyle(Paint.Style.FILL);
                paintRed.setColor(Color.RED);
                canvas.drawLine(0,100*j,width,100*j,paintRed);
                continue;
            }
            canvas.drawLine(0,100*j,width,100*j,paintCoor);
       }
       canvas.save();
    }
}
