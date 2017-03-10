package com.x.organization.assemble.personal.jaxrs.regist;

import com.x.base.core.exception.PromptException;

class InvalidCaptchaException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	InvalidCaptchaException() {
		super("图片验证码错误.");
	}
}
