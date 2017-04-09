package com.benjamin.ledet.budget.model;

import com.google.android.gms.drive.DriveId;

/**
 * Created by benjaminledet on 15/03/2017.
 */

public class BudgetBackup {
    private DriveId driveId;
    private long modifiedDate;
    private long backupSize;

    public BudgetBackup(DriveId driveId, long modifiedDate, long backupSize) {
        this.driveId = driveId;
        this.modifiedDate = modifiedDate;
        this.backupSize = backupSize;
    }

    public DriveId getDriveId() {
        return driveId;
    }

    public void setDriveId(DriveId driveId) {
        this.driveId = driveId;
    }

    public long getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(long modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public long getBackupSize() {
        return backupSize;
    }

    public void setBackupSize(long backupSize) {
        this.backupSize = backupSize;
    }
}