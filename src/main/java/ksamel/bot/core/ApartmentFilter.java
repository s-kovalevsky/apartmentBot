package ksamel.bot.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

public class ApartmentFilter {
    private Integer priceFrom;
    private Integer priceTo;
    private Date updatedFrom;
    ApartmentFilter(){ }

    public ApartmentFilter(Integer priceFrom, Integer priceTo, Date updatedFrom) {
        this.priceFrom = priceFrom;
        this.priceTo = priceTo;
        this.updatedFrom = updatedFrom;
    }

    public Integer getPriceFrom() {
        return priceFrom;
    }

    public void setPriceFrom(Integer priceFrom) {
        this.priceFrom = priceFrom;
    }

    public Integer getPriceTo() {
        return priceTo;
    }

    public void setPriceTo(Integer priceTo) {
        this.priceTo = priceTo;
    }

    public Date getUpdatedFrom() {
        return updatedFrom;
    }

    public void setUpdatedFrom(Date updatedFrom) {
        this.updatedFrom = updatedFrom;
    }
}
