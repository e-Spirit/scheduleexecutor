package com.espirit.ps.psci.scheduleexecutor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import de.espirit.firstspirit.access.AdminService;
import de.espirit.firstspirit.access.ServicesBroker;
import de.espirit.firstspirit.access.User;
import de.espirit.firstspirit.access.project.Group;
import de.espirit.firstspirit.access.project.Project;
import de.espirit.firstspirit.access.schedule.ExecutionSchedule;
import de.espirit.firstspirit.access.schedule.FixedExecutionSchedule;
import de.espirit.firstspirit.access.schedule.ScheduleEntry;
import de.espirit.firstspirit.access.schedule.ScheduleEntryControl;
import de.espirit.firstspirit.access.schedule.ScheduleEntryRunningException;
import de.espirit.firstspirit.access.schedule.ScheduleEntryState;
import de.espirit.firstspirit.access.schedule.ScheduleStorage;
import de.espirit.firstspirit.access.store.Store.Type;
import de.espirit.firstspirit.agency.ProjectAgent;
import de.espirit.firstspirit.agency.SpecialistsBroker;
import de.espirit.firstspirit.agency.StoreAgent;
import de.espirit.firstspirit.agency.UserAgent;

public class ScheduleHelper {

	private ScheduleHelper() {}


	public static ScheduleEntryState getScheduleEntryState(SpecialistsBroker broker, long fromDate, long executionId) {
		Project project = broker.requireSpecialist(StoreAgent.TYPE).getStore(Type.GLOBALSTORE).getProject();
		ScheduleStorage scheduleStorage = broker.requireSpecialist(ServicesBroker.TYPE).getService(AdminService.class).getScheduleStorage();
		ScheduleEntryState scheduleEntryState = null;
		for (ScheduleEntryControl scheduleEntryControl : scheduleStorage.getHistory(new Date(fromDate), new Date(), Integer.MAX_VALUE, project)) {
			scheduleEntryState = scheduleEntryControl.getState();
			if (scheduleEntryState.getId() == executionId) {
				return scheduleEntryState;
			}
		}
		return null;
	}


	public static ScheduleEntryState execute(ScheduleEntry scheduleEntry) throws ScheduleEntryRunningException {
		ScheduleEntryControl scheduleEntryControl = scheduleEntry.execute();
		scheduleEntryControl.awaitTermination();
		return scheduleEntryControl.getState();
	}


	public static List<ScheduleEntry> getScheduleEntries(SpecialistsBroker broker, boolean showSystemScheduleEntries) {
		List<ScheduleEntry> result = new ArrayList<>();
		AdminService adminService = broker.requireSpecialist(ServicesBroker.TYPE).getService(AdminService.class);
		long projectId = broker.requireSpecialist(ProjectAgent.TYPE).getId();
		Project project = adminService.getProjectStorage().getProject(projectId);
		ScheduleStorage scheduleStorage = adminService.getScheduleStorage();
		User user = broker.requireSpecialist(UserAgent.TYPE).getUser();

		for (ScheduleEntry scheduleEntry : scheduleStorage.getScheduleEntries(project)) {
			if (isNotActive(scheduleEntry) || isHiddenSystemEntry(showSystemScheduleEntries, scheduleEntry)) {
				continue;
			}
			if (isAllowedForManualExecution(scheduleEntry, user)) {
				result.add(scheduleEntry);
			}
		}

		return result;
	}


	public static ScheduleEntry getScheduleEntryById(SpecialistsBroker broker, long scheduleEntryId) {
		AdminService adminService = broker.requireSpecialist(ServicesBroker.TYPE).getService(AdminService.class);
		ScheduleStorage scheduleStorage = adminService.getScheduleStorage();
		return scheduleStorage.getScheduleEntry(scheduleEntryId);
	}


	private static boolean isHiddenSystemEntry(boolean showSystemScheduleEntries, ScheduleEntry scheduleEntry) {
		return !showSystemScheduleEntries && scheduleEntry.isSystem();
	}


	private static boolean isNotActive(ScheduleEntry scheduleEntry) {
		return !scheduleEntry.isActive();
	}


	private static boolean isAllowedForManualExecution(ScheduleEntry scheduleEntry, User user) {
		if (!scheduleEntry.getAllowInteractiveExecution()) {
			return false;
		}

		if (scheduleEntry.getUsers().contains(user)) {
			return true;
		}

		for (Group group : scheduleEntry.getGroups()) {
			// Prüfung ob user in den berechtigten Gruppen ist muss so erfolgen, da externe Gruppen nicht mit aufgelöst werden
			if (group.isMember(user)) {
				return true;
			}
		}

		return scheduleEntry.getExecutionSchedule().getType() == ExecutionSchedule.FIXED && ((FixedExecutionSchedule) scheduleEntry.getExecutionSchedule()).isManual() && scheduleEntry.getUsers().isEmpty() && scheduleEntry.getGroups().isEmpty();
	}

}
