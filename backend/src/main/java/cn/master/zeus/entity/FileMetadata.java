package cn.master.zeus.entity;

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
 *  实体类。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "file_metadata")
public class FileMetadata implements Serializable {

    /**
     * File ID
     */
    @Id
    private String id;

    /**
     * File name
     */
    private String name;

    /**
     * File type
     */
    private String type;

    /**
     * File size
     */
    private Long size;

    /**
     * Create timestamp
     */
    @Column(onInsertValue = "now()")
    private LocalDateTime createTime;

    /**
     * Update timestamp
     */
    @Column(onInsertValue = "now()", onUpdateValue = "now()")
    private LocalDateTime updateTime;

    /**
     * 文件存储方式
     */
    private String storage;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 标签
     */
    private String tags;

    /**
     * 描述
     */
    private String description;

    /**
     * 文件所属模块
     */
    private String moduleId;

    /**
     * 是否加载jar（开启后用于接口测试执行时使用）
     */
    private Boolean loadJar;

    /**
     * 文件存储路径
     */
    private String path;

    /**
     * 资源作用范围，主要兼容2.1版本前的历史数据，后续版本不再产生数据
     */
    private String resourceType;

    /**
     * 是否是最新版
     */
    private Boolean latest;

    /**
     * 同版本数据关联的ID
     */
    private String refId;

    /**
     * 附加信息 如果storage为GIT 则存放的是commitID等一系列json数据
     */
    private String attachInfo;

    private String projectId;

}
