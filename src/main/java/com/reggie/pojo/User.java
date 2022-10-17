package com.reggie.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

/**
 * 用户信息
 * @TableName user
 */
@TableName(value ="user")
public class User implements Serializable {
    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 性别
     */
    private String sex;

    /**
     * 身份证号
     */
    private String idNumber;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 状态 0:禁用，1:正常
     */
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    public Long getId() {
        return id;
    }

    /**
     * 主键
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 姓名
     */
    public String getName() {
        return name;
    }

    /**
     * 姓名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 登录密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 登录密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 手机号
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 手机号
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 性别
     */
    public String getSex() {
        return sex;
    }

    /**
     * 性别
     */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * 身份证号
     */
    public String getIdNumber() {
        return idNumber;
    }

    /**
     * 身份证号
     */
    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    /**
     * 头像
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * 头像
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * 状态 0:禁用，1:正常
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 状态 0:禁用，1:正常
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        User other = (User) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getEmail() == null ? other.getEmail() == null : this.getEmail().equals(other.getEmail()))
            && (this.getPassword() == null ? other.getPassword() == null : this.getPassword().equals(other.getPassword()))
            && (this.getPhone() == null ? other.getPhone() == null : this.getPhone().equals(other.getPhone()))
            && (this.getSex() == null ? other.getSex() == null : this.getSex().equals(other.getSex()))
            && (this.getIdNumber() == null ? other.getIdNumber() == null : this.getIdNumber().equals(other.getIdNumber()))
            && (this.getAvatar() == null ? other.getAvatar() == null : this.getAvatar().equals(other.getAvatar()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getEmail() == null) ? 0 : getEmail().hashCode());
        result = prime * result + ((getPassword() == null) ? 0 : getPassword().hashCode());
        result = prime * result + ((getPhone() == null) ? 0 : getPhone().hashCode());
        result = prime * result + ((getSex() == null) ? 0 : getSex().hashCode());
        result = prime * result + ((getIdNumber() == null) ? 0 : getIdNumber().hashCode());
        result = prime * result + ((getAvatar() == null) ? 0 : getAvatar().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", email=").append(email);
        sb.append(", password=").append(password);
        sb.append(", phone=").append(phone);
        sb.append(", sex=").append(sex);
        sb.append(", idNumber=").append(idNumber);
        sb.append(", avatar=").append(avatar);
        sb.append(", status=").append(status);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}