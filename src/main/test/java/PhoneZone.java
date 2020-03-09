import lombok.Data;

import java.util.List;

/**
 * @author duanxinyuan
 * 2019/10/29 14:53
 */
@Data
public class PhoneZone {

    private String msg;

    private ContentEntity content;

    private Integer status;

    @Data
    public static class ContentEntity {

        private List<ListEntity> list;

        @Data
        public static class ListEntity {
            private String zone;
            private String enName;
            private String chName;
        }
    }
}
