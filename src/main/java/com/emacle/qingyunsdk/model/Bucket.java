package com.emacle.qingyunsdk.model;

import java.util.Date;

/**
 * Bucket是OSS上的命名空间。
 * <p>
 * Bucket名在整个 OSS 中具有全局唯一性，且不能修改；存储在OSS上的每个Object必须都包含在某个Bucket中。
 * </p>
 * <p>
 * Bucket 命名规范，需遵守 DNS 命名规则：
 * <ul>
 *  <li>长度在 3 ~ 63 之间。</li>
 *  <li>只允许包含 小写字母，数字，字符 ”.” 和连接字符 “-” ，且不能以特殊字符为开头或结尾。</li>
 *  <li>不能是有效 IP 地址。</li>
 * </ul>
 * </p>
 */
public class Bucket {

    // Bucket 名
    private String name;

    // Bucket 所有者
    private Owner owner;
    
    // Bucket 所在地
    private String location;

    // 创建时间
    private Date creationDate;

    /**
     * 构造函数。
     */
    public Bucket() { }
    
    /**
     * 构造函数。
     * @param name
     *      Bucket 名。
     */
    public Bucket(String name) {
        this.name = name;
    }

    /**
     * 返回字符串表示。
     */
    @Override
    public String toString() {
        return "OSSBucket [name=" + getName()
                + ", creationDate=" + getCreationDate()
                + ", owner=" + getOwner()
                + ", location="+ getLocation() + "]";
    }

    /**
     * 返回Bucket的拥有者（{@link Owner}）。
     * @return
     *      Bucket的拥有者。如果拥有者未知，则返回null。
     */
    public Owner getOwner() {
        return owner;
    }

    /**
     * 设置Bucket的拥有者。（内部使用）
     * @param owner
     *      Bucket的拥有者。
     */
    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    /**
     * 返回Bucket的创建时间。
     * @return Bucket的创建时间。如果创建时间未知，则返回null。
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * 设置Bucket的创建时间。（内部使用）
     * @param creationDate
     *          Bucket的创建时间。
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * 返回Bucket名称。
     * @return Bucket名称。
     */
    public String getName() {
        return name;
    }

    /**
     * 设置Bucket名称。（内部使用）
     * @param name
     *          Bucket名称。
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 返回Bucket所在地
     * @return Bucket所在地
     */
    public String getLocation() {
       return location;
    }

    /**
     * 设置Bucket所在地
     * @param location
     */
    public void setLocation(String location) {
       this.location = location;
    }
}
