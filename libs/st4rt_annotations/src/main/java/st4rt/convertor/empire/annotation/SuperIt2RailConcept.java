/* IT2Rail. Contract H2020 - N. 636078
 *
 * Unless required by applicable law or agreed to in writing, this software
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 * See the IT2Rail Consortium Agreement for the specific language governing permissions and
 * limitations of use.
 */

package st4rt.convertor.empire.annotation;

import java.lang.annotation.*;


@Target({ElementType.TYPE}) @Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface SuperIt2RailConcept {
		
		public boolean enabled() default true;
		
}

 
