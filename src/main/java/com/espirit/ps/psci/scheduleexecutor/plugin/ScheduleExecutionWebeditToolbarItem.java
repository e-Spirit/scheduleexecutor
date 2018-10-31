package com.espirit.ps.psci.scheduleexecutor.plugin;

import java.util.List;
import com.espirit.ps.psci.scheduleexecutor.ScheduleEntryOption;
import com.espirit.ps.psci.scheduleexecutor.ScheduleHelper;
import com.espirit.ps.psci.scheduleexecutor.utils.Resources;
import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.access.editor.value.TargetReference;
import de.espirit.firstspirit.access.schedule.GenerateTask;
import de.espirit.firstspirit.access.schedule.ScheduleEntry;
import de.espirit.firstspirit.access.schedule.ScheduleTask;
import de.espirit.firstspirit.access.store.IDProvider;
import de.espirit.firstspirit.agency.FormsAgent;
import de.espirit.firstspirit.agency.LanguageAgent;
import de.espirit.firstspirit.agency.OperationAgent;
import de.espirit.firstspirit.agency.SpecialistsBroker;
import de.espirit.firstspirit.client.plugin.toolbar.ToolbarContext;
import de.espirit.firstspirit.forms.Form;
import de.espirit.firstspirit.forms.FormData;
import de.espirit.firstspirit.ui.operations.ShowFormDialogOperation;
import de.espirit.firstspirit.webedit.plugin.toolbar.ExecutableToolbarActionsItem;
import de.espirit.firstspirit.webedit.server.ClientScriptOperation;

public class ScheduleExecutionWebeditToolbarItem implements ExecutableToolbarActionsItem {

	private static final String SCRIPT = Resources.asString("/files/com/espirit/ps/psci/scheduleexecutor/execute.js", ScheduleExecutionWebeditToolbarItem.class);
	private static final String GOM_SOURCE = Resources.asString("/files/com/espirit/ps/psci/scheduleexecutor/selectScheduleEntry.form.xml", ScheduleExecutionWebeditToolbarItem.class);
	private static final String STARTNODE_GOM_SOURCE = Resources.asString("/files/com/espirit/ps/psci/scheduleexecutor/selectStartNode.form.xml", ScheduleExecutionWebeditToolbarItem.class);


	@Override
	public boolean isEnabled(ToolbarContext context) {
		return true;
	}


	@Override
	public boolean isVisible(ToolbarContext context) {
		return !ScheduleHelper.getScheduleEntries(context, true).isEmpty();
	}


	@Override
	public String getIconPath(ToolbarContext context) {
		return null;
	}


	@Override
	public String getLabel(ToolbarContext context) {
		return "Auftrag ausführen";
	}


	@Override
	public void execute(ToolbarContext context) {
		OperationAgent operationAgent = context.requireSpecialist(OperationAgent.TYPE);
		FormsAgent formsAgent = context.requireSpecialist(FormsAgent.TYPE);
		Form form = formsAgent.getForm(GOM_SOURCE);
		LanguageAgent languageAgent = context.requireSpecialist(LanguageAgent.TYPE);
		List<Language> languages = languageAgent.getLanguages();
		ShowFormDialogOperation showFormDialogOperation = operationAgent.getOperation(ShowFormDialogOperation.TYPE);
		showFormDialogOperation.setTitle("Auftrag ausführen");
		showFormDialogOperation.setOkText("ausführen");
		FormData formData = showFormDialogOperation.perform(form, languages);
		if (formData == null) {
			return;
		}
		ScheduleEntryOption userSelection = (ScheduleEntryOption) formData.get(null, "scheduleEntry").get();
		ScheduleEntry scheduleEntry = (ScheduleEntry) userSelection.getValue();

		configureStartNodes(context, scheduleEntry);

		ClientScriptOperation clientScriptOperation = operationAgent.getOperation(ClientScriptOperation.TYPE);
		clientScriptOperation.perform(String.format(SCRIPT, scheduleEntry.getId()), true);
	}


	private void configureStartNodes(final SpecialistsBroker broker, final ScheduleEntry scheduleEntry) {
		final List<ScheduleTask> tasks = scheduleEntry.getTasks();
		for (ScheduleTask task : tasks) {
			if (task instanceof GenerateTask && ((GenerateTask) task).getStartnodesDefinableByUser()) {
				final GenerateTask genTask = (GenerateTask) task;

				final FormData startNodeFormData = getStartNode(broker);
				if (startNodeFormData == null) {
					continue;
				}

				final TargetReference startNodeReference = (TargetReference) startNodeFormData.get(null, "startNode").get();
				if (!startNodeReference.isEmpty() && startNodeReference.get().isInReleaseStore()) {
					final List<IDProvider> startNodes = genTask.getStartNodes();
					startNodes.clear();
					startNodes.add(startNodeReference.get());
				}
			}
		}
	}


	private FormData getStartNode(SpecialistsBroker broker) {
		final FormsAgent formsAgent = broker.requireSpecialist(FormsAgent.TYPE);
		final Form startNodeForm = formsAgent.getForm(STARTNODE_GOM_SOURCE);

		final OperationAgent operationAgent = broker.requireSpecialist(OperationAgent.TYPE);
		final ShowFormDialogOperation startNodeFormOperation = operationAgent.getOperation(ShowFormDialogOperation.TYPE);
		startNodeFormOperation.setTitle("Startknoten auswählen");
		startNodeFormOperation.setOkText("Ausführen");

		final List<Language> languages = broker.requireSpecialist(LanguageAgent.TYPE).getLanguages();
		return startNodeFormOperation.perform(startNodeForm, languages);
	}

}
