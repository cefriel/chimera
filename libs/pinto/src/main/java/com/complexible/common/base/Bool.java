/*
 * Copyright (c) 2005-2011 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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
 * Three-valued (true, false, unknown) boolean type. The standard boolean operators are supported. The following truth
 * table gives the results the unary negation operator:
 * <pre>
 *    not
 * T   F
 * F   T
 * U   U
 * </pre>
 * The truth table for boolean operators is as follows:
 * <pre>
 *       and or
 * T  T   T   T
 * T  F   F   T
 * T  U   U   T
 * F  F   F   F
 * F  U   U   U
 * U  U   U   U
 * </pre>
 *
 * @author Evren Sirin
 * @since 2.0
 * @version 2.0
 */
public enum Bool {
	FALSE {
		public Bool and(Bool other) {
			return (other == UNKNOWN) ? UNKNOWN : FALSE;
		}

		public boolean isFalse() {
			return true;
		}

		public boolean isKnown() {
			return true;
		}

		public boolean isTrue() {
			return false;
		}

		public Bool not() {
			return TRUE;
		}

		public Bool or(Bool other) {
			return other;
		}
	},
	TRUE {
		public Bool and(Bool other) {
			return other;
		}

		public boolean isFalse() {
			return false;
		}

		public boolean isKnown() {
			return true;
		}

		public boolean isTrue() {
			return true;
		}

		public Bool not() {
			return FALSE;
		}

		public Bool or(Bool other) {
			return (other == UNKNOWN) ? UNKNOWN : FALSE;
		}
	},
	UNKNOWN {
		public Bool and(Bool other) {
			return UNKNOWN;
		}

		public boolean isFalse() {
			return false;
		}

		public boolean isKnown() {
			return false;
		}

		public boolean isTrue() {
			return false;
		}

		public Bool not() {
			return UNKNOWN;
		}

		public Bool or(Bool other) {
			return UNKNOWN;
		}
	};

	public static Bool valueOf(boolean value) {
		return value ? TRUE : FALSE;
	}

	public abstract Bool and(Bool other);

	public abstract boolean isFalse();

	public abstract boolean isKnown();

	public abstract boolean isTrue();

	public abstract Bool not();

	public abstract Bool or(Bool other);
}
