package com.brick.robotctrl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.ant.liao.GifView;
import com.ant.liao.GifView.GifImageType;
import com.zhangyt.log.LogUtil;

public class ExpressionActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = "ExpressionActivity";

	private static GifView gifView;
	private int index = 0;
//	UserTimer userTimer = null;
	private static int currentIndex = -1;
	private GestureDetector mGestureDetector;
	private int screenWidth;
	private int screenHeight;
	enum EXPRESSION {
		机器人说话(R.drawable.shuohua, "shuohua", 0),
		机器人害羞(R.drawable.haixiu, "haixiu", 1),
		机器人花痴(R.drawable.huachi, "huachi", 2),
		机器人欢呼(R.drawable.huanhu, "huanhu", 3),
		机器人得意(R.drawable.deyi, "deyi", 4),
		机器人财迷(R.drawable.caimi, "caimi", 5),
		机器人困惑(R.drawable.kunhuo, "kunhuo", 6),
		机器人流泪(R.drawable.liulei, "liulei", 7),
		机器人生气(R.drawable.shengqi, "shengqi", 8),
		机器人害怕(R.drawable.haipa, "haipa", 9),
		机器人思索(R.drawable.sisuo, "sisuo", 10),
		机器人叹气(R.drawable.tanqi, "tanqi", 11),
		机器人微笑(R.drawable.weixiao, "weixiao", 12),
		机器人羡慕(R.drawable.xianmu, "xianmu", 13),
		机器人兴奋(R.drawable.xingfen, "xingfen", 14);
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

		screenWidth = getWindowManager().getDefaultDisplay().getWidth();       // 屏幕宽（像素，如：480px）
		screenHeight = getWindowManager().getDefaultDisplay().getHeight();      // 屏幕高（像素，如：800p）
		LogUtil.e("TAG" + "  getDefaultDisplay", "screenWidth=" + screenWidth + "; screenHeight=" + screenHeight);

		Intent intent = getIntent();
//		index = intent.getStringExtra("index");
		index = intent.getIntExtra("index", 0);

//		userTimer = new UserTimer();

		setContentView(R.layout.gif);
		gifView = (GifView) findViewById(R.id.gif2);
//		gifView.setOnClickListener(this);
		gifView.setGifImageType(GifImageType.COVER);
		//gifView.setShowDimension(screenWidth, screenHeight);

		changeExpression(index);
		mGestureDetector = new GestureDetector(this, new ExGestureListener());
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mGestureDetector.onTouchEvent(event))
			return true;

		// 处理手势结束
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_UP:
				endGesture();
				break;
		}
		return super.onTouchEvent(event);
	}

	/** 手势结束 */
	private void endGesture() {
	}
	private class ExGestureListener extends GestureDetector.SimpleOnGestureListener {
		long[] mHitsL = new long[5];
		long[] mHitsR = new long[5];

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			float x = e.getX();
			float y = e.getY();
			LogUtil.d(TAG, "onTouch: x:" + x + "y:" + y);

			if (y < screenHeight / 2) {
				if (x < screenWidth / 2) {
					System.arraycopy(mHitsL, 1, mHitsL, 0, mHitsL.length - 1);
					mHitsL[mHitsL.length - 1] = SystemClock.uptimeMillis();
					//LogUtil.d(TAG, "onPreferenceClick:mHits" + mHits[4]+ ","+mHits[3]+"," + mHits[2]+"," + mHits[1]+"," + mHits[0]);
					if (mHitsL[0] >= (SystemClock.uptimeMillis() - 3000)) {
						//LogUtil.d(TAG,"onPreferenceClick:进入");
						startActivity(new Intent().setClass(ExpressionActivity.this, SettingsActivity.class));
					}
				} else {
					System.arraycopy(mHitsR, 1, mHitsR, 0, mHitsR.length - 1);
					mHitsR[mHitsR.length - 1] = SystemClock.uptimeMillis();
					//LogUtil.d(TAG, "onPreferenceClick:mHits" + mHits[4]+ ","+mHits[3]+"," + mHits[2]+"," + mHits[1]+"," + mHits[0]);
					if (mHitsR[0] >= (SystemClock.uptimeMillis() - 3000)) {
						//LogUtil.d(TAG,"onPreferenceClick:进入");
						startActivity(new Intent().setClass(ExpressionActivity.this, AboutActivity.class));
					}
				}
			}
			return true;
		}
	}


	public static void changeExpression(int index) {
		LogUtil.d(TAG, "changeExpression: current expression:" + currentIndex + "\tset expression:" + index);
		if ( currentIndex != index ) {
			System.gc();
			gifView.setGifImage(EXPRESSION.getExpression(index).id);
			gifView.showAnimation();
			currentIndex = index;
		}
	}


	public void onClick(View v) {
		clearTimerCount();

		int index = currentIndex;
		index++;
		if ( index >= EXPRESSION.getExpressionSize())
			index = 0;
		changeExpression(index);
	}

	public static void startAction(Context context, int index) {
		Intent changeMotionIntent = new Intent();
		changeMotionIntent.setClass(context, ExpressionActivity.class);
		changeMotionIntent.putExtra("index", index);
		context.startActivity(changeMotionIntent);
	}

	// 接收尚未写好，需要再try中判断传递的参数是什么类型
	public static void startAction(Context context, String expName) {
		Intent changeMotionIntent = new Intent();
		changeMotionIntent.setClass(context, ExpressionActivity.class);
		changeMotionIntent.putExtra("expName", expName);
		context.startActivity(changeMotionIntent);
	}
    @Override
    protected void onStop() {
        LogUtil.i(TAG, "onStop");
        currentIndex = -1;		// restart expression
        super.onStop();
    }
}
