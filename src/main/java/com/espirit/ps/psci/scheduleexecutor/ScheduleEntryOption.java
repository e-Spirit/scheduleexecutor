package com.espirit.ps.psci.scheduleexecutor;

import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.access.editor.value.Option;
import de.espirit.firstspirit.access.schedule.ScheduleEntry;

public class ScheduleEntryOption implements Option {

	private ScheduleEntry scheduleEntry;


	public ScheduleEntryOption(ScheduleEntry scheduleEntry) {
		this.scheduleEntry = scheduleEntry;
	}


	@Override
	public int compareTo(Option o) {
		return getKey().compareTo(o.getKey());
	}


	@Override
	public String getLabel(Language language) {
		return scheduleEntry.getName();
	}


	@Override
	public String getLabel(String languageAbbreviation) {
		return scheduleEntry.getName();
	}


	@Override
	public void putLabel(Language language, String name) {
		// not needed scheduleentries have no language-depended names
	}


	@Override
	public void putLabel(String languageAbbreviation, String name) {
		// not needed scheduleentries have no language-depended names
	}


	@Override
	public String getKey() {
		return Long.toString(scheduleEntry.getId());
	}


	@Override
	public Object getValue() {
		return scheduleEntry;
	}


	@Override
	public boolean ignore(Language language) {
		return false;
	}


	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ScheduleEntryOption)) {
			super.equals(obj);
		}
		return obj != null && scheduleEntry.equals(((ScheduleEntryOption) obj).scheduleEntry);
	}


	@Override
	public int hashCode() {
		return scheduleEntry.hashCode();
	}
}
