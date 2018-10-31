package com.espirit.ps.psci.scheduleexecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.espirit.firstspirit.access.schedule.ScheduleEntry;
import de.espirit.firstspirit.access.store.templatestore.gom.GomIncludeValueProvider;
import de.espirit.firstspirit.access.store.templatestore.gom.Parameterizable;
import de.espirit.firstspirit.agency.SpecialistsBroker;

public class ScheduleEntryValueProvider implements GomIncludeValueProvider<ScheduleEntryOption>, Parameterizable {

	private static final String PARAM_SHOW_SYSTEM_SCHEDULE_ENTRIES = "showSystemScheduleEntries";
	private Map<String, String> paramMap = new HashMap<>();


	@Override
	public String getKey(ScheduleEntryOption option) {
		return option.getKey();
	}


	@Override
	public Class<ScheduleEntryOption> getType() {
		return ScheduleEntryOption.class;
	}


	@Override
	public List<ScheduleEntryOption> getValues(SpecialistsBroker broker) {
		List<ScheduleEntryOption> result = new ArrayList<>();

		boolean showSystemScheduleEntries = Boolean.parseBoolean(paramMap.get(PARAM_SHOW_SYSTEM_SCHEDULE_ENTRIES));
		for (ScheduleEntry scheduleEntry : ScheduleHelper.getScheduleEntries(broker, showSystemScheduleEntries)) {
			result.add(new ScheduleEntryOption(scheduleEntry));
		}

		return result;
	}


	@Override
	public void setParameters(Map<String, String> paramMap) {
		this.paramMap.putAll(paramMap);
	}

}
