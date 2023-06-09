/*-
 * -\-\-
 * Spotify Styx Flyte Client
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

package com.spotify.styx.flyte.client;

import static com.spotify.styx.flyte.client.RpcHelper.getExecutionsListFilter;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import org.junit.Test;

public class RpcHelperTest {


  @Test
  public void testGtExecutionsListFilter() {
    final Instant someTime = LocalDateTime.of(2022, 2, 2, 12, 12, 5)
        .atZone(ZoneOffset.UTC)
        .toInstant();

    final String executionsListFilter = getExecutionsListFilter(
        someTime,
        Duration.of(24, ChronoUnit.HOURS),
        Duration.of(3, ChronoUnit.MINUTES));

    assertThat(executionsListFilter,
        equalTo("value_in(phase,RUNNING)+gte(execution_created_at,2022-02-01T12:12:05)+lte(execution_created_at,"
                + "2022-02-02T12:09:05)"));

  }
}
