package com.x.okr.assemble.control.jaxrs.okrauthorize.exception;

import com.x.base.core.exception.PromptException;

public class WorkAuthorizeNotOpenException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public WorkAuthorizeNotOpenException() {
		super("工作授权功能未被开启，无法执行授权相关操作！" );
	}
}
