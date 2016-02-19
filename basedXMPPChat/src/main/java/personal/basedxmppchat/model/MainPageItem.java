package personal.basedxmppchat.model;

/**
 * 主页面菜单项
 * Created by wenrule on 16/2/5.
 */
public class MainPageItem {

    private String name;
    private Integer image;

    public MainPageItem(String name, Integer image) {
        super();
        this.image = image;
        this.name = name;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
