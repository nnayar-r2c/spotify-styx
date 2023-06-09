/*-
 * -\-\-
 * Spotify Styx API Service
 * --
 * Copyright (C) 2016 - 2022 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */

package com.spotify.styx.api.util;

import com.spotify.styx.model.DeploymentSource;
import com.spotify.styx.model.Schedule;
import com.spotify.styx.model.Workflow;
import com.spotify.styx.model.WorkflowConfiguration;
import com.spotify.styx.model.WorkflowId;
import com.spotify.styx.model.WorkflowState;
import com.spotify.styx.model.WorkflowWithState;

import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class CreateWorkflowUtil {

  public static WorkflowState STATE = WorkflowState.builder().enabled(true).build();
  private CreateWorkflowUtil() {
    // no instantiation
  }


  public static WorkflowWithState workflowWithState(Workflow workflow) {
    return WorkflowWithState.create(workflow, STATE);
  }

  public static WorkflowWithState createWorkflowWithType(String id, String type) {
    return workflowWithState(Workflow.create(id, WorkflowConfiguration.builder().id(id).schedule(Schedule.DAYS)
        .deploymentSource(DeploymentSource.builder().source(type).build()).build()));
  }

  public static WorkflowWithState createWorkflowWithTime(String id, Instant instant) {
    return workflowWithState(Workflow.create(id,
        WorkflowConfiguration.builder().id(id).schedule(Schedule.DAYS).deploymentTime(instant).build()));
  }

  public static WorkflowWithState createWorkflowWithTypeAndTime(String id, String type, Instant instant) {
    return workflowWithState(Workflow.create(id,
        WorkflowConfiguration.builder().id(id).schedule(Schedule.DAYS).deploymentTime(instant).deploymentSource(
            DeploymentSource.builder().source(type).build()).build()));
  }

  public static Map<WorkflowId, WorkflowWithState> buildWorkflowMap(WorkflowWithState... workflows){
    return Arrays.stream(workflows).collect(Collectors.toMap(w-> w.workflow().id(), w->w));
  }

}
