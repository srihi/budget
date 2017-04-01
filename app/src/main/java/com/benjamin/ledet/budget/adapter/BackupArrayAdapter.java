package com.benjamin.ledet.budget.adapter;


import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.benjamin.ledet.budget.R;
import com.benjamin.ledet.budget.Realm.FormatDateTime;
import com.benjamin.ledet.budget.model.BudgetBackup;
import com.benjamin.ledet.budget.activity.BackupActivity;
import com.google.android.gms.drive.DriveId;


import java.util.List;

public class BackupArrayAdapter extends ArrayAdapter<BudgetBackup> {

    private Context context;
    private FormatDateTime formatDateTime;

    public BackupArrayAdapter(Context context, int resource, List<BudgetBackup> items) {
        super(context, resource, items);
        this.context = context;
        formatDateTime = new FormatDateTime(context);
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.activity_backup_drive_restore_item, parent, false);
        }

        BudgetBackup budgetBackup = getItem(position);
        if (budgetBackup != null) {
            final DriveId driveId = budgetBackup.getDriveId();
            final String modified = formatDateTime.formatDate(budgetBackup.getModifiedDate());
            final String size = Formatter.formatFileSize(getContext(), budgetBackup.getBackupSize());

            TextView modifiedTextView = (TextView) v.findViewById(R.id.item_history_time);
            TextView typeTextView = (TextView) v.findViewById(R.id.item_history_type);
            modifiedTextView.setText(modified);
            typeTextView.setText(size);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Show custom dialog
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dialog_backup_restore);
                    TextView createdTextView = (TextView) dialog.findViewById(R.id.dialog_backup_restore_created);
                    TextView sizeTextView = (TextView) dialog.findViewById(R.id.dialog_backup_restore_size);
                    Button restoreButton = (Button) dialog.findViewById(R.id.dialog_backup_restore_button_restore);
                    Button cancelButton = (Button) dialog.findViewById(R.id.dialog_backup_restore_button_cancel);

                    createdTextView.setText(modified);
                    sizeTextView.setText(size);

                    restoreButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((BackupActivity) context).downloadFromDrive(driveId.asDriveFile());
                        }
                    });

                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            });
        }

        return v;
    }
}
