package appcheck.defaultcheckers;

import java.io.PrintWriter;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import appcheck.ComponentChecker;

/**
 *  Checa configuracao de JNDIs para EJB's.
 * @author bruno.carneiro
 *
 */
public abstract class AbstractRemoteEJBLookupChecker implements ComponentChecker {

	@Override
	public void check(PrintWriter out) throws Exception {
		
		Map <String, Class<?>> jndisClass = getJNDIClassMap();
		
		for(String jndi : jndisClass.keySet()){
			out.println("	Vai fazer lookup de: "+jndi+", para a classe "+jndisClass.get(jndi));
			try{
				lookup(jndi, jndisClass.get(jndi));
				out.println("		Ok!");
			}catch(Exception e){
				out.println("		Erro!");
				e.printStackTrace(out);
			}
		}
	}

	protected void lookup(String jndi, Class<?> castClass) throws NamingException {

		InitialContext context= new InitialContext();
		Object object = context.lookup(jndi);
		javax.rmi.PortableRemoteObject.narrow(object, castClass);
	}

	@Override
	public String getDescription() {
		return "AbstractRemoteEJBLookupChecker - Checa configuracao de JNDIs para EJB's.";
	}
	
	public abstract Map <String, Class<?>> getJNDIClassMap();

}
