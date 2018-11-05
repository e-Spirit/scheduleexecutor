package com.espirit.ps.psci.scheduleexecutor.plugin;

import java.util.Collection;
import java.util.Collections;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.webedit.plugin.WebeditToolbarActionsItemsPlugin;
import de.espirit.firstspirit.webedit.plugin.toolbar.WebeditToolbarItem;

public class ScheduleExecutionWebeditToolbarActionsItemsPlugin implements WebeditToolbarActionsItemsPlugin {

	@Override
	public void setUp(BaseContext context) {
		// not needed
	}


	@Override
	public void tearDown() {
		// not needed
	}


	@Override
	public Collection<? extends WebeditToolbarItem> getItems() {
		return Collections.<WebeditToolbarItem> singletonList(new ScheduleExecutionWebeditItemsPlugin());
	}

}
