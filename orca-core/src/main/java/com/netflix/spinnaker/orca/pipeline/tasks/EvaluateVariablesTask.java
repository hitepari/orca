/*
 * Copyright 2018 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.orca.pipeline.tasks;

import com.google.common.base.Strings;
import com.netflix.spinnaker.orca.api.pipeline.Task;
import com.netflix.spinnaker.orca.api.pipeline.TaskResult;
import com.netflix.spinnaker.orca.api.pipeline.models.ExecutionStatus;
import com.netflix.spinnaker.orca.api.pipeline.models.StageExecution;
import com.netflix.spinnaker.orca.pipeline.EvaluateVariablesStage;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Copies previously evaluated expressions to the outputs map for consumption by subsequent stages.
 * The variables aren't evaluated here because would've been evaluated already by a call to e.g.
 * ExpressionAware.Stage#withMergedContext
 */
@Component
public class EvaluateVariablesTask implements Task {

  @Autowired
  public EvaluateVariablesTask() {}

  @Nonnull
  @Override
  public TaskResult execute(@Nonnull StageExecution stage) {
    EvaluateVariablesStage.EvaluateVariablesStageContext context =
        stage.mapTo(EvaluateVariablesStage.EvaluateVariablesStageContext.class);

    Map<String, Object> outputs = new HashMap<>();
    for (EvaluateVariablesStage.Variable v : context.getVariables()) {
      if (!Strings.isNullOrEmpty(v.getKey())) {
        outputs.put(v.getKey(), v.getValue());
      }
    }

    return TaskResult.builder(ExecutionStatus.SUCCEEDED)
        .context(stage.mapTo(Map.class))
        .outputs(outputs)
        .build();
  }
}
