/** 
 *  Generated by OpenJPA MetaModel Generator Tool.
**/

package com.x.bbs.entity;

import com.x.base.core.entity.SliceJpaObject_;
import java.lang.String;
import java.util.Date;
import javax.persistence.metamodel.SingularAttribute;

@javax.persistence.metamodel.StaticMetamodel
(value=com.x.bbs.entity.BBSSubjectContent.class)
@javax.annotation.Generated
(value="org.apache.openjpa.persistence.meta.AnnotationProcessor6",date="Sat May 06 19:34:23 CST 2017")
public class BBSSubjectContent_ extends SliceJpaObject_  {
    public static volatile SingularAttribute<BBSSubjectContent,String> content;
    public static volatile SingularAttribute<BBSSubjectContent,Date> createTime;
    public static volatile SingularAttribute<BBSSubjectContent,String> id;
    public static volatile SingularAttribute<BBSSubjectContent,String> sequence;
    public static volatile SingularAttribute<BBSSubjectContent,Date> updateTime;
}
