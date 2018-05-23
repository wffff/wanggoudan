package com.goudan.wang.entity;

import javax.persistence.*;

/**
 * Created by Danny on 2018/2/13.
 */
@Entity
@Table(name = "t_mobile")
public class MobileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;
    private String number;

    public MobileEntity() {
    }

    public MobileEntity(String number) {
        this.number = number;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
