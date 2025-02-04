package ksamel.bot.onliner;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class OnlinerApartmentModel {

    private Integer id;
    private Price price;
    private Location location;
    private Contact contact;
    @SerializedName("last_time_up")
    private Date lastTimeUp;
    @SerializedName("created_at")
    private Date createdAt;
    private String url;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Date getLastTimeUp() {
        return lastTimeUp;
    }

    public void setLastTimeUp(Date lastTimeUp) {
        this.lastTimeUp = lastTimeUp;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static class Price {
        private Double amount;
        private String currency;

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }
    }

    public static class Location {
        private String address;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }

    public static class Contact {
        private Boolean owner;

        public Boolean getOwner() {
            return owner;
        }

        public void setOwner(Boolean owner) {
            this.owner = owner;
        }
    }
}
