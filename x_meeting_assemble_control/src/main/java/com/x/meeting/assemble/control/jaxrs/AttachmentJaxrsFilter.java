package com.x.meeting.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.ManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/attachment/*" })
public class AttachmentJaxrsFilter extends ManagerUserJaxrsFilter {

}
