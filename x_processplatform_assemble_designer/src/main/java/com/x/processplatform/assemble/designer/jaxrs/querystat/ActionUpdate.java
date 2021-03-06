package com.x.processplatform.assemble.designer.jaxrs.querystat;

import java.util.Date;

import com.google.gson.JsonElement;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.wrapin.WrapInQueryStat;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.QueryStat;
import com.x.processplatform.core.entity.element.QueryView;

class ActionUpdate extends ActionBase {
	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			WrapInQueryStat wrapIn = this.convertToWrapIn(jsonElement, WrapInQueryStat.class);
			Business business = new Business(emc);
			QueryStat queryStat = emc.find(id, QueryStat.class);
			if (null == queryStat) {
				throw new QueryStatNotExistedException(id);
			}
			Application application = emc.find(queryStat.getApplication(), Application.class);
			if (null == application) {
				throw new ApplicationNotExistedException(queryStat.getApplication());
			}
			if (!business.applicationEditAvailable(effectivePerson, application)) {
				throw new ApplicationAccessDeniedException(effectivePerson.getName(), application.getName(),
						application.getId());
			}
			emc.beginTransaction(QueryStat.class);
			inCopier.copy(wrapIn, queryStat);
			QueryView queryView = emc.find(queryStat.getQueryView(), QueryView.class);
			if (null == queryView) {
				throw new QueryViewNotExistedException(queryStat.getQueryView());
			}
			queryStat.setQueryView(queryView.getId());
			queryStat.setQueryViewName(queryView.getName());
			queryStat.setQueryViewAlias(queryView.getAlias());
			queryStat.setLastUpdatePerson(effectivePerson.getName());
			queryStat.setLastUpdateTime(new Date());
			emc.check(queryStat, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(QueryStat.class);
			WrapOutId wrap = new WrapOutId(queryStat.getId());
			result.setData(wrap);
			return result;
		}
	}
}