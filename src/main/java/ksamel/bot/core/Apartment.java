package ksamel.bot.core;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Apartment {

    private Double price;
    private String source;
    private Integer apartmentId;
    private String link;
    private Date updateDate;
    private String address;
}
