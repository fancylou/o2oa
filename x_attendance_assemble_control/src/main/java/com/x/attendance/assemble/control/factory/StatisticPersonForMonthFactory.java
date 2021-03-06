package com.x.attendance.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.AbstractFactory;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.attendancestatistic.WrapInFilterStatisticPersonForMonth;
import com.x.attendance.entity.StatisticPersonForMonth;
import com.x.attendance.entity.StatisticPersonForMonth_;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.utils.annotation.MethodDescribe;

public class StatisticPersonForMonthFactory extends AbstractFactory {

	private Logger logger = LoggerFactory.getLogger( StatisticPersonForMonthFactory.class );
	
	public StatisticPersonForMonthFactory(Business business) throws Exception {
		super(business);
	}

	@MethodDescribe("获取指定Id的StatisticPersonForMonth信息对象")
	public StatisticPersonForMonth get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, StatisticPersonForMonth.class, ExceptionWhen.none);
	}
	
	@MethodDescribe("列示全部的StatisticPersonForMonth信息列表")
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<StatisticPersonForMonth> root = cq.from( StatisticPersonForMonth.class);
		cq.select(root.get(StatisticPersonForMonth_.id));
		return em.createQuery(cq).getResultList();
	}
	
	@MethodDescribe("列示指定Id的StatisticPersonForMonth信息列表")
	public List<StatisticPersonForMonth> list( List<String> ids ) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<StatisticPersonForMonth>();
		}
		EntityManager em = this.entityManagerContainer().get(StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<StatisticPersonForMonth> cq = cb.createQuery(StatisticPersonForMonth.class);
		Root<StatisticPersonForMonth> root = cq.from(StatisticPersonForMonth.class);
		Predicate p = root.get(StatisticPersonForMonth_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listByUserYearAndMonth( String employeeName, String sYear, String sMonth ) throws Exception {
		if( employeeName == null || employeeName.isEmpty() ){
			logger.error( new EmployeeNamesEmptyException() );
			return null;
		}
		
		EntityManager em = this.entityManagerContainer().get( StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<StatisticPersonForMonth> root = cq.from( StatisticPersonForMonth.class );
		Predicate p = cb.equal( root.get( StatisticPersonForMonth_.employeeName ), employeeName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( StatisticPersonForMonth_.statisticYear ), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( StatisticPersonForMonth_.statisticMonth ), sMonth));
		}
		cq.select(root.get( StatisticPersonForMonth_.id ) );
		return em.createQuery( cq.where( p ) ).setMaxResults( 60 ).getResultList();
	}

	public List<String> listByDepartmentYearAndMonth( List<String> departmentNameList, String year, String month ) throws Exception{
		
		if( departmentNameList == null || departmentNameList.size() == 0 ){
			logger.error( new DepartmentNamesEmptyException() );
			return null;
		}
		
		EntityManager em = this.entityManagerContainer().get( StatisticPersonForMonth.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class );
		Root<StatisticPersonForMonth> root = cq.from( StatisticPersonForMonth.class );
		Predicate p = root.get( StatisticPersonForMonth_.organizationName ).in(departmentNameList);
		if( year == null || year.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( StatisticPersonForMonth_.statisticYear ), year ) );
		}
		if( month == null || month.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( StatisticPersonForMonth_.statisticMonth ), month ));
		}
		cq.select(root.get( StatisticPersonForMonth_.id ));
		return em.createQuery( cq.where( p ) ).setMaxResults( 60 ).getResultList();
	}
	
	/**
	 * 查询下一页的信息数据
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public List<StatisticPersonForMonth> listIdsNextWithFilter( String id, Integer count, Object sequence, WrapInFilterStatisticPersonForMonth wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( StatisticPersonForMonth.class );
		String order = wrapIn.getOrder();//排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		
		Integer index = 1;
		sql_stringBuffer.append( "SELECT o FROM "+StatisticPersonForMonth.class.getCanonicalName()+" o where 1=1" );

		if ((null != sequence) ) {
			sql_stringBuffer.append(" and o.sequence " + (StringUtils.equalsIgnoreCase(order, "DESC") ? "<" : ">") + (" ?" + (index)));
			vs.add(sequence);
			index++;
		}
				
		if ((null != wrapIn.getEmployeeName()) && wrapIn.getEmployeeName().size() > 0) {
			sql_stringBuffer.append(" and o.employeeName in ?" + (index));
			vs.add( wrapIn.getEmployeeName() );
			index++;
		}
		if ((null != wrapIn.getOrganizationName()) && wrapIn.getOrganizationName().size() > 0 ) {
			sql_stringBuffer.append(" and o.organizationName in ?" + (index));
			vs.add( wrapIn.getOrganizationName() );
			index++;
		}
		if ((null != wrapIn.getCompanyName()) && wrapIn.getCompanyName().size() > 0 ) {
			sql_stringBuffer.append(" and o.companyName in ?" + (index));
			vs.add( wrapIn.getCompanyName() );
			index++;
		}
		if ((null != wrapIn.getStatisticYear() ) && (!wrapIn.getStatisticYear().isEmpty())) {
			sql_stringBuffer.append(" and o.statisticYear = ?" + (index));
			vs.add( wrapIn.getStatisticYear() );
			index++;
		}
		if ((null != wrapIn.getStatisticMonth()) && (!wrapIn.getStatisticMonth().isEmpty())) {
			sql_stringBuffer.append(" and o.statisticMonth = ?" + (index));
			vs.add( wrapIn.getStatisticMonth() );
			index++;
		}
		sql_stringBuffer.append(" order by o.sequence " + order );
		
		//logger.debug("listIdsNextWithFilter:["+sql_stringBuffer.toString()+"]");
		//logger.debug( vs );
		
		Query query = em.createQuery( sql_stringBuffer.toString(), StatisticPersonForMonth.class );
		//为查询设置所有的参数值
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}
		return query.setMaxResults(count).getResultList();
	}	
	
	/**
	 * 查询上一页的文档信息数据
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<StatisticPersonForMonth> listIdsPrevWithFilter( String id, Integer count, Object sequence, WrapInFilterStatisticPersonForMonth wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( StatisticPersonForMonth.class );
		String order = wrapIn.getOrder();//排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		
		sql_stringBuffer.append( "SELECT o FROM "+StatisticPersonForMonth.class.getCanonicalName()+" o where 1=1" );
		if ((null != sequence) ) {
			sql_stringBuffer.append(" and o.sequence " + (StringUtils.equalsIgnoreCase(order, "DESC") ? ">" : "<") + (" ?" + (index)));
			vs.add(sequence);
			index++;
		}
		if ((null != wrapIn.getEmployeeName()) && wrapIn.getEmployeeName().size() > 0) {
			sql_stringBuffer.append(" and o.employeeName in ?" + (index));
			vs.add( wrapIn.getEmployeeName() );
			index++;
		}
		if ((null != wrapIn.getOrganizationName()) && wrapIn.getOrganizationName().size() > 0 ) {
			sql_stringBuffer.append(" and o.organizationName in ?" + (index));
			vs.add( wrapIn.getOrganizationName() );
			index++;
		}
		if ((null != wrapIn.getCompanyName()) && wrapIn.getCompanyName().size() > 0 ) {
			sql_stringBuffer.append(" and o.companyName in ?" + (index));
			vs.add( wrapIn.getCompanyName() );
			index++;
		}
		if ((null != wrapIn.getStatisticYear() ) && (!wrapIn.getStatisticYear().isEmpty())) {
			sql_stringBuffer.append(" and o.statisticYear = ?" + (index));
			vs.add( wrapIn.getStatisticYear() );
			index++;
		}
		if ((null != wrapIn.getStatisticMonth()) && (!wrapIn.getStatisticMonth().isEmpty())) {
			sql_stringBuffer.append(" and o.statisticMonth = ?" + (index));
			vs.add( wrapIn.getStatisticMonth() );
			index++;
		}
		sql_stringBuffer.append(" order by o.sequence " + order );
		
		//logger.debug("listIdsPrevWithFilter:["+sql_stringBuffer.toString()+"]");
		//logger.debug( vs );
		
		Query query = em.createQuery( sql_stringBuffer.toString(), StatisticPersonForMonth.class );
		//为查询设置所有的参数值
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}
		
		return query.setMaxResults(20).getResultList();
	}
	
	/**
	 * 查询符合的文档信息总数
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public long getCountWithFilter( WrapInFilterStatisticPersonForMonth wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( StatisticPersonForMonth.class );
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		sql_stringBuffer.append( "SELECT count(o.id) FROM "+StatisticPersonForMonth.class.getCanonicalName()+" o where 1=1" );
		
		if ((null != wrapIn.getEmployeeName()) && wrapIn.getEmployeeName().size() > 0) {
			sql_stringBuffer.append(" and o.employeeName in ?" + (index));
			vs.add( wrapIn.getEmployeeName() );
			index++;
		}
		if ((null != wrapIn.getOrganizationName()) && wrapIn.getOrganizationName().size() > 0 ) {
			sql_stringBuffer.append(" and o.organizationName in ?" + (index));
			vs.add( wrapIn.getOrganizationName() );
			index++;
		}
		if ((null != wrapIn.getCompanyName()) && wrapIn.getCompanyName().size() > 0 ) {
			sql_stringBuffer.append(" and o.companyName in ?" + (index));
			vs.add( wrapIn.getCompanyName() );
			index++;
		}
		if ((null != wrapIn.getStatisticYear() ) && (!wrapIn.getStatisticYear().isEmpty())) {
			sql_stringBuffer.append(" and o.statisticYear = ?" + (index));
			vs.add( wrapIn.getStatisticYear() );
			index++;
		}
		if ((null != wrapIn.getStatisticMonth()) && (!wrapIn.getStatisticMonth().isEmpty())) {
			sql_stringBuffer.append(" and o.statisticMonth = ?" + (index));
			vs.add( wrapIn.getStatisticMonth() );
			index++;
		}
		
		//logger.debug("listIdsPrevWithFilter:["+sql_stringBuffer.toString()+"]");
		//logger.debug( vs );
		
		Query query = em.createQuery( sql_stringBuffer.toString(), StatisticPersonForMonth.class );
		//为查询设置所有的参数值
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}		
		return (Long) query.getSingleResult();
	}
	
	public StatisticPersonForMonth get(String employeeName, String cycleYear, String cycleMonth) throws Exception {
		if( employeeName == null ){
			logger.error( new EmployeeNamesEmptyException() );
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<StatisticPersonForMonth> cq = cb.createQuery(StatisticPersonForMonth.class);
		Root<StatisticPersonForMonth> root = cq.from( StatisticPersonForMonth.class);	
		Predicate p = cb.equal( root.get( StatisticPersonForMonth_.employeeName ), employeeName);		
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( StatisticPersonForMonth_.statisticYear), cycleYear));
		}
		
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( StatisticPersonForMonth_.statisticMonth), cycleMonth));
		}
		try{
			return em.createQuery(cq.where(p)).getSingleResult();
		}catch(NoResultException e){
			return null;
		}
	}
	
	/**
	 * 根据部门名称，年份月份，统计员工数量
	 * @param organizationName
	 * @param sYear
	 * @param sMonth
	 * @return
	 * @throws Exception
	 */
	public Long countEmployeeCountByDepartmentYearAndMonth( List<String> organizationName, String sYear, String sMonth) throws Exception{
		if( organizationName == null || organizationName.size() == 0 ){
			logger.error( new OganizationNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticPersonForMonth> root = cq.from( StatisticPersonForMonth.class);
		Predicate p = root.get(StatisticPersonForMonth_.organizationName).in( organizationName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticPersonForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticPersonForMonth_.statisticMonth), sMonth));
		}
		//查询总数
		cq.select( cb.count( root ) );	
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据部门，统计年月，计算部门内所有员工迟到数总和
	 * @param organizationName
	 * @param sYear
	 * @param sMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumLateCountByDepartmentYearAndMonth(List<String> organizationName, String sYear, String sMonth) throws Exception{
		if( organizationName == null || organizationName.size() == 0 ){
			logger.error( new OganizationNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticPersonForMonth> root = cq.from( StatisticPersonForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticPersonForMonth_.lateTimes ) ) );		
		Predicate p = root.get(StatisticPersonForMonth_.organizationName).in( organizationName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticPersonForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticPersonForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	
	/**
	 * 根据部门列表，统计年月，计算部门内所有员工出勤天数总和
	 * @param organizationName
	 * @param sYear
	 * @param sMonth
	 * @return
	 * @throws Exception
	 */
	public Double sumAttendanceDayCountByDepartmentYearAndMonth( List<String> organizationName, String sYear, String sMonth) throws Exception{
		if( organizationName == null || organizationName.size() == 0 ){
			logger.error( new OganizationNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<StatisticPersonForMonth> root = cq.from( StatisticPersonForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticPersonForMonth_.onDutyDayCount) ) );		
		Predicate p = root.get(StatisticPersonForMonth_.organizationName).in( organizationName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticPersonForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticPersonForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据部门列表，统计年月，计算部门内所有员工异常打卡数总和
	 * @param organizationName
	 * @param sYear
	 * @param sMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumAbNormalDutyCountByDepartmentYearAndMonth( List<String> organizationName, String sYear, String sMonth) throws Exception{
		if( organizationName == null || organizationName.size() == 0 ){
			logger.error( new OganizationNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticPersonForMonth> root = cq.from( StatisticPersonForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticPersonForMonth_.abNormalDutyCount) ) );		
		Predicate p = root.get(StatisticPersonForMonth_.organizationName).in( organizationName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticPersonForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticPersonForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据部门列表，统计年月，计算部门内所有员工工时不足次数总和
	 * @param organizationName
	 * @param sYear
	 * @param sMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumLackOfTimeCountByDepartmentYearAndMonth(List<String> organizationName, String sYear, String sMonth) throws Exception{
		if( organizationName == null || organizationName.size() == 0 ){
			logger.error( new OganizationNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticPersonForMonth> root = cq.from( StatisticPersonForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticPersonForMonth_.lackOfTimeCount) ) );		
		Predicate p = root.get(StatisticPersonForMonth_.organizationName).in( organizationName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticPersonForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticPersonForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据部门列表，统计年月，计算部门内所有员工早退次数总和
	 * @param organizationName
	 * @param sYear
	 * @param sMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumLeaveEarlyCountByDepartmentYearAndMonth( List<String> organizationName, String sYear, String sMonth) throws Exception{
		if( organizationName == null || organizationName.size() == 0 ){
			logger.error( new OganizationNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticPersonForMonth> root = cq.from( StatisticPersonForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticPersonForMonth_.leaveEarlyTimes ) ) );		
		Predicate p = root.get(StatisticPersonForMonth_.organizationName).in( organizationName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticPersonForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticPersonForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据部门列表，统计年月，计算部门内所有员工签退次数总和
	 * @param organizationName
	 * @param sYear
	 * @param sMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumOffDutyCountByDepartmentYearAndMonth( List<String> organizationName, String sYear, String sMonth) throws Exception{
		if( organizationName == null || organizationName.size() == 0 ){
			logger.error( new OganizationNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticPersonForMonth> root = cq.from( StatisticPersonForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticPersonForMonth_.offDutyTimes ) ) );		
		Predicate p = root.get(StatisticPersonForMonth_.organizationName).in( organizationName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticPersonForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticPersonForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据部门列表，统计年月，计算部门内所有员工签到退次数总和
	 * @param organizationName
	 * @param sYear
	 * @param sMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumOnDutyCountByDepartmentYearAndMonth( List<String> organizationName, String sYear, String sMonth) throws Exception{
		if( organizationName == null || organizationName.size() == 0 ){
			logger.error( new OganizationNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticPersonForMonth> root = cq.from( StatisticPersonForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticPersonForMonth_.onDutyTimes ) ) );		
		Predicate p = root.get(StatisticPersonForMonth_.organizationName).in( organizationName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticPersonForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticPersonForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据部门列表，统计年月，计算部门内所有员工请假天数总和
	 * @param organizationName
	 * @param sYear
	 * @param sMonth
	 * @return
	 * @throws Exception
	 */
	public Double sumOnSelfHolidayCountByDepartmentYearAndMonth( List<String> organizationName, String sYear, String sMonth) throws Exception{
		if( organizationName == null || organizationName.size() == 0 ){
			logger.error( new OganizationNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<StatisticPersonForMonth> root = cq.from( StatisticPersonForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticPersonForMonth_.onSelfHolidayCount) ) );		
		Predicate p = root.get(StatisticPersonForMonth_.organizationName).in( organizationName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticPersonForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticPersonForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据部门列表，统计年月，计算部门内所有员工缺勤天数总和
	 * @param organizationName
	 * @param sYear
	 * @param sMonth
	 * @return
	 * @throws Exception
	 */
	public Double sumAbsenceDayCountByDepartmentYearAndMonth( List<String> organizationName, String sYear, String sMonth) throws Exception{
		if( organizationName == null || organizationName.size() == 0 ){
			logger.error( new OganizationNamesEmptyException() );
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( StatisticPersonForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<StatisticPersonForMonth> root = cq.from( StatisticPersonForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticPersonForMonth_.absenceDayCount) ) );	
		
		Predicate p = root.get(StatisticPersonForMonth_.organizationName).in( organizationName );
		
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticPersonForMonth_.statisticYear), sYear));
		}
		
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticPersonForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
}