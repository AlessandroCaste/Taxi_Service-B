package com.taxi.be.parsing;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Test implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private CityMap cityMap;

    public void setCityMap(CityMap cityMap) {
        this.cityMap = cityMap;
    }
}
