/*
 *  UVCCamera
 *  library and sample to access to UVC web camera on non-rooted Android device
 *
 * Copyright (c) 2014-2017 saki t_saki@serenegiant.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *  All files in the folder are under this Apache License, Version 2.0.
 *  Files in the libjpeg-turbo, libusb, libuvc, rapidjson folder
 *  may have a different license, see the respective files.
 */

package com.serenegiant;

import android.graphics.SurfaceTexture;
import android.hardware.usb.UsbDevice;
import android.util.Log;
import android.view.Surface;

import com.serenegiant.common.BaseActivity;
import com.serenegiant.usb.CameraDialog;
import com.serenegiant.usb.DeviceFilter;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.USBMonitor.OnDeviceConnectListener;
import com.serenegiant.usb.USBMonitor.UsbControlBlock;
import com.serenegiant.usbcameracommon.R;
import com.serenegiant.usbcameracommon.UVCCameraHandler;
import com.serenegiant.widget.CameraViewInterface;

import java.util.List;

public class YvcCameraActivity extends BaseActivity implements OnDeviceConnectListener {
    private final String TAG = this.getClass().getSimpleName();
    //预览
    private static final int PREVIEW_WIDTH = 1280;
    private static final int PREVIEW_HEIGHT = 720;
    private static final int PREVIEW_MODE = 1;//0:YUYV, other:MJPEG

    private USBMonitor mUSBMonitor;
    protected UVCCameraHandler mCameraHandler;
    private CameraViewInterface mUVCCameraView;

    /**
     * 初始化UVC
     *
     * @param cameraViewInterface
     */
    protected void initYuv(CameraViewInterface cameraViewInterface) {
        if (null == cameraViewInterface) {
            throw new IllegalArgumentException("cameraViewInterface can not null !");
        }
        this.mUVCCameraView = cameraViewInterface;
        mUVCCameraView.setAspectRatio(PREVIEW_WIDTH / (float) PREVIEW_HEIGHT);
        mUSBMonitor = new USBMonitor(this, this);
        mCameraHandler = UVCCameraHandler.createHandler(this, mUVCCameraView, 1, PREVIEW_WIDTH, PREVIEW_HEIGHT, PREVIEW_MODE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUSBMonitor.register();
        if (mUVCCameraView != null) {
            mUVCCameraView.onResume();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        queueEvent(new Runnable() {
            @Override
            public void run() {
                connect();
            }
        }, 2000);
    }

    @Override
    protected void onStop() {
        mCameraHandler.close();
        if (mUVCCameraView != null) {
            mUVCCameraView.onPause();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (mCameraHandler != null) {
            mCameraHandler.release();
            mCameraHandler = null;
        }
        if (mUSBMonitor != null) {
            mUSBMonitor.destroy();
            mUSBMonitor = null;
        }
        mUVCCameraView = null;
        super.onDestroy();
    }

    private UsbDevice current;

    protected void connect() {
        List<UsbDevice> devices = getDevices();
        if (null != devices && !devices.isEmpty()) {
            UsbDevice device = devices.get(0);
            if (device.equals(current)) {
                return;
            }
            current = device;
            Log.e(TAG, "connect :" + current.getDeviceName());
            //申请权限连接设备
            mUSBMonitor.requestPermission(current);
        }
    }

    protected void closeCamera() {
        if (mCameraHandler != null) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    mCameraHandler.close();
                }
            }, 0);
        }
    }

    protected void autoRecord(String path) {
        if (mCameraHandler.isOpened()) {
            if (checkPermissionWriteExternalStorage() && checkPermissionAudio()) {
                if (!mCameraHandler.isRecording()) {
                    mCameraHandler.startRecording();
                } else {
                    mCameraHandler.stopRecording();
                }
            }
        }
    }

    protected void startRecord(String path) {
        if (null != mCameraHandler && mCameraHandler.isOpened() && !mCameraHandler.isRecording()) {
            mCameraHandler.startRecording();
        }
    }

    protected void stopRecord() {
        if (null != mCameraHandler && mCameraHandler.isOpened() && mCameraHandler.isRecording()) {
            mCameraHandler.startRecording();
        }
    }

    protected void takepicture(String path) {
        if (mCameraHandler.isOpened() && checkPermissionWriteExternalStorage()) {
            mCameraHandler.captureStill(path);
        }
    }

    protected void startPreview() {
        if (mCameraHandler.isOpened()) {
            final SurfaceTexture st = mUVCCameraView.getSurfaceTexture();
            mCameraHandler.startPreview(new Surface(st));
        }
    }

    public USBMonitor getUSBMonitor() {
        return mUSBMonitor;
    }

    /********* 设备连接监听*************/
    public void onAttach(final UsbDevice device) {
        Log.e(TAG, "usb device attach:" + device.getDeviceName());
        if (mCameraHandler.isEqual(device)) {
            Log.e(TAG, "usb device attach:isEqual = true");
        } else {
            Log.e(TAG, "usb device attach:isEqual = false");
        }
        connect();
    }

    @Override
    public void onConnect(final UsbDevice device, final UsbControlBlock ctrlBlock, final boolean createNew) {
        Log.e(TAG, "usb device connect:" + device.getDeviceName());
        mCameraHandler.open(ctrlBlock);
        startPreview();
    }

    @Override
    public void onDisconnect(final UsbDevice device, final UsbControlBlock ctrlBlock) {
        Log.e(TAG, "usb device disconnect:" + device.getDeviceName());
        closeCamera();
    }

    @Override
    public void onDettach(final UsbDevice device) {
        Log.e(TAG, "usb device dettach:" + device.getDeviceName());
        closeCamera();
    }

    @Override
    public void onCancel(final UsbDevice device) {
        Log.e(TAG, "usb device cancel:" + device.getDeviceName());
    }

    protected List<UsbDevice> getDevices() {
        final List<DeviceFilter> filter = DeviceFilter.getDeviceFilters(this, R.xml.device_filter);
        return mUSBMonitor.getDeviceList(filter.get(0));
    }
}
