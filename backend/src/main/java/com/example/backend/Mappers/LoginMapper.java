package com.example.backend.Mappers;

import com.example.backend.POJO.DO.LoginDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;


@Mapper
public interface LoginMapper {

    int insert(LoginDO login);

    // 2. 根据 ID 查询（单条查询）
    LoginDO selectById(@Param("id") String id);

    // 3. 根据用户名查询（登录验证专用，用户名唯一）
    LoginDO selectByUsername(@Param("username") String username);

    // 4. 查询所有（分页可后续加 PageHelper，这里先查全量）
    List<LoginDO> selectAll();

    // 5. 条件查询（用户名模糊 + 角色精确）
    List<LoginDO> selectByCondition(@Param("username") String username, @Param("role") String role);

    // 6. 全字段更新（非空字段才更新，避免覆盖）
    int update(LoginDO login);

    // 7. 根据 ID 删除（单条删除）
    int deleteById(@Param("id") String id);

    // 8. 批量删除（可选，按 ID 列表删除）
    int deleteBatchByIds(@Param("ids") List<String> ids);
}