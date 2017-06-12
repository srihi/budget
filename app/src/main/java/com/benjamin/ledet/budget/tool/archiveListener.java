package com.benjamin.ledet.budget.tool;

import java.util.EventListener;

public interface archiveListener extends EventListener {

    void onArchive();

    void onUnarchive();
}
