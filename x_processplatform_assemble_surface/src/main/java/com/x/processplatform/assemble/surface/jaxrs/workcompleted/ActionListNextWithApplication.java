package com.x.processplatform.assemble.surface.jaxrs.workcompleted;

import java.util.List;

import com.x.base.core.application.jaxrs.EqualsTerms;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Application;

class ActionListNextWithApplication extends ActionBase {

	ActionResult<List<WrapOutWorkCompleted>> execute(EffectivePerson effectivePerson, String id, Integer count,
			String applicationFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ApplicationNotExistedException(applicationFlag);
			}
			EqualsTerms equals = new EqualsTerms();
			equals.put("creatorPerson", effectivePerson.getName());
			equals.put("application", application.getId());
			ActionResult<List<WrapOutWorkCompleted>> result = this.standardListNext(workCompletedOutCopier, id, count,
					"sequence", equals, null, null, null, null, null, null, true, DESC);
			/* 添加权限 */
			if (null != result.getData()) {
				for (WrapOutWorkCompleted wrap : result.getData()) {
					WorkCompleted o = emc.find(wrap.getId(), WorkCompleted.class);
					Control control = business.getControlOfWorkCompleted(effectivePerson, o);
					wrap.setControl(control);
				}
			}
			return result;
		}
	}

}