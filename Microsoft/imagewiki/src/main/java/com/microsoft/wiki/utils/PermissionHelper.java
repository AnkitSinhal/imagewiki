package com.microsoft.wiki.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;

public enum PermissionHelper {
    INSTANCE;

    /**
     * Check that the application has access to a list of permissions. Any missing permissions
     * will be requested
     *
     * @param permissions     Collections of String Permissions as defined in Android Manifest class
     * @param callingActivity The activity that will handle onRequestPermissionsResult
     * @return True if the permission is already granted,
     * false if the permission is not already granted and a request has for the permission has
     * been made
     */
    public boolean requestPermissions(String[] permissions, Activity callingActivity, int
            requestCode) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Store the permissions not currently granted
            ArrayList<String> permissionsNeedRequesting = new ArrayList<>();
            for (String permission : permissions) {
                // Check each permission
                if (!checkPermission(permission, callingActivity)) {
                    permissionsNeedRequesting.add(permission);
                }
            }

            // Check if any permissions are currently not granted
            if (permissionsNeedRequesting.size() > 0) {
                // Request permissions not currently granted
                callingActivity.requestPermissions(permissionsNeedRequesting.toArray(new
                        String[permissionsNeedRequesting.size()]), requestCode);

                return false;
            } else {
                // All permissions already granted.
                return true;
            }
        } else {
            // Permission management not supported on pre Android M devices
            return true;
        }
    }

    /**
     * Checks to see if the requested permission is already granted. Does not request the permission
     * if it is missing. Use requestPermission or requestPermissions to automatically acquire
     * missing permissions
     *
     * @param permission      the permission to check for
     * @param callingActivity the calling activity
     * @return true if the permission is already granted, false if it is not
     */
    public boolean checkPermission(String permission, Activity callingActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check to see if permission already granted.
            int permissionStatus = callingActivity.checkSelfPermission(permission);

            return permissionStatus == PackageManager.PERMISSION_GRANTED;
        } else {
            // Permission management not supported on pre Android M devices
            return true;
        }
    }
}
