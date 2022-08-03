package com.example.ipcclient

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.ipcservice.IMusicService

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var mService: IMusicService? = null
    private var mIsServiceConnected = false
    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            mService = IMusicService.Stub.asInterface(iBinder)
            mIsServiceConnected = true
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mIsServiceConnected = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindService()
        initViews()
    }

    private fun bindService() {
        val intent = Intent(MUSIC_ACTION)
        intent.setPackage(MUSIC_PACKAGE)
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE)
    }

    private fun initViews() {
        findViewById<Button>(R.id.button_pause).setOnClickListener(this)
        findViewById<Button>(R.id.button_play).setOnClickListener(this)
    }

    override fun onClick(view: View) {
        if (!mIsServiceConnected) {
            return
        }
        when (view.id) {
            R.id.button_play -> try {
                mService?.play()
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
            R.id.button_pause -> try {
                mService?.pause()
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        unbindService(mServiceConnection)
        super.onDestroy()
    }

    companion object {
        private const val MUSIC_ACTION = "com.example.ipcservice.service.MusicService.BIND"
        private const val MUSIC_PACKAGE = "com.example.ipcservice"
    }
}
