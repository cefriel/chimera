/*
 * Copyright (c) 2005-2013 Clark & Parsia, LLC. <http://www.clarkparsia.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.complexible.common.base;

/**
 * <p>The type of a {@link Change}.  Concrete change types should be enums; this is enforced by
 * {@link Change#of(Enum, Object) Change creation} and is in place to promote the serilizability of
 * ChangeType objects, and indirectly, Changes.</p>
 *
 * @author  Michael Grove
 * @since   3.0
 * @version 3.0
 */
public interface ChangeType {
}
