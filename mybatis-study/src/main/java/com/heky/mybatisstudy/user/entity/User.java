package com.heky.mybatisstudy.user.entity;

import lombok.Data;

@Data
public class User {
   private int id;
   private String username;
   private String name;
   private String sex;
   private String password;
}
