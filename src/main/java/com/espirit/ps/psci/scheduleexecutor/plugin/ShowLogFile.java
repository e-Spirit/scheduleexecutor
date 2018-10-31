package com.espirit.ps.psci.scheduleexecutor.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import com.espirit.ps.psci.scheduleexecutor.ScheduleHelper;
import de.espirit.common.base.Logging;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.access.schedule.ScheduleEntryState;
import de.espirit.firstspirit.access.schedule.TaskResult;
import de.espirit.firstspirit.access.script.Executable;

public class ShowLogFile implements Executable {

	@Override
	public Object execute(Map<String, Object> paramMap) {
		BaseContext context = (BaseContext) paramMap.get("context");
		long fromDate = Long.parseLong((String) paramMap.get("fromDate"));
		long executionId = Long.parseLong((String) paramMap.get("scheduleEntryId"));
		int taskPosition = Integer.parseInt((String) paramMap.get("taskPosition"));
		int lines = Integer.parseInt((String) paramMap.get("lines"));
		int offset = Integer.parseInt((String) paramMap.get("offset"));
		ScheduleEntryState scheduleEntryState = ScheduleHelper.getScheduleEntryState(context, fromDate, executionId);
		if (scheduleEntryState == null) {
			return null;
		}
		List<TaskResult> taskResults = scheduleEntryState.getTaskResults();
		TaskResult taskResult = taskResults.get(taskPosition);
		List<String> logFile = readLogFile(taskResult);
		if (offset > logFile.size()) {
			return null;
		}
		if (offset + lines > logFile.size()) {
			lines = logFile.size() - offset;
		}
		return logFile.subList(offset, offset + lines);
	}


	private List<String> readLogFile(TaskResult taskResult) {
		List<String> result = new ArrayList<>();
		String logFile = null;
		try (InputStream logfileInputStream = taskResult.getLogfile(); Scanner scanner = new Scanner(logfileInputStream)) {
			scanner.useDelimiter("\\A");
			logFile = scanner.hasNext() ? scanner.next() : "";
			String[] logFileLines = logFile.split("\\n");
			for (String line : logFileLines) {
				if (line.startsWith("INFO") || line.startsWith("WARN") || line.startsWith("ERROR")) {
					result.add(line);
				} else if (!result.isEmpty()) {
					result.set(result.size() - 1, result.get(result.size() - 1) + "\n" + line);
				}
			}
		} catch (IOException e) {
			Logging.logError(String.format("error while reading logfile [task: %s]", taskResult.getTask().getName()), e, getClass());
			throw new ReadingLogError(e);
		}
		return result;
	}


	@Override
	public Object execute(Map<String, Object> paramMap, Writer arg1, Writer arg2) {
		return execute(paramMap);
	}

}
