package ro.go.mariusiliescu.panicbutton.utils;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import ro.go.mariusiliescu.panicbutton.VApp;

public class DeviceAdminUtil
{
    public static boolean checkisDeviceAdminEnabled()
    {
        if(VApp.devicePolicyManager != null && VApp.mAdminName != null)
        {
            if (VApp.devicePolicyManager.isAdminActive(VApp.mAdminName))
            {
                Log.d("x","Permision is enabled");
                return true;
            }
            else
            {
                Log.d("x" , "No admin permision");
            }
        }
        else
        {
            Log.d("x", "device managet is null");
        }

        return false;
    }

    public static void openDeviceManagerEnableAction(Activity activity)
    {
        openDeviceManagerEnableAction(activity,1);
    }

    public static void openDeviceManagerEnableAction(Activity activity, int requestCode)
    {
        if(!checkisDeviceAdminEnabled())
        {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, VApp.mAdminName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "We need this permission to lock your phone.");
            activity.startActivityForResult(intent, requestCode);
        }
        else
        {
            //Not safe,, May vary depends on ROM
            try
            {
                final Intent intent=new Intent();
                intent.setComponent(new ComponentName("com.android.settings","com.android.settings.DeviceAdminAdd"));
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, VApp.mAdminName);
                activity.startActivity(intent);
            }
            catch (Exception e)
            {
                Log.d("AdminEx", e.getMessage());
            }
        }
    }
}
