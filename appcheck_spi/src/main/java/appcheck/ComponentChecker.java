package appcheck;

import java.io.PrintWriter;

/**
 * Define o comportamento para todos
 * os checadores de componentes de uma aplicacao.
 *   
 * @author bruno.carneiro
 *
 */
public interface ComponentChecker {

	void check(PrintWriter out) throws Exception;
	
	String getDescription();
	
}
