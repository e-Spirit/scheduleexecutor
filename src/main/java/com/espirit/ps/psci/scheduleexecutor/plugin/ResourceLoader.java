package com.espirit.ps.psci.scheduleexecutor.plugin;

import java.util.Collections;
import java.util.List;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.webedit.plugin.ClientResourcePlugin;

public class ResourceLoader implements ClientResourcePlugin {

	@Override
	public void setUp(BaseContext context) {
		// do nothing
	}


	@Override
	public void tearDown() {
		// do nothing
	}


	@Override
	public List<String> getScriptUrls() {
		return Collections.singletonList("com/espirit/ps/psci/scheduleexecutor/ContentCreatorScheduleExecutor.js");
	}


	@Override
	public List<String> getStylesheetUrls() {
		return Collections.singletonList("com/espirit/ps/psci/scheduleexecutor/ContentCreatorScheduleExecutor.css");
	}

}
