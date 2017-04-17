package com.fragment;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.View;

import com.brick.robotctrl.R;

/**
 * Created by lx on 2017/4/11.
 */

public class SoundFragment extends BaseFragment {

    private RecordThread rec;
    private boolean flag = true;
    private AudioRecord audioRecord;
    private AudioTrack audioTrack;

    @Override
    public View initView() {
        return View.inflate(getActivity(), R.layout.sound_fragment,null);
    }

    @Override
    public void initData() {
        rec = new RecordThread();
        rec.start();
    }

    class RecordThread extends Thread{
        static final int frequency = 44100;
        static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
        static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
        @Override
        public void run() {
            // TODO Auto-generated method stub
            int recBufSize = AudioRecord.getMinBufferSize(frequency,
                    channelConfiguration, audioEncoding)*2;
            int plyBufSize = AudioTrack.getMinBufferSize(frequency,
                    channelConfiguration, audioEncoding)*2;

            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
                    channelConfiguration, audioEncoding, recBufSize);

            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
                    channelConfiguration, audioEncoding, plyBufSize, AudioTrack.MODE_STREAM);

            byte[] recBuf = new byte[recBufSize];
            audioRecord.startRecording();
            audioTrack.play();
            while(flag){
                int readLen = audioRecord.read(recBuf, 0, recBufSize);
                audioTrack.write(recBuf, 0, readLen);
                Log.d("SoundFragment","....sound");
//             audioTrack.stop();
//             audioRecord.stop();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
         flag = false;
        try {
            audioTrack.stop();
            audioRecord.stop();
            rec.stop();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
}
