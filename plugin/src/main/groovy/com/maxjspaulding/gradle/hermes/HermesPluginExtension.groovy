package com.maxjspaulding.gradle.hermes

import com.pubnub.api.*
import java.text.SimpleDateFormat;
import org.json.JSONObject;

class HermesPluginExtension {
	def Pubnub pubnub = null;
	def SimpleDateFormat dateFormatter = new SimpleDateFormat();
	
	def String project = null;
	def String id = null;

	def String publishKey = "demo";
	def String subscribeKey = 'demo';
	def String channel = 'hermes-gradle';

	def boolean reportTaskStarts = false;
	def boolean reportTaskCompletions = false;

	def boolean reportBuildStarted = false;
	def boolean reportProjectsEvaluated = false;
	def boolean reportProjectsLoaded = false;
	def boolean reportSettingsEvaluated = false;
	def boolean reportBuildFinished = true;

	def boolean filterTasks = false;
	def preprocessTasks = [];
	def postprocessTasks = [];
	
	public static final int EVENT_BUILD_STARTED = 0;
	public static final int EVENT_PROJECTS_EVALUATED = 1;
	public static final int EVENT_PROJECTS_LOADED = 2;
	public static final int EVENT_SETTINGS_EVALUATED = 3;
	public static final int EVENT_BUILD_FINISHED = 4;
	public static final int EVENT_TASK_PREPROCESSING = 5;
	public static final int EVENT_TASK_POSTPROCESSING = 6;

	public static final int STATE_FAILED = 0;
	public static final int STATE_SUCCEEDED = 1;

	public JSONObject createEventJson(final Integer event, final Integer state){
		JSONObject jsonObject = new JSONObject();

		if(project != null)
			jsonObject.put("project", project);

		if(id != null)
			jsonObject.put("id", id);

		if(event != null)
			jsonObject.put("event", event);

		if(state != null)
			jsonObject.put("state", state);

		jsonObject.put("date", dateFormatter.format(new Date()));

		return jsonObject;
	}
	
	public JSONObject createTaskJson(final String task, final Integer event,
	                                 final Integer state){
		JSONObject jsonObject = createEventJson(event, state);

		if(task != null)
			jsonObject.put("task", task);

		return jsonObject;
	}
	
	public void notify(final JSONObject message){
		if(pubnub == null){
			pubnub = new Pubnub(publishKey, subscribeKey);
		}
		boolean wait = true;
		Callback callback = new Callback() {
			public void successCallback(String channel, Object response) {
				println "PUBNUB: ${channel} notified!";
				wait = false;
			}
			public void errorCallback(String channel, PubnubError error) {
				println "PUBNUB: Failed to notify ${channel}";
				wait = false;
			}
		};

		pubnub.publish(channel, message , callback);

		// Evil busy-wait loop, so Gradle doesn't kill the JVM before
		// the asynchronous pubnub call completes.
		while(wait){
			sleep(1);
		}
	}

}