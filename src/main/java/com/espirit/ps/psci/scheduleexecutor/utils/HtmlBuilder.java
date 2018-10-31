package com.espirit.ps.psci.scheduleexecutor.utils;

import java.util.List;
import de.espirit.firstspirit.access.schedule.RunState;
import de.espirit.firstspirit.access.schedule.ScheduleEntryState;
import de.espirit.firstspirit.access.schedule.TaskResult;

public class HtmlBuilder {

	private HtmlBuilder() {}


	public static String getDetailHtml(ScheduleEntryState scheduleEntryState) {
		long fromDate = scheduleEntryState.getStartTime().getTime();
		StringBuilder result = new StringBuilder();
		List<TaskResult> taskResults = scheduleEntryState.getTaskResults();
		if (!taskResults.isEmpty()) {
			result.append("<table width=\\\"100%\\\">");
			result.append(getTableHeadline());
			int taskPosition = 0;
			for (TaskResult taskResult : taskResults) {
				result.append(getTaskResultHtml(taskResult, scheduleEntryState.getId(), fromDate, taskPosition));
				taskPosition++;
			}
			result.append("</table>");
		}
		return result.toString();
	}


	private static String getTableHeadline() {
		StringBuilder result = new StringBuilder();
		result.append("<tr>");
		result.append(getCell("Name", true));
		result.append(getCell("Warnungen", true));
		result.append(getCell("Fehler", true));
		result.append(getCell("Ergebnis", true));
		result.append("</tr>");
		return result.toString();
	}


	private static String getTaskResultHtml(TaskResult taskResult, long scheduleEntryStateId, long fromDate, int taskPositon) {
		StringBuilder result = new StringBuilder();
		result.append("<tr style=\\\"");
		result.append(getRowStyles(taskResult));
		result.append("\\\">");
		result.append(getCell(taskResult.getTask().getName(), false));
		result.append(getCell(taskResult.getWarningCount(), false));
		result.append(getCell(taskResult.getErrorCount() + taskResult.getFatalErrorCount(), false));
		result.append(getCell(getLogLink(scheduleEntryStateId, taskResult.getState().name(), fromDate, taskPositon), false));
		result.append("</tr>");
		return result.toString();
	}


	private static String getLogLink(long scheduleEntryStateId, String linkContent, long fromDate, int taskPosition) {
		StringBuilder result = new StringBuilder();
		result.append("<a href=\\\"#\\\" onClick=\\\"top.CCSE.showLog('");
		result.append(fromDate);
		result.append("','");
		result.append(scheduleEntryStateId);
		result.append("','");
		result.append(taskPosition);
		result.append("');return false;\\\">");
		result.append(linkContent);
		result.append("</a>");
		return result.toString();
	}


	private static String getRowStyles(TaskResult taskResult) {
		RunState runState = taskResult.getState();
		if (runState == RunState.SUCCESS) {
			return "background-color:green;color:white;";
		} else if (runState == RunState.ERROR) {
			return "background-color:red;color:white;";
		} else {
			return "background-color:white;color:black;";
		}
	}


	private static String getCell(Object value, boolean headline) {
		StringBuilder result = new StringBuilder();
		if (headline) {
			result.append("<th>");
		} else {
			result.append("<td>");
		}
		result.append(value);
		if (headline) {
			result.append("</th>");
		} else {
			result.append("</td>");
		}
		return result.toString();
	}
}
