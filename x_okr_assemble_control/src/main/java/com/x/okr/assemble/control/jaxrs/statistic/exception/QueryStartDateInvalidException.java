package com.x.okr.assemble.control.jaxrs.statistic.exception;

import com.x.base.core.exception.PromptException;

public class QueryStartDateInvalidException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public QueryStartDateInvalidException( Throwable e, String date ) {
		super("查询条件中开始日期不合法。Dates：" + date, e );
	}
}
