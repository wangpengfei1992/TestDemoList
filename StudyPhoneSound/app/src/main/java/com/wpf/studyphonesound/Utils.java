package com.wpf.studyphonesound;

import android.Manifest;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Utils {
    private static Utils utils;

    private Utils() {

    }
    public static Utils getInstance() {
        if (utils == null) {
            utils = new Utils();
        }
        return utils;
    }


    private String environmentFileRoot;//文件根路径
    public String initFileRoot(Context context) {
        if (TextUtils.isEmpty(environmentFileRoot)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // 先判断有没有权限
                if (!Environment.isExternalStorageManager()) {
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//sd卡是否可用
                        int currentapiVersion=android.os.Build.VERSION.SDK_INT;//手机系统版本号
                        Log.e("FileHelp","SDK_INT::"+currentapiVersion);
                        if (currentapiVersion<android.os.Build.VERSION_CODES.Q){
                            environmentFileRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
                        }else {
                            File external = context.getExternalFilesDir(null);
                            if (external != null) {
                                environmentFileRoot =  external.getAbsolutePath();
                            }
                        }
                    }else {
                        environmentFileRoot= context.getFilesDir().getAbsolutePath();
                    }
                }else{
                    environmentFileRoot = "/storage/emulated/0/wpf/StudyPhoneSound";
                }
            }

        }
        Log.e("FileHelp","environmentFileRoot::"+environmentFileRoot);
        return environmentFileRoot;
    }

    private FileChannel fileChannel;
    public void writeFile(String path, byte[] bytes) {

        try {
            File file = new File(path);
            if (!file.getParentFile().exists())file.getParentFile().mkdirs();

            FileOutputStream out = new FileOutputStream(path);//指定写到哪个路径中
            FileChannel fileChannel = out.getChannel();
            fileChannel.write(ByteBuffer.wrap(bytes)); //将字节流写入文件中
            fileChannel.force(true);//强制刷新
//            fileChannel.close();
            this.fileChannel = fileChannel;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void stopWriteFile() {
        try {
            if (fileChannel!=null){
                fileChannel.force(true);//强制刷新
                fileChannel.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pcmToWave(String inFileName, String outFileName){
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudiolen = 0;
        long longSamplRate = 11025;
        long totalDataLen = totalAudiolen+36;//由于不包括RIFF和WAV
        int channels = 2;
        long byteRate = 16*longSamplRate*channels/8;
        byte[] data = new byte[1024];
        try {
            in = new FileInputStream(inFileName);

            out = new FileOutputStream(outFileName);
            totalAudiolen = in.getChannel().size();
            totalDataLen = totalAudiolen+36;
            writeWaveFileHeader(out, totalAudiolen, totalDataLen, longSamplRate, channels, byteRate);
            while (in.read(data) != -1) {
                out.write(data);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeWaveFileHeader(FileOutputStream out, long totalAudioLen, long totalDataLen, long longSampleRate,
                                    int channels, long byteRate) {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);//数据大小
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';//WAVE
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        //FMT Chunk
        header[12] = 'f'; // 'fmt '
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';//过渡字节
        //数据大小
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        //编码方式 10H为PCM编码格式
        header[20] = 1; // format = 1
        header[21] = 0;
        //通道数
        header[22] = (byte) channels;
        header[23] = 0;
        //采样率，每个通道的播放速度
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        //音频数据传送速率,采样率*通道数*采样深度/8
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        // 确定系统一次要处理多少个这样字节的数据，确定缓冲区，通道数*采样位数
        header[32] = (byte) (channels * 16 / 8);
        header[33] = 0;
        //每个样本的数据位数
        header[34] = 16;
        header[35] = 0;
        //Data chunk
        header[36] = 'd';//data
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        try {
            out.write(header, 0, 44);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
