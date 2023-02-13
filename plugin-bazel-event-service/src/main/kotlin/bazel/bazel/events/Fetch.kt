/*
 * Copyright 2000-2023 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package bazel.bazel.events

// Payload of an event indicating that an external resource was fetched. This
// event will only occur in streams where an actual fetch happened, not in ones
// where a cached copy of the entity to be fetched was used.

data class Fetch(
        override val id: Id,
        override val children: List<Id>,
        val url: String,
        val success: Boolean) : BazelContent