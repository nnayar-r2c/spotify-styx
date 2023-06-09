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

import static com.spotify.styx.api.util.QueryParams.DEPLOYMENT_TIME_AFTER;
import static com.spotify.styx.api.util.QueryParams.DEPLOYMENT_TIME_BEFORE;
import static com.spotify.styx.api.util.QueryParams.DEPLOYMENT_TYPE;
import static java.util.stream.Collectors.toList;

import com.spotify.styx.model.Workflow;
import com.spotify.styx.model.WorkflowConfiguration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import com.spotify.styx.model.WorkflowWithState;
import joptsimple.internal.Strings;

public final class WorkflowFiltering {

  private WorkflowFiltering(){}

  public static Collection<WorkflowWithState> filterWorkflows(
          Collection<WorkflowWithState> workflowWithStates, Map<QueryParams, String> paramFilters){
    if (paramFilters.isEmpty()) {
      // Nothing to filter on
      return workflowWithStates;
    }
    List<Predicate> workflowFilters = createWorkflowFilters(paramFilters);

    return workflowWithStates.stream()
        .filter(w -> workflowFilters.stream()
            .allMatch(pre-> pre.test(w.workflow())))
        .collect(toList());
  }

  private static List<Predicate> createWorkflowFilters(Map<QueryParams, String> paramFilters){
    List<Predicate> predicates = new ArrayList<>();

    String deploymentType = paramFilters.get(DEPLOYMENT_TYPE);

    if(!Strings.isNullOrEmpty(deploymentType)) {
      Predicate<Workflow> isWorkflowDeploymentType = workflow -> {
        WorkflowConfiguration workflowConfiguration = workflow.configuration();
        return workflowConfiguration.deploymentSource()
           .map(source -> Objects.equals(source.source(), deploymentType))
            .orElse(false);

      };
      predicates.add(isWorkflowDeploymentType);
    }

    String deploymentTimeBefore = paramFilters.get(DEPLOYMENT_TIME_BEFORE);

    if(!Strings.isNullOrEmpty(deploymentTimeBefore)){
      Predicate<Workflow> workflowDeploymentTimeBefore = workflow -> {
        WorkflowConfiguration workflowConfiguration = workflow.configuration();
        if(workflowConfiguration.deploymentTime().isEmpty()){
          return false;
        }
        Instant deploymentTime = workflowConfiguration.deploymentTime().get();
        Instant deploymentTimeBeforeInstant = Instant.parse(deploymentTimeBefore);
        return deploymentTime.isBefore(deploymentTimeBeforeInstant);
      };
      predicates.add(workflowDeploymentTimeBefore);
    }

    String deploymentTimeAfter = paramFilters.get(DEPLOYMENT_TIME_AFTER);

    if(!Strings.isNullOrEmpty(deploymentTimeAfter)){
      Predicate<Workflow> workflowDeploymentTimeBefore = workflow -> {
        WorkflowConfiguration workflowConfiguration = workflow.configuration();
        if(workflowConfiguration.deploymentTime().isEmpty()){
          return false;
        }
        Instant deploymentTime = workflowConfiguration.deploymentTime().get();
        Instant deploymentTimeAfterInstant = Instant.parse(deploymentTimeAfter);
        return deploymentTime.isAfter(deploymentTimeAfterInstant);
      };
      predicates.add(workflowDeploymentTimeBefore);
    }

    return predicates;
  }
}
