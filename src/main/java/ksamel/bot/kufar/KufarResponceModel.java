package ksamel.bot.kufar;

import java.util.List;

public class KufarResponceModel {
    private List<KufarApartmentModel> ads;
    private Integer total;

    public List<KufarApartmentModel> getAds() {
        return ads;
    }

    public void setAds(List<KufarApartmentModel> ads) {
        this.ads = ads;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
