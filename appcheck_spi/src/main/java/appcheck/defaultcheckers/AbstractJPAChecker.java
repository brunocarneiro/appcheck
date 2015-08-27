package appcheck.defaultcheckers;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;

/**
 * Checa se o JPA e Banco de dados estao ok.
 * 
 * @author bruno.carneiro
 * 
 */
public abstract class AbstractJPAChecker {

	public void check(PrintWriter out) throws Exception {
		testaMapeamento(out);
	}

	public void testaMapeamento(PrintWriter out) {

		Set<?> entidades = getEmDefault().getMetamodel().getEntities();

		for (Iterator<?> iterator = entidades.iterator(); iterator.hasNext();) {

			EntityType<?> entidadeTipo = (EntityType<?>) iterator.next();
			if(!"PlcBaseEntityRevision".equals(entidadeTipo.getName())){
				try {
					SingularAttribute<?, ?> idAtt = entidadeTipo.getId(Long.class);
					if(idAtt!=null) {
						out.println("	Testando entidade: " + entidadeTipo.getName() +" - from " + entidadeTipo.getName() + " where "+idAtt.getName()+"=1");
						getEmDefault().find(entidadeTipo.getJavaType(), 1L);
					}
					else{
						out.println("	Testando entidade: " + entidadeTipo.getName() +" - from " + entidadeTipo.getName());
						getEmDefault().createQuery("from "+entidadeTipo.getName());
					}
					out.println("	Ok!");
				} catch (EntityNotFoundException e) {
					out.println("	Ok!");
				} catch (Exception e) {
					out.println("	Erro!");
					e.printStackTrace(out);
				}
			}
		}
	}

	public String getDescription() {
		return "AbstractJPAChecker - Checa se o JPA (EntityManager) e Banco de dados estao ok.";
	}
	
	public abstract EntityManager getEmDefault();
}
