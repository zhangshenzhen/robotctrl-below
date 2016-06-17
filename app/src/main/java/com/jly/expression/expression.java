package com.jly.expression;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ant.liao.GifView;
import com.ant.liao.GifView.GifImageType;
import com.brick.robotctrl.R;



public class expression extends Activity implements OnClickListener{

	private GifView gf1;
	private Button bt;

	private int w ;
	private int h ;
	private int width ;
	private int height;
	private boolean f = true;
	private int count=0;
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		int screenWidth  = getWindowManager().getDefaultDisplay().getWidth();       // 屏幕宽（像素，如：480px）
		int screenHeight = getWindowManager().getDefaultDisplay().getHeight();      // 屏幕高（像素，如：800p）
		Log.e("TAG" + "  getDefaultDisplay", "screenWidth=" + screenWidth + "; screenHeight=" + screenHeight);


		w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
		h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);


		setContentView(R.layout.gif);
		gf1 = (GifView)findViewById(R.id.gif1);
		gf1.setGifImage(R.drawable.shy);
		gf1.setOnClickListener(this);
		gf1.setGifImageType(GifImageType.COVER);
		gf1.setShowDimension(screenWidth, screenHeight);



		bt = (Button)findViewById(R.id.button1);
		bt.setOnClickListener(this);

		gf1.measure(w, h);
		width =gf1.getMeasuredWidth();
		height =gf1.getMeasuredHeight();
		Log.e("TAG" + "  getDefaultDisplay", "Width=" + width + "; Height=" + height);

	}


	public void onClick(View v) {
		if(v == gf1)
		{
			if(f){
				gf1.showCover();
				f = false;
			}else{
				gf1.showAnimation();
				f = true;
			}
		}else if(v == bt)
		{
			switch (++count){
				case 1: gf1.setGifImage(R.drawable.complacent);
					//gf1.setGifImageType(GifImageType.COVER);
					gf1.showAnimation();
					break;
				case 2: gf1.setGifImage(R.drawable.anthomaniac);
					//gf1.setGifImageType(GifImageType.COVER);
					gf1.showAnimation();
					break;
				case 3: gf1.setGifImage(R.drawable.exciting);
					//gf1.setGifImageType(GifImageType.COVER);
					gf1.showAnimation();
					break;
				case 4: gf1.setGifImage(R.drawable.sigh);
					//gf1.setGifImageType(GifImageType.COVER);
					gf1.showAnimation();
					break;
				case 5: gf1.setGifImage(R.drawable.smile);
					//gf1.setGifImageType(GifImageType.COVER);
					gf1.showAnimation();
					break;
				case 6: gf1.setGifImage(R.drawable.tear);
					//gf1.setGifImageType(GifImageType.COVER);
					gf1.showAnimation();
					break;
				case 7: gf1.setGifImage(R.drawable.think);
					//gf1.setGifImageType(GifImageType.COVER);
					gf1.showAnimation();
					break;
				default : gf1.setGifImage(R.drawable.shy);count=0;
					gf1.showAnimation();
					break;
			}
		}
	}
}
