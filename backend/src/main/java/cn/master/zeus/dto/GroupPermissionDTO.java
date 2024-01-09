package cn.master.zeus.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by 11's papa on 01/09/2024
 **/
@Data
public class GroupPermissionDTO {
    private List<GroupResourceDTO> permissions = new ArrayList<>();
}
