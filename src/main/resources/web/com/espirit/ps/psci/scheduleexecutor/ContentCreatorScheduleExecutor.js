CCSE = {
	dialogResult : undefined,
	dialogLog : undefined,
	dialogOpen : false,
	dialogLogElement : undefined,
	dialogResultElement : undefined,
	resultHtml : undefined,
	offset : undefined,
	newLineExp : new RegExp("(^INFO|^WARN|^ERROR)"),
	showResult : function(html) {
		CCSE.resultHtml = html;
		CCSE.dialogResult = WE_API.Common.createDialog();
		CCSE.dialogResult.setSize(600, 300);
		CCSE.dialogResult.setTitle("Auftrags-Details");
		CCSE.dialogResultElement = document.createElement("DIV");
		CCSE.dialogResultElement.innerHTML = html;
		document.body.appendChild(CCSE.dialogResultElement);
		CCSE.dialogResult.setContent(CCSE.dialogResultElement);
		CCSE.dialogResult.addButton("Schließen", function() {
			CCSE.dialogResult.hide();
			CCSE.dialogResult = undefined;
			CCSE.resultHtml = undefined;
		});
		CCSE.dialogResult.show();
	},
	showLog : function(fromDate, scheduleEntryId, taskPosition) {
		if (CCSE.dialogLog !== undefined) {
			return;
		}
		;
		if (CCSE.dialogResult !== undefined) {
			CCSE.dialogResult.hide();
		}

		CCSE.fromDate = fromDate;
		CCSE.scheduleEntryId = scheduleEntryId;
		CCSE.taskPosition = taskPosition;

		CCSE.offset = 0;
		CCSE.dialogOpen = true;
		CCSE.readLines(CCSE.createDialog);
	},
	readLines : function(callback) {
		top.WE_API.Common.execute(
				"class:com.espirit.ps.psci.schedulestarter.plugin.ShowLogFile",
				{
					"fromDate" : CCSE.fromDate,
					"scheduleEntryId" : CCSE.scheduleEntryId,
					"taskPosition" : CCSE.taskPosition,
					"lines" : "100",
					"offset" : "" + CCSE.offset
				}, callback);
	},
	loadNextLines : function(e) {
		var el = e.target;
		if (el.scrollHeight - el.offsetHeight - el.scrollTop < 30) {
			CCSE.readLines(CCSE.addLines);
		}
	},
	createDialog : function(logLines) {
		CCSE.dialogLog = WE_API.Common.createDialog();
		CCSE.dialogLog.setSize(600, 300);
		CCSE.dialogLog.setTitle("Logfile");
		CCSE.dialogElement = document.createElement("DIV");
		CCSE.dialogElement.className = "log";

		CCSE.dialogElement.addEventListener('scroll', CCSE.loadNextLines);

		document.body.appendChild(CCSE.dialogElement);
		CCSE.dialogLog.setContent(CCSE.dialogElement);
		CCSE.dialogLog.addButton("Schließen", function() {
			CCSE.dialogLog.hide();
			CCSE.dialogLog = undefined;
			CCSE.dialogElement = undefined;
			CCSE.offset = 0;
			if (CCSE.resultHtml !== undefined) {
				CCSE.showResult(CCSE.resultHtml);
			}
		});
		CCSE.dialogLog.show();
		CCSE.addLines(logLines);
	},
	addLines : function(logLines) {
		if (logLines === null) {
			CCSE.dialogElement
					.removeEventListener('scroll', CCSE.loadNextLines);
			return;
		}
		for (var i = 0; i < logLines.length; i++) {
			CCSE.offset++;
			CCSE.writeLogLine(logLines[i]);
		}
	},
	writeLogLine : function(line) {
		if (line.trim() === "") {
			retrun;
		}
		if (line.match(CCSE.newLineExp)) {
			lineElement = document.createElement("div");

			if (line.indexOf("\n") > 0) {
				lineElement.className = "rowClose"
				var expandNode = document.createElement("span");
				expandNode.appendChild(document.createTextNode("[+] "));
				expandNode.onclick = CCSE.expandHandler;
				lineElement.appendChild(expandNode);
			} else {
				lineElement.className = "rowOpen"
			}

			var match = CCSE.newLineExp.exec(line);
			logLevelElement = document.createElement("span");
			logLevelElement.className = match[0].toLowerCase();
			logLevelElement.appendChild(document.createTextNode(match[0]));
			lineElement.appendChild(logLevelElement);
			line = line.replace(CCSE.newLineExp, "");

			if (line.indexOf("\n") > 0) {
				for (i = 0; i < line.split("\n").length; i++) {
					if (i > 0) {
						var lineBreak = document.createElement("BR");
						lineElement.appendChild(lineBreak);
					}
					var logMessage = document
							.createTextNode(line.split("\n")[i]);
					lineElement.appendChild(logMessage);
				}
			} else {
				var logMessage = document.createTextNode(line);
				lineElement.appendChild(logMessage);
			}

			CCSE.dialogElement.appendChild(lineElement);
		}
	},
	expandHandler : function(element) {
		if (element.currentTarget.parentElement.className === "rowClose") {
			element.currentTarget.parentElement.className = "rowOpen";
		} else {
			element.currentTarget.parentElement.className = "rowClose";
		}
		return false;
	}
}