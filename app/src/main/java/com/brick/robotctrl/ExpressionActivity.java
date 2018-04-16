package com.brick.robotctrl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.ant.liao.GifView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.facebook.drawee.view.SimpleDraweeView;
import com.rg2.utils.ToastUtil;

import java.io.File;

public class ExpressionActivity extends BaseActivity  {
	private static final String TAG = "ExpressionActivity";

	private static GifView gifView;
	private int index = 0;
//	UserTimer userTimer = null;
	private static int currentIndex = -1;
	private GestureDetector mGestureDetector;
	private int screenWidth;
	private int screenHeight;
	public static ImageView igv;
	public static SimpleDraweeView f;

private static Context context;


	enum EXPRESSION {
		机器人愤怒(R.drawable.hami2, "fennu", 0),
		机器人嘟嘴(R.drawable.hami2, "duzui", 1);
		/*机器人惊讶(R.drawable.jingya, "jingya", 2),
		机器人花痴(R.drawable.huachi, "huachi", 3),
		机器人可怜(R.drawable.kelian, "kelian", 4),
		机器人可爱(R.drawable.keai, "keshui", 5),
		机器人哭泣(R.drawable.kuqi, "kuqi", 6),
		机器人调皮(R.drawable.tiaopi, "tiaopi", 7),
		机器人委屈(R.drawable.weiqu, "weiqu", 8),
		机器人微笑(R.drawable.weixiao, "weixiao", 9),
		机器人郁闷(R.drawable.yumen, "yumen", 10),
		机器人充电(R.drawable.chongdian, "chongdian", 11);*/


		private int id;
		private String name;
		private int index;
		EXPRESSION(int id, String name, int index) {
			this.id = id;
			this.name = name;
			this.index = index;
		}
		public static int getExpressionSize() {
			int ExpressionSize = 1;
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
			return EXPRESSION.机器人嘟嘴;//return EXPRESSION.机器人可爱;
		}
		public static EXPRESSION getExpression( String name ) {
			for ( EXPRESSION exp: EXPRESSION.values()) {
				if ( name.equals(exp.name) ) {
					return exp;
				}
			}
			return EXPRESSION.机器人嘟嘴;
		}
	}

	@Override
	protected void initViews(Bundle savedInstanceState) {
		//Fresco.initialize(this);//初始化
		setContentView(R.layout.gif);
         context = this;
		screenWidth = getWindowManager().getDefaultDisplay().getWidth();       // 屏幕宽（像素，如：480px）
		screenHeight = getWindowManager().getDefaultDisplay().getHeight();      // 屏幕高（像素，如：800p）

		    Intent intent = getIntent();

		index = intent.getIntExtra("index",1);
		 Log.i("express","........<...>........"+index);

		//暂时注释
	/*	gifView = (GifView) findViewById(R.id.gif2);
		gifView.setGifImageType(GifView.GifImageType.COVER);*/
         igv = (ImageView) findViewById(R.id.igv);
    // 	gifView.setOnClickListener(this);
	//屏幕适配;
   //  gifView.setShowDimension(screenWidth, screenHeight);
     	changeExpression(index);
	}

/*
* 暂时不用先屏蔽掉*/
	@Override
	protected void initData() {
		/*gifView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent( ExpressionActivity.this , PrintActivity.class));
			}
		});*/
	}

	@Override
	protected void initViewData() {
	}
	@Override    //触摸表情返回到选择界面
	protected void initEvent() {

	}

	int doX,  doY;
	int movX,  movY;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		if (mGestureDetector.onTouchEvent(event))
//			return true;

		return super.onTouchEvent(event);
	}


	public static void changeExpression(int index) {
		Log.d(TAG, "changeExpression: current expression:" + currentIndex + "\tset expression:" + index);

		if ( currentIndex != index ) {
		/*	if (index > EXPRESSION.getExpressionSize()-2) {
				//如果表情角标大于表情枚举个数，从0开始一次递增显示表情
			index = index -(EXPRESSION.getExpressionSize()-1);
			Log.d(TAG, EXPRESSION.getExpressionSize()+"changeExpression 大于枚举长度: ..... " + index);
		   }*/
			System.gc();//垃圾回收机制
			/*if(index <=11 ) {
				gifView.setGifImage(EXPRESSION.getExpression(index).id);
				gifView.showAnimation();
			 } else {*/

			   if(index==1){
				 Glide.with(RobotApplication.getAppContext()).load(EXPRESSION.getExpression(1).id).priority(Priority.IMMEDIATE).into(igv);
				   currentIndex = index;
			          return;
			   }
				//图片文件夹
		        String picDir  = Environment.getExternalStorageDirectory().getPath()+"/Pictures/";
				String jpgpath = picDir+index+".jpg";//本地图片完整地址
			     File jpgfile = new File(jpgpath);
                if (jpgfile.exists()){

					try {
		      Glide.with(RobotApplication.getAppContext()).load(jpgpath).priority(Priority.IMMEDIATE).into(igv);
			//.skipMemoryCache(flase).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(igv);*/
		    	/*  FileInputStream	fis = new FileInputStream(new File(gifpath));
					gifView.setGifImage(fis);
					gifView.showAnimation();*/
					Log.d(TAG, "changeExpression: 存在这张图片"+jpgpath);
					ToastUtil.show(RobotApplication.getAppContext(),"存在这张图片,真开心 ^_^ ");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else {
					Log.d(TAG, "changeExpression: 不存在这张图片"+jpgpath);
					Glide.with(RobotApplication.getAppContext()).load(EXPRESSION.getExpression(0).id).priority(Priority.IMMEDIATE).into(igv);
					/*gifView.setGifImage(EXPRESSION.getExpression(9).id);
					 gifView.showAnimation();*/
				}
			/*}*/

		     currentIndex = index;
			Log.d(TAG, "changeExpression: ..... " + index);
		}else {
		  Log.d(TAG, "changeExpression等于当前表情编号: ..... " + index);

		}
	}


	public void onClick(View v) {
		//clearTimerCount();
		int index = currentIndex;
		index++;
		if ( index >= EXPRESSION.getExpressionSize())
		 index = 0;
		/*//如果表情角标大于表情美剧个数，从0开始一次递增显示表情
		index = index - EXPRESSION.getExpressionSize();*/
		Log.d(TAG, "index - EXPRESSION.getExpressionSize(): ..... " + index);
		changeExpression(index);

	  }
	public static void startAction(Context context, int index) {
		Log.d(TAG, "Expression: clear Event");
		Intent changeMotionIntent = new Intent();
		changeMotionIntent.setClass(context, ExpressionActivity.class);
		changeMotionIntent.putExtra("index", index);
		context.startActivity(changeMotionIntent);
	}
	// 接收尚未写好，需要再try中判断传递的参数是什么类型
	public static void startActions(Context context, String expName) {
		Intent changeMotionIntent = new Intent();
		changeMotionIntent.setClass(context, ExpressionActivity.class);
		changeMotionIntent.putExtra("expName", expName);
		context.startActivity(changeMotionIntent);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		//updatePresentation();
		Log.i(TAG, "生命------onRestart:updatePresentation 执行了");
	}

	@Override
	protected void onResume() {
		super.onResume();
		//updatePresentation();//父类中已经被调用了;
		Log.i(TAG, "生命------onResume:ExpresionActivity 执行了");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "生命------onPause: 执行了");
		onStop();
	}

	@Override
    protected void onStop() {
        currentIndex = -1;		// restart expression
        super.onStop();
        Log.i(TAG, "生命------ onStop");
        // finish();
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "生命------ onDestroy");
	}

	@Override
	protected void onStart() {
		Log.i(TAG, "生命------onStart");
		super.onStart();
	}
}
