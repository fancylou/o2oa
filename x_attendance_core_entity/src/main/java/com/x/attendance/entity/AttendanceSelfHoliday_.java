/** 
 *  Generated by OpenJPA MetaModel Generator Tool.
**/

package com.x.attendance.entity;

import com.x.base.core.entity.SliceJpaObject_;
import java.lang.Double;
import java.lang.String;
import java.util.Date;
import javax.persistence.metamodel.SingularAttribute;

@javax.persistence.metamodel.StaticMetamodel
(value=com.x.attendance.entity.AttendanceSelfHoliday.class)
@javax.annotation.Generated
(value="org.apache.openjpa.persistence.meta.AnnotationProcessor6",date="Sat May 06 19:33:53 CST 2017")
public class AttendanceSelfHoliday_ extends SliceJpaObject_  {
    public static volatile SingularAttribute<AttendanceSelfHoliday,String> companyName;
    public static volatile SingularAttribute<AttendanceSelfHoliday,String> companyOu;
    public static volatile SingularAttribute<AttendanceSelfHoliday,Date> createTime;
    public static volatile SingularAttribute<AttendanceSelfHoliday,String> description;
    public static volatile SingularAttribute<AttendanceSelfHoliday,String> docId;
    public static volatile SingularAttribute<AttendanceSelfHoliday,String> employeeName;
    public static volatile SingularAttribute<AttendanceSelfHoliday,String> employeeNumber;
    public static volatile SingularAttribute<AttendanceSelfHoliday,Date> endTime;
    public static volatile SingularAttribute<AttendanceSelfHoliday,String> id;
    public static volatile SingularAttribute<AttendanceSelfHoliday,Double> leaveDayNumber;
    public static volatile SingularAttribute<AttendanceSelfHoliday,String> leaveType;
    public static volatile SingularAttribute<AttendanceSelfHoliday,String> organizationName;
    public static volatile SingularAttribute<AttendanceSelfHoliday,String> organizationOu;
    public static volatile SingularAttribute<AttendanceSelfHoliday,String> sequence;
    public static volatile SingularAttribute<AttendanceSelfHoliday,Date> startTime;
    public static volatile SingularAttribute<AttendanceSelfHoliday,Date> updateTime;
}
