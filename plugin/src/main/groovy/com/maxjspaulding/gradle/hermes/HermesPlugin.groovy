package com.maxjspaulding.gradle.hermes


import com.maxjspaulding.gradle.hermes.HermesPluginExtension

import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.BuildListener
import org.gradle.BuildResult


class HermesPlugin implements Plugin<Project> {

	void apply(Project project) {
		if(project.gradle.extensions.hasProperty('hermes') == false){
			project.gradle.extensions.create("hermes", HermesPluginExtension)
		}
		
		Gradle gradle = project.getGradle();
		println "publishKey:${gradle.hermes.publishKey}";

		// Add pre and post processing of tasks.
		TaskExecutionGraph taskGraph = project.gradle.getTaskGraph();
		taskGraph.beforeTask { task ->
			if(gradle.hermes.reportTaskStarts && processTask(gradle, gradle.hermes.preprocessTasks, task)){
				gradle.hermes.notify(gradle.hermes.createTaskJson(task.getName(), HermesPluginExtension.EVENT_TASK_PREPROCESSING, NULL));
			}
		}
		
		taskGraph.afterTask { task, taskState ->
			if(gradle.hermes.reportTaskCompletions && processTask(gradle, gradle.hermes.postprocessTasks, task)){
				if (taskState.failure) {
					gradle.hermes.notify(gradle.hermes.createTaskJson(task.getName(), HermesPluginExtension.EVENT_TASK_POSTPROCESSING, HermesPluginExtension.STATE_FAILED));
				}else {
					gradle.hermes.notify(gradle.hermes.createTaskJson(task.getName(), HermesPluginExtension.EVENT_TASK_POSTPROCESSING, HermesPluginExtension.STATE_SUCCEEDED));
				}
			}
		}
	}

	boolean processTask(Gradle gradle, List taskList, Task task){
		if(gradle.hermes.filterTasks == false)
			return true;
		if(gradle.hasProperty('hermes'))
			return taskList.contains(task.name);
		return false;
	}
}