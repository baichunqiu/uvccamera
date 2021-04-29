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

package com.bcq.uvc;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import com.serenegiant.YvcCameraActivity;
import com.serenegiant.usb.CameraDialog;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.widget.CameraViewInterface;

public final class CamearActivity extends YvcCameraActivity implements CameraDialog.CameraDialogListeren,
        OnClickListener, CompoundButton.OnCheckedChangeListener {
    private ToggleButton mCameraButton;
    private ImageButton mCaptureButton;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        final View view = findViewById(R.id.camera_view);
        initYuv((CameraViewInterface) view);
        view.setOnLongClickListener(mOnLongClickListener);

        mCameraButton = (ToggleButton) findViewById(R.id.camera_button);

        mCaptureButton = (ImageButton) findViewById(R.id.capture_button);
        mCaptureButton.setVisibility(View.INVISIBLE);
        mCaptureButton.setOnClickListener(this);
        mCameraButton.setOnCheckedChangeListener(this);
    }


    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.capture_button:
                autoRecord("");
                break;
        }
    }

    public void onCheckedChanged(final CompoundButton compoundButton, final boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.camera_button:
//                if (isChecked && !mCameraHandler.isOpened()) {
//                    CameraDialog.showDialog(CamearActivity.this);
//                } else {
//                    mCameraHandler.close();
//                    setCameraButton(false);
//                }
                break;
        }
    }

    /**
     * capture still image when you long click on preview image(not on buttons)
     */
    private final OnLongClickListener mOnLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(final View view) {
            switch (view.getId()) {
                case R.id.camera_view:
                    takepicture("");
            }
            return false;
        }
    };

    private void setCameraButton(final boolean isOn) {
        CompoundButton.OnCheckedChangeListener listener = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mCameraButton != null) {
                    try {
                        mCameraButton.setOnCheckedChangeListener(null);
                        mCameraButton.setChecked(isOn);
                    } finally {
                        mCameraButton.setOnCheckedChangeListener(listener);
                    }
                }
                if (!isOn && (mCaptureButton != null)) {
                    mCaptureButton.setVisibility(View.INVISIBLE);
                }
            }
        }, 0);
    }

    /**
     * to access from CameraDialog
     *
     * @return
     */
    @Override
    public USBMonitor onGetUsbMonitor() {
        return super.getUSBMonitor();
    }

    @Override
    public void onDialogResult(boolean canceled) {
        if (canceled) {
            setCameraButton(false);
        }
    }

}
