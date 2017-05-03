package org.solidstate.translator.ejb;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.ejb.EntityManagerImpl;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory;
import org.hibernate.hql.spi.QueryTranslator;
import org.hibernate.hql.spi.QueryTranslatorFactory;

@Stateless
public class TranslatorEjb implements TranslatorEjbLocal{

	private static final String SELECT = "select";
	private static final String FROM = "from";
	private static final String WHERE = "where";
	private static final String OPEN_SPAN_TAG = "<span class='reservedWord'>";
	private static final String END_SPAN_TAG = "</span>";
	private final String RESERVED_WORDS = "select where and from in as left right inner outer join group by having is null order";

	public TranslatorEjb() {
	}

	@PersistenceContext(unitName = "SigaeEJBPU")
	private EntityManager entityManager;

	// solo funca con glassfish
	@Override
	public String getSql(String hqlQuery) {
		if (hqlQuery != null && hqlQuery.trim().length() > 0) {
			Session session = ((EntityManagerImpl) entityManager.getDelegate())
					.getSession();
			SessionFactory sessionFactory = session.getSessionFactory();
			final QueryTranslatorFactory translatorFactory = new ASTQueryTranslatorFactory();
			final SessionFactoryImplementor factory = (SessionFactoryImplementor) sessionFactory;
			final QueryTranslator translator = translatorFactory
					.createQueryTranslator(hqlQuery, hqlQuery,
							Collections.EMPTY_MAP, factory);

			translator.compile(Collections.EMPTY_MAP, false);

			return translator.getSQLString();
		}
		return null;
	}

	@Override
	public String getResultList(String hqlQuery,
			Map<String, String> parameterMap) {
		Query query = entityManager.createQuery(hqlQuery);
		query.setMaxResults(100);
		List resultList = query.getResultList();
		if (resultList.isEmpty()) {
			return "No rows";
		}
		return getHtmlTable(resultList);

	}

	private String getHtmlTable(List list) {
		StringBuilder sb = new StringBuilder();
		sb.append("<table id='hor-minimalist-b'>");
		Field[] fields = list.get(0).getClass().getDeclaredFields();
		for (Field aField : fields) {
			sb.append("<th>");
			sb.append(aField.getName());
			sb.append("</th>");
		}

		for (Object anObject : list) {
			sb.append("<tr>");
			for (Field aField : fields) {
				try {
					aField.setAccessible(true);
					sb.append("<td>");
					sb.append(aField.get(anObject) == null ? "-" : aField
							.get(anObject));
					sb.append("</td>");
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}

			}
			sb.append("</tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}

	@Override
	public String getSelect(String sql) {
		int beginIndex = sql.indexOf(SELECT) + SELECT.length();
		int endIndex = sql.indexOf(FROM);
		return parseCommas(sql.substring(beginIndex, endIndex));
	}

	@Override
	public String getWhere(String sql) {
		int beginIndex = sql.indexOf(WHERE);
		if (beginIndex < 0)
			return " ";
		return parseAnd(sql.substring(beginIndex + WHERE.length()));
	}

	@Override
	public String getFrom(String sql) {
		int beginIndex = sql.indexOf(FROM) + FROM.length();
		int endIndex = sql.indexOf(WHERE);
		if (endIndex >= 0) {
			return parseCommas(sql.substring(beginIndex, endIndex));
		} else {
			return parseCommas(sql.substring(beginIndex));
		}
	}

	private String parseCommas(String substring) {
		return parseChar(substring, ",");
	}

	private String parseAnd(String substring) {
		return parseChar(substring, "and");
	}

	private String parseChar(String html, String toParse) {
		StringTokenizer st = new StringTokenizer(html);
		StringBuilder sb = new StringBuilder();
		String currentToken = null;
		while (st.hasMoreTokens()) {
			currentToken = st.nextToken();
			sb.append(" ");
			sb.append(highLight(currentToken));
			sb.append(" ");
			if (currentToken.indexOf(toParse) >= 0) {
				sb.append("<br/>");
			}
		}

		return sb.toString();
	}

	private String highLight(String word) {
		if (isReservedWord(word)) {
			return OPEN_SPAN_TAG + word + END_SPAN_TAG;
		} else {
			return word;
		}

	}

	private boolean isReservedWord(String word) {
		return RESERVED_WORDS.indexOf(word) >= 0;
	}


}
