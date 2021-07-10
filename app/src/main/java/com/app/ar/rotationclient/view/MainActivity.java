package com.app.ar.rotationclient.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.widget.Toast;
import com.app.ar.rotationclient.R;
import com.app.ar.rotationclient.constatnts.Const;
import com.app.ar.rotationclient.databinding.ActivityMainBinding;
import com.app.ar.rotationservice.IRotation;
import com.app.ar.rotationservice.ItaskCallback;
import static com.app.ar.rotationclient.constatnts.Const.SERVICE_NAME;

public class MainActivity extends AppCompatActivity {

    private IRotation rotationService = null;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initConnection();
    }

    private void initConnection() {
       if (appInstalledOrNot(Const.SERVER_PKG_NAME)) {
           if (rotationService == null) {
               Intent intent = new Intent(IRotation.class.getName());

               /*this is service name which has been declared in the server's manifest file in service's intent-filter*/
               intent.setAction(SERVICE_NAME);

               /*From 5.0 annonymous intent calls are suspended so replacing with server app's package name*/
               intent.setPackage(Const.SERVER_PKG_NAME);

               // binding to remote service
               bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
           }
       }else {
           Toast.makeText(this,"Please install your server app first",Toast.LENGTH_SHORT).show();
       }
    }
    private ItaskCallback mCallback = new ItaskCallback.Stub() {
        @Override
        public void valueChanged(float pitch, float roll) throws RemoteException {
            binding.attitudeIndicator.setAttitude(pitch, roll);
            setData(pitch,roll);
        }

    };

    private void setData(float pitch,float roll){
        binding.imuData.post(new Runnable() {
            @Override
            public void run() {
                //binding.imuData.setText("Pitch:"+pitch+" roll:"+roll);
            }
        });



    }

    private ServiceConnection serviceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            rotationService = IRotation.Stub.asInterface((IBinder) service);
            try {
                if (rotationService != null) {
                    rotationService.registerCallback(mCallback); // Register Callback
                    rotationService.startCallback();
                }

            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            System.out.println("service disconnected");

        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (rotationService != null && mCallback !=null)
                rotationService.unregisterCallback(mCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            if (rotationService != null && mCallback !=null)
                rotationService.registerCallback(mCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }


}