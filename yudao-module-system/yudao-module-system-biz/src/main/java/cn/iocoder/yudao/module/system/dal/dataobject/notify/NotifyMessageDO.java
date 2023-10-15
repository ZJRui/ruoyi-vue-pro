package cn.iocoder.yudao.module.system.dal.dataobject.notify;

import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.system.dal.dataobject.mail.MailTemplateDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

/**
 * 站内信 DO
 *
 *
 * @author xrcoder
 */

/**
 *
 * careful: typeHandler用来指定 对象转为sql参数，或者从ResultSet取出一个值时，如何做转换。
 *  但是并不是说你 给某一个字段指定了使用某一个typeHandler 他就能够自动的将对象转为sql参数，或者从ResultSet取出一个值时，如何做转换。
 *
 *   @TableField(value = "email", typeHandler = JacksonTypeHandler.class)
 *   private Address address;
 * 1.address属性指定了使用json handler，当insert update的时候会 自动将对象转为 json字符串。
 *
 * 2.在select的时候 仅仅配置 typeHandler是不够的。显示启用typeHandler有两种方式 :
 * (1)指定了与自定义 typeHandler 一致的 jdbcType 和 javaType
 * (2)直接使用typeHandler 指定具体 的 实现类
 *
 * A.依赖于ResultMap,在ReulstMap 中的result标签中指定typeHandler,比如
 *   <resultMap id="myCustomResultMap">
 *        <result column="email" property="address" typeHandler="JacksonTypeHandler"/>
 *    </resultMap>
 *
 *  然后给select语句指定resultMap。
 *
 * B.也可以在sql语句中对参数指定typeHandler:
 *     <select  i d=” findRoles ” parameterType=” string ” resultMap=” roleMapper” >
 *   select  id,  r ole  name ,  note  from  t  role
 *   where  role  name  like  concat （ ’ 哇 ’， ＃｛ roleName , jdbcType=VARCHAR,
 *   javaType=string ｝ ，’ 告 ’ ）
 *   </select>
 *
 *
 *careful：关于自动映射，mybatis的autoMappingBehavior默认为partial, 他只会自动映射没有定义嵌套映射的ResultSet,
 * 假设Person中有一个Address对象嵌套属性，那么select查询到的Person 默认的自动映射就不会映射address属性，也就是address属性为空。
 *
 * E:\programme\mybatis\博文\mybatis3 autoMappingBehavior - TheViper_ - 博客园.pdf
 *
 * mybatis-plus的@TableName的autoResultMap的作用就是生成一个ResutMap让mybatis用这个ResultMap来完成自动映射。
 *
 *
 */
@TableName(value = "system_notify_message", autoResultMap = true)
@KeySequence("system_notify_message_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotifyMessageDO extends BaseDO {

    /**
     * 站内信编号，自增
     */
    @TableId
    private Long id;
    /**
     * 用户编号
     *
     * 关联 MemberUserDO 的 id 字段、或者 AdminUserDO 的 id 字段
     */
    private Long userId;
    /**
     * 用户类型
     *
     * 枚举 {@link UserTypeEnum}
     */
    private Integer userType;

    // ========= 模板相关字段 =========

    /**
     * 模版编号
     *
     * 关联 {@link NotifyTemplateDO#getId()}
     */
    private Long templateId;
    /**
     * 模版编码
     *
     * 关联 {@link NotifyTemplateDO#getCode()}
     */
    private String templateCode;
    /**
     * 模版类型
     *
     * 冗余 {@link NotifyTemplateDO#getType()}
     */
    private Integer templateType;
    /**
     * 模版发送人名称
     *
     * 冗余 {@link NotifyTemplateDO#getNickname()}
     */
    private String templateNickname;
    /**
     * 模版内容
     *
     * 基于 {@link NotifyTemplateDO#getContent()} 格式化后的内容
     */
    private String templateContent;
    /**
     * 模版参数
     *
     * 基于 {@link NotifyTemplateDO#getParams()} 输入后的参数
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> templateParams;

    // ========= 读取相关字段 =========

    /**
     * 是否已读
     */
    private Boolean readStatus;
    /**
     * 阅读时间
     */
    private LocalDateTime readTime;

}
