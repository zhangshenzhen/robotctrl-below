package com.jly.expression;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.brick.robotctrl.UserTimer;

import com.ant.liao.GifView;
import com.ant.liao.GifView.GifImageType;
import com.brick.robotctrl.R;
import com.brick.robotctrl.BaseActivity;


public class expression extends BaseActivity implements OnClickListener {

	private GifView gf;
	private int w ;
	private int h ;
	private int width ;
	private int height;
	private boolean f = true;
	private int count=0;
	private String index = null;
	UserTimer userTimer = null;
	private static enum EXPRESSION {
		EXPRESSION_SHY(R.drawable.shy, "shy", 0),
		EXPRESSION_COMPLACENT(R.drawable.complacent, "complacent", 1),
		EXPRESSION_ANTHOMANIAC(R.drawable.anthomaniac, "anthomaniac", 2),
		EXPRESSION_EXCITING(R.drawable.exciting,"exciting", 3),
		EXPRESSION_SIGH(R.drawable.sigh, "sigh", 4),
		EXPRESSION_SMILE(R.drawable.smile, "smile", 5),
		EXPRESSION_TEAR(R.drawable.tear, "tear", 6),
		EXPRESSION_THINK(R.drawable.think, "think", 7);
		private int id;
		private String name;
		private int index;
		private EXPRESSION(int id, String name, int index) {
			this.id = id;
			this.name = name;
			this.index = index;
		}
		public static EXPRESSION getExpression( int index ) {
			for ( EXPRESSION exp: EXPRESSION.values()) {
				if ( index == exp.index ) {
					return exp;
				}
			}
			return EXPRESSION.EXPRESSION_SMILE;
		}
		public static EXPRESSION getExpression( String name ) {
			for ( EXPRESSION exp: EXPRESSION.values()) {
				if ( name == exp.name ) {
					return exp;
				}
			}
			return EXPRESSION.EXPRESSION_SMILE;
		}
	}
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		int screenWidth = getWindowManager().getDefaultDisplay().getWidth();       // 屏幕宽（像素，如：480px）
		int screenHeight = getWindowManager().getDefaultDisplay().getHeight();      // 屏幕高（像素，如：800p）
		Log.e("TAG" + "  getDefaultDisplay", "screenWidth=" + screenWidth + "; screenHeight=" + screenHeight);

		Intent intent = getIntent();
		index = intent.getStringExtra("index");

		userTimer = new UserTimer();

		w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);


		setContentView(R.layout.gif);
		gf = (GifView) findViewById(R.id.gif2);
		gf.setGifImage(R.drawable.shy);
		gf.setOnClickListener(this);
		gf.setGifImageType(GifImageType.COVER);
		gf.setShowDimension(screenWidth, screenHeight);

		View decorView = getWindow().getDecorView();
//        Hide both the navigation bar and the status bar.
//        SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
//        a general rule, you should design your app to hide the status bar whenever you
//        hide the navigation bar.
		int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;
		decorView.setSystemUiVisibility(uiOptions);

		switch (EXPRESSION.getExpression(index)) {
			case EXPRESSION_SHY:
				gf.setGifImage(R.drawable.shy);
				gf.showAnimation();
			case EXPRESSION_COMPLACENT:
				gf.setGifImage(R.drawable.complacent);
				gf.showAnimation();
				break;
			case EXPRESSION_ANTHOMANIAC:
				gf.setGifImage(R.drawable.anthomaniac);
				gf.showAnimation();
				break;
			case EXPRESSION_EXCITING:
				gf.setGifImage(R.drawable.exciting);
				gf.showAnimation();
				break;
			case EXPRESSION_SIGH:
				gf.setGifImage(R.drawable.sigh);
				gf.showAnimation();
				break;
			case EXPRESSION_SMILE:
				gf.setGifImage(R.drawable.smile);
				gf.showAnimation();
				break;
			case EXPRESSION_TEAR:
				gf.setGifImage(R.drawable.tear);
				gf.showAnimation();
				break;
			case EXPRESSION_THINK:
				gf.setGifImage(R.drawable.think);
				gf.showAnimation();
				break;
			default:
				break;
		}
	}


	public void onClick(View v) {
		userTimer.clearTimerCount();
		if (v == gf) {
			switch (++count) {
				case 1:
					gf.setGifImage(R.drawable.complacent);
					//gf1.setGifImageType(GifImageType.COVER);
					gf.showAnimation();
					break;
				case 2:
					gf.setGifImage(R.drawable.anthomaniac);
					//gf1.setGifImageType(GifImageType.COVER);
					gf.showAnimation();
					break;
				case 3:
					gf.setGifImage(R.drawable.exciting);
					//gf1.setGifImageType(GifImageType.COVER);
					gf.showAnimation();
					break;
				case 4:
					gf.setGifImage(R.drawable.sigh);
					//gf1.setGifImageType(GifImageType.COVER);
					gf.showAnimation();
					break;
				case 5:
					gf.setGifImage(R.drawable.smile);
					//gf1.setGifImageType(GifImageType.COVER);
					gf.showAnimation();
					break;
				case 6:
					gf.setGifImage(R.drawable.tear);
					//gf1.setGifImageType(GifImageType.COVER);
					gf.showAnimation();
					break;
				case 7:
					gf.setGifImage(R.drawable.think);
					//gf1.setGifImageType(GifImageType.COVER);
					gf.showAnimation();
					break;
				default:
					gf.setGifImage(R.drawable.shy);
					count = 0;
					gf.showAnimation();
					break;
			}
		}
	}

	public static void startExpressionActivity(Context context, String index) {
		Intent changeMotionIntent = new Intent();
		changeMotionIntent.setClass(context, expression.class);
		changeMotionIntent.putExtra("index", index);
		context.startActivity(changeMotionIntent);
	}
}
