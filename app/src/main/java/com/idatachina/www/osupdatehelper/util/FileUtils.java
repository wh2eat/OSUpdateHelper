package com.idatachina.www.osupdatehelper.util;

import java.text.DecimalFormat;

/**
 * Created by wanghang on 2017/11/30.
 */

public class FileUtils {

    public static String getDisplaySize(long size){
        DecimalFormat formater = new DecimalFormat("####.00");
        if(size<1024){
            return size+"bytes";
        }else if(size<1024*1024){
            float kbsize = size/1024f;
            return formater.format(kbsize)+"KB";
        }else if(size<1024*1024*1024){
            float mbsize = size/1024f/1024f;
            return formater.format(mbsize)+"MB";
        }else if(size<1024*1024*1024*1024){
            float gbsize = size/1024f/1024f/1024f;
            return formater.format(gbsize)+"GB";
        }else{
            return null;
        }
    }
}
