import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeXinAlarmMsg {

    String[] touser;
    String[] toparty;
    String[] totag;
    Integer toall;
    String msgtype;
    Integer agentid;
    String text;
    Integer safe;


}
