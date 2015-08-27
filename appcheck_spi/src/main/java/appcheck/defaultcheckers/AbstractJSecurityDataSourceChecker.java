package appcheck.defaultcheckers;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.metamodel.EntityType;

import appcheck.ComponentChecker;

/**
 * Checa se o datasource do jSecurity esta funcionando.
 * @author bruno.carneiro
 *
 */
public abstract class AbstractJSecurityDataSourceChecker extends AbstractJPAChecker implements ComponentChecker {

	EntityManager em;
	
	public void check(PrintWriter out) throws Exception {

		out.println("	Vai obter EntityManager para persistenceUnit: jsecurityPlc");
		EntityManagerFactory emFactory=Persistence.createEntityManagerFactory("jsecurityPlc");
		em = emFactory.createEntityManager();
		out.println("	Propriedade do EntityManager declaradas no persistence: "+em.getProperties());
		
		testaMapeamento(out);
	}
	
	public void testaMapeamento(PrintWriter out) {

		Set<?> entidades = getEmDefault().getMetamodel().getEntities();

		for (Iterator<?> iterator = entidades.iterator(); iterator.hasNext();) {

			EntityType<?> entidadeTipo = (EntityType<?>) iterator.next();
			try {
				if(!"DefaultRevisionEntity".equals(entidadeTipo.getName())) {
					out.println("	Testando entidade: " + entidadeTipo.getName() +" - from " + entidadeTipo.getName());
					String query = "from " + entidadeTipo.getName();
					getEmDefault().createQuery(query).getResultList();
					out.println("	Ok!");
				}
			} catch (Exception e) {
				out.println("	Erro!");
				e.printStackTrace(out);
			}
		}
	}
	
	
	@Override
	public EntityManager getEmDefault() {
		return em;
	}

	public String getDescription() {
		return "AbstractJSecurityDataSourceChecker - Checa se o datasource do jSecurity esta configurado corretamente.";
	}
	
}
