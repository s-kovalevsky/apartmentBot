package ksamel.bot.realt.model;

import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Result {

    private String uuid;
    private Date createdAt;
    private Date updatedAt;
    private Double price;
    private Long code;
    private String address;
    private List<Double> location;
}
