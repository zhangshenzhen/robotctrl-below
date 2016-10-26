package com.brick.robotctrl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.ant.liao.GifView;
import com.ant.liao.GifView.GifImageType;

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
		机器人得意(R.drawable.deyi, "deyi", 0),
		机器人尴尬(R.drawable.ganga, "ganga", 1),
		机器人好奇(R.drawable.haoqi, "haoqi", 2),
		机器人怀疑(R.drawable.huaiyi, "huaiyi", 3),
		机器人坏笑(R.drawable.huaixiao, "huaixiao", 4),
		机器人惊讶(R.drawable.jingya, "jingya", 5),
		机器人开心(R.drawable.kaixin, "kaixin", 6),
		机器人瞌睡(R.drawable.keshui, "keshui", 7),
		机器人可爱(R.drawable.keai, "keai", 8),
		机器人可怜(R.drawable.kelian, "kelian", 9),
		机器人哭泣(R.drawable.kuqi, "kuqi", 10),
		机器人愤怒(R.drawable.fennu, "fennu", 11),
		机器人亲亲(R.drawable.qinqin, "qinqin", 12),
		机器人撒娇(R.drawable.sajiao, "sajiao", 13),
		机器人调皮(R.drawable.tiaopi, "tiaopi", 14),
		机器人委屈(R.drawable.weiqu, "weiqu", 15),
		机器人温怒(R.drawable.wennu, "wennu", 16),
		机器人荫郁(R.drawable.yinyu, "yinyu", 17),
		机器人责问(R.drawable.zewen, "zewen", 18),
		机器人眨眼(R.drawable.zhayan, "zhayan", 19),
		机器人专注(R.drawable.zhuanzhu, "zhuanzhu", 20);

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
			return EXPRESSION.机器人可爱;
		}
		public static EXPRESSION getExpression( String name ) {
			for ( EXPRESSION exp: EXPRESSION.values()) {
				if ( name.equals(exp.name) ) {
					return exp;
				}
			}
			return EXPRESSION.机器人可爱;
		}
	}
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		screenWidth = getWindowManager().getDefaultDisplay().getWidth();       // 屏幕宽（像素，如：480px）
		screenHeight = getWindowManager().getDefaultDisplay().getHeight();      // 屏幕高（像素，如：800p）
		Log.e("TAG" + "  getDefaultDisplay", "screenWidth=" + screenWidth + "; screenHeight=" + screenHeight);

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
			Log.d(TAG, "onTouch: x:" + x + "y:" + y);

			if (y < screenHeight / 2) {
				if (x < screenWidth / 2) {
					System.arraycopy(mHitsL, 1, mHitsL, 0, mHitsL.length - 1);
					mHitsL[mHitsL.length - 1] = SystemClock.uptimeMillis();
					//Log.d(TAG, "onPreferenceClick:mHits" + mHits[4]+ ","+mHits[3]+"," + mHits[2]+"," + mHits[1]+"," + mHits[0]);
					if (mHitsL[0] >= (SystemClock.uptimeMillis() - 3000)) {
						//Log.d(TAG,"onPreferenceClick:进入");
						startActivity(new Intent().setClass(ExpressionActivity.this, SettingsActivity.class));
					}
				} else {
					System.arraycopy(mHitsR, 1, mHitsR, 0, mHitsR.length - 1);
					mHitsR[mHitsR.length - 1] = SystemClock.uptimeMillis();
					//Log.d(TAG, "onPreferenceClick:mHits" + mHits[4]+ ","+mHits[3]+"," + mHits[2]+"," + mHits[1]+"," + mHits[0]);
					if (mHitsR[0] >= (SystemClock.uptimeMillis() - 3000)) {
						//Log.d(TAG,"onPreferenceClick:进入");
						startActivity(new Intent().setClass(ExpressionActivity.this, AboutActivity.class));
					}
				}
			}
			return true;
		}
	}


	public static void changeExpression(int index) {
		Log.d(TAG, "changeExpression: current expression:" + currentIndex + "\tset expression:" + index);
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
        Log.i(TAG, "onStop");
        currentIndex = -1;		// restart expression
        super.onStop();
    }
}
