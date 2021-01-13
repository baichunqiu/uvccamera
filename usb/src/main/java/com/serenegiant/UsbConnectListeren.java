//package com.serenegiant;
//
//import android.hardware.usb.UsbDevice;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.serenegiant.usb.USBMonitor;
//
//public class UsbConnectListeren implements USBMonitor.OnDeviceConnectListener {
//    private final static String TAG = "UsbConnectListeren";
//    @Override
//    public void onAttach(final UsbDevice device) {
//        Log.e(TAG,"usb device onAttach");
//    }
//
//    @Override
//    public void onConnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock, final boolean createNew) {
//        mCameraHandler.open(ctrlBlock);
//        startPreview();
//        updateItems();
//    }
//
//    @Override
//    public void onDisconnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock) {
//        if (DEBUG) Log.v(TAG, "onDisconnect:");
//        if (mCameraHandler != null) {
//            queueEvent(new Runnable() {
//                @Override
//                public void run() {
//                    mCameraHandler.close();
//                }
//            }, 0);
//            setCameraButton(false);
//            updateItems();
//        }
//    }
//
//    @Override
//    public void onDettach(final UsbDevice device) {
//        Toast.makeText(MainActivity.this, "USB_DEVICE_DETACHED", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onCancel(final UsbDevice device) {
//        setCameraButton(false);
//    }
//}
