package com.brick.robotctrl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.ant.liao.GifView;
import com.ant.liao.GifView.GifImageType;

public class ExpressionActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = "ExpressionActivity";

	private static GifView gf;
	private int w ;
	private int h ;
	private boolean f = true;
	private String index = null;
	UserTimer userTimer = null;
	private static int currentIndex = 0;

	enum EXPRESSION {
		机器人害怕(R.drawable.haipa, "机器人害怕", 0),
		机器人害羞(R.drawable.haixiu, "机器人害羞", 1),
		机器人花痴(R.drawable.huachi, "机器人花痴", 2),
		机器人欢呼(R.drawable.huanhu,"机器人欢呼", 3),
		机器人骄傲得意(R.drawable.jiaoaodeyi, "机器人骄傲得意", 4),
		机器人金币(R.drawable.jinbi, "机器人金币", 5),
		机器人困惑(R.drawable.kunhuo, "机器人困惑", 6),
		机器人流泪(R.drawable.liulei, "机器人流泪", 7),
		机器人生气(R.drawable.shengqi, "机器人生气", 8),
		机器人说话(R.drawable.shuohua, "机器人说话", 9),
		机器人思索(R.drawable.sisuo, "机器人思索", 10),
		机器人叹气(R.drawable.tanqi, "机器人叹气", 11),
		机器人微笑(R.drawable.weixiao, "机器人微笑", 12),
		机器人羡慕(R.drawable.xianmu, "机器人羡慕", 13),
		机器人兴奋(R.drawable.xingfen, "机器人兴奋", 14);
		private int id;
		private String name;
		private int index;
		EXPRESSION(int id, String name, int index) {
			this.id = id;
			this.name = name;
			this.index = index;
		}
		public static int getExpressionSize() {
			int ExpressionSize = 0;
			for ( EXPRESSION exp: EXPRESSION.values()) {
				ExpressionSize++;
			}
			return ExpressionSize;
		}
		public static EXPRESSION getExpression( int index ) {
			for ( EXPRESSION exp: EXPRESSION.values()) {
				if ( index == exp.index ) {
					return exp;
				}
			}
			return EXPRESSION.机器人微笑;
		}
		public static EXPRESSION getExpression( String name ) {
			for ( EXPRESSION exp: EXPRESSION.values()) {
				if ( name.equals(exp.name) ) {
					return exp;
				}
			}
			return EXPRESSION.机器人微笑;
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
		//gf.setShowDimension(screenWidth, screenHeight);

		changeExpression(Integer.parseInt(index));
	}

	public static void changeExpression(int index) {
		Log.d(TAG, "changeExpression: current expression:" + currentIndex + "\tset expression:" + index);
		if ( currentIndex != index ) {
			System.gc();
			gf.setGifImage(EXPRESSION.getExpression(index).id);
			gf.showAnimation();
			currentIndex = index;
		}
	}


	public void onClick(View v) {
		userTimer.clearTimerCount();

//		int index = currentIndex;
//		index++;
//		if ( index >= EXPRESSION.getExpressionSize())
//			index = 0;
//		changeExpression(index);
	}

	public static void startExpressionActivity(Context context, String index) {
		Intent changeMotionIntent = new Intent();
		changeMotionIntent.setClass(context, ExpressionActivity.class);
		changeMotionIntent.putExtra("index", index);
		context.startActivity(changeMotionIntent);
	}
}
