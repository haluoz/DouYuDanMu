
import javax.xml.bind.DatatypeConverter;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DouYuDanMu {
    private int roomid;
    private int groupid;

    public static void main(String[] args) {


        int roomId = 606118;
        int groupId = 1;

        DouYuDanMu douYuDanMu = new DouYuDanMu(roomId, groupId);
        douYuDanMu.getDanmu();


    }


    public DouYuDanMu(int roomid, int groupid) {

        this.roomid = roomid;
        this.groupid = groupid;

    }

    public void getDanmu() {
        try {
            Socket socket = new Socket("47.52.253.55", 8601);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.write(this.loginInfo());
            dataOutputStream.flush();
            dataOutputStream.write(this.enterGroup());
            dataOutputStream.flush();


            BufferedInputStream bufferedInputStream = new BufferedInputStream(socket.getInputStream());
            Formatter formatter = new Formatter(System.out);
            String pattern = "nn@=(.*)/txt@=(.*?)/";
            Pattern r = Pattern.compile(pattern);


            String danmu;
            int count = 0;
            while (true) {


                count++;
                byte[] b = new byte[1024];
                bufferedInputStream.read(b, 0, 1024);

                danmu = new String(b, "UTF-8");

                Matcher m = r.matcher(danmu);
                if (m.find()) {
//                    formatter.format("%-20s \t %-40s \n", m.group(1), m.group(2));

                    System.out.println(m.group(2));


                }


                if (count % 100 == 0) {

                    dataOutputStream.write(this.heartBeath());
                    dataOutputStream.flush();
                }


            }


        } catch (IOException e) {
            System.out.println(e.getMessage());
        }


    }

    private byte[] loginInfo() {


        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHH");
        Date date = new Date();
        String aver = dateFormat.format(date).toString();

        String header = "9a0000009a000000b1020000";
        String body = String.format("type@=loginreq/username@=auto_xkgHJsFISW/password@=1234567890123456/roomid@=%s/" +
                "dfl@=sn@AA=105@ASss@AA=1/ct@=0/ver@=20150929/aver@=%s/", roomid, aver);
        String end = "00";

        byte[] body_byte = body.getBytes();
        byte[] end_byte = DatatypeConverter.parseHexBinary(end);

        String i = Integer.toHexString(12 + body_byte.length + end_byte.length - 4);


        header = header.replaceAll("9a", i);
        byte[] header_byte = DatatypeConverter.parseHexBinary(header);

        return getStream(header_byte, body_byte, end_byte);


    }

    private byte[] enterGroup() {

        String header = "aa000000aa000000b1020000";
        String body = String.format("type@=joingroup/rid@=%s/gid@=%s/", roomid, groupid);
        String end = "00";

        byte[] body_byte = body.getBytes();
        byte[] end_byte = DatatypeConverter.parseHexBinary(end);


        String i = Integer.toHexString(12 + body_byte.length + end_byte.length - 4);

        header = header.replaceAll("aa", i);

        byte[] header_byte = DatatypeConverter.parseHexBinary(header);


        return getStream(header_byte, body_byte, end_byte);


    }

    private byte[] heartBeath() {


        String header = "1400000014000000b1020000";
        String body = String.format("type@=mrkl/");
        String end = "00";

        byte[] header_byte = DatatypeConverter.parseHexBinary(header);
        byte[] body_byte = body.getBytes();
        byte[] end_byte = DatatypeConverter.parseHexBinary(end);


        return getStream(header_byte, body_byte, end_byte);


    }


    private byte[] getStream(byte[] header, byte[] body, byte[] end) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            byteArrayOutputStream.write(header);
            byteArrayOutputStream.write(body);
            byteArrayOutputStream.write(end);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return byteArrayOutputStream.toByteArray();


    }


}
