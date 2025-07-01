package ksamel.bot.core;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Apartment {

    private Double price;
    private String source;
    private Integer apartmentId;
    private String link;
    private Date updateDate;
    private String address;
    private Double longitude;
    private Double latitude;

    @Override
    public String toString() {
        return "Цена: " + price +
               "," + link +
               "\nадрес: " + address +
               "\nисточник:" + source +
               ", обновлено: " + updateDate;
    }
}
