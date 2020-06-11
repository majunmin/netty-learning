package com.majm.rpc.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author majunmin
 * @description
 * @datetime 2020/6/10 4:39 下午
 * @since
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Hello implements Serializable {
    private String message;
    private String description;
}
