package com.x.cms.assemble.control.jaxrs.categoryinfo;
import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.WrapTools;
import com.x.cms.assemble.control.jaxrs.categoryinfo.exception.CategoryInfoIdEmptyException;
import com.x.cms.assemble.control.jaxrs.categoryinfo.exception.CategoryInfoNotExistsException;
import com.x.cms.assemble.control.jaxrs.categoryinfo.exception.CategoryInfoProcessException;
import com.x.cms.core.entity.CategoryInfo;

import net.sf.ehcache.Element;

public class ExcuteGet extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteGet.class );
	
	protected ActionResult<WrapOutCategoryInfo> execute( HttpServletRequest request, String id, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<WrapOutCategoryInfo> result = new ActionResult<>();
		WrapOutCategoryInfo wrap = null;
		CategoryInfo categoryInfo = null;
		Boolean check = true;
		
		if( id == null || id.isEmpty() ){
			check = false;
			Exception exception = new CategoryInfoIdEmptyException();
			result.error( exception );
		}
		
		String cacheKey = ApplicationCache.concreteCacheKey( id );
		Element element = cache.get(cacheKey);
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			wrap = ( WrapOutCategoryInfo ) element.getObjectValue();
			result.setData(wrap);
		} else {
			if( check ){
				try {
					categoryInfo = categoryInfoServiceAdv.get( id );
					if( categoryInfo == null ){
						check = false;
						Exception exception = new CategoryInfoNotExistsException( id );
						result.error( exception );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new CategoryInfoProcessException( e, "根据ID查询分类信息对象时发生异常。ID:" + id );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			if( check ){
				try {
					
					wrap = WrapTools.category_wrapout_copier.copy( categoryInfo );
					cache.put(new Element( cacheKey, wrap ));
					
					result.setData( wrap );
				} catch ( Exception e ) {
					check = false;
					Exception exception = new CategoryInfoProcessException( e, "将查询出来的分类信息对象转换为可输出的数据信息时发生异常。" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		
		
		return result;
	}

}