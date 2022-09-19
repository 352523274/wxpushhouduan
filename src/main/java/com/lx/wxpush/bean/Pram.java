package com.lx.wxpush.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author 常保安
 * @date 2022/9/17 17:35
 * @description  
 */
@Data
@TableName(value = "pram")
public class Pram {
    @TableField(value = "id")
    private Integer id;

    @TableField(value = "softMsg")
    private String softmsg;

    @TableField(value = "cityId")
    private Integer cityid;

    @TableField(value = "cityName")
    private String cityname;
}