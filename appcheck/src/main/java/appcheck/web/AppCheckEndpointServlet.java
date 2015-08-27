package appcheck.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Properties;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import appcheck.ComponentChecker;

/**
 * 
 * Endpoint que checa todos os componentes
 * implementados pela aplicacao.
 * 
 * @author bruno.carneiro
 *
 */
@WebServlet("/appcheck")
@ServletSecurity
public class AppCheckEndpointServlet extends HttpServlet {
	
	@Inject @Any
	Instance<ComponentChecker> allComponentCheckers;
	
	private String versao;
	
	private String nomeApp;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter out = response.getWriter();
		
		
		int totalComponentes = getTotalComponents(), componentesExecutados=0;
		
		out.println("AppCheck - "+getNomeApp(request)+" - "+getVersao(request));
		
		out.println("Iniciando checagem de "+totalComponentes+" componentes.");
		out.println();
		out.flush();
		
		for(ComponentChecker componentChecker : allComponentCheckers) {
			try {
				out.println("- Checando ("+ (++componentesExecutados) +")/"+totalComponentes + " - "+ componentChecker.getDescription());
				componentChecker.check(out);
				out.println("   Ok.");
				out.println();
			} catch (Exception e) {
				out.println("		Erro!");
				e.printStackTrace(out);
				out.println();
				out.println();
			}
			out.flush();
		}
	}

	private int getTotalComponents() {
		int total = 0;
		
		for(ComponentChecker componentChecker : allComponentCheckers) {
			total++;
		}
		return total;
	}
	
	
	public String getVersao(HttpServletRequest request) throws IOException {
		
		if(versao==null){
			inicializaMetadados(request.getServletContext().getInitParameter("versao"), IOUtils.toString(request.getServletContext().getResourceAsStream("/META-INF/MANIFEST.MF")));
		}
		return versao;
	}
	
	public String getNomeApp(HttpServletRequest request) throws IOException {
		if(nomeApp==null){
			inicializaMetadados(request.getServletContext().getInitParameter("versao"), IOUtils.toString(request.getServletContext().getResourceAsStream("/META-INF/MANIFEST.MF")));
		}
		return nomeApp;
	}
	
	
	public void inicializaMetadados(String webXmlVersion, String metainf) {
		
		if(versao!=null && !"".equals(versao)){
			return;
		}
		
		// Buscar versao do build projeto do manifest.mf
		try {
			Properties p = new Properties();
			p.load(new StringReader(metainf));

			String metaInfVersion = p.getProperty("Implementation-Version");
			String metaInfBuild = p.getProperty("Implementation-Build");
			String metaInfData = p.getProperty("Implementation-Date");
			nomeApp = p.getProperty("Implementation-Title");

			if (metaInfVersion != null) {

				if (webXmlVersion!=null && !"".equals(webXmlVersion) && metaInfVersion.contains("build.") && metaInfBuild == null) {
					versao = webXmlVersion + "." + metaInfVersion.replaceAll("build.", "");
				} else if (!metaInfVersion.contains("build.") && metaInfBuild != null) {
					versao = metaInfVersion + "." + metaInfBuild;
				} else if (!metaInfVersion.contains("build.") && metaInfBuild == null) {
					versao = metaInfVersion;
				}

				if (metaInfData != null) {
					versao += "[" + metaInfData + "]";
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (versao==null && "".equals(versao)) {
				versao = "1.0.0";
		}
	}
	
}
