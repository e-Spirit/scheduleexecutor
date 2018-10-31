package com.espirit.ps.psci.scheduleexecutor.plugin;

import java.io.Writer;
import java.util.Map;
import com.espirit.ps.psci.scheduleexecutor.ScheduleHelper;
import com.espirit.ps.psci.scheduleexecutor.utils.HtmlBuilder;
import de.espirit.common.base.Logging;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.access.schedule.RunState;
import de.espirit.firstspirit.access.schedule.ScheduleEntry;
import de.espirit.firstspirit.access.schedule.ScheduleEntryRunningException;
import de.espirit.firstspirit.access.schedule.ScheduleEntryState;
import de.espirit.firstspirit.access.script.Executable;
import de.espirit.firstspirit.agency.OperationAgent;
import de.espirit.firstspirit.ui.operations.RequestOperation;
import de.espirit.firstspirit.ui.operations.RequestOperation.Answer;
import de.espirit.firstspirit.webedit.server.ClientScriptOperation;

public class ExecuteScheduleEntry implements Executable {

	@Override
	public Object execute(Map<String, Object> paramMap) {
		BaseContext context = (BaseContext) paramMap.get("context");
		long scheduleEntryId = Long.parseLong((String) paramMap.get("scheduleEntryId"));

		ScheduleEntry scheduleEntry = ScheduleHelper.getScheduleEntryById(context, scheduleEntryId);

		try {
			ScheduleEntryState scheduleEntryState = ScheduleHelper.execute(scheduleEntry);

			OperationAgent operationAgent = context.requireSpecialist(OperationAgent.TYPE);
			RequestOperation requestOperation = operationAgent.getOperation(RequestOperation.TYPE);
			requestOperation.setTitle("Auftrag ausgeführt");
			requestOperation.addOk();
			Answer showDetails = requestOperation.addAnswer("details anzeigen");
			RunState runState = scheduleEntryState.getState();
			if (requestOperation.perform(getResultMessage(runState)) == showDetails) {
				showDetails(operationAgent, scheduleEntryState);
			}
		} catch (ScheduleEntryRunningException e) {
			Logging.logError("fehler", e, getClass());
		}
		return null;
	}


	private String getResultMessage(RunState runState) {
		if (runState == RunState.SUCCESS) {
			return "wurde erfolgreich ausgeführt";
		} else if (runState == RunState.FINISHED_WITH_ERRORS) {
			return "wurde mit fehlern beendet";
		} else if (runState == RunState.ERROR) {
			return "enthielt fehler";
		} else {
			return "unbekannt beendet";
		}
	}


	private void showDetails(OperationAgent operationAgent, ScheduleEntryState scheduleEntryState) {
		String detailHtml = HtmlBuilder.getDetailHtml(scheduleEntryState);
		ClientScriptOperation clientScriptOperation = operationAgent.getOperation(ClientScriptOperation.TYPE);
		clientScriptOperation.perform(String.format("top.CCSE.showResult(\"%s\")", detailHtml), true);
	}


	@Override
	public Object execute(Map<String, Object> paramMap, Writer arg1, Writer arg2) {
		return execute(paramMap);
	}

}
