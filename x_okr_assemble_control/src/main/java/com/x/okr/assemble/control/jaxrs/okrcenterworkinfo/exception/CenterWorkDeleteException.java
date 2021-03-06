package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception;

import com.x.base.core.exception.PromptException;

public class CenterWorkDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public CenterWorkDeleteException( Throwable e, String id ) {
		super("中心工作删除操作过程中发生异常。ID:" + id, e );
	}
}
