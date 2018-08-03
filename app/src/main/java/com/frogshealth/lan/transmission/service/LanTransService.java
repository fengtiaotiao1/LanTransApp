package com.frogshealth.lan.transmission.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.frogshealth.lan.transmission.net.NetUdpHelper;

/**********************************************************************
 * 服务类。
 *
 * @类名 LanTransService
 * @包名 com.frogshealth.lan.transmission.service
 * @author yuanjf
 * @创建日期 18/8/2
 ***********************************************************************/
public class LanTransService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LanBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class LanBinder extends Binder {

    }
}
