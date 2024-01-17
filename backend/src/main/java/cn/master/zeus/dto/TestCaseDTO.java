package cn.master.zeus.dto;

import cn.master.zeus.entity.TestCase;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by 11's papa on 01/16/2024
 **/
@Setter
@Getter
public class TestCaseDTO extends TestCase {

    private String apiName;
    private String lastResultId;


    private String versionName;
    private List<CustomFieldDao> fields;
    private List<String> caseTags = new ArrayList<>();
    //private List<IssuesDao> issueList = new ArrayList<>();
}
