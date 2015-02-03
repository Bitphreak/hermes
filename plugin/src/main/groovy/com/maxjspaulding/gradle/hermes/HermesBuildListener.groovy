package com.maxjspaulding.gradle.hermes

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.BuildListener
import org.gradle.BuildResult

public class HermesBuildListener implements BuildListener {
	void buildStarted(Gradle gradle){
		if(gradle.extensions.hermes.reportBuildStarted){
			Project project = gradle.getRootProject();
			gradle.extensions.hermes.notify(gradle.extensions.hermes.createEventJson(HermesPluginExtension.EVENT_BUILD_STARTED, NULL));
		}
	}
	
	void projectsEvaluated(Gradle gradle){
		if(gradle.extensions.hermes.reportProjectsEvaluated){
			Project project = gradle.getRootProject();
			gradle.extensions.hermes.notify(gradle.extensions.hermes.createEventJson(HermesPluginExtension.EVENT_PROJECTS_EVALUATED, NULL));
		}
	}
	
	void projectsLoaded(Gradle gradle){
		if(gradle.extensions.hermes.reportProjectsLoaded){
			Project project = gradle.getRootProject();
			gradle.extensions.hermes.notify(gradle.extensions.hermes.createEventJson(HermesPluginExtension.EVENT_PROJECTS_LOADED, NULL));
		}
	}
	
	void settingsEvaluated(Settings settings){
		Gradle gradle = settings.getGradle();
		if(gradle.extensions.hermes.reportSettingsEvaluated){
			Project project = gradle.getRootProject();
			gradle.extensions.hermes.notify(gradle.extensions.hermes.createEventJson(HermesPluginExtension.EVENT_SETTINGS_EVALUATED, NULL));
		}
	}
	
	void buildFinished(BuildResult buildResult){
		Gradle gradle = buildResult.getGradle();
		if(gradle.extensions.hermes.reportBuildFinished == true){
			Project project = gradle.getRootProject();
			if(buildResult.failure){
				gradle.extensions.hermes.notify(gradle.extensions.hermes.createEventJson(HermesPluginExtension.EVENT_BUILD_FINISHED, HermesPluginExtension.STATE_FAILED));
			} else {
				gradle.extensions.hermes.notify(gradle.extensions.hermes.createEventJson(HermesPluginExtension.EVENT_BUILD_FINISHED, HermesPluginExtension.STATE_SUCCEEDED));
			}
		}
	}
}
