package com.espirit.ps.psci.scheduleexecutor;

import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.access.editor.value.Option;
import de.espirit.firstspirit.access.schedule.ScheduleEntry;

public class ScheduleEntryOption implements Option {

	private final long id;
	private final String name;
	private final ScheduleEntry value;


	public ScheduleEntryOption(ScheduleEntry scheduleEntry) {
		name = scheduleEntry.getName();
		id = scheduleEntry.getId();
		value = scheduleEntry;
	}


	@Override
	public int compareTo(Option o) {
		return getKey().compareTo(o.getKey());
	}


	@Override
	public String getLabel(Language language) {
		return name;
	}


	@Override
	public String getLabel(String languageAbbreviation) {
		return name;
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
		return Long.toString(id);
	}


	@Override
	public Object getValue() {
		return value;
	}


	@Override
	public boolean ignore(Language language) {
		return false;
	}


	// muss implementiert werden, um den Vergleich ob ein gültiger wert im formular vorhanden ist.
	// Es reicht an dieser stelle nicht die standart implementierung von Options zu nutzen
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ScheduleEntryOption)) {
			return super.equals(obj);
		}
		return name.equals(((ScheduleEntryOption) obj).name);
	}


	@Override
	public int hashCode() {
		return name.hashCode();
	}

}
