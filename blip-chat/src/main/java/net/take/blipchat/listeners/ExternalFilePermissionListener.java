package net.take.blipchat.listeners;

/**
 * Created by davidn on 1/26/2018.
 */

public interface ExternalFilePermissionListener {

    void grantExternalFilePermission();

    void rejectExternalFilePermission();
}
