package ksamel.bot.core;

import java.util.Date;
import java.util.Objects;

public class Apartment {
    private Double price;
    private String source;
    private Integer apartmentId;
    private String link;
    private Date updateDate;
    private String address;

    public Apartment() {
    }

    public Apartment(Double price, String source, Integer apartmentId,
                     String link, Date updateDate, String address) {
        this.price = price;
        this.source = source;
        this.apartmentId = apartmentId;
        this.link = link;
        this.updateDate = updateDate;
        this.address = address;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Integer getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(Integer apartmentId) {
        this.apartmentId = apartmentId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Apartment apartment = (Apartment) o;
        return Objects.equals(price, apartment.price) &&
                Objects.equals(source, apartment.source) &&
                Objects.equals(apartmentId, apartment.apartmentId) &&
                Objects.equals(link, apartment.link) &&
                Objects.equals(updateDate, apartment.updateDate) &&
                Objects.equals(address, apartment.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(price, source, apartmentId, link, updateDate, address);
    }

    @Override
    public String toString() {
        return "price=" + price +
                ", source='" + source + '\'' +
                ", apartmentId=" + apartmentId +
                ", link='" + link + '\'' +
                ", updateDate=" + updateDate +
                ", address='" + address;
    }
}
