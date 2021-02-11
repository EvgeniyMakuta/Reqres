package objects;

import com.google.gson.annotations.Expose;
import lombok.Data;

import java.util.ArrayList;

@Data
public class Users {
    @Expose
    ArrayList<objects.Data> data;
}
