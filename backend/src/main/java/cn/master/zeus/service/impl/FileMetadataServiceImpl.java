package cn.master.zeus.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.zeus.entity.FileMetadata;
import cn.master.zeus.mapper.FileMetadataMapper;
import cn.master.zeus.service.IFileMetadataService;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class FileMetadataServiceImpl extends ServiceImpl<FileMetadataMapper, FileMetadata> implements IFileMetadataService {

}
