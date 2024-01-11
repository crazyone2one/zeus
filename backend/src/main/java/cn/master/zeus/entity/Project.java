package cn.master.zeus.entity;

import cn.master.zeus.common.listener.project.ProjectListener;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实体类。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "project", onSet = ProjectListener.class)
public class Project implements Serializable {

    /**
     * Project ID
     */
    @Id
    private String id;

    /**
     * Workspace ID this project belongs to
     */
    private String workspaceId;

    /**
     * Project name
     */
    private String name;

    /**
     * Project description
     */
    private String description;

    /**
     * Create timestamp
     */
    private LocalDateTime createTime;

    /**
     * Update timestamp
     */
    private LocalDateTime updateTime;

    /**
     * （0正常 1删除）
     */
    private Boolean delFlag;

    /**
     * 是否使用第三方平台缺陷模板
     */
    private Boolean thirdPartTemplate;

    /**
     * 项目使用哪个平台的模板
     */
    private String platform;

    private Boolean versionEnable;

    private String issueConfig;

    private String apiTemplateId;

    /**
     * Relate test case template id
     */
    private String caseTemplateId;

    /**
     * Relate test issue template id
     */
    private String issueTemplateId;

    /**
     * 是否开启自定义ID(默认关闭)
     */
    private Boolean customNum;

    /**
     * 是否开启场景自定义ID(默认关闭)
     */
    private Boolean scenarioCustomNum;

    private String createUser;

    private String systemId;

    @Column(ignore = true)
    private long memberSize;

}
