package org.solidstate.translator.ejb;
import java.util.Map;

import javax.ejb.Remote;

@Remote
public interface TranslatorEjbRemote{

	public String getSql(String hqlQueryText);

	public String getResultList(String hqlQuery, Map<String, String> parameterMap);

	public String getSelect(String sql);
	public String getWhere(String sql);
	public String getFrom(String sql);

	

}
