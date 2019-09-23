package com.sakec.chembur.sakecvotes.CustomViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

public class Circular extends View {

    public class Arc{

        private float thetha;
        private float start = 0f;

        public Arc(float thetha){
            this.thetha = thetha;
        }

        public void setThetha(float thetha) {
            if(thetha>=90) {
                this.thetha = 0;
                this.start = 90;
            }else if(thetha<=-90){
                this.thetha = 0;
                this.start = 0;
            }
            else {
                this.thetha = thetha;
            }
        }

        public float getThetha() {
            return this.thetha;
        }

        public float getStart(){
            return this.start;
        }

        public void incThetha(){
            if(this.start==90)
                setThetha(this.thetha-0.05f);
            else
                setThetha(this.thetha+0.05f);
        }
    }

    //arraylist of arcs to define theta and start point of each arc
    ArrayList<Arc> arcs = new ArrayList<>();
    int side;
    int first = 0;

    public Circular(Context context) {
        super(context);
    }

    public Circular(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public Circular(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Circular(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    public void init(@Nullable AttributeSet attributeSet){

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.rgb(204,204,204));

        Paint neon = new Paint(Paint.ANTI_ALIAS_FLAG);
        neon.setStrokeWidth(7);
        neon.setStyle(Paint.Style.STROKE);
        neon.setColor(Color.rgb(90, 42, 148));

        int radius = 10;
        if(getHeight() <= getWidth())
            side = getHeight();
        else
            side = getWidth();
        for(int i = radius;i<side;i=i+20)
        {
            canvas.drawArc(-i,-i,i,i,0f,90f,false,paint);
            if(first==0){
                //if first time run then add arcs
                float r = new Random().nextInt(90);
                arcs.add(new Arc(r));
            }
        }

        first =1;

        for (int j = radius,i=0; j < side; j = j + 20,i++) {
            //inc arcs and draw it
            canvas.drawArc(-j, -j, j, j, arcs.get(i).getStart(),arcs.get(i).getThetha() , false, neon);
            arcs.get(i).incThetha();
        }
        postInvalidate();
    }
}
