package eu.st4rt.converter.empire.util;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

/**
 * @author  Fernando Hernandez
 * @since   0.8.7
 * @version 0.8.7
 */
public class Repositories2 {

	private Repositories2() {
		throw new AssertionError();
	}

	/**
	 * Create a simple in-memory {@link Repository} which is already initialized
	 *
	 * @return an in memory Repository
	 */
	public static Repository createInMemoryRepo() {
		try {
			Repository aRepo = new SailRepository(new MemoryStore());

			aRepo.initialize();

			return aRepo;
		}
		catch (RepositoryException e) {
			// impossible?
			throw new AssertionError(e);
		}
	}
}
