package org.solidstate.jpqlTranslator.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.solidstate.translator.ejb.TranslatorEjbLocal;

public class JpqlTranslatorBean {

	@EJB
	TranslatorEjbLocal translatorEjb;
	private String jpql;
	private String sql;
	private String resultString;
	private String selectWord;
	private String fromWord;
	private String whereWord;
	private String selectSQL;
	private String fromSQL;
	private String whereSQL;
	private List<String> queryHistory;
	private String stackTrace;

	@PostConstruct
	public void init() {
		queryHistory = new ArrayList<String>();
		jpql = "";
		sql = "";
		resultString = "";
		selectSQL = "";
		fromSQL = "";
		whereSQL = "";
	}

	public String getJpql() {
		return jpql;
	}

	public void setJpql(String jpql) {
		this.jpql = jpql;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String translate() {
		try {
			selectSQL = "";
			fromSQL = "";
			whereSQL = "";
			sql = translatorEjb.getSql(jpql);
			selectSQL = translatorEjb.getSelect(sql);
			fromSQL = translatorEjb.getFrom(sql);
			whereSQL = translatorEjb.getWhere(sql);
			if (whereSQL != null && !whereSQL.trim().equals("")) {
				whereWord = "WHERE";
			} else {
				whereWord = "";
			}
			saveToHistory(jpql);
		} catch (Exception e) {
			stackTrace = getStackTraceAsString(e.getCause());
			e.printStackTrace();
			showErrorMessage("Error al compilar la query");
		}
		return null;//"translateOK";
	}

	private String getStackTraceAsString(Throwable e) {
		StringBuilder sb = new StringBuilder();
		sb.append(e.getMessage());
		sb.append("\n");
		for (StackTraceElement aStackTraceElement : e.getStackTrace()) {
			sb.append(aStackTraceElement.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

	private void saveToHistory(String jpql) {
		if (!queryHistory.contains(jpql)) {
			queryHistory.add(jpql);
		}
	}

	public String getResultList() {
		Map<String, String> parameters = new HashMap<String, String>();
		try {
			resultString = translatorEjb.getResultList(jpql, parameters);
		} catch (Exception e) {
			showErrorMessage("No se puede ejecutar la query");
		}

		return null;//"getResultListOK";
	}

	private void showErrorMessage(String mensaje) {
		FacesMessage message = new FacesMessage(mensaje);
		message.setSeverity(FacesMessage.SEVERITY_ERROR);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public String getResultString() {
		return resultString;
	}

	public void setResultString(String resultString) {
		this.resultString = resultString;
	}

	public String getSelectWord() {
		return selectWord;
	}

	public void setSelectWord(String selectWord) {
		this.selectWord = selectWord;
	}

	public String getFromWord() {
		return fromWord;
	}

	public void setFromWord(String fromWord) {
		this.fromWord = fromWord;
	}

	public String getWhereWord() {
		return whereWord;
	}

	public void setWhereWord(String whereWord) {
		this.whereWord = whereWord;
	}

	public String getSelectSQL() {
		return selectSQL;
	}

	public void setSelectSQL(String selectSQL) {
		this.selectSQL = selectSQL;
	}

	public String getFromSQL() {
		return fromSQL;
	}

	public void setFromSQL(String fromSQL) {
		this.fromSQL = fromSQL;
	}

	public String getWhereSQL() {
		return whereSQL;
	}

	public void setWhereSQL(String whereSQL) {
		this.whereSQL = whereSQL;
	}

	public List<String> getQueryHistory() {
		return queryHistory;
	}

	public void setQueryHistory(List<String> queryHistory) {
		this.queryHistory = queryHistory;
	}

	public String reload() {
		return null;//"reloadOK";
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}
}
