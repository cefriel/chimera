/* IT2Rail. Contract H2020 - N. 636078
 *
 * Unless required by applicable law or agreed to in writing, this software
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 * See the IT2Rail Consortium Agreement for the specific language governing permissions and
 * limitations of use.
 */

package eu.st4rt.converter.org.it2rail.empire.rdf4jdatasouce;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryException;

import eu.st4rt.converter.empire.QueryFactory;
import eu.st4rt.converter.empire.ds.Alias;
import eu.st4rt.converter.empire.ds.DataSource;
import eu.st4rt.converter.empire.ds.DataSourceException;
import eu.st4rt.converter.empire.ds.DataSourceFactory;
import eu.st4rt.converter.empire.sesame.RepositoryDataSourceFactory;
import eu.st4rt.converter.empire.util.Repositories2;

import java.util.Map;


/* Create a Memory Repository to be used as "scracth" memory to support triples-to-pojo
 * mappings by the RdfGenerator
 */
@Alias(RepositoryDataSourceFactory.ALIAS)
public final class InMemoryRepositoryDataSourceFactory implements DataSourceFactory, InMemoryRepositoryFactoryKeys {
	
	 @Override
		public boolean canCreate(final Map<String, Object> theMap) {
			return true;
	}
   
	 
	 
	@Override
	public DataSource create(Map<String, Object> theMap)
			throws DataSourceException {
		if (!canCreate(theMap)) {
			throw new DataSourceException("Invalid configuration map: " + theMap);
		}
		return null;
	}



	public static InMemoryRepositoryDataSource create(QueryFactory qFactory) throws DataSourceException {
				

		Repository aRepository;

		try {
			aRepository = Repositories2.createInMemoryRepo();
			//aRepository = new SailRepository(new MemoryStore());
          //  aRepository.initialize();
			return new InMemoryRepositoryDataSource(aRepository,qFactory);
		}
		catch (RepositoryException e) {
			throw new DataSourceException(e);
		}
	}

	
}
